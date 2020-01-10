/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.server;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javax.management.MBeanPermission;

import test.javax.management.PermissionTestCase;

/**
 * @version $Revision: 1.19 $
 */
public class MBeanPermissionTest extends PermissionTestCase
{
   public MBeanPermissionTest(String s)
   {
      super(s);
   }

   public void testInvalidMBeanPermissionBecauseInvalidName() throws Exception
   {
      try
      {
         new MBeanPermission(null, null);
         fail("Invalid MBeanPermission");
      }
      catch (IllegalArgumentException ignored)
      {
      }

      try
      {
         new MBeanPermission("", null);
         fail("Invalid MBeanPermission");
      }
      catch (IllegalArgumentException ignored)
      {
      }

      try
      {
         new MBeanPermission(" ", null);
         fail("Invalid MBeanPermission");
      }
      catch (IllegalArgumentException ignored)
      {
      }

      try
      {
         new MBeanPermission(" ", "*");
         fail("Invalid MBeanPermission");
      }
      catch (IllegalArgumentException ignored)
      {
      }

      try
      {
         new MBeanPermission("", "*");
         fail("Invalid MBeanPermission");
      }
      catch (IllegalArgumentException ignored)
      {
      }

      // Invalid ObjectName
      try
      {
         new MBeanPermission("[d?k=v]", "*");
         fail("Invalid MBeanPermission");
      }
      catch (IllegalArgumentException ignored)
      {
      }

      try
      {
         new MBeanPermission("[*]", "*");
         fail("Invalid MBeanPermission");
      }
      catch (IllegalArgumentException ignored)
      {
      }
   }

   public void testInvalidMBeanPermissionBecauseInvalidActions() throws Exception
   {
      try
      {
         new MBeanPermission("*", null);
         fail("Invalid MBeanPermission");
      }
      catch (IllegalArgumentException ignored)
      {
      }

      try
      {
         new MBeanPermission("com.*[d:k=v]", null);
         fail("Invalid MBeanPermission");
      }
      catch (IllegalArgumentException ignored)
      {
      }

      try
      {
         new MBeanPermission("com.*[d:k=v]", null);
         fail("Invalid MBeanPermission");
      }
      catch (IllegalArgumentException ignored)
      {
      }

      try
      {
         new MBeanPermission("com.*[d:k=v]", "");
         fail("Invalid MBeanPermission");
      }
      catch (IllegalArgumentException ignored)
      {
      }

      try
      {
         new MBeanPermission("com.*[d:k=v]", " ");
         fail("Invalid MBeanPermission");
      }
      catch (IllegalArgumentException ignored)
      {
      }

      try
      {
         new MBeanPermission("com.*[d:k=v]", " , ");
         fail("Invalid MBeanPermission");
      }
      catch (IllegalArgumentException ignored)
      {
      }
   }

   public void testValidMBeanPermissionZero() throws Exception
   {
      MBeanPermission mbp1 = new MBeanPermission("*", "*");
      MBeanPermission mbp2 = new MBeanPermission(" *", "*");
      MBeanPermission mbp3 = new MBeanPermission(" *", "* ");
      MBeanPermission mbp4 = new MBeanPermission("*[*:*]", "*");
      MBeanPermission mbp5 = new MBeanPermission("*#*[*:*]", "*");

      // Test equality
      shouldBeEqual(mbp1, mbp2);
      shouldBeEqual(mbp2, mbp3);
      shouldBeEqual(mbp3, mbp4);
      shouldBeEqual(mbp4, mbp5);
   }

   public void testValidMBeanPermissionOne() throws Exception
   {
      MBeanPermission mbp1 = new MBeanPermission("[d:k=v]", "invoke");
      MBeanPermission mbp2 = new MBeanPermission("*[d:k=v]", "invoke");
      MBeanPermission mbp3 = new MBeanPermission("*#*[d:k=v]", "invoke");

      shouldBeEqual(mbp1, mbp2);
      shouldBeEqual(mbp2, mbp3);
   }

   public void testValidMBeanPermissionTwo() throws Exception
   {
      MBeanPermission mbp1 = new MBeanPermission("com.package.MyClass", "getAttribute");
      MBeanPermission mbp2 = new MBeanPermission("com.package.MyClass#*", "getAttribute");
      MBeanPermission mbp3 = new MBeanPermission("com.package.MyClass#*[*:*]", "getAttribute");

      shouldBeEqual(mbp1, mbp2);
      shouldBeEqual(mbp2, mbp3);
   }

   public void testValidMBeanPermissionThree() throws Exception
   {
      MBeanPermission mbp1 = new MBeanPermission("com.package.MyClass[d:k=v]", "setAttribute");
      MBeanPermission mbp2 = new MBeanPermission("com.package.MyClass#*[d:k=v]", "setAttribute");

      shouldBeEqual(mbp1, mbp2);
   }

   public void testValidMBeanPermissionFour() throws Exception
   {
      MBeanPermission mbp1 = new MBeanPermission("#Name[d:k=v]", "isInstanceOf");
      MBeanPermission mbp2 = new MBeanPermission("*#Name[d:k=v]", "isInstanceOf");

      shouldBeEqual(mbp1, mbp2);
   }

   public void testValidMBeanPermissionFive() throws Exception
   {
      MBeanPermission mbp1 = new MBeanPermission("-", "getMBeanInfo");
      MBeanPermission mbp2 = new MBeanPermission("-#*[*:*]", "getMBeanInfo");

      shouldBeEqual(mbp1, mbp2);
   }

   public void testValidMBeanPermissionSix() throws Exception
   {
      MBeanPermission mbp1 = new MBeanPermission("-#-", "getMBeanInfo");
      MBeanPermission mbp2 = new MBeanPermission("-#-[*:*]", "getMBeanInfo");

      shouldBeEqual(mbp1, mbp2);
   }

   public void testValidMBeanPermissionSeven() throws Exception
   {
      MBeanPermission mbp1 = new MBeanPermission("-#-[-]", "getMBeanInfo");
   }

   public void testValidMBeanPermissionEight() throws Exception
   {
      MBeanPermission mbp1 = new MBeanPermission("com.package.MyClass#Attr[d:k=v]", "*");
      MBeanPermission mbp2 = new MBeanPermission("com.package.MyClass#Attr[d:k=v]", "invoke,*");

      shouldBeEqual(mbp1, mbp2);
   }

   public void testValidMBeanPermissionNine() throws Exception
   {
      new MBeanPermission("*[d:k=v]", "addNotificationListener, *");
      new MBeanPermission("*[d:k1=v1,k2=v2]", "removeNotificationListener, *");
      new MBeanPermission("mx4j.*", "addNotificationListener, removeNotificationListener");
      new MBeanPermission("mx4j.Foo#*", "addNotificationListener, removeNotificationListener");
      new MBeanPermission("mx4j.Foo$Inner#*", "addNotificationListener, removeNotificationListener");
      new MBeanPermission("mx4j.Foo#*[*:*]", "addNotificationListener, removeNotificationListener");
      new MBeanPermission("mx4j.Foo$Inner#*[*:*]", "addNotificationListener, removeNotificationListener");
      new MBeanPermission("mx4j.Foo#Bar[d:k=v]", "addNotificationListener, removeNotificationListener");
      new MBeanPermission("mx4j.Foo$Inner#Bar[d:k=v]", "addNotificationListener, removeNotificationListener");
   }

   public void testMBeanPermissionWildcardAlwaysImplies() throws Exception
   {
      MBeanPermission wild = new MBeanPermission("*", "*");
      shouldImply(wild, wild);
      shouldImply(new MBeanPermission("*[*:*]", "*"), wild);
      shouldImply(wild, new MBeanPermission("*[*:*]", "*"));
      shouldImply(new MBeanPermission("*#*[*:*]", "*"), wild);
      shouldImply(wild, new MBeanPermission("*#*[*:*]", "*"));

      implies(wild, new MBeanPermission("com.*", "*"));
      shouldImply(wild, new MBeanPermission("com.package.MyClass", "*"));
      shouldImply(wild, new MBeanPermission("com.package.MyClass#Attribute", "*"));
      shouldImply(wild, new MBeanPermission("com.package.MyClass#Attribute[d:k=v]", "*"));
      shouldImply(wild, new MBeanPermission("com.package.MyClass#Attribute[d:k=v]", "getObjectInstance"));
      shouldImply(wild, new MBeanPermission("-#-[-]", "*"));
   }

   public void testMBeanPermissionNilCardIsAlwaysImplied() throws Exception
   {
      String action = "queryNames";
      MBeanPermission nil = new MBeanPermission("-#-[-]", action);

      shouldImply(nil, nil);
      shouldImply(new MBeanPermission("com.package.MyClass#Attr[d:k=v]", action), nil);
   }

   public void testMBeanPermissionQueryMBeansImpliesQueryNames() throws Exception
   {
      shouldImply(new MBeanPermission("*", "queryMBeans"), new MBeanPermission("*", "queryNames"));
      shouldNotImply(new MBeanPermission("*", "queryMBeans"), new MBeanPermission("*", "queryNames, invoke"));
   }

   public void testMBeanPermissionShuffledActionsAreEquals() throws Exception
   {
      MBeanPermission mbp1 = new MBeanPermission("*", "addNotificationListener, removeNotificationListener");
      MBeanPermission mbp2 = new MBeanPermission("*", "removeNotificationListener, addNotificationListener");

      shouldBeEqual(mbp1, mbp2);
   }

   public void testMBeanPermissionImpliesOne() throws Exception
   {
      MBeanPermission implied = new MBeanPermission("mx4j.Foo#Bar[domain:k1=v1,k2=v2]", "invoke");
      MBeanPermission all = new MBeanPermission("*", "*");

      shouldImply(implied, implied);

      shouldImply(new MBeanPermission("mx4j.Foo#Bar[domain:k2=v2,k1=v1]", "invoke"), implied);
      shouldImply(new MBeanPermission("mx4j.Foo#Bar[domain:k1=v1,k2=v2]", "invoke,getAttribute"), implied);
      shouldImply(new MBeanPermission("mx4j.Foo#Bar[domain:k1=v1,k2=v2]", "setAttribute,invoke"), implied);
      shouldImply(new MBeanPermission("mx4j.Foo#Bar[domain:k1=v1,k2=v2]", "invoke,*"), implied);
      shouldImply(new MBeanPermission("mx4j.Foo#Bar[domain:k1=v1,k2=v2]", "*"), implied);

      shouldImply(new MBeanPermission("mx4j.Foo#Bar[domain:k1=v1,*]", "invoke"), implied);
      shouldImply(new MBeanPermission("mx4j.Foo#Bar[domain:k2=v2,*]", "invoke"), implied);
      shouldImply(new MBeanPermission("mx4j.Foo#Bar[domain:*]", "invoke"), implied);
      shouldImply(new MBeanPermission("mx4j.Foo#Bar[dom?in:k1=v1,k2=v2]", "invoke"), implied);
      shouldImply(new MBeanPermission("mx4j.Foo#Bar[do*in:k1=v1,k2=v2]", "invoke"), implied);
      shouldImply(new MBeanPermission("mx4j.Foo#Bar[*:k1=v1,k2=v2]", "invoke"), implied);
      shouldImply(new MBeanPermission("mx4j.Foo#Bar[*:*]", "invoke"), implied);
      shouldImply(new MBeanPermission("mx4j.Foo#Bar", "invoke"), implied);

      shouldImply(new MBeanPermission("mx4j.Foo#Bar*[domain:k1=v1,k2=v2]", "invoke"), implied);
      shouldImply(new MBeanPermission("mx4j.Foo#B*[domain:k1=v1,k2=v2]", "invoke"), implied);
      shouldImply(new MBeanPermission("mx4j.Foo#*[domain:k1=v1,k2=v2]", "invoke"), implied);
      shouldImply(new MBeanPermission("mx4j.Foo[domain:k1=v1,k2=v2]", "invoke"), implied);
      shouldImply(new MBeanPermission("mx4j.Foo#Bar*", "invoke"), implied);
      shouldImply(new MBeanPermission("mx4j.Foo#B*", "invoke"), implied);
      shouldImply(new MBeanPermission("mx4j.Foo#*", "invoke"), implied);
      shouldImply(new MBeanPermission("mx4j.Foo", "invoke"), implied);

      shouldImply(new MBeanPermission("mx4j.Foo*#Bar[domain:k1=v1,k2=v2]", "invoke"), implied);
      shouldImply(new MBeanPermission("mx4j.*#Bar[domain:k1=v1,k2=v2]", "invoke"), implied);
      shouldImply(new MBeanPermission("*#Bar[domain:k1=v1,k2=v2]", "invoke"), implied);
      shouldImply(new MBeanPermission("#Bar[domain:k1=v1,k2=v2]", "invoke"), implied);
      shouldImply(new MBeanPermission("mx4j.*#Bar", "invoke"), implied);
      shouldImply(new MBeanPermission("*#Bar", "invoke"), implied);
      shouldImply(new MBeanPermission("#Bar", "invoke"), implied);
      shouldImply(new MBeanPermission("mx4j.*", "invoke"), implied);
      shouldImply(new MBeanPermission("*", "invoke"), implied);

      shouldImply(new MBeanPermission("mx4j.Foo*#*[domain:k1=v1,k2=v2]", "invoke"), implied);
      shouldImply(new MBeanPermission("[domain:k1=v1,k2=v2]", "invoke"), implied);

      shouldImply(all, implied);

      shouldImply(new MBeanPermission("[]", "*"), all);
      shouldImply(implied, new MBeanPermission(null, null, null, "invoke"));
   }

   public void testMBeanPermissionNotImpliesOne() throws Exception
   {
      MBeanPermission notImplied = new MBeanPermission("mx4j.Foo#Bar[d:k=v]", "invoke, getObjectInstance");

      // Different actions
      shouldNotImply(new MBeanPermission("mx4j.Foo#Bar[d:k=v]", "getAttribute"), notImplied);
      shouldNotImply(new MBeanPermission("mx4j.Foo#Bar[d:k=v]", "getAttribute,setAttribute"), notImplied);
      shouldNotImply(new MBeanPermission("mx4j.Foo#Bar[d:k=v]", "invoke,setAttribute"), notImplied);
      shouldNotImply(new MBeanPermission("mx4j.Foo#Bar[d:k=v]", "setAttribute,getObjectInstance"), notImplied);
      shouldNotImply(new MBeanPermission("mx4j.Foo#Bar[d:k=v]", "getObjectInstance,getAttribute"), notImplied);

      // Different objectname
      shouldNotImply(new MBeanPermission("mx4j.Foo#Bar[d:k=v1]", "invoke, getObjectInstance"), notImplied);
      shouldNotImply(new MBeanPermission("mx4j.Foo#Bar[d:k=v,k1=v1]", "invoke, getObjectInstance"), notImplied);
      shouldNotImply(new MBeanPermission("mx4j.Foo#Bar[:k=v]", "invoke, getObjectInstance"), notImplied);

      // Different attribute
      shouldNotImply(new MBeanPermission("mx4j.Foo#Baz[d:k=v]", "invoke, getObjectInstance"), notImplied);

      // Different class
      shouldNotImply(new MBeanPermission("mx4j.Foo.Baz#Bar[d:k=v]", "invoke, getObjectInstance"), notImplied);
      shouldNotImply(new MBeanPermission("mx4j.Foo$Inner#Bar[d:k=v]", "invoke, getObjectInstance"), notImplied);
      shouldNotImply(new MBeanPermission("dummy.Foo#Bar[d:k=v]", "invoke, getObjectInstance"), notImplied);
   }

   public void testMBeanPermissionSerialization() throws Exception
   {
      MBeanPermission permission = new MBeanPermission("mx4j.Foo#Bar[domain:key=value]", "invoke");

      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(permission);
      oos.close();

      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      ObjectInputStream ois = new ObjectInputStream(bais);
      MBeanPermission deserialized = (MBeanPermission)ois.readObject();
      ois.close();

      if (!(deserialized.equals(permission))) fail("Serialization/Deserialization failed");
   }

   private void implies(MBeanPermission p1, MBeanPermission p2)
   {
      if (!imply(p1, p2)) fail("Permission " + p1 + " should imply Permission " + p2);
   }
}
