/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.remote;

import java.util.HashMap;
import java.util.Map;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.remote.NotificationResult;

import mx4j.remote.DefaultRemoteNotificationServerHandler;
import mx4j.remote.MX4JRemoteConstants;
import mx4j.remote.RemoteNotificationServerHandler;
import test.MX4JTestCase;

/**
 * @version $Revision: 1.6 $
 */
public class RemoteNotificationServerHandlerTest extends MX4JTestCase
{
   public RemoteNotificationServerHandlerTest(String s)
   {
      super(s);
   }

   public void testFirstFetch() throws Exception
   {
      RemoteNotificationServerHandler handler = new DefaultRemoteNotificationServerHandler(null);

      // First call is with sequence number negative; no notification waiting
      NotificationResult result = handler.fetchNotifications(-1, 10, 100);

      assertEquals(result.getEarliestSequenceNumber(), 0);
      assertEquals(result.getNextSequenceNumber(), 0);
      assertNotNull(result.getTargetedNotifications());
      assertEquals(result.getTargetedNotifications().length, 0);
   }

   public void testNotificationMissBetweenAddAndFetch() throws Exception
   {
      RemoteNotificationServerHandler handler = new DefaultRemoteNotificationServerHandler(null);

      // Add a notification
      NotificationListener listener = handler.getServerNotificationListener();
      Notification notification = new Notification("dummy", this, 0);
      Integer listenerID = new Integer(1);
      listener.handleNotification(notification, listenerID);

      // Fetch notifications
      NotificationResult result = handler.fetchNotifications(-1, 10, 100);
      assertEquals(result.getEarliestSequenceNumber(), 0);
      assertEquals(result.getNextSequenceNumber(), 1);
      assertNotNull(result.getTargetedNotifications());
      assertEquals(result.getTargetedNotifications().length, 0);

      // Add another notification
      listener.handleNotification(notification, listenerID);

      // Fetch again
      result = handler.fetchNotifications(result.getNextSequenceNumber(), 10, 100);
      assertEquals(result.getEarliestSequenceNumber(), 0);
      assertEquals(result.getNextSequenceNumber(), 2);
      assertNotNull(result.getTargetedNotifications());
      assertEquals(result.getTargetedNotifications().length, 1);
   }

   public void testBufferWithSmallCapacity() throws Exception
   {
      int bufferCapacity = 1;
      Map environment = new HashMap();
      environment.put(MX4JRemoteConstants.NOTIFICATION_BUFFER_CAPACITY, new Integer(bufferCapacity));
      RemoteNotificationServerHandler handler = new DefaultRemoteNotificationServerHandler(environment);

      // Fill the buffer
      NotificationListener listener = handler.getServerNotificationListener();
      Notification notification = new Notification("dummy", this, 0);
      Integer listenerID = new Integer(1);
      for (int i = 0; i < bufferCapacity; ++i) listener.handleNotification(notification, listenerID);

      // Fetch first time
      NotificationResult result = handler.fetchNotifications(-1, bufferCapacity + 1, 100);
      assertEquals(result.getEarliestSequenceNumber(), 0);
      assertEquals(result.getNextSequenceNumber(), bufferCapacity);
      assertNotNull(result.getTargetedNotifications());
      assertEquals(result.getTargetedNotifications().length, 0);

      // Add another notification: the buffer is full, the earliest sequence number must change
      listener.handleNotification(notification, listenerID);

      // Fetch again
      result = handler.fetchNotifications(result.getNextSequenceNumber(), bufferCapacity + 1, 100);
      assertEquals(result.getEarliestSequenceNumber(), bufferCapacity);
      assertEquals(result.getNextSequenceNumber(), bufferCapacity + 1);
      assertNotNull(result.getTargetedNotifications());
      assertEquals(result.getTargetedNotifications().length, 1);
   }

   public void testPurgeNotifications() throws Exception
   {
      int distance = 2;
      Map environment = new HashMap();
      environment.put(MX4JRemoteConstants.NOTIFICATION_PURGE_DISTANCE, new Integer(distance));
      RemoteNotificationServerHandler handler = new DefaultRemoteNotificationServerHandler(environment);

      // Fetch
      NotificationResult result = handler.fetchNotifications(-1, 10, 100);

      // Add notifications
      NotificationListener listener = handler.getServerNotificationListener();
      Notification notification = new Notification("dummy", this, 0);
      Integer listenerID = new Integer(1);
      int count = distance + 1;
      for (int i = 0; i < count; ++i) listener.handleNotification(notification, listenerID);

      // Fetch again
      result = handler.fetchNotifications(result.getNextSequenceNumber(), count + 1, 100);
      assertEquals(result.getEarliestSequenceNumber(), 0);
      assertEquals(result.getNextSequenceNumber(), count);
      assertNotNull(result.getTargetedNotifications());
      assertEquals(result.getTargetedNotifications().length, count);

      // Fetch again: this call triggers purge of old notifications, which changes the earliest sequence number
      result = handler.fetchNotifications(result.getNextSequenceNumber(), count + 1, 100);

      // Check that the earliest sequence number has changed
      long oldEarliest = result.getEarliestSequenceNumber();
      result = handler.fetchNotifications(result.getNextSequenceNumber(), count + 1, 100);
      if (oldEarliest >= result.getEarliestSequenceNumber()) fail();
   }

   public void testPurgeNotificationsWithInitialNotificationMiss() throws Exception
   {
      int distance = 2;
      Map environment = new HashMap();
      environment.put(MX4JRemoteConstants.NOTIFICATION_PURGE_DISTANCE, new Integer(distance));
      RemoteNotificationServerHandler handler = new DefaultRemoteNotificationServerHandler(environment);

      // Add notifications (will be missed)
      NotificationListener listener = handler.getServerNotificationListener();
      Notification notification = new Notification("dummy", this, 0);
      Integer listenerID = new Integer(1);
      int count = distance + 1;
      for (int i = 0; i < count; ++i) listener.handleNotification(notification, listenerID);

      // First fetch
      NotificationResult result = handler.fetchNotifications(-1, count + 1, 100);

      // Add notifications
      for (int i = 0; i < count; ++i) listener.handleNotification(notification, listenerID);

      // Fetch again: this call triggers purge of old notifications, which changes the earliest sequence number
      result = handler.fetchNotifications(result.getNextSequenceNumber(), count + 1, 100);
      assertEquals(result.getEarliestSequenceNumber(), 0);
      assertEquals(result.getNextSequenceNumber(), count + count);
      assertNotNull(result.getTargetedNotifications());
      assertEquals(result.getTargetedNotifications().length, count);

      // Check that the earliest sequence number has changed
      long oldEarliest = result.getEarliestSequenceNumber();
      result = handler.fetchNotifications(result.getNextSequenceNumber(), count + 1, 100);
      if (oldEarliest >= result.getEarliestSequenceNumber()) fail();
   }

   public void testBufferOverflowWhileFetching() throws Exception
   {
      int bufferCapacity = 5;
      Map environment = new HashMap();
      environment.put(MX4JRemoteConstants.NOTIFICATION_BUFFER_CAPACITY, new Integer(bufferCapacity));
      RemoteNotificationServerHandler handler = new DefaultRemoteNotificationServerHandler(environment);

      // First Fetch
      NotificationResult result = handler.fetchNotifications(-1, bufferCapacity + 1, 100);

      // Add some notifications, but don't fill the buffer
      long count = bufferCapacity - 2;
      NotificationListener listener = handler.getServerNotificationListener();
      Notification notification = new Notification("dummy", this, 0);
      Integer listenerID = new Integer(1);
      for (int i = 0; i < count; ++i) listener.handleNotification(notification, listenerID);

      // Fetch again
      result = handler.fetchNotifications(result.getNextSequenceNumber(), bufferCapacity + 1, 100);
      assertEquals(result.getEarliestSequenceNumber(), 0);
      assertEquals(result.getNextSequenceNumber(), count);
      assertNotNull(result.getTargetedNotifications());
      assertEquals(result.getTargetedNotifications().length, count);

      // Add some notification overflowing the buffer by a number that will exceed the
      // next sequence number sent by the client.
      // The client will fetch with its own sequence number, but the server
      // has discarded that number already (it overflew), be sure the system it does
      // not screw up (IndexOutOfBoundsException)
      int overflow = 7;
      // Note that count == result.getNextSequenceNumber() always yield true at this point (see assertion above),
      // so we actually overflew the buffer by a quantity q == count + overflow (see assertion below).
      long overflowCount = (bufferCapacity - count) + result.getNextSequenceNumber() + overflow;
      for (int i = 0; i < overflowCount; ++i) listener.handleNotification(notification, listenerID);
      result = handler.fetchNotifications(result.getNextSequenceNumber(), bufferCapacity + 1, 100);

      // After algebraic semplification, overflowCount == bufferCapacity + overflow, so
      // the buffer is full, and we fetched all notifications
      assertEquals(count + overflow, result.getEarliestSequenceNumber());
      assertEquals(count + overflow + bufferCapacity, result.getNextSequenceNumber());
      assertNotNull(result.getTargetedNotifications());
      assertEquals(result.getTargetedNotifications().length, bufferCapacity);
   }
}
