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

import test.javax.management.remote.JMXConnectorServerTestCase;

/**
 * @version $Revision: 1.4 $
 */
public class LocalConnectorServerTest extends JMXConnectorServerTestCase
{
   public LocalConnectorServerTest(String s)
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
}
