/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management;

import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.StandardMBean;

import test.MX4JTestCase;
import test.javax.management.support.StandardMBeanSupport;

/**
 * @version $Revision: 1.7 $
 */
public class StandardMBeanTest extends MX4JTestCase
{
   public StandardMBeanTest(String s)
   {
      super(s);
   }

   public void testInvalidStandardMBean() throws Exception
   {
      try
      {
         new StandardMBean(null, null);
         fail("Implementation cannot be null");
      }
      catch (IllegalArgumentException x)
      {
      }

      try
      {
         Object impl = new Object();
         new StandardMBean(impl, null);
         fail(impl.getClass().getName() + " is not a compliant MBean");
      }
      catch (NotCompliantMBeanException x)
      {
      }

      try
      {
         Object impl = new Object();
         Class mgmt = Cloneable.class;
         new StandardMBean(impl, mgmt);
         fail(impl.getClass().getName() + " does not implement " + mgmt.getName());
      }
      catch (NotCompliantMBeanException x)
      {
      }

      try
      {
         Object impl = new Object();
         Class mgmt = Object.class;
         new StandardMBean(impl, mgmt);
         fail("Class " + mgmt.getName() + " is not an interface");
      }
      catch (NotCompliantMBeanException x)
      {
      }

      try
      {
         new StandardMBeanSupport.SubclassNotCompliant();
         fail("StandardMBean is not compliant");
      }
      catch (NotCompliantMBeanException x)
      {
      }
   }

   public void testSubclassWithNoManagement() throws Exception
   {
      StandardMBean mbean = new StandardMBeanSupport.SubclassWithNoManagement();
      testNoManagement(mbean);
   }

   public void testSubclassWithManagement() throws Exception
   {
      StandardMBean mbean = new StandardMBeanSupport.SubclassWithManagement();
      testManagement(mbean);
   }

   public void testImplementationWithNoManagement() throws Exception
   {
      StandardMBean mbean = new StandardMBean(new StandardMBeanSupport.ImplementationWithNoManagement(), null);
      testNoManagement(mbean);
   }

   public void testImplementationWithManagement() throws Exception
   {
      StandardMBean mbean = new StandardMBean(new StandardMBeanSupport.ImplementationWithManagement(), StandardMBeanSupport.Management.class);
      testManagement(mbean);
   }

   private void testNoManagement(Object mbean) throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = ObjectName.getInstance(":type=subclass,management=no");
      server.registerMBean(mbean, name);
      Object result = server.invoke(name, "test", null, null);
      assertNotNull(result);
   }

   private void testManagement(Object mbean) throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = ObjectName.getInstance(":type=subclass,management=yes");
      server.registerMBean(mbean, name);
      Object result = server.invoke(name, "test", null, null);
      assertNotNull(result);
      try
      {
         server.invoke(name, "cannotCall", null, null);
         fail("Cannot invoke a method not in the management interface");
      }
      catch (ReflectionException x)
      {
      }
   }

   public void testMBeanInfoCaching() throws Exception
   {
      StandardMBean mbean = new StandardMBeanSupport.SubclassWithNoManagement();
      MBeanInfo original = mbean.getMBeanInfo();

      // Make a second call and be sure it's cached
      MBeanInfo info = mbean.getMBeanInfo();
      if (info != original) fail("MBeanInfo is not cached");
   }

   public void testCallbacks() throws Exception
   {
      StandardMBeanSupport.CallbackCounter mbean = new StandardMBeanSupport.CallbackCounter(0);
      // Trigger the callbacks
      mbean.getMBeanInfo();
      // There are 10 callbacks: the management interface has 1 attribute and 1 operation, so:
      // 1 -> class name of MBeanInfo
      // 2 -> description of MBeanInfo
      // 3 -> description of attribute
      // 6 -> description of constructor + parameter name + parameter description
      // 10 -> description of operation + parameter name + parameter description + operation impact
      assertEquals(mbean.getCount(), 10);
   }

   public void testSetImplementation() throws Exception
   {
      StandardMBean mbean = new StandardMBeanSupport.SubclassWithManagement();
      mbean.setImplementation(new StandardMBeanSupport.ImplementationWithManagement());

      try
      {
         mbean.setImplementation(new Object());
         fail("New implementation does not implement the management interface " + mbean.getMBeanInterface().getName());
      }
      catch (NotCompliantMBeanException x)
      {
      }
   }

   public void testPublicManagementInterfaceWithPrivateImplementation() throws Exception
   {
      // Tests whether a MBean is acceptable as long as the public interface is public
      // Checks compliance with p34 of JMX 1.2 specification
      StandardMBeanSupport.PublicInterfaceMBean mbean = StandardMBeanSupport.createPublicInterfaceMBean();
      MBeanServer server = newMBeanServer();
      ObjectName name = ObjectName.getInstance(":type=privateimplementation");
      server.registerMBean(mbean, name);
      Object result = server.invoke(name, "test", null, null);
      assertNotNull(result);

      try
      {
         name = ObjectName.getInstance(":type=privateimplementation2");
         server.createMBean("test.javax.management.support.StandardMBeanSupport$PublicInterface", name);
         fail("Must not be able to create an MBean whose class is private");
      }
      catch (ReflectionException x)
      {
         Exception xx = x.getTargetException();
         assertTrue(xx instanceof IllegalAccessException);
      }
   }

   public void testIsInstanceOf() throws Exception
   {
      MBeanServer mbs = newMBeanServer();

      StandardMBean smbone = new StandardMBean(new StandardMBeanSupport.ImplementationWithManagement(), StandardMBeanSupport.Management.class);
      ObjectName smbonename = new ObjectName(":type=implwmgmt");
      mbs.registerMBean(smbone, smbonename);

      StandardMBean smbtwo = new StandardMBean(new StandardMBeanSupport.CallbackCounter(42), StandardMBeanSupport.FullManagement.class);
      ObjectName smbtwoname = new ObjectName(":type=cbcounter");
      mbs.registerMBean(smbtwo, smbtwoname);

      assertTrue(mbs.isInstanceOf(smbonename, "test.javax.management.support.StandardMBeanSupport$Management"));
      assertTrue(mbs.isInstanceOf(smbtwoname, "test.javax.management.support.StandardMBeanSupport$FullManagement"));
   }
}
