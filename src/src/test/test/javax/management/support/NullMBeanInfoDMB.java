/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.support;

import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.DynamicMBean;
import javax.management.InvalidAttributeValueException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectName;
import javax.management.ReflectionException;

/**
 * @version $Revision: 1.4 $
 */
public class NullMBeanInfoDMB implements DynamicMBean, MBeanRegistration
{
   private boolean registered;

   public MBeanInfo getMBeanInfo()
   {
      return (registered)
             ? null
             : new MBeanInfo("test.javax.management.support.NullMBeanInfoDMB",
                             "A DynamicMBean that returns null in response to getMBeanInfo() invocations",
                             new MBeanAttributeInfo[0],
                             new MBeanConstructorInfo[0],
                             new MBeanOperationInfo[0],
                             new MBeanNotificationInfo[0]);
   }

   public Object getAttribute(String attribute)
           throws AttributeNotFoundException, MBeanException, ReflectionException
   {
      return null;
   }

   public void setAttribute(Attribute attribute)
           throws
           AttributeNotFoundException,
           InvalidAttributeValueException,
           MBeanException,
           ReflectionException
   {
   }

   public AttributeList getAttributes(String[] attributes)
   {
      return null;
   }

   public AttributeList setAttributes(AttributeList attributes)
   {
      return null;
   }

   public Object invoke(String method, Object[] arguments, String[] params)
           throws MBeanException, ReflectionException
   {
      return null;
   }

   public void postDeregister()
   {
   }

   public void postRegister(Boolean registrationDone)
   {
      registered = registrationDone.booleanValue();
   }

   public void preDeregister() throws Exception
   {
   }

   public ObjectName preRegister(MBeanServer server, ObjectName name)
           throws Exception
   {
      return name;
   }

}
