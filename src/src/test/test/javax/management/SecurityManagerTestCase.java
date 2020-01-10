/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management;

import java.security.Permission;
import java.util.Iterator;
import java.util.Set;
import java.io.IOException;
import javax.management.*;

import test.MX4JTestCase;

/**
 * Test case class that defines all tests to be performed by a security manager test
 * both for the local (that is, MBeanServer tests) and the remote (that is, for MBeanServerConnection
 * tests) case.
 * It also defines the implementations of the test methods, leaving to subclasses
 * only the creation of the MBeanServerConnection (or MBeanServer) object.
 *
 * @version $Revision: 1.5 $
 */
public abstract class SecurityManagerTestCase extends MX4JTestCase
{
   public SecurityManagerTestCase(String s)
   {
      super(s);
   }

   /**
    * Adds the given permission to the client codesource, that is the test codesource
    *
    * @see #resetPermissions
    */
   protected abstract void addPermission(Permission p);

   /**
    * Removes all permissions add via {@link #addPermission}
    */
   protected abstract void resetPermissions();

   public abstract void testAddRemoveNotificationListener() throws Exception;

   public abstract void testCreateMBean4Params() throws Exception;

   public abstract void testCreateMBean5Params() throws Exception;

   public abstract void testGetAttribute() throws Exception;

   public abstract void testGetAttributes() throws Exception;

   public abstract void testGetDefaultDomain() throws Exception;

   public abstract void testGetDomains() throws Exception;

   public abstract void testGetMBeanCount() throws Exception;

   public abstract void testGetMBeanInfo() throws Exception;

   public abstract void testGetObjectInstance() throws Exception;

   public abstract void testInvoke() throws Exception;

   public abstract void testIsInstanceOf() throws Exception;

   public abstract void testIsRegistered() throws Exception;

   public abstract void testQueryMBeans() throws Exception;

   public abstract void testQueryNames() throws Exception;

   public abstract void testSetAttribute() throws Exception;

   public abstract void testSetAttributes() throws Exception;

   public abstract void testUnregisterMBean() throws Exception;

   protected void testAddRemoveNotificationListener(MBeanServerConnection server) throws Exception
   {
      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
         }
      };

      ObjectName delegate = new ObjectName("JMImplementation", "type", "MBeanServerDelegate");

      try
      {
         server.addNotificationListener(delegate, listener, null, null);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      addPermission(new MBeanPermission("[" + delegate.getCanonicalName() + "]", "addNotificationListener"));
      server.addNotificationListener(delegate, listener, null, null);

      // Clean up
      try
      {
         server.removeNotificationListener(delegate, listener);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      addPermission(new MBeanPermission("[" + delegate.getCanonicalName() + "]", "removeNotificationListener"));
      server.removeNotificationListener(delegate, listener);
   }

   protected void testCreateMBean4Params(MBeanServerConnection server) throws Exception
   {
      // Needed to create an MLet, which is a ClassLoader
      addPermission(new RuntimePermission("createClassLoader"));
      ObjectName mletName = new ObjectName(server.getDefaultDomain(), "name", "mlet");
      String mletClassName = "javax.management.loading.MLet";
      addPermission(new MBeanPermission(mletClassName + "[" + mletName.getCanonicalName() + "]", "instantiate, registerMBean"));
      server.createMBean(mletClassName, mletName, null, null, null);

      // Now we have something in the CLR
      String mbeanClassName = "javax.management.MBeanServerDelegate";
      ObjectName mbeanName = new ObjectName(server.getDefaultDomain(), "name", "delegate");

      try
      {
         server.createMBean(mbeanClassName, mbeanName);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      addPermission(new MBeanPermission(mbeanClassName + "[" + mbeanName.getCanonicalName() + "]", "instantiate, registerMBean"));
      ObjectInstance result = server.createMBean(mbeanClassName, mbeanName, null, null);
      assertNotNull(result);
   }

   protected void testCreateMBean5Params(MBeanServerConnection server) throws Exception
   {
      // Needed to create an MLet, which is a ClassLoader
      addPermission(new RuntimePermission("createClassLoader"));

      ObjectName mletName = new ObjectName(server.getDefaultDomain(), "name", "mlet");
      String mletClassName = "javax.management.loading.MLet";

      try
      {
         server.createMBean(mletClassName, mletName, null);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      addPermission(new MBeanPermission(mletClassName + "[" + mletName.getCanonicalName() + "]", "instantiate, registerMBean"));
      ObjectInstance result = server.createMBean(mletClassName, mletName, null, null, null);
      assertNotNull(result);
   }

   protected void testGetAttribute(MBeanServerConnection server) throws Exception
   {
      ObjectName delegate = new ObjectName("JMImplementation", "type", "MBeanServerDelegate");
      String attribute = "ImplementationName";
      try
      {
         server.getAttribute(delegate, attribute);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      addPermission(new MBeanPermission("#" + attribute + "[" + delegate.getCanonicalName() + "]", "getAttribute"));
      String result = (String)server.getAttribute(delegate, attribute);
      assertNotNull(result);
   }

   protected void testGetAttributes(MBeanServerConnection server) throws Exception
   {
      ObjectName delegate = new ObjectName("JMImplementation", "type", "MBeanServerDelegate");

      String[] allAttributes = new String[]{"MBeanServerId", "ImplementationName", "ImplementationVendor", "ImplementationVersion", "SpecificationName", "SpecificationVendor", "SpecificationVersion"};
      String[] allowed = new String[0];
      String[] wanted = new String[0];

      try
      {
         server.getAttributes(delegate, allAttributes);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      // Check that for wrong attribute I get an empty list
      allowed = new String[]{allAttributes[1]};
      wanted = new String[]{allAttributes[0]};
      addPermission(new MBeanPermission("#" + allowed[0] + "[" + delegate.getCanonicalName() + "]", "getAttribute"));
      AttributeList list = server.getAttributes(delegate, wanted);
      assertEquals(list.size(), 0);

      // Check that for the right attribute I get it
      resetPermissions();
      allowed = new String[]{allAttributes[0]};
      wanted = allAttributes;
      addPermission(new MBeanPermission("#" + allowed[0] + "[" + delegate.getCanonicalName() + "]", "getAttribute"));
      list = server.getAttributes(delegate, wanted);
      assertEquals(list.size(), allowed.length);
      Attribute attrib = (Attribute)list.get(0);
      assertEquals(attrib.getName(), allowed[0]);

      // Check that if I grant some I only get some
      resetPermissions();
      // Only attributes that start with 'Implementation'
      allowed = new String[]{allAttributes[1], allAttributes[2], allAttributes[3]};
      wanted = allAttributes;
      addPermission(new MBeanPermission("#Implementation*[" + delegate.getCanonicalName() + "]", "getAttribute"));
      list = server.getAttributes(delegate, wanted);
      assertEquals(list.size(), allowed.length);
      for (int i = 0; i < list.size(); ++i)
      {
         Attribute attr = (Attribute)list.get(i);
         assertEquals(attr.getName(), allowed[i]);
      }

      // Check that if I grant all I get them all
      resetPermissions();
      allowed = allAttributes;
      wanted = allAttributes;
      addPermission(new MBeanPermission("[" + delegate.getCanonicalName() + "]", "getAttribute"));
      list = server.getAttributes(delegate, allAttributes);
      assertEquals(list.size(), allowed.length);
      for (int i = 0; i < list.size(); ++i)
      {
         Attribute attr = (Attribute)list.get(i);
         assertEquals(attr.getName(), allowed[i]);
      }
   }

   protected void testGetDefaultDomain(MBeanServerConnection server, String domain) throws Exception
   {
      String result = server.getDefaultDomain();
      assertEquals(result, domain);
   }

   protected void testGetDomains(MBeanServerConnection server) throws Exception
   {
      try
      {
         server.getDomains();
         fail();
      }
      catch (SecurityException e)
      {
         // OK
      }
      ObjectName name = new ObjectName("test:x=x");
      addPermission(new MBeanPermission(Simple.class.getName(), null, name, "instantiate"));
      addPermission(new MBeanPermission(Simple.class.getName(), null, name, "registerMBean"));
      addPermission(new MBeanTrustPermission("register"));
      server.createMBean(Simple.class.getName(), name);

      addPermission(new MBeanPermission(null, null, name, "getDomains"));

      String[] results = server.getDomains();
      assertNotNull(results);
      assertEquals(results.length, 1);
      assertEquals(results[0], "test");
   }

   protected void testGetMBeanCount(MBeanServerConnection server) throws Exception
   {
      Integer count = server.getMBeanCount();
      assertNotNull(count);
      assertTrue(count.intValue() > 1);
   }

   protected void testGetMBeanInfo(MBeanServerConnection server) throws Exception
   {
      ObjectName delegate = new ObjectName("JMImplementation", "type", "MBeanServerDelegate");
      try
      {
         server.getMBeanInfo(delegate);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      addPermission(new MBeanPermission("[" + delegate.getCanonicalName() + "]", "getMBeanInfo"));
      MBeanInfo info = server.getMBeanInfo(delegate);
      assertNotNull(info);
   }

   protected void testGetObjectInstance(MBeanServerConnection server) throws Exception
   {
      ObjectName delegate = new ObjectName("JMImplementation", "type", "MBeanServerDelegate");
      try
      {
         server.getObjectInstance(delegate);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      addPermission(new MBeanPermission("[" + delegate.getCanonicalName() + "]", "getObjectInstance"));
      ObjectInstance instance = server.getObjectInstance(delegate);
      assertNotNull(instance);
   }

   protected void testInvoke(MBeanServerConnection server) throws Exception
   {
      ObjectName mbeanName = new ObjectName(server.getDefaultDomain(), "mbean", "simple");
      addPermission(new MBeanPermission(Simple.class.getName(), "instantiate, registerMBean"));
      addPermission(new MBeanTrustPermission("register"));
      server.createMBean(Simple.class.getName(), mbeanName, null);

      addPermission(new MBeanPermission(Simple.class.getName(), "setAttribute"));
      String initial = "mx4j";
      server.setAttribute(mbeanName, new Attribute("FirstAttribute", initial));

      String value = "simon";
      String operation = "concatenateWithFirstAttribute";

      try
      {
         server.invoke(mbeanName, operation, new Object[]{value}, new String[]{String.class.getName()});
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      addPermission(new MBeanPermission("#" + operation + "[" + mbeanName.getCanonicalName() + "]", "invoke"));
      String result = (String)server.invoke(mbeanName, operation, new Object[]{value}, new String[]{String.class.getName()});
      assertEquals(result, initial + value);
   }

   protected void testIsInstanceOf(MBeanServerConnection server) throws Exception
   {
      ObjectName delegate = new ObjectName("JMImplementation", "type", "MBeanServerDelegate");
      String className = "javax.management.MBeanServerDelegateMBean";

      try
      {
         server.isInstanceOf(delegate, className);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      addPermission(new MBeanPermission("[" + delegate.getCanonicalName() + "]", "isInstanceOf"));
      boolean isInstance = server.isInstanceOf(delegate, className);
      assertTrue(isInstance);
   }

   protected void testIsRegistered(MBeanServerConnection server) throws Exception
   {
      ObjectName delegate = new ObjectName("JMImplementation", "type", "MBeanServerDelegate");
      boolean registered = server.isRegistered(delegate);
      assertTrue(registered);
   }

   protected void testQueryMBeans(MBeanServerConnection server) throws Exception
   {
      ObjectName mbeanName = new ObjectName(server.getDefaultDomain(), "mbean", "simple");
      addPermission(new MBeanPermission(Simple.class.getName(), "instantiate, registerMBean"));
      addPermission(new MBeanTrustPermission("register"));
      server.createMBean(Simple.class.getName(), mbeanName, null);

      try
      {
         server.queryMBeans(null, null);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      // Check that an ObjectName that does not match returns an empty list
      ObjectName dummy = new ObjectName(server.getDefaultDomain(), "type", "dummy");
      addPermission(new MBeanPermission("[" + dummy.getCanonicalName() + "]", "queryMBeans"));
      Set mbeans = server.queryMBeans(null, null);
      assertEquals(mbeans.size(), 0);

      // Now check the right one
      resetPermissions();
      addPermission(new MBeanPermission("[" + mbeanName.getCanonicalName() + "]", "queryMBeans"));
      mbeans = server.queryMBeans(null, null);
      assertEquals(mbeans.size(), 1);
      ObjectInstance instance = (ObjectInstance)mbeans.iterator().next();
      assertEquals(instance.getObjectName(), mbeanName);

      // Check the right one for a pattern in permission
      resetPermissions();
      addPermission(new MBeanPermission("[" + server.getDefaultDomain() + ":*]", "queryMBeans"));
      mbeans = server.queryMBeans(null, null);
      assertEquals(mbeans.size(), 1);
      instance = (ObjectInstance)mbeans.iterator().next();
      assertEquals(instance.getObjectName(), mbeanName);

      // Check for another pattern
      resetPermissions();
      addPermission(new MBeanPermission("[JMImplementation:*]", "queryMBeans"));
      mbeans = server.queryMBeans(null, null);
      assertTrue(mbeans.size() >= 1);
      for (Iterator iterator = mbeans.iterator(); iterator.hasNext();)
      {
         instance = (ObjectInstance)iterator.next();
         assertEquals(instance.getObjectName().getDomain(), "JMImplementation");
      }

      // Check for all
      resetPermissions();
      addPermission(new MBeanPermission("*", "queryMBeans"));
      // Try queryNames to see if the permission is implies
      mbeans = server.queryNames(null, null);
      assertEquals(mbeans.size(), server.getMBeanCount().intValue());

      // Check for the query expression
      resetPermissions();
      String className = "mx4j.server.MX4JMBeanServerDelegate";
      addPermission(new MBeanPermission(className, "queryMBeans"));
      QueryExp exp = Query.eq(Query.attr(className, "ImplementationName"), Query.value("MX4J"));
      mbeans = server.queryMBeans(null, exp);
      assertEquals(mbeans.size(), 0);

      // Now grant also the permission to retrieve the attribute
      addPermission(new MBeanPermission(className, "getAttribute, getObjectInstance"));
      mbeans = server.queryMBeans(null, exp);
      assertEquals(mbeans.size(), 1);
      instance = (ObjectInstance)mbeans.iterator().next();
      assertEquals(instance.getClassName(), className);
   }

   protected void testQueryNames(MBeanServerConnection server) throws Exception
   {
      ObjectName mbeanName = new ObjectName(server.getDefaultDomain(), "mbean", "simple");
      addPermission(new MBeanPermission(Simple.class.getName(), "instantiate, registerMBean"));
      addPermission(new MBeanTrustPermission("register"));
      server.createMBean(Simple.class.getName(), mbeanName, null);

      try
      {
         server.queryNames(null, null);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      // Check that an ObjectName that does not match returns an empty list
      ObjectName dummy = new ObjectName(server.getDefaultDomain(), "type", "dummy");
      addPermission(new MBeanPermission("[" + dummy.getCanonicalName() + "]", "queryNames"));
      Set mbeans = server.queryNames(null, null);
      assertEquals(mbeans.size(), 0);

      // Now check the right one
      resetPermissions();
      addPermission(new MBeanPermission("[" + mbeanName.getCanonicalName() + "]", "queryNames"));
      mbeans = server.queryNames(null, null);
      assertEquals(mbeans.size(), 1);
      ObjectName name = (ObjectName)mbeans.iterator().next();
      assertEquals(name, mbeanName);

      // Check the right one for a pattern in permission
      resetPermissions();
      addPermission(new MBeanPermission("["+ server.getDefaultDomain() + ":*]", "queryNames"));
      mbeans = server.queryNames(null, null);
      assertEquals(mbeans.size(), 1);
      name = (ObjectName)mbeans.iterator().next();
      assertEquals(name, mbeanName);

      // Check for another pattern
      resetPermissions();
      addPermission(new MBeanPermission("[JMImplementation:*]", "queryNames"));
      mbeans = server.queryNames(null, null);
      assertTrue(mbeans.size() >= 1);
      for (Iterator iterator = mbeans.iterator(); iterator.hasNext();)
      {
         name = (ObjectName)iterator.next();
         assertEquals(name.getDomain(), "JMImplementation");
      }

      // Check for all
      resetPermissions();
      addPermission(new MBeanPermission("*", "queryNames"));
      mbeans = server.queryNames(null, null);
      assertEquals(mbeans.size(), server.getMBeanCount().intValue());

      // Check for the query expression
      resetPermissions();
      String className = "mx4j.server.MX4JMBeanServerDelegate";
      addPermission(new MBeanPermission(className, "queryNames"));
      QueryExp exp = Query.eq(Query.attr(className, "ImplementationName"), Query.value("MX4J"));
      mbeans = server.queryNames(null, exp);
      assertEquals(mbeans.size(), 0);

      // Now grant also the permission to retrieve the attribute
      addPermission(new MBeanPermission(className, "getAttribute, getObjectInstance"));
      mbeans = server.queryNames(null, exp);
      assertEquals(mbeans.size(), 1);
      name = (ObjectName)mbeans.iterator().next();
      assertEquals(name, new ObjectName("JMImplementation", "type", "MBeanServerDelegate"));
   }

   protected void testSetAttribute(MBeanServerConnection server) throws Exception
   {
      ObjectName name = new ObjectName(server.getDefaultDomain(), "name", "test");
      addPermission(new MBeanPermission(Simple.class.getName(), "instantiate, registerMBean"));
      addPermission(new MBeanTrustPermission("register"));
      server.createMBean(Simple.class.getName(), name, null);

      String attributeName = "FirstAttribute";
      String value = "first";
      Attribute attribute = new Attribute(attributeName, value);

      try
      {
         server.setAttribute(name, attribute);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      addPermission(new MBeanPermission("#" + attributeName + "[" + name.getCanonicalName() + "]", "setAttribute"));
      server.setAttribute(name, attribute);

      // Check that it worked
      addPermission(new MBeanPermission("#" + attributeName, "getAttribute"));
      String result = (String)server.getAttribute(name, attributeName);
      assertEquals(result, value);
   }

   protected void testSetAttributes(MBeanServerConnection server) throws Exception
   {
      ObjectName mbeanName = new ObjectName(server.getDefaultDomain(), "mbean", "simple");
      addPermission(new MBeanPermission(Simple.class.getName(), "instantiate, registerMBean"));
      addPermission(new MBeanTrustPermission("register"));
      server.createMBean(Simple.class.getName(), mbeanName, null);

      AttributeList allAttributes = new AttributeList();
      allAttributes.add(new Attribute("FirstAttribute", "first"));
      allAttributes.add(new Attribute("SecondAttribute", "second"));
      allAttributes.add(new Attribute("ThirdAttribute", "third"));
      allAttributes.add(new Attribute("Running", Boolean.TRUE));

      AttributeList allowed = new AttributeList();
      AttributeList wanted = new AttributeList();

      try
      {
         server.setAttributes(mbeanName, allAttributes);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      // Check that for wrong attribute I get an empty list
      allowed.clear();
      allowed.add(allAttributes.get(0));
      wanted.clear();
      wanted.add(allAttributes.get(1));
      addPermission(new MBeanPermission("#" + ((Attribute)allowed.get(0)).getName() + "[" + mbeanName.getCanonicalName() + "]", "setAttribute"));
      AttributeList list = server.setAttributes(mbeanName, wanted);
      assertEquals(list.size(), 0);

      // Check that for the right attribute I get it
      resetPermissions();
      allowed.clear();
      allowed.add(allAttributes.get(0));
      wanted.clear();
      wanted.addAll(allAttributes);
      addPermission(new MBeanPermission("#" + ((Attribute)allowed.get(0)).getName() + "[" + mbeanName.getCanonicalName() + "]", "setAttribute"));
      list = server.setAttributes(mbeanName, wanted);
      assertEquals(list.size(), allowed.size());
      Attribute attrib = (Attribute)list.get(0);
      assertEquals(attrib, allowed.get(0));

      // Check that if I grant some I only get some
      resetPermissions();
      // Only attributes that ends with 'Attribute'
      allowed.clear();
      allowed.add(allAttributes.get(0));
      allowed.add(allAttributes.get(1));
      allowed.add(allAttributes.get(2));
      wanted.clear();
      wanted.addAll(allAttributes);
      addPermission(new MBeanPermission("#*Attribute[" + mbeanName.getCanonicalName() + "]", "setAttribute"));
      list = server.setAttributes(mbeanName, wanted);
      assertEquals(list.size(), allowed.size());
      for (int i = 0; i < list.size(); ++i)
      {
         Attribute attr = (Attribute)list.get(i);
         assertEquals(attr, allowed.get(i));
      }

      // Check that if I grant all I get them all
      resetPermissions();
      allowed.clear();
      allowed.addAll(allAttributes);
      wanted.clear();
      wanted.addAll(allAttributes);
      addPermission(new MBeanPermission("[" + mbeanName.getCanonicalName() + "]", "setAttribute"));
      list = server.setAttributes(mbeanName, wanted);
      assertEquals(list.size(), allowed.size());
      for (int i = 0; i < list.size(); ++i)
      {
         Attribute attr = (Attribute)list.get(i);
         assertEquals(attr, allowed.get(i));
      }
   }

   protected void testUnregisterMBean(MBeanServerConnection server) throws Exception
   {
      ObjectName name = new ObjectName(server.getDefaultDomain(), "name", "test");
      addPermission(new MBeanPermission(Simple.class.getName(), "instantiate, registerMBean"));
      addPermission(new MBeanTrustPermission("register"));
      server.createMBean(Simple.class.getName(), name, null);

      try
      {
         server.unregisterMBean(name);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      addPermission(new MBeanPermission(Simple.class.getName() + "[" + name.getCanonicalName() + "]", "unregisterMBean"));
      server.unregisterMBean(name);
   }

   public interface SimpleMBean
   {
      public void setFirstAttribute(String value);

      public String getFirstAttribute();

      public void setSecondAttribute(String value);

      public String getSecondAttribute();

      public void setThirdAttribute(String value);

      public String getThirdAttribute();

      public void setRunning(boolean value);

      public boolean isRunning();

      public String concatenateWithFirstAttribute(String value);
   }

   public static class Simple implements SimpleMBean
   {
      private String firstAttribute;
      private String secondAttribute;
      private String thirdAttribute;
      private boolean running;

      public String getFirstAttribute()
      {
         return firstAttribute;
      }

      public void setFirstAttribute(String value)
      {
         this.firstAttribute = value;
      }

      public String getSecondAttribute()
      {
         return secondAttribute;
      }

      public void setSecondAttribute(String secondAttribute)
      {
         this.secondAttribute = secondAttribute;
      }

      public String getThirdAttribute()
      {
         return thirdAttribute;
      }

      public void setThirdAttribute(String thirdAttribute)
      {
         this.thirdAttribute = thirdAttribute;
      }

      public boolean isRunning()
      {
         return running;
      }

      public void setRunning(boolean running)
      {
         this.running = running;
      }

      public String concatenateWithFirstAttribute(String value)
      {
         return this.firstAttribute + value;
      }
   }
}
