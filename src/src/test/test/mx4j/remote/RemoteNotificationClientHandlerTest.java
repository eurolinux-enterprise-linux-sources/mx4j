/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.remote;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.NotificationResult;
import javax.management.remote.TargetedNotification;

import mx4j.remote.AbstractRemoteNotificationClientHandler;
import mx4j.remote.MX4JRemoteConstants;
import mx4j.remote.NotificationTuple;
import mx4j.remote.RemoteNotificationClientHandler;
import test.MX4JTestCase;
import test.MutableBoolean;
import test.MutableLong;
import test.MutableObject;

/**
 * @version $Revision: 1.10 $
 */
public class RemoteNotificationClientHandlerTest extends MX4JTestCase
{
   public RemoteNotificationClientHandlerTest(String s)
   {
      super(s);
   }

   protected void tearDown() throws Exception
   {
      sleep(2000);
   }

   public void testListenerEquality() throws Exception
   {
      AbstractRemoteNotificationClientHandler handler = new AbstractRemoteNotificationClientHandler(null, null, null)
      {
         protected NotificationResult fetchNotifications(long sequence, int maxNumber, long timeout)
         {
            sleep(timeout);
            return null;
         }

         protected void sendConnectionNotificationLost(long number)
         {
         }

         protected long getRetryPeriod()
         {
            return 1000;
         }

         protected int getMaxRetries()
         {
            return 2;
         }
      };

      try
      {
         handler.start();

         NotificationListener listener = new NotificationListener()
         {
            public void handleNotification(Notification notification, Object handback)
            {
            }
         };

         ObjectName name = ObjectName.getInstance(":name=emitter");
         handler.addNotificationListener(new Integer(1), new NotificationTuple(name, listener, null, null));
         handler.addNotificationListener(new Integer(2), new NotificationTuple(name, listener, null, new Object()));

         assertTrue(handler.isActive());

         Integer[] ids = handler.getNotificationListeners(new NotificationTuple(name, listener));
         assertEquals(ids.length, 2);

         handler.removeNotificationListeners(new Integer[]{new Integer(1), new Integer(2)});
         assertTrue(handler.isActive());

         handler.stop();
         assertTrue(!handler.isActive());
      }
      finally
      {
         handler.stop();
      }
   }

   public void testAddRemove() throws Exception
   {
      RemoteNotificationClientHandler handler = new AbstractRemoteNotificationClientHandler(null, null, null)
      {
         public NotificationResult fetchNotifications(long sequenceNumber, int maxNumber, long timeout)
         {
            sleep(timeout);
            return null;
         }

         protected void sendConnectionNotificationLost(long number)
         {
         }

         protected long getRetryPeriod()
         {
            return 1000;
         }

         protected int getMaxRetries()
         {
            return 2;
         }
      };

      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
         }
      };

      try
      {
         handler.start();

         ObjectName name = ObjectName.getInstance(":name=emitter");
         handler.addNotificationListener(new Integer(1), new NotificationTuple(name, listener, null, null));
         Object handback = new Object();
         handler.addNotificationListener(new Integer(2), new NotificationTuple(name, listener, null, handback));
         handler.removeNotificationListeners(new Integer[]{new Integer(2)});

         assertFalse(handler.contains(new NotificationTuple(name, listener, null, handback)));
         assertTrue(handler.contains(new NotificationTuple(name, listener, null, null)));

         Integer id = handler.getNotificationListener(new NotificationTuple(name, listener, null, null));
         assertEquals(id.intValue(), 1);

         handler.removeNotificationListeners(new Integer[]{new Integer(1)});
         assertFalse(handler.contains(new NotificationTuple(name, listener, null, null)));
      }
      finally
      {
         handler.stop();
      }
   }

   public void testNotificationDelivery() throws Exception
   {
      final MutableObject holder = new MutableObject(null);
      final ObjectName name = ObjectName.getInstance(":name=emitter");
      final int nextSequence = 1;
      RemoteNotificationClientHandler handler = new AbstractRemoteNotificationClientHandler(null, null, null)
      {
         public NotificationResult fetchNotifications(long sequenceNumber, int maxNumber, long timeout)
         {
            synchronized (holder)
            {
               Notification notification = new Notification("type", name, 0);
               TargetedNotification targeted = new TargetedNotification(notification, new Integer(1));
               if (sequenceNumber < 0)
               {
                  // Return nextSequence as next sequence number: next call must have this sequence number
                  NotificationResult result1 = new NotificationResult(0, nextSequence, new TargetedNotification[]{targeted});
                  holder.set(result1);
                  return result1;
               }

               if (sequenceNumber == nextSequence)
               {
                  NotificationResult result2 = new NotificationResult(1, nextSequence + 1, new TargetedNotification[]{targeted});
                  holder.set(result2);
                  return result2;
               }

               try
               {
                  holder.wait(timeout);
               }
               catch (InterruptedException x)
               {
                  Thread.currentThread().interrupt();
               }
               holder.set(null);
               return null;
            }
         }

         protected void sendConnectionNotificationLost(long number)
         {
         }

         protected int getMaxRetries()
         {
            return 2;
         }

         protected long getRetryPeriod()
         {
            return 1000;
         }
      };

      final MutableObject notifHolder = new MutableObject(null);
      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            notifHolder.set(notification);
         }
      };

      try
      {
         handler.start();

         handler.addNotificationListener(new Integer(1), new NotificationTuple(name, listener, null, null));

         synchronized (holder)
         {
            // This wait time is much less than the sleep time for the fetcher thread
            while (holder.get() == null) holder.wait(10);
            NotificationResult result = (NotificationResult)holder.get();
            assertEquals(result.getNextSequenceNumber(), nextSequence);
            holder.set(null);

            // Wait for the notification to arrive
            while (notifHolder.get() == null) holder.wait(10);
            Notification notif = (Notification)notifHolder.get();
            assertEquals(notif.getSource(), name);

            // Wait for the second fetchNotification call
            while (holder.get() == null) holder.wait(10);
            result = (NotificationResult)holder.get();
            assertEquals(result.getNextSequenceNumber(), nextSequence + 1);
         }

         handler.removeNotificationListeners(new Integer[]{new Integer(1)});
         sleep(2000);
      }
      finally
      {
         handler.stop();
      }
   }

   public void testNotificationsLost() throws Exception
   {
      final MutableObject holder = new MutableObject(null);
      final MutableLong lost = new MutableLong(0);
      final ObjectName name = ObjectName.getInstance(":name=emitter");
      final long losts = 1;
      RemoteNotificationClientHandler handler = new AbstractRemoteNotificationClientHandler(null, null, null)
      {
         public NotificationResult fetchNotifications(long sequenceNumber, int maxNumber, long timeout)
         {
            synchronized (holder)
            {
               if (sequenceNumber < 0) return new NotificationResult(0, 0, new TargetedNotification[0]);

               // Avoid to spin loop the fetcher thread
               sleep(1000);

               // Return a earliest sequence greater than the requested, to test notification lost behavior
               return new NotificationResult(losts, losts, new TargetedNotification[0]);
            }
         }

         protected void sendConnectionNotificationLost(long number)
         {
            synchronized (holder)
            {
               lost.set(number);
            }
         }

         protected int getMaxRetries()
         {
            return 2;
         }

         protected long getRetryPeriod()
         {
            return 1000;
         }
      };

      Integer id = new Integer(1);
      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
         }
      };

      try
      {
         handler.start();

         handler.addNotificationListener(id, new NotificationTuple(name, listener, null, null));

         synchronized (holder)
         {
            while (lost.get() == 0) holder.wait(10);
            assertEquals(lost.get(), losts);
         }

         handler.removeNotificationListeners(new Integer[]{id});
      }
      finally
      {
         handler.stop();
      }
   }

   public void testConnectionFailure() throws Exception
   {
      final int retries = 2;
      final long period = 1000;
      AbstractRemoteNotificationClientHandler handler = new AbstractRemoteNotificationClientHandler(null, null, null)
      {
         protected NotificationResult fetchNotifications(long sequence, int maxNumber, long timeout) throws IOException
         {
            throw new IOException();
         }

         protected void sendConnectionNotificationLost(long number)
         {
         }

         protected int getMaxRetries()
         {
            return retries;
         }

         protected long getRetryPeriod()
         {
            return period;
         }
      };

      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
         }
      };

      try
      {
         handler.start();

         ObjectName name = ObjectName.getInstance(":name=emitter");
         Integer id = new Integer(1);
         handler.addNotificationListener(id, new NotificationTuple(name, listener, null, null));

         sleep(5000 + period * retries);

         assertTrue(!handler.isActive());
      }
      finally
      {
         handler.stop();
      }
   }

   public void testQueueOverflow() throws Exception
   {
      final Object lock = new Object();
      int queueCapacity = 10;
      final int count = 4;
      final long sleep = 500;
      final Integer id = new Integer(1);
      final ObjectName name = ObjectName.getInstance(":name=emitter");
      final MutableBoolean notify = new MutableBoolean(true);
      final MutableLong queued = new MutableLong(0);
      final MutableLong delivered = new MutableLong(0);

      Map environment = new HashMap();
      environment.put(MX4JRemoteConstants.NOTIFICATION_QUEUE_CAPACITY, new Integer(queueCapacity));
      RemoteNotificationClientHandler handler = new AbstractRemoteNotificationClientHandler(null, null, environment)
      {
         protected NotificationResult fetchNotifications(long sequenceNumber, int maxNumber, long timeout)
         {
            if (sequenceNumber < 0) return new NotificationResult(0, 0, new TargetedNotification[0]);

            boolean doNotify = false;
            synchronized (lock)
            {
               doNotify = notify.get();
            }

            if (doNotify)
            {
               // Avoid spin looping the fetcher thread, but don't sleep too much, we have to fill the client's queue
               sleep(sleep);
               TargetedNotification[] notifications = new TargetedNotification[count];
               for (int i = 0; i < count; ++i) notifications[i] = new TargetedNotification(new Notification("type", name, sequenceNumber + i), id);
               long nextSequence = sequenceNumber + count;
               NotificationResult result = new NotificationResult(0, nextSequence, notifications);
               synchronized (lock)
               {
                  queued.set(getNotificationsCount());
               }
               return result;
            }
            else
            {
               sleep(timeout);
               return new NotificationResult(0, sequenceNumber, new TargetedNotification[0]);
            }
         }

         protected long getRetryPeriod()
         {
            return 1000;
         }

         protected int getMaxRetries()
         {
            return 5;
         }

         protected void sendConnectionNotificationLost(long number)
         {
            System.out.println("Lost notifications: " + number);
            // Stop sending notifications
            synchronized (lock)
            {
               notify.set(false);
               // Deliver notifications until the last we queued on the client
               queued.set(getNotificationsCount());
            }
         }
      };

      NotificationListener listener = new NotificationListener()
      {
         public void handleNotification(Notification notification, Object handback)
         {
            long sequence = notification.getSequenceNumber();
            synchronized (lock)
            {
               delivered.set(sequence);
            }
            System.out.println("Received notification, sequence is " + sequence);
            // Sleep longer than notification emission, to fill the client's queue
            sleep(sleep * 2);
            System.out.println("Handled notification, sequence is " + sequence);
         }
      };

      try
      {
         handler.start();
         handler.addNotificationListener(id, new NotificationTuple(name, listener, null, null));
         // Wait until we empty the client's queue
         synchronized (lock)
         {
            while (notify.get())
            {
               lock.wait(50);
               if (queued.get() > queueCapacity) fail("Queued notifications " + queued.get() + " must not pass max capacity " + queueCapacity);
            }

            // Test timeouts if we don't deliver everything
            while (delivered.get() < queued.get()) lock.wait(10);
         }
      }
      finally
      {
         handler.stop();
      }
   }
}
