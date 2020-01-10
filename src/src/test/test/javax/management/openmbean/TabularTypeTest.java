/**
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.openmbean;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularType;

import test.MX4JTestCase;

/**
 * @version $Revision: 1.8 $
 */
public class TabularTypeTest extends MX4JTestCase
{
   private String[] itemNames = null;
   private String[] itemDescriptions = null;
   private OpenType[] itemTypes;
   private String[] indexNames;
   private CompositeType tShirtType;
   private CompositeType wine;
   private TabularType allTShirtTypes;

   public TabularTypeTest(String s)
   {
      super(s);
   }

   protected void setUp() throws Exception
   {
      itemNames = new String[]{"model", "color", "size", "price"};
      itemDescriptions = new String[]{"TShirt's model name", "TShirt's color", "TShirt's size", "TShirt's price"};
      itemTypes = new OpenType[]{SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.FLOAT};
      indexNames = new String[]{"model", "color", "size"};
      tShirtType = new CompositeType("tShirt", "a TShirt", itemNames, itemDescriptions, itemTypes);

      allTShirtTypes = new TabularType("tShirts", "List of available TShirts", tShirtType, indexNames);

      wine =
      new CompositeType("Wine",
                        "Nectar of the gods",
                        new String[]{"Type", "Winery", "Vintage"},
                        new String[]{
                           "Red, White, Rose, etc.",
                           "The wine's producer",
                           "The year the wine was made"},
                        new OpenType[]{
                           SimpleType.STRING,
                           SimpleType.STRING,
                           SimpleType.INTEGER});
   }

   public void testEquals() throws Exception
   {
      String[] itemNames2 = {"model", "color", "size", "price"};
      String[] itemDescriptions2 = {"Manufacturer's model name", "The shirt's color", "How big is it", "How much does it cost"};
      OpenType[] itemTypes2 = {SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.FLOAT};
      String[] indexNames2 = {"model", "color", "size"};
      CompositeType tShirtType2 = new CompositeType("tShirt", "a TShirt", itemNames2, itemDescriptions2, itemTypes2);
      TabularType allTShirtTypes2 = new TabularType("tShirts", "List of available TShirts", tShirtType2, indexNames2);
      assertTrue(allTShirtTypes.equals(allTShirtTypes2));
      // assert reverse
      assertTrue(allTShirtTypes2.equals(allTShirtTypes));
   }

   public void testNotEqual() throws Exception
   {
      String[] itemNames3 = {"model3", "color3", "size3", "price3"};
      String[] itemDescriptions3 = {"TShirt's model name", "TShirt's color", "TShirt's size", "TShirt's price"};
      OpenType[] itemTypes3 = {SimpleType.STRING, SimpleType.BOOLEAN, SimpleType.STRING, SimpleType.FLOAT};
      String[] indexNames3 = {"model3", "color3", "size3"};
      CompositeType tShirtType3 = new CompositeType("tShirt3", "a TShirt", itemNames3, itemDescriptions3, itemTypes3);
      TabularType allTShirtTypes3 = new TabularType("tShirts3", "List of available TShirts", tShirtType3, indexNames3);
      assertFalse(allTShirtTypes.equals(allTShirtTypes3));
   }

   public void testGetRowType()
   {
      CompositeType test = allTShirtTypes.getRowType();
      assertEquals(test, tShirtType);
   }

   public void testGetIndexNames()
   {
      List temp = allTShirtTypes.getIndexNames();
      String[] tempList = (String[])temp.toArray(new String[temp.size()]);

      assertEquals(tempList.length, indexNames.length);
      // assert all elements are the same
      for (int i = 0; i < tempList.length; i++)
      {
         assertTrue(tempList[i] == indexNames[i]);
      }
   }

   public void testHashCode() throws Exception
   {
      String[] itemNames2 = {"model", "color", "size", "price"};
      String[] itemDescriptions2 = {"Manufacturer's model name", "The shirt's color", "How big is it", "How much does it cost"};
      OpenType[] itemTypes2 = {SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.FLOAT};
      String[] indexNames2 = {"model", "color", "size"};
      CompositeType tShirtType2 = new CompositeType("tShirt", "a TShirt", itemNames2, itemDescriptions2, itemTypes2);
      TabularType allTShirtTypes2 = new TabularType("tShirts", "List of available TShirts", tShirtType2, indexNames2);

      String[] itemNames3 = {"model3", "color3", "size3", "price3"};
      String[] itemDescriptions3 = {"TShirt's model name", "TShirt's color", "TShirt's size", "TShirt's price"};
      OpenType[] itemTypes3 = {SimpleType.STRING, SimpleType.BOOLEAN, SimpleType.STRING, SimpleType.FLOAT};
      String[] indexNames3 = {"model3", "color3", "size3"};
      CompositeType tShirtType3 = new CompositeType("tShirt3", "a TShirt", itemNames3, itemDescriptions3, itemTypes3);
      TabularType allTShirtTypes3 = new TabularType("tShirts3", "List of available TShirts", tShirtType3, indexNames3);

      int code1 = allTShirtTypes.hashCode();
      int code2 = allTShirtTypes2.hashCode();
      assertEquals(code2, "tShirts".hashCode() + tShirtType2.hashCode() + (new HashSet(Arrays.asList(indexNames))).hashCode());
      int code3 = allTShirtTypes3.hashCode();

      assertEquals(code1, code2);
      assertTrue(code1 != code3);
   }

   public void testCtor() throws Exception
   {
      TabularType winecatalog =
              new TabularType("Wine Catalog",
                              "Catalog of available wines",
                              wine,
                              new String[]{"Winery", "Vintage"});
      assertTrue("Unexpected name",
                 winecatalog.getTypeName().compareTo("Wine Catalog") == 0);
      assertTrue("Unexpected CompositeType",
                 winecatalog.getRowType().equals(wine));
      List index = winecatalog.getIndexNames();
      assertTrue("Incorrect index size", index.size() == 2);
      assertTrue("Unexpected index entries",
                 ((String)index.get(0)).compareTo("Winery") == 0
                 && ((String)index.get(1)).compareTo("Vintage") == 0);
   }

   public void testCtorNullName() throws Exception
   {
      try
      {
         new TabularType(null,
                         "Catalog of available wines",
                         wine,
                         new String[]{"Winery", "Vintage"});
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
      }
   }

   public void testCtorEmptyName() throws Exception
   {
      try
      {
         new TabularType("",
                         "Catalog of available wines",
                         wine,
                         new String[]{"Winery", "Vintage"});
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
      }
   }

   public void testCtorNullDescriptor() throws Exception
   {
      try
      {
         new TabularType("Wine Catalog",
                         null,
                         wine,
                         new String[]{"Winery", "Vintage"});
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
      }
   }

   public void testCtorEmptyDescriptor() throws Exception
   {
      try
      {
         new TabularType("Wine Catalog",
                         "",
                         wine,
                         new String[]{"Winery", "Vintage"});
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
      }
   }

   public void testCtorNullRowType() throws Exception
   {
      try
      {
         new TabularType("Wine Catalog",
                         "Catalog of available wines",
                         null,
                         new String[]{"Winery", "Vintage"});
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
      }
   }

   public void testCtorNullIndex() throws Exception
   {
      try
      {
         new TabularType("Wine Catalog",
                         "Catalog of available wines",
                         wine,
                         null);
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
      }
   }

   public void testCtorEmptyIndex() throws Exception
   {
      try
      {
         new TabularType("Wine Catalog",
                         "Catalog of available wines",
                         wine,
                         new String[0]);
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
      }
   }

   public void testCtorBogusIndex() throws Exception
   {
      try
      {
         new TabularType("Wine Catalog",
                         "Catalog of available wines",
                         wine,
                         new String[]{"Region", "Vintage"});
         fail("Expecting OpenDataException");
      }
      catch (OpenDataException x)
      {
      }
   }
}

