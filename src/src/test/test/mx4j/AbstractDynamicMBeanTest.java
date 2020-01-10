/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j;

import java.util.ArrayList;
import java.util.List;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.ReflectionException;

import mx4j.AbstractDynamicMBean;
import test.MX4JTestCase;

/**
 * @version $Revision: 1.5 $
 */
public class AbstractDynamicMBeanTest extends MX4JTestCase
{
   public AbstractDynamicMBeanTest(String s)
   {
      super(s);
   }

   public void testRegistration() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      ObjectName name = new ObjectName("domain", "mbean", "dynamic");
      Dynamic mbean = new Dynamic();
      server.registerMBean(mbean, name);
   }

   public void testGetAttibuteForIsAttribute() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      ObjectName name = new ObjectName("domain", "mbean", "dynamic");
      Dynamic mbean = new Dynamic();
      server.registerMBean(mbean, name);
      Object result = server.getAttribute(name, "Running");
      if (!((Boolean)result).booleanValue()) fail("getAttribute does not work");
   }

   public void testGetAttibuteForGetAttribute() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      ObjectName name = new ObjectName("domain", "mbean", "dynamic");
      Dynamic mbean = new Dynamic();
      server.registerMBean(mbean, name);
      Object result = server.getAttribute(name, "Name");
      assertEquals(result, mbean.getName());
   }

   public void testGetAttibuteForPrimitiveType() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      ObjectName name = new ObjectName("domain", "mbean", "dynamic");
      DynamicDerived mbean = new DynamicDerived();
      server.registerMBean(mbean, name);
      Integer result = (Integer)server.getAttribute(name, "Status");
      assertEquals(result.intValue(), mbean.getStatus());
   }

   public void testSetAttibute() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      ObjectName name = new ObjectName("domain", "mbean", "dynamic");
      Dynamic mbean = new Dynamic();
      server.registerMBean(mbean, name);

      String value = "simon";
      server.setAttribute(name, new Attribute("Name", value));

      assertEquals(value, mbean.getName());

      Object result = server.getAttribute(name, "Name");
      assertEquals(result, value);
   }

   public void testSetAttributeWithPrimitiveType() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      ObjectName name = new ObjectName("domain", "mbean", "dynamic");
      DynamicDerived mbean = new DynamicDerived();
      server.registerMBean(mbean, name);

      Integer value = new Integer(13);
      server.setAttribute(name, new Attribute("Status", value));

      Integer result = (Integer)server.getAttribute(name, "Status");
      assertEquals(result.intValue(), value.intValue());
   }

   public void testSetAttributeWithNullValue() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      ObjectName name = new ObjectName("domain", "mbean", "dynamic");
      Dynamic mbean = new Dynamic();
      server.registerMBean(mbean, name);

      String value = null;
      server.setAttribute(name, new Attribute("Name", value));

      assertEquals(value, mbean.getName());

      Object result = server.getAttribute(name, "Name");
      assertEquals(result, value);
   }

   public void testOperation() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      ObjectName name = new ObjectName("domain", "mbean", "dynamic");
      Dynamic mbean = new Dynamic();
      server.registerMBean(mbean, name);

      String key = "key";
      Object value = new Object();
      List list = (List)server.invoke(name, "operation", new Object[]{key, value}, new String[]{String.class.getName(), Object.class.getName()});
      assertEquals(list.size(), 2);
      assertEquals(list.get(0), key);
      assertEquals(list.get(1), value);
   }

   public void testInvocationOfMethodsNotPresentInMBeanInfo() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      ObjectName name = new ObjectName("domain", "mbean", "dynamic");
      Dynamic mbean = new Dynamic();
      server.registerMBean(mbean, name);

      try
      {
         server.getAttribute(name, "MBeanInfo");
         fail("getMBeanInfo should not be invocable");
      }
      catch (AttributeNotFoundException x)
      {
      }
   }

   public void testInvocationOfNonExistingSetter() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      ObjectName name = new ObjectName("domain", "mbean", "dynamic");
      Dynamic mbean = new Dynamic();
      server.registerMBean(mbean, name);

      try
      {
         server.setAttribute(name, new Attribute("Running", Boolean.FALSE));
         fail("getMBeanInfo should not be invocable");
      }
      catch (ReflectionException x)
      {
      }
   }

   public void testComposedDynamicMBean() throws Exception
   {
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      ObjectName name = new ObjectName("domain", "mbean", "dynamic");
      ComposedDynamicMBean mbean = new ComposedDynamicMBean();
      server.registerMBean(mbean, name);

      // Try to invoke an attribute
      int value = 17;
      mbean.setStatus(value);
      Integer result = (Integer)server.getAttribute(name, "Status");
      if (result.intValue() != value) fail("getAttribute does not work");

      // Set the attribute
      value = 3;
      server.setAttribute(name, new Attribute("Status", new Integer(value)));
      if (mbean.getStatus() != value) fail("setAttribute does not work");

      // Invoke operations
      mbean.disable();
      server.invoke(name, "enable", null, null);
      if (!mbean.isEnabled()) fail("invoke does not work");

      server.invoke(name, "disable", null, null);
      if (mbean.isEnabled()) fail("invoke does not work");
   }

   public static class Dynamic extends AbstractDynamicMBean
   {
      private String m_name = "dummy";

      protected MBeanAttributeInfo[] createMBeanAttributeInfo()
      {
         return new MBeanAttributeInfo[]
         {
            new MBeanAttributeInfo("Name", String.class.getName(), "The name", true, true, false),
            new MBeanAttributeInfo("Running", boolean.class.getName(), "The running status", true, false, true)
         };
      }

      protected MBeanOperationInfo[] createMBeanOperationInfo()
      {
         return new MBeanOperationInfo[]
         {
            new MBeanOperationInfo("operation", "An operation", new MBeanParameterInfo[]
            {
               new MBeanParameterInfo("key", String.class.getName(), "The key"),
               new MBeanParameterInfo("value", Object.class.getName(), "The value")
            }, List.class.getName(), MBeanOperationInfo.INFO)
         };
      }

      public String getName()
      {
         return m_name;
      }

      public void setName(String name)
      {
         m_name = name;
      }

      public boolean isRunning()
      {
         return true;
      }

      public List operation(String key, Object value)
      {
         ArrayList list = new ArrayList();
         list.add(key);
         list.add(value);
         return list;
      }
   }

   public static class DynamicDerived extends Dynamic
   {
      private int m_status;

      protected MBeanAttributeInfo[] createMBeanAttributeInfo()
      {
         MBeanAttributeInfo[] info = super.createMBeanAttributeInfo();
         MBeanAttributeInfo[] newInfo = new MBeanAttributeInfo[info.length + 1];
         System.arraycopy(info, 0, newInfo, 0, info.length);
         newInfo[info.length] = new MBeanAttributeInfo("Status", int.class.getName(), "The status", true, true, false);
         return newInfo;
      }

      public MBeanInfo getMBeanInfo()
      {
         // Disable caching
         return createMBeanInfo();
      }

      public int getStatus()
      {
         return m_status;
      }

      public void setStatus(int status)
      {
         m_status = status;
      }
   }

   public static class ComposedDynamicMBean implements DynamicMBean
   {
      private AbstractDynamicMBean delegate = new AbstractDynamicMBean()
      {
         protected MBeanAttributeInfo[] createMBeanAttributeInfo()
         {
            return new MBeanAttributeInfo[]
            {
               new MBeanAttributeInfo("Status", int.class.getName(), "The status", true, true, false),
               new MBeanAttributeInfo("Enabled", boolean.class.getName(), "The enable status", true, false, true)
            };
         }

         protected MBeanOperationInfo[] createMBeanOperationInfo()
         {
            return new MBeanOperationInfo[]
            {
               new MBeanOperationInfo("enable", "Enables this MBean", new MBeanParameterInfo[0], Void.class.getName(), MBeanOperationInfo.ACTION),
               new MBeanOperationInfo("disable", "Disables this MBean", new MBeanParameterInfo[0], Void.class.getName(), MBeanOperationInfo.ACTION)
            };
         }
      };

      private int status;
      private boolean enabled;

      public ComposedDynamicMBean()
      {
         delegate.setResource(this);
      }

      public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException
      {
         return delegate.getAttribute(attribute);
      }

      public AttributeList getAttributes(String[] attributes)
      {
         return delegate.getAttributes(attributes);
      }

      public MBeanInfo getMBeanInfo()
      {
         return delegate.getMBeanInfo();
      }

      public Object invoke(String method, Object[] arguments, String[] params) throws MBeanException, ReflectionException
      {
         return delegate.invoke(method, arguments, params);
      }

      public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
      {
         delegate.setAttribute(attribute);
      }

      public AttributeList setAttributes(AttributeList attributes)
      {
         return delegate.setAttributes(attributes);
      }

      public void setStatus(int status)
      {
         this.status = status;
      }

      public int getStatus()
      {
         return status;
      }

      public boolean isEnabled()
      {
         return this.enabled;
      }

      public void enable()
      {
         this.enabled = true;
      }

      public void disable()
      {
         this.enabled = false;
      }
   }
}
