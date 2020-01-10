/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.support;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
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
import javax.management.ReflectionException;

/**
 * @version $Revision: 1.5 $
 */
public class QuerySupport
{
   public interface TestMBean
   {
      public Integer getNumber();

      public String getStr();

      public Boolean getBoolean();
   }

   public static class Test implements TestMBean
   {
      private Integer n;
      private String str;
      private Boolean b;

      public Test(String str, Integer n, Boolean b)
      {
         this.str = str;
         this.n = n;
         this.b = b;
      }

      public Integer getNumber()
      {
         return n;
      }

      public String getStr()
      {
         return str;
      }

      public Boolean getBoolean()
      {
         return b;
      }
   }

   public static class DynamicTest implements DynamicMBean
   {
      private Boolean boolval;
      private long numval;
      private String strval;

      public DynamicTest(String s, long n, Boolean b)
      {
         this.boolval = b;
         this.numval = n;
         this.strval = s;
      }

      public Boolean getBoolean()
      {
         return this.boolval;
      }

      public long getNumber()
      {
         return this.numval;
      }

      public void setNumber(long value)
      {
         this.numval = value;
      }

      public String getStr()
      {
         throw new RuntimeException("Don't call me!");
      }

      public Object getAttribute(String attribute)
              throws AttributeNotFoundException, MBeanException, ReflectionException
      {
         Object result;
         if (attribute.compareTo("Boolean") == 0)
         {
            result = getBoolean();
         }
         else if (attribute.compareTo("Number") == 0)
         {
            result = new Long(getNumber());
         }
         else if (attribute.compareTo("Str") == 0)
         {
            result = getStr();
         }
         else
         {
            throw new AttributeNotFoundException("Can't find " + attribute);
         }
         return result;
      }

      public AttributeList getAttributes(String[] attributes)
      {
         List attrnames = new ArrayList();
         MBeanAttributeInfo[] attrs = getMBeanInfo().getAttributes();
         for (int i = 0; i < attrs.length; i++)
         {
            attrnames.add(attrs[i].getName());
         }
         AttributeList result = new AttributeList();
         for (int i = 0; i < attributes.length; i++)
         {
            if (attrnames.contains(attributes[i]))
            {
               try
               {
                  Attribute attr = new Attribute(attributes[i], getAttribute(attributes[i]));
                  result.add(attr);
               }
               catch (AttributeNotFoundException e)
               {
                  // Don't add this attribute
               }
               catch (MBeanException e)
               {
                  // Don't add this attribute
               }
               catch (ReflectionException e)
               {
                  // Don't add this attribute
               }
            }
         }
         return result;
      }

      public MBeanInfo getMBeanInfo()
      {
         MBeanInfo result;
         MBeanAttributeInfo[] attrs;
         try
         {
            attrs =
            new MBeanAttributeInfo[]{
               new MBeanAttributeInfo("Number",
                                      "A number",
                                      DynamicTest.class.getMethod("getNumber",
                                                                  new Class[0]),
                                      DynamicTest.class.getMethod("setNumber",
                                                                  new Class[]{long.class})),
               new MBeanAttributeInfo("Str",
                                      "A string",
                                      DynamicTest.class.getMethod("getStr", new Class[0]),
                                      null),
               new MBeanAttributeInfo("Boolean",
                                      "A Boolean",
                                      DynamicTest.class.getMethod("getBoolean",
                                                                  new Class[0]),
                                      null)
            };
         }
         catch (Exception x)
         {
            attrs = new MBeanAttributeInfo[0];
         }
         MBeanConstructorInfo[] ctors = new MBeanConstructorInfo[0];
         MBeanOperationInfo[] ops = new MBeanOperationInfo[0];
         MBeanNotificationInfo[] notifs = new MBeanNotificationInfo[0];
         result =
         new MBeanInfo(DynamicTest.class.getName(),
                       "DynamicTest MBean",
                       attrs,
                       ctors,
                       ops,
                       notifs);
         return result;
      }

      public Object invoke(String method,
                           Object[] arguments,
                           String[] params)
              throws MBeanException, ReflectionException
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
         if (attribute.getName().compareTo("Number") == 0)
         {
            setNumber(((Long)attribute.getValue()).longValue());
         }
         else
         {
            throw new AttributeNotFoundException("Can't find " + attribute.getName());
         }
      }

      public AttributeList setAttributes(AttributeList attributes)
      {
         AttributeList result = new AttributeList();
         Iterator i = attributes.iterator();
         while (i.hasNext())
         {
            try
            {
               Attribute attr = (Attribute)i.next();
               setAttribute(attr);
               result.add(attr);
            }
            catch (Exception x)
            {
               // Don't add this to the result
            }
         }
         return result;
      }

   }
}
