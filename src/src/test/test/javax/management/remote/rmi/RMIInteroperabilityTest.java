/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote.rmi;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Map;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.loading.MLet;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import test.MX4JTestCase;
import test.MutableObject;

/**
 * @version $Revision: 1.4 $
 */
public class RMIInteroperabilityTest extends MX4JTestCase
{
   public RMIInteroperabilityTest(String s)
   {
      super(s);
   }

   private Object startJMXRIRMIConnectorServer() throws Exception
   {
      ClassLoader ri = createRemoteJMXRIWithTestsClassLoader();

      Class mbeanServerFactoryClass = ri.loadClass(MBeanServerFactory.class.getName());
      Method method = mbeanServerFactoryClass.getMethod("newMBeanServer", new Class[0]);
      Object mbeanServer = method.invoke(null, new Object[0]);
      if (!mbeanServer.getClass().getName().startsWith("com.sun")) fail();
      Class mbeanServerClass = ri.loadClass(MBeanServer.class.getName());

      Class serviceURLClass = ri.loadClass(JMXServiceURL.class.getName());
      Constructor constructor = serviceURLClass.getConstructor(new Class[]{String.class});
      Object serviceURL = constructor.newInstance(new Object[]{"service:jmx:rmi://localhost"});
      Class cntorServerFactoryClass = ri.loadClass(JMXConnectorServerFactory.class.getName());
      method = cntorServerFactoryClass.getMethod("newJMXConnectorServer", new Class[]{serviceURLClass, Map.class, mbeanServerClass});
      Object cntorServer = method.invoke(null, new Object[]{serviceURL, null, mbeanServer});

      method = cntorServer.getClass().getMethod("start", new Class[0]);
      method.invoke(cntorServer, new Object[0]);

      return cntorServer;
   }

   public void testJMXRIOnServerMX4JOnClientGetMBeanInfo() throws Exception
   {
      Object cntorServer = startJMXRIRMIConnectorServer();
      Method method = cntorServer.getClass().getMethod("getAddress", new Class[0]);
      Object address = method.invoke(cntorServer, new Object[0]);
      String url = address.toString();

      JMXConnector cntor = JMXConnectorFactory.connect(new JMXServiceURL(url));
      MBeanServerConnection cntion = cntor.getMBeanServerConnection();

      MBeanInfo info = cntion.getMBeanInfo(ObjectName.getInstance("JMImplementation", "type", "MBeanServerDelegate"));
      assertNotNull(info);

      cntor.close();

      method = cntorServer.getClass().getMethod("stop", new Class[0]);
      method.invoke(cntorServer, new Object[0]);
      sleep(2000);
   }

   public void testJMXRIOnServerMX4JOnClientNotificationReceiving() throws Exception
   {
      Object cntorServer = startJMXRIRMIConnectorServer();
      Method method = cntorServer.getClass().getMethod("getAddress", new Class[0]);
      Object address = method.invoke(cntorServer, new Object[0]);
      String url = address.toString();

      JMXConnector cntor = JMXConnectorFactory.connect(new JMXServiceURL(url));
      MBeanServerConnection cntion = cntor.getMBeanServerConnection();

      InteroperabilityListener listener = new InteroperabilityListener();
      ObjectName delegateName = ObjectName.getInstance("JMImplementation:type=MBeanServerDelegate");
      cntion.addNotificationListener(delegateName, listener, null, null);
      sleep(1000);

      ObjectName name = ObjectName.getInstance(":mbean=mlet");
      cntion.createMBean(MLet.class.getName(), name, null);
      sleep(1000);
      Notification notification = (Notification)listener.get();
      assertNotNull(notification);
      listener.reset();

      cntion.removeNotificationListener(delegateName, listener);

      cntion.unregisterMBean(name);
      sleep(1000);

      notification = (Notification)listener.get();
      assertNull(notification);

      cntor.close();

      method = cntorServer.getClass().getMethod("stop", new Class[0]);
      method.invoke(cntorServer, new Object[0]);
      sleep(2000);
   }

   private Object connectJMXRIConnector(ClassLoader ri, String url) throws Exception
   {
      Class serviceURLClass = ri.loadClass(JMXServiceURL.class.getName());
      Constructor constructor = serviceURLClass.getConstructor(new Class[]{String.class});
      Object serviceURL = constructor.newInstance(new Object[]{url});
      Class cntorFactoryClass = ri.loadClass(JMXConnectorFactory.class.getName());
      Method method = cntorFactoryClass.getMethod("connect", new Class[]{serviceURLClass});
      return method.invoke(null, new Object[]{serviceURL});
   }

   public void testMX4JOnServerJMXRIOnClientGetMBeanInfo() throws Exception
   {
      MBeanServer server = newMBeanServer();
      JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://localhost");
      JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
      cntorServer.start();
      url = cntorServer.getAddress();

      ClassLoader ri = createRemoteJMXRIWithTestsClassLoader();
      ClassLoader tccl = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(ri);
      Object cntor = connectJMXRIConnector(ri, url.toString());

      Method method = cntor.getClass().getMethod("getMBeanServerConnection", new Class[0]);
      Object cntion = method.invoke(cntor, new Object[0]);
      Class cntionClass = ri.loadClass(MBeanServerConnection.class.getName());

      Class objectNameClass = ri.loadClass(ObjectName.class.getName());
      method = objectNameClass.getMethod("getInstance", new Class[]{String.class});
      Object objectName = method.invoke(null, new Object[]{"JMImplementation:type=MBeanServerDelegate"});
      method = cntionClass.getMethod("getMBeanInfo", new Class[]{objectNameClass});
      Object info = method.invoke(cntion, new Object[]{objectName});
      assertNotNull(info);

      method = cntor.getClass().getMethod("close", new Class[0]);
      method.invoke(cntor, new Object[0]);

      Thread.currentThread().setContextClassLoader(tccl);
      cntorServer.stop();
      sleep(2000);
   }

   public void testMX4JOnServerJMXRIOnClientNotificationReceiving() throws Exception
   {
      MBeanServer server = newMBeanServer();
      JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://localhost");
      JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
      cntorServer.start();
      url = cntorServer.getAddress();

      ClassLoader ri = createRemoteJMXRIWithTestsClassLoader();
      ClassLoader tccl = Thread.currentThread().getContextClassLoader();
      Thread.currentThread().setContextClassLoader(ri);
      Object cntor = connectJMXRIConnector(ri, url.toString());

      Method method = cntor.getClass().getMethod("getMBeanServerConnection", new Class[0]);
      Object cntion = method.invoke(cntor, new Object[0]);
      Class cntionClass = ri.loadClass(MBeanServerConnection.class.getName());

      Class objectNameClass = ri.loadClass(ObjectName.class.getName());
      method = objectNameClass.getMethod("getInstance", new Class[]{String.class});
      Object delegateName = method.invoke(null, new Object[]{"JMImplementation:type=MBeanServerDelegate"});
      Class notificationListenerClass = ri.loadClass(NotificationListener.class.getName());
      Class notificationFilterClass = ri.loadClass(NotificationFilter.class.getName());
      Object listener = ri.loadClass(InteroperabilityListener.class.getName()).newInstance();
      method = cntionClass.getMethod("addNotificationListener", new Class[]{objectNameClass, notificationListenerClass, notificationFilterClass, Object.class});
      method.invoke(cntion, new Object[]{delegateName, listener, null, null});
      sleep(1000);

      method = objectNameClass.getMethod("getInstance", new Class[]{String.class});
      Object mletName = method.invoke(null, new Object[]{":mbean=mlet"});

      method = cntionClass.getMethod("createMBean", new Class[]{String.class, objectNameClass, objectNameClass});
      method.invoke(cntion, new Object[]{MLet.class.getName(), mletName, null});
      sleep(1000);

      method = listener.getClass().getMethod("get", new Class[0]);
      Object notification = method.invoke(listener, new Object[0]);
      assertNotNull(notification);

      method = listener.getClass().getMethod("reset", new Class[0]);
      method.invoke(listener, new Object[0]);

      method = cntionClass.getMethod("removeNotificationListener", new Class[]{objectNameClass, notificationListenerClass});
      method.invoke(cntion, new Object[]{delegateName, listener});

      method = cntionClass.getMethod("unregisterMBean", new Class[]{objectNameClass});
      method.invoke(cntion, new Object[]{mletName});
      sleep(1000);

      method = listener.getClass().getMethod("get", new Class[0]);
      notification = method.invoke(listener, new Object[0]);
      assertNull(notification);

      method = cntor.getClass().getMethod("close", new Class[0]);
      method.invoke(cntor, new Object[0]);

      Thread.currentThread().setContextClassLoader(tccl);
      cntorServer.stop();
      sleep(2000);
   }

   public static class InteroperabilityListener implements NotificationListener
   {
      private final MutableObject holder;

      public InteroperabilityListener()
      {
         this.holder = new MutableObject(null);
      }

      public void handleNotification(Notification notification, Object handback)
      {
         holder.set(notification);
      }

      public void reset()
      {
         holder.set(null);
      }

      public Object get()
      {
         return holder.get();
      }
   }
}
