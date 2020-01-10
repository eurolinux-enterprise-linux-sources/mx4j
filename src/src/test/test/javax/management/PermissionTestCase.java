/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management;

import java.security.Permission;
import java.security.PermissionCollection;

import test.MX4JTestCase;

/**
 * @version $Revision: 1.3 $
 */
public class PermissionTestCase extends MX4JTestCase
{
   public PermissionTestCase(String s)
   {
      super(s);
   }

   protected void shouldBeEqual(Permission p1, Permission p2)
   {
      if (!p1.equals(p2)) fail("Permission " + p1 + " should be equal to Permission " + p2);
      if (p1.hashCode() != p2.hashCode()) fail("Permission " + p1 + " should have hashCode equal to Permission " + p2);
   }

   protected void shouldImply(Permission p1, Permission p2)
   {
      if (p1.equals(p2))
      {
         // Test identity
         if (!imply(p1, p2)) fail("Permission " + p1 + " should imply Permission " + p2);
         if (!imply(p2, p1)) fail("Permission " + p2 + " should imply Permission " + p1);
      }
      else
      {
         // Test antisymmetry
         if (!imply(p1, p2)) fail("Permission " + p1 + " should imply Permission " + p2);
         if (imply(p2, p1)) fail("Permission " + p2 + " should not imply Permission " + p1);
      }
   }

   protected void shouldNotImply(Permission p1, Permission p2)
   {
      if (p1.equals(p2)) fail("Permissions cannot be equal");
      if (imply(p1, p2)) fail("Permission " + p1 + " should not imply Permission " + p2);
      if (imply(p2, p1)) fail("Permission " + p2 + " should not imply Permission " + p1);
   }

   protected boolean imply(Permission p1, Permission p2)
   {
      PermissionCollection pc = p1.newPermissionCollection();
      if (pc == null)
      {
         // No PermissionCollection provided, go directly to the Permission
         return p1.implies(p2);
      }
      else
      {
         return pc.implies(p2);
      }
   }
}
