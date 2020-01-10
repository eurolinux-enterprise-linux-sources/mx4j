/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package javax.management.loading;

import java.net.URL;
import java.net.URLStreamHandlerFactory;

/**
 * An MLet that is not registered in the MBeanServer's ClassLoaderRepository (since it implements
 * the tag interface PrivateClassLoader).
 *
 * @version $Revision: 1.3 $
 * @since JMX 1.2
 */
public class PrivateMLet extends MLet implements PrivateClassLoader
{
   /**
    * Creates a new PrivateMLet
    *
    * @param urls          The URLs from where loading classes and resources
    * @param delegateToCLR True if the MLet should delegate to the MBeanServer's ClassLoaderRepository
    *                      in case the class or resource cannot be found by this MLet, false otherwise
    */
   public PrivateMLet(URL[] urls, boolean delegateToCLR)
   {
      super(urls, delegateToCLR);
   }

   /**
    * Creates a new PrivateMLet
    *
    * @param urls          The URLs from where loading classes and resources
    * @param parent        The parent classloader
    * @param delegateToCLR True if the MLet should delegate to the MBeanServer's ClassLoaderRepository
    *                      in case the class or resource cannot be found by this MLet, false otherwise
    */
   public PrivateMLet(URL[] urls, ClassLoader parent, boolean delegateToCLR)
   {
      super(urls, parent, delegateToCLR);
   }

   /**
    * Creates a new PrivateMLet
    *
    * @param urls          The URLs from where loading classes and resources
    * @param parent        The parent classloader
    * @param factory       The URL stream handler factory to handle custom URL schemes
    * @param delegateToCLR True if the MLet should delegate to the MBeanServer's ClassLoaderRepository
    *                      in case the class or resource cannot be found by this MLet, false otherwise
    */
   public PrivateMLet(URL[] urls, ClassLoader parent, URLStreamHandlerFactory factory, boolean delegateToCLR)
   {
      super(urls, parent, factory, delegateToCLR);
   }
}
