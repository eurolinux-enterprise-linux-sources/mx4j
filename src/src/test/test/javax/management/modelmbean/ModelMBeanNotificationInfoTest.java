/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.modelmbean;

import javax.management.Descriptor;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.ModelMBeanNotificationInfo;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.4 $
 * @see
 */

public class ModelMBeanNotificationInfoTest extends TestCase
{

   public ModelMBeanNotificationInfoTest(String s)
   {
      super(s);
   }

   public void setUp() throws Exception
   {
      super.setUp();
   }

   public void tearDown() throws Exception
   {
      super.tearDown();
   }

   public void testSeverityField() throws Exception
   {
      // testcase for bug #775742, #744423 and #775739
      // this should work ok
      Descriptor descriptor = new DescriptorSupport(new String[]{"name", "descriptortype", "severity"},
                                                    new String[]{"aNotification", "notification", "6"});
      ModelMBeanNotificationInfo notification
              = new ModelMBeanNotificationInfo(new String[]{"type1"}, "aNotification", "A description", descriptor);
      assertSame(descriptor.getFieldValue("notification"), notification.getDescriptor().getFieldValue("notification"));

      descriptor = new DescriptorSupport(new String[]{"name", "descriptortype", "severity"},
                                         new String[]{"aNotification", "notification", "0"});
      notification
      = new ModelMBeanNotificationInfo(new String[]{"type1"}, "aNotification", "A description", descriptor);
      assertSame(descriptor.getFieldValue("notification"), notification.getDescriptor().getFieldValue("notification"));
   }

   public void testCaseInsensitiveDescriptorType()
   {
      DescriptorSupport ds = new DescriptorSupport(new String[]{
         "name=badthing",
         "descriptorType=NOTification",
         "severity=1"
      });
      ModelMBeanNotificationInfo info =
              new ModelMBeanNotificationInfo(new String[]{"bad.thing"},
                                             "badthing",
                                             "The bad thing happened",
                                             ds);
   }
}
