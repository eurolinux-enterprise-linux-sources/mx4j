/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote.compliance.serialization;

import java.io.Serializable;
import java.lang.reflect.Modifier;

import test.javax.management.compliance.serialization.support.SerializationVerifier;
import test.javax.management.remote.compliance.RemoteJMXComplianceTestCase;

/**
 * @version $Revision: 1.4 $
 */
public class RemoteJMXSerializationTest extends RemoteJMXComplianceTestCase
{
   public RemoteJMXSerializationTest(String s)
   {
      super(s);
   }

   protected boolean skipClassName(String className)
   {
      boolean isStub = className.endsWith("_Stub");
      return isStub;
   }

   protected boolean skipClass(Class cls)
   {
      if (cls.isInterface() || !Serializable.class.isAssignableFrom(cls) || Modifier.isAbstract(cls.getModifiers())) return true;
      return false;
   }

   protected void checkCompliance(String className) throws Exception
   {
      ClassLoader jmxriLoader = createRemoteJMXRIWithTestsClassLoader();
      ClassLoader mx4jLoader = createRemoteMX4JWithTestsClassLoader();

      SerializationVerifier verifier = new SerializationVerifier("test.javax.management.remote.compliance.serialization.support.RemoteInstantiator", "test.javax.management.remote.compliance.serialization.support.RemoteComparator");
      verifier.verifySerialization(className, jmxriLoader, mx4jLoader);
   }
}
