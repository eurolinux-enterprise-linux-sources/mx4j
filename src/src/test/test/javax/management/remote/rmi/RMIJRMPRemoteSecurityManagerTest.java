/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote.rmi;

import java.net.MalformedURLException;
import java.security.Policy;
import javax.management.remote.JMXServiceURL;

import test.javax.management.remote.RemoteSecurityManagerTestCase;

/**
 * @version $Revision: 1.3 $
 */
public class RMIJRMPRemoteSecurityManagerTest extends RemoteSecurityManagerTestCase
{
   static
   {
      // For the way JUnit works, we have one JVM per test class
      Policy.setPolicy(new RMIJRMPRemoteModifiablePolicy());
      System.setSecurityManager(new SecurityManager());
   }

   public RMIJRMPRemoteSecurityManagerTest(String s)
   {
      super(s);
   }

   protected JMXServiceURL createJMXConnectorServerAddress() throws MalformedURLException
   {
      return new JMXServiceURL("rmi", "localhost", 7777);
   }

   public static class RMIJRMPRemoteModifiablePolicy extends RemoteModifiablePolicy
   {
      public boolean isServerSide()
      {
         if (!isSeparateClientServerPermissions()) return true;
         String name = Thread.currentThread().getName();
         if (name.indexOf("RMI") >= 0) return true;
         return false;
      }
   }
}
