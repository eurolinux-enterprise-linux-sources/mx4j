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
public abstract class RemoteJMXComplianceTestCase extends ComplianceTestCase
{
   public RemoteJMXComplianceTestCase(String s)
   {
      super(s);
   }

   protected ClassLoader createClassLoader() throws MalformedURLException
   {
      return createRemoteJMXRIWithTestsClassLoader();
   }

   protected JarFile loadJar() throws IOException
   {
      File rjmx = new File("dist/test/jmxremote.jar");
      if (!rjmx.exists()) fail("JMX Remote jar is not available");
      JarFile jar = new JarFile(rjmx);
      return jar;
   }

   public void test_remote_JMXAuthenticator() throws Exception
   {
      check("remote.JMXAuthenticator");
   }

   public void test_remote_JMXConnectionNotification() throws Exception
   {
      check("remote.JMXConnectionNotification");
   }

   public void test_remote_JMXConnector() throws Exception
   {
      check("remote.JMXConnector");
   }

   public void test_remote_JMXConnectorFactory() throws Exception
   {
      check("remote.JMXConnectorFactory");
   }

   public void test_remote_JMXConnectorProvider() throws Exception
   {
      check("remote.JMXConnectorProvider");
   }

   public void test_remote_JMXConnectorServer() throws Exception
   {
      check("remote.JMXConnectorServer");
   }

   public void test_remote_JMXConnectorServerFactory() throws Exception
   {
      check("remote.JMXConnectorServerFactory");
   }

   public void test_remote_JMXConnectorServerMBean() throws Exception
   {
      check("remote.JMXConnectorServerMBean");
   }

   public void test_remote_JMXConnectorServerProvider() throws Exception
   {
      check("remote.JMXConnectorServerProvider");
   }

   public void test_remote_JMXPrincipal() throws Exception
   {
      check("remote.JMXPrincipal");
   }

   public void test_remote_JMXProviderException() throws Exception
   {
      check("remote.JMXProviderException");
   }

   public void test_remote_JMXServerErrorException() throws Exception
   {
      check("remote.JMXServerErrorException");
   }

   public void test_remote_JMXServiceURL() throws Exception
   {
      check("remote.JMXServiceURL");
   }

   public void test_remote_MBeanServerForwarder() throws Exception
   {
      check("remote.MBeanServerForwarder");
   }

   public void test_remote_NotificationResult() throws Exception
   {
      check("remote.NotificationResult");
   }

   public void test_remote_SubjectDelegationPermission() throws Exception
   {
      check("remote.SubjectDelegationPermission");
   }

   public void test_remote_TargetedNotification() throws Exception
   {
      check("remote.TargetedNotification");
   }

   public void test_remote_rmi_RMIConnection() throws Exception
   {
      check("remote.rmi.RMIConnection");
   }

   public void test_remote_rmi_RMIConnectionImpl() throws Exception
   {
      check("remote.rmi.RMIConnectionImpl");
   }

   public void test_remote_rmi_RMIConnector() throws Exception
   {
      check("remote.rmi.RMIConnector");
   }

   public void test_remote_rmi_RMIConnectorServer() throws Exception
   {
      check("remote.rmi.RMIConnectorServer");
   }

   public void test_remote_rmi_RMIIIOPServerImpl() throws Exception
   {
      check("remote.rmi.RMIIIOPServerImpl");
   }

   public void test_remote_rmi_RMIJRMPServerImpl() throws Exception
   {
      check("remote.rmi.RMIJRMPServerImpl");
   }

   public void test_remote_rmi_RMIServer() throws Exception
   {
      check("remote.rmi.RMIServer");
   }

   public void test_remote_rmi_RMIServerImpl() throws Exception
   {
      check("remote.rmi.RMIServerImpl");
   }
}
