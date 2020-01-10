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

import mx4j.tools.naming.CosNamingService;

/**
 * @version $Revision: 1.7 $
 */
public class RMIIIOPConnectorTest extends RMIConnectorTestCase
{
   private CosNamingService naming;

   public RMIIIOPConnectorTest(String s)
   {
      super(s);
   }

   public JMXServiceURL createJMXConnectorServerAddress() throws MalformedURLException
   {
      return new JMXServiceURL("iiop", "localhost", 0);
   }

   public void startNaming() throws Exception
   {
      naming = new CosNamingService(getNamingPort());
      naming.start();
      Thread.sleep(5000);
   }

   public void stopNaming() throws Exception
   {
      naming.stop();
      naming = null;
      Thread.sleep(5000);
   }

   public int getNamingPort()
   {
      return 1100;
   }

   public Map getEnvironment()
   {
      HashMap env = new HashMap();
      env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.cosnaming.CNCtxFactory");
      env.put(Context.PROVIDER_URL, "iiop://localhost:" + getNamingPort());
//      env.put("org.omg.CORBA.ORBInitialPort", String.valueOf(getNamingPort()));
      return env;
   }
}
