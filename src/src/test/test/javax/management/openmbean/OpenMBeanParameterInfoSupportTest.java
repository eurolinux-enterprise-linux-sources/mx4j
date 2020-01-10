/**
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */
package test.javax.management.openmbean;

import java.util.Iterator;
import java.util.Set;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenMBeanParameterInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.openmbean.SimpleType;

import junit.framework.TestCase;
import test.javax.management.compliance.serialization.support.Serializer;

/**
 * @version $Revision: 1.6 $
 */

public class OpenMBeanParameterInfoSupportTest extends TestCase
{
   private String[] legalModels;
   private String[] legalColors;
   private static String[] legalSizes;
   private float minPrice;
   private float maxPrice;
   private OpenMBeanParameterInfoSupport priceParamInfo;

   public OpenMBeanParameterInfoSupportTest(String s)
   {
      super(s);
   }

   protected void setUp() throws Exception
   {
      super.setUp();
      legalModels = new String[]{"JDMK", "JMX", "JAVA"};
      legalColors = new String[]{"black", "white", "red", "green", "blue"};
      legalSizes = new String[]{"S", "M", "L", "XL", "XXL"};
      minPrice = 9.00f;
      maxPrice = 19.99f;
      priceParamInfo = new OpenMBeanParameterInfoSupport("price",
                                                         "Valid product price",
                                                         SimpleType.FLOAT,
                                                         new Float(10.00), // default price
                                                         new Float(minPrice), // Min legal value for price
                                                         new Float(maxPrice));  // Max legal value for price
   }

   protected void tearDown() throws Exception
   {
      super.tearDown();
   }

   public void testEquals()
   {
      try
      {
         OpenMBeanParameterInfoSupport infoSupport = new OpenMBeanParameterInfoSupport("test", "hello world", SimpleType.STRING, "black", legalColors);
         OpenMBeanParameterInfoSupport equalInfoSupport = new OpenMBeanParameterInfoSupport("test", "hello world", SimpleType.STRING, "black", legalColors);
         OpenMBeanParameterInfoSupport info2Support = new OpenMBeanParameterInfoSupport("test2", "hello world2", SimpleType.STRING);


         OpenMBeanParameterInfoSupport priceParamInfo2 = new OpenMBeanParameterInfoSupport("price",
                                                                                           "Valid product price",
                                                                                           SimpleType.FLOAT,
                                                                                           new Float(10.00), // default price
                                                                                           new Float(minPrice), // Min legal value for price
                                                                                           new Float(maxPrice));  // Max legal value for price
         // test we can equal null values
         assertTrue(!(infoSupport.equals(info2Support)));

         assertTrue(infoSupport.equals(equalInfoSupport));
         assertTrue(equalInfoSupport.equals(infoSupport));
         assertTrue(priceParamInfo.equals(priceParamInfo2));

         OpenMBeanParameterInfo rebootcmd =
                 new OpenMBeanParameterInfoSupport("reboot",
                                                   "Reboot the server",
                                                   SimpleType.INTEGER);
         OpenMBeanParameterInfo rebootquery =
                 new OpenMBeanParameterInfoSupport("reboot",
                                                   "Reboot the server",
                                                   SimpleType.BOOLEAN);
         assertFalse("activeclients.equals(reboot)", rebootcmd.equals(rebootquery));
      }
      catch (OpenDataException e)
      {
         e.printStackTrace();
      }

   }

   public void testDefaultSerialization()
   {
      int expectedHash = priceParamInfo.hashCode();
      Serializer serializer = new Serializer();

      try
      {
         byte[] data = serializer.serialize(priceParamInfo);

         Object obj = serializer.deserialize(data);
         // assert instanceof
         assertTrue(obj instanceof OpenMBeanParameterInfo);

         // if instanceof passes continue otherwise we will not get to the rest
         OpenMBeanParameterInfo type = (OpenMBeanParameterInfo)obj;
// assert hashcodes are equal
         assertEquals(type.hashCode(), expectedHash);
         assertTrue(type.equals(priceParamInfo));
         assertTrue(priceParamInfo.equals(type));
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public void testBasicCtor()
   {
      OpenMBeanParameterInfoSupport info =
              new OpenMBeanParameterInfoSupport("currency",
                                                "monetary currency",
                                                SimpleType.STRING);
      assertTrue("Null info constructed", info != null);
      assertTrue("Unexpected name",
                 info.getName().compareTo("currency") == 0);
      assertTrue("Unexpected description",
                 info.getDescription().compareTo("monetary currency") == 0);
      assertTrue("Unexpected open type",
                 info.getOpenType().equals(SimpleType.STRING));
      assertFalse("Shouldn't have default value", info.hasDefaultValue());
      assertFalse("Shouldn't have legal values",
                  info.hasLegalValues());
      assertFalse("Shouldn't have a min value", info.hasMinValue());
      assertFalse("Shouldn't have a max value", info.hasMaxValue());
   }

   public void testBasicCtorNullName()
   {
      try
      {
         OpenMBeanParameterInfoSupport info =
                 new OpenMBeanParameterInfoSupport(null,
                                                   "monetary currency",
                                                   SimpleType.STRING);
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);
      }
   }

   public void testBasicCtorEmptyName()
   {
      try
      {
         OpenMBeanParameterInfoSupport info =
                 new OpenMBeanParameterInfoSupport("",
                                                   "monetary currency",
                                                   SimpleType.STRING);
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);
      }
   }

   public void testBasicCtorNullDescription()
   {
      try
      {
         OpenMBeanParameterInfoSupport info =
                 new OpenMBeanParameterInfoSupport("currency",
                                                   null,
                                                   SimpleType.STRING);
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);
      }
   }

   public void testBasicCtorEmptyDescription()
   {
      try
      {
         OpenMBeanParameterInfoSupport info =
                 new OpenMBeanParameterInfoSupport("currency",
                                                   "",
                                                   SimpleType.STRING);
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);
      }
   }

   public void testBasicCtorNullOpenType()
   {
      try
      {
         OpenMBeanParameterInfoSupport info =
                 new OpenMBeanParameterInfoSupport("currency",
                                                   "monetary currency",
                                                   null);
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);
      }
   }

   public void testDefaultValueCtor() throws Exception
   {
      OpenMBeanParameterInfoSupport info =
              new OpenMBeanParameterInfoSupport("currency",
                                                "monetary currency",
                                                SimpleType.STRING,
                                                "Euro");
      assertTrue("Null info constructed", info != null);
      assertTrue("Unexpected name",
                 info.getName().compareTo("currency") == 0);
      assertTrue("Unexpected description",
                 info.getDescription().compareTo("monetary currency") == 0);
      assertTrue("Unexpected open type",
                 info.getOpenType().equals(SimpleType.STRING));
      assertTrue("Should have default value", info.hasDefaultValue());
      assertTrue("Unexpected default value",
                 ((String)info.getDefaultValue()).compareTo("Euro") == 0);
      assertFalse("Shouldn't have legal values",
                  info.hasLegalValues());
      assertFalse("Shouldn't have a min value", info.hasMinValue());
      assertFalse("Shouldn't have a max value", info.hasMaxValue());
   }

   public void testDefaultValueCtorInvalidType() throws Exception
   {
      try
      {
         OpenMBeanParameterInfoSupport info =
                 new OpenMBeanParameterInfoSupport("currency",
                                                   "monetary currency",
                                                   SimpleType.STRING,
                                                   new Float(0.42));
         fail("Expecting OpenDataException");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }
   }

   public void testDefaultValueCtorArrayType() throws Exception
   {
      try
      {
         OpenMBeanParameterInfoSupport info =
                 new OpenMBeanParameterInfoSupport("currency",
                                                   "monetary currency",
                                                   new ArrayType(1, SimpleType.STRING),
                                                   null);
         assertTrue("Null info constructed", info != null);

         info =
         new OpenMBeanParameterInfoSupport("currency",
                                           "monetary currency",
                                           new ArrayType(1, SimpleType.STRING),
                                           "Euro");
         fail("Expecting OpenDataException");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }
   }

   public void testLegalValueCtor() throws Exception
   {
      OpenMBeanParameterInfoSupport info =
              new OpenMBeanParameterInfoSupport("currency",
                                                "monetary currency",
                                                SimpleType.STRING,
                                                "Euro",
                                                new String[]{"Dollar", "Euro", "Yen"});
      assertTrue("Null info constructed", info != null);
      assertTrue("Unexpected name",
                 info.getName().compareTo("currency") == 0);
      assertTrue("Unexpected description",
                 info.getDescription().compareTo("monetary currency") == 0);
      assertTrue("Unexpected open type",
                 info.getOpenType().equals(SimpleType.STRING));
      assertTrue("Should have default value", info.hasDefaultValue());
      assertTrue("Unexpected default value",
                 ((String)info.getDefaultValue()).compareTo("Euro") == 0);
      assertTrue("Should have legal values",
                 info.getLegalValues() != null && info.getLegalValues().size() == 3);
      assertFalse("Shouldn't have a min value", info.hasMinValue());
      assertFalse("Shouldn't have a max value", info.hasMaxValue());
   }

   public void testLegalValueCtorInvalidType() throws Exception
   {
      try
      {
         OpenMBeanParameterInfoSupport info =
                 new OpenMBeanParameterInfoSupport("currency",
                                                   "monetary currency",
                                                   SimpleType.STRING,
                                                   "Euro",
                                                   new Object[]{"Dollar", "Euro", new Float(0.88)});
         fail("Expecting OpenDataException");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }
   }

   public void testLegalValueCtorArrayType() throws Exception
   {
      try
      {
         OpenMBeanParameterInfoSupport info =
                 new OpenMBeanParameterInfoSupport("currency",
                                                   "monetary currency",
                                                   new ArrayType(1, SimpleType.STRING),
                                                   null,
                                                   new String[]{"Dollar", "Euro", "Yen"});
         fail("Expecting OpenDataException");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }
   }

   public void testLegalValueCtorBogusDefaultValue() throws Exception
   {
      try
      {
         OpenMBeanParameterInfoSupport info =
                 new OpenMBeanParameterInfoSupport("currency",
                                                   "monetary currency",
                                                   SimpleType.STRING,
                                                   "Pound",
                                                   new String[]{"Dollar", "Euro", "Yen"});
         fail("Expecting OpenDataException");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }
   }

   public void testMinMaxValueCtor() throws Exception
   {
      OpenMBeanParameterInfoSupport info =
              new OpenMBeanParameterInfoSupport("price",
                                                "how much it costs",
                                                SimpleType.FLOAT,
                                                new Float(1.00),
                                                new Float(0.75),
                                                new Float(1.50));
      assertTrue("Null info constructed", info != null);
      assertTrue("Unexpected name",
                 info.getName().compareTo("price") == 0);
      assertTrue("Unexpected description",
                 info.getDescription().compareTo("how much it costs") == 0);
      assertTrue("Unexpected open type",
                 info.getOpenType().equals(SimpleType.FLOAT));
      assertTrue("Should have default value", info.hasDefaultValue());
      assertTrue("Unexpected default value",
                 ((Float)info.getDefaultValue()).equals(new Float(1.00)));
      assertFalse("Shouldn't have legal values",
                  info.hasLegalValues());
      assertTrue("Should have a min value of 0.75",
                 info.hasMinValue()
                 && ((Float)info.getMinValue()).equals(new Float(0.75)));
      assertTrue("Should have a max value of 1.50",
                 info.hasMaxValue()
                 && ((Float)info.getMaxValue()).equals(new Float(1.50)));
   }

   public void testMinMaxValueCtorInvalidMinType() throws Exception
   {
      try
      {
         OpenMBeanParameterInfoSupport info =
                 new OpenMBeanParameterInfoSupport("price",
                                                   "how much it costs",
                                                   SimpleType.FLOAT,
                                                   new Float(1.00),
                                                   "0.75",
                                                   new Float(1.50));
         fail("Expecting OpenDataException");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }
   }

   public void testMinMaxValueCtorInvalidMaxType() throws Exception
   {
      try
      {
         OpenMBeanParameterInfoSupport info =
                 new OpenMBeanParameterInfoSupport("price",
                                                   "how much it costs",
                                                   SimpleType.FLOAT,
                                                   new Float(1.00),
                                                   new Float(0.75),
                                                   "1.50");
         fail("Expecting OpenDataException");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }
   }

   public void testMinMaxValueCtorMinGTMax() throws Exception
   {
      try
      {
         OpenMBeanParameterInfoSupport info =
                 new OpenMBeanParameterInfoSupport("price",
                                                   "how much it costs",
                                                   SimpleType.FLOAT,
                                                   new Float(1.00),
                                                   new Float(1.50),
                                                   new Float(0.75));
         fail("Expecting OpenDataException");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }
   }

   public void testMinMaxValueCtorDefaultOutOfRange() throws Exception
   {
      try
      {
         OpenMBeanParameterInfoSupport info =
                 new OpenMBeanParameterInfoSupport("price",
                                                   "how much it costs",
                                                   SimpleType.FLOAT,
                                                   new Float(0.75),
                                                   new Float(1.00),
                                                   new Float(1.50));
         fail("Expecting OpenDataException default < min");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }

      try
      {
         OpenMBeanParameterInfoSupport info =
                 new OpenMBeanParameterInfoSupport("price",
                                                   "how much it costs",
                                                   SimpleType.FLOAT,
                                                   new Float(1.50),
                                                   new Float(0.75),
                                                   new Float(1.00));
         fail("Expecting OpenDataException default > max");
      }
      catch (OpenDataException x)
      {
         assertTrue(true);
      }
   }

   public void testBasicHashCode() throws Exception
   {
      OpenMBeanParameterInfoSupport infoone =
              new OpenMBeanParameterInfoSupport("currency",
                                                "monetary currency",
                                                SimpleType.STRING);
      assertTrue("Unexpected basic hash code",
                 infoone.hashCode() == hashCode(infoone));

      OpenMBeanParameterInfoSupport infotwo =
              new OpenMBeanParameterInfoSupport("currency",
                                                "legal tender",
                                                SimpleType.STRING);
      assertTrue("Expecting hash codes to be equal", infotwo.hashCode() == infoone.hashCode());
   }

   public void testDefaultValueHashCode() throws Exception
   {
      OpenMBeanParameterInfoSupport infoone =
              new OpenMBeanParameterInfoSupport("currency",
                                                "monetary currency",
                                                SimpleType.STRING,
                                                "Euro");
      assertTrue("Unexpected default value hash code",
                 infoone.hashCode() == hashCode(infoone));

      OpenMBeanParameterInfoSupport infotwo =
              new OpenMBeanParameterInfoSupport("currency",
                                                "legal tender",
                                                SimpleType.STRING,
                                                "Euro");
      assertTrue("Unexpected default value hash code",
                 infotwo.hashCode() == infoone.hashCode());
   }

   public void testLegalValueHashCode() throws Exception
   {
      OpenMBeanParameterInfoSupport infoone =
              new OpenMBeanParameterInfoSupport("currency",
                                                "monetary currency",
                                                SimpleType.STRING,
                                                "Euro",
                                                new String[]{"Dollar", "Euro", "Yen"});
      assertTrue("Unexpected legal value hash code",
                 infoone.hashCode() == hashCode(infoone));

      OpenMBeanParameterInfoSupport infotwo =
              new OpenMBeanParameterInfoSupport("currency",
                                                "monetary currency",
                                                SimpleType.STRING,
                                                "Euro",
                                                new String[]{"Dollar", "Euro", "Yen"});
      assertTrue("Unexpected legal value hash code",
                 infoone.hashCode() == hashCode(infotwo));
   }

   public void testMinMaxHashCode() throws Exception
   {
      OpenMBeanParameterInfoSupport infoone =
              new OpenMBeanParameterInfoSupport("price",
                                                "how much it costs",
                                                SimpleType.FLOAT,
                                                new Float(1.00),
                                                new Float(0.75),
                                                new Float(1.50));
      assertTrue("Unexpected minmax hash code",
                 infoone.hashCode() == hashCode(infoone));

      OpenMBeanParameterInfoSupport infotwo =
              new OpenMBeanParameterInfoSupport("price",
                                                "retail",
                                                SimpleType.FLOAT,
                                                new Float(1.00),
                                                new Float(0.75),
                                                new Float(1.50));
      assertTrue("Unexpected minmax hash code",
                 infotwo.hashCode() == infoone.hashCode());
   }

   private int hashCode(OpenMBeanParameterInfo info)
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
