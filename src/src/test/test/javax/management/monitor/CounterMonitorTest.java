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
import javax.management.monitor.CounterMonitor;
import javax.management.monitor.Monitor;
import javax.management.monitor.MonitorNotification;

import test.MutableInteger;
import test.MutableObject;

/**
 * @version : $Revision 1.2 $
 */
public class CounterMonitorTest extends MonitorTestCase
{
   public CounterMonitorTest(String name)
   {
      super(name);
   }

   protected Monitor createMonitor()
   {
      return new CounterMonitor();
   }

   public void testCorrectInitialization() throws Exception
   {
      CounterMonitor monitor = (CounterMonitor)createMonitor();
      assertEquals(new Integer(0), monitor.getInitThreshold());
      assertEquals(new Integer(0), monitor.getModulus());
      assertEquals(new Integer(0), monitor.getOffset());
      assertFalse(monitor.getDifferenceMode());
      assertFalse(monitor.getNotify());
   }

   public void testSetThreshold() throws Exception
   {
      CounterMonitor monitor = (CounterMonitor)createMonitor();
      try
      {
         monitor.setThreshold(new Integer(-1));
         fail();
      }
      catch (IllegalArgumentException x)
      {
      }
      try
      {
         monitor.setInitThreshold(new Integer(-1));
         fail();
      }
      catch (IllegalArgumentException x)
      {
      }

      Integer threshold = new Integer(1);
      monitor.setThreshold(threshold);
      assertEquals(monitor.getInitThreshold(), threshold);

      threshold = new Integer(2);
      monitor.setInitThreshold(threshold);
      assertEquals(monitor.getInitThreshold(), threshold);
   }

   public void testSetModulus() throws Exception
   {
      CounterMonitor monitor = (CounterMonitor)createMonitor();
      try
      {
         monitor.setModulus(new Integer(-1));
         fail();
      }
      catch (IllegalArgumentException x)
      {
      }

      Integer modulus = new Integer(1);
      monitor.setModulus(modulus);
      assertEquals(monitor.getModulus(), modulus);
   }

   public void testSetOffset() throws Exception
   {
      CounterMonitor monitor = (CounterMonitor)createMonitor();
      try
      {
         monitor.setOffset(new Integer(-1));
         fail();
      }
      catch (IllegalArgumentException x)
      {
      }

      Integer offset = new Integer(1);
      monitor.setOffset(offset);
      assertEquals(monitor.getOffset(), offset);
   }

   public void testMonitorNotificationForBadCounter() throws Exception
   {
      MBeanServer server = newMBeanServer();
      Monitor monitor = createMonitor();
      server.registerMBean(monitor, ObjectName.getInstance(":service=monitor"));

      Counter counter = new Counter();
      ObjectName counterName = ObjectName.getInstance(":mbean=counter");
      server.registerMBean(counter, counterName);

      monitor.addObservedObject(counterName);
      monitor.setGranularityPeriod(1000);
      monitor.setObservedAttribute("ObjectCounter");

      final MutableInteger times = new MutableInteger(0);
      final MutableObject holder = new MutableObject(null);
      monitor.addNotificationListener(new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            times.set(times.get() + 1);
            holder.set(notification);
         }
      }, null, null);
      monitor.start();

      try
      {
         // Wait for notification to arrive
         while (holder.get() == null) sleep(10);

         // Be sure only one arrived
         sleep(5000);
         assertEquals(times.get(), 1);

         MonitorNotification notification = (MonitorNotification)holder.get();
         assertEquals(notification.getType(), MonitorNotification.OBSERVED_ATTRIBUTE_TYPE_ERROR);
      }
      finally
      {
         monitor.stop();
      }
   }

   public void testIntegerCounter() throws Exception
   {
      MBeanServer server = newMBeanServer();
      CounterMonitor monitor = (CounterMonitor)createMonitor();
      server.registerMBean(monitor, ObjectName.getInstance(":service=monitor"));

      Counter counter = new Counter();
      ObjectName counterName = ObjectName.getInstance(":mbean=counter");
      server.registerMBean(counter, counterName);

      long period = 1000;
      monitor.addObservedObject(counterName);
      monitor.setGranularityPeriod(period);
      monitor.setObservedAttribute("IntegerCounter");
      Integer initThreshold = new Integer(3);
      monitor.setInitThreshold(initThreshold);
      monitor.setNotify(true);
      // No modulus, no offset

      counter.setIntegerCounter(initThreshold.intValue() - 1);

      final MutableInteger times = new MutableInteger(0);
      final MutableObject holder = new MutableObject(null);
      monitor.addNotificationListener(new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            times.set(times.get() + 1);
            holder.set(notification);
         }
      }, null, null);
      monitor.start();

      try
      {
         // Below threshold, no notifications should be sent
         sleep(period * 3);
         assertEquals(times.get(), 0);
         assertNull(holder.get());

         // Above threshold, just one notification should be sent
         counter.setIntegerCounter(initThreshold.intValue() + 1);
         sleep(period * 3);
         assertEquals(times.get(), 1);
         MonitorNotification notification = (MonitorNotification)holder.get();
         assertEquals(notification.getType(), MonitorNotification.THRESHOLD_VALUE_EXCEEDED);

         times.set(0);
         holder.set(null);
         sleep(period * 3);
         assertEquals(times.get(), 0);
      }
      finally
      {
         monitor.stop();
      }
   }

   public void testIntegerCounterWithOffset() throws Exception
   {
      MBeanServer server = newMBeanServer();
      CounterMonitor monitor = (CounterMonitor)createMonitor();
      server.registerMBean(monitor, ObjectName.getInstance(":service=monitor"));

      Counter counter = new Counter();
      ObjectName counterName = ObjectName.getInstance(":mbean=counter");
      server.registerMBean(counter, counterName);

      long period = 1000;
      monitor.addObservedObject(counterName);
      monitor.setGranularityPeriod(period);
      monitor.setObservedAttribute("IntegerCounter");
      Integer initThreshold = new Integer(3);
      monitor.setInitThreshold(initThreshold);
      monitor.setNotify(true);
      Integer offset = new Integer(5);
      monitor.setOffset(offset);
      // No modulus

      counter.setIntegerCounter(initThreshold.intValue() - 1);

      final MutableInteger times = new MutableInteger(0);
      final MutableObject holder = new MutableObject(null);
      monitor.addNotificationListener(new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            times.set(times.get() + 1);
            holder.set(notification);
         }
      }, null, null);
      monitor.start();

      try
      {
         // Below threshold, no notifications should be sent
         sleep(period * 3);
         assertEquals(times.get(), 0);
         assertNull(holder.get());

         // Above threshold, just one notification should be sent
         counter.setIntegerCounter(initThreshold.intValue() + 1);
         sleep(period * 3);
         assertEquals(times.get(), 1);
         MonitorNotification notification = (MonitorNotification)holder.get();
         assertEquals(notification.getType(), MonitorNotification.THRESHOLD_VALUE_EXCEEDED);
         // The threshold should have offset
         Number threshold = monitor.getThreshold(counterName);
         assertEquals(threshold.intValue(), monitor.getInitThreshold().intValue() + offset.intValue());

         times.set(0);
         holder.set(null);
         sleep(period * 3);
         assertEquals(times.get(), 0);

         // Above threshold by more than 1 offset
         counter.setIntegerCounter(initThreshold.intValue() + offset.intValue() * 2 + 1);
         sleep(period * 3);
         assertEquals(times.get(), 1);
         notification = (MonitorNotification)holder.get();
         assertEquals(notification.getType(), MonitorNotification.THRESHOLD_VALUE_EXCEEDED);
         // The threshold should have offset correctly
         threshold = monitor.getThreshold(counterName);
         assertEquals(threshold.intValue(), monitor.getInitThreshold().intValue() + offset.intValue() * 3);

         times.set(0);
         holder.set(null);
         sleep(period * 3);
         assertEquals(times.get(), 0);
      }
      finally
      {
         monitor.stop();
      }
   }

   public interface CounterMBean
   {
      public Object getObjectCounter();

      public Integer getNegativeCounter();

      public int getIntegerCounter();
   }

   public static class Counter implements CounterMBean
   {
      private int integerCounter;

      public Object getObjectCounter()
      {
         return new Object();
      }

      public Integer getNegativeCounter()
      {
         return new Integer(-1);
      }

      public int getIntegerCounter()
      {
         return integerCounter;
      }

      public void setIntegerCounter(int integerCounter)
      {
         this.integerCounter = integerCounter;
      }
   }
}
