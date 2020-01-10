/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.modelmbean;

import java.util.List;
import javax.management.*;
import javax.management.modelmbean.*;

import test.MX4JTestCase;
import test.MutableBoolean;
import test.MutableInteger;
import test.javax.management.modelmbean.support.ModelMBeanTarget;

/**
 * @version $Revision: 1.14 $
 */
public class RequiredModelMBeanTest extends MX4JTestCase
{
   private MBeanServer m_server;

   public RequiredModelMBeanTest(String s)
   {
      super(s);
   }

   protected void setUp() throws Exception
   {
      super.setUp();
      m_server = MBeanServerFactory.createMBeanServer("ModelMBean");
   }

   protected void tearDown() throws Exception
   {
      super.tearDown();
      MBeanServerFactory.releaseMBeanServer(m_server);
      m_server = null;
   }

   public void testCopyConstructor() throws Exception
   {
      try
      {
         new RequiredModelMBean(null);
         fail("Expecting RuntimeOperationsException");
      }
      catch (RuntimeOperationsException x)
      {
         assertTrue(true); //success
      }
   }

   public void testRegistration() throws Exception
   {
      RequiredModelMBean rmmb = new RequiredModelMBean();
      ObjectName name = new ObjectName(":type=test");
      try
      {
         m_server.registerMBean(rmmb, name);
         m_server.unregisterMBean(name);
      }
      catch (NotCompliantMBeanException x)
      {
         fail("Default RequireModelMBean cannot be registered");
      }

      try
      {
         m_server.createMBean(RequiredModelMBean.class.getName(), name, null);
         m_server.unregisterMBean(name);
      }
      catch (NotCompliantMBeanException x)
      {
         fail("Default RequireModelMBean cannot be created");
      }

      rmmb = (RequiredModelMBean)m_server.instantiate(RequiredModelMBean.class.getName(), null);
      ModelMBeanInfoSupport info = new ModelMBeanInfoSupport(Object.class.getName(), "Test", null, null, null, null);
      rmmb.setModelMBeanInfo(info);
      m_server.registerMBean(rmmb, name);
   }

   public void testGetAttributeDefault() throws Exception
   {
      ObjectName name = new ObjectName(":type=test");

      MutableInteger counter = new MutableInteger(0);
      ModelMBeanTarget bean = new ModelMBeanTarget(counter);

      String attrName = "FixedContent";

      String[] names = new String[]{"name", "descriptorType", "value", "iterable", "displayName", "default", "currencyTimeLimit"};
      // changed to match the actual behaviour indicated in the specs about currencyTimeLimit
      // currencyTimeLimit is now -1
      Object[] values = new Object[]{attrName, "attribute", null, "false", "", "DEFAULT", "-1"};
      DescriptorSupport attrDescr = new DescriptorSupport(names, values);
      ModelMBeanAttributeInfo attrInfo = new ModelMBeanAttributeInfo(attrName, String.class.getName(), "", true, false, false, attrDescr);
      ModelMBeanInfoSupport info = new ModelMBeanInfoSupport(ModelMBeanTarget.class.getName(), "", new ModelMBeanAttributeInfo[]{attrInfo}, null, null, null);

      RequiredModelMBean rmmb = new RequiredModelMBean();
      rmmb.setModelMBeanInfo(info);
      rmmb.setManagedResource(bean, "ObjectReference");
      m_server.registerMBean(rmmb, name);

      // No get method, should always get the default value back
      int num = 5;
      for (int i = 0; i < num; ++i)
      {
         String value = (String)m_server.getAttribute(name, attrName);
         assertEquals("Returned value is not the default", value, "DEFAULT");
      }

      assertEquals("Wrong staleness algorithm", 0, counter.get());
   }

   public void testGetAttributeAlwaysStale() throws Exception
   {
      ObjectName name = new ObjectName(":type=test");

      MutableInteger counter = new MutableInteger(0);
      ModelMBeanTarget bean = new ModelMBeanTarget(counter);

      String attrName = "FixedContent";

      String[] names = new String[]{"name", "descriptorType", "value", "iterable", "displayName", "default", "getMethod", "currencyTimeLimit"};
      // changed to match the actual behaviour indicated in the specs about currencyTimeLimit
      // if currencyTimeLimit is -1 then the value is always stale
      // fix for bug #794313
      Object[] values = new Object[]{attrName, "attribute", null, "false", "", "DEFAULT", "get" + attrName, "-1"};
      DescriptorSupport attrDescr = new DescriptorSupport(names, values);
      ModelMBeanAttributeInfo attrInfo = new ModelMBeanAttributeInfo(attrName, String.class.getName(), "", true, false, false, attrDescr);
      ModelMBeanInfoSupport info = new ModelMBeanInfoSupport(ModelMBeanTarget.class.getName(), "", new ModelMBeanAttributeInfo[]{attrInfo}, null, null, null);

      RequiredModelMBean rmmb = new RequiredModelMBean();
      rmmb.setModelMBeanInfo(info);
      rmmb.setManagedResource(bean, "ObjectReference");
      m_server.registerMBean(rmmb, name);

      // We set the staleness to 0 (-> always stale) so test if the bean method is always called
      String fixed = bean.getFixedContent();
      counter.set(0);
      int num = 5;
      for (int i = 0; i < num; ++i)
      {
         String value = (String)m_server.getAttribute(name, attrName);
         assertEquals("Method returned different value", value, fixed);
      }

      assertEquals("Wrong staleness algorithm: " + counter.get(), counter.get(), num);
   }

   public void testGetAttributeNeverStale() throws Exception
   {
      ObjectName name = new ObjectName(":type=test");

      MutableInteger counter = new MutableInteger(0);
      ModelMBeanTarget bean = new ModelMBeanTarget(counter);

      String attrName = "FixedContent";

      String[] names = new String[]{"name", "descriptorType", "value", "iterable", "displayName", "default", "getMethod", "currencyTimeLimit", "value"};
      // changed to match the actual behaviour indicated in the specs about currencyTimeLimit
      // if currencyTimeLimit is 0 then the value is never stale
      // fix for bug #794313
      Object[] values = new Object[]{attrName, "attribute", null, "false", "", "DEFAULT", "get" + attrName, "0", "NEVER"};
      DescriptorSupport attrDescr = new DescriptorSupport(names, values);
      ModelMBeanAttributeInfo attrInfo = new ModelMBeanAttributeInfo(attrName, String.class.getName(), "", true, false, false, attrDescr);
      ModelMBeanInfoSupport info = new ModelMBeanInfoSupport(ModelMBeanTarget.class.getName(), "", new ModelMBeanAttributeInfo[]{attrInfo}, null, null, null);

      RequiredModelMBean rmmb = new RequiredModelMBean();
      rmmb.setModelMBeanInfo(info);
      rmmb.setManagedResource(bean, "ObjectReference");
      m_server.registerMBean(rmmb, name);

      // We set the staleness to 0 (-> never stale) so test that the bean method is never called
      int num = 5;
      for (int i = 0; i < num; ++i)
      {
         String value = (String)m_server.getAttribute(name, attrName);
         assertEquals("Method returned different value", value, "NEVER");
      }

      assertEquals("Wrong staleness algorithm", counter.get(), 0);
   }

   public void testGetAttributeStale() throws Exception
   {
      ObjectName name = new ObjectName(":type=test");

      MutableInteger counter = new MutableInteger(0);
      ModelMBeanTarget bean = new ModelMBeanTarget(counter);

      String attrName = "MutableContent";

      String[] names = new String[]{"name", "descriptorType", "value", "iterable", "displayName", "default", "getMethod", "currencyTimeLimit"};
      Object[] values = new Object[]{attrName, "attribute", null, "false", "", "DEFAULT", "get" + attrName, "2"};
      DescriptorSupport attrDescr = new DescriptorSupport(names, values);
      ModelMBeanAttributeInfo attrInfo = new ModelMBeanAttributeInfo(attrName, "java.lang.String", "", true, false, false, attrDescr);
      ModelMBeanInfoSupport info = new ModelMBeanInfoSupport("test.javax.management.modelmbean.ModelMBeanTarget", "", new ModelMBeanAttributeInfo[]{attrInfo}, null, null, null);

      RequiredModelMBean rmmb = new RequiredModelMBean();
      rmmb.setModelMBeanInfo(info);
      rmmb.setManagedResource(bean, "ObjectReference");
      m_server.registerMBean(rmmb, name);

      // We set the staleness to 2 seconds

      // First time
      bean.setMutableContent("First");
      String attrValue = (String)m_server.getAttribute(name, attrName);
      assertEquals("getAttribute does not work", attrValue, "First");

      // Now value should be cached, check it
      bean.setMutableContent("Second");
      attrValue = (String)m_server.getAttribute(name, attrName);
      assertEquals("Attribute value caching does not work", attrValue, "First");

      // Now wait 2 seconds
      Thread.sleep(2000);
      attrValue = (String)m_server.getAttribute(name, attrName);
      assertEquals("Attribute staleness algorithm does not work", attrValue, "Second");
   }

   public void testGetAttributes() throws Exception
   {
      String attrName1 = "FixedContent";
      String attrName2 = "MutableContent";

      ObjectName name = new ObjectName(":type=test");

      MutableInteger counter = new MutableInteger(0);
      ModelMBeanTarget bean = new ModelMBeanTarget(counter);

      String[] names1 = new String[]{"name", "descriptorType", "value", "iterable", "displayName", "getMethod", "currencyTimeLimit"};
      // changed to match the actual behaviour indicated in the specs about currencyTimeLimit
      // currencyTimeLimit is now -1
      Object[] values1 = new Object[]{attrName1, "attribute", null, "false", "", "get" + attrName1, "-1"};
      DescriptorSupport attrDescr1 = new DescriptorSupport(names1, values1);

      String[] names2 = new String[]{"name", "descriptorType", "value", "iterable", "displayName", "getMethod", "currencyTimeLimit"};
      // changed to match the actual behaviour indicated in the specs about currencyTimeLimit
      // currencyTimeLimit is now -1
      Object[] values2 = new Object[]{attrName2, "attribute", null, "false", "", "get" + attrName2, "-1"};
      DescriptorSupport attrDescr2 = new DescriptorSupport(names2, values2);

      ModelMBeanAttributeInfo attrInfo1 = new ModelMBeanAttributeInfo(attrName1, String.class.getName(), "", true, false, false, attrDescr1);
      ModelMBeanAttributeInfo attrInfo2 = new ModelMBeanAttributeInfo(attrName2, String.class.getName(), "", true, false, false, attrDescr2);
      ModelMBeanInfoSupport info = new ModelMBeanInfoSupport(ModelMBeanTarget.class.getName(), "", new ModelMBeanAttributeInfo[]{attrInfo1, attrInfo2}, null, null, null);

      RequiredModelMBean rmmb = new RequiredModelMBean();
      rmmb.setModelMBeanInfo(info);
      rmmb.setManagedResource(bean, "ObjectReference");
      m_server.registerMBean(rmmb, name);

      String[] attributes = new String[]{attrName1, attrName2};
      AttributeList list = m_server.getAttributes(name, attributes);
      assertEquals("Wrong number of attributes", list.size(), 2);

      // Check that they're really the right ones
      Attribute attr = (Attribute)list.get(0);
      assertEquals(attr.getName(), attrName1);
      attr = (Attribute)list.get(1);
      assertEquals(attr.getName(), attrName2);

      // Test also a wrong attribute
      attributes = new String[]{attrName1, null, attrName2};
      list = m_server.getAttributes(name, attributes);
      assertEquals(list.size(), 2);

      // Check that they're really the right ones
      attr = (Attribute)list.get(0);
      assertEquals(attr.getName(), attrName1);
      attr = (Attribute)list.get(1);
      assertEquals(attr.getName(), attrName2);

      // Test also a wrong attribute
      attributes = new String[]{"NonExisting", attrName1, attrName2};
      list = m_server.getAttributes(name, attributes);
      assertEquals(list.size(), 2);
      // Check that they're really the right ones
      attr = (Attribute)list.get(0);
      assertEquals(attr.getName(), attrName1);
      attr = (Attribute)list.get(1);
      assertEquals(attr.getName(), attrName2);

      // Test also a wrong attribute
      attributes = new String[]{"NonExisting", attrName2};
      list = m_server.getAttributes(name, attributes);
      assertEquals(list.size(), 1);
      // Check that it is really the right one
      attr = (Attribute)list.get(0);
      assertEquals(attr.getName(), attrName2);
   }

   public void testSetAttribute() throws Exception
   {
      String attrName1 = "MutableContent";

      ObjectName name = new ObjectName(":type=test");

      MutableInteger counter = new MutableInteger(0);
      ModelMBeanTarget bean = new ModelMBeanTarget(counter);

      String[] names1 = new String[]{"name", "descriptorType", "value", "iterable", "displayName", "setMethod", "getMethod", "currencyTimeLimit"};
      // changed to match the actual behaviour indicated in the specs about currencyTimeLimit
      // currencyTimeLimit is now -1
      Object[] values1 = new Object[]{attrName1, "attribute", null, "false", "", "set" + attrName1, "get" + attrName1, "-1"};
      DescriptorSupport attrDescr1 = new DescriptorSupport(names1, values1);

      ModelMBeanAttributeInfo attrInfo1 = new ModelMBeanAttributeInfo(attrName1, String.class.getName(), "", true, true, false, attrDescr1);
      ModelMBeanInfoSupport info = new ModelMBeanInfoSupport(ModelMBeanTarget.class.getName(), "", new ModelMBeanAttributeInfo[]{attrInfo1}, null, null, null);

      final MutableBoolean storeTester = new MutableBoolean(false);
      RequiredModelMBean rmmb = new StoreTesterRMMB(storeTester);
      rmmb.setModelMBeanInfo(info);
      rmmb.setManagedResource(bean, "ObjectReference");
      m_server.registerMBean(rmmb, name);

      // Adding a attribute change notification listener
      final MutableInteger listenerCount = new MutableInteger(0);
      rmmb.addAttributeChangeNotificationListener(new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            listenerCount.set(listenerCount.get() + 1);
         }
      }, attrName1, null);

      String value = "SET_FIRST_TIME";
      Attribute attribute = new Attribute(attrName1, value);
      m_server.setAttribute(name, attribute);

      // check that has really been set
      assertEquals(bean.getMutableContent(), value);
      // check through MBeanServer
      assertEquals(m_server.getAttribute(name, attrName1), value);
      // check that listener has been called
      assertEquals(listenerCount.get(), 1);
      // There is no persistence settings, check that store was not called
      assertFalse("Store should not have been called", storeTester.get());

      // Adding a attribute change notification listener with
      // null as attribute. test for bug #742389
      NotificationListener dummyListener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
         }
      };

      rmmb.addAttributeChangeNotificationListener(dummyListener, null, null);
      rmmb.removeAttributeChangeNotificationListener(dummyListener, null);

      // Change the persist policy - have to unregeister to call setModelMBeanInfo
      m_server.unregisterMBean(name);
      attrDescr1.setField("persistPolicy", "OnUpdate");
      info.setDescriptor(attrDescr1, "attribute");
      rmmb.setModelMBeanInfo(info);
      storeTester.set(false);
      m_server.registerMBean(rmmb, name);

      value = "SET_SECOND_TIME";
      attribute = new Attribute(attrName1, value);
      m_server.setAttribute(name, attribute);

      // check that listener has been called
      assertEquals(listenerCount.get(), 2);
      // There are persistence settings, check that store was called
      assertTrue("Store should have been called", storeTester.get());

      // Now remove setMethod - again we have to unregister
      m_server.unregisterMBean(name);
      names1 = new String[]{"name", "descriptorType", "value", "iterable", "displayName", "getMethod", "currencyTimeLimit"};
      // changed to match the actual behaviour indicated in the specs about currencyTimeLimit
      // currencyTimeLimit is now -1
      values1 = new Object[]{attrName1, "attribute", null, "false", "", "get" + attrName1, "-1"};
      attrDescr1 = new DescriptorSupport(names1, values1);
      attrDescr1.setField("persistPolicy", "OnUpdate");
      info.setDescriptor(attrDescr1, "attribute");
      rmmb.setModelMBeanInfo(info);
      storeTester.set(false);
      m_server.registerMBean(rmmb, name);

      value = "SET_THIRD_TIME";
      attribute = new Attribute(attrName1, value);
      m_server.setAttribute(name, attribute);

      // check that listener has been called
      assertEquals(listenerCount.get(), 3);
      // There are persistence settings, check that store was called
      assertTrue("Store should have been called", storeTester.get());
      // Check the attribute value
      if (bean.getMutableContent().equals(value))
      {
         fail("No setMethod, bean should not have been modified");
      }
      if (info.getAttribute(attrName1).getDescriptor().getFieldValue("value") != null)
      {
         fail("New value should not have been cached since currencyTimeLimit is negative");
      }

      // Test attribute that takes array as parameters
      m_server.unregisterMBean(name);
      String attrName = "ArrayAttribute";
      String[] names = new String[]{"name", "descriptorType", "value", "iterable", "displayName", "getMethod", "setMethod", "currencyTimeLimit"};
      // changed to match the actual behaviour indicated in the specs about currencyTimeLimit
      // currencyTimeLimit is now -1
      Object[] values = new Object[]{attrName, "attribute", null, "true", "", "get" + attrName, "set" + attrName, "-1"};
      Descriptor attrDescr = new DescriptorSupport(names, values);

      ModelMBeanAttributeInfo attrInfo = new ModelMBeanAttributeInfo(attrName, new String[0].getClass().getName(), "", true, true, false, attrDescr);
      info = new ModelMBeanInfoSupport(ModelMBeanTarget.class.getName(), "", new ModelMBeanAttributeInfo[]{attrInfo}, null, null, null);
      rmmb.setModelMBeanInfo(info);
      m_server.registerMBean(rmmb, name);

      String[] v = new String[]{"one", "two"};
      attribute = new Attribute(attrName, v);
      m_server.setAttribute(name, attribute);
   }

   public void testSetAttributes() throws Exception
   {
      String attrName1 = "MutableContent";
      String attrName2 = "MutableContent2";

      ObjectName name = new ObjectName(":type=test");

      MutableInteger counter = new MutableInteger(0);
      ModelMBeanTarget bean = new ModelMBeanTarget(counter);

      String[] names1 = new String[]{"name", "descriptorType", "value", "iterable", "displayName", "getMethod", "setMethod", "currencyTimeLimit"};
      // changed to match the actual behaviour indicated in the specs about currencyTimeLimit
      // currencyTimeLimit is now -1
      Object[] values1 = new Object[]{attrName1, "attribute", null, "false", "", "get" + attrName1, "set" + attrName1, "-1"};
      DescriptorSupport attrDescr1 = new DescriptorSupport(names1, values1);

      String[] names2 = new String[]{"name", "descriptorType", "value", "iterable", "displayName", "getMethod", "setMethod", "currencyTimeLimit"};
      // changed to match the actual behaviour indicated in the specs about currencyTimeLimit
      // currencyTimeLimit is now -1
      Object[] values2 = new Object[]{attrName2, "attribute", null, "false", "", "get" + attrName2, "set" + attrName2, "-1"};
      DescriptorSupport attrDescr2 = new DescriptorSupport(names2, values2);

      ModelMBeanAttributeInfo attrInfo1 = new ModelMBeanAttributeInfo(attrName1, String.class.getName(), "", true, true, false, attrDescr1);
      ModelMBeanAttributeInfo attrInfo2 = new ModelMBeanAttributeInfo(attrName2, "int", "", true, true, false, attrDescr2);
      ModelMBeanInfoSupport info = new ModelMBeanInfoSupport(ModelMBeanTarget.class.getName(), "", new ModelMBeanAttributeInfo[]{attrInfo1, attrInfo2}, null, null, null);

      RequiredModelMBean rmmb = new RequiredModelMBean();
      rmmb.setModelMBeanInfo(info);
      rmmb.setManagedResource(bean, "ObjectReference");
      m_server.registerMBean(rmmb, name);

      Attribute attr1 = new Attribute(attrName1, "FIRST");
      Attribute attr2 = new Attribute(attrName2, new Integer(5));
      AttributeList list = new AttributeList();
      list.add(attr1);
      list.add(attr2);
      AttributeList result = m_server.setAttributes(name, list);
      assertEquals("Wrong number of attributes were set", result.size(), 2);
      // Check that they're really the right ones
      Attribute attr = (Attribute)result.get(0);
      assertEquals(attr, attr1);
      attr = (Attribute)result.get(1);
      assertEquals(attr, attr2);
      // Check that they were really set
      assertEquals(bean.getMutableContent(), attr1.getValue());
      assertEquals(bean.getMutableContent2(), ((Integer)attr2.getValue()).intValue());

      // Test non-existing attribute
      attr = new Attribute("NonExisting", null);
      attr2 = new Attribute(attrName2, new Integer(7));
      list.clear();
      list.add(attr);
      list.add(attr2);
      result = m_server.setAttributes(name, list);
      assertEquals(result.size(), 1);
      // Check that they're really the right ones
      attr = (Attribute)result.get(0);
      assertEquals(attr, attr2);
      // Check that they were really set
      assertEquals(bean.getMutableContent2(), ((Integer)attr2.getValue()).intValue());

      attr = new Attribute("NonExisting", null);
      list.clear();
      list.add(attr);
      result = m_server.setAttributes(name, list);
      assertEquals(result.size(), 0);
   }

   public void testInvoke() throws Exception
   {
      String operation = "operation1";

      ObjectName name = new ObjectName(":type=test");

      MutableInteger counter = new MutableInteger(0);
      ModelMBeanTarget bean = new ModelMBeanTarget(counter);

      String[] names1 = new String[]{"name", "descriptorType", "displayName", "role", "targetObject", "targetObjectType", "currencyTimeLimit"};
      // changed to match the actual behaviour indicated in the specs about currencyTimeLimit
      // currencyTimeLimit is now -1
      Object[] values1 = new Object[]{operation, "operation", "", "operation", null, null, "-1"};
      DescriptorSupport operDescr = new DescriptorSupport(names1, values1);

      MBeanParameterInfo paramInfo1 = new MBeanParameterInfo("c", "char", "");
      MBeanParameterInfo paramInfo2 = new MBeanParameterInfo("s", "short", "");
      MBeanParameterInfo paramInfo3 = new MBeanParameterInfo("f", new float[0].getClass().getName(), "");
      MBeanParameterInfo paramInfo4 = new MBeanParameterInfo("c", new Object[0][0].getClass().getName(), "");
      ModelMBeanOperationInfo operInfo = new ModelMBeanOperationInfo(operation, "", new MBeanParameterInfo[]{paramInfo1, paramInfo2, paramInfo3, paramInfo4}, "java.util.List", ModelMBeanOperationInfo.UNKNOWN, operDescr);

      ModelMBeanInfoSupport info = new ModelMBeanInfoSupport(ModelMBeanTarget.class.getName(), "", null, null, new ModelMBeanOperationInfo[]{operInfo}, null);

      RequiredModelMBean rmmb = new RequiredModelMBean();
      rmmb.setModelMBeanInfo(info);
      rmmb.setManagedResource(bean, "ObjectReference");
      m_server.registerMBean(rmmb, name);

      short s = 10;
      Object[] args = new Object[]{new Character('z'), new Short(s), new float[]{1.0F}, new Object[][]{{"Hello"}, {"World"}}};
      String[] params = new String[]{paramInfo1.getType(), paramInfo2.getType(), paramInfo3.getType(), paramInfo4.getType()};
      List list = (List)m_server.invoke(name, operation, args, params);

      // Test that was really called
      assertEquals(counter.get(), 1);
      // Right values ?
      for (int i = 0; i < list.size(); ++i)
      {
         Object obj = list.get(i);
         assertEquals("Returned value is different: " + obj, args[i], obj);
      }

      m_server.unregisterMBean(name);
      ModelMBeanTarget.TargetBean target = new ModelMBeanTarget.TargetBean();
      operDescr.setField("targetObject", target);
      operDescr.setField("targetObjectType", "ObjectReference");

      info.setDescriptor(operDescr, "operation");
      rmmb.setModelMBeanInfo(info);
      m_server.registerMBean(rmmb, name);

      list = (List)m_server.invoke(name, operation, args, params);

      // Test that was not called
      assertEquals("Operation should not have been called", counter.get(), 1);
      // Right values ?
      for (int i = 0; i < list.size(); ++i)
      {
         Object obj = list.get(list.size() - 1 - i);
         assertEquals("Returned value is different: " + obj, args[i], obj);
      }
   }

   public void testInvokeModelMBeanMethods() throws Exception
   {
      ObjectName name = new ObjectName(":type=test");

      ModelMBean mmb = (ModelMBean)m_server.instantiate(RequiredModelMBean.class.getName(), null);

      ModelMBeanInfoSupport info = new ModelMBeanInfoSupport(ModelMBeanTarget.class.getName(), "", null, null, null, null);

      mmb.setModelMBeanInfo(info);

      m_server.registerMBean(mmb, name);

      // Now try to invoke methods that are part of the ModelMBean interface

      try
      {
         m_server.setAttribute(name, new Attribute("ModelMBeanInfo", info));
         fail("Cannot invoke a ModelMBean method via MBeanServer");
      }
      catch (Exception ignored)
      {
      }

      // Bug #940161 Required ModelMBean methods are not invoked
      m_server.invoke(name, "setManagedResource", new Object[]{new ModelMBeanTarget(new MutableInteger(0)), "ObjectReference"}, new String[]{Object.class.getName(), String.class.getName()});
      m_server.invoke(name, "store", new Object[0], new String[0]);
      m_server.invoke(name, "sendNotification", new Object[]{"generic"}, new String[]{String.class.getName()});

      // Now specify setManagedResource as an operation in the MMBI
      m_server.unregisterMBean(name);
      String operation = "setManagedResource";
      MBeanParameterInfo paramInfo1 = new MBeanParameterInfo("resource", Object.class.getName(), "");
      MBeanParameterInfo paramInfo2 = new MBeanParameterInfo("type", String.class.getName(), "");
      ModelMBeanOperationInfo operInfo = new ModelMBeanOperationInfo(operation, "", new MBeanParameterInfo[]{paramInfo1, paramInfo2}, null, ModelMBeanOperationInfo.ACTION, null);
      info = new ModelMBeanInfoSupport(ModelMBeanTarget.class.getName(), "", null, null, new ModelMBeanOperationInfo[]{operInfo}, null);
      mmb.setModelMBeanInfo(info);
      m_server.registerMBean(mmb, name);

      // Spec says I must be able to invoke it
      Object target = new ModelMBeanTarget(new MutableInteger(0));
      m_server.invoke(name, operation, new Object[]{target, "ObjectReference"}, new String[]{Object.class.getName(), String.class.getName()});
   }

   public void testNotifications() throws Exception
   {
      ObjectName name = new ObjectName(":type=test");

      ModelMBeanNotificationInfo notification[] = new ModelMBeanNotificationInfo[1];
      notification[0] = new ModelMBeanNotificationInfo(new String[]{ModelMBeanTarget.class.getName() + ".notification"}, "name", "");

      ModelMBeanAttributeInfo attributeInfo = new ModelMBeanAttributeInfo("MutableContent", String.class.getName(), "", true, true, false);
      ModelMBeanInfoSupport info = new ModelMBeanInfoSupport(ModelMBeanTarget.class.getName(), "", new ModelMBeanAttributeInfo[]{attributeInfo}, null, null, notification);

      RequiredModelMBean rmmb = new RequiredModelMBean();
      rmmb.setModelMBeanInfo(info);
      rmmb.setManagedResource(new ModelMBeanTarget(new MutableInteger(0)), "objectReference");
      m_server.registerMBean(rmmb, name);

      Object listenerHandback = new Object();

      TestNotificationListener listener = new TestNotificationListener();
      m_server.addNotificationListener(name, listener, null, listenerHandback);

      rmmb.sendNotification("generic notification");
      assertEquals("jmx.modelmbean.generic", listener.type);
      assertEquals("generic notification", listener.message);
      assertSame(listenerHandback, listener.handback);

      rmmb.sendNotification(new Notification("my.type", rmmb, 1, "a message"));
      assertEquals("my.type", listener.type);
      assertEquals("a message", listener.message);
      assertSame(listenerHandback, listener.handback);

      m_server.setAttribute(name, new Attribute("MutableContent", "Hello World"));
      assertEquals("jmx.attribute.change", listener.type);
   }

   public void testGetNotificationInfo() throws Exception
   {
      RequiredModelMBean rmmb = new RequiredModelMBean();
      MBeanNotificationInfo[] notificationInfos = rmmb.getNotificationInfo();
      assertEquals(2, notificationInfos.length);

      ModelMBeanNotificationInfo notification[] = new ModelMBeanNotificationInfo[1];
      notification[0] = new ModelMBeanNotificationInfo(new String[]{ModelMBeanTarget.class.getName() + ".notification"}, "name", "");

      ModelMBeanInfoSupport info = new ModelMBeanInfoSupport(ModelMBeanTarget.class.getName(), "", null, null, null, notification);
      rmmb.setModelMBeanInfo(info);
      notificationInfos = rmmb.getNotificationInfo();
      assertEquals(3, notificationInfos.length);
   }

   public static class StoreTesterRMMB extends RequiredModelMBean
   {
      private MutableBoolean m_stored;

      public StoreTesterRMMB(MutableBoolean storeTester) throws MBeanException
      {
         m_stored = storeTester;
      }

      public void store() throws MBeanException, RuntimeOperationsException, InstanceNotFoundException
      {
         m_stored.set(true);
         super.store();
      }
   }

   public static class TestNotificationListener implements NotificationListener
   {
      String type = null;
      String message = null;
      Object handback = null;

      public void handleNotification(Notification notification, Object handback)
      {
         type = notification.getType();
         message = notification.getMessage();
         this.handback = handback;
      }
   }
}
