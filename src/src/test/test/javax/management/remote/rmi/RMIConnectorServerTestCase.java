/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote.rmi;

import java.net.MalformedURLException;
import java.util.Map;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnectorServer;

import test.javax.management.remote.JMXConnectorServerTestCase;

/**
 * @version $Revision: 1.8 $
 */
public abstract class RMIConnectorServerTestCase extends JMXConnectorServerTestCase implements RMITestCase
{
   public RMIConnectorServerTestCase(String s)
   {
      super(s);
   }

   public void testNewRMIConnectorServerNullURL() throws Exception
   {
      try
      {
         new RMIConnectorServer(null, null);
         fail();
      }
      catch (IllegalArgumentException x)
      {
      }
   }

   public void testNewRMIConnectorServerNullEnvironment() throws Exception
   {
      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer server = new RMIConnectorServer(url, null, newMBeanServer());
      try
      {
         server.start();
      }
      finally
      {
         server.stop();
      }
   }

   public void testNewRMIConnectorServerWithFactoryWrongClassLoader() throws Exception
   {
      JMXServiceURL url = createJMXConnectorServerAddress();
      Map env = getEnvironment();
      env.put(JMXConnectorServerFactory.DEFAULT_CLASS_LOADER, new Object());
      try
      {
         JMXConnectorServerFactory.newJMXConnectorServer(url, env, null);
         fail();
      }
      catch (IllegalArgumentException x)
      {
      }
   }

   public void testNewRMIConnectorServerWithFactoryWrongClassLoaderName() throws Exception
   {
      JMXServiceURL url = createJMXConnectorServerAddress();
      Map env = getEnvironment();
      env.put(JMXConnectorServerFactory.DEFAULT_CLASS_LOADER_NAME, new Object());
      try
      {
         JMXConnectorServerFactory.newJMXConnectorServer(url, env, null);
         fail();
      }
      catch (IllegalArgumentException x)
      {
      }
   }

   public void testJNDIBindWithWrongPath1() throws Exception
   {
      JMXServiceURL temp = createJMXConnectorServerAddress();
      JMXServiceURL url = new JMXServiceURL(temp.getProtocol(), temp.getHost(), temp.getPort(), "/jndi");
      JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());
      try
      {
         cntorServer.start();
         fail();
      }
      catch (MalformedURLException x)
      {
      }
   }

   public void testJNDIBindWithWrongPath2() throws Exception
   {
      JMXServiceURL temp = createJMXConnectorServerAddress();
      JMXServiceURL url = new JMXServiceURL(temp.getProtocol(), temp.getHost(), temp.getPort(), "/jndi");
      JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());
      try
      {
         cntorServer.start();
         fail();
      }
      catch (MalformedURLException x)
      {
      }
   }

   public void testJNDIBindWithRelativePath() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      try
      {
         startNaming();

         JMXServiceURL temp = createJMXConnectorServerAddress();
         JMXServiceURL url = new JMXServiceURL(temp.getProtocol(), temp.getHost(), temp.getPort(), "/jndi/jmx");
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());
         cntorServer.start();
      }
      finally
      {
         if (cntorServer != null) cntorServer.stop();
         stopNaming();
      }
   }

   public void testJNDIBindWithAbsolutePath() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      try
      {
         startNaming();

         JMXServiceURL temp = createJMXConnectorServerAddress();
         JMXServiceURL url = new JMXServiceURL(temp.getProtocol(), temp.getHost(), temp.getPort(), "/jndi/" + temp.getProtocol() + "://localhost:" + getNamingPort() + "/jmx");
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());
         cntorServer.start();
      }
      finally
      {
         if (cntorServer != null) cntorServer.stop();
         stopNaming();
      }
   }
}
