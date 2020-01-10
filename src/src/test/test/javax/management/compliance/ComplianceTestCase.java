/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.compliance;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import test.MX4JTestCase;

/**
 * @version $Revision: 1.5 $
 */
public abstract class ComplianceTestCase extends MX4JTestCase
{
   public ComplianceTestCase(String s)
   {
      super(s);
   }

   public void testCompliance() throws Exception
   {
      ClassLoader loader = createClassLoader();

      JarFile jar = loadJar();

      Enumeration entries = jar.entries();
      ArrayList nonExistingMethods = new ArrayList();
      while (entries.hasMoreElements())
      {
         JarEntry entry = (JarEntry)entries.nextElement();

         // Skip directories
         if (entry.isDirectory()) continue;

         // Skip Sun's implementation classes
         String entryName = entry.getName();
         if (entryName.startsWith("javax"))
         {
            // Take the class
            String fullClassName = entryName.replace('/', '.');
            fullClassName = fullClassName.substring(0, fullClassName.length() - ".class".length());

            if (skipClassName(fullClassName)) continue;

            Class cls = loader.loadClass(fullClassName);

            if (skipClass(cls)) continue;

            String name = fullClassName.substring("javax.management".length());
            name = name.replace('.', '_');
            try
            {
               // Verify that a method with this name exists
               getClass().getMethod("test" + name, new Class[0]);
            }
            catch (NoSuchMethodException x)
            {
               nonExistingMethods.add(fullClassName);
            }
         }
      }
      Collections.sort(nonExistingMethods);
      if (nonExistingMethods.size() > 0) fail("Compliance test incomplete, missing classes are:\n" + nonExistingMethods);
   }

   protected abstract boolean skipClassName(String className);

   protected abstract boolean skipClass(Class cls);

   protected abstract void checkCompliance(String className) throws Exception;

   protected abstract ClassLoader createClassLoader() throws MalformedURLException;

   protected abstract JarFile loadJar() throws IOException;

   protected void check(String partialClassName) throws Exception
   {
      ClassLoader loader = createClassLoader();
      String fullName = "javax.management." + partialClassName;
      if (skipClassName(fullName)) return;
      if (skipClass(loader.loadClass(fullName))) return;
      checkCompliance(fullName);
   }
}
