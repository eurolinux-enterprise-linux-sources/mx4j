/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote.rmi;

import javax.management.MBeanServerConnection;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXConnectorServer;
import javax.management.remote.JMXConnectorServerFactory;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnector;
import javax.management.remote.rmi.RMIServer;

import test.javax.management.remote.JMXConnectorTestCase;

/**
 * @version $Revision: 1.13 $
 */
public abstract class RMIConnectorTestCase extends JMXConnectorTestCase implements RMITestCase
{
   public RMIConnectorTestCase(String s)
   {
      super(s);
   }

   public void testNewRMIConnectorNullURL() throws Exception
   {
      try
      {
         new RMIConnector((JMXServiceURL)null, null);
         fail();
      }
      catch (IllegalArgumentException x)
      {
      }
   }

   public void testNewRMIConnectorNullRMIServer() throws Exception
   {
      try
      {
         new RMIConnector((RMIServer)null, null);
         fail();
      }
      catch (IllegalArgumentException x)
      {
      }
   }

   public void testJNDILookupWithRelativePath() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         startNaming();

         JMXServiceURL url = createJMXConnectorServerAddress();
         url = new JMXServiceURL(url.getProtocol(), url.getHost(), url.getPort(), "/jndi/jmx");
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());
         cntorServer.start();

         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());
         MBeanServerConnection mbsc = cntor.getMBeanServerConnection();
         mbsc.getDefaultDomain();
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
         stopNaming();
      }
   }

   public void testJNDILookupWithAbsolutePath() throws Exception
   {
      JMXConnectorServer cntorServer = null;
      JMXConnector cntor = null;
      try
      {
         startNaming();

         JMXServiceURL url = createJMXConnectorServerAddress();
         url = new JMXServiceURL(url.getProtocol(), url.getHost(), url.getPort(), "/jndi/" + url.getProtocol() + "://localhost:" + getNamingPort() + "/jmx");
         cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(url, getEnvironment(), newMBeanServer());
         cntorServer.start();

         cntor = JMXConnectorFactory.connect(cntorServer.getAddress(), getEnvironment());
         MBeanServerConnection mbsc = cntor.getMBeanServerConnection();
         mbsc.getDefaultDomain();
      }
      finally
      {
         if (cntor != null) cntor.close();
         if (cntorServer != null) cntorServer.stop();
         stopNaming();
      }
   }
}
