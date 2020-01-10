/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package javax.management;

import mx4j.server.MX4JMBeanServerBuilder;

/**
 * <p>This class is used by the {@link javax.management.MBeanServerFactory} to delegate the
 * creation of new instances of {@link javax.management.MBeanServerDelegate} and
 * {@link javax.management.MBeanServer}.
 * This implementation further delegates the work to {@link mx4j.server.MX4JMBeanServerBuilder}
 * to return implementations in the <code>mx4j.server</code> package.</p>
 * <p/>
 * <p>The {@link javax.management.MBeanServerFactory} creates the delegate before
 * creating the MBeanServer itself and providing a reference to the created delegate.
 * Note that the delegate passed to the MBeanServer might not be the instance returned
 * by this builder; for example, it could be a wrapper around it.</p>
 *
 * @version $Revision: 1.10 $
 * @see MBeanServer
 * @see MBeanServerFactory
 */

public class MBeanServerBuilder
{
   /**
    * The builder to which this implementation delegates all operations.
    */
   private MBeanServerBuilder builder;

   /**
    * This method creates a new MBeanServerDelegate for a new MBeanServer.
    *
    * @return A new {@link javax.management.MBeanServerDelegate}.
    */
   public MBeanServerDelegate newMBeanServerDelegate()
   {
      return builderDelegate().newMBeanServerDelegate();
   }

   /**
    * Returns a new MBeanServer instance.
    *
    * @return A new private implementation of an MBeanServer.
    */
   public MBeanServer newMBeanServer(String defaultDomain, MBeanServer outer, MBeanServerDelegate delegate)
   {
      return builderDelegate().newMBeanServer(defaultDomain, outer, delegate);
   }

   /**
    * Returns the delegate builder.
    *
    * @return the delegate builder.
    */
   private synchronized MBeanServerBuilder builderDelegate()
   {
      if (builder == null)
         builder = new MX4JMBeanServerBuilder();
      return builder;
   }
}
