/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management;

import java.lang.reflect.Method;
import java.util.HashMap;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.MBeanServerBuilder;
import javax.management.MBeanServerDelegate;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import mx4j.server.ChainedMBeanServer;
import mx4j.server.ChainedMBeanServerBuilder;
import mx4j.server.MX4JMBeanServer;
import mx4j.server.MX4JMBeanServerBuilder;
import mx4j.server.MX4JMBeanServerDelegate;
import test.MX4JTestCase;

/**
 * @version $Revision: 1.6 $
 */
public class MBeanServerBuilderTest extends MX4JTestCase
{
   private static final String INITIAL_BUILDER = "javax.management.builder.initial";

   public MBeanServerBuilderTest(String s)
   {
      super(s);
   }

   public void testDefaultBuilder() throws Exception
   {
      System.getProperties().remove(INITIAL_BUILDER);
      MBeanServer server = newMBeanServer();
      if (!(server instanceof MX4JMBeanServer))
         fail("Wrong default server implementation");
   }

   public void testCustomBuilder() throws Exception
   {
      try
      {
         System.setProperty(INITIAL_BUILDER, CustomBuilder.class.getName());
         MBeanServer server = newMBeanServer();
         if (!(server instanceof CustomMBeanServer))
            fail("Wrong custom server implementation");
         if (!server.isInstanceOf(new ObjectName("JMImplementation", "type", "MBeanServerDelegate"), CustomDelegate.class.getName()))
            fail("wrong delegate implementation");
      }
      finally
      {
         System.getProperties().remove(INITIAL_BUILDER);
      }
   }

   public void testMX4JunderJMXRI() throws Exception
   {
      ClassLoader jmxriLoader = createJMXRIWithMX4JImplClassLoader();

      Class jmxri_c_mBeanServerFactory = jmxriLoader.loadClass("javax.management.MBeanServerFactory");
      Method jmxri_m_newMBeanServer = jmxri_c_mBeanServerFactory.getMethod("newMBeanServer", new Class[0]);

      System.getProperties().remove(INITIAL_BUILDER);
      Object jmxri_MBeanServer = jmxri_m_newMBeanServer.invoke(null, new Object[0]);

      boolean isJMXRImBeanServer = jmxri_MBeanServer.getClass().getName().startsWith("com.sun.jmx.");
      if (!isJMXRImBeanServer)
         fail("Failed to make use of JMXRI classes");

      final ClassLoader oldContextLoader = Thread.currentThread().getContextClassLoader();
      try
      {
         System.setProperty(INITIAL_BUILDER, MX4JMBeanServerBuilder.class.getName());
         Thread.currentThread().setContextClassLoader(jmxriLoader);
         Object mx4j_MBeanServer = jmxri_m_newMBeanServer.invoke(null, new Object[0]);
         boolean isMX4JMBeanServer = mx4j_MBeanServer.getClass().getName().startsWith("mx4j.server.");
         if (!isMX4JMBeanServer)
            fail("Not using MX4J as specified by javax.management.builder.initial");
      }
      finally
      {
         System.getProperties().remove(INITIAL_BUILDER);
         Thread.currentThread().setContextClassLoader(oldContextLoader);
      }
   }

   public void testChainedMBeanServerBuilder() throws Exception
   {
      try
      {
         System.setProperty(INITIAL_BUILDER, ComplexBuilder.class.getName());
         MBeanServer server = newMBeanServer();
         if (!(server instanceof LoggingMBeanServer)) fail();
         server.registerMBean(new Simple(), new ObjectName(":mbean=simple"));
         Object vendor = server.getAttribute(new ObjectName("JMImplementation", "type", "MBeanServerDelegate"), "ImplementationVendor");
         if (!(vendor instanceof HashMap)) fail();
         if (((HashMap)vendor).size() != 4) fail();
      }
      finally
      {
         System.getProperties().remove(INITIAL_BUILDER);
      }
   }

   // Support classes

   /**
    * This is a simple terminal builder, that is a builder that creates a full MBeanServer implementation.
    * It cannot be used to chain other builders, it can only be used as the last builder in the chain.
    */
   public static class CustomBuilder extends MBeanServerBuilder
   {
      public MBeanServer newMBeanServer(String defaultDomain, MBeanServer outer, MBeanServerDelegate delegate)
      {
         return new CustomMBeanServer(defaultDomain, outer, delegate);
      }

      public MBeanServerDelegate newMBeanServerDelegate()
      {
         return new CustomDelegate();
      }
   }

   /**
    * Custom delegate class, for testing.
    */
   public static class CustomDelegate extends MX4JMBeanServerDelegate
   {
   }

   public static class CustomMBeanServer extends MX4JMBeanServer
   {
      public CustomMBeanServer(String defaultDomain, MBeanServer outer, MBeanServerDelegate delegate)
      {
         super(defaultDomain, outer, delegate);
      }
   }

   public static class ComplexBuilder extends ChainedMBeanServerBuilder
   {
      public ComplexBuilder()
      {
         super(new LoggingBuilder(new PerformanceBuilder(new MX4JMBeanServerBuilder())));
      }
   }

   public static class LoggingBuilder extends ChainedMBeanServerBuilder
   {
      public LoggingBuilder(MBeanServerBuilder chain)
      {
         super(chain);
      }

      public MBeanServer newMBeanServer(String defaultDomain, MBeanServer outer, MBeanServerDelegate delegate)
      {
         LoggingMBeanServer external = new LoggingMBeanServer();
         MBeanServer nested = getMBeanServerBuilder().newMBeanServer(defaultDomain, outer == null ? external : outer, delegate);
         external.setMBeanServer(nested);
         return external;
      }
   }

   public static class PerformanceBuilder extends ChainedMBeanServerBuilder
   {
      public PerformanceBuilder(MBeanServerBuilder chain)
      {
         super(chain);
      }

      public MBeanServer newMBeanServer(String defaultDomain, MBeanServer outer, MBeanServerDelegate delegate)
      {
         TimingMBeanServer external = new TimingMBeanServer();
         MBeanServer nested = getMBeanServerBuilder().newMBeanServer(defaultDomain, outer == null ? external : outer, delegate);
         external.setMBeanServer(new InvocationCounterMBeanServer(nested));
         return external;
      }
   }

   public static class LoggingMBeanServer extends ChainedMBeanServer
   {
      protected void setMBeanServer(MBeanServer server)
      {
         super.setMBeanServer(server);
      }

      public Object getAttribute(ObjectName objectName, String attribute)
              throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException
      {
         HashMap map = (HashMap)super.getAttribute(objectName, attribute);
         map.put("logged", "logged");
         return map;
      }
   }

   public static class TimingMBeanServer extends ChainedMBeanServer
   {
      protected void setMBeanServer(MBeanServer server)
      {
         super.setMBeanServer(server);
      }

      public Object getAttribute(ObjectName objectName, String attribute)
              throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException
      {
         long start = System.currentTimeMillis();
         HashMap ret = (HashMap)super.getAttribute(objectName, attribute);
         long end = System.currentTimeMillis();
         long elapsed = end - start;
         System.out.println("Elapsed: " + elapsed);
         ret.put("elapsed", new Long(elapsed));
         return ret;
      }
   }

   public static class InvocationCounterMBeanServer extends ChainedMBeanServer
   {
      private ThreadLocal getAttributeCount = new ThreadLocal()
      {
         protected Object initialValue()
         {
            return new Long(0);
         }
      };

      public InvocationCounterMBeanServer(MBeanServer server)
      {
         super(server);
      }

      public Object getAttribute(ObjectName objectName, String attribute)
              throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException
      {
         Long count = (Long)getAttributeCount.get();
         count = new Long(count.longValue() + 1);
         getAttributeCount.set(count);
         System.out.println("Counted: " + count);

         HashMap map = new HashMap();
         map.put("result", super.getAttribute(objectName, attribute));
         map.put("count", count);

         return map;
      }
   }

   public interface SimpleMBean
   {
   }

   public static class Simple implements SimpleMBean, MBeanRegistration
   {
      public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception
      {
         if (!(server instanceof LoggingMBeanServer)) fail();
         return name;
      }

      public void postRegister(Boolean registrationDone)
      {
      }

      public void preDeregister() throws Exception
      {
      }

      public void postDeregister()
      {
      }
   }
}
