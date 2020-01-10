/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.compliance.serialization;

import java.io.Serializable;
import java.lang.reflect.Modifier;

import test.javax.management.compliance.JMXComplianceTestCase;
import test.javax.management.compliance.serialization.support.SerializationVerifier;

/**
 * @version $Revision: 1.4 $
 */
public class JMXSerializationTest extends JMXComplianceTestCase
{
   public JMXSerializationTest(String s)
   {
      super(s);
   }

   protected boolean skipClassName(String className)
   {
      // Skip some classes, not required for compliance
      if (className.equals("javax.management.MBeanServerPermissionCollection") ||
          className.equals("javax.management.loading.MLet") ||
          className.equals("javax.management.loading.PrivateMLet") ||
          className.equals("javax.management.timer.TimerAlarmClockNotification"))
         return true;
      return false;
   }

   protected boolean skipClass(Class cls)
   {
      if (cls.isInterface() || !Serializable.class.isAssignableFrom(cls) || Modifier.isAbstract(cls.getModifiers())) return true;
      return false;
   }

   protected void checkCompliance(String name) throws Exception
   {
      ClassLoader jmxriLoader = createJMXRIWithTestsClassLoader();
      ClassLoader mx4jLoader = createMX4JWithTestsClassLoader();

      SerializationVerifier verifier = new SerializationVerifier("test.javax.management.compliance.serialization.support.Instantiator", "test.javax.management.compliance.serialization.support.Comparator");
      verifier.verifySerialization(name, jmxriLoader, mx4jLoader);
   }
}
