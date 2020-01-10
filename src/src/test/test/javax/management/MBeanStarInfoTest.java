/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management;

import java.util.Map;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.4 $
 */
public class MBeanStarInfoTest extends TestCase
{
   public MBeanStarInfoTest(String s)
   {
      super(s);
   }

   public void testValidMBeanStarInfoNullName() throws Exception
   {
      new MBeanAttributeInfo(null, "java.lang.String", "description", true, false, false);
      new MBeanConstructorInfo(null, "description", null);
      new MBeanOperationInfo(null, "description", null, "java.lang.String", MBeanOperationInfo.UNKNOWN);
      new MBeanParameterInfo(null, "java.lang.String", "description");
   }

   public void testValidMBeanStarInfoEmptyName() throws Exception
   {
      new MBeanAttributeInfo("", "java.lang.String", "description", true, false, false);
      new MBeanConstructorInfo("", "description", null);
      new MBeanOperationInfo("", "description", null, "java.lang.String", MBeanOperationInfo.UNKNOWN);
      new MBeanParameterInfo("", "java.lang.String", "description");
   }

   public void testValidMBeanStarInfoInvalidName1() throws Exception
   {
      new MBeanAttributeInfo("123", "java.lang.String", "description", true, false, false);
      new MBeanConstructorInfo("123", "description", null);
      new MBeanOperationInfo("123", "description", null, "java.lang.String", MBeanOperationInfo.UNKNOWN);
      new MBeanParameterInfo("123", "java.lang.String", "description");
   }

   public void testValidMBeanStarInfoInvalidName2() throws Exception
   {
      new MBeanAttributeInfo(".123", "java.lang.String", "description", true, false, false);
      new MBeanConstructorInfo(".123", "description", null);
      new MBeanOperationInfo(".123", "description", null, "java.lang.String", MBeanOperationInfo.UNKNOWN);
      new MBeanParameterInfo(".123", "java.lang.String", "description");
   }

   public void testValidMBeanStarInfoInvalidName3() throws Exception
   {
      new MBeanAttributeInfo(" identifier", "java.lang.String", "description", true, false, false);
      new MBeanConstructorInfo(" identifier", "description", null);
      new MBeanOperationInfo(" identifier", "description", null, "java.lang.String", MBeanOperationInfo.UNKNOWN);
      new MBeanParameterInfo(" identifier", "java.lang.String", "description");
   }

   public void testValidMBeanStarInfoNullType() throws Exception
   {
      new MBeanAttributeInfo("identifier", null, "description", true, false, false);
      new MBeanNotificationInfo(new String[0], null, "description");
      new MBeanOperationInfo("identifier", "description", null, null, MBeanOperationInfo.UNKNOWN);
      new MBeanParameterInfo("identifier", null, "description");
   }

   public void testValidMBeanStarInfoEmptyType() throws Exception
   {
      new MBeanAttributeInfo("identifier", "", "description", true, false, false);
      new MBeanNotificationInfo(new String[0], "", "description");
      new MBeanOperationInfo("identifier", "description", null, "", MBeanOperationInfo.UNKNOWN);
      new MBeanParameterInfo("identifier", "", "description");
   }

   public void testValidMBeanStarInfoInvalidType1() throws Exception
   {
      new MBeanAttributeInfo("identifier", "123", "description", true, false, false);
      new MBeanNotificationInfo(new String[0], "123", "description");
      new MBeanOperationInfo("identifier", "description", null, "123", MBeanOperationInfo.UNKNOWN);
      new MBeanParameterInfo("identifier", "123", "description");
   }

   public void testValidMBeanStarInfoInvalidType2() throws Exception
   {
      new MBeanAttributeInfo("identifier", ".type", "description", true, false, false);
      new MBeanNotificationInfo(new String[0], ".type", "description");
      new MBeanOperationInfo("identifier", "description", null, ".type", MBeanOperationInfo.UNKNOWN);
      new MBeanParameterInfo("identifier", ".type", "description");
   }

   public void testValidMBeanStarInfoInvalidType3() throws Exception
   {
      new MBeanAttributeInfo("identifier", " type", "description", true, false, false);
      new MBeanNotificationInfo(new String[0], " type", "description");
      new MBeanOperationInfo("identifier", "description", null, " type", MBeanOperationInfo.UNKNOWN);
      new MBeanParameterInfo("identifier", " type", "description");
   }

   public void testValidMBeanStarInfo1() throws Exception
   {
      new MBeanAttributeInfo("name", "type", "description", true, false, false);
      new MBeanConstructorInfo("name", "description", null);
      new MBeanNotificationInfo(new String[0], "type", "description");
      new MBeanOperationInfo("name", "description", null, "type", MBeanOperationInfo.UNKNOWN);
      new MBeanParameterInfo("name", "type", "description");
   }

   public void testValidMBeanStarInfo2() throws Exception
   {
      new MBeanAttributeInfo("_", int.class.getName(), "description", true, false, false);
      new MBeanConstructorInfo("_", "description", null);
      new MBeanNotificationInfo(new String[0], "java.lang.String", "description");
      new MBeanOperationInfo("_", "description", null, int[].class.getName(), MBeanOperationInfo.UNKNOWN);
      new MBeanParameterInfo("_", int[][].class.getName(), "description");
   }

   public void testValidMBeanStarInfo3() throws Exception
   {
      new MBeanAttributeInfo("a", String.class.getName(), "description", true, false, false);
      new MBeanConstructorInfo("a", "description", null);
      new MBeanNotificationInfo(new String[0], "java.lang.String", "description");
      new MBeanOperationInfo("a", "description", null, String[].class.getName(), MBeanOperationInfo.UNKNOWN);
      new MBeanParameterInfo("a", String[][].class.getName(), "description");
   }

   public void testValidMBeanStarInfo4() throws Exception
   {
      new MBeanAttributeInfo("a", Map.Entry.class.getName(), "description", true, false, false);
      new MBeanConstructorInfo("a", "description", null);
      new MBeanNotificationInfo(new String[0], "java.lang.String", "description");
      new MBeanOperationInfo("a", "description", null, Map.Entry[].class.getName(), MBeanOperationInfo.UNKNOWN);
      new MBeanParameterInfo("a", Map.Entry[][].class.getName(), "description");
   }

   // TODO: implements tests for MBeanInfo
}
