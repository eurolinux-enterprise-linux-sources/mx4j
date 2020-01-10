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

import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import junit.framework.TestCase;


public class ArrayTypeTest extends TestCase
{
   public ArrayTypeTest(String s)
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

   public void testIsValue() throws Exception
   {
      ArrayType simple = new ArrayType(1, SimpleType.STRING);

      String[] stringarray = {"do", "re", "mi"};
      assertTrue("Expecting equality for array of strings", simple.isValue(stringarray));
      assertFalse("Expecting inequality for array of ints", simple.isValue(new int[]{1, 2, 3, 4}));
      assertFalse("Expecting inequality for null", simple.isValue(null));
      assertFalse("Expecting inequality for string", simple.isValue("fa"));

      String[] items = {"type", "winery", "vintage"};
      String[] descriptions = {"Type of wine", "Wine producer", "Year produced"};
      OpenType[] types = {SimpleType.STRING, SimpleType.STRING, SimpleType.INTEGER};
      CompositeType wine = new CompositeType("wine", "nectar of the gods", items, descriptions, types);

      items = new String[]{"type", "brewery"};
      descriptions = new String[]{"Type of beer", "Beer producer"};
      types = new OpenType[]{SimpleType.STRING, SimpleType.STRING};
      CompositeType beer = new CompositeType("beer", "a meal in a glass", items, descriptions, types);

      ArrayType composite = new ArrayType(1, wine);

      CompositeDataSupport amarone =
              new CompositeDataSupport(wine,
                                       new String[]{"type", "winery", "vintage"},
                                       new Object[]{"red", "Allegrini", new Integer(1996)});
      CompositeDataSupport orvieto =
              new CompositeDataSupport(wine,
                                       new String[]{"type", "winery", "vintage"},
                                       new Object[]{"white", "Ruffino", new Integer(2002)});
      CompositeData[] winecellar = {amarone, orvieto};
      CompositeData[] sparsewines = {amarone, null, orvieto};

      CompositeDataSupport stout =
              new CompositeDataSupport(beer,
                                       new String[]{"type", "brewery"},
                                       new Object[]{"stout", "Guiness"});
      CompositeData[] beerlist = {stout};

      CompositeData[] mixer = {amarone, stout, orvieto};

      assertTrue("Expecting equality for array of wines", composite.isValue(winecellar));
      assertTrue("Expecting equality for sparse array of wines", composite.isValue(sparsewines));
      assertFalse("Expecting inequality for array of beer", composite.isValue(beerlist));
      assertFalse("Expecting inequality for mixed array", composite.isValue(mixer));
      assertFalse("Expecting inequality for single wine", composite.isValue(orvieto));

      ArrayType winematrix = new ArrayType(2, wine);

      CompositeData[][] matrix = {{amarone, orvieto}, {orvieto, amarone}};
      assertTrue("Expecting equality for wine matrix", winematrix.isValue(matrix));
      assertFalse("Expecting inequality for wine vector", winematrix.isValue(winecellar));

      winematrix = new ArrayType(2, wine);
      CompositeData[][] matrix2 = new CompositeData[1][1];
      assertTrue("Expecting equality for wine matrix", winematrix.isValue(matrix2));
   }

   public void testEquals() throws Exception
   {
      ArrayType a1 = new ArrayType(1, SimpleType.LONG);
      ArrayType a2 = new ArrayType(1, SimpleType.LONG);
      ArrayType a3 = new ArrayType(2, SimpleType.LONG);
      ArrayType a4 = new ArrayType(2, SimpleType.STRING);

      assertTrue(a1.equals(a2));
      assertTrue(!a1.equals(a3));
      assertTrue(!a1.equals(a4));


   }

   public void testCompositeEquals() throws Exception
   {
      String[] items = {"type", "winery", "vintage"};
      String[] descriptions = {"Type of wine", "Wine producer", "Year produced"};
      OpenType[] types = {SimpleType.STRING, SimpleType.STRING, SimpleType.DATE};
      CompositeType californiaWine = new CompositeType("California", "Vino", items, descriptions, types);
      ArrayType aone = new ArrayType(2, californiaWine);
      ArrayType atwo = new ArrayType(2, californiaWine);
      assertTrue("Expecting equality for identical composite types", aone.equals(atwo));

      CompositeType italianWine = new CompositeType("Italia", "Vino", items, descriptions, types);
      atwo = new ArrayType(2, italianWine);
      assertFalse("Expecting inequality for different composite types", aone.equals(atwo));
   }

   public void testClassName() throws Exception
   {
      ArrayType a1 = new ArrayType(2, SimpleType.STRING);
      assertEquals(a1.getClassName(), "[[Ljava.lang.String;");
      assertEquals(a1.getTypeName(), "[[Ljava.lang.String;");

   }

   public void testHashCode() throws Exception
   {


      ArrayType a1 = new ArrayType(2, SimpleType.STRING);
      ArrayType a2 = new ArrayType(2, SimpleType.STRING);

      assertTrue(a1.hashCode() == a2.hashCode());

   }

   public void testBadSize() throws Exception
   {
      try
      {
         ArrayType t = new ArrayType(0, SimpleType.STRING);
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true); // success
      }
   }
}
