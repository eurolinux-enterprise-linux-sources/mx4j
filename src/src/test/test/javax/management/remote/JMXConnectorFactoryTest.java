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
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXProviderException;
import javax.management.remote.JMXServiceURL;

import test.MX4JTestCase;

/**
 * @version $Revision: 1.9 $
 */
public class JMXConnectorFactoryTest extends MX4JTestCase
{
   public JMXConnectorFactoryTest(String s)
   {
      super(s);
   }

   public void testInvalidURLs() throws Exception
   {
      try
      {
         JMXConnectorFactory.connect(null);
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
      env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, new Object());
      try
      {
         JMXConnectorFactory.newJMXConnector(url, env);
         fail("Only Strings can be specified as provider packages");
      }
      catch (JMXProviderException x)
      {
      }

      // Empty not allowed
      env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "");
      try
      {
         JMXConnectorFactory.newJMXConnector(url, env);
         fail("Provider package string cannot be empty");
      }
      catch (JMXProviderException x)
      {
      }

      // Empty not allowed
      env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_PACKAGES, "dummy| |dummy");
      try
      {
         JMXConnectorFactory.newJMXConnector(url, env);
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
         JMXConnectorFactory.newJMXConnector(url, null);
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

      env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_CLASS_LOADER, new Object());
      try
      {
         JMXConnectorFactory.newJMXConnector(url, env);
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
      ClassLoader old = Thread.currentThread().getContextClassLoader();
      try
      {
         Thread.currentThread().setContextClassLoader(getClass().getClassLoader().getParent());
         env.put(JMXConnectorFactory.PROTOCOL_PROVIDER_CLASS_LOADER, getClass().getClassLoader());
         JMXConnector connector = JMXConnectorFactory.newJMXConnector(url, env);
         assertNotNull(connector);
      }
      finally
      {
         Thread.currentThread().setContextClassLoader(old);
      }
   }

   public void testRMIProvider() throws Exception
   {
      JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://host");
      JMXConnector connector = JMXConnectorFactory.newJMXConnector(url, null);
      assertNotNull(connector);
   }
}
