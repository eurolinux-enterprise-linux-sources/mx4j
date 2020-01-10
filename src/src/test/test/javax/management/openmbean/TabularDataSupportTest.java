/**
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */
package test.javax.management.openmbean;

import java.util.Collection;
import java.util.Set;
import javax.management.openmbean.CompositeData;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularData;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.10 $
 */
public class TabularDataSupportTest extends TestCase
{
   private String[] itemNames = null;
   private String[] itemDescriptions = null;
   private OpenType[] itemTypes;
   private String[] indexNames;
   private CompositeType tShirtType;
   private TabularType allTShirtTypes;
   private TabularDataSupport tabularSupport;
   private CompositeData compositeData;

   public TabularDataSupportTest(String s)
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
      tShirtType = new CompositeType("tShirt",
                                     "a TShirt",
                                     itemNames,
                                     itemDescriptions,
                                     itemTypes);

      allTShirtTypes = new TabularType("tShirts",
                                       "List of available TShirts",
                                       tShirtType, // row type
                                       indexNames);

      Object[] itemValues = new Object[]{"MX4J", "red", "L", new Float(15.0f)};

      compositeData = new CompositeDataSupport(tShirtType, itemNames, itemValues);
      // takes tabular type
      tabularSupport = new TabularDataSupport(allTShirtTypes);
   }

   protected void tearDown() throws Exception
   {
      super.tearDown();
   }

   public void testCreation()
   {
      TabularDataSupport tabularData2 = new TabularDataSupport(allTShirtTypes);
      assertTrue(tabularData2 != null);
   }

   public void testPut()
   {
      try
      {
         tabularSupport.put(compositeData);
         assertTrue("tabularSupport doesn't contain compositeData", tabularSupport.containsValue(compositeData));
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public void testPutAll() throws Exception
   {
      CompositeData snmpshirt =
              new CompositeDataSupport(tShirtType,
                                       itemNames,
                                       new Object[]{"SNMP", "green", null, new Float(15.0f)});
      CompositeData[] shirts = {compositeData, snmpshirt};
      tabularSupport.putAll(shirts);
      assertTrue("tabularSupport doesn't contain compositeData", tabularSupport.containsValue(compositeData));
      assertTrue("tabularSupport doesn't contain snmpshirt", tabularSupport.containsValue(snmpshirt));
   }

   public void testRemove() throws Exception
   {
      CompositeData snmpshirt =
              new CompositeDataSupport(tShirtType,
                                       itemNames,
                                       new Object[]{"SNMP", "green", null, new Float(15.0f)});
      CompositeData[] shirts = {compositeData, snmpshirt};
      tabularSupport.putAll(shirts);
      CompositeData oldshirt = tabularSupport.remove(new Object[]{"SNMP", "green", null});
      assertFalse("oldshirt is null", oldshirt == null);
      assertTrue("Expecting oldshirt equals snmpshirt", snmpshirt.equals(oldshirt));
   }

   public void testGetTabularType()
   {
      TabularType toVerify = tabularSupport.getTabularType();
      assertEquals(toVerify, allTShirtTypes);
   }


   public void testCalculateIndex()
   {
      // returns an array of the indexNames as represented by the simpleTypes(itemTypes) ie returns the values for the index names which are the simpleTypes
      Object[] object = tabularSupport.calculateIndex(compositeData);
      assertTrue(object.length == indexNames.length);
   }

   public void testContainsValue()
   {
      OpenType[] keyTypes = new OpenType[]{SimpleType.STRING, SimpleType.STRING, SimpleType.STRING};
      tabularSupport.put(compositeData);
      boolean expected = tabularSupport.containsValue(compositeData);
      assertTrue(expected);
   }

   public void testContainsValueMissingValue() throws Exception
   {
      CompositeData snmpshirt =
              new CompositeDataSupport(tShirtType,
                                       itemNames,
                                       new Object[]{"SNMP", "green", "M", new Float(15.0f)});
      tabularSupport.put(compositeData);
      tabularSupport.put(snmpshirt);
      assertTrue("Should contain MX4J and SNMP shirts",
                 tabularSupport.containsValue(compositeData)
                 && tabularSupport.containsValue(snmpshirt));
      CompositeData jmxshirt =
              new CompositeDataSupport(tShirtType,
                                       itemNames,
                                       new Object[]{"JMX", "blue", "M", new Float(15.0f)});
      assertFalse("Contains JMX shirt",
                  tabularSupport.containsValue(jmxshirt));
      CompositeData bogusshirt =
              new CompositeDataSupport(tShirtType,
                                       itemNames,
                                       new Object[]{"Bogus", null, "M", new Float(15.0f)});
      assertFalse("Contains Bogus shirt",
                  tabularSupport.containsValue(bogusshirt));
   }

   public void testContainsValueNullValue() throws Exception
   {
      assertFalse("Contains 'null'", tabularSupport.containsValue(null));
   }

   public void testKeySet()
   {
      tabularSupport.put(compositeData);
      Set set = tabularSupport.keySet();
      assertTrue(set.size() == 1);
   }


   public void testValues()
   {
      tabularSupport.put(compositeData);
      Collection values = tabularSupport.values();
      assertTrue(values.contains(compositeData));
   }

   public void testEquals() throws Exception
   {
      assertFalse("Equal to 'null'", tabularSupport.equals(null));
      assertFalse("Equal to Non-TabularData",
                  tabularSupport.equals(new Integer(42)));

      String[] items = {"model", "color", "size", "price"};

      String[] usdescriptions =
              {
                 "Manufacturer's model name",
                 "The shirt's color",
                 "How big is it",
                 "How much does it cost (dollars)"};
      String[] eurodescriptions =
              {
                 "Designer's model name",
                 "The hue of the garment",
                 "Garment size",
                 "How much does it cost (euros)"};

      OpenType[] types =
              {
                 SimpleType.STRING,
                 SimpleType.STRING,
                 SimpleType.STRING,
                 SimpleType.FLOAT};
      String[] indices = {"model", "color", "size"};

      CompositeType usst =
              new CompositeType("tShirt",
                                "US Shirt",
                                items,
                                usdescriptions,
                                types);
      CompositeType eurost =
              new CompositeType("tShirt",
                                "European Shirt",
                                items,
                                usdescriptions,
                                types);

      TabularType usstt =
              new TabularType("tShirts",
                              "List of available US Shirts",
                              usst,
                              indices);
      TabularType eurostt =
              new TabularType("tShirts",
                              "List of available European Shirts",
                              eurost,
                              indices);

      TabularData ussdata = new TabularDataSupport(usstt);
      TabularData eurosdata = new TabularDataSupport(eurostt);
      assertTrue("Expecting equality for tabular shirt data",
                 ussdata.equals(eurosdata));
      assertTrue("Expecting equal hash codes for equal data",
                 eurosdata.hashCode() == ussdata.hashCode());

      OpenType[] txtypes =
              {
                 SimpleType.STRING,
                 SimpleType.STRING,
                 SimpleType.STRING,
                 SimpleType.DOUBLE};
      CompositeType txst =
              new CompositeType("tShirt",
                                "Texas Shirt",
                                items,
                                usdescriptions,
                                txtypes);
      TabularType txstt =
              new TabularType("tShirts",
                              "List of available Texas Shirts",
                              txst,
                              indices);
      TabularData txsdata = new TabularDataSupport(txstt);
      assertFalse("Texas shirt equals US shirt", ussdata.equals(txsdata));

      CompositeData ussnmpshirt =
              new CompositeDataSupport(usst,
                                       items,
                                       new Object[]{"SNMP", "green", "M", new Float(15.0f)});
      CompositeData usjmxshirt =
              new CompositeDataSupport(usst,
                                       items,
                                       new Object[]{"JMX", "blue", "M", new Float(15.0f)});
      ussdata.put(ussnmpshirt);
      ussdata.put(usjmxshirt);

      CompositeData eurosnmpshirt =
              new CompositeDataSupport(usst,
                                       items,
                                       new Object[]{"SNMP", "green", "M", new Float(15.0f)});
      CompositeData eurojmxshirt =
              new CompositeDataSupport(usst,
                                       items,
                                       new Object[]{"JMX", "blue", "M", new Float(15.0f)});
      eurosdata.put(eurosnmpshirt);
      eurosdata.put(eurojmxshirt);

      assertTrue("Expecting US and Euro shirt data to be equal",
                 ussdata.equals(eurosdata));
      assertTrue("Expecting US and Euro shirt data hash codes to be equal",
                 eurosdata.hashCode() == ussdata.hashCode());
      int ushashcode = ussdata.getTabularType().hashCode();
      ushashcode += ussnmpshirt.hashCode();
      ushashcode += usjmxshirt.hashCode();
      assertTrue("Unexpected hash code computation", ussdata.hashCode() == ushashcode);
   }
}
