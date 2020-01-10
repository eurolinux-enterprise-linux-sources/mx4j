/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.tools.i18n;

import java.util.Locale;
import javax.management.NotCompliantMBeanException;

import mx4j.tools.i18n.I18NStandardMBean;


/**
 */
public class I18NStandardMBeanSupport
{
   /**
    * No management interface and it is not a standard MBean
    */
   public static class SubclassNotCompliant extends I18NStandardMBean
   {
      public SubclassNotCompliant() throws NotCompliantMBeanException
      {
         super(null);
      }
   }

   /**
    * A simple management interface.
    */
   public interface FullManagement
   {
      public void setAttrib(int i);

      public void operation(int i, Object obj);
   }

   /**
    * A management interface have overloaded operations.
    */
   public interface OverloadManagement
   {
      public void setAttrib(int i);

      public void operation();

      public void operation(int i);

      public void operation(Object obj);

      public void operation(int[] array);

      public void operation(Object[] array);

      public void operation(int i, Object obj);

      public void operation(int i, int j, int k);

      public void operation(int i, int j, String s);
   }

   /**
    * A base subclass of I18NStandardMBean (for factorization).
    */
   public static class BaseSubclass extends I18NStandardMBean
   {
      private int m_attrib = 0;

      protected BaseSubclass(Class mbeanInterface) throws NotCompliantMBeanException
      {
         super(mbeanInterface);
      }

      protected BaseSubclass(Class mbeanInterface, Locale locale) throws NotCompliantMBeanException
      {
         super(mbeanInterface, locale);
      }

      public void setAttrib(int i)
      {
         m_attrib = i;
      }

      public void operation(int i, Object obj)
      {
      }
   }

   /**
    * A base implementation NOT derrived from I18NStandardMBean.
    */
   public static class BaseImplementation implements FullManagement
   {
      private int m_attrib = 0;

      public void setAttrib(int i)
      {
         m_attrib = i;
      }

      public void operation(int i, Object obj)
      {
      }
   }

   /**
    * An I18NStandardMBean subclass having just a global name decription.
    */
   public static class SubclassNameOnly extends BaseSubclass implements FullManagement
   {
      public SubclassNameOnly() throws NotCompliantMBeanException
      {
         super(FullManagement.class);
      }

      public SubclassNameOnly(Locale locale) throws NotCompliantMBeanException
      {
         super(FullManagement.class, locale);
      }
   }

   /**
    * An I18NStandardMBean subclass having full descriptions.
    */
   public static class SubclassComplete extends BaseSubclass implements FullManagement
   {
      public SubclassComplete(Locale locale) throws NotCompliantMBeanException
      {
         super(FullManagement.class, locale);
      }

      public SubclassComplete() throws NotCompliantMBeanException
      {
         super(FullManagement.class);
      }
   }

   /**
    * An I18NStandardMBean subclass having no bundle.
    */
   public static class SubclassNoBundle extends BaseSubclass implements FullManagement
   {
      public SubclassNoBundle() throws NotCompliantMBeanException
      {
         super(FullManagement.class);
      }
   }

   /**
    * An I18NStandardMBean subclass having ambiguous constructors
    */
   public static class SubclassAmbiguousConstructors extends BaseSubclass implements FullManagement
   {
      // desciption can be found based on number of parameters
      public SubclassAmbiguousConstructors() throws NotCompliantMBeanException
      {
         super(FullManagement.class);
      }

      // with no signature info in the bundle this cannot be distinguished from the Locale version
      public SubclassAmbiguousConstructors(Locale locale) throws NotCompliantMBeanException
      {
         super(FullManagement.class, locale);
      }

      // with no signature info in the bundle this cannot be distinguished from the Locale version
      public SubclassAmbiguousConstructors(int i) throws NotCompliantMBeanException
      {
         super(FullManagement.class);
      }

      // with no signature info in the bundle this cannot be distinguished from the Locale version
      public SubclassAmbiguousConstructors(String s) throws NotCompliantMBeanException
      {
         super(FullManagement.class);
      }

      // This can determined with no signature (2 args)
      public SubclassAmbiguousConstructors(int i, int j) throws NotCompliantMBeanException
      {
         super(FullManagement.class);
      }

      // of the two 3 arg constructors below only one is described in the bundle
      // BUT they should both still be considered ambiguous
      public SubclassAmbiguousConstructors(int i, int j, int k) throws NotCompliantMBeanException
      {
         super(FullManagement.class);
      }

      public SubclassAmbiguousConstructors(int i, int j, String s) throws NotCompliantMBeanException
      {
         super(FullManagement.class);
      }
   }

   /**
    * An I18NStandardMBean subclass overloaded constructors and operations.
    */
   public static class SubclassOverload extends BaseSubclass implements OverloadManagement
   {

      // no arguments
      public SubclassOverload() throws NotCompliantMBeanException
      {
         super(OverloadManagement.class);
      }

      // one argument Object
      public SubclassOverload(Object obj) throws NotCompliantMBeanException
      {
         super(OverloadManagement.class);
      }

      // one argument int
      public SubclassOverload(int i) throws NotCompliantMBeanException
      {
         super(OverloadManagement.class);
      }

      // one argument int[]
      public SubclassOverload(int[] i) throws NotCompliantMBeanException
      {
         super(OverloadManagement.class);
      }

      // one argument Object[]
      public SubclassOverload(Object[] objs) throws NotCompliantMBeanException
      {
         super(OverloadManagement.class);
      }

      // 2 arguments
      public SubclassOverload(int a, Object b) throws NotCompliantMBeanException
      {
         super(OverloadManagement.class);
      }

      public void operation()
      {
      }

      public void operation(int i)
      {
      }

      public void operation(Object obj)
      {
      }

      public void operation(int[] array)
      {
      }

      public void operation(Object[] array)
      {
      }

      public void operation(int i, int j, int k)
      {
      }

      public void operation(int i, int j, String s)
      {
      }

   }

   /**
    * A subclass whose bundle will not specify sigs (so operations are ambiguous).
    */
   public static class SubclassAmbiguousOperation extends BaseSubclass implements OverloadManagement
   {
      public SubclassAmbiguousOperation() throws NotCompliantMBeanException
      {
         super(OverloadManagement.class);
      }

      public void operation()
      {
      }

      public void operation(int i)
      {
      }

      public void operation(Object obj)
      {
      }

      public void operation(int[] array)
      {
      }

      public void operation(Object[] array)
      {
      }

      public void operation(int i, int j, int k)
      {
      }

      public void operation(int i, int j, String s)
      {
      }

   }

   /**
    * An I18N MBean implementation having just a global name decription.
    */
   public static class ImplementationNameOnly extends BaseImplementation
   {
   }

   /**
    * An I18N MBean implementation having full decriptions.
    */
   public static class ImplementationComplete extends BaseImplementation
   {
   }

   /**
    * An I18N MBean implementation having no bundle.
    */
   public static class ImplementationNoBundle extends BaseImplementation
   {
   }


}
