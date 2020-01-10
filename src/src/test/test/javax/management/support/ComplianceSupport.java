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
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.ReflectionException;

/**
 * @version $Revision: 1.6 $
 */
public class ComplianceSupport
{
   // Not a manageable class: missing management interface
   public static class NoManagement
   {
   }


   // Not a manageable class: implements an interface with different name
   public interface LexicalPatternNotRespectedMBean
   {
      public void fake();
   }

   public static class DoesntRespectLexicalPattern implements LexicalPatternNotRespectedMBean
   {
      public void fake()
      {
      }
   }


   // MBeans with overloaded attributes are not compliant
   public interface OverloadedAttributeSetSetMBean
   {
      public void setAttribute(String s);

      public void setAttribute(Integer i);
   }

   public static class OverloadedAttributeSetSet implements OverloadedAttributeSetSetMBean
   {
      public void setAttribute(String s)
      {
      }

      public void setAttribute(Integer i)
      {
      }
   }

   public interface OverloadedAttributeGetSetMBean
   {
      public String getAttribute();

      public void setAttribute(Integer i);
   }

   public static class OverloadedAttributeGetSet implements OverloadedAttributeGetSetMBean
   {
      public String getAttribute()
      {
         return null;
      }

      public void setAttribute(Integer i)
      {
      }
   }

   public interface OverloadedAttributeIsGetMBean
   {
      public boolean isBoolean();

      public boolean getBoolean();
   }

   public static class OverloadedAttributeIsGet implements OverloadedAttributeIsGetMBean
   {
      public boolean isBoolean()
      {
         return false;
      }

      public boolean getBoolean()
      {
         return false;
      }
   }


   // In JMX 1.0 this is not a manageable class: it's abstract
   // In JMX 1.1 the requirement for the MBean class to be concrete has been removed
//	public interface AbstractMBean {}
//	public static abstract class Abstract implements AbstractMBean {}


   // Valid MBean
   public static interface BasicStandardMBean
   {
      public void test();
   }

   public static class BasicStandard implements BasicStandardMBean
   {
      private int m_count;

      // This method should not be part of the management interface
      public void noManage()
      {
      }

      public void test()
      {
         ++m_count;
      }
   }


   // Valid MBean that inherits from parent its manageability
   public static class Derived extends BasicStandard
   {
      public void derivedNoManage()
      {
      }
   }


   // Valid MBean with inherited management interface
   public interface BaseMBean
   {
      public void base();
   }

   public interface InheritedMBean extends BaseMBean
   {
      public void derived();

      public void test2();

      public void retest();
   }

   public static class Inherited implements InheritedMBean
   {
      public void base()
      {
      }

      public void derived()
      {
      }

      public void test2()
      {
      }

      public void retest()
      {
      }
   }


   // Valid MBean with a trap: the management interface should be only the one inherited from Basic
   public static class NotInherited extends BasicStandard implements InheritedMBean
   {
      public void base()
      {
      }

      public void derived()
      {
      }

      public void retest()
      {
      }

      public void test2()
      {
      }

      public void unManage()
      {
      }
   }


   // Valid MBean with multiple inheritance
   public interface MultiMBean extends BasicStandardMBean, InheritedMBean
   {
   }

   public static class Multi extends Inherited implements MultiMBean
   {
      public void test()
      {
      }
   }


   // Valid MBean even if the class is package private
   public interface PackagePrivateMBean
   {
   }

   static class PackagePrivate implements PackagePrivateMBean
   {
   }


   // In JMX 1.0 this is not a valid MBean: it is standard and dynamic
   // In JMX 1.1 it is dynamic, since the spec says that every class that implements DynamicMBean is a dynamic mbean
   // However, I assume that if someone writes such a class, or it did not understand JMX or is trying to fool the MBeanServer
   public interface StandardDynamicMBean
   {
   }

   public static class StandardDynamic implements DynamicMBean, StandardDynamicMBean
   {
      public MBeanInfo getMBeanInfo()
      {
         return new MBeanInfo(getClass().getName(), null, null, null, null, null);
      }

      public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException
      {
         return null;
      }

      public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
      {
      }

      public AttributeList getAttributes(String[] attributes)
      {
         return new AttributeList();
      }

      public AttributeList setAttributes(AttributeList attributes)
      {
         return new AttributeList();
      }

      public Object invoke(String method, Object[] arguments, String[] params) throws MBeanException, ReflectionException
      {
         return null;
      }
   }


   // JMX 1.0: Invalid MBean: the standard MBean interface is a dynamic MBean
   // JMX 1.1: This is a dynamic MBean
   public interface StandardAndDynamicMBean extends DynamicMBean
   {
      public void mix();
   }

   public static class StandardAndDynamic implements StandardAndDynamicMBean
   {
      public void mix()
      {
      }

      public MBeanInfo getMBeanInfo()
      {
         return new MBeanInfo(getClass().getName(), null, null, null, null, null);
      }

      public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException
      {
         return null;
      }

      public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
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

      public Object invoke(String method, Object[] arguments, String[] params) throws MBeanException, ReflectionException
      {
         return null;
      }
   }


   // A valid dynamic MBean
   public static class BasicDynamic implements DynamicMBean
   {
      public MBeanInfo getMBeanInfo()
      {
         return new MBeanInfo(getClass().getName(), null, null, null, null, null);
      }

      public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException
      {
         return null;
      }

      public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
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

      public Object invoke(String method, Object[] arguments, String[] params) throws MBeanException, ReflectionException
      {
         return null;
      }
   }


   // Invalid dynamic MBean because getClassName() returns null
   public static class NoClassNameDynamicMBean extends BasicDynamic
   {
      public MBeanInfo getMBeanInfo()
      {
         MBeanInfo info = super.getMBeanInfo();
         return new MBeanInfo(null, info.getDescription(), info.getAttributes(), info.getConstructors(), info.getOperations(), info.getNotifications());
      }
   }


   // Valid dynamic MBean, even if its parent is standard
   public static class DynamicFromStandard extends BasicStandard implements DynamicMBean
   {
      public MBeanInfo getMBeanInfo()
      {
         return new MBeanInfo(getClass().getName(), null, null, null, null, null);
      }

      public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException
      {
         return null;
      }

      public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
      {
      }

      public AttributeList getAttributes(String[] attributes)
      {
         return new AttributeList();
      }

      public AttributeList setAttributes(AttributeList attributes)
      {
         return new AttributeList();
      }

      public Object invoke(String method, Object[] arguments, String[] params) throws MBeanException, ReflectionException
      {
         return null;
      }
   }


   // In JMX 1.0, this is a valid standard MBean even if its parent is dynamic
   // In JMX 1.1, this is a dynamic MBean
   public interface StandardFromDynamicMBean
   {
   }

   public static class StandardFromDynamic extends DynamicFromStandard implements StandardFromDynamicMBean
   {
   }

}
