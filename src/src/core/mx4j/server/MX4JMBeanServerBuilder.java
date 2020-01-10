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
 * <p>This class is responsible for creating new instances of {@link MBeanServerDelegate}
 * and {@link MBeanServer}. It creates instances from the implementation in the
 * <code>mx4j.server</code> package.</p>
 * <p/>
 * <p>The {@link javax.management.MBeanServerFactory} first creates the delegate, then it
 * creates the MBeanServer and provides a reference to the created delegate to it.
 * Note that the delegate passed to the MBeanServer might not be the instance returned
 * by this builder; for example, it could be a wrapper around it.</p>
 *
 * @version $Revision: 1.7 $
 * @see MBeanServer
 * @see javax.management.MBeanServerFactory
 */

public class MX4JMBeanServerBuilder extends MBeanServerBuilder
{
   /**
    * Returns a new {@link MX4JMBeanServerDelegate} instance for a new MBeanServer.
    *
    * @return a new {@link MX4JMBeanServerDelegate} instance for a new MBeanServer.
    */
   public MBeanServerDelegate newMBeanServerDelegate()
   {
      return new MX4JMBeanServerDelegate();
   }

   /**
    * Returns a new {@link MX4JMBeanServer} instance.
    *
    * @param defaultDomain the default domain name for the new server.
    * @param outer         the {@link MBeanServer} that is passed in calls to
    *                      {@link javax.management.MBeanRegistration#preRegister(javax.management.MBeanServer, javax.management.ObjectName)}.
    * @param delegate      the {@link MBeanServerDelegate} instance for the new server.
    * @return a new {@link MX4JMBeanServer} instance.
    */
   public MBeanServer newMBeanServer(String defaultDomain, MBeanServer outer, MBeanServerDelegate delegate)
   {
      return new MX4JMBeanServer(defaultDomain, outer, delegate);
   }
}
