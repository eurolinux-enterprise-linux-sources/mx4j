/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.server;

import javax.management.MBeanServer;
import javax.management.MBeanServerBuilder;
import javax.management.MBeanServerDelegate;

/**
 * Base class for chained MBeanServerBuilders. <br>
 * By default this class delegates all method calls to the nested MBeanServerBuilder. <br>
 * See the MX4J documentation on how to use correctly this class. <br>
 * <br>
 * Example implementation:
 * <pre>
 * public class LoggingBuilder extends ChainedMBeanServerBuilder
 * {
 *    public LoggingBuilder()
 *    {
 *       super(new MX4JMBeanServerBuilder());
 *    }
 * <p/>
 *    public MBeanServer newMBeanServer(String defaultDomain, MBeanServer outer, MBeanServerDelegate delegate)
 *    {
 *       LoggingMBeanServer external = new LoggingMBeanServer();
 *       MBeanServer nested = getBuilder().newMBeanServer(defaultDomain, outer == null ? external : outer, delegate);
 *       external.setMBeanServer(nested);
 *       return external;
 *    }
 * }
 * <p/>
 * public class LoggingMBeanServer extends ChainedMBeanServer
 * {
 *    protected void setMBeanServer(MBeanServer server)
 *    {
 *       super.setMBeanServer(server);
 *    }
 * <p/>
 *    public Object getAttribute(ObjectName objectName, String attribute)
 *            throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException
 *    {
 *       Object value = super.getAttribute(objectName, attribute);
 *       System.out.println("Value is: " + value);
 *       return value;
 *    }
 * <p/>
 *    ...
 * }
 * </pre>
 *
 * @version $Revision: 1.3 $
 */
public class ChainedMBeanServerBuilder extends MBeanServerBuilder
{
   private final MBeanServerBuilder builder;

   /**
    * Creates a new chained MBeanServerBuilder
    *
    * @param builder The MBeanServerBuilder this object delegates to.
    */
   public ChainedMBeanServerBuilder(MBeanServerBuilder builder)
   {
      if (builder == null) throw new IllegalArgumentException();
      this.builder = builder;
   }

   /**
    * Forwards the call to the chained builder.
    *
    * @see MBeanServerBuilder#newMBeanServerDelegate
    */
   public MBeanServerDelegate newMBeanServerDelegate()
   {
      return getMBeanServerBuilder().newMBeanServerDelegate();
   }

   /**
    * Forwards the call to the chained builder.
    *
    * @see MBeanServerBuilder#newMBeanServer
    */
   public MBeanServer newMBeanServer(String defaultDomain, MBeanServer outer, MBeanServerDelegate delegate)
   {
      return getMBeanServerBuilder().newMBeanServer(defaultDomain, outer, delegate);
   }

   /**
    * Returns the chained MBeanServerBuilder this object delegates to.
    */
   protected MBeanServerBuilder getMBeanServerBuilder()
   {
      return builder;
   }
}
