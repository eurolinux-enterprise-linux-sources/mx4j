/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.compliance.signature.support;

/**
 * @version $Revision: 1.4 $
 */
public interface ObjectMethod
{
   public int getModifiers();

   public Class getReturnType();

   public String getName();

   public Class[] getParameterTypes();

   public Class[] getExceptionTypes();

   public static class Constructor implements ObjectMethod
   {
      private java.lang.reflect.Constructor constructor;

      public Constructor(java.lang.reflect.Constructor ctor)
      {
         this.constructor = ctor;
      }

      public int getModifiers()
      {
         return constructor.getModifiers();
      }

      public Class getReturnType()
      {
         return constructor.getDeclaringClass();
      }

      public String getName()
      {
         return constructor.getName();
      }

      public Class[] getParameterTypes()
      {
         return constructor.getParameterTypes();
      }

      public Class[] getExceptionTypes()
      {
         return constructor.getExceptionTypes();
      }
   }

   public static class Method implements ObjectMethod
   {
      private java.lang.reflect.Method method;

      public Method(java.lang.reflect.Method method)
      {
         this.method = method;
      }

      public int getModifiers()
      {
         return method.getModifiers();
      }

      public Class getReturnType()
      {
         return method.getReturnType();
      }

      public String getName()
      {
         return method.getName();
      }

      public Class[] getParameterTypes()
      {
         return method.getParameterTypes();
      }

      public Class[] getExceptionTypes()
      {
         return method.getExceptionTypes();
      }
   }
}
