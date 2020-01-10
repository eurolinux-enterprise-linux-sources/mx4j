/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.tools.remote.local;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import javax.management.remote.JMXServiceURL;

import test.javax.management.remote.JMXConnectorTestCase;

/**
 * @version $Revision: 1.5 $
 */
public class LocalConnectorTest extends JMXConnectorTestCase
{
   public LocalConnectorTest(String s)
   {
      super(s);
   }

   public JMXServiceURL createJMXConnectorServerAddress() throws MalformedURLException
   {
      return new JMXServiceURL("local", "localhost", 0);
   }

   public Map getEnvironment()
   {
      return new HashMap();
   }

   public void testDefaultClassLoader() throws Exception
   {
      // Nothing to test, the local connector does not handle classloading
   }
}
