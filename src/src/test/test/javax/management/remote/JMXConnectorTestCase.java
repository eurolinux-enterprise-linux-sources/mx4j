/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.Notification;
import javax.management.NotificationEmitter;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectName;
import javax.management.remote.JMXAuthenticator;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXPrincipal;
import javax.management.remote.JMXServiceURL;
import javax.security.auth.Subject;

import test.MX4JTestCase;

/**
 * @version $Revision: 1.21 $
 */
public abstract class JMXConnectorTestCase extends MX4JTestCase
{
   public JMXConnectorTestCase(String name)
   {
      super(name);
   }

   protected void tearDown() throws Exception
   {
      sleep(5000);
   }

   public abstract JMXServiceURL createJMXConnectorServerAddress() throws MalformedURLException;

   public abstract Map getEnvironment();

   public void testNewJMXConnectorWithNullURL() throws Exception
   {
      try
      {
         JMXConnectorFactory.connect(null);
         fail();
      }
      catch (NullPointerException x)
      {
      }
   }

   public void testConnectionId() throws Exception
   {
      // Format is:
      // protocol:[[host]:port] [clientId] [arbitrary]
      // Spaces are mandatory, brackets indicates optional parts

      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         JMXServiceURL url = createJMXConnectorServerAddress();
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());
         cntorServer.start();
         sleep(5000);

         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());
         String connectionId = cntor.getConnectionId();
         String protocol = connectionId.substring(0, connectionId.indexOf(':'));
         assertEquals(protocol, url.getProtocol());

         // Match first mandatory space
         int space = connectionId.indexOf(' ');
         String remaining = connectionId.substring(space + 1);
         // Match second mandatory space
         space = remaining.indexOf(' ');
         String arbitrary = remaining.substring(space + 1);
         if (arbitrary.length() < 1) fail("Missing MX4J arbitrary test");
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testConnectionWithNoPath() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         JMXServiceURL url = createJMXConnectorServerAddress();
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());
         cntorServer.start();
         sleep(5000);

         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testJMXAuthenticator() throws Exception
   {
      final String password = "mx4j";
      JMXAuthenticator authenticator = new JMXAuthenticator()
      {
         public Subject authenticate(Object credentials) throws SecurityException
         {
            if (password.equals(credentials))
            {
               JMXPrincipal principal = new JMXPrincipal("mx4j");
               Subject subject = new Subject();
               subject.getPrincipals().add(principal);
               subject.setReadOnly();
               return subject;
            }
            throw new SecurityException("Authentication Failed");
         }
      };

      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         JMXServiceURL url = createJMXConnectorServerAddress();
         MBeanServer server = newMBeanServer();
         Map serverEnv = getEnvironment();
         serverEnv.put(JMXConnectorServer.AUTHENTICATOR, authenticator);
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, serverEnv, server);
         cntorServer.start();
         sleep(5000);

         // Try to provide wrong password
         Map clientEnv = getEnvironment();
         try
         {
            testJMXAuthenticatorConnect(cntorServer.getAddress(), clientEnv);
            fail();
         }
         catch (SecurityException x)
         {
         }

         // Try now with a correct password
         clientEnv.put(JMXConnector.CREDENTIALS, password);
         testJMXAuthenticatorConnect(cntorServer.getAddress(), clientEnv);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   protected void testJMXAuthenticatorConnect(JMXServiceURL url, Map environment) throws SecurityException, IOException
   {
      JMXConnectorFactory.connect(url, environment);
   }

   public void testStopServerBeforeClosingClient() throws Exception
   {
      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());
      cntorServer.start();
      sleep(5000);

      JMXConnector cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());
      MBeanServerConnection mbsc = cntor.getMBeanServerConnection();

      cntorServer.stop();

      try
      {
         mbsc.getDefaultDomain();
         fail();
      }
      catch (IOException x)
      {
      }
   }

   public void testStopServerAndCloseClientThenInvoke() throws Exception
   {
      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());
      cntorServer.start();
      sleep(5000);

      JMXConnector cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());
      MBeanServerConnection mbsc = cntor.getMBeanServerConnection();

      cntor.close();
      cntorServer.stop();

      try
      {
         mbsc.getDefaultDomain();
         fail();
      }
      catch (IOException x)
      {
      }
   }

   public void testSerializedConnectorCanConnect() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         JMXServiceURL url = createJMXConnectorServerAddress();
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());
         cntorServer.start();
         sleep(5000);

         cntor = JMXConnectorFactory.newJMXConnector(cntorServer.getAddress(), getEnvironment());

         // Serialize it: we want to test serialization does no reset data members
         ByteArrayOutputStream baos = new ByteArrayOutputStream();
         ObjectOutputStream oos = new ObjectOutputStream(baos);
         oos.writeObject(cntor);
         oos.close();
         ByteArrayInputStream bais = new ByteArrayInputStream(baos.toByteArray());
         ObjectInputStream ois = new ObjectInputStream(bais);
         cntor = (JMXConnector)ois.readObject();
         ois.close();

         cntor.connect();
         MBeanServerConnection mbsc = cntor.getMBeanServerConnection();
         mbsc.getDefaultDomain();

         // Again
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());

         // Serialize it: we want to test serialization does no reset data members
         baos = new ByteArrayOutputStream();
         oos = new ObjectOutputStream(baos);
         oos.writeObject(cntor);
         oos.close();
         bais = new ByteArrayInputStream(baos.toByteArray());
         ois = new ObjectInputStream(bais);
         cntor = (JMXConnector)ois.readObject();
         ois.close();

         cntor.connect();
         mbsc = cntor.getMBeanServerConnection();
         mbsc.getDefaultDomain();
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testDefaultClassLoader() throws Exception
   {
      JMXServiceURL url = createJMXConnectorServerAddress();
      Map environment = new HashMap(getEnvironment());
      environment.put(JMXConnectorFactory.DEFAULT_CLASS_LOADER, new Object());
      try
      {
         JMXConnectorFactory.newJMXConnector(url, environment);
         fail();
      }
      catch (IllegalArgumentException x)
      {
      }

      JMXConnector cntor = JMXConnectorFactory.newJMXConnector(url, getEnvironment());
      try
      {
         cntor.connect(environment);
         fail();
      }
      catch (IllegalArgumentException x)
      {
      }
   }

   public void testListenersAreRemovedOnConnectorClose() throws Exception
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

         Emitter emitter = new Emitter();
         ObjectName emitterName = ObjectName.getInstance(":name=emitter");
         server.registerMBean(emitter, emitterName);

         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());
         MBeanServerConnection mbsc = cntor.getMBeanServerConnection();

         NotificationListener listener = new NotificationListener()
         {
            public void handleNotification(Notification notification, Object handback)
            {
            }
         };

         // Add the listener and be sure the mechanism of removal works fine
         mbsc.addNotificationListener(emitterName, listener, null, null);
         assertEquals(emitter.getSize(), 1);
         mbsc.removeNotificationListener(emitterName, listener, null, null);
         assertEquals(emitter.getSize(), 0);

         // Add the listener and close the connector
         mbsc.addNotificationListener(emitterName, listener, null, null);
         assertEquals(emitter.getSize(), 1);
         cntor.close();
         assertEquals(emitter.getSize(), 0);
      }
      finally
      {
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testConnectWithProviderClassLoader() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         JMXServiceURL url = createJMXConnectorServerAddress();
         MBeanServer server = newMBeanServer();
         Map serverEnv = getEnvironment();
         serverEnv.put(JMXConnectorServerFactory.PROTOCOL_PROVIDER_CLASS_LOADER, getClass().getClassLoader());
         ClassLoader old = Thread.currentThread().getContextClassLoader();
         Thread.currentThread().setContextClassLoader(getClass().getClassLoader().getParent());
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, serverEnv, server);
         cntorServer.start();
         Thread.currentThread().setContextClassLoader(old);
         sleep(5000);

         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());
         MBeanServerConnection mbsc = cntor.getMBeanServerConnection();
         assertNotNull(mbsc);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public interface EmitterMBean
   {
   }

   private static class Emitter implements NotificationEmitter, EmitterMBean
   {
      private List listeners = new ArrayList();

      public void addNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws IllegalArgumentException
      {
         listeners.add(listener);
      }

      public void removeNotificationListener(NotificationListener listener, NotificationFilter filter, Object handback) throws ListenerNotFoundException
      {
         listeners.remove(listener);
      }

      public MBeanNotificationInfo[] getNotificationInfo()
      {
         return new MBeanNotificationInfo[0];
      }

      public void removeNotificationListener(NotificationListener listener) throws ListenerNotFoundException
      {
      }

      public int getSize()
      {
         return listeners.size();
      }
   }
}
