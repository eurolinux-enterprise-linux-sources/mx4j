/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.modelmbean;

import java.lang.reflect.Method;
import javax.management.Descriptor;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.ModelMBeanOperationInfo;

import test.MX4JTestCase;

/**
 * Test case of ModelMBeanOperationInfo. It will try to verify an appropriate
 * behaviour in particular with respect to the descriptor values
 *
 * @version $Revision: 1.5 $
 * @see
 */

public class ModelMBeanOperationInfoTest extends MX4JTestCase
{
   public static class Surgeon
   {
      public boolean appendectomy()
      {
         return true;
      }

      public boolean tonsillectomy(int tonsils)
      {
         return true;
      }
   }

   public ModelMBeanOperationInfoTest(String s)
   {
      super(s);
   }

   public void setUp() throws Exception
   {
      super.setUp();
   }

   public void tearDown() throws Exception
   {
      super.tearDown();
   }

   public void testValidDescriptorFields() throws Exception
   {
      // Test that only name and descriptorType are mandatory
      Descriptor descriptor = new DescriptorSupport(new String[]{"name", "descriptortype", "role", "visibility"},
                                                    new String[]{"operation1", "operation", "operation", "1"});
      ModelMBeanOperationInfo operation
              = new ModelMBeanOperationInfo("operation1", "An operation", null, "java.lang.String", ModelMBeanOperationInfo.ACTION, descriptor);
      // in case the descriptor is not valid this should be overriden
      assertEquals(operation.getDescriptor().getFieldValue("visibility"), "1");
   }

   public void testAddDefaultDisplayName() throws Exception
   {
      Method op =
              ModelMBeanOperationInfoTest.Surgeon.class.getMethod("appendectomy",
                                                                  new Class[0]);
      String[] fields = {"name", "descriptorType", "role"};
      String[] values =
              {op.getName(), "operation", "operation"};
      DescriptorSupport ds = new DescriptorSupport(fields, values);
      ModelMBeanOperationInfo info =
              new ModelMBeanOperationInfo("Good Appendectomy", op, ds);
      Descriptor d = info.getDescriptor();
      String dispname = (String)d.getFieldValue("displayName");
      assertTrue("Unexpected displayName",
                 dispname.compareTo(op.getName()) == 0);
   }

   public void testRoleValidation() throws Exception
   {
      Method op =
              ModelMBeanOperationInfoTest.Surgeon.class.getMethod("appendectomy",
                                                                  new Class[0]);
      String[] fields = {"name", "descriptorType", "role", "displayName"};
      String[] values =
              {op.getName(), "operation", "operation", "appendectomy"};
      DescriptorSupport ds = new DescriptorSupport(fields, values);
      ModelMBeanOperationInfo info =
              new ModelMBeanOperationInfo("Good Appendectomy", op, ds);

      try
      {
         values =
         new String[]{
            op.getName(),
            "operation",
            "constructor",
            "appendectomy"};
         ds = new DescriptorSupport(fields, values);
         info = new ModelMBeanOperationInfo("Bad Appendectomy", op, ds);
         fail("Expecting RuntimeOperationsException");
      }
      catch (RuntimeOperationsException x)
      {
         assertTrue(true); // success
      }
   }

   public void testCaseInsensitiveDescriptorType()
   {
      DescriptorSupport ds = new DescriptorSupport(new String[]{
         "name=getWineList",
         "descriptorType=oPERATION",
         "displayName=Retrieve the list of available wines",
         "role=getter"
      });
      ModelMBeanOperationInfo attrinfo =
              new ModelMBeanOperationInfo("getWineList",
                                          "Retrieve the list of available wines",
                                          new MBeanParameterInfo[0],
                                          "String",
                                          MBeanOperationInfo.INFO,
                                          ds);
   }
}
