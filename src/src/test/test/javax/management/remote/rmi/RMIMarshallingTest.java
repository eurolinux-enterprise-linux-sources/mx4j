/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote.rmi;

import java.io.File;
import java.io.Serializable;
import java.net.URL;
import java.util.Set;
import javax.management.Attribute;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.loading.PrivateMLet;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import test.MX4JTestCase;
import test.javax.management.remote.support.Marshalling;
import test.javax.management.remote.support.Unknown;

/**
 * @version $Revision: 1.5 $
 */
public class RMIMarshallingTest extends MX4JTestCase
{
   private MBeanServer server = null;
   private MBeanServerConnection conn = null;
   private JMXConnectorServer cntorServer = null;
   private JMXConnector cntor = null;
   private ObjectName mbeanName;
   private ObjectName mbeanLoaderName;


   public RMIMarshallingTest(String s)
   {
      super(s);
   }

   public void setUp() throws Exception
   {
      super.setUp();
      // Create a classloader that sees only the MBean and its parameter classes (Unknown)
      File mbeanJar = new File("dist/test/mx4j-tests.jar");
      PrivateMLet mbeanLoader = new PrivateMLet(new URL[]{mbeanJar.toURL()}, getClass().getClassLoader().getParent(), false);
      mbeanLoaderName = ObjectName.getInstance("marshal:type=mlet");

      server = newMBeanServer();
      server.registerMBean(mbeanLoader, mbeanLoaderName);

      JMXServiceURL url = new JMXServiceURL("rmi", "localhost", 0);
      cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
      cntorServer.start();

      cntor = JMXConnectorFactory.connect(cntorServer.getAddress());
      conn = cntor.getMBeanServerConnection();
      mbeanName = ObjectName.getInstance("marshal:type=mbean");

   }

   public void tearDown() throws Exception
   {
      if (cntor != null) cntor.close();
      if (cntorServer != null) cntorServer.stop();
   }

   protected static class MockNotificationListener implements NotificationListener, Serializable
   {
      long numberOfNotifications = 0;

      public void handleNotification(Notification notification, Object handback)
      {
//   	 	System.out.println("[MockNotificationListener] Notification: "+notification+" Handback: "+handback);
         assertEquals(notification.getSequenceNumber(), numberOfNotifications);
         assertEquals(notification.getType(), Unknown.class.getName());
         numberOfNotifications++;
         synchronized (this)
         {
            this.notify();
         }
      }

      public synchronized void waitOnNotification(long timeout, long sequence) throws InterruptedException
      {
         long to;
         if (timeout > 0)
            to = System.currentTimeMillis() + timeout;
         else
            to = -1;
         while (numberOfNotifications < sequence) // Check for missed notification
         {
            this.wait(timeout);
            if (to > -1 && System.currentTimeMillis() >= to) // Check if waited for full timeout
            {
               Thread.currentThread().interrupt();
               break;
            }
         }
      }

   }

   protected static class MockNotificationFilter implements NotificationFilter, Serializable
   {

      public boolean isNotificationEnabled(Notification notification)
      {
//		System.out.println("[MockNotificationFilter] Notification: "+notification);
         return true;
      }

   }

   private void createMBean() throws Exception
   {
      ObjectInstance inst = conn.createMBean(Marshalling.class.getName(), mbeanName, new Object[]{new Unknown()}, new String[]{Unknown.class.getName()});
   }

   public void testCreateMBean() throws Exception
   {
      conn.createMBean(Marshalling.class.getName(), mbeanName);
      checkRegistration();

      conn.createMBean(Marshalling.class.getName(),
                       mbeanName, new Object[]{new Unknown()},
                       new String[]{Unknown.class.getName()});
      checkRegistration();
   }

   private void checkRegistration() throws Exception
   {
      // Check registrations
      if (!conn.isRegistered(mbeanName)) fail();
      if (!server.isRegistered(mbeanName)) fail();
      conn.unregisterMBean(mbeanName);
   }

   public void testInstanceOf() throws Exception
   {
      createMBean();
      // Check instanceof
      if (!conn.isInstanceOf(mbeanName, Marshalling.class.getName())) fail();
      if (!server.isInstanceOf(mbeanName, Marshalling.class.getName())) fail();
   }

   public void testInvocationUnknownReturn() throws Exception
   {
      createMBean();
      // Check invocation
      Object returned = conn.invoke(mbeanName, "unknownReturnValue", new Object[0], new String[0]);
      if (!returned.getClass().getName().equals(Unknown.class.getName())) fail();
      returned = server.invoke(mbeanName, "unknownReturnValue", new Object[0], new String[0]);
      if (!returned.getClass().getName().equals(Unknown.class.getName())) fail();
      Object remoteUnk = conn.invoke(mbeanName, "unknownArgument", new Object[]{new Unknown()}, new String[]{Unknown.class.getName()});
      Object localUnk = server.invoke(mbeanName, "unknownArgument", new Object[]{new Unknown()}, new String[]{Unknown.class.getName()});
      assertEquals(remoteUnk, localUnk);
   }

   public void testUnregisterMBean() throws Exception
   {
      createMBean();
      // Check unregistration
      conn.unregisterMBean(mbeanName);
      if (conn.isRegistered(mbeanName)) fail();
      if (server.isRegistered(mbeanName)) fail();

   }

   public void testNotifications() throws Exception
   {
      createMBean();
      MockNotificationListener listener = new MockNotificationListener();
      conn.addNotificationListener(mbeanName,
                                   listener,
                                   new MockNotificationFilter(),
                                   new Object());

      Thread.sleep(1000L);
      Attribute att = new Attribute("UnknownAttribute", new Unknown());

      conn.setAttribute(mbeanName, att);

      Thread.sleep(1000L);
      // This triggers a notification
      try
      {
         listener.waitOnNotification(1000L, 1);
      }
      catch (InterruptedException ignore)
      {
      }

      assertEquals(1, listener.numberOfNotifications);

      conn.removeNotificationListener(mbeanName, listener);

      Thread.sleep(1000L);
      conn.setAttribute(mbeanName, att);

      Thread.sleep(1000L);
      // This triggers a notification
      try
      {
         listener.waitOnNotification(1000L, 2);
      }
      catch (InterruptedException ignore)
      {
      }

      assertEquals(1, listener.numberOfNotifications);
   }

   public void testQuery() throws Exception
   {
      createMBean();
      ObjectName pattern = mbeanName;
      ObjectName query = mbeanName;
      Set beans = conn.queryMBeans(pattern, query);
      Object[] set = beans.toArray();
      assertEquals(1, set.length);
//	System.out.println("set[0]: "+set[0]+" class: "+set[0].getClass());
      ObjectInstance inst = (ObjectInstance)set[0];
      assertTrue(inst.getClassName().equals(Marshalling.class.getName()));

      beans = conn.queryNames(pattern, query);
      set = beans.toArray();
      assertEquals(1, set.length);
//	System.out.println("set[0]: "+set[0]+" class: "+set[0].getClass());
      ObjectName nm = (ObjectName)set[0];
      assertTrue(nm.equals(mbeanName));
   }

   public void testAttributes() throws Exception
   {
      createMBean();
      Unknown value = new Unknown();
      Attribute att = new Attribute("UnknownAttribute", value);
      conn.setAttribute(mbeanName, att);
      Object returned = conn.getAttribute(mbeanName, "UnknownAttribute");
      assertTrue(returned.getClass().getName().equals(Unknown.class.getName()));
   }
}
