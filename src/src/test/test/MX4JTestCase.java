/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import junit.framework.TestCase;

/**
 * Base class for MX4J tests
 *
 * @version $Revision: 1.11 $
 */
public class MX4JTestCase extends TestCase
{
   public MX4JTestCase(String name)
   {
      super(name);
   }

   protected MBeanServer newMBeanServer()
   {
      return MBeanServerFactory.newMBeanServer();
   }

   protected ClassLoader createMX4JClassLoader() throws MalformedURLException
   {
      File jmx = new File("dist/test/mx4j-jmx.jar");
      File impl = new File("dist/test/mx4j-impl.jar");
      return new URLClassLoader(new URL[]{jmx.toURL(), impl.toURL()}, getClass().getClassLoader().getParent());
   }

   protected ClassLoader createJMXRIClassLoader() throws MalformedURLException
   {
      File jmxri = new File("dist/test/jmxri.jar");
      if (!jmxri.exists()) fail("JMXRI jar is not available");
      return new URLClassLoader(new URL[]{jmxri.toURL()}, getClass().getClassLoader().getParent());
   }

   protected ClassLoader createJMXRIWithMX4JImplClassLoader() throws MalformedURLException
   {
      File jmxri = new File("dist/test/jmxri.jar");
      if (!jmxri.exists()) fail("JMXRI jar is not available");
      File mx4j = new File("dist/test/mx4j-impl.jar");
      return new URLClassLoader(new URL[]{jmxri.toURL(), mx4j.toURL()}, getClass().getClassLoader().getParent());
   }

   protected ClassLoader createMX4JWithTestsClassLoader() throws MalformedURLException
   {
      File jmx = new File("dist/test/mx4j-jmx.jar");
      File impl = new File("dist/test/mx4j-impl.jar");
      File tests = new File("dist/test/mx4j-tests.jar");
      return new URLClassLoader(new URL[]{jmx.toURL(), impl.toURL(), tests.toURL()}, getClass().getClassLoader().getParent());
   }

   protected ClassLoader createRemoteMX4JWithTestsClassLoader() throws MalformedURLException
   {
      File jmx = new File("dist/test/mx4j-jmx.jar");
      File impl = new File("dist/test/mx4j-impl.jar");
      File rjmx = new File("dist/test/mx4j-rjmx.jar");
      File rimpl = new File("dist/test/mx4j-rimpl.jar");
      File tests = new File("dist/test/mx4j-tests.jar");
      File jaas = new File("dist/test/jaas.jar");
      return new URLClassLoader(new URL[]{jaas.toURL(), jmx.toURL(), impl.toURL(), rjmx.toURL(), rimpl.toURL(), tests.toURL()}, getClass().getClassLoader().getParent());
   }

   protected ClassLoader createJMXRIWithTestsClassLoader() throws MalformedURLException
   {
      File jmxri = new File("dist/test/jmxri.jar");
      if (!jmxri.exists()) fail("JMXRI jar is not available");
      File tests = new File("dist/test/mx4j-tests.jar");
      return new URLClassLoader(new URL[]{jmxri.toURL(), tests.toURL()}, getClass().getClassLoader().getParent());
   }

   protected ClassLoader createRemoteJMXRIWithTestsClassLoader() throws MalformedURLException
   {
      File jmx = new File("dist/test/jmxri.jar");
      if (!jmx.exists()) fail("JMXRI jar is not available");
      File rjmx = new File("dist/test/jmxremote.jar");
      if (!rjmx.exists()) fail("JMX Remote jar is not available");
      File tests = new File("dist/test/mx4j-tests.jar");
      File jaas = new File("dist/test/jaas.jar");
      return new URLClassLoader(new URL[]{jaas.toURL(), jmx.toURL(), rjmx.toURL(), tests.toURL()}, getClass().getClassLoader().getParent());
   }

   protected ClassLoader createOptionalRemoteJMXRIWithTestsClassLoader() throws MalformedURLException
   {
      File jmx = new File("dist/test/jmxri.jar");
      if (!jmx.exists()) fail("JMXRI jar is not available");
      File rjmx = new File("dist/test/jmxremote.jar");
      if (!rjmx.exists()) fail("JMX Remote jar is not available");
      File orjmx = new File("dist/test/jmxremote_optional.jar");
      if (!orjmx.exists()) fail("JMX Optional Remote jar is not available");
      File tests = new File("dist/test/mx4j-tests.jar");
      return new URLClassLoader(new URL[]{jmx.toURL(), rjmx.toURL(), orjmx.toURL(), tests.toURL()}, getClass().getClassLoader().getParent());
   }

   protected void sleep(long time)
   {
      try
      {
         Thread.sleep(time);
      }
      catch (InterruptedException x)
      {
         Thread.currentThread().interrupt();
      }
   }
}
