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
import java.security.AccessController;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MBeanServerFactory;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.RuntimeMBeanException;
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
 * @version $Revision: 1.7 $
 */
public abstract class JMXConnectorInvocationTestCase extends MX4JTestCase
{
   private JMXConnectorServer connectorServer;

   public JMXConnectorInvocationTestCase(String name)
   {
      super(name);
   }

   public abstract JMXServiceURL createJMXConnectorServerAddress() throws MalformedURLException;

   public abstract Map getEnvironment();

   private MBeanServerConnection getMBeanServerConnection(MBeanServer server) throws IOException
   {
      JMXServiceURL url = createJMXConnectorServerAddress();
      connectorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), server);
      connectorServer.start();
      sleep(5000);
      JMXConnector connector = JMXConnectorFactory.connect(connectorServer.getAddress(), getEnvironment());
      return connector.getMBeanServerConnection();
   }

   protected void tearDown() throws Exception
   {
      if (connectorServer != null) connectorServer.stop();
      sleep(5000);
   }

   public void testCallToMBeanServerWithAttributeNotFoundException() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());
      try
      {
         // The attribute does not exist
         mbsc.getAttribute(new ObjectName("JMImplementation:type=MBeanServerDelegate"), "dummy");
         fail();
      }
      catch (AttributeNotFoundException e)
      {
      }
   }

   public void testCallToMBeanServerWithInstanceNotFoundException() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());
      try
      {
         // The mbean does not exist
         mbsc.getAttribute(new ObjectName(":type=dummy"), "ImplementationVersion");
         fail();
      }
      catch (InstanceNotFoundException x)
      {
      }
   }

   public void testCreateMBeanWith3Params() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());
      ObjectName name = ObjectName.getInstance("", "test", "invocation");
      ObjectInstance instance = mbsc.createMBean(Support.class.getName(), name, null);
      assertNotNull(instance);
   }

   public void testCreateMBeanWith5Params() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());
      String value = "mx4j";
      ObjectName name = ObjectName.getInstance("", "test", "invocation");
      ObjectInstance instance = mbsc.createMBean(Support.class.getName(), name, null, new Object[]{value}, new String[]{String.class.getName()});
      assertNotNull(instance);

      // Be sure the argument arrived to the MBean
      String result = (String)mbsc.getAttribute(name, "Name");
      assertEquals(result, value);
   }

   public void testGetAttribute() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());
      String value = "mx4j";
      ObjectName name = ObjectName.getInstance("", "test", "invocation");
      ObjectInstance instance = mbsc.createMBean(Support.class.getName(), name, null, new Object[]{value}, new String[]{String.class.getName()});
      assertNotNull(instance);

      // Be sure the argument arrived to the MBean
      String result = (String)mbsc.getAttribute(name, "Name");
      assertEquals(result, value);
   }

   public void testGetAttributes() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());
      String value = "mx4j";
      ObjectName name = ObjectName.getInstance("", "test", "invocation");
      ObjectInstance instance = mbsc.createMBean(Support.class.getName(), name, null, new Object[]{value}, new String[]{String.class.getName()});
      assertNotNull(instance);

      // Be sure the argument arrived to the MBean
      String attribute = "Name";
      String[] attributes = new String[]{attribute};
      AttributeList result = mbsc.getAttributes(name, attributes);
      assertNotNull(result);
      assertEquals(result.size(), attributes.length);
      assertEquals(((Attribute)result.get(0)).getName(), attribute);
      assertEquals(((Attribute)result.get(0)).getValue(), value);
   }

   public void testGetDefaultDomain() throws Exception
   {
      String domain = "DOMAIN";
      MBeanServer server = MBeanServerFactory.newMBeanServer(domain);
      MBeanServerConnection mbsc = getMBeanServerConnection(server);

      String defaultDomain = mbsc.getDefaultDomain();
      assertEquals(defaultDomain, domain);
   }

   public void testGetDomains() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());

      String[] domains = mbsc.getDomains();
      if (domains.length < 1) fail();
   }

   public void testGetMBeanCount() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());

      Integer count = mbsc.getMBeanCount();
      if (count.intValue() < 1) fail();
   }

   public void testGetMBeanInfo() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());

      ObjectName name = ObjectName.getInstance("JMImplementation", "type", "MBeanServerDelegate");
      MBeanInfo info = mbsc.getMBeanInfo(name);
      assertNotNull(info);
      if (info.getAttributes().length < 1) fail();
   }

   public void testGetObjectInstance() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());
      ObjectName name = ObjectName.getInstance("", "test", "invocation");
      ObjectInstance instance = mbsc.createMBean(Support.class.getName(), name, null);
      assertNotNull(instance);

      ObjectInstance result = mbsc.getObjectInstance(name);
      assertNotNull(result);
      assertEquals(result, instance);
   }

   public void testInvoke() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());
      ObjectName name = ObjectName.getInstance("", "test", "invocation");
      ObjectInstance instance = mbsc.createMBean(Support.class.getName(), name, null);
      assertNotNull(instance);

      String value = "value";
      String result = (String)mbsc.invoke(name, "echo", new Object[]{value}, new String[]{String.class.getName()});
      assertNotNull(result);
      assertEquals(result, value);
   }

   public void testInvokeThrowsCustomException() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());
      ObjectName name = ObjectName.getInstance("", "test", "invocation");
      ObjectInstance instance = mbsc.createMBean(Support.class.getName(), name, null);
      assertNotNull(instance);

      try
      {
         mbsc.invoke(name, "throwCustomException", null, null);
      }
      catch (MBeanException x)
      {
         Exception xx = x.getTargetException();
         if (xx != null && xx.getClass() != SupportException.class) fail();
      }
   }

   public void testInvokeThrowsRuntimeException() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());
      ObjectName name = ObjectName.getInstance("", "test", "invocation");
      ObjectInstance instance = mbsc.createMBean(Support.class.getName(), name, null);
      assertNotNull(instance);

      try
      {
         mbsc.invoke(name, "throwRuntimeException", null, null);
      }
      catch (RuntimeMBeanException x)
      {
         Exception xx = x.getTargetException();
         if (xx != null && xx.getClass() != IllegalArgumentException.class) fail();
      }
   }

   public void testIsInstanceOf() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());
      ObjectName name = ObjectName.getInstance("", "test", "invocation");
      ObjectInstance instance = mbsc.createMBean(Support.class.getName(), name, null);
      assertNotNull(instance);

      boolean isInstanceOf = mbsc.isInstanceOf(name, Support.class.getName());
      if (!isInstanceOf) fail("Class");

      isInstanceOf = mbsc.isInstanceOf(name, SupportMBean.class.getName());
      if (!isInstanceOf) fail("Interface");
   }

   public void testIsRegistered() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());
      ObjectName name = ObjectName.getInstance("", "test", "invocation");
      ObjectInstance instance = mbsc.createMBean(Support.class.getName(), name, null);
      assertNotNull(instance);

      boolean isRegistered = mbsc.isRegistered(name);
      if (!isRegistered) fail();
   }

   public void testQueryMBeansNullNull() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());

      Set mbeans = mbsc.queryMBeans(null, null);
      assertNotNull(mbeans);
      if (mbeans.size() < 1) fail();
   }

   public void testQueryMBeansObjectNameNull() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());

      Set mbeans = mbsc.queryMBeans(ObjectName.getInstance("JMImplementation:*"), null);
      assertNotNull(mbeans);
      if (mbeans.size() < 1) fail();
   }

   public void testQueryMBeansObjectNameQueryExp() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());

      Set mbeans = mbsc.queryMBeans(ObjectName.getInstance("JMImplementation:*"), new ObjectName("*:type=MBeanServerDelegate"));
      assertNotNull(mbeans);
      if (mbeans.size() != 1) fail();
   }

   public void testSetAttribute() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());
      ObjectName name = ObjectName.getInstance("", "test", "invocation");
      ObjectInstance instance = mbsc.createMBean(Support.class.getName(), name, null);
      assertNotNull(instance);

      String attribute = "Name";
      String value = "mx4j";
      mbsc.setAttribute(name, new Attribute(attribute, value));

      String result = (String)mbsc.getAttribute(name, attribute);
      assertEquals(result, value);
   }

   public void testSetAttributes() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());
      ObjectName name = ObjectName.getInstance("", "test", "invocation");
      ObjectInstance instance = mbsc.createMBean(Support.class.getName(), name, null);
      assertNotNull(instance);

      String attribute = "Name";
      String value = "mx4j";
      AttributeList attributes = new AttributeList();
      attributes.add(new Attribute(attribute, value));
      AttributeList result = mbsc.setAttributes(name, attributes);
      assertNotNull(result);
      assertEquals(result, attributes);
   }

   public void testUnregisterMBean() throws Exception
   {
      MBeanServerConnection mbsc = getMBeanServerConnection(newMBeanServer());
      ObjectName name = ObjectName.getInstance("", "test", "invocation");
      ObjectInstance instance = mbsc.createMBean(Support.class.getName(), name, null);
      assertNotNull(instance);

      mbsc.unregisterMBean(name);
      if (mbsc.isRegistered(name)) fail();
   }

   public void testAuthenticatedInvoke() throws Exception
   {
      final String principalName = "authenticatedPrincipal";

      JMXServiceURL url = createJMXConnectorServerAddress();
      Map serverEnv = getEnvironment();
      serverEnv.put(JMXConnectorServer.AUTHENTICATOR, new JMXAuthenticator()
      {
         public Subject authenticate(Object credentials) throws SecurityException
         {
            Set principals = new HashSet();
            principals.add(new JMXPrincipal(principalName));
            return new Subject(true, principals, Collections.EMPTY_SET, Collections.EMPTY_SET);
         }
      });

      JMXConnectorServer cntorServer = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, serverEnv, newMBeanServer());
         cntorServer.start();

         Map clientEnv = getEnvironment();
         clientEnv.put(JMXConnector.CREDENTIALS, new String[0]);

         JMXConnector cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), clientEnv);
         MBeanServerConnection mbsc = cntor.getMBeanServerConnection();

         ObjectName name = ObjectName.getInstance("", "test", "invocation");
         ObjectInstance instance = mbsc.createMBean(Support.class.getName(), name, null);
         assertNotNull(instance);

         Boolean result = (Boolean)mbsc.invoke(name, "authenticated", new Object[]{principalName}, new String[]{String.class.getName()});
         assertTrue(result.booleanValue());
      }
      finally
      {
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public void testDelegatedInvoke() throws Exception
   {
      JMXServiceURL url = createJMXConnectorServerAddress();
      Map serverEnv = getEnvironment();
      serverEnv.put(JMXConnectorServer.AUTHENTICATOR, new JMXAuthenticator()
      {
         public Subject authenticate(Object credentials) throws SecurityException
         {
            Set principals = new HashSet();
            principals.add(new JMXPrincipal("authenticatedPrincipal"));
            return new Subject(true, principals, Collections.EMPTY_SET, Collections.EMPTY_SET);
         }
      });
      JMXConnectorServer cntorServer = null;
      try
      {
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, serverEnv, newMBeanServer());
         cntorServer.start();

         Map clientEnv = getEnvironment();
         clientEnv.put(JMXConnector.CREDENTIALS, new String[0]);

         JMXConnector cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), clientEnv);
         Set principals = new HashSet();
         String delegatedName = "delegatedPrincipal";
         principals.add(new JMXPrincipal(delegatedName));
         Subject delegate = new Subject(true, principals, Collections.EMPTY_SET, Collections.EMPTY_SET);
         MBeanServerConnection mbsc = cntor.getMBeanServerConnection(delegate);

         ObjectName name = ObjectName.getInstance("", "test", "invocation");
         ObjectInstance instance = mbsc.createMBean(Support.class.getName(), name, null);
         assertNotNull(instance);

         Boolean result = (Boolean)mbsc.invoke(name, "delegated", new Object[]{delegatedName}, new String[]{String.class.getName()});
         assertTrue(result.booleanValue());
      }
      finally
      {
         if (cntorServer != null) cntorServer.stop();
      }
   }

   public static class SupportException extends Exception
   {
   }

   public interface SupportMBean
   {
      public String getName();

      public void setName(String name);

      public void throwCustomException() throws SupportException;

      public void throwRuntimeException() throws IllegalArgumentException;

      public String echo(String argument);

      public boolean authenticated(String name);

      public boolean delegated(String name);
   }

   public static class Support implements SupportMBean
   {
      private String name;

      public Support()
      {
      }

      public Support(String name)
      {
         this.name = name;
      }

      public String getName()
      {
         return name;
      }

      public void setName(String name)
      {
         this.name = name;
      }

      public void throwCustomException() throws SupportException
      {
         throw new SupportException();
      }

      public void throwRuntimeException() throws IllegalArgumentException
      {
         throw new IllegalArgumentException();
      }

      public String echo(String argument)
      {
         return argument;
      }

      public boolean authenticated(String name)
      {
         Subject subject = Subject.getSubject(AccessController.getContext());
         if (subject == null) return false;
         Set principals = subject.getPrincipals();
         return principals.contains(new JMXPrincipal(name));
      }

      public boolean delegated(String name)
      {
         Subject subject = Subject.getSubject(AccessController.getContext());
         if (subject == null) return false;
         Set principals = subject.getPrincipals();
         return principals.contains(new JMXPrincipal(name));
      }
   }
}
