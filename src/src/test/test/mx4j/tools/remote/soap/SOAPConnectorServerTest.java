/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.tools.remote.soap;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import mx4j.tools.remote.http.HTTPConnectorServer;
import mx4j.tools.remote.http.jetty.JettyWebContainer;
import org.apache.axis.transport.http.AxisServlet;
import test.mx4j.tools.remote.HTTPConnectorServerTestCase;

/**
 * @version $Revision: 1.9 $
 */
public class SOAPConnectorServerTest extends HTTPConnectorServerTestCase
{
   public SOAPConnectorServerTest(String s)
   {
      super(s);
   }

   public JMXServiceURL createJMXConnectorServerAddress() throws MalformedURLException
   {
      return new JMXServiceURL("soap", null, 8080, "/soap");
   }

   public Map getEnvironment()
   {
      return new HashMap();
   }

   public void testExternalWebContainer() throws Exception
   {
      // Start Jetty, externally
      JettyWebContainer jetty = new JettyWebContainer();
      try
      {
         JMXServiceURL url = new JMXServiceURL("soap", null, 8080, "/external");
         jetty.start(url, null);
         jetty.deploy(AxisServlet.class.getName(), url, null);

         // A SOAPConnector
         JMXConnectorServer cntorServer = null;
         JMXConnector cntor = null;
         try
         {
            Map environment = new HashMap();
            environment.put(HTTPConnectorServer.USE_EXTERNAL_WEB_CONTAINER, Boolean.TRUE);
            cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, environment, newMBeanServer());
            cntorServer.start();

            // Check that it works
            cntor = JMXConnectorFactory.connect(cntorServer.getAddress());
            MBeanServerConnection cntion = cntor.getMBeanServerConnection();
            Set names = cntion.queryNames(null, null);
            assertNotNull(names);
            assertFalse(names.isEmpty());
         }
         finally
         {
            if (cntor != null) cntor.close();
            if (cntorServer != null) cntorServer.stop();
         }
      }
      finally
      {
         jetty.stop();
      }
   }
}
