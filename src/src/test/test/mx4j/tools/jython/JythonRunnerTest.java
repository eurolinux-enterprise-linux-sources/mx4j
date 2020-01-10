/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */
package test.mx4j.tools.jython;

import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

import junit.framework.TestCase;
import mx4j.tools.jython.JythonRunner;

/**
 * @version $Revision: 1.3 $
 */
public class JythonRunnerTest extends TestCase
{
   private MBeanServer svr;
   private JythonRunner jythonRunner;
   private ObjectName jythonName;
   private Int _int;
   private ObjectName intName;

   public interface IntMBean
   {
      int getValue();

      void setValue(int value);

      ObjectInstance getMBeans();

      void setMBeans(ObjectInstance value);

      ObjectInstance getInstances();

      void setInstances(ObjectInstance value);

      void invocation();

      void invocationWithString(String param);

      Long invocationWithLong(Long param);

      Number subtract(Number one, Number two);

      int subtractInts(int one, int two);
   }

   public class Int extends NotificationBroadcasterSupport implements IntMBean
   {
      private int value;
      private String param;
      private boolean invoked;
      private Number subtracted;
      private int subtractedInts;
      private Notification notification;
      private ObjectInstance mbean;
      private ObjectInstance instance;

      public int getValue()
      {
         return value;
      }

      public void setValue(int value)
      {
         this.value = value;
      }

      public ObjectInstance getMBeans()
      {
         return mbean;
      }

      public void setMBeans(ObjectInstance mbeans)
      {
         this.mbean = mbeans;
      }

      public ObjectInstance getInstances()
      {
         return instance;
      }

      public void setInstances(ObjectInstance instance)
      {
         this.instance = instance;
      }

      public void invocation()
      {
         this.invoked = true;
      }

      public void invocationWithString(String param)
      {
         this.param = param;
      }

      public Long invocationWithLong(Long param)
      {
         return param;
      }

      public Number subtract(Number one, Number two)
      {
         if ((one.floatValue() - one.intValue()) == 0)
         {
            subtracted = new Integer(one.intValue() - two.intValue());
         }
         else
         {
            subtracted = new Float(one.floatValue() - two.floatValue());
         }
         return subtracted;
      }

      public int subtractInts(int one, int two)
      {
         subtractedInts = one - two;
         return subtractedInts;
      }
   }

   public JythonRunnerTest(String name)
   {
      super(name);
   }

   public void setUp() throws Exception
   {
      super.setUp();
      svr = MBeanServerFactory.createMBeanServer();
      jythonRunner = new JythonRunner();
      jythonName = ObjectName.getInstance("tools", "type", "JythonRunner");
      svr.registerMBean(jythonRunner, jythonName);
      _int = new Int();
      intName = ObjectName.getInstance("test", "type", "Int");
      svr.registerMBean(_int, intName);
   }

   public void testInvokeFromJython()
   {
      jythonRunner.setScript("import jarray\n" +
                             "from java.lang import String,Long,Integer,Float\n" +
                             "from javax.management import Attribute\n" +
                             "o = ObjectName.getInstance(\"test\",\"type\",\"Int\")\n" +
                             "server.setAttribute(o,Attribute(\"Value\",1))\n" +
                             "server.invoke(o,\"invocation\",None,None)\n" +
                             "params = ['parameter']\n" +
                             "paramTypes = ['java.lang.String']\n" +
                             "c = String().getClass()\n" +
                             "aryParm = jarray.array(params,c)\n" +
                             "aryType = jarray.array(paramTypes,c)\n" +
                             "server.invoke(o,\"invocationWithString\",aryParm,aryType)\n");
      jythonRunner.runScript();
      assertTrue(_int.invoked);
      assertEquals(1, _int.value);
      assertEquals(_int.param, "parameter");
   }

   public void testHelperJythonObjects() throws Exception
   {
      jythonRunner.setScript("from java.lang import String,Long,Integer,Float\n" +
                             "from javax.management import Attribute\n" +
                             "o = ObjectName.getInstance(\"test\",\"type\",\"Int\")\n" +
                             "p = proxy(server,o)\n" +
                             "p.invocationWithLong(Long(10000))\n" +
                             "p.subtract(Float(2.1),Float(1.9))\n" +
                             "p.subtract(Integer(3),Integer(1))\n" +
                             "p.subtractInts(10,5)");
      jythonRunner.runScript();
      assertEquals(new Integer(2), _int.subtracted);
      assertEquals(5, _int.subtractedInts);
   }

   public void testListenerJythonScript() throws Exception
   {
      jythonRunner.setScript("o = ObjectName.getInstance(\"test\",\"type\",\"Int\")\n" +
                             "p = Proxy(server,o)\n" +
                             "p.Value=111");
      jythonRunner.setObservedObject(intName);
      jythonRunner.setNotificationType("Type");
      _int.sendNotification(new Notification("Type", "Source", 1L));
      // Make sure notification is sent
      Thread.sleep(1000L);
      assertEquals(111, _int.getValue());
   }

   public void testHelperFunctions() throws Exception
   {
      jythonRunner.setScript("import jarray\n" +
                             "s = 'test.mx4j.tools.jython.JythonRunnerTest$Int'\n" +
                             "o = ObjectName.getInstance(\"test\",\"type\",\"Int\")\n" +
                             "p = Proxy(server,o)\n" +
                             "p.MBeans=mbeans('test:type=Int')[0]\n" +
                             "p.Instances=instances(s,'*:*')[0]");
      jythonRunner.runScript();

      ObjectInstance oinst = _int.instance;
      assertEquals(intName, oinst.getObjectName());
      assertEquals("test.mx4j.tools.jython.JythonRunnerTest$Int",
                   oinst.getClassName());
      oinst = _int.mbean;
      assertEquals(intName, oinst.getObjectName());
      assertEquals("test.mx4j.tools.jython.JythonRunnerTest$Int",
                   oinst.getClassName());
   }

   public void tearDown() throws Exception
   {
      svr.unregisterMBean(jythonName);
      super.tearDown();
   }

}
