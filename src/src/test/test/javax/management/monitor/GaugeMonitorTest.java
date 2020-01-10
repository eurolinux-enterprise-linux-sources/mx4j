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
import javax.management.monitor.GaugeMonitor;
import javax.management.monitor.Monitor;
import javax.management.monitor.MonitorNotification;

import test.MutableInteger;
import test.MutableObject;

/**
 * @version $Revision: 1.12 $
 */
public class GaugeMonitorTest extends MonitorTestCase
{
   public GaugeMonitorTest(String name)
   {
      super(name);
   }

   protected Monitor createMonitor()
   {
      return new GaugeMonitor();
   }

   public void testCorrectInitialization() throws Exception
   {
      GaugeMonitor monitor = (GaugeMonitor)createMonitor();
      assertEquals(new Integer(0), monitor.getHighThreshold());
      assertEquals(new Integer(0), monitor.getLowThreshold());
      assertFalse(monitor.getDifferenceMode());
      assertFalse(monitor.getNotifyHigh());
      assertFalse(monitor.getNotifyLow());
   }

   public void testSetThresholds() throws Exception
   {
      GaugeMonitor monitor = (GaugeMonitor)createMonitor();
      try
      {
         monitor.setThresholds(null, null);
         fail();
      }
      catch (IllegalArgumentException ignored)
      {
      }
      try
      {
         monitor.setThresholds(new Integer(0), null);
         fail();
      }
      catch (IllegalArgumentException ignored)
      {
      }
      try
      {
         monitor.setThresholds(null, new Integer(0));
         fail();
      }
      catch (IllegalArgumentException ignored)
      {
      }
      try
      {
         // Different types
         monitor.setThresholds(new Integer(1), new Long(0));
         fail();
      }
      catch (IllegalArgumentException ignored)
      {
      }
      try
      {
         // High less than low
         monitor.setThresholds(new Integer(0), new Integer(1));
         fail();
      }
      catch (IllegalArgumentException ignored)
      {
      }

      monitor.setThresholds(new Float(5.7), new Float(5.0));
   }

   /**
    * This also serves as a test case for bug #710028
    */
   public void testHighHysteresisStartBelow() throws Exception
   {
      ObjectName name = new ObjectName(":mbean=target");
      ObjectName monitorName = new ObjectName(":monitor=gauge");

      MBeanServer server = newMBeanServer();
      GaugeMonitor monitor = (GaugeMonitor)createMonitor();
      monitor.setDifferenceMode(true);
      monitor.addObservedObject(name);
      monitor.setObservedAttribute("Integer");
      int period = 1000;
      monitor.setGranularityPeriod(period);
      Integer high = new Integer(10);
      Integer low = new Integer(5);
      monitor.setThresholds(high, low);
      monitor.setNotifyHigh(true);
      monitor.setNotifyLow(false);
      server.registerMBean(monitor, monitorName);

      // Initial value < lowThreshold
      MonitorTarget target = new MonitorTarget();
      int value = low.intValue() - 1;
      target.setInteger(value);
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
         assertEquals(times.get(), 0);
         assertNull(holder.get());

         // Set gauge above high threshold
         value = value + high.intValue() + 1;
         target.setInteger(value);
         sleep(period * 3);
         assertEquals(times.get(), 1);
         MonitorNotification notification = (MonitorNotification)holder.get();
         assertEquals(notification.getType(), MonitorNotification.THRESHOLD_HIGH_VALUE_EXCEEDED);

         times.set(0);
         holder.set(null);
         sleep(period * 3);
         assertEquals(times.get(), 0);

         // Set gauge inside threshold
         value = value + low.intValue() + 1;
         target.setInteger(value);
         sleep(period * 3);
         assertEquals(times.get(), 0);
         assertNull(holder.get());

         // Set gauge above threshold again
         value = value + high.intValue() + 1;
         target.setInteger(value);
         sleep(period * 3);
         assertEquals(times.get(), 1);
         notification = (MonitorNotification)holder.get();
         assertEquals(notification.getType(), MonitorNotification.THRESHOLD_HIGH_VALUE_EXCEEDED);
      }
      finally
      {
         monitor.stop();
      }
   }

   /**
    * This also serves as a test case for bug #742554
    */
   public void testLowHysteresisStartInside() throws Exception
   {
      ObjectName name = new ObjectName(":mbean=target");
      ObjectName monitorName = new ObjectName(":monitor=gauge");

      MBeanServer server = newMBeanServer();
      GaugeMonitor monitor = (GaugeMonitor)createMonitor();
      monitor.setDifferenceMode(true);
      monitor.addObservedObject(name);
      monitor.setObservedAttribute("Integer");
      int period = 1000;
      monitor.setGranularityPeriod(period);
      Integer high = new Integer(5);
      Integer low = new Integer(0);
      monitor.setThresholds(high, low);
      monitor.setNotifyHigh(true);
      monitor.setNotifyLow(true);
      server.registerMBean(monitor, monitorName);

      // Initial gauge inside thresholds
      MonitorTarget target = new MonitorTarget();
      int value = low.intValue() + 1;
      target.setInteger(value);
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
         // Inside the thresholds, be sure low notification
         sleep(period * 3);
         assertEquals(times.get(), 1);
         MonitorNotification notification = (MonitorNotification)holder.get();
         assertEquals(notification.getType(), MonitorNotification.THRESHOLD_LOW_VALUE_EXCEEDED);

         times.set(0);
         holder.set(null);
         sleep(period * 3);
         assertEquals(times.get(), 0);

         // Monitoring takes time, so I disable low notification to be sure to get only the high one
         // The monitor is in difference mode, so the first time will get the high notification, but
         // the second time will get zero, since the gauge did not change, which will triggers a low notification
         monitor.setNotifyLow(false);
         // Set gauge above high threshold
         value = value + high.intValue() + 1;
         target.setInteger(value);
         sleep(period * 3);
         assertEquals(times.get(), 1);
         notification = (MonitorNotification)holder.get();
         assertEquals(notification.getType(), MonitorNotification.THRESHOLD_HIGH_VALUE_EXCEEDED);

         times.set(0);
         holder.set(null);
         sleep(period * 3);
         assertEquals(times.get(), 0);

         monitor.setNotifyHigh(false);
         monitor.setNotifyLow(true);
         // Set gauge above high threshold, so just after goes below low threshold
         value = value + high.intValue() + 1;
         target.setInteger(value);
         sleep(period * 3);
         assertEquals(times.get(), 1);
         notification = (MonitorNotification)holder.get();
         assertEquals(notification.getType(), MonitorNotification.THRESHOLD_LOW_VALUE_EXCEEDED);

         times.set(0);
         holder.set(null);
         sleep(period * 3);
         assertEquals(times.get(), 0);

         // Set gauge inside threshold
         value = value + low.intValue() + 1;
         target.setInteger(value);
         sleep(period * 3);
         assertEquals(times.get(), 0);
         assertNull(holder.get());
      }
      finally
      {
         monitor.stop();
      }
   }

   /**
    * This also serves as a test case for bug #822849
    */
   public void testGetDerivedGauge() throws Exception
   {
      ObjectName name = new ObjectName(":mbean=target");
      ObjectName monitorName = new ObjectName(":monitor=gauge");

      MBeanServer server = newMBeanServer();
      GaugeMonitor monitor = (GaugeMonitor)createMonitor();
      monitor.setDifferenceMode(false);
      monitor.addObservedObject(name);
      monitor.setObservedAttribute("Integer");
      int period = 1000;
      monitor.setGranularityPeriod(period);
      server.registerMBean(monitor, monitorName);

      // Set initial gauge
      MonitorTarget target = new MonitorTarget();
      int gauge = 4;
      target.setInteger(gauge);
      server.registerMBean(target, name);

      monitor.start();

      try
      {
         sleep(period * 3);

         Number observed = monitor.getDerivedGauge(name);
         assertEquals(observed.intValue(), gauge);
      }
      finally
      {
         monitor.stop();
      }
   }

   public interface MonitorTargetMBean
   {
      public int getInteger();
   }

   public static class MonitorTarget implements MonitorTargetMBean
   {
      private int value;

      public int getInteger()
      {
         return value;
      }

      public void setInteger(int value)
      {
         this.value = value;
      }
   }
}
