/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management;

import java.io.IOException;
import java.lang.reflect.UndeclaredThrowableException;
import javax.management.InstanceNotFoundException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerInvocationHandler;
import javax.management.Notification;
import javax.management.NotificationBroadcaster;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.naming.NamingException;

import test.MX4JTestCase;

/**
 * @version $Revision: 1.8 $
 */
public class MBeanServerInvocationHandlerTest extends MX4JTestCase
{
   public MBeanServerInvocationHandlerTest(String s)
   {
      super(s);
   }

   public void testBadArguments() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("domain:key=value");

      try
      {
         MBeanServerInvocationHandler.newProxyInstance(null, name, LocalServiceMBean.class, false);
         fail("MBeanServerConnection cannot be null");
      }
      catch (IllegalArgumentException x)
      {
      }

      try
      {
         MBeanServerInvocationHandler.newProxyInstance(server, null, LocalServiceMBean.class, false);
         fail("ObjectName cannot be null");
      }
      catch (IllegalArgumentException x)
      {
      }

      try
      {
         MBeanServerInvocationHandler.newProxyInstance(server, name, null, false);
         fail("Class cannot be null");
      }
      catch (IllegalArgumentException x)
      {
      }

      try
      {
         MBeanServerInvocationHandler.newProxyInstance(server, name, LocalService.class, false);
         fail("Class must be an interface");
      }
      catch (IllegalArgumentException x)
      {
      }
   }

   public void testMBeanNotAnEmitter() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("domain:key=value");
      LocalService mbean = new LocalService();
      server.registerMBean(mbean, name);

      NotificationEmitter emitter = (NotificationEmitter)MBeanServerInvocationHandler.newProxyInstance(server, name, LocalServiceMBean.class, true);

      try
      {
         emitter.addNotificationListener(new TestListener(), null, null);
         fail("The MBean is not a NotificationEmitter");
      }
      catch (IllegalArgumentException x)
      {
      }
   }

   public void testDeregisteredMBean() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("domain:key=value");
      LocalService mbean = new LocalService();
      server.registerMBean(mbean, name);

      // Check what the proxy throws if the ObjectName is removed from the server
      LocalServiceMBean proxy = (LocalServiceMBean)MBeanServerInvocationHandler.newProxyInstance(server, name, LocalServiceMBean.class, false);
      server.unregisterMBean(name);
      try
      {
         proxy.throwCheckedException();
         fail();
      }
      catch (NamingException x)
      {
         fail("Expecting an InstanceNotFoundException");
      }
      catch (UndeclaredThrowableException x)
      {
         Throwable xx = x.getUndeclaredThrowable();
         if (!(xx instanceof InstanceNotFoundException))
            fail("Expecting an InstanceNotFoundException");
      }
   }

   public void testCheckedException() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("domain:key=value");
      LocalService mbean = new LocalService();
      server.registerMBean(mbean, name);

      LocalServiceMBean proxy = (LocalServiceMBean)MBeanServerInvocationHandler.newProxyInstance(server, name, LocalServiceMBean.class, false);
      try
      {
         proxy.throwCheckedException();
         fail();
      }
      catch (NamingException x)
      {
      }
   }

   public void testMBeanException() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("domain:key=value");
      LocalService mbean = new LocalService();
      server.registerMBean(mbean, name);

      LocalServiceMBean proxy = (LocalServiceMBean)MBeanServerInvocationHandler.newProxyInstance(server, name, LocalServiceMBean.class, false);
      try
      {
         proxy.throwMBeanException();
         fail();
      }
      catch (MBeanException x)
      {
      }
   }

   public void testRuntimeException() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("domain:key=value");
      LocalService mbean = new LocalService();
      server.registerMBean(mbean, name);

      LocalServiceMBean proxy = (LocalServiceMBean)MBeanServerInvocationHandler.newProxyInstance(server, name, LocalServiceMBean.class, false);
      try
      {
         proxy.throwNullPointerException();
         fail();
      }
      catch (NullPointerException x)
      {
      }
   }

   public void testError() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("domain:key=value");
      LocalService mbean = new LocalService();
      server.registerMBean(mbean, name);

      LocalServiceMBean proxy = (LocalServiceMBean)MBeanServerInvocationHandler.newProxyInstance(server, name, LocalServiceMBean.class, false);
      try
      {
         proxy.throwError();
         fail();
      }
      catch (Error x)
      {
      }
   }

   public void testNotificationBroadcasterProxy() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("domain:key=value");
      LocalBroadcasterService mbean = new LocalBroadcasterService();
      server.registerMBean(mbean, name);

      // The returned interface should be NotificationEmitter, even though the MBean only implements NotificationBroadcaster
      NotificationEmitter proxy = (NotificationEmitter)MBeanServerInvocationHandler.newProxyInstance(server, name, NotificationBroadcaster.class, true);
      assertNotNull(proxy);
   }

   public void testAddNotificationListener() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("domain:key=value");
      LocalEmitterService mbean = new LocalEmitterService();
      server.registerMBean(mbean, name);

      NotificationEmitter proxy = (NotificationEmitter)MBeanServerInvocationHandler.newProxyInstance(server, name, NotificationEmitter.class, true);
      TestListener listener = new TestListener();
      proxy.addNotificationListener(listener, null, null);
      mbean.test();
      if (!listener.received) fail();
   }

   public void testGetNotificationInfo() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("domain:key=value");
      LocalEmitterService mbean = new LocalEmitterService();
      server.registerMBean(mbean, name);

      NotificationEmitter proxy = (NotificationEmitter)MBeanServerInvocationHandler.newProxyInstance(server, name, NotificationEmitter.class, true);
      MBeanNotificationInfo[] infos = proxy.getNotificationInfo();
      if (!infos[0].getDescription().equals(LocalEmitterService.DESC)) fail();
   }

   public void testSimpleRemoveNotificationListener() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("domain:key=value");
      LocalEmitterService mbean = new LocalEmitterService();
      server.registerMBean(mbean, name);

      NotificationEmitter proxy = (NotificationEmitter)MBeanServerInvocationHandler.newProxyInstance(server, name, NotificationEmitter.class, true);
      TestListener listener = new TestListener();
      proxy.addNotificationListener(listener, null, null);
      proxy.removeNotificationListener(listener);
      mbean.test();
      if (listener.received) fail();
   }

   public void testRemoveNotificationListener() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("domain:key=value");
      LocalEmitterService mbean = new LocalEmitterService();
      server.registerMBean(mbean, name);

      NotificationEmitter proxy = (NotificationEmitter)MBeanServerInvocationHandler.newProxyInstance(server, name, NotificationEmitter.class, true);
      TestListener listener = new TestListener();
      TestFilter filter = new TestFilter();
      Object handback = new Object();
      proxy.addNotificationListener(listener, filter, handback);
      proxy.removeNotificationListener(listener, filter, handback);
      mbean.test();
      if (listener.received) fail();
   }

   public void testRemoveMultiNotificationListener() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("domain:key=value");
      LocalEmitterService mbean = new LocalEmitterService();
      server.registerMBean(mbean, name);

      NotificationEmitter proxy = (NotificationEmitter)MBeanServerInvocationHandler.newProxyInstance(server, name, NotificationEmitter.class, true);
      TestListener listener1 = new TestListener();
      TestFilter filter = new TestFilter();
      Object handback = new Object();
      proxy.addNotificationListener(listener1, filter, handback);

      TestListener listener2 = new TestListener();
      proxy.addNotificationListener(listener2, null, null);

      mbean.test();
      if (!listener1.received) fail();
      if (!listener2.received) fail();

      try
      {
         proxy.removeNotificationListener(listener2, filter, handback);
         fail("Listener is not registered");
      }
      catch (ListenerNotFoundException x)
      {
      }

      proxy.removeNotificationListener(listener2, null, null);
      listener1.received = false;
      listener2.received = false;

      mbean.test();
      if (!listener1.received) fail();
      if (listener2.received) fail();
   }

   public void testRemoteExceptionWithRemoteInterface() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("domain:key=value");
      LocalService mbean = new LocalService();
      server.registerMBean(mbean, name);

      JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://localhost");
      JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
      cntorServer.start();
      JMXConnector cntor = JMXConnectorFactory.connect(cntorServer.getAddress());
      MBeanServerConnection mbsc = cntor.getMBeanServerConnection();

      RemoteService remoteMBean = (RemoteService)MBeanServerInvocationHandler.newProxyInstance(mbsc, name, RemoteService.class, false);

      // Close everything to get IOException
      cntor.close();
      cntorServer.stop();

      try
      {
         remoteMBean.throwCheckedException();
         fail("Must not be able to connect");
      }
      catch (IOException x)
      {
      }
   }

   public void testRemoteExceptionWithLocalInterface() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("domain:key=value");
      LocalService mbean = new LocalService();
      server.registerMBean(mbean, name);

      JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://localhost");
      JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
      cntorServer.start();
      JMXConnector cntor = JMXConnectorFactory.connect(cntorServer.getAddress());
      MBeanServerConnection mbsc = cntor.getMBeanServerConnection();

      LocalServiceMBean remoteMBean = (LocalServiceMBean)MBeanServerInvocationHandler.newProxyInstance(mbsc, name, LocalServiceMBean.class, false);

      // Close everything to get IOException
      cntor.close();
      cntorServer.stop();

      // Now try the local interface
      try
      {
         remoteMBean.throwCheckedException();
         fail("Must not be able to connect");
      }
      catch (UndeclaredThrowableException x)
      {
         Throwable xx = x.getUndeclaredThrowable();
         if (!(xx instanceof IOException)) fail();
      }
   }

   public void testRemoteCheckedException() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName("domain:key=value");
      LocalService mbean = new LocalService();
      server.registerMBean(mbean, name);

      JMXServiceURL url = new JMXServiceURL("service:jmx:rmi://localhost");
      JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, null, server);
      cntorServer.start();
      JMXConnector cntor = JMXConnectorFactory.connect(cntorServer.getAddress());
      MBeanServerConnection mbsc = cntor.getMBeanServerConnection();

      LocalServiceMBean remoteMBean = (LocalServiceMBean)MBeanServerInvocationHandler.newProxyInstance(mbsc, name, LocalServiceMBean.class, false);
      try
      {
         remoteMBean.throwCheckedException();
         fail();
      }
      catch (NamingException x)
      {
      }
   }

   public interface LocalServiceMBean
   {
      public void throwCheckedException() throws NamingException;

      public void throwMBeanException() throws MBeanException;

      public void throwNullPointerException();

      public void throwError();
   }

   public interface RemoteService
   {
      public void throwCheckedException() throws NamingException, IOException;

      public void throwMBeanException() throws MBeanException, IOException;

      public void throwNullPointerException() throws IOException;

      public void throwError() throws IOException;
   }

   public class LocalService implements LocalServiceMBean
   {
      public void throwCheckedException() throws NamingException
      {
         throw new NamingException();
      }

      public void throwMBeanException() throws MBeanException
      {
         throw new MBeanException(new Exception());
      }

      public void throwNullPointerException()
      {
         throw new NullPointerException();
      }

      public void throwError()
      {
         throw new Error();
      }
   }

   public class LocalBroadcasterService extends LocalService implements NotificationBroadcaster
   {
      private NotificationBroadcasterSupport support = new NotificationBroadcasterSupport();
      static final String TYPE = "test.notification";
      static final String DESC = "Test Notification";
      private long sequence;

      /**
       * @see javax.management.NotificationBroadcaster#addNotificationListener(NotificationListener, NotificationFilter, Object)
       */
      public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
              throws IllegalArgumentException
      {
         support.addNotificationListener(listener, filter, handback);
      }

      /**
       * @see javax.management.NotificationBroadcaster#getNotificationInfo()
       */
      public MBeanNotificationInfo[] getNotificationInfo()
      {
         return new MBeanNotificationInfo[]{
            new MBeanNotificationInfo(new String[]{TYPE}, Notification.class.getName(), DESC),
         };
      }

      /**
       * @see javax.management.NotificationBroadcaster#removeNotificationListener(NotificationListener)
       */
      public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException
      {
         support.removeNotificationListener(listener);
      }

      public void test()
      {
         Notification notification = new Notification(TYPE, this, ++sequence, System.currentTimeMillis(), DESC);
         support.sendNotification(notification);
      }
   }

   public class LocalEmitterService extends LocalService implements NotificationEmitter
   {
      private NotificationBroadcasterSupport support = new NotificationBroadcasterSupport();
      static final String TYPE = "test.notification";
      static final String DESC = "Test Notification";
      private long sequence;

      public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback)
              throws IllegalArgumentException
      {
         support.addNotificationListener(listener, filter, handback);
      }

      public MBeanNotificationInfo[] getNotificationInfo()
      {
         return new MBeanNotificationInfo[]{
            new MBeanNotificationInfo(new String[]{TYPE}, Notification.class.getName(), DESC),
         };
      }

      public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException
      {
         support.removeNotificationListener(listener);
      }

      public void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws ListenerNotFoundException
      {
         support.removeNotificationListener(listener, filter, handback);
      }

      public void test()
      {
         Notification notification = new Notification(TYPE, this, ++sequence, System.currentTimeMillis(), DESC);
         support.sendNotification(notification);
      }
   }

   public class TestListener implements NotificationListener
   {
      boolean received = false;

      public void handleNotification(Notification notification, Object handback)
      {
         received = true;
      }
   }

   public class TestFilter implements NotificationFilter
   {
      public boolean isNotificationEnabled(Notification notification)
      {
         return notification.getType().equals(LocalEmitterService.TYPE);
      }
   }
}
