/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote;

import java.net.MalformedURLException;
import java.util.HashMap;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXProviderException;
import javax.management.remote.JMXServiceURL;

import test.MX4JTestCase;

/**
 * @version $Revision: 1.3 $
 */
public class JMXConnectorServerFactoryTest extends MX4JTestCase
{
   public JMXConnectorServerFactoryTest(String s)
   {
      super(s);
   }

   public void testInvalidURLs() throws Exception
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

   public void testInvalidPackages() throws Exception
   {
      HashMap env = new HashMap();
      JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://host");

      // Only Strings
      env.put(JMXConnectorServerFactory.PROTOCOL_PROVIDER_PACKAGES, new Object());
      try
      {
         JMXConnectorServerFactory.newJMXConnectorServer(url, env, null);
         fail("Only Strings can be specified as provider packages");
      }
      catch (JMXProviderException x)
      {
      }

      // Empty not allowed
      env.put(JMXConnectorServerFactory.PROTOCOL_PROVIDER_PACKAGES, "");
      try
      {
         JMXConnectorServerFactory.newJMXConnectorServer(url, env, null);
         fail("Provider package string cannot be empty");
      }
      catch (JMXProviderException x)
      {
      }

      // Empty not allowed
      env.put(JMXConnectorServerFactory.PROTOCOL_PROVIDER_PACKAGES, "dummy| |dummy");
      try
      {
         JMXConnectorServerFactory.newJMXConnectorServer(url, env, null);
         fail("Provider package string cannot contain an empty string");
      }
      catch (JMXProviderException x)
      {
      }
   }

   public void testInvalidProtocol() throws Exception
   {
      JMXServiceURL url = new JMXServiceURL("service:jmx:dummy://host");
      try
      {
         JMXConnectorServerFactory.newJMXConnectorServer(url, null, null);
         fail();
      }
      catch (MalformedURLException x)
      {
      }
   }

   public void testInvalidClassLoader() throws Exception
   {
      HashMap env = new HashMap();
      JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://host");

      env.put(JMXConnectorServerFactory.PROTOCOL_PROVIDER_CLASS_LOADER, new Object());
      try
      {
         JMXConnectorServerFactory.newJMXConnectorServer(url, env, null);
         fail();
      }
      catch (IllegalArgumentException x)
      {
      }
   }

   public void testLoadProviderWithProvidedClassLoader() throws Exception
   {
      HashMap env = new HashMap();
      JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://host");

      env.put(JMXConnectorServerFactory.PROTOCOL_PROVIDER_CLASS_LOADER, getClass().getClassLoader());
      JMXConnectorServerFactory.newJMXConnectorServer(url, env, null);
   }

   public void testRMIProvider() throws Exception
   {
      JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://host");
      JMXConnectorServer connector = JMXConnectorServerFactory.newJMXConnectorServer(url, null, null);
      assertNotNull(connector);
   }
}
