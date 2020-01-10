/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote;

import java.net.MalformedURLException;
import javax.management.remote.JMXServiceURL;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.9 $
 */
public class JMXServiceURLTest extends TestCase
{
   public JMXServiceURLTest(String s)
   {
      super(s);
   }

   public void testInvalidJMXServiceURLNull() throws Exception
   {
      try
      {
         new JMXServiceURL(null);
      }
      catch (NullPointerException x)
      {
      }
   }

   public void testInvalidJMXServiceURLEmpty() throws Exception
   {
      try
      {
         new JMXServiceURL("");
      }
      catch (MalformedURLException e)
      {
      }
   }

   public void testInvalidJMXServiceURLWhiteSpace() throws Exception
   {
      try
      {
         new JMXServiceURL(" ");
      }
      catch (MalformedURLException e)
      {
      }
   }

   public void testInvalidJMXServiceURLNoServiceJMX() throws Exception
   {
      try
      {
         // No service:jmx
         new JMXServiceURL("dummy");
      }
      catch (MalformedURLException e)
      {
      }
   }

   public void testInvalidJMXServiceURLNoProtocol1() throws Exception
   {
      try
      {
         // No protocol
         new JMXServiceURL("service:jmx: ");
      }
      catch (MalformedURLException e)
      {
      }
   }

   public void testInvalidJMXServiceURLNoProtocol2() throws Exception
   {
      try
      {
         // No protocol
         new JMXServiceURL("service:jmx: :// ");
      }
      catch (MalformedURLException e)
      {
      }
   }

   public void testInvalidJMXServiceURLNoHost() throws Exception
   {
      try
      {
         // No host
         new JMXServiceURL("service:jmx:rmi:// ");
      }
      catch (MalformedURLException e)
      {
      }
   }

   public void testInvalidJMXServiceURLBadPort() throws Exception
   {
      try
      {
         // No host
         new JMXServiceURL("service:jmx:rmi://host:port");
      }
      catch (MalformedURLException e)
      {
      }
   }

   public void testInvalidJMXServiceURLWrongPath() throws Exception
   {
      try
      {
         // Wrong path
         new JMXServiceURL("service:jmx:rmi://host//path");
      }
      catch (MalformedURLException e)
      {
      }
   }

   public void testValidJMXServiceURLProtocolOnly() throws Exception
   {
      String proto = "rmi";
      JMXServiceURL url = new JMXServiceURL("service:jmx:" + proto + "://");
      assertEquals(proto, url.getProtocol());
   }

   public void testValidJMXServiceURLProtocolHost() throws Exception
   {
      String proto = "rmi";
      String host = "host";

      JMXServiceURL url = new JMXServiceURL("service:jmx:" + proto + "://" + host);
      assertEquals(proto, url.getProtocol());
      assertEquals(host, url.getHost());
      assertEquals(0, url.getPort());
      assertEquals("", url.getURLPath());
   }

   public void testValidJMXServiceURLProtocolHostPort() throws Exception
   {
      String proto = "rmi";
      String host = "host";
      int port = 1099;

      JMXServiceURL url = new JMXServiceURL("service:jmx:" + proto + "://" + host + ":" + port);
      assertEquals(proto, url.getProtocol());
      assertEquals(host, url.getHost());
      assertEquals(port, url.getPort());
      assertEquals("", url.getURLPath());
   }

   public void testValidJMXServiceURLProtocolHostPortPath() throws Exception
   {
      String proto = "rmi";
      String host = "host";
      int port = 1099;
      String path = "/path";

      JMXServiceURL url = new JMXServiceURL("service:jmx:" + proto + "://" + host + ":" + port + path);
      assertEquals(proto, url.getProtocol());
      assertEquals(host, url.getHost());
      assertEquals(port, url.getPort());
      assertEquals(path, url.getURLPath());
   }

   public void testValidJMXServiceURLCaseNotSignificant() throws Exception
   {
      JMXServiceURL reference = new JMXServiceURL("service:jmx:rmi://");
      JMXServiceURL url = new JMXServiceURL("SERVICE:JMX:RMI://");
      assertEquals(url, reference);

      url = new JMXServiceURL("SERVICE:JMX:rmi://");
      assertEquals(url, reference);

      url = new JMXServiceURL("Service:JMX:rmi://");
      assertEquals(url, reference);

      url = new JMXServiceURL("service:JMX:rmi://");
      assertEquals(url, reference);

      url = new JMXServiceURL("service:Jmx:RMI://");
      assertEquals(url, reference);

      url = new JMXServiceURL("service:Jmx:rmi://");
      assertEquals(url, reference);
   }

   public void testDifferentConstructorsYieldEqualJMXServiceURL() throws Exception
   {
      JMXServiceURL one = new JMXServiceURL("service:jmx:rmi://");
      JMXServiceURL two = new JMXServiceURL("rmi", null, 0, null);
      assertEquals(one, two);
      assertEquals(one.hashCode(), two.hashCode());
      assertEquals(one.getURLPath(), two.getURLPath());

      one = new JMXServiceURL("service:jmx:rmi://myhost");
      two = new JMXServiceURL("rmi", "myhost", 0, null);
      assertEquals(one, two);
      assertEquals(one.hashCode(), two.hashCode());
      assertEquals(one.getURLPath(), two.getURLPath());

      one = new JMXServiceURL("service:jmx:rmi://myhost/");
      two = new JMXServiceURL("rmi", "myhost", 0, null);
      assertEquals(one, two);
      assertEquals(one.hashCode(), two.hashCode());
      assertEquals(one.getURLPath(), two.getURLPath());

      one = new JMXServiceURL("service:jmx:rmi://myhost/mypath");
      two = new JMXServiceURL("rmi", "myhost", 0, "mypath");
      assertEquals(one, two);
      assertEquals(one.hashCode(), two.hashCode());
      assertEquals(one.getURLPath(), two.getURLPath());

      one = new JMXServiceURL("service:jmx:rmi://myhost/mypath");
      two = new JMXServiceURL("rmi", "myhost", 0, "/mypath");
      assertEquals(one, two);
      assertEquals(one.hashCode(), two.hashCode());
      assertEquals(one.getURLPath(), two.getURLPath());
   }
}
