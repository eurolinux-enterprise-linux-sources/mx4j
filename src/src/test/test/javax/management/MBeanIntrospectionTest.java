/* =====================================================================
 *
 * Copyright (c) 2004 Jeremy Boynes.  All rights reserved.
 *
 * =====================================================================
 */
package test.javax.management;

import test.MX4JTestCase;
import mx4j.server.MBeanIntrospector;
import mx4j.server.MBeanMetaData;

import javax.management.MBeanInfo;
import javax.management.MBeanOperationInfo;

/**
 * @version $Revision: 1.1 $ $Date: 2005/02/08 04:11:49 $
 */
public class MBeanIntrospectionTest extends MX4JTestCase {
   private MBeanIntrospector introspector;

   public MBeanIntrospectionTest(String name)
   {
      super(name);
   }

   public void testOperationInfo() throws Exception
   {
      MBeanInfo info = introspect(new Basic());
      MBeanOperationInfo[] operations = info.getOperations();
      assertEquals(1, operations.length);
   }

   private MBeanMetaData createMBeanMetaData(Object mbean) throws Exception
   {
      MBeanMetaData metadata = MBeanMetaData.Factory.create();
      metadata.setMBean(mbean);
      metadata.setClassLoader(mbean.getClass().getClassLoader());
      return metadata;
   }

   private MBeanInfo introspect(Object mbean) throws Exception
   {
      MBeanMetaData md = createMBeanMetaData(mbean);
      introspector.introspect(md);
      return md.getMBeanInfo();
   }

   protected void setUp() throws Exception
   {
      introspector = new MBeanIntrospector();
   }

   public static interface BasicMBean
   {
      boolean is();
   }

   public static class Basic implements BasicMBean
   {
      public boolean is() {
         throw new UnsupportedOperationException();
      }
   }
}
