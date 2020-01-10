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

import test.javax.management.remote.JMXNotificationsTestCase;

/**
 * @version $Revision: 1.4 $
 */
public class HessianNotificationsTest extends JMXNotificationsTestCase
{
   public HessianNotificationsTest(String name)
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

   public void testNonSerializableNotifications() throws Exception
   {
      // Burlap protocol does not see if the Object does not implement Serializable,
      // it tries anyway to serialize it
   }
}
