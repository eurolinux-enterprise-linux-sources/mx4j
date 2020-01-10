/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.tools.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Hashtable;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import mx4j.tools.config.ConfigurationException;
import mx4j.tools.config.ConfigurationLoader;
import mx4j.tools.config.DefaultConfigurationBuilder;
import test.MX4JTestCase;

/**
 * @version $Revision: 1.7 $
 */
public class ConfigurationLoaderTest extends MX4JTestCase
{
   public ConfigurationLoaderTest(String s)
   {
      super(s);
   }

   public void testMalformedXML() throws Exception
   {
      MBeanServer server = newMBeanServer();
      String config =
              "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
              "<configuration>";
      ConfigurationLoader loader = new ConfigurationLoader(server);
      try
      {
         loader.startup(new StringReader(config));
         fail();
      }
      catch (ConfigurationException x)
      {
      }
   }

   public void testUnknownElement() throws Exception
   {
      MBeanServer server = newMBeanServer();
      String config =
              "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
              "<configuration>" +
              "   <dummy />" +
              "</configuration>";
      ConfigurationLoader loader = new ConfigurationLoader(server);
      try
      {
         loader.startup(new StringReader(config));
         fail();
      }
      catch (ConfigurationException x)
      {
      }
   }

   public void testEmptyConfiguration() throws Exception
   {
      MBeanServer server = newMBeanServer();
      String config =
              "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
              "<configuration />";
      ConfigurationLoader loader = new ConfigurationLoader(server);
      loader.startup(new StringReader(config));
   }

   public void testEmptyStartup() throws Exception
   {
      MBeanServer server = newMBeanServer();
      String config =
              "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
              "<configuration>" +
              "   <startup />" +
              "</configuration>";
      ConfigurationLoader loader = new ConfigurationLoader(server);
      loader.startup(new StringReader(config));
   }

   public void testEmptyShutdown() throws Exception
   {
      MBeanServer server = newMBeanServer();
      String config =
              "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
              "<configuration>" +
              "   <shutdown />" +
              "</configuration>";
      ConfigurationLoader loader = new ConfigurationLoader(server);
      loader.startup(new StringReader(config));
   }

   public void testEmptyShutdownWithSocket() throws Exception
   {
      MBeanServer server = newMBeanServer();
      int port = 8872;
      String config =
              "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
              "<configuration port=\"" + port + "\">" +
              "   <shutdown />" +
              "</configuration>";
      ConfigurationLoader loader = new ConfigurationLoader(server);
      loader.startup(new StringReader(config));
      sleep(1000);

      // Be sure we can connect to the socket
      Socket socket = new Socket((String)null, port);
      socket.getOutputStream().write(DefaultConfigurationBuilder.SHUTDOWN_COMMAND.getBytes());
      socket.close();
      sleep(2000);
   }

   public void testEmptyStartupAndShutdown() throws Exception
   {
      MBeanServer server = newMBeanServer();
      String config =
              "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
              "<configuration>" +
              "   <startup />" +
              "   <shutdown />" +
              "</configuration>";
      ConfigurationLoader loader = new ConfigurationLoader(server);
      loader.startup(new StringReader(config));
   }

   public void testEmptyStartupAndShutdownWithSocket() throws Exception
   {
      MBeanServer server = newMBeanServer();
      int port = 8872;
      String config =
              "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
              "<configuration port=\"" + port + "\">" +
              "   <startup />" +
              "   <shutdown />" +
              "</configuration>";
      ConfigurationLoader loader = new ConfigurationLoader(server);
      loader.startup(new StringReader(config));
      sleep(1000);

      // Be sure we can connect to the socket
      Socket socket = new Socket((String)null, port);
      socket.getOutputStream().write(DefaultConfigurationBuilder.SHUTDOWN_COMMAND.getBytes());
      socket.close();
      sleep(2000);
   }

   public void testWrongShutdownCommand() throws Exception
   {
      MBeanServer server = newMBeanServer();
      int port = 8872;
      String config =
              "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
              "<configuration port=\"" + port + "\">" +
              "   <startup />" +
              "   <shutdown />" +
              "</configuration>";
      ConfigurationLoader loader = new ConfigurationLoader(server);
      loader.startup(new StringReader(config));
      sleep(1000);

      // Be sure we can connect to the socket
      Socket socket = new Socket((String)null, port);
      socket.close();

      socket = new Socket((String)null, port);
      socket.getOutputStream().write("dummy".getBytes());
      socket.close();

      socket = new Socket((String)null, port);
      socket.getOutputStream().write(DefaultConfigurationBuilder.SHUTDOWN_COMMAND.getBytes());
      socket.close();

      sleep(2000);

      try
      {
         new Socket((String)null, port);
         fail();
      }
      catch (IOException x)
      {
      }
   }

   public void testConfigurationLoaderAsMBean() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ConfigurationLoader loader = new ConfigurationLoader();
      ObjectName loaderName = ObjectName.getInstance("configuration:service=loader");
      server.registerMBean(loader, loaderName);

      int port = 8872;
      String config =
              "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
              "<configuration port=\"" + port + "\">" +
              "   <startup />" +
              "   <shutdown />" +
              "</configuration>";
      server.invoke(loaderName, "startup", new Object[]{new StringReader(config)}, new String[]{Reader.class.getName()});
      sleep(1000);

      // Be sure we can connect to the socket
      Socket socket = new Socket((String)null, port);
      socket.close();

      // Shutdown via JMX
      server.invoke(loaderName, "shutdown", null, null);

      sleep(2000);

      try
      {
         new Socket((String)null, port);
         fail();
      }
      catch (IOException x)
      {
      }
   }

   public void testCallElementStaticInvocation() throws Exception
   {
      MBeanServer server = newMBeanServer();
      String config =
              "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
              "<configuration>" +
              "  <startup>" +
              "    <call classname=\"javax.management.MBeanServerFactory\" method=\"createMBeanServer\" />" +
              "  </startup>" +
              "</configuration>";
      ConfigurationLoader loader = new ConfigurationLoader(server);
      loader.startup(new StringReader(config));
      ArrayList list = MBeanServerFactory.findMBeanServer(null);
      assertEquals(list.size(), 1);
      MBeanServerFactory.releaseMBeanServer((MBeanServer)list.get(0));
   }

   public void testCallElementWithObjectArgument() throws Exception
   {
      MBeanServer server = newMBeanServer();
      String config =
              "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
              "<configuration>" +
              "  <startup>" +
              "    <object objectid=\"list\">" +
              "      <new classname=\"java.util.ArrayList\" />" +
              "    </object>" +
              "    <call refobjectid=\"list\" method=\"add\">" +
              "      <arg type=\"object\">A String</arg>" +
              "    </call>" +
              "  </startup>" +
              "</configuration>";
      ConfigurationLoader loader = new ConfigurationLoader(server);
      loader.startup(new StringReader(config));
   }

   public void testCallElementObjectInvocation() throws Exception
   {
      MBeanServer server = newMBeanServer();
      String domain = "test";
      String config =
              "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
              "<configuration>" +
              "  <startup>" +
              "    <object objectid=\"url\">" +
              "      <new classname=\"javax.management.remote.JMXServiceURL\">" +
              "        <arg type=\"string\">rmi</arg>" +
              "        <arg type=\"string\">" + domain + "</arg>" +
              "        <arg type=\"int\">0</arg>" +
              "        <arg type=\"string\">/path</arg>" +
              "      </new>" +
              "    </object>" +
              "    <call classname=\"javax.management.MBeanServerFactory\" method=\"createMBeanServer\">" +
              "      <arg type=\"string\">" +
              "        <call refobjectid=\"url\" method=\"getHost\" />" +
              "      </arg>" +
              "    </call>" +
              "  </startup>" +
              "</configuration>";
      ConfigurationLoader loader = new ConfigurationLoader(server);
      loader.startup(new StringReader(config));

      ArrayList list = MBeanServerFactory.findMBeanServer(null);
      assertEquals(list.size(), 1);
      MBeanServer mbs = (MBeanServer)list.get(0);
      assertEquals(mbs.getDefaultDomain(), domain);
      MBeanServerFactory.releaseMBeanServer(mbs);
   }

   public void testCallElementMBeanServerGetAttribute() throws Exception
   {
      MBeanServer server = newMBeanServer();
      String name = "JMImplementation:type=MBeanServerDelegate";
      String config =
              "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
              "<configuration>" +
              "  <startup>" +
              "    <call classname=\"javax.management.MBeanServerFactory\" method=\"createMBeanServer\">" +
              "      <arg type=\"string\">" +
              "        <call objectname=\"" + name + "\" attribute=\"MBeanServerId\" />" +
              "      </arg>" +
              "    </call>" +
              "  </startup>" +
              "</configuration>" +
              "";
      ConfigurationLoader loader = new ConfigurationLoader(server);
      Reader reader = new BufferedReader(new StringReader(config));
      loader.startup(reader);

      ArrayList list = MBeanServerFactory.findMBeanServer(null);
      assertEquals(list.size(), 1);
      MBeanServer mbs = (MBeanServer)list.get(0);
      String id = (String)server.getAttribute(new ObjectName(name), "MBeanServerId");
      assertEquals(id, mbs.getDefaultDomain());
      MBeanServerFactory.releaseMBeanServer(mbs);
   }

   public void testCallElementMBeanServerOperation() throws Exception
   {
      MBeanServer server = newMBeanServer();
      int shutdownPort = 8872;
      String name = "naming:type=rmiregistry";
      String config =
              "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
              "<configuration port=\"" + shutdownPort + "\">" +
              "  <startup>" +
              "    <create objectname=\"" + name + "\" classname=\"mx4j.tools.naming.NamingService\">" +
              "      <arg type=\"int\">1099</arg>" +
              "    </create>" +
              "    <call objectname=\"" + name + "\" operation=\"start\" />" +
              "  </startup>" +
              "  <shutdown>" +
              "    <call objectname=\"" + name + "\" operation=\"stop\" />" +
              "  </shutdown>" +
              "</configuration>" +
              "";
      ConfigurationLoader loader = new ConfigurationLoader(server);
      Reader reader = new BufferedReader(new StringReader(config));
      loader.startup(reader);

      Hashtable env = new Hashtable();
      env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
      env.put(Context.PROVIDER_URL, "rmi://localhost");
      InitialContext ctx = new InitialContext(env);
      ctx.list("");
      sleep(1000);

      Socket socket = new Socket((String)null, shutdownPort);
      socket.getOutputStream().write(DefaultConfigurationBuilder.SHUTDOWN_COMMAND.getBytes());
      socket.close();

      sleep(2000);

      try
      {
         ctx.list("");
         fail();
      }
      catch (NamingException x)
      {
      }
   }

   public void testCallElementMBeanSetAttribute() throws Exception
   {
      MBeanServer server = newMBeanServer();
      int shutdownPort = 8872;
      String name = "naming:type=rmiregistry";
      String port = "1199";
      String config =
              "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
              "<configuration port=\"" + shutdownPort + "\">" +
              "  <startup>" +
              "    <create objectname=\"" + name + "\" classname=\"mx4j.tools.naming.NamingService\" />" +
              "    <call objectname=\"" + name + "\" attribute=\"Port\">" +
              "      <arg type=\"int\">" + port + "</arg>" +
              "    </call>" +
              "    <call objectname=\"" + name + "\" operation=\"start\" />" +
              "  </startup>" +
              "  <shutdown>" +
              "    <call objectname=\"" + name + "\" operation=\"stop\" />" +
              "  </shutdown>" +
              "</configuration>" +
              "";
      ConfigurationLoader loader = new ConfigurationLoader(server);
      Reader reader = new BufferedReader(new StringReader(config));
      loader.startup(reader);

      Hashtable env = new Hashtable();
      env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.rmi.registry.RegistryContextFactory");
      env.put(Context.PROVIDER_URL, "rmi://localhost:" + port);
      InitialContext ctx = new InitialContext(env);
      ctx.list("");
      sleep(1000);

      Socket socket = new Socket((String)null, shutdownPort);
      socket.getOutputStream().write(DefaultConfigurationBuilder.SHUTDOWN_COMMAND.getBytes());
      socket.close();

      sleep(2000);

      try
      {
         ctx.list("");
         fail();
      }
      catch (NamingException x)
      {
      }
   }

   public void testRegisterUnregisterElements() throws Exception
   {
      MBeanServer server = newMBeanServer();
      int port = 8872;
      String name = "connectors:protocol=rmi";
      String config =
              "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
              "<configuration port=\"" + port + "\">" +
              "  <startup>" +
              "    <register objectname=\"" + name + "\">" +
              "      <call method=\"newJMXConnectorServer\" classname=\"javax.management.remote.JMXConnectorServerFactory\">" +
              "        <arg type=\"javax.management.remote.JMXServiceURL\">service:jmx:rmi://localhost</arg>" +
              "        <arg type=\"java.util.Map\" />" +
              "        <arg type=\"javax.management.MBeanServer\" />" +
              "      </call>" +
              "    </register>" +
              "  </startup>" +
              "  <shutdown>" +
              "    <unregister objectname=\"" + name + "\" />" +
              "  </shutdown>" +
              "</configuration>" +
              "";
      ConfigurationLoader loader = new ConfigurationLoader(server);
      Reader reader = new BufferedReader(new StringReader(config));
      loader.startup(reader);
      sleep(1000);

      assertTrue(server.isRegistered(new ObjectName(name)));

      Socket socket = new Socket((String)null, port);
      socket.getOutputStream().write(DefaultConfigurationBuilder.SHUTDOWN_COMMAND.getBytes());
      socket.close();

      sleep(2000);

      assertTrue(!server.isRegistered(new ObjectName(name)));
   }

   public void testObjectElement() throws Exception
   {
      MBeanServer server = newMBeanServer();
      int port = 8872;
      String id = "connector";
      String name = "connectors:protocol=rmi";
      String config =
              "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>" +
              "<configuration port=\"" + port + "\">" +
              "  <startup>" +
              "    <object objectid=\"" + id + "\">" +
              "      <call method=\"newJMXConnectorServer\" classname=\"javax.management.remote.JMXConnectorServerFactory\">" +
              "        <arg type=\"javax.management.remote.JMXServiceURL\">service:jmx:rmi://localhost</arg>" +
              "        <arg type=\"java.util.Map\" />" +
              "        <arg type=\"javax.management.MBeanServer\" />" +
              "      </call>" +
              "    </object>" +
              "    <register objectname=\"" + name + "\">" +
              "      <arg type=\"java.lang.Object\" refobjectid=\"" + id + "\" />" +
              "    </register>" +
              "    <call refobjectid=\"" + id + "\" method=\"start\" />" +
              "  </startup>" +
              "  <shutdown>" +
              "    <call refobjectid=\"" + id + "\" method=\"stop\" />" +
              "    <unregister objectname=\"" + name + "\" />" +
              "  </shutdown>" +
              "</configuration>" +
              "";
      ConfigurationLoader loader = new ConfigurationLoader(server);
      Reader reader = new BufferedReader(new StringReader(config));
      loader.startup(reader);
      sleep(1000);

      assertTrue(server.isRegistered(new ObjectName(name)));
      Boolean active = (Boolean)server.getAttribute(new ObjectName(name), "Active");
      assertTrue(active.booleanValue());

      Socket socket = new Socket((String)null, port);
      socket.getOutputStream().write(DefaultConfigurationBuilder.SHUTDOWN_COMMAND.getBytes());
      socket.close();

      sleep(2000);


      assertTrue(!server.isRegistered(new ObjectName(name)));
   }
}
