/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.modelmbean;

import java.lang.reflect.Constructor;
import javax.management.Descriptor;
import javax.management.MBeanParameterInfo;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.ModelMBeanConstructorInfo;

import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 * @version $Revision: 1.5 $
 */
public class ModelMBeanConstructorInfoTest extends TestCase
{
   public static class Bob
   {
      public Bob(String address)
      {
      }
   }

   public static void main(String[] args)
   {
      TestRunner.run(ModelMBeanConstructorInfoTest.class);
   }

   public ModelMBeanConstructorInfoTest(String name)
   {
      super(name);
   }

   public void testValid() throws Exception
   {
      Constructor bobctor =
              ModelMBeanConstructorInfoTest.Bob.class.getConstructor(new Class[]{String.class});
      String[] fields = {"name", "descriptorType", "displayName", "role"};
      String[] values =
              {bobctor.getName(), "operation", "bob maker", "constructor"};
      DescriptorSupport ds = new DescriptorSupport(fields, values);
      ModelMBeanConstructorInfo ctorinfo =
              new ModelMBeanConstructorInfo("BobBuilder", bobctor, ds);
   }

   public void testAddDefaultDisplayName() throws Exception
   {
      Constructor bobctor =
              ModelMBeanConstructorInfoTest.Bob.class.getConstructor(new Class[]{String.class});
      String[] fields = {"name", "descriptorType", "role"};
      String[] values = {bobctor.getName(), "operation", "constructor"};
      DescriptorSupport ds = new DescriptorSupport(fields, values);
      ModelMBeanConstructorInfo ctorinfo =
              new ModelMBeanConstructorInfo("BobBuilder", bobctor, ds);
      Descriptor d = ctorinfo.getDescriptor();
      String dispname = (String)d.getFieldValue("displayName");
      assertTrue("Unexpected displayName",
                 dispname.compareTo(bobctor.getName()) == 0);
   }

   public void testBadRole() throws Exception
   {
      try
      {
         Constructor bobctor =
                 ModelMBeanConstructorInfoTest.Bob.class.getConstructor(new Class[]{String.class});
         String[] fields =
                 {"name", "descriptorType", "displayName", "role"};
         String[] values =
                 {bobctor.getName(), "operation", "bob maker", "getter"};
         DescriptorSupport ds = new DescriptorSupport(fields, values);
         ModelMBeanConstructorInfo ctorinfo =
                 new ModelMBeanConstructorInfo("BobBuilder", bobctor, ds);
         fail("Expecting RuntimeOperationsException");
      }
      catch (RuntimeOperationsException x)
      {
         if (!(x.getTargetException() instanceof IllegalArgumentException))
         {
            fail("Target exception should be IllegalArgumentException");
         }
         assertTrue(true); // success
      }
   }

   public void testCaseInsensitiveDescriptorType()
   {
      DescriptorSupport ds =
              new DescriptorSupport(new String[]{
                 "descriptorType=OPERATION",
                 "role=constructor",
                 "name=BobBuilder",
                 "displayname=bob maker"});
      ModelMBeanConstructorInfo ctorinfo =
              new ModelMBeanConstructorInfo("BobBuilder",
                                            "Default Bob Constructor",
                                            new MBeanParameterInfo[0],
                                            ds);
   }

   protected void setUp()
   {
   }

   protected void tearDown()
   {
   }
}
