/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote.compliance;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.jar.JarFile;

import test.javax.management.compliance.ComplianceTestCase;

/**
 * @version $Revision: 1.4 $
 */
public abstract class OptionalRemoteJMXComplianceTestCase extends ComplianceTestCase
{
   public OptionalRemoteJMXComplianceTestCase(String s)
   {
      super(s);
   }

   protected ClassLoader createClassLoader() throws MalformedURLException
   {
      return createOptionalRemoteJMXRIWithTestsClassLoader();
   }

   protected JarFile loadJar() throws IOException
   {
      File rjmx = new File("dist/test/jmxremote_optional.jar");
      if (!rjmx.exists()) fail("JMX Optional Remote jar is not available");
      JarFile jar = new JarFile(rjmx);
      return jar;
   }

   public void test_remote_generic_ConnectionClosedException() throws Exception
   {
      check("remote.generic.ConnectionClosedException");
   }

   public void test_remote_generic_GenericConnector() throws Exception
   {
      check("remote.generic.GenericConnector");
   }

   public void test_remote_generic_GenericConnectorServer() throws Exception
   {
      check("remote.generic.GenericConnectorServer");
   }

   public void test_remote_generic_MessageConnection() throws Exception
   {
      check("remote.generic.MessageConnection");
   }

   public void test_remote_generic_MessageConnectionServer() throws Exception
   {
      check("remote.generic.MessageConnectionServer");
   }

   public void test_remote_generic_ObjectWrapping() throws Exception
   {
      check("remote.generic.ObjectWrapping");
   }

   public void test_remote_jmxmp_JMXMPConnector() throws Exception
   {
      check("remote.jmxmp.JMXMPConnector");
   }

   public void test_remote_jmxmp_JMXMPConnectorServer() throws Exception
   {
      check("remote.jmxmp.JMXMPConnectorServer");
   }

   public void test_remote_message_CloseMessage() throws Exception
   {
      check("remote.message.CloseMessage");
   }

   public void test_remote_message_HandshakeBeginMessage() throws Exception
   {
      check("remote.message.HandshakeBeginMessage");
   }

   public void test_remote_message_HandshakeEndMessage() throws Exception
   {
      check("remote.message.HandshakeEndMessage");
   }

   public void test_remote_message_HandshakeErrorMessage() throws Exception
   {
      check("remote.message.HandshakeErrorMessage");
   }

   public void test_remote_message_JMXMPMessage() throws Exception
   {
      check("remote.message.JMXMPMessage");
   }

   public void test_remote_message_MBeanServerRequestMessage() throws Exception
   {
      check("remote.message.MBeanServerRequestMessage");
   }

   public void test_remote_message_MBeanServerResponseMessage() throws Exception
   {
      check("remote.message.MBeanServerResponseMessage");
   }

   public void test_remote_message_Message() throws Exception
   {
      check("remote.message.Message");
   }

   public void test_remote_message_NotificationRequestMessage() throws Exception
   {
      check("remote.message.NotificationRequestMessage");
   }

   public void test_remote_message_NotificationResponseMessage() throws Exception
   {
      check("remote.message.NotificationResponseMessage");
   }

   public void test_remote_message_ProfileMessage() throws Exception
   {
      check("remote.message.ProfileMessage");
   }

   public void test_remote_message_SASLMessage() throws Exception
   {
      check("remote.message.SASLMessage");
   }

   public void test_remote_message_TLSMessage() throws Exception
   {
      check("remote.message.TLSMessage");
   }
}
