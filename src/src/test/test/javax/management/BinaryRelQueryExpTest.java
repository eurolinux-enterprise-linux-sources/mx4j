/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management;

import javax.management.BadBinaryOpValueExpException;
import javax.management.Query;
import javax.management.QueryExp;
import javax.management.StringValueExp;
import javax.management.ValueExp;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.4 $
 */
public class BinaryRelQueryExpTest extends TestCase
{
   /**
    * Constructor requested by the JUnit framework
    */
   public BinaryRelQueryExpTest(String name)
   {
      super(name);
   }

   public void testExceptions() throws Exception
   {
      QueryExp operation = Query.eq(null, null);
      assertTrue(operation.apply(null));

      ValueExp value1 = Query.value(new Integer(3));
      operation = Query.eq(value1, null);
      assertTrue(!operation.apply(null));

      operation = Query.eq(null, value1);
      assertTrue(!operation.apply(null));

      ValueExp value2 = Query.value(new Long(3));
      ValueExp result = Query.plus(value1, value2);
      operation = Query.eq(Query.value(6), result);
      assertTrue(operation.apply(null));

      // Comparing apple and oranges
      ValueExp bvalue1 = Query.value(true);
      operation = Query.eq(bvalue1, value2);
      assertTrue(!operation.apply(null));

      // Adding 2 booleans
      ValueExp bvalue2 = Query.value(true);
      result = Query.plus(bvalue1, bvalue2);
      operation = Query.eq(Query.value(false), result);
      try
      {
         operation.apply(null);
      }
      catch (BadBinaryOpValueExpException ignored)
      {
      }

      StringValueExp svalue1 = new StringValueExp("a");
      StringValueExp svalue2 = new StringValueExp("b");
      operation = Query.eq(svalue1, null);
      assertTrue(!operation.apply(null));
      operation = Query.eq(svalue1, svalue2);
      assertTrue(!operation.apply(null));
   }

   public void testNumericals() throws Exception
   {
      ValueExp value1 = Query.value(new Integer(3));
      ValueExp value2 = Query.value(new Long(3));
      QueryExp operation = Query.eq(value1, value2);
      assertTrue(operation.apply(null));

      value1 = Query.value(new Integer(5));
      value2 = Query.value(new Long(4));
      operation = Query.gt(value1, value2);
      assertTrue(operation.apply(null));

      value1 = Query.value(new Integer(3));
      value2 = Query.value(new Long(4));
      operation = Query.lt(value1, value2);
      assertTrue(operation.apply(null));

      value1 = Query.value(new Double(3));
      value2 = Query.value(new Long(3));
      operation = Query.eq(value1, value2);
      assertTrue(operation.apply(null));

      value1 = Query.value(new Float(5));
      value2 = Query.value(new Double(4));
      operation = Query.gt(value1, value2);
      assertTrue(operation.apply(null));

      value1 = Query.value(new Double(3));
      value2 = Query.value(new Double(4));
      operation = Query.lt(value1, value2);
      assertTrue(operation.apply(null));
   }

   public void testBooleans() throws Exception
   {
      ValueExp value1 = Query.value(true);
      ValueExp value2 = Query.value(false);
      QueryExp operation = Query.eq(value1, value2);
      assertTrue(!operation.apply(null));

      operation = Query.or(Query.eq(value1, value1), Query.eq(value1, value2));
      assertTrue(operation.apply(null));

      operation = Query.or(Query.eq(value1, value2), Query.eq(value2, value2));
      assertTrue(operation.apply(null));

      operation = Query.and(Query.eq(value1, value2), Query.eq(value2, value2));
      assertTrue(!operation.apply(null));

      operation = Query.and(Query.eq(value1, value1), Query.eq(value1, value2));
      assertTrue(!operation.apply(null));
   }

   public void testStrings() throws Exception
   {
      StringValueExp value1 = new StringValueExp("a");
      StringValueExp value2 = new StringValueExp("a");
      QueryExp operation = Query.eq(value1, value2);
      assertTrue(operation.apply(null));
      operation = Query.geq(value1, value2);
      assertTrue(operation.apply(null));
      operation = Query.gt(value1, value2);
      assertTrue(!operation.apply(null));
      operation = Query.leq(value1, value2);
      assertTrue(operation.apply(null));
      operation = Query.lt(value1, value2);
      assertTrue(!operation.apply(null));

      value1 = new StringValueExp("a");
      value2 = new StringValueExp("b");

      operation = Query.geq(value1, value2);
      assertTrue(!operation.apply(null));
      operation = Query.geq(value2, value1);
      assertTrue(operation.apply(null));
      operation = Query.gt(value1, value2);
      assertTrue(!operation.apply(null));
      operation = Query.gt(value2, value1);
      assertTrue(operation.apply(null));
      operation = Query.leq(value1, value2);
      assertTrue(operation.apply(null));
      operation = Query.leq(value2, value1);
      assertTrue(!operation.apply(null));
      operation = Query.lt(value1, value2);
      assertTrue(operation.apply(null));
      operation = Query.lt(value2, value1);
      assertTrue(!operation.apply(null));
   }
}

