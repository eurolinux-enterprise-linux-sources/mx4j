/**
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */
package test.javax.management.openmbean;

/**
 * @version $Revision: 1.5 $
 */

import java.util.Arrays;
import javax.management.MBeanOperationInfo;
import javax.management.openmbean.OpenMBeanOperationInfo;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.openmbean.SimpleType;

import junit.framework.TestCase;


public class OpenMBeanOperationInfoSupportTest extends TestCase
{
   OpenMBeanParameterInfo[] signature;
   OpenMBeanParameterInfo[] params = null;

   public OpenMBeanOperationInfoSupportTest(String s)
   {
      super(s);
   }

   protected void setUp() throws Exception
   {
      super.setUp();

      params =
      new OpenMBeanParameterInfo[]{

         new OpenMBeanParameterInfoSupport("name", "The name", SimpleType.STRING)
      };

      signature = new OpenMBeanParameterInfo[]{
         new OpenMBeanParameterInfoSupport("from",
                                           "currency to convert from",
                                           SimpleType.STRING),
         new OpenMBeanParameterInfoSupport("to",
                                           "currency to convert to",
                                           SimpleType.STRING)};
   }

   protected void tearDown() throws Exception
   {
      super.tearDown();
   }

   public void testCtor() throws Exception
   {
      OpenMBeanOperationInfoSupport info =
              new OpenMBeanOperationInfoSupport("exchangeRate",
                                                "compute the exchange rate for two currencies",
                                                signature,
                                                SimpleType.FLOAT,
                                                MBeanOperationInfo.INFO);
      assertTrue("Null info constructed", info != null);
      assertTrue("Unexpected name",
                 info.getName().compareTo("exchangeRate") == 0);
      assertTrue("Unexpected description",
                 info.getDescription().compareTo("compute the exchange rate for two currencies")
                 == 0);
      assertTrue("Unexpected return open type",
                 info.getReturnOpenType().equals(SimpleType.FLOAT));
      assertTrue("Unexpected signature",
                 Arrays.equals(info.getSignature(), signature));
      assertTrue("Unexpected impact",
                 info.getImpact() == MBeanOperationInfo.INFO);
   }

   public void testCtorNullName() throws Exception
   {
      try
      {
         OpenMBeanOperationInfoSupport info =
                 new OpenMBeanOperationInfoSupport(null,
                                                   "compute the exchange rate for two currencies",
                                                   signature,
                                                   SimpleType.FLOAT,
                                                   MBeanOperationInfo.INFO);
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);
      }
   }

   public void testCtorEmptyName() throws Exception
   {
      try
      {
         OpenMBeanOperationInfoSupport info =
                 new OpenMBeanOperationInfoSupport("",
                                                   "compute the exchange rate for two currencies",
                                                   signature,
                                                   SimpleType.FLOAT,
                                                   MBeanOperationInfo.INFO);
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);
      }
   }

   public void testCtorNullDescription() throws Exception
   {
      try
      {
         OpenMBeanOperationInfoSupport info =
                 new OpenMBeanOperationInfoSupport("exchangeRate",
                                                   null,
                                                   signature,
                                                   SimpleType.FLOAT,
                                                   MBeanOperationInfo.INFO);
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);
      }
   }

   public void testCtorEmptyDescription() throws Exception
   {
      try
      {
         OpenMBeanOperationInfoSupport info =
                 new OpenMBeanOperationInfoSupport("exchangeRate",
                                                   "",
                                                   signature,
                                                   SimpleType.FLOAT,
                                                   MBeanOperationInfo.INFO);
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);
      }
   }

   public void testCtorNullSignature() throws Exception
   {
      OpenMBeanOperationInfoSupport info =
              new OpenMBeanOperationInfoSupport("exchangeRate",
                                                "compute the exchange rate for two currencies",
                                                null,
                                                SimpleType.FLOAT,
                                                MBeanOperationInfo.INFO);
      assertTrue("Null info constructed", info != null);
      assertTrue("Unexpected name",
                 info.getName().compareTo("exchangeRate") == 0);
      assertTrue("Unexpected description",
                 info.getDescription().compareTo("compute the exchange rate for two currencies")
                 == 0);
      assertTrue("Unexpected return open type",
                 info.getReturnOpenType().equals(SimpleType.FLOAT));
      assertTrue("Unexpected signature",
                 Arrays.equals(info.getSignature(), new OpenMBeanParameterInfo[0]));
      assertTrue("Unexpected impact",
                 info.getImpact() == MBeanOperationInfo.INFO);
   }

   public void testCtorEmptySignature() throws Exception
   {
      OpenMBeanOperationInfoSupport info =
              new OpenMBeanOperationInfoSupport("exchangeRate",
                                                "compute the exchange rate for two currencies",
                                                new OpenMBeanParameterInfo[0],
                                                SimpleType.FLOAT,
                                                MBeanOperationInfo.INFO);
      assertTrue("Null info constructed", info != null);
      assertTrue("Unexpected name",
                 info.getName().compareTo("exchangeRate") == 0);
      assertTrue("Unexpected description",
                 info.getDescription().compareTo("compute the exchange rate for two currencies")
                 == 0);
      assertTrue("Unexpected return open type",
                 info.getReturnOpenType().equals(SimpleType.FLOAT));
      assertTrue("Unexpected signature",
                 Arrays.equals(info.getSignature(), new OpenMBeanParameterInfo[0]));
      assertTrue("Unexpected impact",
                 info.getImpact() == MBeanOperationInfo.INFO);
   }

   public void testCtorNullReturnOpenType() throws Exception
   {
      try
      {
         OpenMBeanOperationInfoSupport info =
                 new OpenMBeanOperationInfoSupport("exchangeRate",
                                                   "compute the exchange rate for two currencies",
                                                   signature,
                                                   null,
                                                   MBeanOperationInfo.INFO);
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);
      }
   }

   public void testCtorBogusImpact() throws Exception
   {
      try
      {
         OpenMBeanOperationInfoSupport info =
                 new OpenMBeanOperationInfoSupport("exchangeRate",
                                                   "compute the exchange rate for two currencies",
                                                   signature,
                                                   SimpleType.FLOAT,
                                                   42);
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);
      }
   }

   public void testEquals() throws Exception
   {

      OpenMBeanOperationInfoSupport o1 =
              new OpenMBeanOperationInfoSupport("hello", "Say Hello", params, SimpleType.STRING, 1);
      OpenMBeanOperationInfoSupport o2 =
              new OpenMBeanOperationInfoSupport("hello", "Say Hello", params, SimpleType.STRING, 1);
      OpenMBeanOperationInfoSupport o3 =
              new OpenMBeanOperationInfoSupport("hello", "Say Hello", params, SimpleType.STRING, 0);
      OpenMBeanOperationInfoSupport o4 =
              new OpenMBeanOperationInfoSupport("goAway", "Go Away", params, SimpleType.STRING, 1);
      OpenMBeanOperationInfoSupport o5 =
              new OpenMBeanOperationInfoSupport("goAway", "Hey There", params, SimpleType.STRING, 1);
      OpenMBeanOperationInfoSupport o6 =
              new OpenMBeanOperationInfoSupport("goAway", "Hey There", params, SimpleType.INTEGER, 1);



      //test
      assertTrue(!o1.equals(null));
      assertTrue(o1.equals(o2));
      assertEquals(o1.hashCode(), o2.hashCode());
      assertTrue(!o1.equals(o3));
      assertTrue(o4.equals(o5));
      assertTrue(!o5.equals(o6));


   }

   public void testHashCode() throws Exception
   {
      OpenMBeanOperationInfoSupport infoone =
              new OpenMBeanOperationInfoSupport("convertPrice",
                                                "converts the price from one currency to another",
                                                signature,
                                                SimpleType.FLOAT,
                                                MBeanOperationInfo.ACTION);
      assertTrue("Unexpected hash code", infoone.hashCode() == hashCode(infoone));

      OpenMBeanOperationInfoSupport infotwo =
              new OpenMBeanOperationInfoSupport("convertPrice",
                                                "multiply Currency by exchange rate to get TargetCurrency price",
                                                signature,
                                                SimpleType.FLOAT,
                                                MBeanOperationInfo.ACTION);
      assertTrue("Unexpected hash code", infotwo.hashCode() == hashCode(infoone));
   }

   private int hashCode(OpenMBeanOperationInfo info)
   {
      int result = info.getName().hashCode();
      result += info.getReturnOpenType().hashCode();
      result += info.getImpact();
      result += java.util.Arrays.asList(info.getSignature()).hashCode();
      return result;
   }

}
