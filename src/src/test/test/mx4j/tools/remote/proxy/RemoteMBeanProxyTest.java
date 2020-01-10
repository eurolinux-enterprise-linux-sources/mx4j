/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.tools.remote.proxy;

import java.io.IOException;
import java.net.URL;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerDelegateMBean;
import javax.management.MBeanServerInvocationHandler;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.loading.MLet;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.timer.Timer;

import mx4j.tools.remote.proxy.RemoteMBeanProxy;
import mx4j.util.Utils;
import test.MX4JTestCase;
import test.MutableObject;

/**
 * @version $Revision: 1.6 $
 */
public class RemoteMBeanProxyTest extends MX4JTestCase
{
   public RemoteMBeanProxyTest(String s)
   {
      super(s);
   }

   public void testNotifications() throws Exception
   {
      // The remote MBeanServer
      MBeanServer remoteServer = newMBeanServer();

      JMXServiceURL address1 = new JMXServiceURL("rmi", "localhost", 0);
      JMXConnectorServer connectorServer1 = JMXConnectorServerFactory.newJMXConnectorServer(address1, null, null);
      ObjectName connectorServerName1 = ObjectName.getInstance(":type=connector,protocol=" + address1.getProtocol());
      remoteServer.registerMBean(connectorServer1, connectorServerName1);
      connectorServer1.start();
      address1 = connectorServer1.getAddress();

      ObjectName remoteDelegateName = ObjectName.getInstance("JMImplementation:type=MBeanServerDelegate");

      // The local MBeanServer
      MBeanServer localServer = newMBeanServer();

      // The MBean proxy for the remote delegate
      JMXConnector cntor = JMXConnectorFactory.newJMXConnector(address1, null);
      RemoteMBeanProxy proxy = new RemoteMBeanProxy(remoteDelegateName, cntor, null, null);
      ObjectName proxyName = ObjectName.getInstance(":proxy=" + ObjectName.quote(remoteDelegateName.getCanonicalName()));
      localServer.registerMBean(proxy, proxyName);

      // Register a listener to the MBean proxy for the remote delegate
      final MutableObject holder = new MutableObject(null);
      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            synchronized (holder)
            {
               holder.set(notification);
            }
         }
      };
      localServer.addNotificationListener(proxyName, listener, null, null);

      // Sleep to make sure the remote notifications threads started before we emit the notification
      sleep(1000);

      // Add an MBean to the remote MBeanServer: this will trigger a notification from the remote delegate,
      // that should be dispatched transparently the the listener above
      MLet remoteMLet = new MLet();
      ObjectName remoteMLetName = ObjectName.getInstance(":type=mlet");
      remoteServer.registerMBean(remoteMLet, remoteMLetName);

      synchronized (holder)
      {
         while (holder.get() == null) holder.wait(10);
         assertNotNull(holder.get());
         holder.set(null);
      }

      // Remove the listener
      localServer.removeNotificationListener(proxyName, listener);

      // Unregister the MLet: this will trigger a notification
      remoteServer.unregisterMBean(remoteMLetName);

      assertNull(holder.get());
   }

   public void testMultipleRemoteMBeanServers() throws Exception
   {
      // The 1st remote server
      MBeanServer remoteServer1 = newMBeanServer();
      MLet mlet = new MLet(new URL[]{new URL("http", "host", 80, "/path")});
      ObjectName remoteMLetName = ObjectName.getInstance(":type=mlet");
      remoteServer1.registerMBean(mlet, remoteMLetName);
      JMXServiceURL address1 = new JMXServiceURL("rmi", "localhost", 0);
      JMXConnectorServer connectorServer1 = JMXConnectorServerFactory.newJMXConnectorServer(address1, null, null);
      ObjectName connectorServerName1 = ObjectName.getInstance(":type=connector,protocol=" + address1.getProtocol());
      remoteServer1.registerMBean(connectorServer1, connectorServerName1);
      connectorServer1.start();
      address1 = connectorServer1.getAddress();

      // The 2nd remote server
      MBeanServer remoteServer2 = newMBeanServer();
      Timer timer = new Timer();
      ObjectName remoteTimerName = ObjectName.getInstance(":type=timer");
      remoteServer2.registerMBean(timer, remoteTimerName);
      timer.start();
      JMXServiceURL address2 = new JMXServiceURL("rmi", "localhost", 0);
      JMXConnectorServer connectorServer2 = JMXConnectorServerFactory.newJMXConnectorServer(address2, null, remoteServer2);
      connectorServer2.start();
      address2 = connectorServer2.getAddress();

      // The local server
      MBeanServer localServer = newMBeanServer();
      RemoteMBeanProxy proxy1 = new RemoteMBeanProxy(remoteMLetName, address1, null, null);
      JMXConnector cntor = JMXConnectorFactory.connect(address2);
      RemoteMBeanProxy proxy2 = new RemoteMBeanProxy(remoteTimerName, cntor.getMBeanServerConnection());
      ObjectName proxyName1 = ObjectName.getInstance(":proxy=" + ObjectName.quote(remoteMLetName.getCanonicalName()));
      ObjectName proxyName2 = ObjectName.getInstance(":proxy=" + ObjectName.quote(remoteTimerName.getCanonicalName()));
      localServer.registerMBean(proxy1, proxyName1);
      localServer.registerMBean(proxy2, proxyName2);
      JMXServiceURL address3 = new JMXServiceURL("local", "localhost", 0);
      JMXConnectorServer connectorServer3 = JMXConnectorServerFactory.newJMXConnectorServer(address3, null, localServer);
      connectorServer3.start();
      address3 = connectorServer3.getAddress();

      // The client
      JMXConnector connector = JMXConnectorFactory.connect(address3);
      MBeanServerConnection mbsc = connector.getMBeanServerConnection();

      // Tests
      URL[] urls = (URL[])mbsc.getAttribute(proxyName1, "URLs");
      if (!Utils.arrayEquals(urls, mlet.getURLs())) fail();

      if (!timer.isActive()) fail();
      mbsc.invoke(proxyName2, "stop", null, null);
      if (timer.isActive()) fail();
   }

   public void testJMXConnectorCloseOnDeregistration() throws Exception
   {
      // The remote MBeanServer
      MBeanServer remoteServer = newMBeanServer();

      JMXServiceURL address = new JMXServiceURL("rmi", "localhost", 0);
      JMXConnectorServer connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(address, null, null);
      ObjectName connectorServerName = ObjectName.getInstance(":type=connector,protocol=" + address.getProtocol());
      remoteServer.registerMBean(connectorServer, connectorServerName);
      connectorServer.start();
      address = connectorServer.getAddress();

      ObjectName remoteDelegateName = ObjectName.getInstance("JMImplementation:type=MBeanServerDelegate");

      // The local MBeanServer
      MBeanServer localServer = newMBeanServer();

      // The MBean proxy for the remote delegate
      JMXConnector cntor = JMXConnectorFactory.newJMXConnector(address, null);
      RemoteMBeanProxy proxy = new RemoteMBeanProxy(remoteDelegateName, cntor, null, null);
      ObjectName proxyName = ObjectName.getInstance(":proxy=" + ObjectName.quote(remoteDelegateName.getCanonicalName()));
      localServer.registerMBean(proxy, proxyName);

      // Be sure it works
      MBeanServerDelegateMBean mbean = (MBeanServerDelegateMBean)MBeanServerInvocationHandler.newProxyInstance(localServer, proxyName, MBeanServerDelegateMBean.class, true);
      String vendor = mbean.getImplementationVendor();
      assertNotNull(vendor);

      // Unregister and be sure the connector is closed
      localServer.unregisterMBean(proxyName);
      try
      {
         cntor.getMBeanServerConnection();
         fail();
      }
      catch (IOException x)
      {
      }
   }
}
