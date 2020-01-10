/**
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */
package test.javax.management.openmbean;

/**
 * @version $Revision: 1.6 $
 */

import java.util.HashMap;
import java.util.Map;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;

import junit.framework.TestCase;

public class SimpleTypeTest extends TestCase
{
   public SimpleTypeTest(String s)
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

   public void testIsValue()
   {
      OpenType type = SimpleType.BOOLEAN;
      OpenType type2 = SimpleType.INTEGER;
      assertTrue(type.isValue(new Boolean(true)));
      assertTrue(!(type.isValue(new Integer(0))));
   }

   public void testEquals()
   {
      OpenType type = SimpleType.BOOLEAN;
      OpenType type2 = SimpleType.BOOLEAN;
      OpenType type3 = SimpleType.OBJECTNAME;
      OpenType type4 = SimpleType.DATE;

      assertTrue(type.equals(type2));
      // test the reverse
      assertTrue(type2.equals(type));
      assertTrue(!(type.equals(type3)));
      assertTrue(!(type.equals(type4)));
      // test reverse
      assertTrue(!(type3.equals(type)));
      assertTrue(!(type4.equals(type)));

      assertTrue(type4.equals(SimpleType.DATE));
   }

   public void testHashCode()
   {
      Map m = new HashMap();
      // these objects are immutable hence the instances must be the same, equal, with an equal hashCode
      OpenType type = SimpleType.CHARACTER;
      OpenType type2 = SimpleType.CHARACTER;
      OpenType type3 = SimpleType.OBJECTNAME;

      m.put(type, "java.lang.Character");
      String value = (String)m.get(type2);

      assertEquals(value, "java.lang.Character");
      assertEquals(type.hashCode(), type2.hashCode());
      assertTrue(type.hashCode() != type3.hashCode());
   }

   public void testTypeNameDescriptionClassNameAllEqual()
   {
      OpenType type = SimpleType.BIGINTEGER;
      String typeName = type.getTypeName();
      String description = type.getDescription();
      String className = type.getClassName();

      assertTrue(typeName.equals(description));
      assertTrue(typeName.equals(className));
      assertTrue(className.equals(description));

      assertEquals(typeName, "java.math.BigInteger");
   }
}
