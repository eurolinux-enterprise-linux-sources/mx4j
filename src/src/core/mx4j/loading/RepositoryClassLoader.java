/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.loading;

import java.security.SecureClassLoader;
import javax.management.loading.ClassLoaderRepository;

/**
 * A classloader that delegates to the ClassLoaderRepository
 *
 * @version $Revision: 1.5 $
 */
public class RepositoryClassLoader extends SecureClassLoader
{
   private ClassLoaderRepository repository;

   public RepositoryClassLoader(ClassLoaderRepository repository)
   {
      this.repository = repository;
   }

   public Class loadClass(String name) throws ClassNotFoundException
   {
      return repository.loadClass(name);
   }
}
