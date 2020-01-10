/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.server.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.management.ListenerNotFoundException;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import mx4j.server.MBeanMetaData;

/**
 * Interceptor that takes care of replacing the source of Notifications to the
 * ObjectName of the NotificationBroadcaster that emitted it.
 *
 * @version $Revision: 1.12 $
 */
public class NotificationListenerMBeanServerInterceptor extends DefaultMBeanServerInterceptor
{
   private Map wrappers = new HashMap();

   public String getType()
   {
      return "notificationlistener";
   }

   /**
    * The goal of this machinery is to make sure that, for a given emitter,
    * when a listener is removed the listener can be identity compared
    * (using ==) with a listener that has been previously added to the same emitter.
    * This is the reason why {@link ListenerWrapperKey#equals} has been implemented
    * using identity (==) and not equality (equals()).
    * The listener instance passed by the MBeanServer to the emitter when removing
    * the listener itself should be the same passed when the listener was added.
    * If for any reason the listener is not found, it will be wrapped with a new
    * wrapper that will allow the emitter to compare listener by equality
    * (what normally happens using Collection data structures) and behave correctly
    * most of the times (certainly MX4J's NotificationBroadcasterSupport will do).
    */
   private ListenerWrapper getListenerWrapper(MBeanMetaData metadata, NotificationListener listener)
   {
      ListenerWrapperKey key = new ListenerWrapperKey(listener, metadata.getMBean());
      ListenerWrapper wrapper = null;
      synchronized (wrappers)
      {
         wrapper = (ListenerWrapper)wrappers.get(key);
         if (wrapper == null)
         {
            wrapper = new ListenerWrapper(listener, metadata.getObjectName());
            wrappers.put(key, wrapper);
         }
      }
      return wrapper;
   }

   public void addNotificationListener(MBeanMetaData metadata, NotificationListener listener, NotificationFilter filter, Object handback)
   {
      if (isEnabled())
      {
         ListenerWrapper wrapper = getListenerWrapper(metadata, listener);
         super.addNotificationListener(metadata, wrapper, filter, handback);
      }
      else
      {
         super.addNotificationListener(metadata, listener, filter, handback);
      }
   }

   public void removeNotificationListener(MBeanMetaData metadata, NotificationListener listener) throws ListenerNotFoundException
   {
      if (isEnabled())
      {
         ListenerWrapper wrapper = getListenerWrapper(metadata, listener);
         super.removeNotificationListener(metadata, wrapper);
      }
      else
      {
         super.removeNotificationListener(metadata, listener);
      }
   }

   public void removeNotificationListener(MBeanMetaData metadata, NotificationListener listener, NotificationFilter filter, Object handback) throws ListenerNotFoundException
   {
      if (isEnabled())
      {
         ListenerWrapper wrapper = getListenerWrapper(metadata, listener);
         super.removeNotificationListener(metadata, wrapper, filter, handback);
      }
      else
      {
         super.removeNotificationListener(metadata, listener, filter, handback);
      }
   }

   private static class ListenerWrapper implements NotificationListener
   {
      private final NotificationListener listener;
      private final ObjectName objectName;

      private ListenerWrapper(NotificationListener listener, ObjectName name)
      {
         this.listener = listener;
         this.objectName = name;
      }

      public void handleNotification(Notification notification, Object handback)
      {
         // The JMX spec does not specify how to change the source to be the ObjectName
         // of the broadcaster. If we serialize the calls to the listeners, then it's
         // possible to change the source and restore it back to the old value before
         // calling the next listener; but if we want to support concurrent calls
         // to the listeners, this is not possible. Here I chose to support concurrent
         // calls so I change the value once and I never restore it.
         Object src = notification.getSource();
         if (!(src instanceof ObjectName))
         {
            // Change the source to be the ObjectName of the notification broadcaster
            // if we are not already an ObjectName (compliant with RI behaviour)
            notification.setSource(objectName);
         }

         // Notify the real listener
         NotificationListener listener = getTargetListener();
         listener.handleNotification(notification, handback);
      }

      private NotificationListener getTargetListener()
      {
         return listener;
      }

      public int hashCode()
      {
         return getTargetListener().hashCode();
      }

      public boolean equals(Object obj)
      {
         if (obj == null) return false;
         if (obj == this) return true;

         try
         {
            ListenerWrapper other = (ListenerWrapper)obj;
            return getTargetListener().equals(other.getTargetListener());
         }
         catch (ClassCastException ignored)
         {
         }
         return false;
      }

      public String toString()
      {
         return getTargetListener().toString();
      }
   }

   private static class ListenerWrapperKey
   {
      private final NotificationListener listener;
      private final Object mbean;

      private ListenerWrapperKey(NotificationListener listener, Object mbean)
      {
         this.listener = listener;
         this.mbean = mbean;
      }

      public boolean equals(Object obj)
      {
         if (this == obj) return true;
         if (!(obj instanceof ListenerWrapperKey)) return false;

         ListenerWrapperKey other = (ListenerWrapperKey)obj;

         if (listener != other.listener) return false;
         if (mbean != other.mbean) return false;

         return true;
      }

      public int hashCode()
      {
         int result = listener.hashCode();
         result = 29 * result + mbean.hashCode();
         return result;
      }
   }
}
