/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote;

import java.net.MalformedURLException;
import java.net.SocketPermission;
import java.security.AccessController;
import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.Principal;
import java.security.ProtectionDomain;
import java.security.SecurityPermission;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.management.MBeanPermission;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerPermission;
import javax.management.MBeanTrustPermission;
import javax.management.ObjectName;
import javax.management.remote.JMXAuthenticator;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXPrincipal;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.SubjectDelegationPermission;
import javax.security.auth.AuthPermission;
import javax.security.auth.Subject;

import junit.framework.TestCase;
import mx4j.remote.MX4JRemoteUtils;
import mx4j.server.MX4JMBeanServer;
import test.javax.management.SecurityManagerTestCase;

/**
 * @version $Revision: 1.4 $
 */
public abstract class RemoteSecurityManagerTestCase extends SecurityManagerTestCase
{
   public RemoteSecurityManagerTestCase(String s)
   {
      super(s);
   }

   protected void setUp() throws Exception
   {
      // Be sure we have a security manager and the right policy
      SecurityManager sm = System.getSecurityManager();
      if (sm == null) fail();
      Policy policy = Policy.getPolicy();
      if (!(policy instanceof RemoteModifiablePolicy)) fail();
      ((RemoteModifiablePolicy)policy).initialize();
   }

   protected void tearDown() throws Exception
   {
      // Allow the sockets to shut down
      sleep(2000);
   }

   protected void addPermission(Permission p)
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      policy.addServerPermission(p);
   }

   protected void resetPermissions()
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      policy.initialize();
   }

   /**
    * Creates and returns a suitable JMXServiceURL for the specific JMXConnectorServer
    * used in the tests. Subclasses implements it to return specific JMXServiceURL.
    */
   protected abstract JMXServiceURL createJMXConnectorServerAddress() throws MalformedURLException;

   /**
    * Creates and returns a new environment map to be used for the server side
    *
    * @see #createClientEnvironment
    */
   protected Map createServerEnvironment()
   {
      return new HashMap();
   }

   /**
    * Creates and returns a new environment map to be used for the client side
    *
    * @see #createServerEnvironment
    */
   protected Map createClientEnvironment()
   {
      return new HashMap();
   }

   public void testNewJMXConnectorServer() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());

      try
      {
         cntorServer.start();
         fail();
      }
      catch (SecurityException x)
      {
      }
   }

   public void testStartJMXConnectorServer() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());

      addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
      try
      {
         cntorServer.start();
      }
      finally
      {
         cntorServer.stop();
      }
   }

   public void testConnect() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());

      // Required by the server
      addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));

      JMXConnector cntor = null;
      try
      {
         cntorServer.start();

         RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
         policy.setSeparateClientServerPermissions(true);

         try
         {
            JMXConnectorFactory.connect(cntorServer.getAddress());
            fail();
         }
         catch (SecurityException x)
         {
         }

         // Allow any client to connect to the server
         addPermission(new SocketPermission("*:1024-" + url.getPort(), "accept"));
         // Allow this client to open a socket to connect to the server
         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress());
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testAuthenticatedConnect() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      Map serverEnv = createServerEnvironment();
      serverEnv.put(JMXConnectorServer.AUTHENTICATOR, new JMXAuthenticator()
      {
         public Subject authenticate(Object credentials) throws SecurityException
         {
            if (!(credentials instanceof String[])) throw new SecurityException("No credentials provided");
            String[] creds = (String[])credentials;
            if (creds.length != 2) throw new SecurityException("Bad credentials");
            String user = creds[0];
            String password = creds[1];
            if (!"test".equals(user)) throw new SecurityException("Unknown user");
            if (!"test".equals(password)) throw new SecurityException("Wrong password");
            Principal principal = new JMXPrincipal(user);
            Set principals = new HashSet();
            principals.add(principal);
            Subject subject = new Subject(true, principals, Collections.EMPTY_SET, Collections.EMPTY_SET);
            return subject;
         }
      });

      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, serverEnv, newMBeanServer());

         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
         policy.setSeparateClientServerPermissions(true);

         Map clientEnv = createClientEnvironment();
         clientEnv.put(JMXConnector.CREDENTIALS, new String[]{"test", "test"});
         // Allow this client to open a socket to connect to the server
         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         // Allow the authenticated subject to listen and accept a connection
         policy.addServerPermission(new JMXPrincipal("test"), new SocketPermission("localhost:" + url.getPort(), "listen"));
         policy.addServerPermission(new JMXPrincipal("test"), new SocketPermission("*:1024-" + url.getPort(), "accept"));

         // No credentials
         try
         {
            JMXConnectorFactory.connect(cntorServer.getAddress());
            fail();
         }
         catch (SecurityException x)
         {
         }

         // Bad credentials
         clientEnv = createClientEnvironment();
         clientEnv.put(JMXConnector.CREDENTIALS, null);
         try
         {
            JMXConnectorFactory.connect(cntorServer.getAddress(), clientEnv);
            fail();
         }
         catch (SecurityException x)
         {
         }

         // Bad credentials
         clientEnv = createClientEnvironment();
         clientEnv.put(JMXConnector.CREDENTIALS, new StringBuffer());
         try
         {
            JMXConnectorFactory.connect(cntorServer.getAddress(), clientEnv);
            fail();
         }
         catch (SecurityException x)
         {
         }

         // Bad credentials
         clientEnv = createClientEnvironment();
         clientEnv.put(JMXConnector.CREDENTIALS, new String[0]);
         try
         {
            JMXConnectorFactory.connect(cntorServer.getAddress(), clientEnv);
            fail();
         }
         catch (SecurityException x)
         {
         }

         // Bad credentials
         clientEnv = createClientEnvironment();
         clientEnv.put(JMXConnector.CREDENTIALS, new String[]{"dummy"});
         try
         {
            JMXConnectorFactory.connect(cntorServer.getAddress(), clientEnv);
            fail();
         }
         catch (SecurityException x)
         {
         }

         // Bad credentials
         clientEnv = createClientEnvironment();
         clientEnv.put(JMXConnector.CREDENTIALS, new String[]{"dummy", "dummy"});
         try
         {
            JMXConnectorFactory.connect(cntorServer.getAddress(), clientEnv);
            fail();
         }
         catch (SecurityException x)
         {
         }

         // Bad credentials
         clientEnv = createClientEnvironment();
         clientEnv.put(JMXConnector.CREDENTIALS, new String[]{"dummy", "dummy", "dummy"});
         try
         {
            JMXConnectorFactory.connect(cntorServer.getAddress(), clientEnv);
            fail();
         }
         catch (SecurityException x)
         {
         }

         // Bad credentials
         clientEnv = createClientEnvironment();
         clientEnv.put(JMXConnector.CREDENTIALS, new String[]{"test", "wrong"});
         try
         {
            JMXConnectorFactory.connect(cntorServer.getAddress(), clientEnv);
            fail();
         }
         catch (SecurityException x)
         {
         }

         clientEnv = createClientEnvironment();
         clientEnv.put(JMXConnector.CREDENTIALS, new String[]{"test", "test"});
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), clientEnv);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testAuthenticatedSubjectOnServerSide() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      Map serverEnv = createServerEnvironment();
      serverEnv.put(JMXConnectorServer.AUTHENTICATOR, new JMXAuthenticator()
      {
         public Subject authenticate(Object credentials) throws SecurityException
         {
            if (!(credentials instanceof String[])) throw new SecurityException("No credentials provided");
            String[] creds = (String[])credentials;
            if (creds.length != 2) throw new SecurityException("Bad credentials");
            String user = creds[0];
            String password = creds[1];
            if (!"test".equals(user)) throw new SecurityException("Unknown user");
            if (!"test".equals(password)) throw new SecurityException("Wrong password");
            Principal principal = new JMXPrincipal(user);
            Set principals = new HashSet();
            principals.add(principal);
            Subject subject = new Subject(true, principals, Collections.EMPTY_SET, Collections.EMPTY_SET);
            return subject;
         }
      });

      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, serverEnv, newMBeanServer());

         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         Map clientEnv = createClientEnvironment();
         clientEnv.put(JMXConnector.CREDENTIALS, new String[]{"test", "test"});
         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         policy.addServerPermission(new JMXPrincipal("test"), new AuthPermission("doAsPrivileged"));
         policy.addServerPermission(new JMXPrincipal("test"), new SocketPermission("localhost:" + url.getPort(), "listen"));
         policy.addServerPermission(new JMXPrincipal("test"), new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), clientEnv);

         addPermission(new MBeanTrustPermission("*"));
         policy.addServerPermission(new JMXPrincipal("test"), new MBeanPermission("*", "instantiate, registerMBean, getAttribute"));
         MBeanServerConnection cntion = cntor.getMBeanServerConnection();
         ObjectName name = ObjectName.getInstance(":name=subject");
         cntion.createMBean(SubjectCheck.class.getName(), name, null);
         policy.addServerPermission(new JMXPrincipal("test"), new AuthPermission("getSubject"));
         Subject subject = (Subject)cntion.getAttribute(name, "Subject");

         Set principals = subject.getPrincipals();
         assertNotNull(principals);
         assertEquals(principals.size(), 1);
         Principal principal = (Principal)principals.iterator().next();
         assertTrue(principal instanceof JMXPrincipal);
         assertEquals(principal.getName(), "test");
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testDelegateSubjectOnServerSide() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      Map serverEnv = createServerEnvironment();
      serverEnv.put(JMXConnectorServer.AUTHENTICATOR, new JMXAuthenticator()
      {
         public Subject authenticate(Object credentials) throws SecurityException
         {
            if (!(credentials instanceof String[])) throw new SecurityException("No credentials provided");
            String[] creds = (String[])credentials;
            if (creds.length != 2) throw new SecurityException("Bad credentials");
            String user = creds[0];
            String password = creds[1];
            if (!"test".equals(user)) throw new SecurityException("Unknown user");
            if (!"test".equals(password)) throw new SecurityException("Wrong password");
            Principal principal = new JMXPrincipal(user);
            Set principals = new HashSet();
            principals.add(principal);
            Subject subject = new Subject(true, principals, Collections.EMPTY_SET, Collections.EMPTY_SET);
            return subject;
         }
      });

      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, serverEnv, newMBeanServer());
         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         Map clientEnv = createClientEnvironment();
         clientEnv.put(JMXConnector.CREDENTIALS, new String[]{"test", "test"});
         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         policy.addServerPermission(new JMXPrincipal("test"), new AuthPermission("doAsPrivileged"));
         policy.addServerPermission(new JMXPrincipal("test"), new SocketPermission("localhost:" + url.getPort(), "listen"));
         policy.addServerPermission(new JMXPrincipal("test"), new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), clientEnv);

         addPermission(new MBeanTrustPermission("*"));
         policy.addServerPermission(new JMXPrincipal("delegate"), new MBeanPermission("*", "instantiate, registerMBean, getAttribute"));
         policy.addServerPermission(new JMXPrincipal("test"), new SubjectDelegationPermission(JMXPrincipal.class.getName() + ".delegate"));

         Set delegates = new HashSet();
         delegates.add(new JMXPrincipal("delegate"));
         Subject delegate = new Subject(true, delegates, Collections.EMPTY_SET, Collections.EMPTY_SET);
         MBeanServerConnection cntion = cntor.getMBeanServerConnection(delegate);
         ObjectName name = ObjectName.getInstance(":name=subject");
         cntion.createMBean(SubjectCheck.class.getName(), name, null);
         policy.addServerPermission(new JMXPrincipal("delegate"), new AuthPermission("getSubject"));
         Subject subject = (Subject)cntion.getAttribute(name, "Subject");

         Set principals = subject.getPrincipals();
         assertNotNull(principals);
         assertEquals(principals.size(), 1);
         Principal principal = (Principal)principals.iterator().next();
         assertTrue(principal instanceof JMXPrincipal);
         assertEquals(principal.getName(), "delegate");
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testAddRemoveNotificationListener() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());
         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         addPermission(new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), createClientEnvironment());

         MBeanServerConnection cntion = cntor.getMBeanServerConnection();

         testAddRemoveNotificationListener(cntion);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testCreateMBean4Params() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());
         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         addPermission(new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), createClientEnvironment());

         MBeanServerConnection cntion = cntor.getMBeanServerConnection();

         testCreateMBean4Params(cntion);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testCreateMBean5Params() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());
         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         addPermission(new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), createClientEnvironment());

         MBeanServerConnection cntion = cntor.getMBeanServerConnection();

         testCreateMBean5Params(cntion);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testGetAttribute() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());
         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         addPermission(new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), createClientEnvironment());

         MBeanServerConnection cntion = cntor.getMBeanServerConnection();

         testGetAttribute(cntion);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testGetAttributes() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());
         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         addPermission(new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), createClientEnvironment());

         MBeanServerConnection cntion = cntor.getMBeanServerConnection();

         testGetAttributes(cntion);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testGetDefaultDomain() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         String domain = "xxx";
         MBeanServer server = MBeanServerFactory.newMBeanServer(domain);
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), server);
         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         addPermission(new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), createClientEnvironment());

         MBeanServerConnection cntion = cntor.getMBeanServerConnection();

         testGetDefaultDomain(cntion, domain);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testGetDomains() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());
         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         addPermission(new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), createClientEnvironment());

         MBeanServerConnection cntion = cntor.getMBeanServerConnection();

         testGetDomains(cntion);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testGetMBeanCount() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());
         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         addPermission(new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), createClientEnvironment());

         MBeanServerConnection cntion = cntor.getMBeanServerConnection();

         testGetMBeanCount(cntion);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testGetMBeanInfo() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());
         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         addPermission(new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), createClientEnvironment());

         MBeanServerConnection cntion = cntor.getMBeanServerConnection();

         testGetMBeanInfo(cntion);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testGetObjectInstance() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());
         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         addPermission(new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), createClientEnvironment());

         MBeanServerConnection cntion = cntor.getMBeanServerConnection();

         testGetObjectInstance(cntion);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testInvoke() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());
         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         addPermission(new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), createClientEnvironment());

         MBeanServerConnection cntion = cntor.getMBeanServerConnection();

         testInvoke(cntion);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testIsInstanceOf() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());
         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         addPermission(new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), createClientEnvironment());

         MBeanServerConnection cntion = cntor.getMBeanServerConnection();

         testIsInstanceOf(cntion);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testIsRegistered() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());
         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         addPermission(new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), createClientEnvironment());

         MBeanServerConnection cntion = cntor.getMBeanServerConnection();

         testIsRegistered(cntion);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testQueryMBeans() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());
         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         addPermission(new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), createClientEnvironment());

         MBeanServerConnection cntion = cntor.getMBeanServerConnection();

         testQueryMBeans(cntion);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testQueryNames() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());
         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         addPermission(new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), createClientEnvironment());

         MBeanServerConnection cntion = cntor.getMBeanServerConnection();

         testQueryNames(cntion);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testSetAttribute() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());
         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         addPermission(new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), createClientEnvironment());

         MBeanServerConnection cntion = cntor.getMBeanServerConnection();

         testSetAttribute(cntion);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testSetAttributes() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());
         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         addPermission(new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), createClientEnvironment());

         MBeanServerConnection cntion = cntor.getMBeanServerConnection();

         testSetAttributes(cntion);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testUnregisterMBean() throws Exception
   {
      RemoteModifiablePolicy policy = (RemoteModifiablePolicy)Policy.getPolicy();
      addPermission(new MBeanServerPermission("newMBeanServer"));

      JMXServiceURL url = createJMXConnectorServerAddress();
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, createServerEnvironment(), newMBeanServer());
         // Required by the server
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         cntorServer.start();

         policy.setSeparateClientServerPermissions(true);

         policy.addClientPermission(new SocketPermission("localhost:" + url.getPort(), "connect"));
         addPermission(new SocketPermission("localhost:" + url.getPort(), "listen"));
         addPermission(new SocketPermission("*:1024-" + url.getPort(), "accept"));
         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), createClientEnvironment());

         MBeanServerConnection cntion = cntor.getMBeanServerConnection();

         testUnregisterMBean(cntion);
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public interface SubjectCheckMBean
   {
      public Subject getSubject();
   }

   public static class SubjectCheck implements SubjectCheckMBean
   {
      public Subject getSubject()
      {
         return Subject.getSubject(AccessController.getContext());
      }
   }

   /**
    * A modifiable policy that allow permissions to be added at runtime, used for tests purposes only.
    */
   public abstract static class RemoteModifiablePolicy extends Policy
   {
      private final ProtectionDomain testDomain;
      private final Map serverPermissions = new HashMap();
      private final Map clientPermissions = new HashMap();
      private final Map principalPermissions = new HashMap();
      private volatile boolean separated;

      public RemoteModifiablePolicy()
      {
         // Here we still have no security manager installed
         testDomain = RemoteModifiablePolicy.class.getProtectionDomain();

         // Add the permissions needed to run the tests
         CodeSource junitCodeSource = TestCase.class.getProtectionDomain().getCodeSource();
         serverPermissions.put(junitCodeSource, createAllPermissions());
         clientPermissions.put(junitCodeSource, createAllPermissions());

         CodeSource mx4jCodeSource = MBeanServerFactory.class.getProtectionDomain().getCodeSource();
         serverPermissions.put(mx4jCodeSource, createAllPermissions());
         clientPermissions.put(mx4jCodeSource, createAllPermissions());

         CodeSource implCodeSource = MX4JMBeanServer.class.getProtectionDomain().getCodeSource();
         serverPermissions.put(implCodeSource, createAllPermissions());
         clientPermissions.put(implCodeSource, createAllPermissions());

         CodeSource rmx4jCodeSource = JMXConnector.class.getProtectionDomain().getCodeSource();
         serverPermissions.put(rmx4jCodeSource, createAllPermissions());
         clientPermissions.put(rmx4jCodeSource, createAllPermissions());

         CodeSource rimplCodeSource = MX4JRemoteUtils.class.getProtectionDomain().getCodeSource();
         serverPermissions.put(rimplCodeSource, createAllPermissions());
         clientPermissions.put(rimplCodeSource, createAllPermissions());

         ClassLoader loader = getClass().getClassLoader();

         // BCEL
         try
         {
            Class cls = loader.loadClass("org.apache.bcel.generic.Type");
            CodeSource bcelCodeSource = cls.getProtectionDomain().getCodeSource();
            serverPermissions.put(bcelCodeSource, createAllPermissions());
            clientPermissions.put(bcelCodeSource, createAllPermissions());
         }
         catch (ClassNotFoundException ignored)
         {
         }


         // When we run automated, we need also permissions for Ant jars
         try
         {
            Class cls = loader.loadClass("org.apache.tools.ant.Task");
            CodeSource antCodeSource = cls.getProtectionDomain().getCodeSource();
            serverPermissions.put(antCodeSource, createAllPermissions());
            clientPermissions.put(antCodeSource, createAllPermissions());
            cls = loader.loadClass("org.apache.tools.ant.taskdefs.optional.junit.JUnitTask");
            antCodeSource = cls.getProtectionDomain().getCodeSource();
            serverPermissions.put(antCodeSource, createAllPermissions());
            clientPermissions.put(antCodeSource, createAllPermissions());
         }
         catch (ClassNotFoundException ignored)
         {
         }

         mapServerPermissions(serverPermissions);

         initialize();
      }

      /**
       * Callback for subclasses to add more mappings between a codesource and
       * (normally) a Permissions object containing AllPermission.
       * This is necessary if the implementation of the JMXConnectorServer needs additional
       * jars such as for example commons-logging.jar, whose codesource is normally mapped
       * with AllPermission.
       *
       * @see #createAllPermissions
       */
      protected void mapServerPermissions(Map permissions)
      {
         // Nothing necessary here
      }

      /**
       * Creates and returns a Permissions object containing AllPermission.
       *
       * @see #mapServerPermissions
       */
      protected Permissions createAllPermissions()
      {
         Permissions allPermissions = new Permissions();
         allPermissions.add(new AllPermission());
         return allPermissions;
      }

      /**
       * Returns whether the current thread is a server side thread or
       * a client side thread.
       * Subclasses implement this method by for example looking at the thread name.
       *
       * @see #setSeparateClientServerPermissions
       * @see #isSeparateClientServerPermissions
       */
      public abstract boolean isServerSide();

      public PermissionCollection getPermissions(CodeSource codesource)
      {
         return getPermissions(codesource, isServerSide());
      }

      private synchronized PermissionCollection getPermissions(CodeSource codesource, boolean serverside)
      {
         if (serverside)
         {
            PermissionCollection perms = (PermissionCollection)serverPermissions.get(codesource);
            if (perms == null) perms = new Permissions();
            perms = copyIfReadOnly(perms);
            serverPermissions.put(codesource, perms);
            return perms;
         }
         else
         {
            PermissionCollection perms = (PermissionCollection)clientPermissions.get(codesource);
            if (perms == null) perms = new Permissions();
            perms = copyIfReadOnly(perms);
            clientPermissions.put(codesource, perms);
            return perms;
         }
      }

      /**
       * For JDK 1.4 overriding this method disables caching of Permissions done by the
       * standard Policy implementation.
       * This is done because we install this policy *before* installing the security manager.
       * By doing so, in JDK 1.4 the permissions granted at the moment of policy installation
       * (no security manager == AllPermission) are cached and will invalidate all tests, since
       * they will become unmodifiable.
       * <p/>
       * The stack trace is when checking a permission is:
       * <p/>
       * SecurityManager.checkPermission()
       * AccessController.checkPermission()
       * AccessControlContext.checkPermission()
       * ProtectionDomain.implies()
       * RemoteModifiablePolicy.implies()
       */
      public boolean implies(ProtectionDomain domain, Permission permission)
      {
         Principal[] principals = domain.getPrincipals();
         boolean injectedDomain = false;
         CodeSource cs = domain.getCodeSource();
         if (principals != null && principals.length > 0 && cs != null && cs.getLocation() == null && domain.getClassLoader() == null) injectedDomain = true;

         if (!injectedDomain)
         {
            PermissionCollection perms = getPermissions(cs);
            boolean result = perms.implies(permission);
//            System.out.println("Policy.implies, side is " + (isServerSide() ? "server" : "client") + " codesource " + cs + " on " + permission + " over " + perms + ": " + result);
            return result;
         }
         else
         {
            for (int i = 0; i < principals.length; ++i)
            {
               Principal principal = principals[i];
               PermissionCollection perms = getPrincipalPermissions(principal);
               if (perms.implies(permission)) return true;
            }
            return false;
         }
      }

      private synchronized PermissionCollection getPrincipalPermissions(Principal principal)
      {
         PermissionCollection perms = (PermissionCollection)principalPermissions.get(principal);
         if (perms == null) perms = new Permissions();
         perms = copyIfReadOnly(perms);
         principalPermissions.put(principal, perms);
         return perms;
      }

      public void refresh()
      {
      }

      /**
       * Adds the given permission to the client (the test in this case) codesource,
       * on server side
       */
      public void addServerPermission(Permission p)
      {
         Permissions permissions = (Permissions)getPermissions(testDomain.getCodeSource(), true);
         permissions.add(p);
      }

      /**
       * Adds the given permission to the client (the test in this case) codesource,
       * on client side
       */
      public void addClientPermission(Permission p)
      {
         Permissions permissions = (Permissions)getPermissions(testDomain.getCodeSource(), false);
         permissions.add(p);
      }

      /**
       * Adds the given permission to the JSR 160 injected codesource,
       * on server side, for the given principal
       */
      public void addServerPermission(Principal principal, Permission p)
      {
         Permissions permissions = (Permissions)getPrincipalPermissions(principal);
         permissions.add(p);
         addServerPermission(p);
      }

      /**
       * Initializes the permissions for the client (the test in this case) codesource,
       * and marks this policy as working on server side.
       *
       * @see #setSeparateClientServerPermissions
       */
      public synchronized void initialize()
      {
         Permissions permissions = new Permissions();
         permissions.add(new SecurityPermission("getPolicy"));
         serverPermissions.put(testDomain.getCodeSource(), permissions);
         permissions = new Permissions();
         permissions.add(new SecurityPermission("getPolicy"));
         clientPermissions.put(testDomain.getCodeSource(), permissions);
         principalPermissions.clear();
         setSeparateClientServerPermissions(false);
      }

      /**
       * Tells this policy to distinguish the check of permissions basing on the fact
       * that the thread is a server side thread or a client side thread.
       *
       * @see #isServerSide
       * @see #isSeparateClientServerPermissions
       */
      public synchronized void setSeparateClientServerPermissions(boolean value)
      {
         separated = value;
      }

      /**
       * Returns if this policy distinguishes between server side and client side
       * permission checking.
       *
       * @see #setSeparateClientServerPermissions
       */
      protected synchronized boolean isSeparateClientServerPermissions()
      {
         return separated;
      }

      /**
       * The RMI marshalling mechanism (and this is only one place I have discovered so far)
       * sometimes marks the PermissionCollection as read-only via
       * {@link PermissionCollection#setReadOnly}. Since this policy
       * adds permissions at runtime, having the Permissions object marked as read-only
       * causes an exception that invalidates the test.
       * Here I copy the read-only Permissions into a new Permissions object (by copying
       * all permissions it contains into the copied object), and return it.
       * If the Permissions object is not read-only, it is returned immediately.
       */
      private PermissionCollection copyIfReadOnly(PermissionCollection p)
      {
         if (!p.isReadOnly()) return p;
         Permissions permissions = new Permissions();
         for (Enumeration e = p.elements(); e.hasMoreElements();)
         {
            Permission permission = (Permission)e.nextElement();
            permissions.add(permission);
         }
         return permissions;
      }
   }
}
