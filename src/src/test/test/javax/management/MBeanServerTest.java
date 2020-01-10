/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management;

import java.io.File;
import java.net.URL;
import java.util.Arrays;
import java.util.Set;

import javax.management.JMRuntimeException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerFactory;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.RuntimeOperationsException;

import test.MX4JTestCase;
import test.MutableInteger;
import test.javax.management.support.ClassLoaderSupport;
import test.javax.management.support.ComplianceSupport;
import test.javax.management.support.MBeanThrowingExceptions;
import test.javax.management.support.NotificationSupport;
import test.javax.management.support.PostRegistrationSupport;

/**
 * @version $Revision: 1.12 $
 */
public class MBeanServerTest extends MX4JTestCase
{
   public MBeanServerTest(String s)
   {
      super(s);
   }

   public void testDefaultDomainConversion() throws Exception
   {
      String domain = "test";
      MBeanServer server = MBeanServerFactory.newMBeanServer(domain);

      // Every operation with default domain must match the one with the
      // explicit domain

      ObjectName defaultName = new ObjectName(":key=value");
      ObjectName explicitName = new ObjectName(server.getDefaultDomain(), "key", "value");

      // A broadcaster mbean
      MBeanServerDelegate broadcaster = new MBeanServerDelegate();

      // Register with the explicit object name
      server.registerMBean(broadcaster, explicitName);

      // Query
      Set set = server.queryNames(defaultName, null);
      if (set.size() != 1)
      {
         fail("Default domain not handled in ObjectNames");
      }

      // Register and remove a listener
      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
         }
      };
      server.addNotificationListener(defaultName, listener, null, null);
      // Remove throws if the listener is not found
      server.removeNotificationListener(defaultName, listener);

      // Invoke operations
      server.getAttribute(defaultName, "MBeanServerId");

      // Metadata
      server.getMBeanInfo(defaultName);
      server.getObjectInstance(defaultName);
      if (!server.isRegistered(defaultName))
      {
         fail("Default domain not handled in ObjectNames");
      }
      server.isInstanceOf(defaultName, "javax.management.MBeanServerDelegateMBean");
   }

   public void testRegistrationOfJMImplementationDomain() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer();

      // Test that MBeans with reserved object names cannot be registered
      Object mbean = new ComplianceSupport.BasicStandard();
      ObjectName reserved = new ObjectName("JMImplementation:simon=true");
      try
      {
         server.registerMBean(mbean, reserved);
         fail("MBeans with reserved object names cannot be registered");
      }
      catch (JMRuntimeException ignored)
      {
      }
   }

   public void testDeregistrationOfJMImplementationDomain() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer();

      // Test that the delegate MBean cannot be unregistered
      ObjectName delegate = new ObjectName("JMImplementation:type=MBeanServerDelegate");
      try
      {
         server.unregisterMBean(delegate);
         fail("Delegate MBean cannot be unregistered");
      }
      catch (RuntimeOperationsException ignored)
      {
      }
   }

   public void testDelegateID() throws Exception
   {
      ObjectName delegate = new ObjectName("JMImplementation:type=MBeanServerDelegate");
      MBeanServer server1 = MBeanServerFactory.newMBeanServer();
      MBeanServer server2 = MBeanServerFactory.newMBeanServer();
      String id1 = (String)server1.getAttribute(delegate, "MBeanServerId");
      String id2 = (String)server2.getAttribute(delegate, "MBeanServerId");

      // Be sure they're different
      if (id1.equals(id2)) fail("MBeanServer ID must differ");
   }

   public void testAddRemoveListenerOnMultipleMBeans() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer();

      ObjectName name1 = new ObjectName("domain:key=mbean1");
      ObjectName name2 = new ObjectName("domain:key=mbean2");

      NotificationBroadcasterSupport mbean1 = new NotificationSupport.Emitter();
      NotificationBroadcasterSupport mbean2 = new NotificationSupport.Emitter();

      server.registerMBean(mbean1, name1);
      server.registerMBean(mbean2, name2);

      final MutableInteger integer = new MutableInteger(0);
      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            integer.set(integer.get() + 1);
         }
      };

      server.addNotificationListener(name1, listener, null, null);
      server.addNotificationListener(name2, listener, null, null);

      Notification notification = new Notification("test", mbean1, 1);
      mbean1.sendNotification(notification);

      // Be sure the listener is called
      assertEquals("Listener is not called", integer.get(), 1);

      mbean2.sendNotification(notification);

      // Be sure the listener is called
      assertEquals("Listener is not called", integer.get(), 2);

      // Remove one listener
      server.removeNotificationListener(name1, listener);

      // Be sure it is not called
      mbean1.sendNotification(notification);
      assertEquals("Listener is called", integer.get(), 2);

      // Be sure it is called
      mbean2.sendNotification(notification);
      assertEquals("Listener is not called", integer.get(), 3);

      try
      {
         server.removeNotificationListener(name1, listener);
         fail("Listener has been removed");
      }
      catch (ListenerNotFoundException ignored)
      {
      }

      // Remove also the second listener
      server.removeNotificationListener(name2, listener);

      // Be sure it is not called
      mbean2.sendNotification(notification);
      assertEquals("Listener is called", integer.get(), 3);
   }

   public void testAddRemoveMixedListenerOnMultipleMBeans() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer();

      ObjectName name1 = new ObjectName("domain:key=mbean1");
      ObjectName name2 = new ObjectName("domain:key=mbean2");

      NotificationBroadcasterSupport mbean1 = new NotificationSupport.Emitter();
      NotificationBroadcasterSupport mbean2 = new NotificationSupport.Emitter();

      server.registerMBean(mbean1, name1);
      server.registerMBean(mbean2, name2);

      final MutableInteger integer = new MutableInteger(0);
      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            integer.set(integer.get() + 1);
         }
      };

      server.addNotificationListener(name1, listener, null, null);
      server.addNotificationListener(name2, listener, null, null);
      mbean2.addNotificationListener(listener, null, null);

      Notification notification = new Notification("test", mbean1, 1);
      mbean1.sendNotification(notification);

      // Be sure the listener is called
      assertEquals("Listener is not called", integer.get(), 1);

      mbean2.sendNotification(notification);

      // Be sure the listeners are called
      assertEquals("Listeners are not called", integer.get(), 3);

      // Remove one listener
      server.removeNotificationListener(name2, listener);

      // Be sure the listener is called
      mbean2.sendNotification(notification);
      assertEquals("Listener is not called", integer.get(), 4);

      // Be sure it is called
      mbean1.sendNotification(notification);
      assertEquals("Listener is not called", integer.get(), 5);

      server.removeNotificationListener(name1, listener);

      // Be sure it is not called
      mbean1.sendNotification(notification);
      assertEquals("Listener is called", integer.get(), 5);

      // Be sure it is called
      mbean2.sendNotification(notification);
      assertEquals("Listener is not called", integer.get(), 6);

      try
      {
         server.removeNotificationListener(name2, listener);
         fail("Listener has been removed");
      }
      catch (ListenerNotFoundException ignored)
      {
      }

      // Remove also the second listener
      mbean2.removeNotificationListener(listener);

      // Be sure it is not called
      mbean2.sendNotification(notification);
      assertEquals("Listener is called", integer.get(), 6);
   }

   public void testObjectInstanceOnPostRegister() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      PostRegistrationSupport mbean = new PostRegistrationSupport();
      ObjectName name = new ObjectName(":mbean=postRegistration");
      server.registerMBean(mbean, name);
   }

   public void testGetDomains() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer();

      String[] domains = server.getDomains();
      if (domains.length != 1) fail("Fresh new MBeanServer contains MBeans not in the JMImplementation domain");
      if (!"JMImplementation".equals(domains[0])) fail("Fresh new MBeanServer contains MBeans not in the JMImplementation domain");

      Object mbean = new ComplianceSupport.BasicStandard();

      ObjectName name1 = new ObjectName("domain1", "mbean", "1");
      server.registerMBean(mbean, name1);
      domains = server.getDomains();
      Arrays.sort(domains, null);

      if (domains.length != 2) fail("New MBean domain is not present in getDomains()");
      if (!"domain1".equals(domains[1])) fail("New MBean domain is not present in getDomains()");

      ObjectName name2 = new ObjectName("domain1", "mbean", "2");
      server.registerMBean(mbean, name2);
      domains = server.getDomains();
      Arrays.sort(domains, null);

      if (domains.length != 2) fail("Existing MBean domain should not be duplicated in getDomains()");
      if (!"domain1".equals(domains[1])) fail("Existing MBean domain should not be duplicated in getDomains()");

      server.unregisterMBean(name2);
      domains = server.getDomains();
      Arrays.sort(domains, null);

      if (domains.length != 2) fail("Unregistering still existing MBean domain should not be removed from getDomains()");
      if (!"domain1".equals(domains[1])) fail("Unregistering still existing MBean domain should not be removed from getDomains()");

      server.unregisterMBean(name1);
      domains = server.getDomains();
      Arrays.sort(domains, null);

      if (domains.length != 1) fail("Unregistering MBean domain should be removed from getDomains()");
      if (!"JMImplementation".equals(domains[0])) fail("Unregistering MBean domain should be removed from getDomains()");
   }

   public void testInstantiate() throws Exception
   {
      MBeanServer server = newMBeanServer();

      String className = ComplianceSupport.BasicStandard.class.getName();
      Object mbean1 = server.instantiate(className, null, new Object[0], new String[0]);

      // Register one classloader mbean
      File file = new File("dist/test/mx4j-tests.jar");
      ClassLoader parent = getClass().getClassLoader().getParent();
      ClassLoaderSupport loader = new ClassLoaderSupport(new URL[]{file.toURL()}, parent);
      ObjectName loaderName = new ObjectName(":type=ClassLoader");
      server.registerMBean(loader, loaderName);

      Object mbean2 = server.instantiate(className, loaderName, new Object[0], new String[0]);

      // Now mbean1 should be of a different class from mbean2
      if (mbean1.getClass().equals(mbean2.getClass())) fail("MBean classes should be different");

      Object mbean3 = server.instantiate(className, new Object[0], new String[0]);

      // Since JMX 1.2, the CLR has the cl of the MBeanServer in its classpath.
      if (!mbean1.getClass().equals(mbean3.getClass())) fail("MBean classes should be equal");

      server.unregisterMBean(loaderName);

      Object mbean4 = server.instantiate(className, new Object[0], new String[0]);
      if (!mbean1.getClass().equals(mbean4.getClass())) fail("MBean classes should be equal");
   }

   public void testWrapExceptionsThrownByMBeanMethods() throws Exception
   {
      MBeanServer server = newMBeanServer();

      MBeanThrowingExceptions mbean = new MBeanThrowingExceptions();
      ObjectName objectName = ObjectName.getInstance(":name=exceptions");
      server.registerMBean(mbean, objectName);

      try
      {
         server.invoke(objectName, "throwReflectionException", null, null);
         fail();
      }
      catch (MBeanException x)
      {
      }
   }
}
