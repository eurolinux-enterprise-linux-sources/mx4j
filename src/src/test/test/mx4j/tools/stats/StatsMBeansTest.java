/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.tools.stats;

import java.util.Date;
import java.util.SortedMap;
import javax.management.Attribute;
import javax.management.AttributeChangeNotification;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectName;

import junit.framework.TestCase;
import mx4j.tools.stats.PointTime;

/**
 * Test the Statistics MBeans
 *
 * @version $Revision: 1.4 $
 */
public class StatsMBeansTest extends TestCase
{
   private MBeanServer server;

   /**
    * Constructor requested by the JUnit framework
    */
   public StatsMBeansTest()
   {
      super("StatsMBeansTest Test");
   }

   /**
    * Constructor requested by the JUnit framework
    */
   public StatsMBeansTest(String name)
   {
      super(name);
   }

   public void setUp()
   {
      try
      {
         server = MBeanServerFactory.createMBeanServer("Stats");
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public void tearDown()
   {
      try
      {
         MBeanServerFactory.releaseMBeanServer(server);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public void testValueMBean() throws Exception
   {
      ObjectName name = new ObjectName("Domain:name=value");
      try
      {
         server.createMBean("mx4j.tools.stats.ValueStatisticsRecorder", name, null);

         // Test that nothing happens if not started

         //Every time the value is set the statistics are updated
         server.setAttribute(name, new Attribute("Value", new Double(10)));
         server.setAttribute(name, new Attribute("Value", new Double(5)));
         server.setAttribute(name, new Attribute("Value", new Double(20)));

         assertEquals(new Long(0), server.getAttribute(name, "Max"));
         assertEquals(new Long(0), server.getAttribute(name, "Min"));
         assertEquals(new Long(0), server.getAttribute(name, "Average"));
         assertNull(server.getAttribute(name, "RecordingStart"));
         assertEquals(0, ((SortedMap)server.getAttribute(name, "Entries")).size());

         // start it
         server.invoke(name, "start", null, null);

         //Every time the value is set the statistics are updated
         server.setAttribute(name, new Attribute("Value", new Double(10)));
         server.setAttribute(name, new Attribute("Value", new Double(5)));
         server.setAttribute(name, new Attribute("Value", new Double(20)));

         assertEquals(new Double(20), server.getAttribute(name, "Max"));
         assertEquals(new Double(5), server.getAttribute(name, "Min"));
         assertEquals(11.6, ((Double)server.getAttribute(name, "Average")).doubleValue(), 0.1);
         assertEquals(3, ((SortedMap)server.getAttribute(name, "Entries")).size());
         Date start1 = (Date)server.getAttribute(name, "RecordingStart");

         // start it
         server.invoke(name, "stop", null, null);
         server.invoke(name, "start", null, null);

         // Restart with longs
         server.setAttribute(name, new Attribute("Value", new Long(10)));
         server.setAttribute(name, new Attribute("Value", new Long(5)));
         server.setAttribute(name, new Attribute("Value", new Long(20)));

         assertEquals(new Long(20), server.getAttribute(name, "Max"));
         assertEquals(new Long(5), server.getAttribute(name, "Min"));
         assertEquals(11, ((Long)server.getAttribute(name, "Average")).longValue());
         assertEquals(((SortedMap)server.getAttribute(name, "Entries")).size(), 3);
         Date start2 = (Date)server.getAttribute(name, "RecordingStart");

         assertTrue(start1.equals(start2) || start1.before(start2));
         SortedMap values = (SortedMap)server.getAttribute(name, "Entries");
         PointTime point1 = (PointTime)values.firstKey();
         PointTime point2 = (PointTime)values.lastKey();
         assertTrue(point1.getDate().equals(point2.getDate()) || point1.getDate().before(point2.getDate()));

         // re start it
         server.invoke(name, "stop", null, null);
         server.invoke(name, "start", null, null);

         int maxSize = ((Integer)server.getAttribute(name, "MaxEntries")).intValue();
         // set some random number
         for (int i = 0; i < maxSize * 2; i++)
         {
            server.setAttribute(name, new Attribute("Value", new Double(Math.random())));
         }

         // min has to be higher than 0
         assertTrue(((Double)server.getAttribute(name, "Min")).doubleValue() >= 0);
         // max has to be less than 1
         assertTrue(((Double)server.getAttribute(name, "Max")).doubleValue() <= 1);

         //check than max size of entries has been kept
         assertEquals(((SortedMap)server.getAttribute(name, "Entries")).size(), maxSize);
      }
      finally
      {
         server.unregisterMBean(name);
      }
   }

   public void testNotificationMBean() throws Exception
   {
      ObjectName name = new ObjectName("Domain:name=notification");
      ObjectName beanName = new ObjectName("Domain:name=observed");
      try
      {
         server.createMBean("mx4j.tools.stats.NotificationStatisticsRecorder", name, null);
         Test t = new Test();
         server.registerMBean(t, beanName);
         server.setAttribute(name, new Attribute("ObservedObject", beanName));
         server.setAttribute(name, new Attribute("ObservedAttribute", "Value"));

         // Test that nothing happens if not started

         //Every time the value is set the statistics are updated
         server.setAttribute(beanName, new Attribute("Value", new Double(10)));
         server.setAttribute(beanName, new Attribute("Value", new Double(5)));
         server.setAttribute(beanName, new Attribute("Value", new Double(20)));

         assertEquals(new Long(0), server.getAttribute(name, "Max"));
         assertEquals(new Long(0), server.getAttribute(name, "Min"));
         assertEquals(new Long(0), server.getAttribute(name, "Average"));
         assertEquals(0, ((SortedMap)server.getAttribute(name, "Entries")).size());

         // start it
         server.invoke(name, "start", null, null);

         //Every time the value is set the statistics are updated
         server.setAttribute(beanName, new Attribute("Value", new Double(10)));
         server.setAttribute(beanName, new Attribute("Value", new Double(5)));
         server.setAttribute(beanName, new Attribute("Value", new Double(20)));

         assertEquals(new Double(20), server.getAttribute(name, "Max"));
         assertEquals(new Double(5), server.getAttribute(name, "Min"));
         assertEquals(11.6, ((Double)server.getAttribute(name, "Average")).doubleValue(), 0.1);
         assertEquals(3, ((SortedMap)server.getAttribute(name, "Entries")).size());

         // start it
         server.invoke(name, "stop", null, null);
         server.invoke(name, "start", null, null);

         // Restart with longs
         server.setAttribute(beanName, new Attribute("Value", new Double(10)));
         server.setAttribute(beanName, new Attribute("Value", new Double(15)));
         server.setAttribute(beanName, new Attribute("Value", new Double(20)));

         assertEquals(new Double(20), server.getAttribute(name, "Max"));
         assertEquals(new Double(10), server.getAttribute(name, "Min"));
         assertEquals(15, ((Double)server.getAttribute(name, "Average")).longValue());
         assertEquals(((SortedMap)server.getAttribute(name, "Entries")).size(), 3);

      }
      finally
      {
         server.unregisterMBean(name);
      }
   }

   public synchronized void testTimedMBean() throws Exception
   {
      ObjectName name = new ObjectName("Domain:name=notification");
      ObjectName beanName = new ObjectName("Domain:name=observed");
      try
      {
         server.createMBean("mx4j.tools.stats.TimedStatisticsRecorder", name, null);
         Test t = new Test();
         server.registerMBean(t, beanName);
         server.setAttribute(name, new Attribute("ObservedObject", beanName));
         server.setAttribute(name, new Attribute("ObservedAttribute", "Value"));
         server.setAttribute(name, new Attribute("Granularity", new Long(1000)));

         // Test that nothing happens if not started

         //Every time the value is set the statistics are updated
         server.setAttribute(beanName, new Attribute("Value", new Double(10)));
         server.setAttribute(beanName, new Attribute("Value", new Double(5)));
         server.setAttribute(beanName, new Attribute("Value", new Double(20)));

         assertEquals(new Long(0), server.getAttribute(name, "Max"));
         assertEquals(new Long(0), server.getAttribute(name, "Min"));
         assertEquals(new Long(0), server.getAttribute(name, "Average"));
         assertEquals(0, ((SortedMap)server.getAttribute(name, "Entries")).size());

         // start it
         server.invoke(name, "start", null, null);

         //Every time the value is set the statistics are updated
         server.setAttribute(beanName, new Attribute("Value", new Double(10)));
         Thread.currentThread().sleep(1500);
         server.setAttribute(beanName, new Attribute("Value", new Double(5)));
         Thread.currentThread().sleep(1500);
         server.setAttribute(beanName, new Attribute("Value", new Double(20)));
         Thread.currentThread().sleep(1500);

         assertEquals(new Double(20), server.getAttribute(name, "Max"));
         assertEquals(new Double(5), server.getAttribute(name, "Min"));
         // difficult to predict an exact value
         assertTrue(((Double)server.getAttribute(name, "Average")).longValue() > 5 && ((Double)server.getAttribute(name, "Average")).longValue() < 20);
         assertTrue(((SortedMap)server.getAttribute(name, "Entries")).size() >= 3);

         // start it
         server.invoke(name, "stop", null, null);
         server.invoke(name, "start", null, null);

         // Restart with longs
         server.setAttribute(beanName, new Attribute("Value", new Double(10)));
         Thread.currentThread().sleep(1500);
         server.setAttribute(beanName, new Attribute("Value", new Double(15)));
         Thread.currentThread().sleep(1500);
         server.setAttribute(beanName, new Attribute("Value", new Double(20)));
         Thread.currentThread().sleep(1500);

         assertEquals(new Double(20), server.getAttribute(name, "Max"));
         assertEquals(new Double(10), server.getAttribute(name, "Min"));
         // difficult to predict an exact value
         assertTrue(((Double)server.getAttribute(name, "Average")).longValue() >= 15 && ((Double)server.getAttribute(name, "Average")).longValue() <= 20);
         assertTrue(((SortedMap)server.getAttribute(name, "Entries")).size() >= 3);

      }
      finally
      {
         server.unregisterMBean(name);
      }
   }

   public interface TestMBean
   {
      public double getValue();

      public void setValue(double value);
   }

   public class Test extends NotificationBroadcasterSupport implements TestMBean
   {
      private double value;

      public double getValue()
      {
         return value;
      }

      public void setValue(double value)
      {
         double oldValue = this.value;
         this.value = value;
         sendNotification(new AttributeChangeNotification(this, 0L, System.currentTimeMillis(), "test", "Value", Double.class.toString(), new Double(oldValue), new Double(value)));
      }
   }

}

