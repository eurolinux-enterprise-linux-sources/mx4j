/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote.rmi;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import javax.management.remote.JMXServiceURL;
import javax.naming.Context;

import test.javax.management.remote.JMXNotificationsTestCase;

/**
 * @version $Revision: 1.3 $
 */
public class RMIIIOPNotificationsTest extends JMXNotificationsTestCase
{
   public RMIIIOPNotificationsTest(String s)
   {
      super(s);
   }

   public JMXServiceURL createJMXConnectorServerAddress() throws MalformedURLException
   {
      return new JMXServiceURL("iiop", "localhost", 0);
   }

   public Map getEnvironment()
   {
      HashMap env = new HashMap();
      env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.cosnaming.CNCtxFactory");
      env.put(Context.PROVIDER_URL, "iiop://localhost:1100");
      return env;
   }
}
