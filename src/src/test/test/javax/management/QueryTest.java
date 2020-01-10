/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management;

import java.util.Arrays;
import java.util.Set;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.Query;
import javax.management.ValueExp;

import junit.framework.TestCase;
import test.javax.management.support.QuerySupport;

/**
 * Class QueryTest, tests the query service
 *
 * @version $Revision: 1.10 $
 */
public class QueryTest extends TestCase
{
   public QueryTest(String name)
   {
      super(name);
   }

   public void testQueryEmpty() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer("query");

      int count = server.getMBeanCount().intValue();
      QuerySupport.Test bean1 = new QuerySupport.Test(null, null, null);
      server.registerMBean(bean1, new ObjectName("Query:name=bean1"));
      QuerySupport.Test bean2 = new QuerySupport.Test(null, null, null);
      server.registerMBean(bean2, new ObjectName("Query:name=bean2"));
      QuerySupport.Test bean3 = new QuerySupport.Test(null, null, null);
      server.registerMBean(bean3, new ObjectName("Query:name=bean3"));
      Set result = server.queryMBeans(null, null);
      assertNotNull(result);
      assertEquals(count + 3, result.size());
      result = server.queryNames(null, null);
      assertEquals(count + 3, result.size());
      assertNotNull(result);
      assertTrue(result.contains(new ObjectName("Query:name=bean1")));
      assertTrue(result.contains(new ObjectName("Query:name=bean2")));
      assertTrue(result.contains(new ObjectName("Query:name=bean3")));
   }

   public void testStringMatch() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer("query");

      ObjectName name1 = new ObjectName("StringMatch:name=test1");
      ObjectName name2 = new ObjectName("StringMatch:name=test2");
      ObjectName name3 = new ObjectName("StringMatch:name=test3");

      QuerySupport.Test a = new QuerySupport.Test("trial", null, null);
      QuerySupport.Test b = new QuerySupport.Test("arial", null, null);
      QuerySupport.Test c = new QuerySupport.Test("trial2", null, null);
      server.registerMBean(a, name1);
      server.registerMBean(b, name2);
      server.registerMBean(c, name3);
      Set result =
              server.queryMBeans(null,
                                 Query.match(Query.attr("Str"), Query.value("*rial")));
      assertEquals(2, result.size());
      result =
      server.queryMBeans(null,
                         Query.match(Query.attr("Str"), Query.value("[at]rial")));
      assertEquals(1, result.size());
      result =
      server.queryMBeans(null,
                         Query.match(Query.attr("Str"), Query.value("[a-z]rial")));
      assertEquals(2, result.size());
      result =
      server.queryMBeans(null,
                         Query.match(Query.attr("Str"), Query.value("[b-z]rial?")));
      assertEquals(1, result.size());
   }

   public void testArray() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer("query");

      QuerySupport.Test bean1 =
              new QuerySupport.Test("a", new Integer(0), null);
      server.registerMBean(bean1, new ObjectName("Query:name=bean1"));
      QuerySupport.Test bean2 =
              new QuerySupport.Test("b", new Integer(1), null);
      server.registerMBean(bean2, new ObjectName("Query:name=bean2"));
      QuerySupport.Test bean3 =
              new QuerySupport.Test("c", new Integer(2), null);
      server.registerMBean(bean3, new ObjectName("Query:name=bean3"));
      Set result =
              server.queryMBeans(null,
                                 Query.in(Query.attr("Number"),
                                          new ValueExp[]{Query.value(0), Query.value(1)}));
      assertEquals(2, result.size());
      result =
      server.queryMBeans(null,
                         Query.in(Query.attr("Str"),
                                  new ValueExp[]{Query.value("a"), Query.value("d")}));
      assertEquals(1, result.size());
   }

   public void testFinalSubString() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer("query");

      ObjectName name1 = new ObjectName("StringMatch:name=test1");
      ObjectName name2 = new ObjectName("StringMatch:name=test2");
      ObjectName name3 = new ObjectName("StringMatch:name=test3");

      QuerySupport.Test a = new QuerySupport.Test("trial", null, null);
      QuerySupport.Test b = new QuerySupport.Test("arial", null, null);
      QuerySupport.Test c = new QuerySupport.Test("tria2l", null, null);
      server.registerMBean(a, name1);
      server.registerMBean(b, name2);
      server.registerMBean(c, name3);
      Set result =
              server.queryMBeans(null,
                                 Query.finalSubString(Query.attr("Str"), Query.value("l")));
      assertEquals(3, result.size());
      result =
      server.queryMBeans(null,
                         Query.finalSubString(Query.attr("Str"), Query.value("rial")));
      assertEquals(2, result.size());
      result =
      server.queryMBeans(null,
                         Query.finalSubString(Query.attr("Str"),
                                              Query.value("nothing")));
      assertEquals(0, result.size());
   }

   public void testInitialString() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer("query");

      ObjectName name1 = new ObjectName("StringMatch:name=test1");
      ObjectName name2 = new ObjectName("StringMatch:name=test2");
      ObjectName name3 = new ObjectName("StringMatch:name=test3");

      QuerySupport.Test a = new QuerySupport.Test("trial", null, null);
      QuerySupport.Test b = new QuerySupport.Test("arial", null, null);
      QuerySupport.Test c = new QuerySupport.Test("trial2", null, null);
      server.registerMBean(a, name1);
      server.registerMBean(b, name2);
      server.registerMBean(c, name3);
      Set result =
              server.queryMBeans(null,
                                 Query.initialSubString(Query.attr("Str"), Query.value("t")));
      assertEquals(2, result.size());
      result =
      server.queryMBeans(null,
                         Query.initialSubString(Query.attr("Str"), Query.value("tr")));
      assertEquals(2, result.size());
      result =
      server.queryMBeans(null,
                         Query.initialSubString(Query.attr("Str"), Query.value("tri")));
      assertEquals(2, result.size());
      result =
      server.queryMBeans(null,
                         Query.initialSubString(Query.attr("Str"),
                                                Query.value("nothing")));
      assertEquals(0, result.size());
   }

   public void testMathOperations() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer("query");

      ObjectName name1 = new ObjectName("StringMatch:name=test1");
      ObjectName name2 = new ObjectName("StringMatch:name=test2");
      ObjectName name3 = new ObjectName("StringMatch:name=test3");

      QuerySupport.Test a = new QuerySupport.Test(null, new Integer(1), null);
      QuerySupport.Test b = new QuerySupport.Test(null, new Integer(2), null);
      QuerySupport.Test c = new QuerySupport.Test(null, new Integer(3), null);
      server.registerMBean(a, name1);
      server.registerMBean(b, name2);
      server.registerMBean(c, name3);
      Set result =
              server.queryMBeans(null,
                                 Query.eq(Query.value(3),
                                          Query.plus(Query.attr("Number"), Query.value(1))));
      assertEquals(1, result.size());
      result =
      server.queryMBeans(null,
                         Query.eq(Query.value(0),
                                  Query.minus(Query.attr("Number"), Query.value(2))));
      assertEquals(1, result.size());
      result =
      server.queryMBeans(null,
                         Query.eq(Query.value(0),
                                  Query.times(Query.attr("Number"), Query.value(0))));
      assertEquals(3, result.size());
      result =
      server.queryMBeans(null,
                         Query.eq(Query.value(1),
                                  Query.div(Query.attr("Number"), Query.value(3))));
      assertEquals(1, result.size());
   }

   public void testAttribute() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer("query");

      ObjectName name1 = new ObjectName("StringMatch:name=test1");
      ObjectName name2 = new ObjectName("StringMatch:name=test2");
      ObjectName name3 = new ObjectName("StringMatch:name=test3");

      QuerySupport.Test a = new QuerySupport.Test(null, new Integer(1), null);
      QuerySupport.Test b = new QuerySupport.Test(null, new Integer(2), null);
      QuerySupport.Test c = new QuerySupport.Test(null, new Integer(2), null);
      server.registerMBean(a, name1);
      server.registerMBean(b, name2);
      server.registerMBean(c, name3);
      Set result =
              server.queryMBeans(null,
                                 Query.eq(Query.value(2), Query.attr("Number")));
      assertEquals(2, result.size());
      result =
      server.queryMBeans(null,
                         Query.eq(Query.classattr(),
                                  Query.value("test.javax.management.support.QuerySupport$Test")));
      assertEquals(3, result.size());
   }

   public void testBetween() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer("query");

      ObjectName name1 = new ObjectName("StringMatch:name=test1");
      ObjectName name2 = new ObjectName("StringMatch:name=test2");
      ObjectName name3 = new ObjectName("StringMatch:name=test3");

      QuerySupport.Test a = new QuerySupport.Test("a", new Integer(1), null);
      QuerySupport.Test b = new QuerySupport.Test("b", new Integer(2), null);
      QuerySupport.Test c = new QuerySupport.Test("c", new Integer(5), null);
      server.registerMBean(a, name1);
      server.registerMBean(b, name2);
      server.registerMBean(c, name3);
      Set result =
              server.queryMBeans(null,
                                 Query.between(Query.attr("Number"),
                                               Query.value(2),
                                               Query.value(3)));
      assertEquals(1, result.size());
      result =
      server.queryMBeans(null,
                         Query.between(Query.attr("Number"),
                                       Query.value(1),
                                       Query.value(3)));
      assertEquals(2, result.size());
      result =
      server.queryMBeans(null,
                         Query.between(Query.attr("Number"),
                                       Query.value(1),
                                       Query.value(5)));
      assertEquals(3, result.size());
/*
      result =
      server.queryMBeans(null,
                         Query.between(Query.attr("Str"),
                                       Query.value("a"),
                                       Query.value("b")));
      assertEquals(2, result.size());
      result =
      server.queryMBeans(null,
                         Query.between(Query.attr("Str"),
                                       Query.value("a"),
                                       Query.value("z")));
      assertEquals(3, result.size());
*/
   }

   public void testRelation() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer("query");

      ObjectName name1 = new ObjectName("StringMatch:name=test1");
      ObjectName name2 = new ObjectName("StringMatch:name=test2");
      ObjectName name3 = new ObjectName("StringMatch:name=test3");
      ObjectName name4 = new ObjectName("BigNumber:name=test4");
      ObjectName name5 = new ObjectName("BigNumber:name=test5");
      ObjectName name6 = new ObjectName("LittleNumber:name=test6");

      QuerySupport.Test a = new QuerySupport.Test("a", new Integer(1), null);
      QuerySupport.Test b = new QuerySupport.Test("b", new Integer(2), null);
      QuerySupport.Test c = new QuerySupport.Test("c", new Integer(5), null);
      QuerySupport.Test d =
              new QuerySupport.Test("d", new Integer(112), null);
      QuerySupport.DynamicTest e =
              new QuerySupport.DynamicTest("e", 119L, null);
      QuerySupport.DynamicTest f =
              new QuerySupport.DynamicTest("f", 8L, null);

      server.registerMBean(a, name1);
      server.registerMBean(b, name2);
      server.registerMBean(c, name3);
      server.registerMBean(d, name4);
      server.registerMBean(e, name5);
      server.registerMBean(f, name6);

      Set result =
              server.queryMBeans(null,
                                 Query.lt(Query.attr("Number"), Query.value(3)));
      assertEquals(2, result.size());
      result =
      server.queryMBeans(null,
                         Query.leq(Query.attr("Number"), Query.value(5)));
      assertEquals(3, result.size());
      result =
      server.queryMBeans(new ObjectName("StringMatch:*"),
                         Query.eq(Query.attr("Str"), Query.value("a")));
      assertEquals(1, result.size());
      result =
      server.queryMBeans(new ObjectName("StringMatch:*"),
                         Query.gt(Query.attr("Number"), Query.value(2)));
      assertEquals(1, result.size());
      result =
      server.queryMBeans(null,
                         Query.gt(Query.attr("Boolean"), Query.value(2)));
      assertEquals(0, result.size());
      result =
      server.queryMBeans(new ObjectName("StringMatch:*"),
                         Query.geq(Query.attr("Number"), Query.value(2)));
      assertEquals(2, result.size());
      result =
      server.queryMBeans(new ObjectName("StringMatch:*"),
                         Query.geq(Query.attr("Str"), Query.value("a")));
      assertEquals(3, result.size());
      result =
      server.queryMBeans(null,
                         Query.gt(Query.attr("Number"), Query.value(2)));
      assertEquals(4, result.size());
      result =
      server.queryMBeans(new ObjectName("*Number:*"),
                         Query.gt(Query.attr("Number"), Query.value(100)));
      assertEquals(2, result.size());
   }

   public void testAnyString() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer("query");

      ObjectName name1 = new ObjectName("StringMatch:name=test1");
      ObjectName name2 = new ObjectName("StringMatch:name=test2");
      ObjectName name3 = new ObjectName("StringMatch:name=test3");

      QuerySupport.Test a = new QuerySupport.Test("trial", null, null);
      QuerySupport.Test b = new QuerySupport.Test("arial", null, null);
      QuerySupport.Test c = new QuerySupport.Test("trial2", null, null);
      server.registerMBean(a, name1);
      server.registerMBean(b, name2);
      server.registerMBean(c, name3);
      Set result =
              server.queryMBeans(null,
                                 Query.anySubString(Query.attr("Str"), Query.value("trial")));
      assertEquals(2, result.size());
      result =
      server.queryMBeans(null,
                         Query.anySubString(Query.attr("Str"), Query.value("rial")));
      assertEquals(3, result.size());
      result =
      server.queryMBeans(null,
                         Query.anySubString(Query.attr("Str"), Query.value("tri")));
      assertEquals(2, result.size());
      result =
      server.queryMBeans(null,
                         Query.anySubString(Query.attr("Str"), Query.value("ri")));
      assertEquals(3, result.size());
      result =
      server.queryMBeans(null,
                         Query.anySubString(Query.attr("Str"), Query.value("no")));
      assertEquals(0, result.size());
   }

   public void testNotOperation() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer("query");

      ObjectName name1 = new ObjectName("StringMatch:name=test1");

      QuerySupport.Test a = new QuerySupport.Test(null, null, Boolean.TRUE);
      server.registerMBean(a, name1);
      Set result =
              server.queryMBeans(null,
                                 Query.not(Query.eq(Query.value(false), Query.attr("Boolean"))));
      assertEquals(1, result.size());
   }

   public void testLogical() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer("query");

      ObjectName name1 = new ObjectName("StringMatch:name=test1");
      ObjectName name2 = new ObjectName("StringMatch:name=test2");
      ObjectName name3 = new ObjectName("StringMatch:name=test3");

      QuerySupport.Test a =
              new QuerySupport.Test("a", new Integer(1), Boolean.TRUE);
      QuerySupport.Test b =
              new QuerySupport.Test("b", new Integer(2), Boolean.FALSE);
      QuerySupport.Test c =
              new QuerySupport.Test("c", new Integer(5), Boolean.TRUE);
      server.registerMBean(a, name1);
      server.registerMBean(b, name2);
      server.registerMBean(c, name3);
      Set result =
              server.queryMBeans(null,
                                 Query.and(Query.eq(Query.attr("Boolean"), Query.value(true)),
                                           Query.lt(Query.attr("Number"), Query.value(3))));
      assertEquals(1, result.size());
      result =
      server.queryMBeans(null,
                         Query.or(Query.eq(Query.attr("Boolean"), Query.value(true)),
                                  Query.eq(Query.attr("Str"), Query.value("a"))));
      assertEquals(2, result.size());
      result =
      server.queryMBeans(null,
                         Query.or(Query.eq(Query.attr("Boolean"), Query.value(true)),
                                  Query.geq(Query.attr("Str"), Query.value("a"))));
      assertEquals(3, result.size());
   }

   public void testQualifiedAttributeName() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer("query");

      ObjectName name1 = new ObjectName("StringMatch:name=test1");
      ObjectName name2 = new ObjectName("StringMatch:name=test2");
      ObjectName name3 = new ObjectName("StringMatch:name=test3");

      QuerySupport.Test a =
              new QuerySupport.Test("a", new Integer(1), Boolean.TRUE);
      QuerySupport.Test b =
              new QuerySupport.Test("b", new Integer(2), Boolean.FALSE);
      QuerySupport.Test c =
              new QuerySupport.Test("c", new Integer(5), Boolean.TRUE);
      server.registerMBean(a, name1);
      server.registerMBean(b, name2);
      server.registerMBean(c, name3);
      Set result =
              server.queryMBeans(null,
                                 Query.eq(Query.attr("test.javax.management.support.QuerySupport$Test",
                                                     "Number"),
                                          Query.value(2)));
      assertEquals(1, result.size());
      result =
      server.queryMBeans(null,
                         Query.eq(Query.attr("test.javax.management.support.QuerySupport.Test2",
                                             "Number"),
                                  Query.value(2)));
      assertEquals(0, result.size());
   }

   public void testQueryScope() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer("query");

      ObjectName name1 = new ObjectName("Scope:name0=test0");
      ObjectName name2 = new ObjectName("Scope2:name0=test0");
      ObjectName name3 = new ObjectName("Scope:name1=test1");
      ObjectName name4 = new ObjectName("Scope:name1=test1,name2=test2");
      ObjectName name5 =
              new ObjectName("Scope:name0=test0,name1=test1,name2=test2");

      QuerySupport.Test a =
              new QuerySupport.Test("a", new Integer(1), Boolean.TRUE);
      QuerySupport.Test b =
              new QuerySupport.Test("b", new Integer(2), Boolean.TRUE);
      QuerySupport.Test c =
              new QuerySupport.Test("c", new Integer(3), Boolean.TRUE);
      QuerySupport.Test d =
              new QuerySupport.Test("d", new Integer(4), Boolean.TRUE);
      QuerySupport.Test e =
              new QuerySupport.Test("e", new Integer(5), Boolean.TRUE);
      server.registerMBean(a, name1);
      server.registerMBean(b, name2);
      server.registerMBean(c, name3);
      server.registerMBean(d, name4);
      server.registerMBean(e, name5);

      ObjectName scope = new ObjectName("*:*");

      int count = server.getMBeanCount().intValue();
      Set result = server.queryMBeans(scope, null);
      assertEquals(count, result.size());

      scope = new ObjectName("*:name0=test0");
      result = server.queryMBeans(scope, null);
      assertEquals(2, result.size());

      scope = new ObjectName("*:name0=test0,*");
      result = server.queryMBeans(scope, null);
      assertEquals(3, result.size());

      scope = new ObjectName("*:name1=test1");
      result = server.queryMBeans(scope, null);
      assertEquals(1, result.size());

      scope = new ObjectName("*:*,name1=test1");
      result = server.queryMBeans(scope, null);
      assertEquals(3, result.size());

      scope = new ObjectName("*:name1=test1,*");
      result = server.queryMBeans(scope, null);
      assertEquals(3, result.size());

      scope = new ObjectName("*:name2=test2");
      result = server.queryMBeans(scope, null);
      assertEquals(0, result.size());

      scope = new ObjectName("*:name2=test2,*");
      result = server.queryMBeans(scope, null);
      assertEquals(2, result.size());

      scope = new ObjectName("*:name0=test0,name2=test2");
      result = server.queryMBeans(scope, null);
      assertEquals(0, result.size());

      scope = new ObjectName("*:name0=test0,name2=test2,*");
      result = server.queryMBeans(scope, null);
      assertEquals(1, result.size());

      ObjectName[] xpnames = new ObjectName[]{name1, name2};
      scope = new ObjectName("S*:name0=test0");
      result = server.queryNames(scope, null);
      assertTrue(Arrays.asList(xpnames).containsAll(result));
      ObjectInstance[] xpinstances =
              new ObjectInstance[]{
                 new ObjectInstance(name1, QuerySupport.Test.class.getName()),
                 new ObjectInstance(name2, QuerySupport.Test.class.getName())};
      result = server.queryMBeans(scope, null);
      assertTrue(Arrays.asList(xpinstances).containsAll(result)
                 && result.size() == xpinstances.length);
      assertEquals(2, result.size());

      scope = new ObjectName("S*:*");
      xpnames = new ObjectName[]{name1, name2, name3, name4, name5};
      result = server.queryNames(scope, null);
      assertTrue(Arrays.asList(xpnames).containsAll(result)
                 && result.size() == xpnames.length);
      result = server.queryMBeans(scope, null);
      xpinstances =
      new ObjectInstance[]{
         new ObjectInstance(name1, QuerySupport.Test.class.getName()),
         new ObjectInstance(name2, QuerySupport.Test.class.getName()),
         new ObjectInstance(name3, QuerySupport.Test.class.getName()),
         new ObjectInstance(name4, QuerySupport.Test.class.getName()),
         new ObjectInstance(name5, QuerySupport.Test.class.getName())};
      assertTrue(Arrays.asList(xpinstances).containsAll(result)
                 && result.size() == xpinstances.length);
      assertEquals(5, result.size());

      scope = new ObjectName("Scope?:*");
      xpnames = new ObjectName[]{name2};
      result = server.queryNames(scope, null);
      assertTrue(Arrays.asList(xpnames).containsAll(result)
                 && result.size() == xpnames.length);
      result = server.queryMBeans(scope, null);
      xpinstances =
      new ObjectInstance[]{
         new ObjectInstance(name2, QuerySupport.Test.class.getName())};
      assertTrue(Arrays.asList(xpinstances).containsAll(result)
                 && result.size() == xpinstances.length);
      assertEquals(1, result.size());

      scope = new ObjectName("S?o?e?:*");
      xpnames = new ObjectName[]{name2};
      result = server.queryNames(scope, null);
      assertTrue(Arrays.asList(xpnames).containsAll(result)
                 && result.size() == xpnames.length);
      result = server.queryMBeans(scope, null);
      xpinstances =
      new ObjectInstance[]{
         new ObjectInstance(name2, QuerySupport.Test.class.getName())};
      assertTrue(Arrays.asList(xpinstances).containsAll(result)
                 && result.size() == xpinstances.length);
      assertEquals(1, result.size());

      scope = new ObjectName("?c*e?:*");
      xpnames = new ObjectName[]{name2};
      result = server.queryNames(scope, null);
      assertTrue(Arrays.asList(xpnames).containsAll(result)
                 && result.size() == xpnames.length);
      result = server.queryMBeans(scope, null);
      xpinstances =
      new ObjectInstance[]{
         new ObjectInstance(name2, QuerySupport.Test.class.getName())};
      assertTrue(Arrays.asList(xpinstances).containsAll(result)
                 && result.size() == xpinstances.length);
      assertEquals(1, result.size());
   }

   public void testExceptionPropagation() throws Exception
   {
      MBeanServer server = MBeanServerFactory.createMBeanServer();
      ObjectName objname =
              new ObjectName("querytest:name=testExceptionPropagation");
      QuerySupport.DynamicTest mbean =
              new QuerySupport.DynamicTest("xyzzy", 42L, Boolean.TRUE);
      server.registerMBean(mbean, objname);
      Set result =
              server.queryNames(null,
                                Query.match(Query.attr("Str"), Query.value("x*y")));
      assertEquals(0, result.size());
      result =
      server.queryMBeans(new ObjectName("query*:*"),
                         Query.match(Query.attr("Str"), Query.value("x*y")));
      assertEquals(0, result.size());
   }
}
