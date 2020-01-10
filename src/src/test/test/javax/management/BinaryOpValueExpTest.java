/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management;

import javax.management.Query;
import javax.management.QueryExp;
import javax.management.ValueExp;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.4 $
 */
public class BinaryOpValueExpTest extends TestCase
{
   /**
    * Constructor requested by the JUnit framework
    */
   public BinaryOpValueExpTest(String name)
   {
      super(name);
   }

   public void testLongOperations() throws Exception
   {
      ValueExp value1 = Query.value(new Integer(3));
      ValueExp value2 = Query.value(new Integer(4));
      ValueExp op = Query.plus(value1, value2);
      QueryExp result = Query.eq(Query.value(7L), op);
      assertTrue(result.apply(null));

      op = Query.minus(value1, value2);
      result = Query.eq(Query.value(-1L), op);
      assertTrue(result.apply(null));

      op = Query.times(value1, value2);
      result = Query.eq(Query.value(12L), op);
      assertTrue(result.apply(null));

      op = Query.div(value1, value2);
      result = Query.eq(Query.value(0L), op);
      assertTrue(result.apply(null));
   }

   public void testDoubleOperations() throws Exception
   {
      ValueExp value1 = Query.value(new Double(3.0D));
      ValueExp value2 = Query.value(new Double(4.0D));
      ValueExp op = Query.plus(value1, value2);
      QueryExp result = Query.eq(Query.value(7.0D), op);
      assertTrue(result.apply(null));

      op = Query.minus(value1, value2);
      result = Query.eq(Query.value(-1.0D), op);
      assertTrue(result.apply(null));

      op = Query.times(value1, value2);
      result = Query.eq(Query.value(12.0D), op);
      assertTrue(result.apply(null));

      op = Query.div(value1, value2);
      result = Query.eq(Query.value(3.0D / 4.0D), op);
      assertTrue(result.apply(null));
   }
}

