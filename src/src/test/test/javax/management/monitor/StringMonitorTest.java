/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.monitor;

import javax.management.MBeanServer;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.monitor.Monitor;
import javax.management.monitor.MonitorNotification;
import javax.management.monitor.StringMonitor;

import test.MutableInteger;
import test.MutableObject;

/**
 * @version $Revision: 1.8 $
 */
public class StringMonitorTest extends MonitorTestCase
{
   public StringMonitorTest(String name)
   {
      super(name);
   }

   protected Monitor createMonitor()
   {
      return new StringMonitor();
   }

   public void testCorrectInitialization() throws Exception
   {
      StringMonitor monitor = (StringMonitor)createMonitor();
      assertEquals("", monitor.getStringToCompare());
      assertFalse(monitor.getNotifyDiffer());
      assertFalse(monitor.getNotifyMatch());
   }

   public void testSetStringToCompare() throws Exception
   {
      StringMonitor monitor = (StringMonitor)createMonitor();
      try
      {
         monitor.setStringToCompare(null);
         fail();
      }
      catch (IllegalArgumentException x)
      {
      }
   }

   /**
    * The case outlined in the JMX specification
    */
   public void testSpecificationCase() throws Exception
   {
      ObjectName name = new ObjectName(":mbean=target");
      ObjectName monitorName = new ObjectName(":monitor=gauge");

      MBeanServer server = newMBeanServer();
      StringMonitor monitor = (StringMonitor)createMonitor();
      String reference = "XYZ";
      monitor.setStringToCompare(reference);
      monitor.setNotifyMatch(true);
      monitor.setNotifyDiffer(true);
      monitor.addObservedObject(name);
      monitor.setObservedAttribute("String");
      int period = 1000;
      monitor.setGranularityPeriod(period);
      server.registerMBean(monitor, monitorName);

      MonitorTarget target = new MonitorTarget();
      target.setString(reference);
      server.registerMBean(target, name);

      final MutableInteger times = new MutableInteger(0);
      final MutableObject holder = new MutableObject(null);
      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            times.set(times.get() + 1);
            holder.set(notification);
         }
      };
      server.addNotificationListener(monitorName, listener, null, null);

      monitor.start();

      try
      {
         sleep(period * 3);
         assertEquals(times.get(), 1);
         MonitorNotification notification = (MonitorNotification)holder.get();
         assertEquals(notification.getType(), MonitorNotification.STRING_TO_COMPARE_VALUE_MATCHED);

         times.set(0);
         holder.set(null);
         target.setString("xx");

         sleep(period * 3);
         assertEquals(times.get(), 1);
         notification = (MonitorNotification)holder.get();
         assertEquals(notification.getType(), MonitorNotification.STRING_TO_COMPARE_VALUE_DIFFERED);

         times.set(0);
         holder.set(null);
         target.setString(reference);

         sleep(period * 3);
         assertEquals(times.get(), 1);
         notification = (MonitorNotification)holder.get();
         assertEquals(notification.getType(), MonitorNotification.STRING_TO_COMPARE_VALUE_MATCHED);

         times.set(0);
         holder.set(null);
         target.setString("yyyy");

         sleep(period * 3);
         assertEquals(times.get(), 1);
         notification = (MonitorNotification)holder.get();
         assertEquals(notification.getType(), MonitorNotification.STRING_TO_COMPARE_VALUE_DIFFERED);

         times.set(0);
         holder.set(null);
         target.setString("zzzzz");

         sleep(period * 3);
         assertEquals(times.get(), 0);
         assertNull(holder.get());
      }
      finally
      {
         monitor.stop();
      }
   }

   public interface MonitorTargetMBean
   {
      public String getString();
   }

   public static class MonitorTarget implements MonitorTargetMBean
   {
      private String value;

      public String getString()
      {
         return value;
      }

      public void setString(String value)
      {
         this.value = value;
      }
   }
}
