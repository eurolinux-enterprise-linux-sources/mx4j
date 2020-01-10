/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.tools.remote.hessian;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import javax.management.remote.JMXServiceURL;

import test.javax.management.remote.JMXConnectorInvocationTestCase;

/**
 * @version $Revision: 1.3 $
 */
public class HessianConnectorInvocationTest extends JMXConnectorInvocationTestCase
{
   public HessianConnectorInvocationTest(String name)
   {
      super(name);
   }

   public JMXServiceURL createJMXConnectorServerAddress() throws MalformedURLException
   {
      return new JMXServiceURL("hessian", null, 8080, "/hessian");
   }

   public Map getEnvironment()
   {
      return new HashMap();
   }
}
