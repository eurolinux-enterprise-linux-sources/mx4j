/**
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */
package test.javax.management.openmbean;

import java.io.IOException;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import junit.framework.TestCase;
import test.javax.management.compliance.serialization.support.Serializer;

/**
 * @version $Revision: 1.12 $
 */
public class CompositeTypeTest extends TestCase
{
   private String[] itemNames = null;
   private String[] itemDescriptions = null;
   private OpenType[] itemTypes;
   private String[] indexNames;
   private CompositeType tShirtType;

   public CompositeTypeTest(String s)
   {
      super(s);
   }

   protected void setUp() throws Exception
   {
      super.setUp();
      itemNames = new String[]{"model", "color", "size", "price"};
      itemDescriptions = new String[]{"TShirt's model name", "TShirt's color", "TShirt's size", "TShirt's price"};
      itemTypes = new OpenType[]{SimpleType.STRING, SimpleType.STRING, SimpleType.STRING, SimpleType.FLOAT};
      indexNames = new String[]{"model", "color", "size"};
      tShirtType = new CompositeType("tShirt", "a TShirt", itemNames, itemDescriptions, itemTypes);
   }

   protected void tearDown() throws Exception
   {
      super.tearDown();
   }

   public void testCreation()
   {
      assertTrue(tShirtType != null);
   }

   public void testInvalidCreation()
   {
      try
      {
         // duplicate names
         itemNames = new String[]{"model", "color", "size", "model"};
         tShirtType = new CompositeType("tShirt", "a TShirt", itemNames, itemDescriptions, itemTypes);
         fail("Expect exception, invalid itemDescriptions (not the same size as itemNames - we should not see this");
      }
      catch (OpenDataException e)
      {
      }

      try
      {
         // empty typeName
         String[] itemDescriptions = new String[]{"TShirt's model name", "TShirt's color", "TShirt's size", "TShirt's price"};
         tShirtType = new CompositeType("", "a TShirt", itemNames, itemDescriptions, itemTypes);
         fail("No exception thrown");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);	// success
      }
      catch (Exception x)
      {
         fail("Expecting IllegalArgumentException");
      }

      try
      {
         // null typeName
         String[] itemDescriptions = new String[]{"TShirt's model name", "TShirt's color", "TShirt's size", "TShirt's price"};
         tShirtType = new CompositeType(null, "a TShirt", itemNames, itemDescriptions, itemTypes);
         fail("No exception thrown");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);	// success
      }
      catch (Exception x)
      {
         fail("Expecting IllegalArgumentException");
      }

      try
      {
         // empty description
         String[] itemDescriptions = new String[]{"TShirt's model name", "TShirt's color", "TShirt's size", "TShirt's price"};
         tShirtType = new CompositeType("tShirt", "", itemNames, itemDescriptions, itemTypes);
         fail("No exception thrown");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);	// success
      }
      catch (Exception x)
      {
         fail("Expecting IllegalArgumentException");
      }

      try
      {
         // null description
         String[] itemDescriptions = new String[]{"TShirt's model name", "TShirt's color", "TShirt's size", "TShirt's price"};
         tShirtType = new CompositeType("tShirt", null, itemNames, itemDescriptions, itemTypes);
         fail("No exception thrown");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);	// success
      }
      catch (Exception x)
      {
         fail("Expecting IllegalArgumentException");
      }

      try
      {
         // null itemName entry
         String[] itemDescriptions = new String[]{"TShirt's model name", "TShirt's color", "TShirt's size", "TShirt's price"};
         itemNames = new String[]{"model", null, "size", "price"};
         tShirtType = new CompositeType("tShirt", "", itemNames, itemDescriptions, itemTypes);
         fail("No exception thrown");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);	// success
      }
      catch (Exception x)
      {
         fail("Expecting IllegalArgumentException");
      }

      try
      {
         // empty itemName entry
         String[] itemDescriptions = new String[]{"TShirt's model name", "TShirt's color", "TShirt's size", "TShirt's price"};
         itemNames = new String[]{"model", "color", "", "price"};
         tShirtType = new CompositeType("tShirt", "", itemNames, itemDescriptions, itemTypes);
         fail("No exception thrown");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);	// success
      }
      catch (Exception x)
      {
         fail("Expecting IllegalArgumentException");
      }

      try
      {
         // null itemDescription entry
         String[] itemDescriptions = new String[]{"TShirt's model name", "TShirt's color", null, "TShirt's price"};
         tShirtType = new CompositeType("tShirt", "", itemNames, itemDescriptions, itemTypes);
         fail("No exception thrown");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);	// success
      }
      catch (Exception x)
      {
         fail("Expecting IllegalArgumentException");
      }

      try
      {
         // empty itemDescription entry
         String[] itemDescriptions = new String[]{"TShirt's model name", "", "TShirt's size", "TShirt's price"};
         tShirtType = new CompositeType("tShirt", "", itemNames, itemDescriptions, itemTypes);
         fail("No exception thrown");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);	// success
      }
      catch (Exception x)
      {
         fail("Expecting IllegalArgumentException");
      }

      try
      {
         // null itemTypes
         tShirtType = new CompositeType("tShirt", "a TShirt", itemNames, itemDescriptions, null);
         fail("No exception thrown");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);	// success
      }
      catch (Exception x)
      {
         fail("Expecting IllegalArgumentException");
      }

      try
      {
         // mismatched lengths
         String[] itemDescriptions = new String[]{"TShirt's model name", "TShirt's color", "TShirt's price"};
         itemNames = new String[]{"model", "color", "size", "price"};
         tShirtType = new CompositeType("tShirt", "a TShirt", itemNames, itemDescriptions, itemTypes);
         fail("No exception thrown");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);	// success
      }
      catch (Exception x)
      {
         fail("Expecting IllegalArgumentException");
      }

   }

   public void testContainsKey()
   {
      boolean valid = tShirtType.containsKey("model");
      assertTrue(valid);

      // test fail for same name different case
      assertTrue(tShirtType.containsKey("Model") == false);
   }

   public void testGetDescription()
   {
      String expected = "TShirt's color";
      String result = tShirtType.getDescription("color");
      assertTrue(expected == result);
   }

   public void testGetType()
   {
      OpenType expected = SimpleType.FLOAT;
      OpenType result = tShirtType.getType("price");
      assertEquals(expected, result);
   }

   public void testSerialization()
   {
      // write out

      try
      {
         Serializer serializer = new Serializer();
         byte[] data = serializer.serialize(tShirtType);
         Object obj = serializer.deserialize(data);
         // assert instanceof
         assertTrue(obj instanceof CompositeType);

         // if instanceof passes continue otherwise we will not get to the rest
         CompositeType type = (CompositeType)obj;
         // assert hashcodes are equal
         assertEquals(type.hashCode(), tShirtType.hashCode());
         assertTrue(type.getType("price").equals(SimpleType.FLOAT));
         assertEquals(type.getType("size"), tShirtType.getType("size"));
         assertEquals(type.getDescription("model"), tShirtType.getDescription("model"));
         assertEquals(type.keySet(), tShirtType.keySet());
      }
      catch (IOException e)
      {
         e.printStackTrace();
      }
      catch (ClassNotFoundException e)
      {
      }
   }

   public void testHashCode() throws Exception
   {
      int ehc = 0;
      ehc += tShirtType.getTypeName().hashCode();
      for (int i = 0; i < itemNames.length; i++)
      {
         ehc += itemNames[i].hashCode();
         ehc += itemTypes[i].hashCode();
      }
      int hc = tShirtType.hashCode();
      assertTrue("Unexpected hashcode", hc == ehc);
   }

   public void testEquals() throws Exception
   {
      CompositeType undershirt =
              new CompositeType("UnderShirt",
                                "a TShirt",
                                itemNames,
                                itemDescriptions,
                                itemTypes);
      assertFalse("tShirtType and undershirt should not be equal",
                  undershirt.equals(tShirtType));
      String[] italianDescriptions =
              {"modello", "nome", "colore", "prezzo"};
      CompositeType italianshirt =
              new CompositeType("UnderShirt",
                                "una Camicia",
                                itemNames,
                                italianDescriptions,
                                itemTypes);
      assertTrue("undershirt and italianshirt should be equal",
                 italianshirt.equals(undershirt));
   }
}
