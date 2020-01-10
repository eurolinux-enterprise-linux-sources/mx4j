/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerNotification;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilter;
import javax.management.NotificationFilterSupport;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.loading.MLet;
import javax.management.relation.MBeanServerNotificationFilter;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;

import mx4j.remote.MX4JRemoteConstants;
import test.MX4JTestCase;
import test.MutableBoolean;
import test.MutableInteger;
import test.MutableObject;

/**
 * @version $Revision: 1.10 $
 */
public abstract class JMXNotificationsTestCase extends MX4JTestCase
{
   public JMXNotificationsTestCase(String name)
   {
      super(name);
   }

   protected void tearDown() throws Exception
   {
      sleep(5000);
   }

   public abstract JMXServiceURL createJMXConnectorServerAddress() throws MalformedURLException;

   public abstract Map getEnvironment();

   public void testConnectionNotificationOpenedOnServer() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      try
      {
         JMXServiceURL url = createJMXConnectorServerAddress();
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());
         cntorServer.start();
         sleep(5000);

         final MutableObject holder = new MutableObject(null);
         cntorServer.addNotificationListener(new NotificationListener()
         {
            public void handleNotification(Notification notification, Object handback)
            {
               holder.set(notification);
            }
         }, null, null);

         JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());

         Notification notification = (Notification) holder.get();
         if (!(notification instanceof JMXConnectionNotification)) fail();
         assertEquals(notification.getType(), JMXConnectionNotification.OPENED);
      } catch (Exception x)
      {
         x.printStackTrace();
         throw x;
      } finally
      {
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testConnectionNotificationClosedOnServer() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      try
      {
         JMXServiceURL url = createJMXConnectorServerAddress();
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());
         cntorServer.start();
         sleep(5000);

         final MutableObject holder = new MutableObject(null);
         cntorServer.addNotificationListener(new NotificationListener()
         {
            public void handleNotification(Notification notification, Object handback)
            {
               holder.set(notification);
            }
         }, null, null);

         JMXConnector cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());
         cntor.close();

         Notification notification = (Notification) holder.get();
         if (!(notification instanceof JMXConnectionNotification)) fail();
         assertEquals(notification.getType(), JMXConnectionNotification.CLOSED);
      } catch (Exception x)
      {
         x.printStackTrace();
         throw x;
      } finally
      {
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testConnectionNotificationOpenedOnClient() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         JMXServiceURL url = createJMXConnectorServerAddress();
         MBeanServer server = newMBeanServer();
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), server);
         cntorServer.start();
         sleep(5000);

         final MutableObject holder = new MutableObject(null);
         cntor = JMXConnectorFactory.newJMXConnector(cntorServer.getAddress(), getEnvironment());
         cntor.addConnectionNotificationListener(new NotificationListener()
         {
            public void handleNotification(Notification notification, Object handback)
            {
               holder.set(notification);
            }
         }, null, null);

         cntor.connect(getEnvironment());

         JMXConnectionNotification notification = (JMXConnectionNotification) holder.get();
         assertEquals(notification.getType(), JMXConnectionNotification.OPENED);
      } catch (Exception x)
      {
         x.printStackTrace();
         throw x;
      } finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testConnectionNotificationClosedOnClient() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         JMXServiceURL url = createJMXConnectorServerAddress();
         MBeanServer server = newMBeanServer();
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), server);
         cntorServer.start();
         sleep(5000);

         final MutableObject holder = new MutableObject(null);
         cntor = JMXConnectorFactory.newJMXConnector(cntorServer.getAddress(), getEnvironment());
         cntor.addConnectionNotificationListener(new NotificationListener()
         {
            public void handleNotification(Notification notification, Object handback)
            {
               holder.set(notification);
            }
         }, null, null);

         cntor.connect(getEnvironment());
         cntor.close();

         JMXConnectionNotification notification = (JMXConnectionNotification) holder.get();
         assertEquals(notification.getType(), JMXConnectionNotification.CLOSED);
      } catch (Exception x)
      {
         x.printStackTrace();
         throw x;
      } finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testConnectionNotificationFailedOnClient() throws Exception
   {
      JMXServiceURL url = createJMXConnectorServerAddress();
      MBeanServer server = newMBeanServer();
      JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), server);

      cntorServer.start();
      sleep(5000);

      final MutableObject holder = new MutableObject(null);
      long period = 1000;
      int retries = 3;
      try
      {
         JMXConnector cntor = JMXConnectorFactory.newJMXConnector(cntorServer.getAddress(), getEnvironment());
         cntor.addConnectionNotificationListener(new NotificationListener()
         {
            public void handleNotification(Notification notification, Object handback)
            {
               holder.set(notification);
            }
         }, null, null);

         Map clientEnv = getEnvironment();
         clientEnv.put(MX4JRemoteConstants.CONNECTION_HEARTBEAT_PERIOD, new Long(period));
         clientEnv.put(MX4JRemoteConstants.CONNECTION_HEARTBEAT_RETRIES, new Integer(retries));
         cntor.connect(clientEnv);

         JMXConnectionNotification notification = (JMXConnectionNotification) holder.get();
         assertEquals(notification.getType(), JMXConnectionNotification.OPENED);
         holder.set(null);
      } catch (Exception x)
      {
         x.printStackTrace();
         throw x;
      } finally
      {
         cntorServer.stop();
         sleep(5000);
      }

      // Wait for the heartbeat to send the failed notification
      sleep((retries * 3) * period);

      JMXConnectionNotification notification = (JMXConnectionNotification) holder.get();
      assertNotNull(notification);
      assertEquals(notification.getType(), JMXConnectionNotification.FAILED);
   }

   public void testRemoteNotificationListener() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         MBeanServer server = newMBeanServer();

         JMXServiceURL url = createJMXConnectorServerAddress();
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), server);
         cntorServer.start();
         sleep(5000);

         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());
         MBeanServerConnection mbsc = cntor.getMBeanServerConnection();
         ObjectName delegate = new ObjectName("JMImplementation:type=MBeanServerDelegate");

         final MutableObject holder = new MutableObject(null);
         mbsc.addNotificationListener(delegate, new NotificationListener()
         {
            public void handleNotification(Notification notification, Object handback)
            {
               holder.set(notification);
            }
         }, null, null);

         // Wait for notifications threads to start
         sleep(1000);

         // Register a new MBean, it will generate a notification
         MLet mlet = new MLet();
         ObjectName name = new ObjectName(":mbean=mlet");
         server.registerMBean(mlet, name);

         // Wait for notifications to arrive
         sleep(1000);

         Notification notification = (Notification) holder.get();
         assertEquals(notification.getType(), MBeanServerNotification.REGISTRATION_NOTIFICATION);
         holder.set(null);

         // Unregister the MBean
         server.unregisterMBean(name);

         // Wait for notifications to arrive
         sleep(1000);

         notification = (Notification) holder.get();
         assertEquals(notification.getType(), MBeanServerNotification.UNREGISTRATION_NOTIFICATION);
         holder.set(null);
      } catch (Exception x)
      {
         x.printStackTrace();
         throw x;
      } finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testNonSerializableNotifications() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         MBeanServer server = newMBeanServer();

         // Register an MBean Emitter
         ObjectName emitterName = ObjectName.getInstance(":mbean=emitter");
         MBeanEmitter emitter = new MBeanEmitter();
         server.registerMBean(emitter, emitterName);

         JMXServiceURL url = createJMXConnectorServerAddress();
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), null);
         ObjectName cntorName = ObjectName.getInstance("connector:protocol=rmi");
         server.registerMBean(cntorServer, cntorName);
         cntorServer.start();
         sleep(5000);

         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());
         MBeanServerConnection mbsc = cntor.getMBeanServerConnection();
         final MutableObject plainNotification = new MutableObject(null);
         mbsc.addNotificationListener(emitterName, new NotificationListener()
         {
            public void handleNotification(Notification notification, Object handback)
            {
               plainNotification.set(notification);
            }
         }, null, null);

         final MutableObject connectionNotification = new MutableObject(null);
         cntor.addConnectionNotificationListener(new NotificationListener()
         {
            public void handleNotification(Notification notification, Object handback)
            {
               connectionNotification.set(notification);
            }
         }, null, null);

         // Wait for notifications threads to start
         sleep(1000);

         String type = "notification.type";
         Notification notification = new Notification(type, this, 0);
         // Make it non-serializable
         notification.setUserData(this);
         emitter.emit(notification);

         // Wait for notifications to arrive
         sleep(1000);

         assertNull(plainNotification.get());
         assertNotNull(connectionNotification.get());
         assertEquals(((Notification) connectionNotification.get()).getType(), JMXConnectionNotification.NOTIFS_LOST);
      } catch (Exception x)
      {
         x.printStackTrace();
         throw x;
      } finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testAddRemoveMBeanListener() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         MBeanServer server = newMBeanServer();

         // Register an MBean Emitter
         ObjectName emitterName = ObjectName.getInstance(":mbean=emitter");
         MBeanEmitter emitter = new MBeanEmitter();
         server.registerMBean(emitter, emitterName);

         // Register an MBean Listener
         MutableObject notificationHolder = new MutableObject(null);
         MutableObject handbackHolder = new MutableObject(null);
         ObjectName listenerName = ObjectName.getInstance(":mbean=listener");
         MBeanListener listener = new MBeanListener(notificationHolder, handbackHolder);
         server.registerMBean(listener, listenerName);

         JMXServiceURL url = createJMXConnectorServerAddress();
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), server);
         cntorServer.start();
         sleep(5000);

         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());
         MBeanServerConnection mbsc = cntor.getMBeanServerConnection();

         // Non-serializable filter
         try
         {
            mbsc.addNotificationListener(emitterName, listenerName, new NotificationFilter()
            {
               public boolean isNotificationEnabled(Notification notification)
               {
                  return false;
               }
            }, null);
            fail();
         } catch (IOException x)
         {
         }

         // Non-serializable handback
         try
         {
            mbsc.addNotificationListener(emitterName, listenerName, null, new Object());
            fail();
         } catch (IOException x)
         {
         }

         // Non-serializable filter and non serializable handback
         try
         {
            mbsc.addNotificationListener(emitterName, listenerName, new NotificationFilter()
            {
               public boolean isNotificationEnabled(Notification notification)
               {
                  return false;
               }
            }, new Object());
            fail();
         } catch (IOException x)
         {
         }

         // Everything is serializable
         ObjectName name = ObjectName.getInstance(":mbean=dummy");
         MBeanServerNotificationFilter filter = new MBeanServerNotificationFilter();
         filter.disableObjectName(name);
         Object handback = new Integer(13);
         mbsc.addNotificationListener(emitterName, listenerName, filter, handback);

         // Wait for notifications threads to start
         sleep(1000);

         Notification notification = new MBeanServerNotification(MBeanServerNotification.REGISTRATION_NOTIFICATION, this, 0, name);
         emitter.emit(notification);

         // Wait for notification to arrive
         sleep(1000);

         // Be sure the notification has been filtered
         assertNull(notificationHolder.get());
         assertNull(handbackHolder.get());

         // Disable filtering
         filter.enableAllObjectNames();
         // Remove and readd: on server side there is a serialized copy of the filter
         mbsc.removeNotificationListener(emitterName, listenerName);
         mbsc.addNotificationListener(emitterName, listenerName, filter, handback);

         // Wait for notifications threads to start
         sleep(1000);

         emitter.emit(notification);

         // Wait for notification to arrive
         sleep(1000);

         // Be sure we got it
         assertEquals(handbackHolder.get(), handback);
         Notification emitted = (Notification) notificationHolder.get();
         assertNotNull(emitted);
         if (!(notification instanceof MBeanServerNotification)) fail();
         assertEquals(((MBeanServerNotification) emitted).getMBeanName(), name);
         notificationHolder.set(null);
         handbackHolder.set(null);

         mbsc.removeNotificationListener(emitterName, listenerName, filter, handback);

         // Be sure we don't get notifications anymore
         emitter.emit(notification);

         // Wait for notification to arrive
         sleep(1000);

         assertNull(notificationHolder.get());
         assertNull(handbackHolder.get());
      } catch (Exception x)
      {
         x.printStackTrace();
         throw x;
      } finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testAddRemoveListenerWithNonSerializableFilter() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         MBeanServer server = newMBeanServer();

         // Register an MBean Emitter
         ObjectName emitterName = ObjectName.getInstance(":mbean=emitter");
         MBeanEmitter emitter = new MBeanEmitter();
         server.registerMBean(emitter, emitterName);

         MutableObject notificationHolder = new MutableObject(null);
         MutableObject handbackHolder = new MutableObject(null);
         MBeanListener listener = new MBeanListener(notificationHolder, handbackHolder);

         JMXServiceURL url = createJMXConnectorServerAddress();
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), server);
         cntorServer.start();
         sleep(5000);

         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());
         MBeanServerConnection mbsc = cntor.getMBeanServerConnection();

         // Non-serializable filter, should run on client side
         final MutableBoolean enable = new MutableBoolean(false);
         NotificationFilter filter = new NotificationFilter()
         {
            public boolean isNotificationEnabled(Notification notification)
            {
               return enable.get();
            }
         };
         mbsc.addNotificationListener(emitterName, listener, filter, null);

         // Wait for notification threads to start
         sleep(1000);

         String type = "notification.type";
         Notification notification = new Notification(type, this, 0);
         emitter.emit(notification);

         // Wait for notification to arrive
         sleep(1000);

         // Be sure the notification has been filtered
         assertNull(notificationHolder.get());
         assertNull(handbackHolder.get());

         // Disable the filter
         enable.set(true);

         emitter.emit(notification);

         // Wait for notification to arrive
         sleep(1000);

         // Be sure we got the notification
         assertNull(handbackHolder.get());
         Notification emitted = (Notification) notificationHolder.get();
         assertNotNull(emitted);
         notificationHolder.set(null);
         handbackHolder.set(null);

         mbsc.removeNotificationListener(emitterName, listener, filter, null);

         // Be sure we don't get notifications anymore
         emitter.emit(notification);

         // Wait for notification to arrive
         sleep(1000);

         assertNull(notificationHolder.get());
         assertNull(handbackHolder.get());
      } catch (Exception x)
      {
         x.printStackTrace();
         throw x;
      } finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testAddRemoveListenerWithNonSerializableHandback() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         MBeanServer server = newMBeanServer();

         // Register an MBean Emitter
         ObjectName emitterName = ObjectName.getInstance(":mbean=emitter");
         MBeanEmitter emitter = new MBeanEmitter();
         server.registerMBean(emitter, emitterName);

         MutableObject notificationHolder = new MutableObject(null);
         MutableObject handbackHolder = new MutableObject(null);
         MBeanListener listener = new MBeanListener(notificationHolder, handbackHolder);

         JMXServiceURL url = createJMXConnectorServerAddress();
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), server);
         cntorServer.start();
         sleep(5000);

         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());
         MBeanServerConnection mbsc = cntor.getMBeanServerConnection();

         // Non-serializable handback, should stay on client side
         Object handback = new Object();
         mbsc.addNotificationListener(emitterName, listener, null, handback);

         // Wait for notification threads to start
         sleep(1000);

         String type = "notification.type";
         Notification notification = new Notification(type, this, 0);
         emitter.emit(notification);

         // Wait for notification to arrive
         sleep(1000);

         // Be sure we got the notification
         assertSame(handbackHolder.get(), handback);
         Notification emitted = (Notification) notificationHolder.get();
         assertNotNull(emitted);
         notificationHolder.set(null);
         handbackHolder.set(null);

         mbsc.removeNotificationListener(emitterName, listener, null, handback);

         // Be sure we don't get notifications anymore
         emitter.emit(notification);

         // Wait for notification to arrive
         sleep(1000);

         assertNull(notificationHolder.get());
         assertNull(handbackHolder.get());
      } catch (Exception x)
      {
         x.printStackTrace();
         throw x;
      } finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testAddRemoveSameListenerMultipleTimesWithDifferentFiltersAndHandbacks() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         MBeanServer server = newMBeanServer();

         // Register an MBean Emitter
         ObjectName emitterName = ObjectName.getInstance(":mbean=emitter");
         MBeanEmitter emitter = new MBeanEmitter();
         server.registerMBean(emitter, emitterName);

         final MutableInteger counter1 = new MutableInteger(0);
         NotificationListener listener1 = new NotificationListener()
         {
            public void handleNotification(Notification notification, Object handback)
            {
               counter1.set(counter1.get() + 1);
            }
         };
         final MutableInteger counter2 = new MutableInteger(0);
         NotificationListener listener2 = new NotificationListener()
         {
            public void handleNotification(Notification notification, Object handback)
            {
               counter2.set(counter2.get() + 1);
            }
         };

         JMXServiceURL url = createJMXConnectorServerAddress();
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), server);
         cntorServer.start();
         sleep(5000);

         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());
         MBeanServerConnection mbsc = cntor.getMBeanServerConnection();

         String type = "notification.type";

         // First listener
         mbsc.addNotificationListener(emitterName, listener1, null, null);
         // Second listener
         NotificationFilterSupport filter = new NotificationFilterSupport();
         filter.enableType(type);
         mbsc.addNotificationListener(emitterName, listener1, filter, null);
         // Third listener
         Object handback = new Object();
         mbsc.addNotificationListener(emitterName, listener1, null, handback);
         // Fourth listener
         mbsc.addNotificationListener(emitterName, listener2, null, null);

         // Wait for notification threads to start
         sleep(1000);

         Notification notification = new Notification(type, this, 0);
         emitter.emit(notification);
         // Wait for notification to arrive
         sleep(1000);

         // Be sure we got all notifications
         assertEquals(counter1.get(), 3);
         assertEquals(counter2.get(), 1);
         counter1.set(0);
         counter2.set(0);

         // Remove one listener
         mbsc.removeNotificationListener(emitterName, listener1, null, handback);

         emitter.emit(notification);
         // Wait for notification to arrive
         sleep(1000);

         assertEquals(counter1.get(), 2);
         assertEquals(counter2.get(), 1);
         counter1.set(0);
         counter2.set(0);

         // Remove all listeners
         mbsc.removeNotificationListener(emitterName, listener1);

         emitter.emit(notification);
         // Wait for notification to arrive
         sleep(1000);

         assertEquals(counter1.get(), 0);
         assertEquals(counter2.get(), 1);
         counter1.set(0);
         counter2.set(0);

         mbsc.removeNotificationListener(emitterName, listener2);

         emitter.emit(notification);
         // Wait for notification to arrive
         sleep(1000);

         assertEquals(counter1.get(), 0);
         assertEquals(counter2.get(), 0);
      } catch (Exception x)
      {
         x.printStackTrace();
         throw x;
      } finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testTwoMBeanServerConnectionsHaveSameListener() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         MBeanServer server = newMBeanServer();

         // Register an MBean Emitter
         ObjectName emitterName = ObjectName.getInstance(":mbean=emitter");
         MBeanEmitter emitter = new MBeanEmitter();
         server.registerMBean(emitter, emitterName);

         final MutableInteger counter = new MutableInteger(0);
         NotificationListener listener = new NotificationListener()
         {
            public void handleNotification(Notification notification, Object handback)
            {
               counter.set(counter.get() + 1);
            }
         };

         JMXServiceURL url = createJMXConnectorServerAddress();
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), server);
         cntorServer.start();
         sleep(5000);

         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());
         MBeanServerConnection mbsc1 = cntor.getMBeanServerConnection();

         mbsc1.addNotificationListener(emitterName, listener, null, null);
         // Wait for notification threads to start
         sleep(1000);

         Notification notification = new Notification("type", emitter, 0);
         emitter.emit(notification);

         // Wait for the notification to arrive
         sleep(1000);

         // Be sure it's received
         assertEquals(counter.get(), 1);

         // Be sure I can remove the same listener from another MBeanServerConnection
         MBeanServerConnection mbsc2 = cntor.getMBeanServerConnection();
         mbsc2.removeNotificationListener(emitterName, listener, null, null);

         emitter.emit(notification);

         // Be sure no listeners anymore
         assertEquals(counter.get(), 1);
      } catch (Exception x)
      {
         x.printStackTrace();
         throw x;
      } finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public interface MBeanListenerMBean
   {
   }

   public static class MBeanListener implements NotificationListener, MBeanListenerMBean
   {
      private MutableObject notificationHolder;
      private MutableObject handbackHolder;

      public MBeanListener(MutableObject notificationHolder, MutableObject handbackHolder)
      {
         this.notificationHolder = notificationHolder;
         this.handbackHolder = handbackHolder;
      }

      public void handleNotification(Notification notification, Object handback)
      {
         notificationHolder.set(notification);
         handbackHolder.set(handback);
      }
   }

   public interface MBeanEmitterMBean
   {
      public void emit(Notification notification);
   }

   public static class MBeanEmitter extends NotificationBroadcasterSupport implements MBeanEmitterMBean
   {
      public void emit(Notification notification)
      {
         sendNotification(notification);
      }
   }
}
