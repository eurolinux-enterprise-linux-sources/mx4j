/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote.rmi;

import javax.management.MBeanServer;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import test.javax.management.MultiThreadMBeanServerTest;

/**
 * @version $Revision: 1.3 $
 */
public class RMIJRMPMultiThreadMBeanServerConnectionTest extends MultiThreadMBeanServerTest
{
   private JMXConnectorServer connectorServer;

   public RMIJRMPMultiThreadMBeanServerConnectionTest(String s)
   {
      super(s);
   }

   protected void setUp() throws Exception
   {
      MBeanServer server = newMBeanServer();
      JMXServiceURL url = new JMXServiceURL("rmi", "localhost", 0);
      connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
      connectorServer.start();
      sleep(1000);

      JMXConnector connector = JMXConnectorFactory.connect(connectorServer.getAddress());
      this.server = connector.getMBeanServerConnection();
   }

   protected void tearDown() throws Exception
   {
      super.tearDown();
      connectorServer.stop();
      sleep(1000);
   }
}
