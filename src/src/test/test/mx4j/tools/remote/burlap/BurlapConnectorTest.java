/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.tools.remote.burlap;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import javax.management.remote.JMXServiceURL;

import test.javax.management.remote.JMXConnectorTestCase;

/**
 * @version $Revision: 1.4 $
 */
public class BurlapConnectorTest extends JMXConnectorTestCase
{
   public BurlapConnectorTest(String name)
   {
      super(name);
   }

   public JMXServiceURL createJMXConnectorServerAddress() throws MalformedURLException
   {
      return new JMXServiceURL("burlap", null, 8080, "/burlap");
   }

   public Map getEnvironment()
   {
      return new HashMap();
   }

   public void testDefaultClassLoader() throws Exception
   {
      // No classloading magic is performed by the burlap protocol
   }

   public void testConnectWithProviderClassLoader() throws Exception
   {
      // No classloading magic is performed by the burlap protocol
   }
}
