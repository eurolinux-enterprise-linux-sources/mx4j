/**
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */
package test.javax.management.openmbean;

/**
 * @version $Revision: 1.7 $
 */

import java.util.Iterator;
import java.util.Set;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.SimpleType;

import junit.framework.TestCase;


public class OpenMBeanAttributeInfoSupportTest extends TestCase
{


   public OpenMBeanAttributeInfoSupportTest(String s)
   {
      super(s);
   }

   protected void setUp() throws Exception
   {
      super.setUp();

   }

   protected void tearDown() throws Exception
   {
      super.tearDown();
   }

   public void testEquals() throws Exception
   {
      try
      {

         OpenMBeanAttributeInfoSupport o1 =
                 new OpenMBeanAttributeInfoSupport("name", "The name", SimpleType.STRING, false, true, false);
         OpenMBeanAttributeInfoSupport o2 =
                 new OpenMBeanAttributeInfoSupport("name", "The name", SimpleType.STRING, false, true, false);
         OpenMBeanAttributeInfoSupport o3 =
                 new OpenMBeanAttributeInfoSupport("name", "The name", SimpleType.STRING, true, false, false);

         assertTrue(!o1.equals(null));
         assertTrue(o1.equals(o2));
         assertEquals(o1.hashCode(), o2.hashCode());
         assertTrue(!o1.equals(o3));

      }
      catch (Exception e)
      {
         e.printStackTrace();
      }


   }

   public void testSixParamCtor() throws Exception
   {
      OpenMBeanAttributeInfoSupport info =
              new OpenMBeanAttributeInfoSupport("price",
                                                "how much it costs",
                                                SimpleType.FLOAT,
                                                true,
                                                false,
                                                false);
      assertTrue("Null info constructed", info != null);
      assertTrue("OpenType should be FLOAT",
                 info.getOpenType().equals(SimpleType.FLOAT));
      assertTrue("Attribute should be readable", info.isReadable());
      assertFalse("Attribute should not be writeable", info.isWritable());
      assertFalse("Attribute is not 'is", info.isIs());

      try
      {
         info =
         new OpenMBeanAttributeInfoSupport(null,
                                           "how much it costs",
                                           SimpleType.FLOAT,
                                           true,
                                           false,
                                           false);
         fail("Expecting exception for null name");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);
      }

      try
      {
         info =
         new OpenMBeanAttributeInfoSupport("",
                                           "how much it costs",
                                           SimpleType.FLOAT,
                                           true,
                                           false,
                                           false);
         fail("Expecting exception for empty name");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);
      }

      try
      {
         info =
         new OpenMBeanAttributeInfoSupport("price",
                                           null,
                                           SimpleType.FLOAT,
                                           true,
                                           false,
                                           false);
         fail("Expecting exception for null description");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);
      }

      try
      {
         info =
         new OpenMBeanAttributeInfoSupport("price",
                                           "",
                                           SimpleType.FLOAT,
                                           true,
                                           false,
                                           false);
         fail("Expecting exception for empty description");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);
      }

      try
      {
         info =
         new OpenMBeanAttributeInfoSupport("price",
                                           "how much it costs",
                                           null,
                                           true,
                                           false,
                                           false);
         fail("Expecting exception for null type");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);
      }
   }

   public void testSevenParamCtor() throws Exception
   {
      Float defaultvalue = new Float(1.00);
      OpenMBeanAttributeInfoSupport info =
              new OpenMBeanAttributeInfoSupport("price",
                                                "how much it costs",
                                                SimpleType.FLOAT,
                                                true,
                                                false,
                                                false,
                                                defaultvalue);
      assertTrue("Null info constructed", info != null);
      assertTrue("Expecting default value of 1.00", defaultvalue.equals((Float)info.getDefaultValue()));

      info =
      new OpenMBeanAttributeInfoSupport("price",
                                        "how much it costs",
                                        SimpleType.FLOAT,
                                        true,
                                        false,
                                        false,
                                        null);
      assertTrue("Null info constructed", info != null);
      assertFalse("There should be no default value", info.hasDefaultValue());
   }

   public void testEightParamCtor() throws Exception
   {
      Float[] legalvalues = {new Float(0.75), new Float(1.00), new Float(1.50)};
      Float defaultvalue = new Float(1.00);
      OpenMBeanAttributeInfoSupport info =
              new OpenMBeanAttributeInfoSupport("price",
                                                "how much it costs",
                                                SimpleType.FLOAT,
                                                true,
                                                false,
                                                false,
                                                defaultvalue,
                                                legalvalues);
      assertTrue("Null info constructed", info != null);
      Set legalset = info.getLegalValues();
      assertTrue("Legal set is the wrong size", legalset.size() == legalvalues.length);
      assertTrue("0.75 not in legal set",
                 legalset.contains(new Float(0.75)));
      assertTrue("1.00 not in legal set",
                 legalset.contains(new Float(1.00)));
      assertTrue("1.50 not in legal set",
                 legalset.contains(new Float(1.50)));

      info =
      new OpenMBeanAttributeInfoSupport("price",
                                        "how much it costs",
                                        SimpleType.FLOAT,
                                        true,
                                        false,
                                        false,
                                        defaultvalue,
                                        null);
      assertTrue("Null info constructed", info != null);
      assertFalse("There should be no legal value set for null",
                  info.hasLegalValues());

      info =
      new OpenMBeanAttributeInfoSupport("price",
                                        "how much it costs",
                                        SimpleType.FLOAT,
                                        true,
                                        false,
                                        false,
                                        defaultvalue,
                                        new Float[0]);
      assertTrue("Null info constructed", info != null);
      assertFalse("There should be no legal value set for Float[0]",
                  info.hasLegalValues());

      info =
      new OpenMBeanAttributeInfoSupport("price",
                                        "how much it costs",
                                        SimpleType.FLOAT,
                                        true,
                                        false,
                                        false,
                                        null,
                                        legalvalues);
      assertTrue("Null info constructed", info != null);
      assertFalse("Has a default value", info.hasDefaultValue());

      try
      {
         info =
         new OpenMBeanAttributeInfoSupport("price",
                                           "how much it costs",
                                           SimpleType.FLOAT,
                                           true,
                                           false,
                                           false,
                                           "Invalid Default Value",
                                           new Float[0]);
         fail("Expecting exception for invalid default value");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }

      try
      {
         info =
         new OpenMBeanAttributeInfoSupport("price",
                                           "how much it costs",
                                           SimpleType.FLOAT,
                                           true,
                                           false,
                                           false,
                                           defaultvalue,
                                           new Object[]{new Float(0.75), "$1.50"});
         fail("Expecting exception for invalid legal value");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }

      try
      {
         info =
         new OpenMBeanAttributeInfoSupport("price",
                                           "how much it costs",
                                           new ArrayType(1, SimpleType.FLOAT),
                                           true,
                                           false,
                                           false,
                                           defaultvalue,
                                           null);
         fail("Expecting exception for non null default w/ArrayType attribute");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }

      try
      {
         info =
         new OpenMBeanAttributeInfoSupport("price",
                                           "how much it costs",
                                           new ArrayType(1, SimpleType.FLOAT),
                                           true,
                                           false,
                                           false,
                                           null,
                                           new Float[]{new Float(0.75), new Float(1.50)});
         fail("Expecting exception for non null legal set w/ArrayType attribute");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }

      try
      {
         info =
         new OpenMBeanAttributeInfoSupport("price",
                                           "how much it costs",
                                           new ArrayType(1, SimpleType.FLOAT),
                                           true,
                                           false,
                                           false,
                                           new Float(0.25),
                                           legalvalues);
         fail("Expecting exception for default not in legal set");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }

      try
      {
         info =
         new OpenMBeanAttributeInfoSupport("price",
                                           "how much it costs",
                                           SimpleType.INTEGER,
                                           true,
                                           false,
                                           false,
                                           new Integer(1),
                                           new Integer[]{new Integer(0), new Integer(2)});
         fail("Expecting exception for default not in legal set");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }
   }

   public void testNineParameCtor() throws Exception
   {
      Float defaultvalue = new Float(1.00);
      Float minvalue = new Float(0.75);
      Float maxvalue = new Float(1.50);
      OpenMBeanAttributeInfoSupport info =
              new OpenMBeanAttributeInfoSupport("price",
                                                "how much it costs",
                                                SimpleType.FLOAT,
                                                true,
                                                false,
                                                false,
                                                defaultvalue,
                                                minvalue,
                                                maxvalue);
      assertTrue("Null info constructed", info != null);
      assertTrue("Expecting min value of 0.75",
                 info.hasMinValue() && minvalue.equals((Float)info.getMinValue()));
      assertTrue("Expecting max value of 1.50",
                 info.hasMaxValue() && maxvalue.equals((Float)info.getMaxValue()));

      info =
      new OpenMBeanAttributeInfoSupport("price",
                                        "how much it costs",
                                        SimpleType.FLOAT,
                                        true,
                                        false,
                                        false,
                                        defaultvalue,
                                        null,
                                        maxvalue);
      assertTrue("Null info constructed", info != null);
      assertFalse("Not expecting a min value",
                  info.hasMinValue());
      assertTrue("Expecting max value of 1.50",
                 info.hasMaxValue() && maxvalue.equals((Float)info.getMaxValue()));

      info =
      new OpenMBeanAttributeInfoSupport("price",
                                        "how much it costs",
                                        SimpleType.FLOAT,
                                        true,
                                        false,
                                        false,
                                        defaultvalue,
                                        minvalue,
                                        null);
      assertTrue("Null info constructed", info != null);
      assertTrue("Expecting min value of 0.75",
                 info.hasMinValue() && minvalue.equals((Float)info.getMinValue()));
      assertFalse("Not expecting a max value",
                  info.hasMaxValue());

      try
      {
         info =
         new OpenMBeanAttributeInfoSupport("price",
                                           "how much it costs",
                                           SimpleType.FLOAT,
                                           true,
                                           false,
                                           false,
                                           "1.00",
                                           minvalue,
                                           maxvalue);
         fail("Expecting exception for bad default value type");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }

      try
      {
         info =
         new OpenMBeanAttributeInfoSupport("price",
                                           "how much it costs",
                                           SimpleType.FLOAT,
                                           true,
                                           false,
                                           false,
                                           defaultvalue,
                                           "0.75",
                                           maxvalue);
         fail("Expecting exception for bad min value type");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }

      try
      {
         info =
         new OpenMBeanAttributeInfoSupport("price",
                                           "how much it costs",
                                           SimpleType.FLOAT,
                                           true,
                                           false,
                                           false,
                                           defaultvalue,
                                           minvalue,
                                           "1.50");
         fail("Expecting exception for bad min value type");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }

      try
      {
         info =
         new OpenMBeanAttributeInfoSupport("price",
                                           "how much it costs",
                                           new ArrayType(1, SimpleType.FLOAT),
                                           true,
                                           false,
                                           false,
                                           defaultvalue,
                                           minvalue,
                                           maxvalue);
         fail("Expecting exception for non-null default value w/ArrayType attribute");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }

      try
      {
         info =
         new OpenMBeanAttributeInfoSupport("price",
                                           "how much it costs",
                                           SimpleType.FLOAT,
                                           true,
                                           false,
                                           false,
                                           defaultvalue,
                                           maxvalue,
                                           minvalue);
         fail("Expecting exception for min > max");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }

      try
      {
         info =
         new OpenMBeanAttributeInfoSupport("price",
                                           "how much it costs",
                                           SimpleType.FLOAT,
                                           true,
                                           false,
                                           false,
                                           minvalue,
                                           defaultvalue,
                                           maxvalue);
         fail("Expecting exception for default < min");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }
   }

   public void testSimpleInfoHashCode()
   {
      OpenMBeanAttributeInfoSupport info =
              new OpenMBeanAttributeInfoSupport("price",
                                                "how much it costs",
                                                SimpleType.FLOAT,
                                                true,
                                                false,
                                                false);
      assertTrue("Unexpected hash code for simple info", info.hashCode() == hashCode(info));
   }

   public void testDefaultValueHashCode() throws Exception
   {
      OpenMBeanAttributeInfoSupport info =
              new OpenMBeanAttributeInfoSupport("price",
                                                "how much it costs",
                                                SimpleType.FLOAT,
                                                true,
                                                false,
                                                false,
                                                new Float(1.00));
      assertTrue("Unexpected hash code for info w/default value", info.hashCode() == hashCode(info));
   }

   public void testNullDefaultValueHashCode() throws Exception
   {
      OpenMBeanAttributeInfoSupport info =
              new OpenMBeanAttributeInfoSupport("price",
                                                "how much it costs",
                                                SimpleType.FLOAT,
                                                true,
                                                false,
                                                false,
                                                null);
      assertTrue("Unexpected hash code for info w/null default value",
                 info.hashCode() == hashCode(info));
   }

   public void testLegalValueHashCode() throws Exception
   {
      OpenMBeanAttributeInfoSupport info =
              new OpenMBeanAttributeInfoSupport("price",
                                                "how much it costs",
                                                SimpleType.FLOAT,
                                                true,
                                                false,
                                                false,
                                                new Float(1.00),
                                                new Float[]{
                                                   new Float(0.75),
                                                   new Float(1.00),
                                                   new Float(1.50)});
      assertTrue("Unexpected hash code for info w/legal values",
                 info.hashCode() == hashCode(info));
   }

   public void testEmptyLegalValueHashCode() throws Exception
   {
      OpenMBeanAttributeInfoSupport info =
              new OpenMBeanAttributeInfoSupport("price",
                                                "how much it costs",
                                                SimpleType.FLOAT,
                                                true,
                                                false,
                                                false,
                                                new Float(1.00),
                                                new Float[0]);
      assertTrue("Unexpected hash code for info w/empty legal values",
                 info.hashCode() == hashCode(info));
   }

   public void testMinMaxValueHashCode() throws Exception
   {
      OpenMBeanAttributeInfoSupport info =
              new OpenMBeanAttributeInfoSupport("price",
                                                "how much it costs",
                                                SimpleType.FLOAT,
                                                true,
                                                false,
                                                false,
                                                new Float(1.00),
                                                new Float(0.75),
                                                new Float(1.50));
      assertTrue("Unexpected hash code for info w/minmax values",
                 info.hashCode() == hashCode(info));
   }

   public void testNullMinValueHashCode() throws Exception
   {
      OpenMBeanAttributeInfoSupport info =
              new OpenMBeanAttributeInfoSupport("price",
                                                "how much it costs",
                                                SimpleType.FLOAT,
                                                true,
                                                false,
                                                false,
                                                new Float(1.00),
                                                null,
                                                new Float(1.50));
      assertTrue("Unexpected hash code for info w/null min values",
                 info.hashCode() == hashCode(info));
   }

   public void testNullMaxValueHashCode() throws Exception
   {
      OpenMBeanAttributeInfoSupport info =
              new OpenMBeanAttributeInfoSupport("price",
                                                "how much it costs",
                                                SimpleType.FLOAT,
                                                true,
                                                false,
                                                false,
                                                new Float(1.00),
                                                new Float(0.75),
                                                null);
      assertTrue("Unexpected hash code for info w/empty legal values",
                 info.hashCode() == hashCode(info));
   }

   private int hashCode(OpenMBeanAttributeInfoSupport info)
   {
      int result = info.getName().hashCode();
      result += info.getOpenType().hashCode();
      result += (info.hasDefaultValue() == false) ? 0 : info.getDefaultValue().hashCode();
      result += (info.hasLegalValues() == false) ? 0 : hashCode(info.getLegalValues());
      result += (info.hasMinValue() == false) ? 0 : info.getMinValue().hashCode();
      result += (info.hasMaxValue() == false) ? 0 : info.getMaxValue().hashCode();
      return result;
   }

   private int hashCode(Set legalvalues)
   {
      int result = 0;
      Iterator i = legalvalues.iterator();
      while (i.hasNext())
      {
         Object v = i.next();
         result += v.hashCode();
      }
      return result;
   }
}
