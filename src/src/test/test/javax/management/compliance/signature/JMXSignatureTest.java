/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.compliance.signature;

import java.io.Serializable;
import java.lang.reflect.Modifier;

import test.javax.management.compliance.JMXComplianceTestCase;
import test.javax.management.compliance.signature.support.NotCompliantException;
import test.javax.management.compliance.signature.support.NotCompliantWarningException;
import test.javax.management.compliance.signature.support.SignatureVerifier;

/**
 * Test that verifies that the signature of the classes in JMXRI are equal to MX4J classes.
 * It resembles a small TCK, for signatures of the JMX classes only.
 *
 * @version $Revision: 1.3 $
 */
public class JMXSignatureTest extends JMXComplianceTestCase
{
   public JMXSignatureTest(String s)
   {
      super(s);
   }

   protected boolean skipClassName(String className)
   {
      return "javax.management.MBeanServerPermissionCollection".equals(className);
   }

   protected boolean skipClass(Class cls)
   {
      // Exclude implementation classes in javax.management package
      // Do not exclude classes that are package private but serializable
      // like for example the QueryExp and ValueExp implementations (unless some exception)

      int modifiers = cls.getModifiers();
      boolean isPublic = Modifier.isPublic(modifiers);
      boolean isProtected = Modifier.isProtected(modifiers);
      boolean isPackage = !Modifier.isPrivate(modifiers) && !isProtected && !isPublic;
      boolean isSerializable = Serializable.class.isAssignableFrom(cls);

      if (isPublic || isProtected || (isPackage && isSerializable)) return false;
      return true;
   }

   protected void checkCompliance(String className) throws Exception
   {
      ClassLoader jmxriLoader = createJMXRIWithTestsClassLoader();
      ClassLoader mx4jLoader = createMX4JWithTestsClassLoader();

      SignatureVerifier verifier = new SignatureVerifier();

      try
      {
         verifier.verifySignature(className, jmxriLoader, mx4jLoader);
      }
      catch (NotCompliantException x)
      {
         fail(x.getMessage());
      }
      catch (NotCompliantWarningException x)
      {
         System.out.println("WARNING: " + x.getMessage());
      }
   }
}
