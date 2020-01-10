/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.tools.remote;

import java.io.IOException;
import java.util.Set;
import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import test.javax.management.remote.JMXConnectorServerTestCase;

/**
 * @version $Revision: 1.4 $
 */
public abstract class HTTPConnectorServerTestCase extends JMXConnectorServerTestCase
{
   public HTTPConnectorServerTestCase(String name)
   {
      super(name);
   }

   public void testTwoConnectorServersOneHTTPServerStart1Start2Invoke1Invoke2Stop1Stop2() throws Exception
   {
      JMXServiceURL temp = createJMXConnectorServerAddress();
      JMXServiceURL url1 = new JMXServiceURL(temp.getProtocol(), temp.getHost(), temp.getPort(), "one");
      JMXServiceURL url2 = new JMXServiceURL(temp.getProtocol(), temp.getHost(), temp.getPort(), "two");

      JMXConnectorServer cntorServer1 = null;
      JMXConnectorServer cntorServer2 = null;

      try
      {
         cntorServer1 = JMXConnectorServerFactory.newJMXConnectorServer(url1, getEnvironment(), newMBeanServer());
         cntorServer2 = JMXConnectorServerFactory.newJMXConnectorServer(url2, getEnvironment(), newMBeanServer());

         cntorServer1.start();
         sleep(5000);
         cntorServer2.start();
         sleep(5000);

         // Make sure they work
         JMXConnector cntor1 = null;
         JMXConnector cntor2 = null;
         try
         {
            cntor1 = JMXConnectorFactory.connect(cntorServer1.getAddress(), getEnvironment());
            cntor2 = JMXConnectorFactory.connect(cntorServer2.getAddress(), getEnvironment());

            Set names = cntor1.getMBeanServerConnection().queryNames(null, null);
            assertNotNull(names);
            assertTrue(names.size() > 0);

            names = cntor2.getMBeanServerConnection().queryNames(null, null);
            assertNotNull(names);
            assertTrue(names.size() > 0);
         }
         finally
         {
            if (cntor2 != null) cntor2.close();
            if (cntor1 != null) cntor1.close();
         }
      }
      finally
      {
         if (cntorServer2 != null) cntorServer2.stop();
         if (cntorServer1 != null) cntorServer1.stop();
      }
   }

   public void testTwoConnectorServersOneHTTPServerStart1Start2Stop1Invoke1Invoke2Stop2() throws Exception
   {
      JMXServiceURL temp = createJMXConnectorServerAddress();
      JMXServiceURL url1 = new JMXServiceURL(temp.getProtocol(), temp.getHost(), temp.getPort(), "one");
      JMXServiceURL url2 = new JMXServiceURL(temp.getProtocol(), temp.getHost(), temp.getPort(), "two");

      JMXConnectorServer cntorServer1 = null;
      JMXConnectorServer cntorServer2 = null;

      try
      {
         cntorServer1 = JMXConnectorServerFactory.newJMXConnectorServer(url1, getEnvironment(), newMBeanServer());
         cntorServer2 = JMXConnectorServerFactory.newJMXConnectorServer(url2, getEnvironment(), newMBeanServer());

         cntorServer1.start();
         sleep(5000);
         cntorServer2.start();
         sleep(5000);

         // Make sure they work
         JMXConnector cntor1 = null;
         JMXConnector cntor2 = null;
         try
         {
            cntor1 = JMXConnectorFactory.connect(cntorServer1.getAddress(), getEnvironment());
            cntor2 = JMXConnectorFactory.connect(cntorServer2.getAddress(), getEnvironment());

            MBeanServerConnection conn1 = cntor1.getMBeanServerConnection();
            Set names = conn1.queryNames(null, null);
            assertNotNull(names);
            assertTrue(names.size() > 0);

            MBeanServerConnection conn2 = cntor2.getMBeanServerConnection();
            names = conn2.queryNames(null, null);
            assertNotNull(names);
            assertTrue(names.size() > 0);

            cntorServer1.stop();

            try
            {
               conn1.queryNames(null, null);
               fail();
            }
            catch (IOException ignored)
            {
            }

            names = conn2.queryNames(null, null);
            assertNotNull(names);
            assertTrue(names.size() > 0);
         }
         catch (Exception x)
         {
            x.printStackTrace();
            throw x;
         }
         finally
         {
            if (cntor2 != null) cntor2.close();
         }
      }
      finally
      {
         if (cntorServer2 != null) cntorServer2.stop();
      }
   }

   public void testConnectorServerOnDefaultHTTPPort() throws Exception
   {
      JMXServiceURL temp = createJMXConnectorServerAddress();
      JMXServiceURL url = new JMXServiceURL(temp.getProtocol(), temp.getHost(), 80, temp.getURLPath());
      JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());

      try
      {
         cntorServer.start();
         sleep(5000);
         JMXConnector cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());
         try
         {
            MBeanServerConnection server = cntor.getMBeanServerConnection();
            Set names = server.queryNames(null, null);
            assertNotNull(names);
            assertTrue(names.size() > 0);
            assertEquals(names.size(), server.getMBeanCount().intValue());
         }
         finally
         {
            cntor.close();
         }
      }
      finally
      {
         cntorServer.stop();
      }
   }

   public void testStartWithProviderClassLoader() throws Exception
   {
      // Do nothing since messing with the context classloader confuses the web container
   }
}
