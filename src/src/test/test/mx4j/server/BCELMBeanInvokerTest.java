/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.server;

import java.lang.reflect.Method;
import javax.management.MBeanServer;
import javax.management.ObjectName;

import mx4j.MX4JSystemKeys;
import test.MX4JTestCase;
import test.MutableInteger;

/**
 * @version $Revision: 1.12 $
 */
public class BCELMBeanInvokerTest extends MX4JTestCase
{
   private int m_reps;
   private int m_calls;

   public BCELMBeanInvokerTest(String s)
   {
      super(s);
   }

   protected void setUp() throws Exception
   {
      m_reps = 10;
      m_calls = 50000;
   }

   protected void tearDown() throws Exception
   {
      super.tearDown();
   }

   public void testReflectedAttributePerformance() throws Exception
   {
      String property = MX4JSystemKeys.MX4J_MBEAN_INVOKER;
      System.setProperty(property, "mx4j.server.CachingReflectionMBeanInvoker");
      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("BCEL:test=performance,type=reflection");
      try
      {
         MutableInteger integer = new MutableInteger(0);
         BCELPerformance mbean = new BCELPerformance(integer);
         server.registerMBean(mbean, name);

         long[] results = new long[m_reps];
         for (int i = 0; i < m_reps; ++i)
         {
            long start = System.currentTimeMillis();
            for (int j = 0; j < m_calls; ++j)
            {
               server.getAttribute(name, "Test");
            }
            long end = System.currentTimeMillis();
            results[i] = end - start;
            System.out.println("Reflection result: " + results[i]);
         }

         if (integer.get() != m_calls * m_reps)
         {
            fail("MBean not called !");
         }

         long reflectionAverage = 0;
         for (int i = 1; i < m_reps; ++i)
         {
            reflectionAverage += results[i];
         }
         reflectionAverage = reflectionAverage / (m_reps - 1);
         System.out.println("Reflection Average for getAttribute = " + reflectionAverage);
      }
      finally
      {
         System.getProperties().remove(property);
         server.unregisterMBean(name);
      }
   }

   public void testReflectedOperationPerformance() throws Exception
   {
      String property = MX4JSystemKeys.MX4J_MBEAN_INVOKER;
      System.setProperty(property, "mx4j.server.CachingReflectionMBeanInvoker");
      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("BCEL:test=performance,type=reflection");
      try
      {
         MutableInteger integer = new MutableInteger(0);
         BCELPerformance mbean = new BCELPerformance(integer);
         server.registerMBean(mbean, name);

         long[] results = new long[m_reps];
         for (int i = 0; i < m_reps; ++i)
         {
            long start = System.currentTimeMillis();
            for (int j = 0; j < m_calls; ++j)
            {
               server.invoke(name, "test", null, null);
            }
            long end = System.currentTimeMillis();
            results[i] = end - start;
            System.out.println("Reflection result: " + results[i]);
         }

         if (integer.get() != m_calls * m_reps)
         {
            fail("MBean not called !");
         }

         long reflectionAverage = 0;
         for (int i = 1; i < m_reps; ++i)
         {
            reflectionAverage += results[i];
         }
         reflectionAverage = reflectionAverage / (m_reps - 1);
         System.out.println("Reflection Average for invoke = " + reflectionAverage);
      }
      finally
      {
         System.getProperties().remove(property);
         server.unregisterMBean(name);
      }
   }

   public void testBCELAttributePerformance() throws Exception
   {
      // Be sure we use the BCEL invoker
      System.getProperties().remove(MX4JSystemKeys.MX4J_MBEAN_INVOKER);

      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("BCEL:test=performance,type=direct");
      try
      {
         MutableInteger integer = new MutableInteger(0);
         BCELPerformance mbean = new BCELPerformance(integer);
         server.registerMBean(mbean, name);

         long[] results = new long[m_reps];
         for (int i = 0; i < m_reps; ++i)
         {
            long start = System.currentTimeMillis();
            for (int j = 0; j < m_calls; ++j)
            {
               server.getAttribute(name, "Test");
            }
            long end = System.currentTimeMillis();
            results[i] = end - start;
            System.out.println("Direct result: " + results[i]);
         }

         if (integer.get() != m_calls * m_reps)
         {
            fail("MBean not called !");
         }

         long directAverage = 0;
         for (int i = 1; i < m_reps; ++i)
         {
            directAverage += results[i];
         }
         directAverage = directAverage / (m_reps - 1);
         System.out.println("Direct Average for getAttribute = " + directAverage);
      }
      finally
      {
         server.unregisterMBean(name);
      }
   }

   public void testBCELOperationPerformance() throws Exception
   {
      // Be sure we use the BCEL invoker
      System.getProperties().remove(MX4JSystemKeys.MX4J_MBEAN_INVOKER);

      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("BCEL:test=performance,type=direct");
      try
      {
         MutableInteger integer = new MutableInteger(0);
         BCELPerformance mbean = new BCELPerformance(integer);
         server.registerMBean(mbean, name);

         long[] results = new long[m_reps];
         for (int i = 0; i < m_reps; ++i)
         {
            long start = System.currentTimeMillis();
            for (int j = 0; j < m_calls; ++j)
            {
               server.invoke(name, "test", null, null);
            }
            long end = System.currentTimeMillis();
            results[i] = end - start;
            System.out.println("Direct result: " + results[i]);
         }

         if (integer.get() != m_calls * m_reps)
         {
            fail("MBean not called !");
         }

         long directAverage = 0;
         for (int i = 1; i < m_reps; ++i)
         {
            directAverage += results[i];
         }
         directAverage = directAverage / (m_reps - 1);
         System.out.println("Direct Average for invoke = " + directAverage);
      }
      finally
      {
         server.unregisterMBean(name);
      }
   }

   public void testPlainReflectionPerformance() throws Exception
   {
      MutableInteger integer = new MutableInteger(0);
      BCELPerformance mbean = new BCELPerformance(integer);
      Method test = mbean.getClass().getMethod("test", (Class[])null);

      int factor = 100;
      int calls = m_calls * factor;

      long[] results = new long[m_reps];
      for (int i = 0; i < m_reps; ++i)
      {
         long start = System.currentTimeMillis();
         for (int j = 0; j < calls; ++j)
         {
            test.invoke(mbean, (Object[])null);
         }
         long end = System.currentTimeMillis();
         results[i] = end - start;
         System.out.println("Plain reflection: " + (results[i] / factor));
      }

      if (integer.get() != calls * m_reps)
      {
         fail("MBean not called !");
      }

      long directAverage = 0;
      for (int i = 1; i < m_reps; ++i)
      {
         directAverage += results[i];
      }
      directAverage = directAverage / (m_reps - 1);
      System.out.println("Plain average = " + (directAverage / factor));
   }

   public void testBCELPackagePrivate() throws Exception
   {
      // Be sure we use the BCEL invoker
      System.getProperties().remove(MX4JSystemKeys.MX4J_MBEAN_INVOKER);

      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("BCEL:test=package,type=direct");
      try
      {
         MutableInteger integer = new MutableInteger(0);
         BCELPerformance mbean = new BCELPerformance(integer);
         server.registerMBean(mbean, name);

         PackagePrivate arg = new PackagePrivate();

         server.invoke(name, "testPackagePrivate", new Object[]{arg}, new String[]{arg.getClass().getName()});

         if (integer.get() != 1)
         {
            fail("MBean not called !");
         }
      }
      finally
      {
         server.unregisterMBean(name);
      }
   }

   public interface BCELPerformanceMBean
   {
      public void test();

      public int getTest();

      public void testPackagePrivate(PackagePrivate param);
   }

   class PackagePrivate
   {
   }

   public static class BCELPerformance implements BCELPerformanceMBean
   {
      private MutableInteger m_integer;

      public BCELPerformance(MutableInteger integer)
      {
         m_integer = integer;
      }

      public void test()
      {
         m_integer.set(m_integer.get() + 1);
      }

      public int getTest()
      {
         m_integer.set(m_integer.get() + 1);
         return 0;
      }

      public void testPackagePrivate(PackagePrivate param)
      {
         m_integer.set(m_integer.get() + 1);
      }
   }
}
