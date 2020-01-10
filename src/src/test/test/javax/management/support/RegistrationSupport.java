/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.support;

import javax.management.InstanceNotFoundException;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import mx4j.AbstractDynamicMBean;
import test.MutableBoolean;
import test.MutableObject;

/**
 * @version $Revision: 1.7 $
 */
public class RegistrationSupport
{
   public interface NullObjectNameMBean
   {
   }

   public static class NullObjectName implements NullObjectNameMBean, MBeanRegistration
   {
      public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception
      {
         return null;
      }

      public void postRegister(Boolean registrationDone)
      {
      }

      public void preDeregister() throws Exception
      {
      }

      public void postDeregister()
      {
      }
   }

   public interface PreRegisterExceptionMBean
   {
   }

   public static class PreRegisterException implements PreRegisterExceptionMBean, MBeanRegistration
   {
      public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception
      {
         throw new Exception();
      }

      public void postRegister(Boolean registrationDone)
      {
      }

      public void preDeregister() throws Exception
      {
      }

      public void postDeregister()
      {
      }
   }

   public interface PostRegisterExceptionMBean
   {
   }

   public static class PostRegisterException implements PostRegisterExceptionMBean, MBeanRegistration
   {
      public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception
      {
         return name;
      }

      public void postRegister(Boolean registrationDone)
      {
         throw new RuntimeException();
      }

      public void preDeregister() throws Exception
      {
      }

      public void postDeregister()
      {
      }
   }

   public interface PreDeregisterExceptionMBean
   {
   }

   public static class PreDeregisterException implements PreDeregisterExceptionMBean, MBeanRegistration
   {
      public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception
      {
         return name;
      }

      public void postRegister(Boolean registrationDone)
      {
      }

      public void preDeregister() throws Exception
      {
         throw new Exception();
      }

      public void postDeregister()
      {
      }
   }

   public interface PostDeregisterExceptionMBean
   {
   }

   public static class PostDeregisterException implements PostDeregisterExceptionMBean, MBeanRegistration
   {
      public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception
      {
         return name;
      }

      public void postRegister(Boolean registrationDone)
      {
      }

      public void preDeregister() throws Exception
      {
      }

      public void postDeregister()
      {
         throw new RuntimeException();
      }
   }

   public interface EmptyMBean
   {
   }

   public static class Empty implements EmptyMBean, MBeanRegistration
   {
      private MutableBoolean m_bool1;
      private MutableBoolean m_bool2;

      public Empty(MutableBoolean bool1, MutableBoolean bool2)
      {
         m_bool1 = bool1;
         m_bool2 = bool2;
      }

      public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception
      {
         return name;
      }

      public void postRegister(Boolean registrationDone)
      {
         m_bool1.set(registrationDone.booleanValue());
      }

      public void preDeregister() throws Exception
      {
         m_bool1.set(false);
      }

      public void postDeregister()
      {
         m_bool2.set(false);
      }
   }

   public interface EmptyDuplicateMBean
   {
   }

   public static class EmptyDuplicate implements EmptyDuplicateMBean, MBeanRegistration
   {
      private ObjectName m_name;
      private MutableBoolean m_bool1;

      public EmptyDuplicate(ObjectName name, MutableBoolean bool1)
      {
         m_name = name;
         m_bool1 = bool1;
      }

      public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception
      {
         return m_name;
      }

      public void postRegister(Boolean registrationDone)
      {
         m_bool1.set(registrationDone.booleanValue());
      }

      public void preDeregister() throws Exception
      {
      }

      public void postDeregister()
      {
      }
   }

   public interface StdMBean
   {
      public void method();
   }

   public static class Std implements StdMBean, MBeanRegistration
   {
      public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception
      {
         return name;
      }

      public void postRegister(Boolean registrationDone)
      {
      }

      public void preDeregister() throws Exception
      {
      }

      public void postDeregister()
      {
      }

      public void method()
      {
      }
   }

   public static class Dyn extends AbstractDynamicMBean implements MBeanRegistration
   {
      public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception
      {
         return name;
      }

      public void postRegister(Boolean registrationDone)
      {
      }

      public void preDeregister() throws Exception
      {
      }

      public void postDeregister()
      {
      }

      public void method()
      {
      }

      protected MBeanOperationInfo[] createMBeanOperationInfo()
      {
         return new MBeanOperationInfo[]{new MBeanOperationInfo(StdMBean.class.getMethods()[0].getName(), null, new MBeanParameterInfo[0], "void", MBeanOperationInfo.UNKNOWN)};
      }
   }

   public interface ListenerRegistrarMBean
   {
   }

   public static class ListenerRegistrar implements ListenerRegistrarMBean, MBeanRegistration, NotificationListener
   {
      private final MutableObject holder;
      private MBeanServer server;
      private ObjectName delegate;

      public ListenerRegistrar(MutableObject holder)
      {
         this.holder = holder;
      }

      public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception
      {
         this.server = server;
         delegate = ObjectName.getInstance("JMImplementation:type=MBeanServerDelegate");
         return name;
      }

      public void postRegister(Boolean registrationDone)
      {
         try
         {
            server.addNotificationListener(delegate, this, null, null);
         }
         catch (InstanceNotFoundException x)
         {
            throw new Error(x.toString());
         }
      }

      public void preDeregister() throws Exception
      {
      }

      public void postDeregister()
      {
         try
         {
            server.removeNotificationListener(delegate, this);
         }
         catch (Exception x)
         {
            throw new Error(x.toString());
         }
      }

      public void handleNotification(Notification notification, Object handback)
      {
         holder.set(notification);
      }
   }
}
