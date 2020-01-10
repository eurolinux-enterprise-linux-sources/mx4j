/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.relation;

import java.util.ArrayList;
import java.util.List;
import javax.management.ObjectName;
import javax.management.relation.Role;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.4 $
 */
public class RoleTest extends TestCase
{
   private Role _role;

   public RoleTest(String name)
   {
      super(name);
   }

   protected void setUp() throws Exception
   {
      _role = new Role("test Role", new ArrayList());
   }

   protected void tearDown() throws Exception
   {
   }

   public void testGetRoleValue() throws Exception
   {
      List roleValues = _role.getRoleValue();
      assertNotNull(roleValues);
   }

   public void testGetRoleValue_ListHasElements() throws Exception
   {
      List values = new ArrayList();
      values.add(new ObjectName("domain:name=test"));
      Role role = new Role("Test Role", values);
      assertEquals(1, role.getRoleValue().size());
   }
}