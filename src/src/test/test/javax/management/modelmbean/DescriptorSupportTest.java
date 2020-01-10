/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.modelmbean;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.management.Descriptor;
import javax.management.RuntimeOperationsException;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.XMLParseException;

import test.MX4JTestCase;

/**
 * @version $Revision: 1.19 $
 */

public class DescriptorSupportTest extends MX4JTestCase
{
   public DescriptorSupportTest(String s)
   {
      super(s);
   }

   public void testValid() throws Exception
   {
      // Create a valid Descriptor object
      String[] attributes = {"name=mytest", "descriptorType=MBean", "role=constructor"};
      new DescriptorSupport(attributes);

      // Try a different constructor
      String[] names = {"name", "descriptorType", "role", "persistPolicy", "persistPeriod"};
      String[] values = {"mytest", "MBean", "constructor", "Never", "1"};
      new DescriptorSupport(names, values);

      // Create a valid Descriptor object. Check that persistPolicy's value is not case sensitive
      names = new String[]{"name", "descriptorType", "persistPolicy"};
      values = new String[]{"mytest", "MBean", "never"};

      new DescriptorSupport(names, values);
   }

   public void testInvalid() throws Exception
   {
      // Create an invalid Descriptor object. Check persist policy
      String[] names = {"name", "descriptorType", "role", "persistPolicy"};
      String[] values = {"mytest", "MBean", "constructor", "Something"};

      try
      {
         new DescriptorSupport(names, values);
         fail("Descriptor support object created with invalid attributes");
      }
      catch (RuntimeOperationsException ex)
      {
      }

      // Create an invalid Descriptor object. Check persistPeriod
      // Persist period should be bigger or equal than -1
      names = new String[]{"name", "descriptorType", "persistPolicy", "persistPeriod"};
      values = new String[]{"mytest", "MBean", "Never", "-2"};

      try
      {
         new DescriptorSupport(names, values);
         fail("Descriptor support object created with invalid persistPeriod");
      }
      catch (RuntimeOperationsException ex)
      {
      }

      // Create an invalid Descriptor object. Check visiblity
      // visibility should be between 1 and 4
      names = new String[]{"name", "descriptorType", "visibility"};
      values = new String[]{"mytest", "MBean", "0"};

      try
      {
         new DescriptorSupport(names, values);
         fail("Descriptor support object created with invalid visiblity");
      }
      catch (RuntimeOperationsException ex)
      {
      }

      // Create an invalid Descriptor object. Check visiblity
      // visibility should be between 1 and 4
      names = new String[]{"name", "descriptorType", "visibility"};
      values = new String[]{"mytest", "MBean", "5"};

      try
      {
         new DescriptorSupport(names, values);
         fail("Descriptor support object created with invalid visiblity");
      }
      catch (RuntimeOperationsException ex)
      {
      }
   }

   public void testIsValid() throws Exception
   {
      // Test for bug #686306
      String[] names = {"name", "descriptorType", "persistPolicy", "persistPeriod"};
      String[] values = {"test", "mbean", "AlwaYs", "-1"};

      DescriptorSupport ds = new DescriptorSupport(names, values);

      assertTrue(ds.isValid());
   }

   public void testSeverityField() throws Exception
   {
      // Test for bug #744423 and #775742
      String[] names = {"name", "descriptorType", "severity"};
      String[] values = {"test", "mbean", "0"};

      DescriptorSupport ds = new DescriptorSupport(names, values);
      assertTrue(ds.isValid());

      names = new String[]{"name", "descriptorType", "severity"};
      values = new String[]{"test", "mbean", "6"};

      ds = new DescriptorSupport(names, values);
      assertTrue(ds.isValid());
   }

   public void testCaseInsensitiveFieldNames() throws Exception
   {
      String[] fields = {"descriptorType", "myField"};
      Object[] values = {"MBean", "top secret"};
      DescriptorSupport ds = new DescriptorSupport(fields, values);
      assertEquals("Expecting 'descriptorType' value to be 'mbean'", (String)ds.getFieldValue("DESCRIPTORTYPE"), "MBean");
      assertEquals("Expecting 'myField' value to be 'top secret'", (String)ds.getFieldValue("MYfIELD"), "top secret");

      fields = new String[]{"name", "descriptorType", "deleteMe"};
      values = new String[]{"testCaseInsensitiveFieldNames", "MBean", "nothing of consequence"};
      ds = new DescriptorSupport(fields, values);
      ds.removeField("DELETEmE");
      String[] fieldnames = ds.getFields();
      List fieldlist = new ArrayList();
      for (int i = 0; i < fieldnames.length; i++) fieldlist.add(fieldnames[i]);
      int fieldcount = fieldnames.length;
      assertEquals("Expecting 'deleteMe' to be gone", fieldcount, 2);
      assertFalse(fieldlist.contains("deleteme"));
   }

   public void testCaseInsensitiveFieldValues() throws Exception
   {
      String[] fields = {"descriptorType", "persistPolicy", "log"};
      String[] values = {"mBEAN", "oNuPDATE", "TRUE"};
      new DescriptorSupport(fields, values);
   }

   public void testCaseSensitivityPreserved() throws Exception
   {
      String[] names = {"Name", "DescriptorType"};
      String[] values = {"test", "mbean"};
      DescriptorSupport descriptor = new DescriptorSupport(names, values);

      // Check case insensitivity on get
      String value = (String)descriptor.getFieldValue("name");
      assertNotNull(value);
      assertEquals(value, values[0]);
      value = (String)descriptor.getFieldValue("descriptorType");
      assertNotNull(value);
      assertEquals(value, values[1]);

      // Be sure case is preserved
      String[] fieldNames = descriptor.getFieldNames();
      assertNotNull(fieldNames);
      assertEquals(fieldNames.length, 2);
      if (!fieldNames[0].equals(names[0]) && !fieldNames[0].equals(names[1])) fail();
      if (!fieldNames[1].equals(names[0]) && !fieldNames[1].equals(names[1])) fail();

      // Check serialization works
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      ObjectOutputStream oos = new ObjectOutputStream(baos);
      oos.writeObject(descriptor);
      oos.close();
      ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
      ObjectInputStream ois = new ObjectInputStream(bais);
      DescriptorSupport newDescriptor = (DescriptorSupport)ois.readObject();
      value = (String)newDescriptor.getFieldValue("name");
      assertNotNull(value);
      assertEquals(value, values[0]);
      // Be sure case is preserved
      fieldNames = newDescriptor.getFieldNames();
      assertNotNull(fieldNames);
      assertEquals(fieldNames.length, 2);
      if (!fieldNames[0].equals(names[0]) && !fieldNames[0].equals(names[1])) fail();
      if (!fieldNames[1].equals(names[0]) && !fieldNames[1].equals(names[1])) fail();

      // Check that removeField() really removes the field, no matter case sensitivity
      descriptor.removeField("name");
      value = (String)descriptor.getFieldValue("name");
      assertNull(value);
      fieldNames = descriptor.getFieldNames();
      assertNotNull(fieldNames);
      assertEquals(fieldNames.length, 1);
      assertEquals(fieldNames[0], names[1]);
   }

   public void testDefaultFieldValuesSize() throws Exception
   {
      DescriptorSupport ds = new DescriptorSupport();
      Object[] fields = ds.getFieldValues(null);
      assertTrue("Expecting 0 length array", fields.length == 0);
   }

   public void testNullDescriptorConstructorParam() throws Exception
   {
      DescriptorSupport ds = new DescriptorSupport((DescriptorSupport)null);
      assertNotNull(ds.getFields());
      assertEquals("Expecting the descriptor to be empty", ds.getFields().length, 0);
      assertFalse("Expecting the descriptor to be invalid", ds.isValid());
   }

   public void testNullStringConstructorParam() throws Exception
   {
      try
      {
         new DescriptorSupport((String)null);
         fail("Expecting RuntimeOperationsException");
      }
      catch (RuntimeOperationsException x)
      {
         if (!(x.getTargetException() instanceof IllegalArgumentException))
            fail("Target exception should be IllegalArgumentException");
      }
   }

   public void testNullStringArrayCtorParm() throws Exception
   {
      String[] nullfields = null;
      DescriptorSupport ds = new DescriptorSupport(nullfields);
      Object[] fields = ds.getFieldValues(null);
      assertEquals("Expecting 0 length array", fields.length, 0);
   }

   public void testToXMLString() throws Exception
   {
      String[] fields = {"wine", "vineyard", "year", "price"};
      Object[] values = {"Amarone", "Allegrini", "1996", new Integer(90)};
      DescriptorSupport ds = new DescriptorSupport(fields, values);
      String xml = ds.toXMLString();
      assertTrue("Descriptor from XML != Descriptor", descriptorsEqual(ds, (new DescriptorSupport(xml))));

      fields = new String[]{"wine=Amarone", "vineyard=Allegrini", "year=1996"};
      ds = new DescriptorSupport(fields);
      xml = ds.toXMLString();
      assertTrue("Descriptor from XML != Descriptor", descriptorsEqual(ds, (new DescriptorSupport(xml))));
   }

   public void testEmptyDescriptorToXMLString() throws Exception
   {
      DescriptorSupport ds = new DescriptorSupport();
      String xml = ds.toXMLString();
      assertEquals("Unexpected xml: " + xml, xml, "<Descriptor></Descriptor>");
   }

   public void testGetFieldsUnknownName()
   {
      String[] fields = {"wine", "vineyard", "year"};
      String[] values = {"Amarone", "Allegrini", "1996"};
      DescriptorSupport ds = new DescriptorSupport(fields, values);
      Object fv = ds.getFieldValue("price");
      assertNull(fv);
      fields = new String[]{"wine", "vineyard", "year", "price"};
      Object[] fvs = ds.getFieldValues(fields);
      assertEquals("wrong number of values", fvs.length, fields.length);
      assertNull(fvs[3]);
   }

   public void testGetFieldsEmptyArray()
   {
      String[] fields = {"wine", "vineyard", "year"};
      String[] values = {"Amarone", "Allegrini", "1996"};
      DescriptorSupport ds = new DescriptorSupport(fields, values);
      Object[] fvs = ds.getFieldValues(new String[0]);
      assertEquals("Expecting empty array", fvs.length, 0);
   }

   public void testRemoveField()
   {
      String[] fields = {"wine", "vineyard", "year", "price"};
      Object[] values = {"Amarone", "Allegrini", "1996", new Integer(90)};
      DescriptorSupport ds = new DescriptorSupport(fields, values);
      assertEquals("Expecting " + fields.length + " names", ds.getFieldNames().length, fields.length);
      ds.removeField("price");
      assertEquals("Expecting " + (fields.length - 1) + " names", ds.getFieldNames().length, fields.length - 1);
   }

   public void testRemoveNonexistentFieldDoesntThrow()
   {
      String[] fields = {"wine", "vineyard", "year"};
      String[] values = {"Amarone", "Allegrini", "1996"};
      DescriptorSupport ds = new DescriptorSupport(fields, values);
      ds.removeField("price");
   }

   public void testRemoveNullField()
   {
      String[] fields = {"wine", "vineyard", "year"};
      String[] values = {"Amarone", "Allegrini", "1996"};
      DescriptorSupport ds = new DescriptorSupport(fields, values);
      ds.removeField(null);
   }

   public void testXMLStringConstructor() throws Exception
   {
      StringBuffer xmldescriptor = new StringBuffer();
      xmldescriptor.append("<Descriptor>");
      xmldescriptor.append("<field ");
      xmldescriptor.append("name=\"bogus\" ");
      xmldescriptor.append("value=\"xyzzy\"");
      xmldescriptor.append(">");
      xmldescriptor.append("</field>");
      xmldescriptor.append("</Descriptor>");
      DescriptorSupport ds = new DescriptorSupport(xmldescriptor.toString());
      String xml = ds.toXMLString();
      assertTrue("Descriptor from XML != Descriptor", descriptorsEqual(ds, (new DescriptorSupport(xml))));
   }

   public void testXMLSpecialStringConstructor() throws Exception
   {
      String[] fields = {"name=Lawrence", "nickname=(Larry)"};
      DescriptorSupport ds = new DescriptorSupport(fields);
      String xml = ds.toXMLString();
      DescriptorSupport xmlds = new DescriptorSupport(xml);
      assertTrue("Descriptors not equal", descriptorsEqual(ds, xmlds));
   }

   public void testXMLPrimitiveConstructor() throws Exception
   {
      StringBuffer xmldescriptor = new StringBuffer();
      xmldescriptor.append("<Descriptor>");
      xmldescriptor.append("<field ");
      xmldescriptor.append("name=\"boolean\" ");
      xmldescriptor.append("value=\"(java.lang.Boolean/true)\"");
      xmldescriptor.append(">");
      xmldescriptor.append("</field>");
      xmldescriptor.append("<field ");
      xmldescriptor.append("name=\"byte\" ");
      xmldescriptor.append("value=\"(java.lang.Byte/127)\"");
      xmldescriptor.append(">");
      xmldescriptor.append("</field>");
      xmldescriptor.append("<field ");
      xmldescriptor.append("name=\"char\" ");
      xmldescriptor.append("value=\"(java.lang.Character/k)\"");
      xmldescriptor.append(">");
      xmldescriptor.append("</field>");
      xmldescriptor.append("<field ");
      xmldescriptor.append("name=\"short\" ");
      xmldescriptor.append("value=\"(java.lang.Short/4096)\"");
      xmldescriptor.append(">");
      xmldescriptor.append("</field>");
      xmldescriptor.append("<field ");
      xmldescriptor.append("name=\"int\" ");
      xmldescriptor.append("value=\"(java.lang.Integer/16384)\"");
      xmldescriptor.append(">");
      xmldescriptor.append("</field>");
      xmldescriptor.append("<field ");
      xmldescriptor.append("name=\"long\" ");
      xmldescriptor.append("value=\"(java.lang.Long/123456789)\"");
      xmldescriptor.append(">");
      xmldescriptor.append("</field>");
      xmldescriptor.append("<field ");
      xmldescriptor.append("name=\"float\" ");
      xmldescriptor.append("value=\"(java.lang.Float/3.14)\"");
      xmldescriptor.append(">");
      xmldescriptor.append("</field>");
      xmldescriptor.append("<field ");
      xmldescriptor.append("name=\"double\" ");
      xmldescriptor.append("value=\"(java.lang.Double/3.14e-10)\"");
      xmldescriptor.append(">");
      xmldescriptor.append("</field>");
      xmldescriptor.append("<field ");
      xmldescriptor.append("name=\"null\" ");
      xmldescriptor.append("value=\"(null)\"");
      xmldescriptor.append(">");
      xmldescriptor.append("</field>");
      xmldescriptor.append("</Descriptor>");
      DescriptorSupport ds = new DescriptorSupport(xmldescriptor.toString());
      String xml = ds.toXMLString();
      assertTrue("Descriptor from XML != Descriptor", descriptorsEqual(ds, (new DescriptorSupport(xml))));
   }

   public void testXMLClassConstructor() throws Exception
   {
      StringBuffer xmldescriptor = new StringBuffer();
      xmldescriptor.append("<Descriptor>");
      xmldescriptor.append("<field ");
      xmldescriptor.append("name=\"date\" ");
      xmldescriptor.append("value=\"(java.net.URL/http://mx4j.sourceforge.net)\"");
      xmldescriptor.append(">");
      xmldescriptor.append("</field>");
      xmldescriptor.append("</Descriptor>");
      DescriptorSupport ds = new DescriptorSupport(xmldescriptor.toString());
      String xml = ds.toXMLString();
      assertTrue("Descriptor from XML != Descriptor", descriptorsEqual(ds, (new DescriptorSupport(xml))));
   }

   public void testBogusXMLConstructor() throws Exception
   {
      try
      {
         new DescriptorSupport("<Descriptor><field name=</Descriptor>");
         fail("Expecting XMLParseException");
      }
      catch (XMLParseException x)
      {
      }
   }

   public void testBogusXMLValueCtor() throws Exception
   {
      try
      {
         StringBuffer xmldescriptor = new StringBuffer();
         xmldescriptor.append("<Descriptor>");
         xmldescriptor.append("<field ");
         xmldescriptor.append("name=\"bogus\" ");
         xmldescriptor.append("value=\"(java.lang.Byte/256)\"");
         xmldescriptor.append(">");
         xmldescriptor.append("</field>");
         xmldescriptor.append("</Descriptor>");
         new DescriptorSupport(xmldescriptor.toString());
         fail("Expecting XMLParseException");
      }
      catch (XMLParseException x)
      {
      }
   }

   private boolean descriptorsEqual(Descriptor done, Descriptor dtwo)
   {
      Set namesone = new HashSet(Arrays.asList(done.getFieldNames()));
      Set namestwo = new HashSet(Arrays.asList(dtwo.getFieldNames()));
      if (!namesone.equals(namestwo)) return false;
      Iterator i = namesone.iterator();
      while (i.hasNext())
      {
         String field = (String)i.next();
         Object vone = done.getFieldValue(field);
         Object vtwo = dtwo.getFieldValue(field);
         if ((vone == null && vtwo != null) || (vone != null && !vone.equals(vtwo)))
         {
            return false;
         }
      }
      return true;
   }
}
