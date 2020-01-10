/**
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.openmbean;

import java.util.Arrays;
import java.util.Set;
import javax.management.openmbean.OpenMBeanConstructorInfo;
import javax.management.openmbean.OpenMBeanConstructorInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import junit.framework.TestCase;
import junit.textui.TestRunner;

/**
 */
public class OpenMBeanConstructorInfoSupportTest extends TestCase
{
   private static class MyMBeanParameterInfo implements OpenMBeanParameterInfo
   {
      public boolean equals(Object o)
      {
         return false;
      }

      public Object getDefaultValue()
      {
         return null;
      }

      public String getDescription()
      {
         return null;
      }

      public Set getLegalValues()
      {
         return null;
      }

      public Comparable getMaxValue()
      {
         return null;
      }

      public Comparable getMinValue()
      {
         return null;
      }

      public String getName()
      {
         return null;
      }

      public OpenType getOpenType()
      {
         return null;
      }

      public boolean hasDefaultValue()
      {
         return false;
      }

      public boolean hasLegalValues()
      {
         return false;
      }

      public boolean hasMinValue()
      {
         return false;
      }

      public boolean hasMaxValue()
      {
         return false;
      }

      public boolean isValue(Object o)
      {
         return false;
      }

      public String toString()
      {
         return null;
      }
   }

   private OpenMBeanParameterInfo[] signature;

   public static void main(String[] args)
   {
      TestRunner.run(OpenMBeanConstructorInfoSupportTest.class);
   }

   public void testCtor() throws Exception
   {
      OpenMBeanConstructorInfoSupport info =
              new OpenMBeanConstructorInfoSupport("wine",
                                                  "Non-default constructor",
                                                  signature);
      assertTrue("Null info constructed", info != null);
      assertTrue("Unexpected name", info.getName().compareTo("wine") == 0);
      assertTrue("Unexpected description",
                 info.getDescription().compareTo("Non-default constructor") == 0);
      assertTrue("Unexpected signature",
                 Arrays.equals(info.getSignature(), signature));

      info =
      new OpenMBeanConstructorInfoSupport("wine",
                                          "Non-default constructor",
                                          null);
      assertTrue("Null info constructed", info != null);
      assertTrue("Unexpected name", info.getName().compareTo("wine") == 0);
      assertTrue("Unexpected description",
                 info.getDescription().compareTo("Non-default constructor") == 0);
      assertTrue("Unexpected signature", info.getSignature() == null || info.getSignature().length == 0);

      info =
      new OpenMBeanConstructorInfoSupport("wine",
                                          "Non-default constructor",
                                          new OpenMBeanParameterInfoSupport[0]);
      assertTrue("Null info constructed", info != null);
      assertTrue("Unexpected name", info.getName().compareTo("wine") == 0);
      assertTrue("Unexpected description",
                 info.getDescription().compareTo("Non-default constructor") == 0);
      assertTrue("Unexpected signature", info.getSignature().length == 0);
   }

   public void testCtorNullName() throws Exception
   {
      try
      {
         OpenMBeanConstructorInfoSupport info =
                 new OpenMBeanConstructorInfoSupport(null,
                                                     "Non-default constructor",
                                                     signature);
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
         OpenMBeanConstructorInfoSupport info =
                 new OpenMBeanConstructorInfoSupport("",
                                                     "Non-default constructor",
                                                     signature);
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
         OpenMBeanConstructorInfoSupport info =
                 new OpenMBeanConstructorInfoSupport("wine", null, signature);
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
         OpenMBeanConstructorInfoSupport info =
                 new OpenMBeanConstructorInfoSupport("wine", "", signature);
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);
      }
   }

   public void testCtorBogusSignature() throws Exception
   {
      try
      {
         MyMBeanParameterInfo[] bogusig =
                 {
                    new MyMBeanParameterInfo(),
                    new MyMBeanParameterInfo(),
                    new MyMBeanParameterInfo()};

         OpenMBeanConstructorInfoSupport info =
                 new OpenMBeanConstructorInfoSupport("wine",
                                                     "Non-default constructor",
                                                     bogusig);
         fail("Expecting ArrayStoreException");
      }
      catch (ArrayStoreException x)
      {
         assertTrue(true);
      }
   }

   public void testEquals() throws Exception
   {
      OpenMBeanConstructorInfo infoone =
              new OpenMBeanConstructorInfoSupport("wine", "Vino", signature);
      assertTrue("Null infoone constructed", infoone != null);

      OpenMBeanConstructorInfo infotwo =
              new OpenMBeanConstructorInfoSupport("wine",
                                                  "Nectar of the gods",
                                                  signature);
      assertTrue("Null infotwo constructed", infotwo != null);

      assertTrue("Expected equality", infoone.equals(infotwo));

      OpenMBeanConstructorInfo infothree =
              new OpenMBeanConstructorInfoSupport("Vino", "Vino", signature);
      assertTrue("Null infothree constructed", infothree != null);

      assertFalse("Expected inequality based on name",
                  infothree.equals(infoone));

      OpenMBeanConstructorInfo infofour =
              new OpenMBeanConstructorInfoSupport("wine",
                                                  "Vino",
                                                  new OpenMBeanParameterInfoSupport[0]);
      assertTrue("Null infofour constructed", infofour != null);

      assertFalse("Expected inequality based on signature",
                  infofour.equals(infoone));
   }

   public void testHashCode() throws Exception
   {
      OpenMBeanConstructorInfo infoone =
              new OpenMBeanConstructorInfoSupport("wine", "Vino", signature);
      assertTrue("Null infoone constructed", infoone != null);

      assertTrue("Unexpected hash code",
                 infoone.hashCode() == hashCode(infoone));

      OpenMBeanConstructorInfo infotwo =
              new OpenMBeanConstructorInfoSupport("wine",
                                                  "Nectar of the gods",
                                                  signature);
      assertTrue("Null infotwo constructed", infotwo != null);

      assertTrue("Expecting equal hash codes",
                 infoone.hashCode() == infotwo.hashCode());
   }

   protected void setUp()
   {
      try
      {
         signature =
         new OpenMBeanParameterInfoSupport[]{
            new OpenMBeanParameterInfoSupport("type",
                                              "type of wine",
                                              SimpleType.STRING,
                                              "Red",
                                              new String[]{"Red", "White", "Rose"}),
            new OpenMBeanParameterInfoSupport("winery",
                                              "who produced the wine",
                                              SimpleType.STRING),
            new OpenMBeanParameterInfoSupport("vintage",
                                              "when the wine was produced",
                                              SimpleType.INTEGER,
                                              null,
                                              new Integer(1900),
                                              new Integer(2000))
         };
      }
      catch (Exception x)
      {
         fail(x.toString());
      }
   }

   protected void tearDown()
   {
   }

   private int hashCode(OpenMBeanConstructorInfo info)
   {
      int result = info.getName().hashCode();
      result += Arrays.asList(info.getSignature()).hashCode();
      return result;
   }
}
