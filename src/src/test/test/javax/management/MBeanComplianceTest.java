/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mx4j.MX4JSystemKeys;
import mx4j.server.MBeanIntrospector;
import mx4j.server.MBeanMetaData;
import test.MX4JTestCase;
import test.javax.management.support.ComplianceSupport;

import javax.management.MBeanConstructorInfo;

/**
 * @version $Revision: 1.10 $
 */
public class MBeanComplianceTest extends MX4JTestCase
{
   private MBeanIntrospector introspector;

   public MBeanComplianceTest(String s)
   {
      super(s);
   }

   protected void setUp() throws Exception
   {
      String property = MX4JSystemKeys.MX4J_STRICT_MBEAN_INTERFACE;
      System.setProperty(property, "no");
      introspector = new MBeanIntrospector();
   }

   protected void tearDown() throws Exception
   {
      introspector = null;
   }

   private MBeanMetaData createMBeanMetaData(Object mbean) throws Exception
   {
      MBeanMetaData metadata = MBeanMetaData.Factory.create();
      metadata.setMBean(mbean);
      metadata.setClassLoader(mbean.getClass().getClassLoader());
      return metadata;
   }

   private boolean isCompliant(Object mbean) throws Exception
   {
      Object metadata = createMBeanMetaData(mbean);
      Method method = introspector.getClass().getDeclaredMethod("testCompliance", new Class[]{MBeanMetaData.class});
      method.setAccessible(true);
      Boolean value = (Boolean)method.invoke(introspector, new Object[]{metadata});
      return value.booleanValue();
   }

   private boolean isStandardCompliant(Object mbean) throws Exception
   {
      MBeanMetaData metadata = createMBeanMetaData(mbean);
      Method method = introspector.getClass().getDeclaredMethod("testCompliance", new Class[]{MBeanMetaData.class});
      method.setAccessible(true);
      Boolean value = (Boolean)method.invoke(introspector, new Object[]{metadata});
      return value.booleanValue() && metadata.isMBeanStandard() && !metadata.isMBeanDynamic() && metadata.getMBeanInterface() != null;
   }

   private boolean isDynamicCompliant(Object mbean) throws Exception
   {
      MBeanMetaData metadata = createMBeanMetaData(mbean);
      Method method = introspector.getClass().getDeclaredMethod("testCompliance", new Class[]{MBeanMetaData.class});
      method.setAccessible(true);
      Boolean value = (Boolean)method.invoke(introspector, new Object[]{metadata});
      return value.booleanValue() && !metadata.isMBeanStandard() && metadata.isMBeanDynamic();
   }

   private Class getStandardManagementInterface(Object mbean) throws Exception
   {
      MBeanMetaData metadata = createMBeanMetaData(mbean);
      introspector.introspect(metadata);
      return metadata.getMBeanInterface();
   }

   private Method[] getStandardManagementMethods(Object mbean) throws Exception
   {
      Class intf = getStandardManagementInterface(mbean);
      return intf.getMethods();
   }

   public void testNotCompliantNoManagement() throws Exception
   {
      Object bad = new ComplianceSupport.NoManagement();
      if (isCompliant(bad)) fail("MBean is not compliant");
   }

   public void testNotCompliantLexicalPatternNotRespected() throws Exception
   {
      Object bad = new ComplianceSupport.DoesntRespectLexicalPattern();
      if (isCompliant(bad)) fail("MBean is not compliant");
   }

   public void testNotCompliantOverloadedAttributeSetSet() throws Exception
   {
      Object bad = new ComplianceSupport.OverloadedAttributeSetSet();
      if (isCompliant(bad)) fail("MBean is not compliant");
   }

   public void testNotCompliantOverloadedAttributeGetGet() throws Exception
   {
      // This is guaranteed by the Java compiler: a class with 2 getters that return different types
      // does not compile
   }

   public void testNotCompliantOverloadedAttribute3() throws Exception
   {
      Object bad = new ComplianceSupport.OverloadedAttributeGetSet();
      if (isCompliant(bad)) fail("MBean is not compliant");
   }

   public void testNotCompliantOverloadedAttribute4() throws Exception
   {
      Object bad = new ComplianceSupport.OverloadedAttributeIsGet();
      if (isCompliant(bad)) fail("MBean is not compliant");
   }

   public void testCompliantBasicStandard() throws Exception
   {
      Object good = new ComplianceSupport.BasicStandard();
      if (!isStandardCompliant(good)) fail("MBean is compliant");

      Method[] methods = ComplianceSupport.BasicStandardMBean.class.getMethods();
      List list = Arrays.asList(methods);
      Method[] management = getStandardManagementMethods(good);
      List list2 = Arrays.asList(management);
      assertTrue("Different management interface", list.containsAll(list2) && list2.containsAll(list));
   }

   public void testCompliantDerived() throws Exception
   {
      Object good = new ComplianceSupport.Derived();
      if (!isStandardCompliant(good)) fail("MBean is compliant");

      Method[] methods = ComplianceSupport.BasicStandardMBean.class.getMethods();
      List list = Arrays.asList(methods);
      Method[] management = getStandardManagementMethods(good);
      List list2 = Arrays.asList(management);
      assertTrue("Different management interface", list.containsAll(list2) && list2.containsAll(list));
   }

   public void testCompliantInherited() throws Exception
   {
      Object good = new ComplianceSupport.Inherited();
      if (!isStandardCompliant(good)) fail("MBean is compliant");

      Method[] methods = ComplianceSupport.InheritedMBean.class.getMethods();
      List list = Arrays.asList(methods);
      Method[] management = getStandardManagementMethods(good);
      List list2 = Arrays.asList(management);
      assertTrue("Different management interface", list.containsAll(list2) && list2.containsAll(list));
   }

   public void testCompliantNotInherited() throws Exception
   {
      Object good = new ComplianceSupport.NotInherited();
      if (!isStandardCompliant(good)) fail("MBean is compliant");

      Method[] methods = ComplianceSupport.BasicStandardMBean.class.getMethods();
      List list = Arrays.asList(methods);
      Method[] management = getStandardManagementMethods(good);
      List list2 = Arrays.asList(management);
      assertTrue("Different management interface", list.containsAll(list2) && list2.containsAll(list));
   }

   public void testCompliantMulti() throws Exception
   {
      Object good = new ComplianceSupport.Multi();
      if (!isStandardCompliant(good)) fail("MBean is compliant");

      Method[] methods = ComplianceSupport.BasicStandardMBean.class.getMethods();
      List list = new ArrayList();
      list.addAll(Arrays.asList(methods));
      methods = ComplianceSupport.InheritedMBean.class.getMethods();
      list.addAll(Arrays.asList(methods));
      Method[] management = getStandardManagementMethods(good);
      List list2 = Arrays.asList(management);
      assertTrue("Different management interface", list.containsAll(list2) && list2.containsAll(list));
   }

   public void testCompliantPackagePrivate() throws Exception
   {
      String clsName = "test.javax.management.support.ComplianceSupport$PackagePrivate";
      Class cls = getClass().getClassLoader().loadClass(clsName);
      Constructor ctor = cls.getDeclaredConstructor(new Class[0]);
      ctor.setAccessible(true);
      Object good = ctor.newInstance(new Object[0]);
      if (!isStandardCompliant(good)) fail("MBean is compliant");
   }

   public void testNotCompliantDynamicNoClassName() throws Exception
   {
      // In JMX 1.2 it is not possible to create an MBeanInfo with null class name
      Object mbean = new ComplianceSupport.NoClassNameDynamicMBean();
      if (isCompliant(mbean)) fail();
   }

   public void testCompliantBasicDynamic() throws Exception
   {
      Object mbean = new ComplianceSupport.BasicDynamic();
      if (!isDynamicCompliant(mbean)) fail("MBean is compliant");
   }

   public void testCompliantStandardAndDynamic() throws Exception
   {
      // JMX 1.0, this is invalid. For JMX 1.1 this is a dynamic MBean
      Object mbean = new ComplianceSupport.StandardAndDynamic();
      if (!isDynamicCompliant(mbean)) fail("MBean is compliant");

      Class intf = getStandardManagementInterface(mbean);
      if (intf != null) fail("MBean is dynamic");
   }

   public void testCompliantStandardDynamic() throws Exception
   {
      // In JMX 1.0 this is an invalid MBean; in JMX 1.1 is a dynamic MBean
      Object mbean = new ComplianceSupport.StandardDynamic();
      if (!isDynamicCompliant(mbean)) fail("MBean is compliant");

      Class intf = getStandardManagementInterface(mbean);
      if (intf != null) fail("MBean is dynamic");
   }

   public void testDynamicFromStandard() throws Exception
   {
      // A standard mbean subclassed to be dynamic
      Object mbean = new ComplianceSupport.DynamicFromStandard();
      if (!isDynamicCompliant(mbean)) fail("MBean is compliant");

      Class intf = getStandardManagementInterface(mbean);
      if (intf != null) fail("MBean is dynamic");
   }

   public void testStandardFromDynamic() throws Exception
   {
      // A dynamic mbean subclassed to be standard, it remains a dynamic (as of JMX 1.1)
      Object mbean = new ComplianceSupport.StandardFromDynamic();
      if (!isDynamicCompliant(mbean)) fail("MBean is compliant");

      Class intf = getStandardManagementInterface(mbean);
      if (intf != null) fail("MBean is dynamic");
   }

   public void testStandardConstructorInfo() throws Exception
   {
      Object mbean = new ComplianceSupport.BasicStandard();
      MBeanMetaData md = createMBeanMetaData(mbean);
      introspector.introspect(md);
      MBeanConstructorInfo[] constructors = md.getMBeanInfo().getConstructors();
      assertEquals(1, constructors.length);
      MBeanConstructorInfo info = constructors[0];
      assertEquals(mbean.getClass().getName(), info.getName());
   }
}
