/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.modelmbean;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import javax.management.Descriptor;
import javax.management.IntrospectionException;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.ModelMBeanAttributeInfo;

import test.MX4JTestCase;

/**
 * Test case of ModelMBeanAttributeInfo. It will try to verify an appropriate
 * behaviour in particular with respect to the descriptor values
 *
 * @version $Revision: 1.7 $
 * @see
 */

public class ModelMBeanAttributeInfoTest extends MX4JTestCase
{
   public static class BogusNIC
   {
      public String getMAC()
      {
         return null;
      }

      public void setMAC(int mac)
      {
      }
   }


   public ModelMBeanAttributeInfoTest(String s)
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
      // testcase for bug #794320
      // Test that only name and descriptorType are mandatory
      Descriptor descriptor = new DescriptorSupport(new String[]{"name", "descriptortype", "default"},
                                                    new String[]{"attribute1", "attribute", "default"});
      ModelMBeanAttributeInfo attribute
              = new ModelMBeanAttributeInfo("attribute1", "java.lang.String", "An attribute", true, true, false, descriptor);
      // in case of bug #794320 the descriptor is overrided
      assertEquals(attribute.getDescriptor().getFieldValue("default"), "default");
      assertNull(attribute.getDescriptor().getFieldValue("value"));
   }

   public void testBadCtor() throws Exception
   {
      try
      {
         Method macgetter = BogusNIC.class.getMethod("getMAC", new Class[0]);
         Method macsetter =
                 BogusNIC.class.getMethod("setMAC", new Class[]{int.class});
         ModelMBeanAttributeInfo attrinfo =
                 new ModelMBeanAttributeInfo("MAC",
                                             "MAC Address",
                                             macgetter,
                                             macsetter);
         fail("Expecting an IntrospectionException");
      }
      catch (IntrospectionException x)
      {
         assertTrue(true); // success;
      }
   }

   public void testValuelessAttribute() throws Exception
   {
      ModelMBeanAttributeInfo attrinfo =
              new ModelMBeanAttributeInfo("SerialNo",
                                          "NIC Card's serial number",
                                          null,
                                          null);
      attrinfo =
      new ModelMBeanAttributeInfo("SerialNo",
                                  "String",
                                  "NIC Card's serial number",
                                  false,
                                  false,
                                  false);
   }

   public void testCaseInsenstiveDescriptorType()
   {
      DescriptorSupport ds = new DescriptorSupport(new String[]{
         "name=PreferredWine",
         "descriptorType=Attribute",
         "value=Amarone",
         "default=Red"
      });
      ModelMBeanAttributeInfo attrinfo =
              new ModelMBeanAttributeInfo("PreferredWine",
                                          "String",
                                          "Wine of choice",
                                          true,
                                          false,
                                          false,
                                          ds);
   }

   public void testGetDescriptor()
   {
      DescriptorSupport defds =
              new DescriptorSupport(new String[]{
                 "name=PreferredWine",
                 "descriptorType=Attribute",
                 "displayName=PreferredWine"});
      DescriptorSupport ds = new DescriptorSupport(new String[]{
         "name=PreferredWine",
         "descriptorType=Attribute",
         "value=Amarone",
         "displayName=PreferredWine",
         "default=Red"
      });

      ModelMBeanAttributeInfo attrinfo =
              new ModelMBeanAttributeInfo("PreferredWine",
                                          "String",
                                          "Wine of choice",
                                          true,
                                          false,
                                          false);
      Descriptor d = attrinfo.getDescriptor();
      assertTrue("Expecting default descriptor", descriptorsEqual(d, defds));

      attrinfo =
      new ModelMBeanAttributeInfo("PreferredWine",
                                  "String",
                                  "Wine of choice",
                                  true,
                                  false,
                                  false,
                                  ds);
      d = attrinfo.getDescriptor();
      assertTrue("Expecting copy of ds", descriptorsEqual(d, ds));
   }

   private boolean descriptorsEqual(Descriptor done, Descriptor dtwo)
   {
      List cifields = Arrays.asList(new String[]{"descriptortype", "persistpolicy", "log"});
      String[] fields = done.getFieldNames();
      boolean result = done.getFields().length == dtwo.getFields().length;
      for (int i = 0; i < fields.length && result == true; i++)
      {
         String field = fields[i];
         Object vone = done.getFieldValue(field);
         Object vtwo = done.getFieldValue(field);
         if (vtwo == null)
         {
            result = false;
         }
         else if (cifields.contains(field))
         {
            if (!(vone instanceof String) || !(vtwo instanceof String))
            {
               result = false;
            }
            else
            {
               result = ((String)vone).compareToIgnoreCase((String)vtwo) == 0;
            }
         }
         else
         {
            vone.equals(vtwo);
         }
      }
      return result;
   }
}
