/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.JMRuntimeException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;
import javax.management.RuntimeMBeanException;

import test.MX4JTestCase;
import test.javax.management.support.MBeanDynamic;

/**
 * @version $Revision: 1.10 $
 */
public class DynamicMBeanFunctionalityTest extends MX4JTestCase
{
   private MBeanServer m_server;
   private ObjectName m_name;

   public DynamicMBeanFunctionalityTest(String s)
   {
      super(s);
   }

   protected void setUp() throws Exception
   {
      m_server = MBeanServerFactory.createMBeanServer("dynamic");
      m_name = new ObjectName(":type=dynamic");
      Object dynamic = new MBeanDynamic();
      m_server.registerMBean(dynamic, m_name);
   }

   protected void tearDown() throws Exception
   {
      m_server.unregisterMBean(m_name);
      MBeanServerFactory.releaseMBeanServer(m_server);
   }

   public void testGetAttributes() throws Exception
   {
      AttributeList list = null;

      list = m_server.getAttributes(m_name, new String[0]);
      assertEquals(list.size(), 0);

      list = m_server.getAttributes(m_name, new String[]{"doesNotExist"});
      if (list.size() != 0)
      {
         fail("Attribute does not exist");
      }

      String attributeName = "DynamicAttribute1";

      list = m_server.getAttributes(m_name, new String[]{attributeName});
      if (list.size() != 1 && ((Attribute)list.get(0)).getName().equals(attributeName))
      {
         fail("Attribute exists");
      }
   }

   public void testGetSetAttribute() throws Exception
   {
      String attributeName = "DynamicAttribute1";

      Object valueBefore = m_server.getAttribute(m_name, attributeName);

      Object newValue = "newValue";
      Attribute attribute = new Attribute(attributeName, newValue);
      m_server.setAttribute(m_name, attribute);

      Object valueAfter = m_server.getAttribute(m_name, attributeName);
      if (valueAfter.equals(valueBefore) || !valueAfter.equals(newValue))
      {
         fail("setAttribute does not work");
      }
   }

   public void testSetAttributes() throws Exception
   {
      String attributeName1 = "DynamicAttribute1";
      Object value1Before = m_server.getAttribute(m_name, attributeName1);
      String attributeName2 = "DynamicAttribute2";
      Object value2Before = m_server.getAttribute(m_name, attributeName2);
      AttributeList changeThese = new AttributeList();
      AttributeList list = m_server.setAttributes(m_name, changeThese);
      if (list.size() != 0)
      {
         fail("No Attributes were changed");
      }
      if (!value1Before.equals(m_server.getAttribute(m_name, attributeName1)) || !value2Before.equals(m_server.getAttribute(m_name, attributeName2)))
      {
         fail("Attribute was not changed");
      }
      Attribute attr = new Attribute(attributeName2, "Value2");
      changeThese.add(attr);
      list = m_server.setAttributes(m_name, changeThese);
      if (list.size() != 1)
      {
         fail("One attribute was changed");
      }
      if (!list.get(0).equals(attr))
      {
         fail("Wrong return value");
      }
      if (!value1Before.equals(m_server.getAttribute(m_name, attributeName1)) ||
          value2Before.equals(m_server.getAttribute(m_name, attributeName2)) ||
          !attr.getValue().equals(m_server.getAttribute(m_name, attributeName2)))
      {
         fail("Attribute was not changed");
      }
   }

   public void testInvoke() throws Exception
   {
      String attributeName1 = "DynamicAttribute1";
      Object value1 = m_server.getAttribute(m_name, attributeName1);

      Boolean result = (Boolean)m_server.invoke(m_name, "dynamicOperation", new Object[]{"dummy"}, new String[]{"java.lang.String"});
      if (result.booleanValue())
      {
         fail("Operation does not work");
      }
      result = (Boolean)m_server.invoke(m_name, "dynamicOperation", new Object[]{value1}, new String[]{"java.lang.String"});
      if (!result.booleanValue())
      {
         fail("Operation does not work");
      }
   }

   public void testGetMBeanInfoRegistrationException() throws Exception
   {
      try
      {
         m_server.createMBean("test.javax.management.support.ExceptionGeneratingDMB",
                              new ObjectName(":register=no"),
                              new Object[]{new Boolean(false)},
                              new String[]{"boolean"});
         fail("Expecting NotCompliantMBeanException");
      }
      catch (NotCompliantMBeanException x)
      {
      }
   }

   public void testGetMBeanInfoInvocationException() throws Exception
   {
      try
      {
         ObjectName objname = new ObjectName(":register=yes");
         m_server.createMBean("test.javax.management.support.ExceptionGeneratingDMB",
                              objname,
                              new Object[]{new Boolean(true)},
                              new String[]{"boolean"});
         m_server.getMBeanInfo(objname);
         fail("Expecting RuntimeMBeanException");
      }
      catch (RuntimeMBeanException x)
      {
      }
   }

   public void testNullMBeanInfo() throws Exception
   {
      try
      {
         ObjectName objname = new ObjectName(":id=testNullMBeanInfo");
         m_server.createMBean("test.javax.management.support.NullMBeanInfoDMB", objname);
         m_server.getMBeanInfo(objname);
         fail("Expecting JMRuntimeException");
      }
      catch (JMRuntimeException x)
      {
      }
   }
}
