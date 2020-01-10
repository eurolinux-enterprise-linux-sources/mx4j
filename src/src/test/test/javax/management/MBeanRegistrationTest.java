/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management;

import javax.management.*;
import javax.management.loading.MLet;

import test.MX4JTestCase;
import test.MutableBoolean;
import test.MutableObject;
import test.javax.management.support.RegistrationSupport;

/**
 * @version $Revision: 1.13 $
 */
public class MBeanRegistrationTest extends MX4JTestCase
{
   public static interface BarMBean
   {
      int getBeer();

      void getBEER();

      int getBeer(String name);

      String[] get();
   }

   public static class Bar implements BarMBean
   {
      public Bar()
      {
      }

      public String[] get()
      {
         return new String[0];
      }

      public int getBeer()
      {
         return 0;
      }

      public void getBEER()
      {
         throw new java.lang.Error("No BEER here");
      }

      public int getBeer(String name)
      {
         return 0;
      }

   }

   public MBeanRegistrationTest(String s)
   {
      super(s);
   }

   public void testNullObjectName() throws Exception
   {
      MBeanServer server = newMBeanServer();
      int count = server.getMBeanCount().intValue();
      Object nullObjectName = new RegistrationSupport.NullObjectName();
      try
      {
         server.registerMBean(nullObjectName, null);
         fail("MBean cannot be registered");
      }
      catch (RuntimeOperationsException ignored)
      {
      }
      // Check that was not registered
      if (server.getMBeanCount().intValue() != count)
      {
         fail("MBean with null ObjectName was registered");
      }
   }

   public void testPreRegisterException() throws Exception
   {
      MBeanServer server = newMBeanServer();
      int count = server.getMBeanCount().intValue();
      Object preRegisterException = new RegistrationSupport.PreRegisterException();
      try
      {
         server.registerMBean(preRegisterException, null);
         fail("MBean cannot be registered");
      }
      catch (MBeanRegistrationException ignored)
      {
      }
      // Check that was not registered
      if (server.getMBeanCount().intValue() != count)
      {
         fail("MBean threw exception in preRegister, but was registered");
      }
   }

   public void testPostRegisterException() throws Exception
   {
      MBeanServer server = newMBeanServer();
      int count = server.getMBeanCount().intValue();
      Object postRegisterException = new RegistrationSupport.PostRegisterException();
      ObjectName name = new ObjectName(":test=postRegister");
      try
      {
         server.registerMBean(postRegisterException, name);
         fail("MBean must throw an exception");
      }
      catch (RuntimeMBeanException ignored)
      {
      }
      // Check that was registered
      if (server.getMBeanCount().intValue() != count + 1)
      {
         fail("MBean threw exception in postRegister, but was NOT registered");
      }
   }

   public void testPreDeregisterException() throws Exception
   {
      MBeanServer server = newMBeanServer();
      int count = server.getMBeanCount().intValue();
      Object preDeregisterException = new RegistrationSupport.PreDeregisterException();
      ObjectName name = new ObjectName("simon:mbean=test");
      server.registerMBean(preDeregisterException, name);
      if (server.getMBeanCount().intValue() != count + 1)
      {
         fail("MBean was not registered");
      }
      try
      {
         server.unregisterMBean(name);
         fail("MBean cannot be unregistered");
      }
      catch (MBeanRegistrationException ignored)
      {
      }
      if (server.getMBeanCount().intValue() != count + 1)
      {
         fail("MBean was unregistered");
      }
   }

   public void testPostDeregisterException() throws Exception
   {
      MBeanServer server = newMBeanServer();
      int count = server.getMBeanCount().intValue();
      Object postDeregisterException = new RegistrationSupport.PostDeregisterException();
      ObjectName name = new ObjectName("simon:mbean=test");
      server.registerMBean(postDeregisterException, name);
      if (server.getMBeanCount().intValue() != count + 1)
      {
         fail("MBean was not registered");
      }
      try
      {
         server.unregisterMBean(name);
         fail("MBean must throw an exception");
      }
      catch (RuntimeMBeanException ignored)
      {
      }
      if (server.getMBeanCount().intValue() != count)
      {
         fail("MBean was NOT unregistered");
      }
   }

   public void testRegistration() throws Exception
   {
      MBeanServer server = newMBeanServer();
      int count = server.getMBeanCount().intValue();
      final MutableBoolean bool1 = new MutableBoolean(false);
      final MutableBoolean bool2 = new MutableBoolean(false);
      Object empty = new RegistrationSupport.Empty(bool1, bool2);
      final ObjectName name = new ObjectName("simon:mbean=empty");
      server.registerMBean(empty, name);
      // Check registration
      if (!bool1.get())
      {
         fail("postRegister called with wrong argument value for successful registration");
      }
      if (server.getMBeanCount().intValue() != count + 1)
      {
         fail("MBean was not registered");
      }
   }

   public void testDuplicateRegistration() throws Exception
   {
      MBeanServer server = newMBeanServer();
      int count = server.getMBeanCount().intValue();
      final MutableBoolean bool1 = new MutableBoolean(false);
      final MutableBoolean bool2 = new MutableBoolean(false);
      Object empty = new RegistrationSupport.Empty(bool1, bool2);
      final ObjectName name = new ObjectName("simon:mbean=empty");
      server.registerMBean(empty, name);
      if (server.getMBeanCount().intValue() != count + 1)
      {
         fail("MBean was not registered");
      }

      Object duplicate = new RegistrationSupport.EmptyDuplicate(name, bool1);
      try
      {
         server.registerMBean(duplicate, null);
         fail("MBean with same name cannot be registered");
      }
      catch (InstanceAlreadyExistsException ignored)
      {
      }
      // Check that postRegister was called correctly
      if (bool1.get())
      {
         fail("postRegister called with wrong argument value for unsuccessful registration");
      }
      if (server.getMBeanCount().intValue() != count + 1)
      {
         fail("MBean was registered, and it shouldn't");
      }
   }

   public void testDeregistration() throws Exception
   {
      MBeanServer server = newMBeanServer();
      int count = server.getMBeanCount().intValue();
      final MutableBoolean bool1 = new MutableBoolean(false);
      final MutableBoolean bool2 = new MutableBoolean(false);
      Object empty = new RegistrationSupport.Empty(bool1, bool2);
      final ObjectName name = new ObjectName("simon:mbean=empty");
      server.registerMBean(empty, name);
      if (server.getMBeanCount().intValue() != count + 1)
      {
         fail("MBean was not registered");
      }

      bool1.set(true);
      bool2.set(true);
      server.unregisterMBean(name);
      if (server.getMBeanCount().intValue() != count)
      {
         fail("MBean was not unregistered");
      }
      if (bool1.get() || bool2.get())
      {
         fail("preDeregister or postDeregister are not called");
      }
   }

   public void testDuplicateDeregistration() throws Exception
   {
      MBeanServer server = newMBeanServer();
      int count = server.getMBeanCount().intValue();
      final MutableBoolean bool1 = new MutableBoolean(false);
      final MutableBoolean bool2 = new MutableBoolean(false);
      Object empty = new RegistrationSupport.Empty(bool1, bool2);
      final ObjectName name = new ObjectName("simon:mbean=empty");
      server.registerMBean(empty, name);
      if (server.getMBeanCount().intValue() != count + 1)
      {
         fail("MBean was not registered");
      }

      bool1.set(true);
      bool2.set(true);
      server.unregisterMBean(name);
      if (server.getMBeanCount().intValue() != count)
      {
         fail("MBean was not unregistered");
      }
      if (bool1.get() || bool2.get())
      {
         fail("preDeregister or postDeregister are not called");
      }

      // Try again
      try
      {
         server.unregisterMBean(name);
         fail("Already unregistered MBean can be unregistered");
      }
      catch (InstanceNotFoundException ignored)
      {
      }
   }

   public void testNotificationDuringRegistrationForStdMBean() throws Exception
   {
      final MBeanServer server = newMBeanServer();
      Object mbean = new RegistrationSupport.Std();
      final ObjectName name = new ObjectName(":mbean=std");
      server.addNotificationListener(new ObjectName("JMImplementation:type=MBeanServerDelegate"), new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            invokeOperationsDuringRegistration(server, name, notification);
         }
      }, null, null);

      server.registerMBean(mbean, name);
   }

   public void testNotificationDuringRegistrationForDynMBean() throws Exception
   {
      final MBeanServer server = newMBeanServer();
      Object mbean = new RegistrationSupport.Dyn();
      final ObjectName name = new ObjectName(":mbean=dyn");
      server.addNotificationListener(new ObjectName("JMImplementation:type=MBeanServerDelegate"), new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            invokeOperationsDuringRegistration(server, name, notification);
         }
      }, null, null);

      server.registerMBean(mbean, name);
   }

   private void invokeOperationsDuringRegistration(MBeanServer server, ObjectName name, Notification notification)
   {
      if (notification != null)
      {
         MBeanServerNotification notif = (MBeanServerNotification)notification;
         ObjectName registered = notif.getMBeanName();
         if (!registered.equals(name)) fail("Notification for the wrong MBean: " + registered + ", should be " + name);
         if (!MBeanServerNotification.REGISTRATION_NOTIFICATION.equals(notif.getType())) fail("Expecting a registration notification");
      }

      try
      {
         MBeanInfo info = server.getMBeanInfo(name);
         if (info.getClassName() == null) fail("MBeanInfo not initialized correctly");
         if (info.getOperations().length == 0) fail("MBeanInfo not initialized correctly");

         ObjectInstance instance = server.getObjectInstance(name);
         if (instance == null) fail("ObjectInstance should be already initialized");

         boolean isRegistered = server.isRegistered(name);
         if (!isRegistered) fail("MBean is registered");

         // Must be able to invoke it with no exceptions
         server.invoke(name, RegistrationSupport.StdMBean.class.getMethods()[0].getName(), null, null);
      }
      catch (Exception x)
      {
         fail("MBean metadata structures are not yet ready, but they should be: " + x);
      }
   }

   public void testInvokeMBeanServerOperationsInCallbacks() throws Exception
   {
      MBeanServer server = newMBeanServer();
      Object mbean = new InvokeDuringCallbacks();
      ObjectName name = ObjectName.getInstance(":name=invoke");
      server.registerMBean(mbean, name);
      server.unregisterMBean(name);
   }


   public interface InvokeDuringCallbacksMBean
   {
      public void method();
   }

   public class InvokeDuringCallbacks implements InvokeDuringCallbacksMBean, MBeanRegistration
   {
      private MBeanServer server;
      private ObjectName name;

      public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception
      {
         this.server = server;
         this.name = name;
         return name;
      }

      public void postRegister(Boolean registrationDone)
      {
         invokeOperationsDuringRegistration(server, name, null);
      }

      public void preDeregister() throws Exception
      {
         invokeOperationsDuringRegistration(server, name, null);
      }

      public void postDeregister()
      {
      }

      public void method()
      {
      }
   }

   public void testDistinguishAttributesOperations() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName objname = new ObjectName("tests:id=distinguishAttributesOperations");
      Bar b = new Bar();
      server.registerMBean(b, objname);
      MBeanInfo info = server.getMBeanInfo(objname);
      assertTrue("Expecting one attribute", info.getAttributes().length == 1);
      try
      {
         assertTrue("No 'Beer' attribute", ((Integer)server.getAttribute(objname, "Beer")).intValue() == 0);
         String[] getresult = (String[])server.invoke(objname, "get", new Object[0], new String[0]);
         assertTrue("Expecting zero length result", getresult.length == 0);
         server.getAttribute(objname, "BEER");
         fail("Expecting AttributeNotFoundException");
      }
      catch (AttributeNotFoundException x)
      {
         assertTrue(true);
      }
      assertTrue("Expecting three operations", info.getOperations().length == 3);
   }

   public void testListenerRegistrationUnregistrationDuringCallbacks() throws Exception
   {
      MBeanServer server = newMBeanServer();
      MutableObject holder = new MutableObject(null);
      Object mbean = new RegistrationSupport.ListenerRegistrar(holder);
      ObjectName name = ObjectName.getInstance("test:type=notifications");
      server.registerMBean(mbean, name);

      // Register a new MBean, the holder must be notified
      ObjectName mlet = ObjectName.getInstance("test:type=mlet");
      server.createMBean(MLet.class.getName(), mlet, null);

      Notification notification = (Notification)holder.get();
      assertNotNull(notification);
      assertEquals(notification.getType(), MBeanServerNotification.REGISTRATION_NOTIFICATION);
      holder.set(null);

      server.unregisterMBean(mlet);

      notification = (Notification)holder.get();
      assertNotNull(notification);
      assertEquals(notification.getType(), MBeanServerNotification.UNREGISTRATION_NOTIFICATION);
      holder.set(null);

      // Unregisters also the listeners (in postDeregister)
      server.unregisterMBean(name);
      notification = (Notification)holder.get();
      assertNotNull(notification);
      assertEquals(notification.getType(), MBeanServerNotification.UNREGISTRATION_NOTIFICATION);
      holder.set(null);

      server.createMBean(MLet.class.getName(), mlet, null);
      notification = (Notification)holder.get();
      assertNull(notification);

      server.unregisterMBean(mlet);
      notification = (Notification)holder.get();
      assertNull(notification);
   }

   public void testAbstractClass() throws Exception
   {
      MBeanServer server = newMBeanServer();
      try {
         server.createMBean(Foo.class.getName(), null);
         fail();
      }
      catch (NotCompliantMBeanException e)
      {
         // ok
      }
      catch (Exception e)
      {
         e.printStackTrace();
         fail();
      }
   }

   public static interface FooMBean
   {
      void something();
   }

   public static abstract class Foo implements FooMBean
   {
      public void something() {
      }
   }
}
