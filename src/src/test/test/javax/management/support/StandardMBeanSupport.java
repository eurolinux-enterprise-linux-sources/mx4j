/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.support;

import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.NotCompliantMBeanException;
import javax.management.StandardMBean;

import test.MutableInteger;

/**
 * @version $Revision: 1.5 $
 */
public class StandardMBeanSupport
{
   /**
    * No management interface and it is not a standard MBean
    */
   public static class SubclassNotCompliant extends StandardMBean
   {
      public SubclassNotCompliant() throws NotCompliantMBeanException
      {
         super(null);
      }
   }

   /**
    * Valid StandardMBean with a standard MBean as implementation
    */
   public static class SubclassWithNoManagement extends StandardMBean implements SubclassWithNoManagementMBean
   {
      public SubclassWithNoManagement() throws NotCompliantMBeanException
      {
         super(null);
      }

      public Object test()
      {
         return new Object();
      }
   }

   public interface SubclassWithNoManagementMBean
   {
      public Object test();
   }

   public static class SubclassWithManagement extends StandardMBean implements Management
   {
      public SubclassWithManagement() throws NotCompliantMBeanException
      {
         super(Management.class);
      }

      public void cannotCall()
      {
      }

      public Object test()
      {
         return new Object();
      }
   }

   public interface Management
   {
      public Object test();
   }

   public static class ImplementationWithNoManagement implements ImplementationWithNoManagementMBean
   {
      public Object test()
      {
         return new Object();
      }
   }

   public interface ImplementationWithNoManagementMBean
   {
      public Object test();
   }

   public static class ImplementationWithManagement implements Management
   {
      public Object test()
      {
         return new Object();
      }
   }

   public interface FullManagement
   {
      public void setAttrib(int i);

      public void operation(int i);
   }

   public interface PublicInterfaceMBean
   {
      public Object test();
   }

   private static class PublicInterface implements PublicInterfaceMBean
   {
      public PublicInterface()
      {
      }

      public Object test()
      {
         return new Object();
      }
   }

   public static PublicInterfaceMBean createPublicInterfaceMBean()
   {
      return new PublicInterface();
   }


   public static class CallbackCounter extends StandardMBean implements FullManagement
   {
      private MutableInteger count;

      public CallbackCounter(int dummy) throws NotCompliantMBeanException
      {
         // Variable dummy only serves to enable the callback on the constructor parameter
         super(FullManagement.class);
      }

      public void setAttrib(int i)
      {
      }

      public void operation(int i)
      {
      }

      public int getCount()
      {
         return count.get();
      }

      protected String getClassName(MBeanInfo info)
      {
         increment();
         return super.getClassName(info);
      }

      protected String getDescription(MBeanInfo info)
      {
         increment();
         return super.getDescription(info);
      }

      protected String getDescription(MBeanAttributeInfo info)
      {
         increment();
         return super.getDescription(info);
      }

      protected String getDescription(MBeanConstructorInfo info)
      {
         increment();
         return super.getDescription(info);
      }

      protected String getDescription(MBeanOperationInfo info)
      {
         increment();
         return super.getDescription(info);
      }

      protected String getDescription(MBeanConstructorInfo constructor, MBeanParameterInfo param, int sequence)
      {
         increment();
         return super.getDescription(constructor, param, sequence);
      }

      protected String getDescription(MBeanOperationInfo operation, MBeanParameterInfo param, int sequence)
      {
         increment();
         return super.getDescription(operation, param, sequence);
      }

      protected String getParameterName(MBeanConstructorInfo constructor, MBeanParameterInfo param, int sequence)
      {
         increment();
         return super.getParameterName(constructor, param, sequence);
      }

      protected String getParameterName(MBeanOperationInfo operation, MBeanParameterInfo param, int sequence)
      {
         increment();
         return super.getParameterName(operation, param, sequence);
      }

      protected int getImpact(MBeanOperationInfo info)
      {
         increment();
         return super.getImpact(info);
      }

      private void increment()
      {
         if (count == null) count = new MutableInteger(0);
         count.set(count.get() + 1);
      }

   }

}
