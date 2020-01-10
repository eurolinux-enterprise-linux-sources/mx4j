/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package javax.management;

/**
 * Do not use this class !
 *
 * @version $Revision: 1.3 $
 * @deprecated Use {@link MBeanServer#getClassLoaderRepository} instead.
 */
public class DefaultLoaderRepository
{
   /**
    * Do not use !
    *
    * @deprecated Use {@link javax.management.loading.ClassLoaderRepository#loadClass} instead.
    */
   public static Class loadClass(String s) throws ClassNotFoundException
   {
      return javax.management.loading.DefaultLoaderRepository.loadClass(s);
   }

   /**
    * Do not use !
    *
    * @deprecated Use {@link javax.management.loading.ClassLoaderRepository#loadClassWithout} instead.
    */
   public static Class loadClassWithout(ClassLoader classloader, String s) throws ClassNotFoundException
   {
      return javax.management.loading.DefaultLoaderRepository.loadClassWithout(classloader, s);
   }
}
