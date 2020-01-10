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
public class RMIJRMPNotificationsTest extends JMXNotificationsTestCase
{
   public RMIJRMPNotificationsTest(String s)
   {
      super(s);
   }

   public JMXServiceURL createJMXConnectorServerAddress() throws MalformedURLException
   {
      return new JMXServiceURL("rmi", "localhost", 0);
   }

   public Map getEnvironment()
   {
      HashMap env = new HashMap();
      env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
      env.put(Context.PROVIDER_URL, "rmi://localhost:1099");
      return env;
   }
}
