/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.compliance.serialization.support;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * @version $Revision: 1.4 $
 */
public class SerializationVerifier
{
   private String instantiator;
   private String comparator;

   public SerializationVerifier(String instantiator, String comparator)
   {
      this.instantiator = instantiator;
      this.comparator = comparator;
   }

   public void verifySerialization(String name, ClassLoader jmxriLoader, ClassLoader mx4jLoader) throws Exception
   {
      name = name.substring(name.lastIndexOf('.') + 1);

      // Create the object, one with MX4J, one with JMXRI
      Thread.currentThread().setContextClassLoader(mx4jLoader);
      Object mx4j = create(name);
      Thread.currentThread().setContextClassLoader(jmxriLoader);
      Object jmxri = create(name);

      // Be sure they're not the same class
      if (mx4j.getClass().isInstance(jmxri)) throw new Exception("Classes must be different");
      if (jmxri.getClass().isInstance(mx4j)) throw new Exception("Classes must be different");

      // Serialize MX4J object
      Thread.currentThread().setContextClassLoader(mx4jLoader);
      byte[] mx4jBytes = serialize(mx4j);

      // Deserialize with JMXRI
      Thread.currentThread().setContextClassLoader(jmxriLoader);
      Object jmxriObject = deserialize(mx4jBytes);

      // Be sure they're not the same class
      if (mx4j.getClass().isInstance(jmxriObject)) throw new Exception("Classes must be different");
      if (jmxriObject.getClass().isInstance(mx4j)) throw new Exception("Classes must be different");
      // Be also sure the deserialized is of the same type as JMXRI
      if (jmxri.getClass() != jmxriObject.getClass()) throw new Exception("Classes must be equal");

      // Now compare the original and the deserialized
      compare(name, jmxri, jmxriObject);

      // Now, do the opposite

      // Serialize JMXRI object
      Thread.currentThread().setContextClassLoader(jmxriLoader);
      byte[] jmxriBytes = serialize(jmxri);

      // Deserialize with MX4J
      Thread.currentThread().setContextClassLoader(mx4jLoader);
      Object mx4jObject = deserialize(jmxriBytes);

      // Be sure they're not the same class
      if (jmxri.getClass().isInstance(mx4jObject)) throw new Exception("Classes must be different");
      if (mx4jObject.getClass().isInstance(jmxri)) throw new Exception("Classes must be different");
      // Be also sure the deserialized is of the same type as MX4J
      if (mx4j.getClass() != mx4jObject.getClass()) throw new Exception("Classes must be equal");

      // Now compare the original and the deserialized
      compare(name, mx4j, mx4jObject);

   }

   private Object create(String name) throws Exception
   {
      // Create an instance of the Instantiator
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      Object creator = loader.loadClass(instantiator).newInstance();

      // Lookup the creation method and call it
      Method method = creator.getClass().getMethod("create" + name, new Class[0]);
      Object object = method.invoke(creator, new Object[0]);
      return object;
   }

   private byte[] serialize(Object object) throws Exception
   {
      // Must delegate again to another object loaded with the correct classloader,
      // otherwise the deserialization will use the system classloader instead of
      // the context classloader
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      Object serializer = loader.loadClass("test.javax.management.compliance.serialization.support.Serializer").newInstance();
      Method method = serializer.getClass().getMethod("serialize", new Class[]{Object.class});
      return (byte[])method.invoke(serializer, new Object[]{object});
   }

   private Object deserialize(byte[] bytes) throws Exception
   {
      // Must delegate again to another object loaded with the correct classloader,
      // otherwise the deserialization will use the system classloader instead of
      // the context classloader
      ClassLoader loader = Thread.currentThread().getContextClassLoader();
      Object serializer = loader.loadClass("test.javax.management.compliance.serialization.support.Serializer").newInstance();
      Method method = serializer.getClass().getMethod("deserialize", new Class[]{byte[].class});
      return method.invoke(serializer, new Object[]{bytes});
   }

   private void compare(String name, Object obj1, Object obj2) throws Exception
   {
      // First check if the class has the equals method
      try
      {
         obj1.getClass().getDeclaredMethod("equals", new Class[]{Object.class});
         // It's present
         if (!obj1.equals(obj2)) throw new RuntimeException();

      }
      catch (NoSuchMethodException x)
      {
         // No equals(), create an instance of the Comparator
         ClassLoader loader = Thread.currentThread().getContextClassLoader();
         Object creator = loader.loadClass(comparator).newInstance();

         // Lookup the compare method
         Method method = creator.getClass().getMethod("compare" + name, new Class[]{Object.class, Object.class});
         try
         {
            method.invoke(creator, new Object[]{obj1, obj2});
         }
         catch (InvocationTargetException xx)
         {
            Throwable t = xx.getTargetException();
            if (t instanceof Exception)
               throw (Exception)t;
            else
               throw (Error)t;
         }
      }
   }
}
