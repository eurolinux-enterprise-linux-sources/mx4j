/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.tools.i18n;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.MissingResourceException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanFeatureInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import junit.framework.TestCase;
import mx4j.tools.i18n.I18NStandardMBean;

/**
 * Tests for the translatable standard MBean extensions.
 */
public class I18NStandardMBeanTest extends TestCase
{
   public I18NStandardMBeanTest(String s)
   {
      super(s);
   }

   public void setUp()
   {
      I18NStandardMBean.setDefaultLocale(null);
      setLocaleProp(""); // Hashtable does not accept null!
   }

   /**
    * Verify loading of correct resource bundle for subclassed I18NStandardMBeans.
    * Only use a single key (MBean decription)
    */
   public void testSubclassSource() throws Exception
   {
      // No system property, standard locale with no I18N => default file
      setDefaultLocale(Locale.ITALY); // no I18N files for this
      I18NStandardMBean mbean =
              new I18NStandardMBeanSupport.SubclassNameOnly();
      MBeanInfo info = mbean.getMBeanInfo();
      assertEquals(info.getDescription(),
                   "The default subclassed name only MBean description");

      // Explicitly requested locale (overrides system default locale)
      setDefaultLocale(Locale.ENGLISH);
      mbean = new I18NStandardMBeanSupport.SubclassNameOnly(Locale.FRENCH);
      info = mbean.getMBeanInfo();
      assertEquals(info.getDescription(),
                   "The French subclassed name only MBean description");

      // system default locale
      mbean = new I18NStandardMBeanSupport.SubclassNameOnly();
      info = mbean.getMBeanInfo();
      assertEquals(info.getDescription(),
                   "The English subclassed name only MBean description");

      // system property (overrides default locale)
      setLocaleProp("fr_FR");
      mbean = new I18NStandardMBeanSupport.SubclassNameOnly();
      info = mbean.getMBeanInfo();
      assertEquals(info.getDescription(),
                   "The French subclassed name only MBean description");

      // static setLocale method (overrides default locale)
      I18NStandardMBean.setDefaultLocale(Locale.ENGLISH);
      mbean = new I18NStandardMBeanSupport.SubclassNameOnly();
      info = mbean.getMBeanInfo();
      assertEquals(info.getDescription(),
                   "The English subclassed name only MBean description");
   }

   /**
    * Verify loading of correct resource bundle for non subclassed MBeans.
    * Only use a single key (MBean decription)
    */
   public void testImplementationSource() throws Exception
   {
      I18NStandardMBeanSupport.ImplementationNameOnly impl =
              new I18NStandardMBeanSupport.ImplementationNameOnly();

      // No system property, standard locale with no I18N => default file
      setDefaultLocale(Locale.ITALY); // no I18N files for this
      I18NStandardMBean mbean =
              new I18NStandardMBean(impl,
                                    I18NStandardMBeanSupport.FullManagement.class);
      MBeanInfo info = mbean.getMBeanInfo();
      assertEquals(info.getDescription(),
                   "The default direct implementation name only MBean description");

      // Explicitly requested locale (overrides system default locale)
      setDefaultLocale(Locale.ENGLISH);
      mbean =
      new I18NStandardMBean(impl,
                            I18NStandardMBeanSupport.FullManagement.class,
                            Locale.FRENCH);
      info = mbean.getMBeanInfo();
      assertEquals(info.getDescription(),
                   "The French direct implementation name only MBean description");

      // system default locale
      mbean =
      new I18NStandardMBean(impl,
                            I18NStandardMBeanSupport.FullManagement.class);
      info = mbean.getMBeanInfo();
      assertEquals(info.getDescription(),
                   "The English direct implementation name only MBean description");

      // system property (overrides default locale)
      setLocaleProp("fr_FR");
      mbean =
      new I18NStandardMBean(impl,
                            I18NStandardMBeanSupport.FullManagement.class);
      info = mbean.getMBeanInfo();
      assertEquals(info.getDescription(),
                   "The French direct implementation name only MBean description");

      // static setLocale method (overrides default locale)
      I18NStandardMBean.setDefaultLocale(Locale.ENGLISH);
      mbean =
      new I18NStandardMBean(impl,
                            I18NStandardMBeanSupport.FullManagement.class);
      info = mbean.getMBeanInfo();
      assertEquals(info.getDescription(),
                   "The English direct implementation name only MBean description");
   }

   /**
    * Verify loading of all the keys.
    * Locale switching already tested above so just use a single locale here.
    * Only test "simple" constuctors and operations that do not require signatures.
    */
   public void testAllKeys() throws Exception
   {
      I18NStandardMBean mbean =
              new I18NStandardMBeanSupport.SubclassComplete();
      MBeanInfo info = mbean.getMBeanInfo();
      info = mbean.getMBeanInfo(); // ensure cache OK
      assertEquals(info.getDescription(),
                   "The default subclassed complete MBean description");
      MBeanAttributeInfo attrInfo = info.getAttributes()[0];
      assertEquals(attrInfo.getDescription(), "The attribute description");
      MBeanOperationInfo opInfo = info.getOperations()[0];
      assertEquals(opInfo.getDescription(), "The operation description");
      MBeanParameterInfo paramInfo = opInfo.getSignature()[0];
      checkParam(paramInfo,
                 "The first parameter for the operation",
                 "parameter1");
      paramInfo = opInfo.getSignature()[1];
      checkParam(paramInfo,
                 "The second parameter for the operation",
                 "parameter2");

      MBeanConstructorInfo[] cstrs = info.getConstructors();
      assertEquals(cstrs.length, 2);
      //dumpConstructors(info);

      Map mapCstrForDesc = makeFeatureMap(cstrs);

      // no args constructor
      MBeanConstructorInfo cstr =
              (MBeanConstructorInfo)mapCstrForDesc.get("The no-args constructor");
      assertNotNull(cstr);
      assertEquals(cstr.getSignature().length, 0);

      // Locale constructor
      cstr =
      (MBeanConstructorInfo)mapCstrForDesc.get("The Locale specific constructor");
      assertNotNull(cstr);
      assertEquals(cstr.getSignature().length, 1);
      paramInfo = cstr.getSignature()[0];
      checkParam(paramInfo, "The locale", "locale");
   }

   /**
    * Various tests of constructors that need to be destinguished by their signatures.
    */
   public void testOverloadedConstructors() throws Exception
   {
      I18NStandardMBean mbean =
              new I18NStandardMBeanSupport.SubclassOverload();
      MBeanInfo info = mbean.getMBeanInfo();
      info = mbean.getMBeanInfo(); // ensure cache OK
      //dumpConstructors(info);
      MBeanConstructorInfo[] cstrs = info.getConstructors();
      assertEquals(cstrs.length, 6);
      Map mapCstrForDesc = makeFeatureMap(cstrs);

      // no args constructor
      MBeanConstructorInfo cstr =
              (MBeanConstructorInfo)mapCstrForDesc.get("The no-args constructor");
      assertNotNull(cstr);
      assertEquals(cstr.getSignature().length, 0);

      // Object constuctor
      cstr =
      (MBeanConstructorInfo)mapCstrForDesc.get("The Object constructor");
      assertNotNull(cstr);
      assertEquals(cstr.getSignature().length, 1);
      checkParam(cstr.getSignature()[0], "an object", "obj");

      // int constructor
      cstr = (MBeanConstructorInfo)mapCstrForDesc.get("The int constructor");
      assertNotNull(cstr);
      assertEquals(cstr.getSignature().length, 1);
      checkParam(cstr.getSignature()[0], "a number", "value");

      // int[] constructor
      cstr =
      (MBeanConstructorInfo)mapCstrForDesc.get("The int[] constructor");
      assertNotNull(cstr);
      assertEquals(cstr.getSignature().length, 1);
      checkParam(cstr.getSignature()[0], "an array of int", "intArray");

      // Object[] constructor
      cstr =
      (MBeanConstructorInfo)mapCstrForDesc.get("The Object[] constructor");
      assertNotNull(cstr);
      assertEquals(cstr.getSignature().length, 1);
      checkParam(cstr.getSignature()[0], "an array of Object", "objArray");

      // int,Object constructor
      cstr =
      (MBeanConstructorInfo)mapCstrForDesc.get("A two parameter int,Object constructor");
      assertNotNull(cstr);
      assertEquals(cstr.getSignature().length, 2);
      checkParam(cstr.getSignature()[0], "a number", "a");
      checkParam(cstr.getSignature()[1], "an object", "b");
   }

   /**
    * Various tests of ambiguous constructors
    */
   public void testAmbiguousConstructors() throws Exception
   {
      System.out.println("****");
      I18NStandardMBean mbean =
              new I18NStandardMBeanSupport.SubclassAmbiguousConstructors();
      MBeanInfo info = mbean.getMBeanInfo();
      info = mbean.getMBeanInfo(); // ensure cache OK
      //dumpConstructors(info);

      MBeanConstructorInfo[] cstrs = info.getConstructors();
      assertEquals(cstrs.length, 7);
      Map mapCstrForDesc = makeFeatureMap(cstrs);
      assertEquals(mapCstrForDesc.size(), 3); // 5 ambiguous mapped to same

      // no args constructor
      MBeanConstructorInfo cstr =
              (MBeanConstructorInfo)mapCstrForDesc.get("The no-args constructor");
      assertNotNull(cstr);
      assertEquals(cstr.getSignature().length, 0);

      // Amiguous constuctors
      cstr =
      (MBeanConstructorInfo)mapCstrForDesc.get("ambiguous constructor");
      assertNotNull(cstr);

      // int,Object constructor
      cstr =
      (MBeanConstructorInfo)mapCstrForDesc.get("The two parameter constructor");
      assertNotNull(cstr);
      assertEquals(cstr.getSignature().length, 2);
      checkParam(cstr.getSignature()[0], "a number", "a");
      checkParam(cstr.getSignature()[1], "another number", "b");
   }

   /**
    * Various tests of operations that need to be destinguished by their signatures.
    */
   public void testOverloadedOperations() throws Exception
   {
      I18NStandardMBean mbean =
              new I18NStandardMBeanSupport.SubclassOverload();
      MBeanInfo info = mbean.getMBeanInfo();
      info = mbean.getMBeanInfo(); // ensure cache OK
      MBeanOperationInfo[] ops = info.getOperations();
      //dumpOperations(info);
      assertEquals(ops.length, 8);
      Map mapOpsForDesc = makeFeatureMap(ops);

      // no args operation
      MBeanOperationInfo op =
              (MBeanOperationInfo)mapOpsForDesc.get("a no parameter operation");
      assertNotNull(op);
      assertEquals(op.getSignature().length, 0);

      // int operation
      op = (MBeanOperationInfo)mapOpsForDesc.get("an int operation");
      assertNotNull(op);
      assertEquals(op.getSignature().length, 1);
      checkParam(op.getSignature()[0], "a number", "value");

      // Object operation
      op = (MBeanOperationInfo)mapOpsForDesc.get("an Object operation");
      assertNotNull(op);
      assertEquals(op.getSignature().length, 1);
      checkParam(op.getSignature()[0], "an object", "obj");

      // int[] operation
      op = (MBeanOperationInfo)mapOpsForDesc.get("an int[] operation");
      assertNotNull(op);
      assertEquals(op.getSignature().length, 1);
      checkParam(op.getSignature()[0], "an array of int", "intArray");

      // Object[] operation
      op = (MBeanOperationInfo)mapOpsForDesc.get("an Object[] operation");
      assertNotNull(op);
      assertEquals(op.getSignature().length, 1);
      checkParam(op.getSignature()[0], "an array of Object", "objArray");

      // int,Object operation
      op =
      (MBeanOperationInfo)mapOpsForDesc.get("a two parameter int,Object operation");
      assertNotNull(op);
      assertEquals(op.getSignature().length, 2);
      checkParam(op.getSignature()[0], "a number", "a");
      checkParam(op.getSignature()[1], "an object", "obj");

      // int,int,int operation
      op =
      (MBeanOperationInfo)mapOpsForDesc.get("a three parameter int,int,int operation");
      assertNotNull(op);
      assertEquals(op.getSignature().length, 3);
      checkParam(op.getSignature()[0], "a number", "i");
      checkParam(op.getSignature()[1], "another number", "j");
      checkParam(op.getSignature()[2], "yet another number", "k");

      // int,int,String operation
      op =
      (MBeanOperationInfo)mapOpsForDesc.get("a three parameter int,int,String operation");
      assertNotNull(op);
      assertEquals(op.getSignature().length, 3);
      checkParam(op.getSignature()[0], "a number", "i");
      checkParam(op.getSignature()[1], "another number", "j");
      checkParam(op.getSignature()[2], "a String", "s");

   }

   /**
    * Various tests of ambiguous operations.
    */
   public void testAmbiguousOperations() throws Exception
   {
      I18NStandardMBean mbean =
              new I18NStandardMBeanSupport.SubclassAmbiguousOperation();
      MBeanInfo info = mbean.getMBeanInfo();
      info = mbean.getMBeanInfo(); // ensure cache OK
      //dumpOperations(info);
      MBeanOperationInfo[] ops = info.getOperations();
      assertEquals(ops.length, 8);
      Map mapOpsForDesc = makeFeatureMap(ops);
      assertEquals(mapOpsForDesc.size(), 3); // 5 ambiguous mapped to same

      // no args operation
      MBeanOperationInfo op =
              (MBeanOperationInfo)mapOpsForDesc.get("a no parameter operation");
      assertNotNull(op);
      assertEquals(op.getSignature().length, 0);

      // Ambiguous operation
      op =
      (MBeanOperationInfo)mapOpsForDesc.get("a two parameter int,Object operation");
      assertNotNull(op);

      // int,Object operation
      op =
      (MBeanOperationInfo)mapOpsForDesc.get("a two parameter int,Object operation");
      assertNotNull(op);
      assertEquals(op.getSignature().length, 2);
      checkParam(op.getSignature()[0], "a number", "a");
      checkParam(op.getSignature()[1], "an object", "obj");

   }

   /**
    * Tests missing translations (but bundle found).
    */
   public void testPartialDesciptions() throws Exception
   {
      setDefaultLocale(Locale.ITALY); // no I18N files for this
      I18NStandardMBean mbean =
              new I18NStandardMBeanSupport.SubclassNameOnly();
      MBeanInfo info = mbean.getMBeanInfo();
      info = mbean.getMBeanInfo(); // ensure cache OK
      assertEquals(info.getDescription(),
                   "The default subclassed name only MBean description");
      MBeanAttributeInfo attrInfo = info.getAttributes()[0];
      assertEquals(attrInfo.getDescription(), "??(attr.Attrib)");
      MBeanOperationInfo opInfo = info.getOperations()[0];
      assertEquals(opInfo.getDescription(), "??(op.operation)");
      MBeanParameterInfo paramInfo = opInfo.getSignature()[0];
      checkParam(paramInfo, "??(op.operation.param.1)", "param1");
      paramInfo = opInfo.getSignature()[1];
      checkParam(paramInfo, "??(op.operation.param.2)", "param2");

      MBeanConstructorInfo[] cstrs = info.getConstructors();
      assertEquals(cstrs.length, 2);
      dumpConstructors(info);

      Map mapCstrForDesc = makeFeatureMap(cstrs);
      assertEquals(mapCstrForDesc.size(), 1); // ambiguous
      MBeanConstructorInfo cstr =
              (MBeanConstructorInfo)mapCstrForDesc.get("ambiguous constructor");
      assertNotNull(cstr);
   }

   /**
    * Tests missing resource bundles.
    */
   public void testMissingBundle() throws Exception
   {
      try
      {
         I18NStandardMBean mbean =
                 new I18NStandardMBeanSupport.SubclassNoBundle();
         assertTrue(false);
      }
      catch (MissingResourceException e)
      {
      }

      try
      {
         I18NStandardMBeanSupport.ImplementationNoBundle impl =
                 new I18NStandardMBeanSupport.ImplementationNoBundle();
         I18NStandardMBean mbean =
                 new I18NStandardMBean(impl,
                                       I18NStandardMBeanSupport.FullManagement.class);
         MBeanInfo info = mbean.getMBeanInfo();
         assertTrue(false);
      }
      catch (MissingResourceException e)
      {
      }
   }

   // create a feature description=>info mapping since order unknown
   private Map makeFeatureMap(MBeanFeatureInfo[] features)
   {
      Map mapFeatureForDesc = new HashMap(); // use map since co
      for (int i = 0; i < features.length; i++)
      {
         mapFeatureForDesc.put(features[i].getDescription(), features[i]);
      }
      return mapFeatureForDesc;
   }

   private void dumpConstructors(MBeanInfo info)
   {
      MBeanConstructorInfo[] consts = info.getConstructors();
      System.out.println("NB cons = " + consts.length);
      for (int i = 0; i < consts.length; i++)
      {
         System.out.println("Cons " + i + ":" + consts[i].getDescription());
         MBeanParameterInfo[] params = consts[i].getSignature();
         for (int j = 0; j < params.length; j++)
         {
            MBeanParameterInfo p = params[j];
            System.out.println("   Param "
                               + j
                               + ":"
                               + p.getDescription()
                               + " name="
                               + p.getName()
                               + " type="
                               + p.getType());
         }
      }
   }

   private void dumpOperations(MBeanInfo info)
   {
      MBeanOperationInfo[] ops = info.getOperations();
      System.out.println("NB operations = " + ops.length);
      for (int i = 0; i < ops.length; i++)
      {
         System.out.println("Op " + i + ":" + ops[i].getDescription());
         MBeanParameterInfo[] params = ops[i].getSignature();
         for (int j = 0; j < params.length; j++)
         {
            MBeanParameterInfo p = params[j];
            System.out.println("   Param "
                               + j
                               + ":"
                               + p.getDescription()
                               + " name="
                               + p.getName()
                               + " type="
                               + p.getType());
         }
      }
   }

   private void setLocaleProp(String s)
   {
      System.setProperty("mx4j.descriptionLocale", s);
   }

   private void setDefaultLocale(Locale locale)
   {
      Locale.setDefault(locale);
   }

   private void checkParam(MBeanParameterInfo paramInfo,
                           String description,
                           String name)
   {
      assertEquals(paramInfo.getDescription(), description);
      assertEquals(paramInfo.getName(), name);
   }

}
