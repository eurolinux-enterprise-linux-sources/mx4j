/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.MalformedURLException;
import java.util.Map;
import javax.management.MBeanServer;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.MBeanServerForwarder;

import test.MX4JTestCase;

/**
 * @version $Revision: 1.11 $
 */
public abstract class JMXConnectorServerTestCase extends MX4JTestCase
{
   public JMXConnectorServerTestCase(String name)
   {
      super(name);
   }

   public abstract JMXServiceURL createJMXConnectorServerAddress() throws MalformedURLException;

   public abstract Map getEnvironment();

   public void testNewJMXConnectorServerWithNullURL() throws Exception
   {
      try
      {
         JMXConnectorServerFactory.newJMXConnectorServer(null, null, null);
         fail();
      }
      catch (NullPointerException x)
      {
      }
   }

   public void testNewJMXConnectorServerWithFactory() throws Exception
   {
      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer server = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), null);
      if (server == null) fail();
   }

   public void testStartWithoutMBeanServer() throws Exception
   {
      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer server = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), null);
      try
      {
         server.start();
         fail();
      }
      catch (IllegalStateException x)
      {
      }
   }

   public void testMBeanServerForwarderNoMBeanServer() throws Exception
   {
      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer server = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), null);
      MBeanServerForwarder forwarder = (MBeanServerForwarder)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{MBeanServerForwarder.class}, new InvocationHandler()
      {
         public Object invoke(Object proxy, Method method, Object[] args)
                 throws Throwable
         {
            return null;
         }
      });

      try
      {
         server.setMBeanServerForwarder(forwarder);
         fail("No MBeanServer to forward to");
      }
      catch (IllegalStateException x)
      {
      }
   }

   public void testMBeanServerForwarder() throws Exception
   {
      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer server = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());
      MBeanServerForwarder forwarder = (MBeanServerForwarder)Proxy.newProxyInstance(getClass().getClassLoader(), new Class[]{MBeanServerForwarder.class}, new InvocationHandler()
      {
         public Object invoke(Object proxy, Method method, Object[] args)
                 throws Throwable
         {
            return null;
         }
      });

      server.setMBeanServerForwarder(forwarder);

      try
      {
         server.start();
         if (server.getMBeanServer() != forwarder) fail();
      }
      finally
      {
         server.stop();
      }
   }

   public void testStart() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      try
      {
         JMXServiceURL url = createJMXConnectorServerAddress();
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());
         cntorServer.start();
      }
      finally
      {
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testStartStart() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      try
      {
         JMXServiceURL url = createJMXConnectorServerAddress();
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());
         cntorServer.start();
         cntorServer.start();
      }
      finally
      {
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testStartStop() throws Exception
   {
      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());
      cntorServer.start();
      cntorServer.stop();
   }

   public void testStartStopStart() throws Exception
   {
      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());
      cntorServer.start();
      cntorServer.stop();
      try
      {
         cntorServer.start();
         fail();
      }
      catch (IOException x)
      {
      }
   }

   public void testStartStopStop() throws Exception
   {
      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());
      cntorServer.start();
      cntorServer.stop();
      cntorServer.stop();
   }

   public void testTwoConnectorServers() throws Exception
   {
      JMXConnectorServer cntorServer1 = null;
      JMXConnectorServer cntorServer2 = null;
      JMXConnector cntor10 = null;
      JMXConnector cntor11 = null;
      JMXConnector cntor12 = null;
      JMXConnector cntor20 = null;
      try
      {
         JMXServiceURL url1 = createJMXConnectorServerAddress();
         JMXServiceURL url2 = new JMXServiceURL(url1.getProtocol(), url1.getHost(), (url1.getPort() > 0) ? (url1.getPort() + 1) : 0, url1.getURLPath());

         MBeanServer server = newMBeanServer();
         cntorServer1 = JMXConnectorServerFactory.newJMXConnectorServer(url1, getEnvironment(), server);
         cntorServer2 = JMXConnectorServerFactory.newJMXConnectorServer(url2, getEnvironment(), server);
         cntorServer1.start();
         cntorServer2.start();

         cntor10 = JMXConnectorFactory.connect(cntorServer1.getAddress(), getEnvironment());
         cntor11 = JMXConnectorFactory.connect(cntorServer1.getAddress(), getEnvironment());
         cntor12 = JMXConnectorFactory.connect(cntorServer1.getAddress(), getEnvironment());
         cntor20 = JMXConnectorFactory.connect(cntorServer2.getAddress(), getEnvironment());

         if (cntor10.getConnectionId().equals(cntor11.getConnectionId())) fail();
         if (cntor10.getConnectionId().equals(cntor12.getConnectionId())) fail();
         if (cntor10.getConnectionId().equals(cntor20.getConnectionId())) fail();

      }
      finally
      {
         if (cntor20 != null) cntor20.close();
         if (cntor12 != null) cntor12.close();
         if (cntor11 != null) cntor11.close();
         if (cntor10 != null) cntor10.close();

         if (cntorServer2 != null) cntorServer2.stop();
         if (cntorServer1 != null) cntorServer1.stop();
      }
   }

   public void testStartWithProviderClassLoader() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      try
      {
         JMXServiceURL url = createJMXConnectorServerAddress();
         MBeanServer server = newMBeanServer();
         Map serverEnv = getEnvironment();
         serverEnv.put(JMXConnectorServerFactory.PROTOCOL_PROVIDER_CLASS_LOADER, getClass().getClassLoader());
         Thread.currentThread().setContextClassLoader(getClass().getClassLoader().getParent());
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, serverEnv, server);
         cntorServer.start();
      }
      finally
      {
         if (cntorServer != null) cntorServer.stop();
      }
   }
}
