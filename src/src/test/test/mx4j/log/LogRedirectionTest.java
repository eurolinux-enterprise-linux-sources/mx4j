/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.log;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.StringReader;
import java.net.URL;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.MBeanServerInvocationHandler;
import javax.management.Notification;
import javax.management.NotificationListener;
import javax.management.ObjectName;

import mx4j.log.Log;
import mx4j.log.Log4JLogger;
import mx4j.log.Logger;
import mx4j.log.LoggerBroadcasterMBean;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.PatternLayout;
import test.MX4JTestCase;
import test.MutableInteger;

/**
 * @version $Revision: 1.9 $
 * @see
 */
public class LogRedirectionTest extends MX4JTestCase
{
   private int m_defaultPriority;

   public LogRedirectionTest(String s)
   {
      super(s);
   }

   protected void setUp() throws Exception
   {
      m_defaultPriority = Log.getDefaultPriority();
      Log.setDefaultPriority(Logger.TRACE);
   }

   protected void tearDown() throws Exception
   {
      Log.setDefaultPriority(m_defaultPriority);
   }

   public void testDirectRedirection() throws Exception
   {
      PrintStream out = System.out;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try
      {
         // Replace System.out be be able to check results
         System.setOut(new PrintStream(baos));

         // Redirect logging
         Log.redirectTo(new LoggerTestRedirector());

         // Create some log
         MBeanServer server = newMBeanServer();
         server.createMBean("javax.management.loading.MLet", new ObjectName(":type=test"), null);
      }
      finally
      {
         // Stop redirection
         Log.redirectTo(null);
// Re-set normal System.out
         System.setOut(out);
      }

      // Collect and test results
      byte[] bytes = baos.toByteArray();
      String log = new String(bytes);
      BufferedReader br = new BufferedReader(new StringReader(log));
      String line = null;
      while ((line = br.readLine()) != null)
      {
         if (!line.startsWith("{")) fail("Redirection failed");
      }
   }

   public void testMBeanRedirection() throws Exception
   {
      MBeanServer server = newMBeanServer();
      ObjectName name = new ObjectName(":type=test");
      // Register a logger mbean
      server.createMBean("mx4j.log.LoggerBroadcaster", name, null);
      LoggerBroadcasterMBean redirector = (LoggerBroadcasterMBean)MBeanServerInvocationHandler.newProxyInstance(server, name, LoggerBroadcasterMBean.class, false);

      try
      {
         // Register a notification listener
         final MutableInteger notified = new MutableInteger(0);
         NotificationListener listener = new NotificationListener()
         {
            public void handleNotification(Notification notification, Object handback)
            {
               notified.set(notified.get() + 1);
            }
         };

         server.addNotificationListener(name, listener, null, null);

         // Redirect logging
         redirector.start();

         // Create some log
         ObjectName mlet = new ObjectName(":type=mlet");
         server.createMBean("javax.management.loading.MLet", mlet, null);
         // This should create some log
         server.invoke(mlet, "addURL", new Object[]{new URL("http://mx4j.sourceforge.net")}, new String[]{"java.net.URL"});

         // Test if redirection worked
         if (notified.get() < 1) fail("Notification listener not called");
      }
      finally
      {
         redirector.stop();
      }
   }

   public void testLog4JRedirection() throws Exception
   {
      PrintStream out = System.out;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();
      try
      {
         // Replace System.out be be able to check results
         System.setOut(new PrintStream(baos));

         org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();
         root.removeAllAppenders();
         ConsoleAppender console = new ConsoleAppender(new PatternLayout("[%c] %p: %m%n"));
         root.addAppender(console);
         Log.redirectTo(new Log4JLogger());

         // Create some log
         newMBeanServer();
      }
      finally
      {
         // Stop redirection
         Log.redirectTo(null);
// Re-set normal System.out
         System.setOut(out);

         org.apache.log4j.Logger.getRoot().removeAllAppenders();
      }

      // Collect and test results
      byte[] bytes = baos.toByteArray();
      String log = new String(bytes);
      BufferedReader br = new BufferedReader(new StringReader(log));
      String line = null;
      while ((line = br.readLine()) != null)
      {
         if (!line.startsWith("["))
         {
            fail("Redirection failed");
         }
      }
   }

   public void testPartialDirectRedirection() throws Exception
   {
      PrintStream out = System.out;
      ByteArrayOutputStream baos = new ByteArrayOutputStream();

      String category = MBeanServerFactory.class.getName();
      try
      {
         // Replace System.out be be able to check results
         System.setOut(new PrintStream(baos));

         org.apache.log4j.Logger root = org.apache.log4j.Logger.getRootLogger();
         root.removeAllAppenders();
         ConsoleAppender console = new ConsoleAppender(new PatternLayout("[%c] %p: %m%n"));
         root.addAppender(console);
         Log.redirectTo(new Log4JLogger(), category);
         Log.redirectTo(new LoggerTestRedirector());

         // Create some log for log4j redirector
         MBeanServer server = newMBeanServer();

         // Create some log for the test redirector
         ObjectName mlet = new ObjectName(":type=mlet");
         server.createMBean("javax.management.loading.MLet", mlet, null);
         // This should create one info log
         server.invoke(mlet, "addURL", new Object[]{new URL("http://mx4j.sourceforge.net")}, new String[]{"java.net.URL"});
      }
      finally
      {
         // Stop redirection
         Log.redirectTo(null);
         Log.redirectTo(null, category);
// Re-set normal System.out
         System.setOut(out);

         org.apache.log4j.Logger.getRootLogger().removeAllAppenders();
      }

      // Collect and test results
      byte[] bytes = baos.toByteArray();
      String log = new String(bytes);
      BufferedReader br = new BufferedReader(new StringReader(log));
      String line = null;
      boolean bracketFound = false, braceFound = false;
      while ((line = br.readLine()) != null)
      {
         System.out.println(line);
         if (line.startsWith("["))
         {
            bracketFound = true;
         }
         else if (line.startsWith("{"))
         {
            braceFound = true;
         }
         else
         {
            fail("Redirection failed");
         }
      }
      if (!bracketFound || !braceFound)
      {
         fail("Redirection failed");
      }
   }

   public static class LoggerTestRedirector extends Logger
   {
      public LoggerTestRedirector()
      {
         super.setPriority(Logger.TRACE);
      }

      public void setPriority(int priority)
      {
         // Ignore the default priority set by the internal logging system
         // Here we want to trace
      }

      protected void log(int priority, Object message, Throwable t)
      {
         String msg = message == null ? "" : message.toString();
         StringBuffer b = new StringBuffer("{").append(getCategory()).append("} ").append(msg);
         super.log(priority, b, t);
      }
   }
}
