/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management;

import java.util.ArrayList;
import java.util.List;

import javax.management.ListenerNotFoundException;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import test.MX4JTestCase;
import test.MutableInteger;
import test.MutableObject;

/**
 * @version $Revision: 1.1 $
 */
public class NotificationListenerTest extends MX4JTestCase
{
   public NotificationListenerTest(String name)
   {
      super(name);
   }

   public void testAddRemoveOneListenerOnOneMBean() throws Exception
   {
      MBeanServer server = newMBeanServer();
      IdentityEmitter emitter = new IdentityEmitter();
      ObjectName objectName = ObjectName.getInstance("test:type=emitter");
      server.registerMBean(emitter, objectName);

      final MutableObject source = new MutableObject(null);
      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            source.set(notification.getSource());
         }
      };

      server.addNotificationListener(objectName, listener, null, null);
      assertEquals(emitter.getNotificationListeners().size(), 1);

      Notification notification = new Notification("type", emitter, 0);
      emitter.sendNotification(notification);
      assertEquals(objectName, source.get());

      server.removeNotificationListener(objectName, listener, null, null);
      assertEquals(emitter.getNotificationListeners().size(), 0);
   }

   public void testAddRemoveOneListenerTwiceOnOneMBean() throws Exception
   {
      MBeanServer server = newMBeanServer();
      IdentityEmitter emitter = new IdentityEmitter();
      ObjectName objectName = ObjectName.getInstance("test:type=emitter");
      server.registerMBean(emitter, objectName);

      final MutableInteger count = new MutableInteger(0);
      final MutableObject source = new MutableObject(null);
      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            source.set(notification.getSource());
            count.set(count.get() + 1);
         }
      };

      // Add same listener twice, with different handbacks
      Object handback = new Object();
      server.addNotificationListener(objectName, listener, null, null);
      server.addNotificationListener(objectName, listener, null, handback);
      assertEquals(emitter.getNotificationListeners().size(), 2);

      Notification notification = new Notification("type", emitter, 0);
      emitter.sendNotification(notification);
      assertEquals(objectName, source.get());
      assertEquals(count.get(), 2);

      server.removeNotificationListener(objectName, listener, null, null);
      assertEquals(emitter.getNotificationListeners().size(), 1);

      server.removeNotificationListener(objectName, listener, null, handback);
      assertEquals(emitter.getNotificationListeners().size(), 0);
   }

   public void testAddRemoveTwoListenersOnOneMBean() throws Exception
   {
      MBeanServer server = newMBeanServer();
      IdentityEmitter emitter = new IdentityEmitter();
      ObjectName objectName = ObjectName.getInstance("test:type=emitter");
      server.registerMBean(emitter, objectName);

      NotificationListener listener1 = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
         }
      };

      NotificationListener listener2 = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
         }
      };

      server.addNotificationListener(objectName, listener1, null, null);
      server.addNotificationListener(objectName, listener2, null, null);
      assertEquals(emitter.getNotificationListeners().size(), 2);

      server.removeNotificationListener(objectName, listener1, null, null);
      assertEquals(emitter.getNotificationListeners().size(), 1);

      server.removeNotificationListener(objectName, listener2, null, null);
      assertEquals(emitter.getNotificationListeners().size(), 0);
   }

   public void testAddRemoveOneListenerOnTwoMBeans() throws Exception
   {
      MBeanServer server = newMBeanServer();
      IdentityEmitter emitter1 = new IdentityEmitter();
      ObjectName objectName1 = ObjectName.getInstance("test:type=emitter1");
      server.registerMBean(emitter1, objectName1);
      IdentityEmitter emitter2 = new IdentityEmitter();
      ObjectName objectName2 = ObjectName.getInstance("test:type=emitter2");
      server.registerMBean(emitter2, objectName2);

      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
         }
      };

      server.addNotificationListener(objectName1, listener, null, null);
      assertEquals(emitter1.getNotificationListeners().size(), 1);
      assertEquals(emitter2.getNotificationListeners().size(), 0);

      server.addNotificationListener(objectName2, listener, null, null);
      assertEquals(emitter1.getNotificationListeners().size(), 1);
      assertEquals(emitter2.getNotificationListeners().size(), 1);

      server.removeNotificationListener(objectName1, listener, null, null);
      assertEquals(emitter1.getNotificationListeners().size(), 0);
      assertEquals(emitter2.getNotificationListeners().size(), 1);

      server.removeNotificationListener(objectName2, listener, null, null);
      assertEquals(emitter1.getNotificationListeners().size(), 0);
      assertEquals(emitter2.getNotificationListeners().size(), 0);
   }

   public interface IdentityEmitterMBean
   {
   }

   public static class IdentityEmitter extends NotificationBroadcasterSupport implements IdentityEmitterMBean
   {
      private List listeners = new ArrayList();

      public List getNotificationListeners()
      {
         return listeners;
      }

      public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
      {
         super.addNotificationListener(listener, filter, handback);
         listeners.add(listener);
      }

      public void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws ListenerNotFoundException
      {
         NotificationListener[] listens = (NotificationListener[])listeners.toArray(new NotificationListener[0]);
         for (int i = 0; i < listens.length; i++)
         {
            NotificationListener listen = listens[i];
            if (listen == listener)
            {
               super.removeNotificationListener(listener, filter, handback);
               listeners.remove(listener);
               return;
            }
         }
         throw new ListenerNotFoundException();
      }
   }
}
