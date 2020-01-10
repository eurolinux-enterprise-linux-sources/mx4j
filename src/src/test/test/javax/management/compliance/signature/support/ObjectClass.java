/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.compliance.signature.support;

/**
 * @version $Revision: 1.3 $
 */
public interface ObjectClass
{
   public String getName();

   public ObjectMethod[] getDeclaredMethods();

   public ObjectMethod[] getMethods();

   public ObjectClass getSuperclass();

   public static class Constructor implements ObjectClass
   {
      private java.lang.Class cls;

      public Constructor(Class cls)
      {
         this.cls = cls;
      }

      public String getName()
      {
         return cls.getName();
      }

      public ObjectMethod[] getDeclaredMethods()
      {
         java.lang.reflect.Constructor[] constructors = cls.getDeclaredConstructors();
         ObjectMethod[] ctors = new ObjectMethod[constructors.length];
         for (int i = 0; i < ctors.length; ++i) ctors[i] = new ObjectMethod.Constructor(constructors[i]);
         return ctors;
      }

      public ObjectMethod[] getMethods()
      {
         java.lang.reflect.Constructor[] constructors = cls.getConstructors();
         ObjectMethod[] ctors = new ObjectMethod[constructors.length];
         for (int i = 0; i < ctors.length; ++i) ctors[i] = new ObjectMethod.Constructor(constructors[i]);
         return ctors;
      }

      public ObjectClass getSuperclass()
      {
         Class superCls = cls.getSuperclass();
         return superCls == null ? null : new Constructor(superCls);
      }
   }

   public static class Method implements ObjectClass
   {
      private java.lang.Class cls;

      public Method(Class cls)
      {
         this.cls = cls;
      }

      public String getName()
      {
         return cls.getName();
      }

      public ObjectMethod[] getDeclaredMethods()
      {
         java.lang.reflect.Method[] methods = cls.getDeclaredMethods();
         ObjectMethod[] mthds = new ObjectMethod[methods.length];
         for (int i = 0; i < mthds.length; ++i) mthds[i] = new ObjectMethod.Method(methods[i]);
         return mthds;
      }

      public ObjectMethod[] getMethods()
      {
         java.lang.reflect.Method[] methods = cls.getMethods();
         ObjectMethod[] mthds = new ObjectMethod[methods.length];
         for (int i = 0; i < mthds.length; ++i) mthds[i] = new ObjectMethod.Method(methods[i]);
         return mthds;
      }

      public ObjectClass getSuperclass()
      {
         Class superCls = cls.getSuperclass();
         return superCls == null ? null : new Method(superCls);
      }
   }
}
