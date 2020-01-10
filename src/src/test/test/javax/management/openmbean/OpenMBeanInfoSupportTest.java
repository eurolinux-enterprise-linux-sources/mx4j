/**
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.openmbean;

import java.util.Arrays;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.OpenMBeanAttributeInfo;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanConstructorInfo;
import javax.management.openmbean.OpenMBeanConstructorInfoSupport;
import javax.management.openmbean.OpenMBeanInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfo;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.openmbean.SimpleType;

import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 */
public class OpenMBeanInfoSupportTest extends TestCase
{
   private OpenMBeanAttributeInfo[] attrs;
   private OpenMBeanConstructorInfo[] ctors;
   private OpenMBeanOperationInfo[] ops;
   private MBeanNotificationInfo[] notifs;

   public static void main(String[] args)
   {
      TestRunner.run(OpenMBeanInfoSupportTest.class);
   }

   public void testCtor() throws Exception
   {
      OpenMBeanInfoSupport info =
              new OpenMBeanInfoSupport("CurrencyMBean",
                                       "Does stuff with currencies",
                                       attrs,
                                       ctors,
                                       ops,
                                       notifs);
      assertTrue("Null info constructed", info != null);
      assertTrue("Unexpected name",
                 info.getClassName().compareTo("CurrencyMBean") == 0);
      assertTrue("Unexpected description",
                 info.getDescription().compareTo("Does stuff with currencies") == 0);
      assertTrue("Unexpected attributes",
                 Arrays.equals(info.getAttributes(), attrs));
      assertTrue("Unexpected constructors",
                 Arrays.equals(info.getConstructors(), ctors));
      assertTrue("Unexpected operations",
                 Arrays.equals(info.getOperations(), ops));
      assertTrue("Unexpected notifications",
                 Arrays.equals(info.getNotifications(), notifs));
   }

   public void testEquals() throws Exception
   {
      OpenMBeanInfoSupport infoone =
              new OpenMBeanInfoSupport("CurrencyMBean",
                                       "Does stuff with currencies",
                                       attrs,
                                       ctors,
                                       ops,
                                       notifs);
      assertTrue("Null info constructed", infoone != null);

      OpenMBeanInfoSupport infotwo =
              new OpenMBeanInfoSupport("CurrencyMBean",
                                       "manages currencies",
                                       attrs,
                                       ctors,
                                       ops,
                                       notifs);
      assertTrue("Null info constructed", infotwo != null);

      assertTrue("Expecting equality (only description is different)",
                 infotwo.equals(infoone));

      OpenMBeanAttributeInfo[] testattrs =
              new OpenMBeanAttributeInfoSupport[]{
                 new OpenMBeanAttributeInfoSupport("Uptime",
                                                   "how long it's been running",
                                                   SimpleType.FLOAT,
                                                   true,
                                                   false,
                                                   false),
                 new OpenMBeanAttributeInfoSupport("Exchanges",
                                                   "number of exchanges completed",
                                                   SimpleType.FLOAT,
                                                   true,
                                                   false,
                                                   false)};

      OpenMBeanConstructorInfo[] testctors =
              new OpenMBeanConstructorInfoSupport[]{
                 new OpenMBeanConstructorInfoSupport("CurrencyMBean",
                                                     "create a currency mbean",
                                                     new OpenMBeanParameterInfoSupport[]{
                                                        new OpenMBeanParameterInfoSupport("currencies",
                                                                                          "currencies that may be exchanged",
                                                                                          new ArrayType(1, SimpleType.STRING))})
              };

      OpenMBeanParameterInfo[] ratesig =
              new OpenMBeanParameterInfo[]{
                 new OpenMBeanParameterInfoSupport("to",
                                                   "currency to convert to",
                                                   SimpleType.STRING),
                 new OpenMBeanParameterInfoSupport("from",
                                                   "currency to convert from",
                                                   SimpleType.STRING)};

      OpenMBeanParameterInfo[] exsig =
              new OpenMBeanParameterInfo[]{
                 new OpenMBeanParameterInfoSupport("from",
                                                   "currency to convert from",
                                                   SimpleType.STRING),
                 new OpenMBeanParameterInfoSupport("amount",
                                                   "amount of currency to convert",
                                                   SimpleType.FLOAT),
                 new OpenMBeanParameterInfoSupport("to",
                                                   "currency to convert to",
                                                   SimpleType.STRING)};

      OpenMBeanOperationInfo[] testops =
              new OpenMBeanOperationInfoSupport[]{
                 new OpenMBeanOperationInfoSupport("exchangeRate",
                                                   "compute the exchange rate for two currencies",
                                                   ratesig,
                                                   SimpleType.FLOAT,
                                                   MBeanOperationInfo.INFO),
                 new OpenMBeanOperationInfoSupport("exchange",
                                                   "compute the exchange rate for two currencies",
                                                   exsig,
                                                   SimpleType.FLOAT,
                                                   MBeanOperationInfo.INFO)};

      OpenMBeanInfoSupport infothree =
              new OpenMBeanInfoSupport("CurrencyMBean",
                                       "Does stuff with currencies",
                                       testattrs,
                                       testctors,
                                       testops,
                                       notifs);
      assertTrue("Null info constructed", infoone != null);

      assertFalse("Expecting inequality (the parameter orders are different)",
                  infothree.equals(infoone));
   }

   public void testHashCode() throws Exception
   {
      OpenMBeanInfoSupport infoone =
              new OpenMBeanInfoSupport("CurrencyMBean",
                                       "Does stuff with currencies",
                                       attrs,
                                       ctors,
                                       ops,
                                       notifs);
      assertTrue("Null info constructed", infoone != null);

      OpenMBeanInfoSupport infotwo =
              new OpenMBeanInfoSupport("CurrencyMBean",
                                       "manages currencies",
                                       attrs,
                                       ctors,
                                       ops,
                                       notifs);
      assertTrue("Null info constructed", infotwo != null);

      assertTrue("Expecting identical hash codes (only description is different)",
                 infotwo.hashCode() == infoone.hashCode());

      OpenMBeanAttributeInfo[] testattrs =
              new OpenMBeanAttributeInfoSupport[]{
                 new OpenMBeanAttributeInfoSupport("Uptime",
                                                   "how long it's been running",
                                                   SimpleType.FLOAT,
                                                   true,
                                                   false,
                                                   false),
                 new OpenMBeanAttributeInfoSupport("Exchanges",
                                                   "number of exchanges completed",
                                                   SimpleType.FLOAT,
                                                   true,
                                                   false,
                                                   false)};

      OpenMBeanConstructorInfo[] testctors =
              new OpenMBeanConstructorInfoSupport[]{
                 new OpenMBeanConstructorInfoSupport("CurrencyMBean",
                                                     "create a currency mbean",
                                                     new OpenMBeanParameterInfoSupport[]{
                                                        new OpenMBeanParameterInfoSupport("currencies",
                                                                                          "currencies that may be exchanged",
                                                                                          new ArrayType(1, SimpleType.STRING))})
              };

      OpenMBeanParameterInfo[] ratesig =
              new OpenMBeanParameterInfo[]{
                 new OpenMBeanParameterInfoSupport("to",
                                                   "currency to convert to",
                                                   SimpleType.STRING),
                 new OpenMBeanParameterInfoSupport("from",
                                                   "currency to convert from",
                                                   SimpleType.STRING)};

      OpenMBeanParameterInfo[] exsig =
              new OpenMBeanParameterInfo[]{
                 new OpenMBeanParameterInfoSupport("from",
                                                   "currency to convert from",
                                                   SimpleType.STRING),
                 new OpenMBeanParameterInfoSupport("amount",
                                                   "amount of currency to convert",
                                                   SimpleType.FLOAT),
                 new OpenMBeanParameterInfoSupport("to",
                                                   "currency to convert to",
                                                   SimpleType.STRING)};

      OpenMBeanOperationInfo[] testops =
              new OpenMBeanOperationInfoSupport[]{
                 new OpenMBeanOperationInfoSupport("exchangeRate",
                                                   "compute the exchange rate for two currencies",
                                                   ratesig,
                                                   SimpleType.FLOAT,
                                                   MBeanOperationInfo.INFO),
                 new OpenMBeanOperationInfoSupport("exchange",
                                                   "compute the exchange rate for two currencies",
                                                   exsig,
                                                   SimpleType.FLOAT,
                                                   MBeanOperationInfo.INFO)};

      OpenMBeanInfoSupport infothree =
              new OpenMBeanInfoSupport("CurrencyMBean",
                                       "Does stuff with currencies",
                                       testattrs,
                                       testctors,
                                       testops,
                                       notifs);
      assertTrue("Null info constructed", infoone != null);

      assertFalse("Expecting different hash codes (the parameter orders are different)",
                  infothree.hashCode() == infoone.hashCode());
   }

   protected void setUp() throws Exception
   {
      attrs =
      new OpenMBeanAttributeInfoSupport[]{
         new OpenMBeanAttributeInfoSupport("Exchanges",
                                           "number of exchanges completed",
                                           SimpleType.FLOAT,
                                           true,
                                           false,
                                           false),
         new OpenMBeanAttributeInfoSupport("Uptime",
                                           "how long it's been running",
                                           SimpleType.FLOAT,
                                           true,
                                           false,
                                           false)};

      ctors =
      new OpenMBeanConstructorInfoSupport[]{
         new OpenMBeanConstructorInfoSupport("CurrencyMBean",
                                             "create a currency mbean",
                                             new OpenMBeanParameterInfoSupport[]{
                                                new OpenMBeanParameterInfoSupport("currencies",
                                                                                  "currencies that may be exchanged",
                                                                                  new ArrayType(1, SimpleType.STRING))})
      };

      OpenMBeanParameterInfo[] ratesig =
              new OpenMBeanParameterInfo[]{
                 new OpenMBeanParameterInfoSupport("from",
                                                   "currency to convert from",
                                                   SimpleType.STRING),
                 new OpenMBeanParameterInfoSupport("to",
                                                   "currency to convert to",
                                                   SimpleType.STRING)};

      OpenMBeanParameterInfo[] exsig =
              new OpenMBeanParameterInfo[]{
                 new OpenMBeanParameterInfoSupport("from",
                                                   "currency to convert from",
                                                   SimpleType.STRING),
                 new OpenMBeanParameterInfoSupport("to",
                                                   "currency to convert to",
                                                   SimpleType.STRING),
                 new OpenMBeanParameterInfoSupport("amount",
                                                   "amount of currency to convert",
                                                   SimpleType.FLOAT)};

      ops =
      new OpenMBeanOperationInfoSupport[]{
         new OpenMBeanOperationInfoSupport("exchangeRate",
                                           "compute the exchange rate for two currencies",
                                           ratesig,
                                           SimpleType.FLOAT,
                                           MBeanOperationInfo.INFO),
         new OpenMBeanOperationInfoSupport("exchange",
                                           "compute the exchange rate for two currencies",
                                           exsig,
                                           SimpleType.FLOAT,
                                           MBeanOperationInfo.INFO)};

      notifs = new MBeanNotificationInfo[0];
   }

   protected void tearDown()
   {
   }
}
