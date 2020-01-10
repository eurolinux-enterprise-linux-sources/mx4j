/* =====================================================================
 *
 * Copyright (c) 2004 Jeremy Boynes.  All rights reserved.
 *
 * =====================================================================
 */
package test.javax.management.monitor;

import junit.framework.TestCase;

import javax.management.*;
import javax.management.monitor.CounterMonitor;
import javax.management.monitor.MonitorNotification;
import java.util.ArrayList;
import java.util.List;

import mx4j.log.Log;
import mx4j.log.Logger;

/**
 * @version $Revision: 1.1 $ $Date: 2005/02/15 22:31:40 $
 */
public class CounterNotificationTest extends TestCase implements NotificationListener
{
   private static final Number ZERO = new Integer(0);
   private MBeanServer mbServer;

   private ObjectName observedName;
   private ObservedObject observed;

   private ObjectName counterMonitorName;
   private CounterMonitor counterMonitor;

   private List notifications;
   private int granularity;
   private Logger logger;

   public void testSimpleIncrementingCounter() throws Exception
   {
      initMonitor(ZERO, ZERO, ZERO, false);
      counterMonitor.start();

      setAttribute(new Integer(-2));
      sleep();
      assertEquals(0, notifications.size());
      checkMonitor(new Integer(-2), ZERO);

      setAttribute(new Integer(-1));
      sleep();
      assertEquals(0, notifications.size());
      checkMonitor(new Integer(-1), ZERO);

      setAttribute(new Integer(0));
      sleep();
      assertEquals(1, notifications.size());
      checkMonitor(new Integer(0), ZERO);
      checkNotification((Notification)notifications.get(0));

      setAttribute(new Integer(1));
      sleep();
      assertEquals(1, notifications.size());
      checkMonitor(new Integer(1), ZERO);

      setAttribute(new Integer(2));
      sleep();
      assertEquals(1, notifications.size());
      checkMonitor(new Integer(2), ZERO);
   }

   public void testSimpleCounterWithOffset() throws Exception
   {
      initMonitor(ZERO, new Integer(2), ZERO, false);
      counterMonitor.start();

      setAttribute(new Integer(0));
      sleep();
      checkMonitor(new Integer(0), new Integer(2));
      assertEquals(1, notifications.size());
      checkNotification((Notification)notifications.get(0));

      setAttribute(new Integer(1));
      sleep();
      checkMonitor(new Integer(1), new Integer(2));
      assertEquals(1, notifications.size());

      setAttribute(new Integer(2));
      sleep();
      checkMonitor(new Integer(2), new Integer(4));
      assertEquals(2, notifications.size());
      checkNotification((Notification)notifications.get(1));

      setAttribute(new Integer(3));
      sleep();
      checkMonitor(new Integer(3), new Integer(4));
      assertEquals(2, notifications.size());

      setAttribute(new Integer(4));
      sleep();
      checkMonitor(new Integer(4), new Integer(6));
      assertEquals(3, notifications.size());
      checkNotification((Notification)notifications.get(2));

      setAttribute(new Integer(5));
      sleep();
      checkMonitor(new Integer(5), new Integer(6));
      assertEquals(3, notifications.size());

      setAttribute(new Integer(6));
      sleep();
      checkMonitor(new Integer(6), new Integer(8));
      assertEquals(4, notifications.size());
      checkNotification((Notification)notifications.get(3));
   }

   public void testSimpleCounterWithModulusAndDecreasingOffset() throws Exception
   {
      initMonitor(new Integer(1), new Integer(1), new Integer(5), false);
      counterMonitor.start();

      setAttribute(new Integer(1));
      sleep();
      checkMonitor(new Integer(1), new Integer(2));
      assertEquals(1, notifications.size());

      counterMonitor.setOffset(new Integer(2));
      setAttribute(new Integer(2));
      sleep();
      checkMonitor(new Integer(2), new Integer(4));
      assertEquals(2, notifications.size());

      counterMonitor.setOffset(new Integer(1));
      setAttribute(new Integer(4));
      sleep();
      checkMonitor(new Integer(4), new Integer(5));
      assertEquals(3, notifications.size());

      setAttribute(new Integer(5));
      sleep();
      checkMonitor(new Integer(5), new Integer(6));
      assertEquals(4, notifications.size());

      setAttribute(new Integer(5));
      sleep();
      checkMonitor(new Integer(5), new Integer(6));
      assertEquals(4, notifications.size());

      setAttribute(new Integer(2));
      sleep();
      checkMonitor(new Integer(2), new Integer(3));
      assertEquals(5, notifications.size());

      setAttribute(new Integer(1));
      sleep();
//      Thread.sleep(10000000);
      checkMonitor(new Integer(1), new Integer(3));
      assertEquals(5, notifications.size());

      setAttribute(new Integer(1));
      sleep();
      checkMonitor(new Integer(1), new Integer(3));
      assertEquals(5, notifications.size());

      setAttribute(new Integer(3));
      sleep();
      checkMonitor(new Integer(3), new Integer(4));
      assertEquals(6, notifications.size());
   }

   public void testDeltaWithModulus() throws Exception
   {
      initMonitor(new Integer(2), ZERO, new Integer(7), true);
      counterMonitor.start();

      setAttribute(new Integer(-2));
      sleep();
      checkMonitor(new Integer(-2), new Integer(2));
      assertEquals(0, notifications.size());

      setAttribute(new Integer(-1));
      sleep();
      checkMonitor(new Integer(-1), new Integer(2));
      assertEquals(0, notifications.size());

      setAttribute(new Integer(0));
      sleep();
      checkMonitor(new Integer(0), new Integer(2));
      assertEquals(0, notifications.size());

      setAttribute(new Integer(8));
      sleep();
      checkMonitor(new Integer(8), new Integer(2));
      assertEquals(1, notifications.size());

      setAttribute(new Integer(3));
      sleep();
      checkMonitor(new Integer(3), new Integer(2));
      assertEquals(2, notifications.size());

      setAttribute(new Integer(5));
      sleep();
      checkMonitor(new Integer(5), new Integer(2));
      assertEquals(3, notifications.size());

      setAttribute(new Integer(0));
      sleep();
      checkMonitor(new Integer(0), new Integer(2));
      assertEquals(4, notifications.size());

      setAttribute(new Integer(1));
      sleep();
      checkMonitor(new Integer(1), new Integer(2));
      assertEquals(4, notifications.size());

      setAttribute(new Integer(4));
      sleep();
      checkMonitor(new Integer(4), new Integer(2));
      assertEquals(5, notifications.size());

      setAttribute(new Integer(5));
      sleep();
      checkMonitor(new Integer(5), new Integer(2));
      assertEquals(5, notifications.size());

      setAttribute(new Integer(0));
      sleep();
      checkMonitor(new Integer(0), new Integer(2));
      assertEquals(6, notifications.size());
   }

   public void testDeltaWithOffsetAndModulus() throws Exception
   {
      initMonitor(new Integer(2), new Integer(1), new Integer(7), true);
      counterMonitor.start();

      setAttribute(new Integer(0));
      sleep();
      checkMonitor(new Integer(0), new Integer(2));
      assertEquals(0, notifications.size());

      setAttribute(new Integer(2));
      sleep();
      checkMonitor(new Integer(2), new Integer(3));
      assertEquals(1, notifications.size());

      setAttribute(new Integer(3));
      sleep();
      checkMonitor(new Integer(3), new Integer(3));
      assertEquals(1, notifications.size());

      setAttribute(new Integer(5));
      sleep();
      checkMonitor(new Integer(5), new Integer(3));
      assertEquals(1, notifications.size());

      setAttribute(new Integer(0));
      sleep();
      checkMonitor(new Integer(0), new Integer(3));
      assertEquals(2, notifications.size());

      setAttribute(new Integer(1));
      sleep();
      checkMonitor(new Integer(1), new Integer(3));
      assertEquals(2, notifications.size());

      setAttribute(new Integer(4));
      sleep();
      checkMonitor(new Integer(4), new Integer(4));
      assertEquals(3, notifications.size());

      setAttribute(new Integer(5));
      sleep();
      checkMonitor(new Integer(5), new Integer(4));
      assertEquals(3, notifications.size());

      setAttribute(new Integer(0));
      sleep();
      checkMonitor(new Integer(0), new Integer(3));
      assertEquals(4, notifications.size());
   }

   private void setAttribute(Number value) throws Exception
   {
      logger.debug("Setting attribute to " + value);
      mbServer.setAttribute(observedName, new Attribute("Counter", value));
   }

   private void sleep()
   {
      try
      {
         Thread.sleep(granularity * 3);
      }
      catch (InterruptedException e)
      {
      }
   }

   private void initMonitor(Number initThreshold, Number offset, Number modulus, boolean difference)
   {
      counterMonitor.setObservedObject(observedName);
      counterMonitor.setObservedAttribute("Counter");
      counterMonitor.setGranularityPeriod(granularity);
      counterMonitor.setNotify(true);

      counterMonitor.setThreshold(initThreshold);
      counterMonitor.setOffset(offset);
      counterMonitor.setModulus(modulus);
      counterMonitor.setDifferenceMode(difference);
   }

   private void checkMonitor(Number value, Number threshold) throws Exception
   {
      assertEquals(value, mbServer.getAttribute(observedName, "Counter"));
      assertEquals(threshold, counterMonitor.getThreshold());
   }

   private void checkNotification(Notification notification)
   {
      assertEquals(MonitorNotification.THRESHOLD_VALUE_EXCEEDED, notification.getType());
   }

   public void handleNotification(Notification notification, Object handback)
   {
      notifications.add(notification);
   }

   protected void setUp() throws Exception
   {
      logger = Log.getLogger(this.getClass().getName());

      granularity = 200;
      notifications = new ArrayList();
      mbServer = MBeanServerFactory.newMBeanServer();

      observedName = new ObjectName("test:name=ObservedObject");
      observed = new ObservedObject();
      mbServer.registerMBean(observed, observedName);

      counterMonitorName = new ObjectName("test:name=CounterMonitor");
      counterMonitor = new CounterMonitor();
      mbServer.registerMBean(counterMonitor, counterMonitorName);
      counterMonitor.addNotificationListener(this, null, null);
   }

   protected void tearDown() throws Exception
   {
      if (counterMonitor.isActive()) counterMonitor.stop();
      mbServer.unregisterMBean(counterMonitorName);
      mbServer.unregisterMBean(observedName);
   }

   public static interface ObservedObjectMBean
   {
      Number getCounter();

      void setCounter(Number counter);
   }

   public static class ObservedObject implements ObservedObjectMBean
   {
      private Number counter;

      public Number getCounter()
      {
         return counter;
      }

      public void setCounter(Number counter)
      {
         this.counter = counter;
      }
   }
}
