/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.server;

import java.lang.reflect.Method;
import java.net.URL;
import java.util.Iterator;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.loading.MLet;

import mx4j.MBeanDescriptionAdapter;
import mx4j.MX4JSystemKeys;
import mx4j.server.MBeanMetaData;
import mx4j.server.MBeanRepository;
import mx4j.server.interceptor.DefaultMBeanServerInterceptor;
import mx4j.server.interceptor.MBeanServerInterceptor;
import mx4j.server.interceptor.MBeanServerInterceptorConfigurator;
import test.MX4JTestCase;
import test.MutableInteger;

/**
 * @version $Revision: 1.3 $
 */
public class MX4JMBeanServerTest extends MX4JTestCase
{
   public MX4JMBeanServerTest(String s)
   {
      super(s);
   }

   public void testCustomMBeanRepository() throws Exception
   {
      String property = MX4JSystemKeys.MX4J_MBEANSERVER_REPOSITORY;
      try
      {
         System.setProperty(property, MX4JMBeanServerTest.TestMBeanRepository.class.getName());
         MBeanServer server = newMBeanServer();
         Method method = server.getClass().getDeclaredMethod("getMBeanRepository", new Class[0]);
         method.setAccessible(true);
         Object repository = method.invoke(server, new Object[0]);
         if (!(repository instanceof TestMBeanRepository)) fail("Custom Repository does not work");
      }
      finally
      {
         System.getProperties().remove(property);
      }
   }

   public void testContextClassLoaderOnMethodCalls() throws Exception
   {
      // Create the appropriate class loader hierarchy
      URL testCodebase = getClass().getProtectionDomain().getCodeSource().getLocation();
      URL implCodebase = MBeanServer.class.getProtectionDomain().getCodeSource().getLocation();
      MLet mlet = new MLet(new URL[]{testCodebase, implCodebase}, getClass().getClassLoader().getParent());
      MBeanServer server = newMBeanServer();

      // Register the MLet loader
      ObjectName loader = new ObjectName(":mbean=loader");
      server.registerMBean(mlet, loader);

      // Be sure the context classloader interceptor is enabled
      server.setAttribute(ObjectName.getInstance("JMImplementation", "interceptor", "contextclassloader"), new Attribute("Enabled", Boolean.TRUE));

      // Register the MBean
      ObjectName name = new ObjectName("Test:mbean=ccl");
      server.createMBean("test.mx4j.server.MX4JMBeanServerTest$CCL", name, loader, null, null);

      server.invoke(name, "method", null, null);
   }

   public void testAddRemoveMBeanServerInterceptor() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName configurator = new ObjectName(MBeanServerInterceptorConfigurator.OBJECT_NAME);

      MutableInteger integer = new MutableInteger(0);
      TestMBeanServerInterceptor tester = new TestMBeanServerInterceptor(integer);
      server.invoke(configurator, "addInterceptor", new Object[]{tester}, new String[]{MBeanServerInterceptor.class.getName()});

      server.getAttribute(configurator, "Running");
      if (integer.get() != 1) fail("Interceptor not installed");

      server.invoke(configurator, "clearInterceptors", null, null);
      server.getAttribute(configurator, "Running");

      // Be sure the interceptor is not anymore in the chain
      if (integer.get() != 1) fail("Interceptor not removed");
   }

   public void testRegisterRemoveMBeanServerInterceptor() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName configurator = new ObjectName(MBeanServerInterceptorConfigurator.OBJECT_NAME);

      MutableInteger integer = new MutableInteger(0);
      TestMBeanServerInterceptor tester = new TestMBeanServerInterceptor(integer);
      ObjectName name = new ObjectName("Interceptor:category=MBeanServer,type=Test");
      server.invoke(configurator, "registerInterceptor", new Object[]{tester, name}, new String[]{MBeanServerInterceptor.class.getName(), ObjectName.class.getName()});

      server.getMBeanInfo(configurator);
      if (integer.get() != 1) fail("Interceptor not installed");

      // Let's check if the interceptor is registered, let's change something in it
      server.setAttribute(name, new Attribute("Enabled", Boolean.FALSE));

      // Call again
      server.getMBeanInfo(configurator);
      if (integer.get() != 1) fail("Interceptor not registered");

      AttributeList list = new AttributeList();
      list.add(new Attribute("Enabled", Boolean.TRUE));
      server.setAttributes(name, list);

      server.getMBeanInfo(configurator);
      if (integer.get() != 2) fail("Interceptor not enabled");

      server.invoke(configurator, "clearInterceptors", null, null);
      server.getAttribute(configurator, "Running");

      // Be sure the interceptor is not anymore in the chain
      if (integer.get() != 2) fail("Interceptor not removed");
   }

   public void testMBeanDescription() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = ObjectName.getInstance(":mbean=description");
      server.registerMBean(new Description(), name);
      MBeanInfo info = server.getMBeanInfo(name);
      assertEquals(info.getDescription(), new DescriptionMBeanDescription().getMBeanDescription());
   }

   public interface DescriptionMBean
   {
   }

   public static class Description implements DescriptionMBean
   {
   }

   public static class DescriptionMBeanDescription extends MBeanDescriptionAdapter
   {
      public String getMBeanDescription()
      {
         return "test";
      }
   }

   public static class TestMBeanServerInterceptor extends DefaultMBeanServerInterceptor
   {
      private MutableInteger m_integer;

      public TestMBeanServerInterceptor(MutableInteger integer)
      {
         m_integer = integer;
      }

      public String getType()
      {
         return "test";
      }

      public Object getAttribute(MBeanMetaData metadata, String attribute) throws MBeanException, AttributeNotFoundException, ReflectionException
      {
         if (isEnabled()) m_integer.set(m_integer.get() + 1);
         return super.getAttribute(metadata, attribute);
      }

      public MBeanInfo getMBeanInfo(MBeanMetaData metadata)
      {
         if (isEnabled()) m_integer.set(m_integer.get() + 1);
         return super.getMBeanInfo(metadata);
      }
   }

   public static class TestMBeanRepository implements MBeanRepository
   {
      public MBeanMetaData get(ObjectName name)
      {
         return null;
      }

      public void put(ObjectName name, MBeanMetaData metadata)
      {
      }

      public void remove(ObjectName name)
      {
      }

      public int size()
      {
         return 0;
      }

      public Iterator iterator()
      {
         return null;
      }

      public Object clone()
      {
         return null;
      }
   }

   public interface CCLMBean
   {
      public void method();
   }

   public static class CCL implements CCLMBean
   {
      public CCL()
      {
         ClassLoader cl = Thread.currentThread().getContextClassLoader();
         String loaderClass = cl.getClass().getName();
         if (!loaderClass.endsWith("MLet"))
         {
            throw new RuntimeException();
         }
      }

      public void method()
      {
         ClassLoader cl = Thread.currentThread().getContextClassLoader();
         String loaderClass = cl.getClass().getName();
         if (!loaderClass.endsWith("MLet"))
         {
            throw new RuntimeException();
         }
      }
   }
}
