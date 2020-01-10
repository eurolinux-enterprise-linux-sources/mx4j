/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote.rmi;

import java.io.IOException;
import java.io.Serializable;
import java.net.MalformedURLException;
import java.net.ServerSocket;
import java.net.Socket;
import java.rmi.server.RMIClientSocketFactory;
import java.rmi.server.RMIServerSocketFactory;
import java.util.HashMap;
import java.util.Map;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnectorServer;
import javax.naming.Context;

import mx4j.tools.naming.NamingService;
import test.MutableBoolean;

/**
 * @version $Revision: 1.9 $
 */
public class RMIJRMPConnectorTest extends RMIConnectorTestCase
{
   private NamingService naming;

   public RMIJRMPConnectorTest(String s)
   {
      super(s);
   }

   public JMXServiceURL createJMXConnectorServerAddress() throws MalformedURLException
   {
      return new JMXServiceURL("rmi", "localhost", 0);
   }

   public void startNaming() throws Exception
   {
      naming = new NamingService(getNamingPort());
      naming.start();
   }

   public void stopNaming() throws Exception
   {
      naming.stop();
      naming = null;
      Thread.sleep(5000);
   }

   public int getNamingPort()
   {
      return 1099;
   }

   public Map getEnvironment()
   {
      HashMap env = new HashMap();
      env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
      env.put(Context.PROVIDER_URL, "rmi://localhost:" + getNamingPort());
      return env;
   }

   public void testRMIConnectorWithCustomSocketFactories() throws Exception
   {
      RMIClientSocketFactory client = new RMICSF();

      final MutableBoolean serverCheck = new MutableBoolean(false);
      RMIServerSocketFactory server = new RMISSF(serverCheck);

      JMXServiceURL url = createJMXConnectorServerAddress();
      Map env = getEnvironment();
      env.put(RMIConnectorServer.RMI_CLIENT_SOCKET_FACTORY_ATTRIBUTE, client);
      env.put(RMIConnectorServer.RMI_SERVER_SOCKET_FACTORY_ATTRIBUTE, server);

      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;

      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, env, newMBeanServer());
         cntorServer.start();

         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());
         assertTrue(serverCheck.get());
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public static class RMICSF implements RMIClientSocketFactory, Serializable
   {
      public Socket createSocket(String host, int port) throws IOException
      {
         return new Socket(host, port);
      }
   }

   public static class RMISSF implements RMIServerSocketFactory
   {
      private MutableBoolean check;

      public RMISSF(MutableBoolean check)
      {
         this.check = check;
      }

      public ServerSocket createServerSocket(int port) throws IOException
      {
         check.set(true);
         return new ServerSocket(port);
      }
   }
}
