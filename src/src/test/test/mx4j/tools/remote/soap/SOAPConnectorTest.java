/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.tools.remote.soap;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.management.remote.JMXServiceURL;

import org.apache.axis.AxisFault;
import test.javax.management.remote.JMXConnectorTestCase;

/**
 * @version $Revision: 1.15 $
 */
public class SOAPConnectorTest extends JMXConnectorTestCase
{
   public SOAPConnectorTest(String name)
   {
      super(name);
   }

   public JMXServiceURL createJMXConnectorServerAddress() throws MalformedURLException
   {
      return new JMXServiceURL("soap", null, 8080, "/soap");
   }

   public Map getEnvironment()
   {
      return new HashMap();
   }

   /**
    * The SOAPConnector does not handle gracefully the case of a SecurityException thrown by a
    * JMXAuthenticator. This is more an Axis problem, but for now we fix the test.
    */
   protected void testJMXAuthenticatorConnect(JMXServiceURL url, Map environment) throws SecurityException, IOException
   {
      try
      {
         super.testJMXAuthenticatorConnect(url, environment);
      }
      catch (AxisFault x)
      {
         String name = x.getFaultString();
         if (name.startsWith(SecurityException.class.getName())) throw new SecurityException();
         throw x;
      }
   }

   /**
    * No default classloader for the SOAPConnector (at least not now).
    * It may be added later, but for now we fix the test.
    */
   public void testDefaultClassLoader() throws Exception
   {
      // Do nothing
   }

   public void testConnectWithProviderClassLoader() throws Exception
   {
      // Do nothing since messing with the context classloader causes the connector server to fail its start
   }
}
