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
import javax.management.MBeanParameterInfo;
import javax.management.ReflectionException;


/**
 * @version $Revision: 1.3 $
 */

class PrivateMBeanDynamic implements DynamicMBean

{

   private String m_value1 = "";

   private String m_value2 = "";


   public MBeanInfo getMBeanInfo()

   {

      MBeanAttributeInfo[] attrs = new MBeanAttributeInfo[2];

      attrs[0] = new MBeanAttributeInfo("DynamicAttribute1", "java.lang.String", "A first dynamic attribute", true, true, false);

      attrs[1] = new MBeanAttributeInfo("DynamicAttribute2", "java.lang.String", "A second dynamic attribute", true, true, false);


      MBeanConstructorInfo[] ctors = new MBeanConstructorInfo[1];

      ctors[0] = new MBeanConstructorInfo("Parameterless Constructor", "A dynamic constructor", new MBeanParameterInfo[0]);


      MBeanOperationInfo[] opers = new MBeanOperationInfo[1];

      MBeanParameterInfo[] params = new MBeanParameterInfo[1];

      params[0] = new MBeanParameterInfo("supposedAttributeValue", "java.lang.String", "Checks if the value of the argument is equal to the value of the attribute");

      opers[0] = new MBeanOperationInfo("dynamicOperation", "A dynamic operation", params, "boolean", MBeanOperationInfo.INFO);


      MBeanNotificationInfo[] notifs = new MBeanNotificationInfo[0];


      return new MBeanInfo(getClass().getName(), "A MBeanDynamic MBean", attrs, ctors, opers, notifs);

   }


   private String getDynamicAttribute1()
   {
      return m_value1;
   }

   private void setDynamicAttribute1(String value)
   {
      m_value1 = value;
   }

   private String getDynamicAttribute2()
   {
      return m_value2;
   }

   private void setDynamicAttribute2(String value)
   {
      m_value2 = value;
   }

   private boolean dynamicOperation(String value)
   {
      return m_value1.equals(value);
   }


   public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException

   {

      if (attribute.equals("DynamicAttribute1"))
      {
         return getDynamicAttribute1();
      }

      else if (attribute.equals("DynamicAttribute2"))
      {
         return getDynamicAttribute2();
      }

      else
         throw new AttributeNotFoundException(attribute);

   }


   public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException

   {

      if (attribute.getName().equals("DynamicAttribute1"))

      {

         Object val = attribute.getValue();

         if (val instanceof String)
         {
            setDynamicAttribute1((String)val);
         }

         else
         {
            throw new InvalidAttributeValueException(val == null ? "null" : val.toString());
         }

      }

      else if (attribute.getName().equals("DynamicAttribute2"))

      {

         Object val = attribute.getValue();

         if (val instanceof String)
         {
            setDynamicAttribute2((String)val);
         }

         else
         {
            throw new InvalidAttributeValueException(val == null ? "null" : val.toString());
         }

      }

      else
      {
         throw new AttributeNotFoundException(attribute.getName());
      }

   }


   public AttributeList getAttributes(String[] attributes)

   {

      AttributeList list = new AttributeList();

      for (int i = 0; i < attributes.length; ++i)

      {

         if (attributes[i].equals("DynamicAttribute1"))

         {

            list.add(new Attribute(attributes[i], getDynamicAttribute1()));

         }

         else if (attributes[i].equals("DynamicAttribute2"))

         {

            list.add(new Attribute(attributes[i], getDynamicAttribute2()));

         }

      }

      return list;

   }


   public AttributeList setAttributes(AttributeList attributes)

   {

      AttributeList list = new AttributeList();

      for (int i = 0; i < attributes.size(); ++i)

      {

         Attribute attr = (Attribute)attributes.get(i);

         if (attr.getName().equals("DynamicAttribute1") || attr.getName().equals("DynamicAttribute2"))

         {

            try

            {

               setAttribute(attr);

               list.add(attr);

            }

            catch (AttributeNotFoundException ignored)
            {
            }

            catch (InvalidAttributeValueException ignored)
            {
            }

            catch (MBeanException ignored)
            {
            }

            catch (ReflectionException ignored)
            {
            }

         }

      }

      return list;

   }


   public Object invoke(String method, Object[] arguments, String[] params) throws MBeanException, ReflectionException

   {

      if (method.equals("dynamicOperation") &&

          params.length == 1 &&

          params[0].equals("java.lang.String") &&

          arguments.length == 1 &&

          arguments[0] instanceof String)

      {

         boolean match = dynamicOperation((String)arguments[0]);

         return new Boolean(match);

      }

      else

      {

         throw new MBeanException(new IllegalArgumentException("Invalid method or arguments for invoke"));

      }

   }

}

