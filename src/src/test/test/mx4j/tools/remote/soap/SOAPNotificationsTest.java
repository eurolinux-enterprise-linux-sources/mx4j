/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.tools.remote.soap;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import javax.management.remote.JMXServiceURL;

import test.javax.management.remote.JMXNotificationsTestCase;

/**
 * @version $Revision: 1.5 $
 */
public class SOAPNotificationsTest extends JMXNotificationsTestCase
{
   public SOAPNotificationsTest(String s)
   {
      super(s);
   }

   public JMXServiceURL createJMXConnectorServerAddress() throws MalformedURLException
   {
      return new JMXServiceURL("soap", null, 8080, "/soap");
   }

   public Map getEnvironment()
   {
      return new HashMap();
   }

   public void testNonSerializableNotifications() throws Exception
   {
      // SOAP serialization is always performed,
      // it does not care the fact that the object does not implement Serializable
   }
}
