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

import test.MX4JTestCase;
import test.MutableInteger;
import test.MutableObject;

/**
 * @version : 1.2 $
 */
public abstract class MonitorTestCase extends MX4JTestCase
{
   public MonitorTestCase(String name)
   {
      super(name);
   }

   protected abstract Monitor createMonitor();

   public void testStartStopIsActive() throws Exception
   {
      Monitor monitor = createMonitor();
      monitor.setGranularityPeriod(1000);
      assertFalse(monitor.isActive());
      monitor.start();
      sleep(5000);
      assertTrue(monitor.isActive());
      monitor.stop();
      assertFalse(monitor.isActive());
      monitor.start();
      assertTrue(monitor.isActive());
      monitor.stop();
      assertFalse(monitor.isActive());
   }

   public void testSetObservedObject() throws Exception
   {
      Monitor monitor = createMonitor();
      ObjectName name1 = ObjectName.getInstance(":name=one");
      monitor.addObservedObject(name1);
      ObjectName name2 = ObjectName.getInstance(":name=two");
      monitor.addObservedObject(name2);
      assertEquals(monitor.getObservedObjects().length, 2);
      assertTrue(monitor.containsObservedObject(name1));
      assertTrue(monitor.containsObservedObject(name2));
      monitor.setObservedObject(name1);
      assertEquals(monitor.getObservedObjects().length, 1);
      assertTrue(monitor.containsObservedObject(name1));
   }

   public void testMonitorNotificationForMBeanNotRegistered() throws Exception
   {
      MBeanServer server = newMBeanServer();
      Monitor monitor = createMonitor();
      server.registerMBean(monitor, ObjectName.getInstance(":service=monitor"));

      ObjectName name1 = ObjectName.getInstance(":name=one");
      monitor.addObservedObject(name1);
      monitor.setGranularityPeriod(1000);
      monitor.setObservedAttribute("dummy");

      final MutableInteger counter = new MutableInteger(0);
      final MutableObject holder = new MutableObject(null);
      monitor.addNotificationListener(new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            counter.set(counter.get() + 1);
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
         assertEquals(counter.get(), 1);

         MonitorNotification notification = (MonitorNotification)holder.get();
         assertEquals(notification.getType(), MonitorNotification.OBSERVED_OBJECT_ERROR);
      }
      finally
      {
         monitor.stop();
      }
   }

   public void testMonitorNotificationForUnknownAttribute() throws Exception
   {
      MBeanServer server = newMBeanServer();
      Monitor monitor = createMonitor();
      server.registerMBean(monitor, ObjectName.getInstance(":service=monitor"));

      ObjectName name1 = ObjectName.getInstance("JMImplementation:type=MBeanServerDelegate");
      monitor.addObservedObject(name1);
      monitor.setGranularityPeriod(1000);
      monitor.setObservedAttribute("dummy");

      final MutableInteger counter = new MutableInteger(0);
      final MutableObject holder = new MutableObject(null);
      monitor.addNotificationListener(new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            counter.set(counter.get() + 1);
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
         assertEquals(counter.get(), 1);

         MonitorNotification notification = (MonitorNotification)holder.get();
         assertEquals(notification.getType(), MonitorNotification.OBSERVED_ATTRIBUTE_ERROR);
      }
      finally
      {
         monitor.stop();
      }
   }
}
