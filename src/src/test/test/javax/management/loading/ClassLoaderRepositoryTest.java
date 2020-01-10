/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.loading;

import java.lang.reflect.Method;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.loading.ClassLoaderRepository;
import javax.management.loading.MLet;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.9 $
 */
public class ClassLoaderRepositoryTest extends TestCase
{
   public ClassLoaderRepositoryTest(String s)
   {
      super(s);
   }

   public void testSingleMBeanServer() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      ClassLoaderRepository clr = server.getClassLoaderRepository();
      Method method = clr.getClass().getDeclaredMethod("getSize", new Class[0]);
      method.setAccessible(true);
      Integer size = (Integer)method.invoke(clr, new Object[0]);
      int initial = size.intValue();

      ObjectName name1 = new ObjectName(":name=mlet1");
      ObjectName name2 = new ObjectName(":name=mlet2");

      MLet mlet1 = new MLet();
      server.registerMBean(mlet1, name1);

      // Check that the mlet was registered as classloader
      size = (Integer)method.invoke(clr, new Object[0]);
      if (size.intValue() != initial + 1) fail("ClassLoader not registered in ClassLoaderRepository");

      // Add another classloader
      MLet mlet2 = new MLet();
      server.registerMBean(mlet2, name2);

      size = (Integer)method.invoke(clr, new Object[0]);
      if (size.intValue() != initial + 2) fail("ClassLoader not registered in ClassLoaderRepository");
   }

   public void testMultipleMBeanServer() throws Exception
   {
      MBeanServer server1 = MBeanServerFactory.newMBeanServer("domain1");
      MBeanServer server2 = MBeanServerFactory.newMBeanServer("domain2");
      ClassLoaderRepository clr1 = server1.getClassLoaderRepository();
      ClassLoaderRepository clr2 = server2.getClassLoaderRepository();
      Method method = clr1.getClass().getDeclaredMethod("getSize", new Class[0]);
      method.setAccessible(true);
      Integer size1 = (Integer)method.invoke(clr1, new Object[0]);
      int initial1 = size1.intValue();
      Integer size2 = (Integer)method.invoke(clr2, new Object[0]);
      int initial2 = size2.intValue();

      ObjectName name1 = new ObjectName(":name=mlet1");
      ObjectName name2 = new ObjectName(":name=mlet2");

      MLet mlet1 = new MLet();
      server1.registerMBean(mlet1, name1);

      MLet mlet2 = new MLet();
      server2.registerMBean(mlet2, name2);

      // Check that the mlet was registered as classloader
      size1 = (Integer)method.invoke(clr1, new Object[0]);
      if (size1.intValue() != initial1 + 1) fail("ClassLoader not registered in ClassLoaderRepository");

      size2 = (Integer)method.invoke(clr2, new Object[0]);
      if (size2.intValue() != initial2 + 1) fail("ClassLoader not registered in ClassLoaderRepository");
   }

   public void testMultipleRegistrationOfSameClassLoader() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      ClassLoaderRepository clr = server.getClassLoaderRepository();
      Method method = clr.getClass().getDeclaredMethod("getSize", new Class[0]);
      method.setAccessible(true);
      Integer size = (Integer)method.invoke(clr, new Object[0]);
      int initial = size.intValue();

      ObjectName name1 = new ObjectName(":name=mlet1");
      ObjectName name2 = new ObjectName(":name=mlet2");

      MLet mlet1 = new MLet();
      server.registerMBean(mlet1, name1);
      server.registerMBean(mlet1, name2);

      // Check that the mlet was registered only once
      size = (Integer)method.invoke(clr, new Object[0]);
      if (size.intValue() != initial + 1) fail("Same ClassLoader was registered more than once");
   }
}
