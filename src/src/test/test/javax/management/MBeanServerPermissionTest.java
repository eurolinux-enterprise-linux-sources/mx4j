/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import javax.management.MBeanServerPermission;

/**
 * @version $Revision: 1.9 $
 */
public class MBeanServerPermissionTest extends PermissionTestCase
{
   public MBeanServerPermissionTest(String s)
   {
      super(s);
   }

   public void testInvalid() throws Exception
   {
      try
      {
         new MBeanServerPermission(null);
         fail("Invalid MBeanServerPermission");
      }
      catch (NullPointerException ignored)
      {
      }

      try
      {
         new MBeanServerPermission("");
         fail("Invalid MBeanServerPermission");
      }
      catch (IllegalArgumentException ignored)
      {
      }

      try
      {
         new MBeanServerPermission(" ");
         fail("Invalid MBeanServerPermission");
      }
      catch (IllegalArgumentException ignored)
      {
      }

      try
      {
         new MBeanServerPermission(" , ");
         fail("Invalid MBeanServerPermission");
      }
      catch (IllegalArgumentException ignored)
      {
      }

      try
      {
         new MBeanServerPermission("invalid");
         fail("Invalid MBeanServerPermission");
      }
      catch (IllegalArgumentException ignored)
      {
      }

      try
      {
         new MBeanServerPermission("*", "invalid");
         fail("Invalid MBeanServerPermission");
      }
      catch (IllegalArgumentException ignored)
      {
      }
   }

   public void testValid() throws Exception
   {
      new MBeanServerPermission("*");
      new MBeanServerPermission("*", "");
      new MBeanServerPermission("createMBeanServer");
      new MBeanServerPermission("releaseMBeanServer, findMBeanServer");
      new MBeanServerPermission("newMBeanServer, *");
   }

   public void testShuffledAreEquals() throws Exception
   {
      MBeanServerPermission mbsp1 = new MBeanServerPermission("createMBeanServer, releaseMBeanServer");
      MBeanServerPermission mbsp2 = new MBeanServerPermission("releaseMBeanServer, createMBeanServer");

      shouldBeEqual(mbsp1, mbsp2);

      MBeanServerPermission mbsp3 = new MBeanServerPermission("*");
      MBeanServerPermission mbsp4 = new MBeanServerPermission("releaseMBeanServer, *");

      shouldBeEqual(mbsp3, mbsp4);
   }

   public void testCreateImpliesNew() throws Exception
   {
      shouldImply(new MBeanServerPermission("createMBeanServer"), new MBeanServerPermission("newMBeanServer"));
   }

   public void testImplies() throws Exception
   {
      shouldImply(new MBeanServerPermission("*"), new MBeanServerPermission("createMBeanServer"));
      shouldImply(new MBeanServerPermission("*"), new MBeanServerPermission("newMBeanServer"));
      shouldImply(new MBeanServerPermission("*"), new MBeanServerPermission("findMBeanServer"));
      shouldImply(new MBeanServerPermission("*"), new MBeanServerPermission("releaseMBeanServer"));
      shouldImply(new MBeanServerPermission("*"), new MBeanServerPermission("createMBeanServer, releaseMBeanServer"));
      shouldImply(new MBeanServerPermission("createMBeanServer, releaseMBeanServer"), new MBeanServerPermission("createMBeanServer"));
      shouldImply(new MBeanServerPermission("createMBeanServer, releaseMBeanServer"), new MBeanServerPermission("releaseMBeanServer"));
   }

   public void testNotImplies()
   {
      shouldNotImply(new MBeanServerPermission("createMBeanServer"), new MBeanServerPermission("releaseMBeanServer"));
      shouldNotImply(new MBeanServerPermission("createMBeanServer"), new MBeanServerPermission("newMBeanServer, releaseMBeanServer"));
   }

   public void testSerializationDeserialization() throws Exception
   {
      MBeanServerPermission permission = new MBeanServerPermission("newMBeanServer, *");

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(permission);
      oos.close();

      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      ObjectInputStream ois = new ObjectInputStream(bais);
      MBeanServerPermission deserialized = (MBeanServerPermission)ois.readObject();
      ois.close();

      if (!(deserialized.equals(permission))) fail("Serialization/Deserialization failed");
   }
}
