/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.timer;

import java.util.Date;
import java.util.Vector;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MBeanServerInvocationHandler;
import javax.management.Notification;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.timer.Timer;
import javax.management.timer.TimerMBean;
import javax.management.timer.TimerNotification;

import test.MX4JTestCase;
import test.MutableBoolean;
import test.MutableInteger;
import test.MutableLong;

/**
 * @version $Revision: 1.13 $
 */
public class TimerTest extends MX4JTestCase
{
   private MBeanServer m_server;
   private ObjectName m_timerName;
   private TimerMBean m_timer;

   public TimerTest(String s)
   {
      super(s);
   }

   protected void setUp() throws Exception
   {
      m_server = newMBeanServer();
      m_timerName = new ObjectName("Service:type=Timer");
      m_server.createMBean("javax.management.timer.Timer", m_timerName, null);
      m_timer = (TimerMBean)MBeanServerInvocationHandler.newProxyInstance(m_server, m_timerName, TimerMBean.class, false);
   }

   protected void tearDown() throws Exception
   {
      m_server.unregisterMBean(m_timerName);
   }

   public void testStartStop() throws Exception
   {
      m_timer.start();
      assertTrue(m_timer.isActive());
      m_timer.stop();
      assertFalse(m_timer.isActive());
   }

   public void testStartStopStart() throws Exception
   {
      m_timer.start();
      assertTrue(m_timer.isActive());
      m_timer.stop();
      assertFalse(m_timer.isActive());
      m_timer.start();
      assertTrue(m_timer.isActive());
      // Will be stopped during unregistration
   }

   public void testOneShotNotification() throws Exception
   {
      m_timer.start();

      final long now = System.currentTimeMillis();
      final MutableInteger mid = new MutableInteger(-1);
      final MutableInteger occurrencesCount = new MutableInteger(0);

      final String notifType = "timer-test";
      final long delay = 3 * Timer.ONE_SECOND;

      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            // Test that the listener has been called at the specified time
            long elapsed = System.currentTimeMillis() - now;
            assertTrue(elapsed >= delay);
            assertFalse(elapsed - delay > 50);

            assertTrue(notification instanceof TimerNotification);

            Integer id = ((TimerNotification)notification).getNotificationID();
            assertEquals(mid.get(), id.intValue());

            occurrencesCount.set(occurrencesCount.get() + 1);
         }
      };

      m_server.addNotificationListener(m_timerName, listener, new NotificationFilter()
      {
         public boolean isNotificationEnabled(Notification notification)
         {
            return notification.getType().equals(notifType);
         }
      }, null);

      // Notify after a while
      Date date = new Date(now + delay);
      // One shot notification at the specified time
      Integer id = m_timer.addNotification(notifType, "timer-message", "user-data", date);
      mid.set(id.intValue());

      // Sleep to wait for the notification to happen
      sleep(delay * 2);

      // Check notification arrived
      assertTrue(occurrencesCount.get() == 1);

      // Check that it won't be notified again
      assertTrue(m_timer.getNbNotifications() == 0);
   }

   public void testPeriodicNotification() throws Exception
   {
      m_timer.start();

      final String notifType = "timer-test";
      final String periodicNotifType = "timer-test-periodic";

      final MutableInteger occurrencesCount = new MutableInteger(0);

      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            occurrencesCount.set(occurrencesCount.get() + 1);
         }
      };

      final MutableInteger periodicOccurrences = new MutableInteger(0);
      NotificationListener periodicListener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            periodicOccurrences.set(periodicOccurrences.get() + 1);
         }
      };

      m_server.addNotificationListener(m_timerName, listener, new NotificationFilter()
      {
         public boolean isNotificationEnabled(Notification notification)
         {
            return notification.getType().equals(notifType);
         }
      }, null);
      m_server.addNotificationListener(m_timerName, periodicListener, new NotificationFilter()
      {
         public boolean isNotificationEnabled(Notification notification)
         {
            return notification.getType().equals(periodicNotifType);
         }
      }, null);

      // Register to happen 3 times on the first listener
      long now = System.currentTimeMillis();
      // Notify in one second
      Date date = new Date(now + Timer.ONE_SECOND);
      String message = "timer-message";
      Integer id = m_timer.addNotification(notifType, message, "user-data", date, Timer.ONE_SECOND, 3L);

      // Register to happen periodically
      // Notify in one second
      date = new Date(now + Timer.ONE_SECOND);
      String userDataPeriodic = "user-data-periodic";
      Integer periodicID = m_timer.addNotification(periodicNotifType, "timer-message-periodic", userDataPeriodic, date, Timer.ONE_SECOND);

      // Sleep some time
      sleep(Timer.ONE_SECOND);

      Vector v = m_timer.getAllNotificationIDs();
      assertEquals(v.size(), 2);
      assertTrue(v.contains(id));
      assertTrue(v.contains(periodicID));

      v = m_timer.getNotificationIDs(periodicNotifType);
      assertEquals(v.size(), 1);
      assertTrue(v.contains(periodicID));

      assertEquals(m_timer.getNotificationMessage(id), message);

      assertEquals(m_timer.getNotificationUserData(periodicID), userDataPeriodic);

      // Sleep till the end of the three-time notification
      sleep(Timer.ONE_SECOND * 6);

      // Check that was called the right number of times
      assertEquals(occurrencesCount.get(), 3);

      // The three-time notification is expired now
      v = m_timer.getAllNotificationIDs();
      assertEquals(v.size(), 1);
      assertTrue(v.contains(periodicID));

      Long p = m_timer.getPeriod(periodicID);
      assertEquals(p.longValue(), Timer.ONE_SECOND);

      assertEquals(m_timer.getNotificationType(periodicID), periodicNotifType);

      // Removing non existing notification
      try
      {
         m_timer.removeNotifications("dummy");
         fail("Removed non-existing notification");
      }
      catch (InstanceNotFoundException ignored)
      {
      }

      // Should have already been removed, was the three-shot notification
      try
      {
         m_timer.removeNotification(id);
         fail("Removed non-existing notification");
      }
      catch (InstanceNotFoundException ignored)
      {
      }

      // Some more wait
      sleep(Timer.ONE_SECOND * 3);

      // Removing existing notification
      m_timer.removeNotification(periodicID);

      // Check that none are still present
      assertTrue(m_timer.isEmpty());

      // Wait some more to be sure the periodic listener is not notified anymore
      int periodTimes = periodicOccurrences.get();
      assertTrue(periodTimes > 0);

      sleep(Timer.ONE_SECOND * 5);

      assertEquals(periodicOccurrences.get(), periodTimes);
   }

   public void testTimerNotStarted() throws Exception
   {
      // Don't start the Timer. Notification should not be emitted
//      m_timer.start();

      final MutableBoolean bool = new MutableBoolean(false);
      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            bool.set(true);
         }
      };
      m_server.addNotificationListener(m_timerName, listener, null, null);

      long now = System.currentTimeMillis();
      m_timer.addNotification("timer-notif", "Must not be emitted", null, new Date(now + Timer.ONE_SECOND));

      // Sleep to wait for the notification to happen
      sleep(Timer.ONE_SECOND * 2);

      assertFalse(bool.get());
   }

   public void testAddStopRemoveNotification() throws Exception
   {
      // Check that add + stop + remove behaves correctly

      final MutableInteger count = new MutableInteger(0);
      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            count.set(count.get() + 1);
         }
      };
      m_server.addNotificationListener(m_timerName, listener, null, null);

      long now = System.currentTimeMillis();
      Date date = new Date(now + Timer.ONE_SECOND);

      // Periodic notification
      Integer id = m_timer.addNotification("notif-type", "notif-message", "notif-data", date, Timer.ONE_SECOND);
      m_timer.start();

      // Wait for the notifications to arrive...
      sleep(Timer.ONE_SECOND * 2);

      m_timer.stop();

      int counted = count.get();

      assertEquals(m_timer.getNbNotifications(), 1);

      m_timer.removeNotification(id);
      assertTrue(m_timer.isEmpty());

      // Wait some more to be sure that there are no more notifications
      Thread.sleep(Timer.ONE_SECOND * 5);

      assertEquals(counted, count.get());
   }

   public void testSendPastNotifications1() throws Exception
   {
      final MutableBoolean bool = new MutableBoolean(false);
      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            bool.set(true);
         }
      };
      m_server.addNotificationListener(m_timerName, listener, null, null);

      long now = System.currentTimeMillis();

      // This one-shot notification is already passed, sendPastNotifications is false
      // so the notification must not be emitted
      Date date = new Date(now - Timer.ONE_SECOND);
      m_timer.setSendPastNotifications(false);
      m_timer.addNotification("notif-type", "notif-message", "notif-data", date);
      m_timer.start();

      // Wait that the notification arrives
      sleep(Timer.ONE_SECOND);

      assertFalse(bool.get());
      assertTrue(m_timer.isEmpty());
   }

   public void testNotificationsWithOldDate() throws Exception
   {
      final MutableBoolean bool = new MutableBoolean(false);
      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            bool.set(true);
         }
      };
      m_server.addNotificationListener(m_timerName, listener, null, null);

      long now = System.currentTimeMillis();

      m_timer.start();
      Date date = new Date(now - Timer.ONE_SECOND);
      m_timer.addNotification("notif-type", "notif-message", "notif-data", date);

      // Wait that the notification arrives
      sleep(Timer.ONE_SECOND);

      assertTrue(bool.get());
      assertTrue(m_timer.isEmpty());
   }

   public void testSendPastNotifications2() throws Exception
   {
      final MutableBoolean bool = new MutableBoolean(false);
      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            bool.set(true);
         }
      };
      m_server.addNotificationListener(m_timerName, listener, null, null);

      long now = System.currentTimeMillis();

      // This one-shot notification is already passed, sendPastNotifications is true
      // so the notification must be emitted
      Date date = new Date(now - Timer.ONE_SECOND);
      m_timer.setSendPastNotifications(true);
      m_timer.addNotification("notif-type", "notif-message", "notif-data", date);
      m_timer.start();

      // Wait that the notification arrives
      sleep(Timer.ONE_SECOND);

      assertTrue(bool.get());
      assertTrue(m_timer.isEmpty());
   }

   public void testSendPastNotifications3() throws Exception
   {
      final MutableInteger count = new MutableInteger(0);
      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            count.set(count.get() + 1);
         }
      };
      m_server.addNotificationListener(m_timerName, listener, null, null);

      long now = System.currentTimeMillis();

      // This periodic notification started in the past, sendPastNotifications is false
      // so only some notification must be emitted
      long occurrences = 10;
      long skip = 4;
      Date date = new Date(now - Timer.ONE_SECOND * skip);
      m_timer.setSendPastNotifications(false);
      m_timer.addNotification("notif-type", "notif-message", "notif-data", date, Timer.ONE_SECOND, occurrences);
      m_timer.start();

      // Wait for the notifications to happen
      sleep(Timer.ONE_SECOND * (occurrences + 1));

      // Sometimes we loose one notification because we're not that fast, it's ok.
      long expected = occurrences - skip;
      if (count.get() != expected && count.get() != expected - 1)
         fail("Expected notifications not emitted: expecting " + expected + " got " + count.get());
      assertTrue(m_timer.isEmpty());
   }

   public void testSendPastNotifications4() throws Exception
   {
      final MutableInteger count = new MutableInteger(0);
      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            count.set(count.get() + 1);
         }
      };
      m_server.addNotificationListener(m_timerName, listener, null, null);

      long now = System.currentTimeMillis();

      // This periodic notification started in the past, sendPastNotifications is true
      // so all notifications must be emitted
      long occurrences = 10;
      long skip = 4;
      Date date = new Date(now - Timer.ONE_SECOND * skip);
      m_timer.setSendPastNotifications(true);
      m_timer.addNotification("notif-type", "notif-message", "notif-data", date, Timer.ONE_SECOND, occurrences);
      m_timer.start();

      // Wait for the notifications to happen
      sleep(Timer.ONE_SECOND * (occurrences + 1));

      assertEquals(count.get(), occurrences);
      assertTrue(m_timer.isEmpty());
   }

   public void testSendPastNotifications5() throws Exception
   {
      final MutableInteger count = new MutableInteger(0);
      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            count.set(count.get() + 1);
         }
      };
      m_server.addNotificationListener(m_timerName, listener, null, null);

      long now = System.currentTimeMillis();

      // This periodic notification is started, sendPastNotifications is false
      // the Timer is started, then stopped, then restarted
      long occurrences = 10;
      long pre = 2;
      long skip = 4;
      Date date = new Date(now + Timer.ONE_SECOND);
      m_timer.setSendPastNotifications(false);
      m_timer.addNotification("notif-type", "notif-message", "notif-data", date, Timer.ONE_SECOND, occurrences);
      m_timer.start();

      // Wait for the notifications to happen
      sleep(Timer.ONE_SECOND * pre);
      m_timer.stop();

      // Sometimes we loose one notification because we're not that fast, it's ok.
      if (count.get() != pre && count.get() != pre - 1)
         fail("Expected notifications not emitted: expecting " + pre + " got " + count.get());
      assertEquals(m_timer.getNbNotifications(), 1);

      // Wait to skip some notification
      sleep(Timer.ONE_SECOND * skip);

      // Restart the Timer
      m_timer.start();

      // Wait for the remaining notifications to happen
      sleep(Timer.ONE_SECOND * (occurrences - pre - skip + 1));

      m_timer.stop();

      // Sometimes we loose one notification because we're not that fast, it's ok.
      long expected = occurrences - skip;
      if (count.get() != expected && count.get() != expected - 1)
         fail("Expected notifications not emitted.  Expected " + expected + " or " + (expected - 1) + ". got " + count.get());
      assertTrue(m_timer.isEmpty());
   }

   public void testSendPastNotifications6() throws Exception
   {
      final MutableInteger count = new MutableInteger(0);
      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            count.set(count.get() + 1);
         }
      };
      m_server.addNotificationListener(m_timerName, listener, null, null);

      long now = System.currentTimeMillis();

      // This periodic notification is started, sendPastNotifications is true
      // the Timer is started, then stopped, then restarted
      long occurrences = 10;
      long pre = 2;
      long skip = 4;
      Date date = new Date(now + Timer.ONE_SECOND);
      m_timer.setSendPastNotifications(true);
      m_timer.addNotification("notif-type", "notif-message", "notif-data", date, Timer.ONE_SECOND, occurrences, true);
      m_timer.start();

      // Wait for the notifications to happen
      sleep(Timer.ONE_SECOND * pre);
      m_timer.stop();

      // Sometimes we loose one notification because we're not that fast, it's ok.
      if (count.get() != pre && count.get() != pre - 1)
         fail("Expected notifications not emitted: expecting " + pre + " got " + count.get());
      assertFalse(m_timer.isEmpty());

      // Wait to skip some notification
      sleep(Timer.ONE_SECOND * skip);

      // Restart the Timer
      m_timer.start();

      // Wait for the remaining notifications to happen
      sleep(Timer.ONE_SECOND * (occurrences - pre - skip + 1));

      m_timer.stop();
      assertEquals(count.get(), occurrences);
      assertTrue(m_timer.isEmpty());
   }

   public void testFixedDelay() throws Exception
   {
      m_timer.start();

      final int occurrences = 100;
      final String fdNotifType = "timer-test-fixed-delay";
      final String frNotifType = "timer-test-fixed-rate";

      final MutableInteger frOccurrences = new MutableInteger(0);
      final MutableLong frElapsedTime = new MutableLong(0);
      final MutableLong frLastTime = new MutableLong(System.currentTimeMillis());

      NotificationListener frListener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            if (frOccurrences.get() < occurrences)
            {
               long now = System.currentTimeMillis();
               frElapsedTime.set(frElapsedTime.get() + (now - frLastTime.get()));
               frLastTime.set(now);
               frOccurrences.set(frOccurrences.get() + 1);
            }
         }
      };

      final MutableInteger fdOccurrences = new MutableInteger(0);
      final MutableLong fdElapsedTime = new MutableLong(0);
      final MutableLong fdLastTime = new MutableLong(System.currentTimeMillis());

      NotificationListener fdListener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            if (fdOccurrences.get() < occurrences)
            {
               long now = System.currentTimeMillis();
               fdElapsedTime.set(fdElapsedTime.get() + (now - fdLastTime.get()));
               fdLastTime.set(now);
               fdOccurrences.set(fdOccurrences.get() + 1);
            }
         }
      };

      m_server.addNotificationListener(m_timerName, fdListener, new NotificationFilter()
      {
         public boolean isNotificationEnabled(Notification notification)
         {
            return notification.getType().equals(fdNotifType);
         }
      }, null);

      m_server.addNotificationListener(m_timerName, frListener, new NotificationFilter()
      {
         public boolean isNotificationEnabled(Notification notification)
         {
            return notification.getType().equals(frNotifType);
         }
      }, null);

      // Testing fixed delay/fixed rate
      long now = System.currentTimeMillis();
      // Notify in one second
      Date date = new Date(now + Timer.ONE_SECOND);
      // Register to happen 10 times
      m_timer.addNotification(fdNotifType, "timer-message", "user-data", date, 10, occurrences, false);
      m_timer.addNotification(frNotifType, "timer-message", "user-data", date, 10, occurrences, true);

      // Sleep some time
      while (frOccurrences.get() < occurrences || fdOccurrences.get() < occurrences)
      {
         sleep(10);
         System.gc();
      }

      assertEquals(frOccurrences.get(), occurrences);
      assertEquals(fdOccurrences.get(), occurrences);

      if (((1.0f * frElapsedTime.get()) / fdElapsedTime.get()) > 0.95)
         fail("Fixed rate and fixed delay exhibit no execution rate differences");
   }

   public void testRemoveNotifications() throws Exception
   {
      m_timer.addNotification("mx4j.timer.test", "test notification", null, new Date(System.currentTimeMillis()), 8);
      m_timer.addNotification("mx4j.timer.ignore", "ignore me", null, new Date(System.currentTimeMillis()), 4);
      m_timer.addNotification("mx4j.timer.test", "another test", null, new Date(System.currentTimeMillis()), 8);
      assertEquals(m_timer.getNbNotifications(), 3);
      m_timer.start();
      m_timer.removeNotifications("mx4j.timer.ignore");
      assertEquals(m_timer.getNbNotifications(), 2);
   }

   public void testRemoveNonexistentNotifications() throws Exception
   {
      m_timer.addNotification("mx4j.timer.test", "test notification", null, new Date(System.currentTimeMillis()), 4);
      m_timer.start();
      try
      {
         m_timer.removeNotifications("mx4j.timer.bogus");
         fail("Expecting InstanceNotFoundException");
      }
      catch (InstanceNotFoundException x)
      {
      }
   }
}
