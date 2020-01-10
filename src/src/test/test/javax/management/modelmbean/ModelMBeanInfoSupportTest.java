/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.modelmbean;

import java.util.Arrays;
import javax.management.Descriptor;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanConstructorInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanNotificationInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.7 $
 * @see
 */

public class ModelMBeanInfoSupportTest extends TestCase
{

   public ModelMBeanInfoSupportTest(String s)
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

   public void testGetDescriptor() throws Exception
   {
      ModelMBeanAttributeInfo[] attributes = new ModelMBeanAttributeInfo[2];
      attributes[0] = new ModelMBeanAttributeInfo("test", "java.lang.String", "A description", true, true, false);
      attributes[1] = new ModelMBeanAttributeInfo("test2", "java.lang.String", "A description", true, true, false);
      // testcase for bug #700905
      ModelMBeanInfoSupport support = new ModelMBeanInfoSupport("somepackage.someclass", "Test case", attributes, null, null, null);
      // this worked ok
      Descriptor descriptor = support.getDescriptor("test", "attribute");
      assertNotNull(descriptor);
      assertEquals("test", descriptor.getFieldValue("name"));
      // this didn't
      descriptor = support.getDescriptor("test", null);
      assertNotNull(descriptor);
      assertEquals("test", descriptor.getFieldValue("name"));
   }

   public void testCaseInsensitiveDescriptorType()
   {
      DescriptorSupport ds =
              new DescriptorSupport(new String[]{
                 "name=TestMetadata",
                 "descriptorType=mbEAN",
                 "displayname=Test Metadata"
              });

      ModelMBeanInfoSupport info =
              new ModelMBeanInfoSupport("TestMetadata",
                                        "An empty model mbean info instance",
                                        new ModelMBeanAttributeInfo[0],
                                        new ModelMBeanConstructorInfo[0],
                                        new ModelMBeanOperationInfo[0],
                                        new ModelMBeanNotificationInfo[0],
                                        ds);
   }

   public void testGetMBeanDescriptorDefault() throws Exception
   {
      ModelMBeanInfoSupport info =
              new ModelMBeanInfoSupport("TestMetadata",
                                        "An empty model mbean info instance",
                                        new ModelMBeanAttributeInfo[0],
                                        new ModelMBeanConstructorInfo[0],
                                        new ModelMBeanOperationInfo[0],
                                        new ModelMBeanNotificationInfo[0]);
      Descriptor dd = info.getMBeanDescriptor();
      assertFalse("default descriptor is null", dd == null);
      assertTrue("Expecting 7 fields", dd.getFieldNames().length == 7);
      String[] deffields =
              {
                 "name",
                 "descriptorType",
                 "displayName",
                 "persistPolicy",
                 "log",
                 "export",
                 "visibility"};
      String[] fields = dd.getFieldNames();
      assertTrue("Expected field names not present",
                 Arrays.asList(fields).containsAll(Arrays.asList(deffields)));
      assertTrue("Unexpected name",
                 ((String)dd.getFieldValue("name")).compareTo("TestMetadata") == 0);
      assertTrue("Unexpected descriptorType",
                 ((String)dd.getFieldValue("descriptortype")).compareToIgnoreCase("mbean")
                 == 0);
      assertTrue("Unexpected displayName: " + dd.getFieldValue("displayname"),
                 ((String)dd.getFieldValue("displayname")).compareTo("TestMetadata")
                 == 0);
      assertTrue("Unexpected persistpolicy",
                 ((String)dd.getFieldValue("persistpolicy")).compareToIgnoreCase("never")
                 == 0);
      assertTrue("Unexpected log",
                 ((String)dd.getFieldValue("log")).compareToIgnoreCase("F") == 0);
      assertTrue("Unexpected export",
                 ((String)dd.getFieldValue("export")).compareTo("F") == 0);
      assertTrue("Unexpected visibility",
                 ((String)dd.getFieldValue("visibility")).compareTo("1") == 0);

      info =
      new ModelMBeanInfoSupport("TestMetadata",
                                "An empty model mbean info instance",
                                new ModelMBeanAttributeInfo[0],
                                new ModelMBeanConstructorInfo[0],
                                new ModelMBeanOperationInfo[0],
                                new ModelMBeanNotificationInfo[0],
                                null);
      dd = info.getMBeanDescriptor();
      assertFalse("default descriptor is null", dd == null);
      assertTrue("Expecting 7 fields", dd.getFieldNames().length == 7);
      assertTrue("Expected field names not present",
                 Arrays.asList(fields).containsAll(Arrays.asList(deffields)));
      assertTrue("Unexpected name",
                 ((String)dd.getFieldValue("name")).compareTo("TestMetadata") == 0);
      assertTrue("Unexpected descriptorType",
                 ((String)dd.getFieldValue("descriptortype")).compareToIgnoreCase("mbean")
                 == 0);
      assertTrue("Unexpected displayName: " + dd.getFieldValue("displayname"),
                 ((String)dd.getFieldValue("displayname")).compareTo("TestMetadata")
                 == 0);
      assertTrue("Unexpected persistpolicy",
                 ((String)dd.getFieldValue("persistpolicy")).compareToIgnoreCase("never")
                 == 0);
      assertTrue("Unexpected log",
                 ((String)dd.getFieldValue("log")).compareToIgnoreCase("F") == 0);
      assertTrue("Unexpected export",
                 ((String)dd.getFieldValue("export")).compareTo("F") == 0);
      assertTrue("Unexpected visibility",
                 ((String)dd.getFieldValue("visibility")).compareTo("1") == 0);

   }

   public void testGetMBeanDescriptorAdditionalValues() throws Exception
   {
      String[] fields =
              {
                 "name",
                 "descriptortype",
                 "displayname",
                 "targetObject",
                 "export",
                 "defaultValue"};
      Object[] values =
              {
                 "TestMetadata",
                 "MBEAN",
                 "JMX Metadata for testing purposes",
                 new Double(0),
                 "test metadata",
                 new Double(99.999)};
      DescriptorSupport ds = new DescriptorSupport(fields, values);
      ModelMBeanInfoSupport info =
              new ModelMBeanInfoSupport("TestMetadata",
                                        "An empty model mbean info instance",
                                        new ModelMBeanAttributeInfo[0],
                                        new ModelMBeanConstructorInfo[0],
                                        new ModelMBeanOperationInfo[0],
                                        new ModelMBeanNotificationInfo[0],
                                        ds);
      Descriptor d = info.getMBeanDescriptor();
      assertFalse("descriptor is null", d == null);
      assertTrue("Expecting 9 fields", d.getFieldNames().length == 9);
      String[] deffields =
              {
                 "name",
                 "descriptortype",
                 "displayname",
                 "persistpolicy",
                 "log",
                 "export",
                 "visibility"};
      String[] infofields = d.getFieldNames();
      assertTrue("Expected field names not present",
                 Arrays.asList(infofields).containsAll(Arrays.asList(deffields)));
      assertTrue("Unexpected name",
                 ((String)d.getFieldValue("name")).compareTo((String)ds.getFieldValue("name"))
                 == 0);
      assertTrue("Unexpected descriptorType",
                 ((String)d.getFieldValue("descriptortype")).compareToIgnoreCase((String)ds.getFieldValue("descriptortype"))
                 == 0);
      assertTrue("Unexpected displayName: " + d.getFieldValue("displayname"),
                 ((String)d.getFieldValue("displayname")).compareTo((String)ds.getFieldValue("displayname"))
                 == 0);
      assertTrue("Unexpected targetObject: " + d.getFieldValue("targetObject"),
                 ((Double)d.getFieldValue("targetObject")).equals(new Double(0.0)));
      assertTrue("Unexpected persistpolicy",
                 ((String)d.getFieldValue("persistpolicy")).compareToIgnoreCase("never")
                 == 0);
      assertTrue("Unexpected log",
                 ((String)d.getFieldValue("log")).compareToIgnoreCase("F") == 0);
      assertTrue("Unexpected export",
                 ((String)d.getFieldValue("export")).compareTo((String)ds.getFieldValue("export"))
                 == 0);
      assertTrue("Unexpected defaultValue",
                 ((Double)d.getFieldValue("defaultValue")).equals(new Double(99.999)));
      assertTrue("Unexpected visibility",
                 ((String)d.getFieldValue("visibility")).compareTo("1") == 0);
   }

   public void testGetMBeanDescriptorCustom() throws Exception
   {
      String[] fields =
              {
                 "name",
                 "descriptortype",
                 "displayname",
                 "persistpolicy",
                 "log",
                 "export",
                 "visibility"};
      Object[] values =
              {
                 "TestMetadata",
                 "MBEAN",
                 "JMX Metadata for testing purposes",
                 "onUPDATE",
                 "T",
                 "test metadata",
                 "4"};
      DescriptorSupport ds = new DescriptorSupport(fields, values);
      ModelMBeanInfoSupport info =
              new ModelMBeanInfoSupport("TestMetadata",
                                        "An empty model mbean info instance",
                                        new ModelMBeanAttributeInfo[0],
                                        new ModelMBeanConstructorInfo[0],
                                        new ModelMBeanOperationInfo[0],
                                        new ModelMBeanNotificationInfo[0],
                                        ds);
      Descriptor d = info.getMBeanDescriptor();
      assertFalse("descriptor is null", d == null);
      assertTrue("Expecting 7 fields", d.getFieldNames().length == 7);
      String[] deffields =
              {
                 "name",
                 "descriptortype",
                 "displayname",
                 "persistpolicy",
                 "log",
                 "export",
                 "visibility"};
      String[] infofields = d.getFieldNames();
      assertTrue("Expected field names not present",
                 Arrays.asList(infofields).containsAll(Arrays.asList(deffields)));
      assertTrue("Unexpected name",
                 ((String)d.getFieldValue("name")).compareTo((String)ds.getFieldValue("name"))
                 == 0);
      assertTrue("Unexpected descriptorType",
                 ((String)d.getFieldValue("descriptortype")).compareToIgnoreCase((String)ds.getFieldValue("descriptortype"))
                 == 0);
      assertTrue("Unexpected displayName: " + d.getFieldValue("displayname"),
                 ((String)d.getFieldValue("displayname")).compareTo((String)ds.getFieldValue("displayname"))
                 == 0);
      assertTrue("Unexpected persistpolicy",
                 (
                    (String)d.getFieldValue("persistpolicy")).compareToIgnoreCase(((String)d.getFieldValue("persistpolicy"))) == 0);
      assertTrue("Unexpected log",
                 ((String)d.getFieldValue("log")).compareToIgnoreCase(((String)d.getFieldValue("log"))) == 0);
      assertTrue("Unexpected export",
                 ((String)d.getFieldValue("export")).compareTo((String)ds.getFieldValue("export"))
                 == 0);
      assertTrue("Unexpected visibility",
                 ((String)d.getFieldValue("visibility")).compareTo(((String)d.getFieldValue("visibility"))) == 0);
   }
}
