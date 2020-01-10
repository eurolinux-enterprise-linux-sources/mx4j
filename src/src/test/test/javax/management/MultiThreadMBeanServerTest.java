/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerDelegate;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.loading.MLet;
import javax.management.timer.Timer;

import test.MX4JTestCase;
import test.MultiThreadTestRunner;

/**
 * @version $Revision: 1.4 $
 */
public class MultiThreadMBeanServerTest extends MX4JTestCase
{
   protected MBeanServerConnection server;

   public MultiThreadMBeanServerTest(String s)
   {
      super(s);
   }

   protected void setUp() throws Exception
   {
      server = newMBeanServer();
   }

   protected void tearDown() throws Exception
   {
      server = null;
   }

   public void testAddRemoveNotifyListeners() throws Exception
   {
      final ObjectName delegateName = ObjectName.getInstance("JMImplementation:type=MBeanServerDelegate");

      MultiThreadTestRunner.Test test = new MultiThreadTestRunner.Test()
      {
         public void test() throws Exception
         {
            final List notifications = new ArrayList();
            NotificationListener listener = new NotificationListener()
            {
               public void handleNotification(Notification notification, Object handback)
               {
                  synchronized (notifications)
                  {
                     notifications.add(notification);
                  }
               }
            };

            // Add the listener
            server.addNotificationListener(delegateName, listener, null, null);

            // Emit a notification
            ObjectName mletName = ObjectName.getInstance(":name=" + new Object().hashCode());
            server.createMBean(MLet.class.getName(), mletName, null);

            // Emit another notification
            server.unregisterMBean(mletName);

            // Remove the listener
            server.removeNotificationListener(delegateName, listener, null, null);
         }
      };

      MultiThreadTestRunner runner = new MultiThreadTestRunner(50, 10);
      runner.run(test);
   }

   public void testRegisterUnregisterQueryMBeans() throws Exception
   {
      MultiThreadTestRunner.Test test = new MultiThreadTestRunner.Test()
      {
         public void test() throws Exception
         {
            Set names = server.queryNames(null, null);

            for (Iterator i = names.iterator(); i.hasNext();)
            {
               ObjectName name = (ObjectName)i.next();
               try
               {
                  if (server.isInstanceOf(name, MBeanServerDelegate.class.getName()))
                  {
                     server.getAttribute(name, "ImplementationVendor");
                  }
               }
               catch (InstanceNotFoundException ignored)
               {
                  // The Timer may be unregistered by another thread
               }
            }

            ObjectName timerName = ObjectName.getInstance(":timer=" + new Object().hashCode());
            server.createMBean(Timer.class.getName(), timerName, null);

            Set mbeans = server.queryMBeans(new ObjectName("JMImplementation:*"), null);

            for (Iterator i = mbeans.iterator(); i.hasNext();)
            {
               ObjectInstance instance = (ObjectInstance)i.next();
               try
               {
                  if (server.isInstanceOf(instance.getObjectName(), MBeanServerDelegate.class.getName()))
                  {
                     server.getAttribute(instance.getObjectName(), "ImplementationVendor");
                  }
               }
               catch (InstanceNotFoundException ignored)
               {
                  // The Timer may be unregistered by another thread
               }
            }

            server.unregisterMBean(timerName);
         }
      };

      MultiThreadTestRunner runner = new MultiThreadTestRunner(50, 10);
      runner.run(test);
   }
}
