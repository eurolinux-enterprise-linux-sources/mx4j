/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management;

import java.security.AllPermission;
import java.security.CodeSource;
import java.security.Permission;
import java.security.PermissionCollection;
import java.security.Permissions;
import java.security.Policy;
import java.security.ProtectionDomain;
import java.security.SecurityPermission;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import javax.management.MBeanPermission;
import javax.management.MBeanServer;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerPermission;
import javax.management.ObjectName;
import javax.management.loading.ClassLoaderRepository;
import javax.management.loading.MLet;

import junit.framework.TestCase;
import mx4j.log.Log;
import mx4j.server.MX4JMBeanServer;

/**
 * @version $Revision: 1.5 $
 */
public class LocalSecurityManagerTest extends SecurityManagerTestCase
{
   static
   {
      // For the way JUnit works, we have one JVM per test class
      Policy.setPolicy(new LocalModifiablePolicy());
      System.setSecurityManager(new SecurityManager());
   }

   public LocalSecurityManagerTest(String s)
   {
      super(s);
   }

   protected void setUp() throws Exception
   {
      // Be sure we have a security manager and the right policy
      SecurityManager sm = System.getSecurityManager();
      if (sm == null) fail();
      Policy policy = Policy.getPolicy();
      if (!(policy instanceof LocalModifiablePolicy)) fail();
      ((LocalModifiablePolicy)policy).initialize();
   }

   protected void addPermission(Permission p)
   {
      LocalModifiablePolicy policy = (LocalModifiablePolicy)Policy.getPolicy();
      policy.addPermission(p);
   }

   protected void resetPermissions()
   {
      LocalModifiablePolicy policy = (LocalModifiablePolicy)Policy.getPolicy();
      policy.initialize();
   }

   public void testNewMBeanServer() throws Exception
   {
      try
      {
         MBeanServerFactory.newMBeanServer();
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServerFactory.newMBeanServer();

      try
      {
         MBeanServerFactory.createMBeanServer();
         fail();
      }
      catch (SecurityException ignored)
      {
      }
   }

   public void testCreateMBeanServer() throws Exception
   {
      try
      {
         MBeanServerFactory.createMBeanServer();
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      addPermission(new MBeanServerPermission("createMBeanServer"));
      MBeanServer server = MBeanServerFactory.createMBeanServer();
      MBeanServerFactory.newMBeanServer();

      try
      {
         MBeanServerFactory.releaseMBeanServer(server);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      // Clean up
      addPermission(new MBeanServerPermission("releaseMBeanServer"));
      MBeanServerFactory.releaseMBeanServer(server);
   }

   public void testReleaseMBeanServer() throws Exception
   {
      addPermission(new MBeanServerPermission("createMBeanServer"));
      MBeanServer server = MBeanServerFactory.createMBeanServer();

      try
      {
         MBeanServerFactory.releaseMBeanServer(server);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      addPermission(new MBeanServerPermission("releaseMBeanServer"));
      MBeanServerFactory.releaseMBeanServer(server);
   }

   public void testReleaseMBeanServer2() throws Exception
   {
      addPermission(new MBeanServerPermission("createMBeanServer, releaseMBeanServer"));
      MBeanServer server = MBeanServerFactory.createMBeanServer();
      MBeanServerFactory.releaseMBeanServer(server);
   }

   public void testFindMBeanServer() throws Exception
   {
      addPermission(new MBeanServerPermission("createMBeanServer"));
      MBeanServer server = MBeanServerFactory.createMBeanServer();

      try
      {
         MBeanServerFactory.findMBeanServer(null);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      addPermission(new MBeanServerPermission("findMBeanServer"));
      ArrayList list = MBeanServerFactory.findMBeanServer(null);
      if (!list.contains(server)) fail();

      // Clean up
      addPermission(new MBeanServerPermission("releaseMBeanServer"));
      MBeanServerFactory.releaseMBeanServer(server);
   }

   public void testAddRemoveNotificationListener() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      testAddRemoveNotificationListener(server);
   }

   public void testCreateMBean4Params() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      testCreateMBean4Params(server);
   }

   public void testCreateMBean5Params() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      testCreateMBean5Params(server);
   }

   public void testGetAttribute() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      testGetAttribute(server);
   }

   public void testGetAttributes() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      testGetAttributes(server);
   }

   public void testGetDefaultDomain() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      String domain = "simon";
      MBeanServer server = MBeanServerFactory.newMBeanServer(domain);
      testGetDefaultDomain(server, domain);
   }

   public void testGetDomains() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      testGetDomains(server);
   }

   public void testGetMBeanCount() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      testGetMBeanCount(server);
   }

   public void testGetMBeanInfo() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      testGetMBeanInfo(server);
   }

   public void testGetObjectInstance() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      testGetObjectInstance(server);
   }

   public void testInstantiate() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();

      String className = "java.lang.String";

      try
      {
         server.instantiate(className, null);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      addPermission(new MBeanPermission(className, "instantiate"));
      server.instantiate(className, null);

      // Check also the overloaded version, we need an MLet
      String mletClassName = "javax.management.loading.MLet";
      ObjectName name = new ObjectName(server.getDefaultDomain(), "mbean", "mlet");
      resetPermissions();
      addPermission(new MBeanPermission(mletClassName, "instantiate, registerMBean"));
      addPermission(new RuntimePermission("createClassLoader"));
      server.createMBean(mletClassName, name, null);

      try
      {
         server.instantiate(className, null);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      addPermission(new MBeanPermission(className, "instantiate"));
      server.instantiate(className, null);
   }

   public void testInvoke() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      testInvoke(server);
   }

   public void testIsInstanceOf() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      testIsInstanceOf(server);
   }

   public void testIsRegistered() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      testIsRegistered(server);
   }

   public void testQueryMBeans() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      testQueryMBeans(server);
   }

   public void testQueryNames() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      testQueryNames(server);
   }

   public void testRegisterMBean() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();

      MBeanServerDelegate mbean = new MBeanServerDelegate();
      ObjectName name = new ObjectName(server.getDefaultDomain(), "name", "test");

      try
      {
         server.registerMBean(mbean, name);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      addPermission(new MBeanPermission(mbean.getClass().getName() + "[" + name.getCanonicalName() + "]", "registerMBean"));
      server.registerMBean(mbean, name);
   }

   public void testSetAttribute() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      testSetAttribute(server);
   }

   public void testSetAttributes() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      testSetAttributes(server);
   }

   public void testUnregisterMBean() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      testUnregisterMBean(server);
   }

   public void testGetClassLoader() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();

      // Needed to create an MLet, which is a ClassLoader
      addPermission(new RuntimePermission("createClassLoader"));
      ObjectName name = new ObjectName(server.getDefaultDomain(), "mbean", "mlet");
      MLet mlet = new MLet();
      addPermission(new MBeanPermission(mlet.getClass().getName(), "registerMBean"));
      server.registerMBean(mlet, name);

      try
      {
         server.getClassLoader(null);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      // Dummy class
      addPermission(new MBeanPermission("foo[" + name.getCanonicalName() + "]", "getClassLoader"));

      try
      {
         server.getClassLoader(name);
         fail();
      }
      catch (SecurityException ignored)
      {
      }

      addPermission(new MBeanPermission(mlet.getClass().getName() + "[" + name.getCanonicalName() + "]", "getClassLoader"));
      ClassLoader result = server.getClassLoader(name);
      assertSame(result, mlet);
   }

   public void testGetClassLoaderFor() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      ObjectName delegate = ObjectName.getInstance("JMImplementation", "type", "MBeanServerDelegate");
      try
      {
         server.getClassLoaderFor(delegate);
         fail();
      }
      catch (SecurityException x)
      {
      }
      addPermission(new MBeanPermission("[" + delegate.getCanonicalName() + "]", "getClassLoaderFor"));
      ClassLoader loader = server.getClassLoaderFor(delegate);
      assertNotNull(loader);
   }

   public void testGetClassLoaderRepository() throws Exception
   {
      addPermission(new MBeanServerPermission("newMBeanServer"));
      MBeanServer server = MBeanServerFactory.newMBeanServer();
      try
      {
         server.getClassLoaderRepository();
         fail();
      }
      catch (SecurityException x)
      {
      }
      addPermission(new MBeanPermission("*", "getClassLoaderRepository"));
      ClassLoaderRepository loader = server.getClassLoaderRepository();
      assertNotNull(loader);
   }

   /**
    * A modifiable policy that allow permissions to be added at runtime, used for tests purposes only.
    */
   public static class LocalModifiablePolicy extends Policy
   {
      private final ProtectionDomain testDomain;
      private final Map permissionsMap = new HashMap();

      public LocalModifiablePolicy()
      {
         // Here we still have no security manager installed
         testDomain = LocalModifiablePolicy.class.getProtectionDomain();

         // Add the permissions needed to run the tests
         CodeSource junitCodeSource = TestCase.class.getProtectionDomain().getCodeSource();
         permissionsMap.put(junitCodeSource, createAllPermissions());

         CodeSource mx4jCodeSource = MBeanServerFactory.class.getProtectionDomain().getCodeSource();
         permissionsMap.put(mx4jCodeSource, createAllPermissions());

         CodeSource implCodeSource = MX4JMBeanServer.class.getProtectionDomain().getCodeSource();
         permissionsMap.put(implCodeSource, createAllPermissions());

         // In the automated tests, the log classes may be taken from the JSR 160 jars.
         CodeSource logCodeSource = Log.class.getProtectionDomain().getCodeSource();
         permissionsMap.put(logCodeSource, createAllPermissions());

         ClassLoader loader = getClass().getClassLoader();

         // BCEL
         try
         {
            Class cls = loader.loadClass("org.apache.bcel.generic.Type");
            CodeSource bcelCodeSource = cls.getProtectionDomain().getCodeSource();
            permissionsMap.put(bcelCodeSource, createAllPermissions());
         }
         catch (ClassNotFoundException ignored)
         {
         }


         // When we run automated, we need also permissions for Ant jars
         try
         {
            Class cls = loader.loadClass("org.apache.tools.ant.Task");
            CodeSource antCodeSource = cls.getProtectionDomain().getCodeSource();
            permissionsMap.put(antCodeSource, createAllPermissions());
            cls = loader.loadClass("org.apache.tools.ant.taskdefs.optional.junit.JUnitTask");
            antCodeSource = cls.getProtectionDomain().getCodeSource();
            permissionsMap.put(antCodeSource, createAllPermissions());
         }
         catch (ClassNotFoundException ignored)
         {
         }

         initialize();
      }

      private Permissions createAllPermissions()
      {
         Permissions allPermissions = new Permissions();
         allPermissions.add(new AllPermission());
         return allPermissions;
      }

      public PermissionCollection getPermissions(CodeSource codesource)
      {
         Permissions permissions = (Permissions)permissionsMap.get(codesource);
         if (permissions == null)
         {
            permissions = new Permissions();
            permissionsMap.put(codesource, permissions);
         }
         return permissions;
      }

      public void refresh()
      {
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
       * LocalModifiablePolicy.implies()
       */
      public boolean implies(ProtectionDomain domain, Permission permission)
      {
         PermissionCollection perms = getPermissions(domain.getCodeSource());
         return perms.implies(permission);
      }

      /**
       * Adds the given permission to the client (the test in this case) codesource
       */
      public void addPermission(Permission p)
      {
         Permissions permissions = (Permissions)getPermissions(testDomain.getCodeSource());
         permissions.add(p);
      }

      /**
       * Initializes the permissions for the client (the test in this case) codesource
       */
      public synchronized void initialize()
      {
         Permissions permissions = new Permissions();
         permissions.add(new SecurityPermission("getPolicy"));
         permissionsMap.put(testDomain.getCodeSource(), permissions);
      }
   }
}
