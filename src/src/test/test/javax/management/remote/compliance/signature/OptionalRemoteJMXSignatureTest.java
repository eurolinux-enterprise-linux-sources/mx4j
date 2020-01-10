/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote.compliance.signature;

import java.io.Serializable;
import java.lang.reflect.Modifier;

import test.javax.management.compliance.signature.support.NotCompliantException;
import test.javax.management.compliance.signature.support.NotCompliantWarningException;
import test.javax.management.compliance.signature.support.SignatureVerifier;
import test.javax.management.remote.compliance.OptionalRemoteJMXComplianceTestCase;

/**
 * @version $Revision: 1.5 $
 */
public class OptionalRemoteJMXSignatureTest extends OptionalRemoteJMXComplianceTestCase
{
   public OptionalRemoteJMXSignatureTest(String s)
   {
      super(s);
   }

   protected boolean skipClassName(String className)
   {
      // For now the optional JSR 160 part is not implemented
      return true;
   }

   protected boolean skipClass(Class cls)
   {
      // Exclude implementation classes in javax.management.remote optional subpackages

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
      ClassLoader jmxriLoader = createOptionalRemoteJMXRIWithTestsClassLoader();
      ClassLoader mx4jLoader = createRemoteMX4JWithTestsClassLoader();

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
