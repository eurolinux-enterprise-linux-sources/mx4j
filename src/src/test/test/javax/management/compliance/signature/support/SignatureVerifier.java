/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.compliance.signature.support;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * @version $Revision: 1.5 $
 */
public class SignatureVerifier
{
   public void verifySignature(String className, ClassLoader jmxriLoader, ClassLoader mx4jLoader) throws Exception
   {
      Class jmxriClass = jmxriLoader.loadClass(className);
      Class mx4jClass = mx4jLoader.loadClass(className);

      int modifiers = jmxriClass.getModifiers();
      boolean isPublic = Modifier.isPublic(modifiers);
      boolean isProtected = Modifier.isProtected(modifiers);
      boolean isPackage = !Modifier.isPrivate(modifiers) && !isProtected && !isPublic;
      boolean isSerializable = Serializable.class.isAssignableFrom(jmxriClass);

      NotCompliantWarningException warning = null;

      try
      {
         checkSameClassModifiers(jmxriClass, mx4jClass);
      }
      catch (NotCompliantWarningException x)
      {
         warning = x;
      }

      try
      {
         checkSameInheritance(jmxriClass, mx4jClass);
      }
      catch (NotCompliantWarningException x)
      {
         warning = x;
      }

      if (!isPackage)
      {
         try
         {
            checkSameConstructors(jmxriClass, mx4jClass);
         }
         catch (NotCompliantWarningException x)
         {
            warning = x;
         }
         try
         {
            checkSameMethods(jmxriClass, mx4jClass);
         }
         catch (NotCompliantWarningException x)
         {
            warning = x;
         }
         try
         {
            checkSameFields(jmxriClass, mx4jClass);
         }
         catch (NotCompliantWarningException x)
         {
            warning = x;
         }
      }

      if (isSerializable)
      {
         try
         {
            checkSameSerialVersionUID(jmxriClass, mx4jClass);
         }
         catch (NotCompliantWarningException x)
         {
            warning = x;
         }
      }

      if (warning != null) throw warning;
   }

   private void checkSameClassModifiers(Class jmxri, Class mx4j) throws NotCompliantException
   {
      int jmxriModifiers = jmxri.getModifiers();
      int mx4jModifers = mx4j.getModifiers();
      if (jmxriModifiers != mx4jModifers)
      {
         int modifier = jmxriModifiers ^ mx4jModifers;
         if ((modifier & jmxriModifiers) != 0)
         {
            throw new NotCompliantException("JMX class " + jmxri.getName() + " in MX4J implementation is not declared " + Modifier.toString(modifier) + " as it should be");
         }
         if ((modifier & mx4jModifers) != 0)
         {
            throw new NotCompliantWarningException("JMX class " + jmxri.getName() + " in MX4J implementation is declared " + Modifier.toString(modifier) + ", it is not in JMXRI");
         }
      }
   }

   private void checkSameInheritance(Class jmxri, Class mx4j) throws NotCompliantException
   {
      // I have to walk the inheritance hierarchy

      Set jmxriInterfaces = new HashSet();
      Set mx4jInterfaces = new HashSet();
      for (Class jmxriParent = jmxri, mx4jParent = mx4j; jmxriParent != null; jmxriParent = jmxriParent.getSuperclass(), mx4jParent = mx4jParent.getSuperclass())
      {
         findInterfaces(jmxriParent, jmxriInterfaces);

         findInterfaces(mx4jParent, mx4jInterfaces);

         if (!jmxriParent.getName().equals(mx4jParent.getName()))
         {
            throw new NotCompliantException("JMX class " + jmxri.getName() + " in MX4J implementation does not have the same hierarchy as JMXRI: " + mx4jParent.getName() + ", should be " + jmxriParent.getName());
         }
      }

      if (!jmxriInterfaces.containsAll(mx4jInterfaces))
      {
         mx4jInterfaces.removeAll(jmxriInterfaces);
         checkInterfacesHaveMethods(jmxri, mx4jInterfaces);
      }
      if (!mx4jInterfaces.containsAll(jmxriInterfaces))
      {
         jmxriInterfaces.removeAll(mx4jInterfaces);
         throw new NotCompliantException("JMX class " + jmxri.getName() + " in MX4J implementation does not implement the required interfaces: " + jmxriInterfaces);
      }
   }

   private void findInterfaces(Class cls, Set interfaces)
   {
      Class[] intfs = cls.getInterfaces();
      for (int i = 0; i < intfs.length; ++i)
      {
         Class intf = intfs[i];
         boolean added = interfaces.add(intf.getName());
         if (added) findInterfaces(intf, interfaces);
      }
   }

   private void checkInterfacesHaveMethods(Class cls, Set interfaces) throws NotCompliantException
   {
      boolean warning = false;
      for (Iterator i = interfaces.iterator(); i.hasNext();)
      {
         String name = (String)i.next();
         if (name.equals("java.lang.Cloneable"))
            warning = true;
         else
            warning = false;
      }

      if (warning)
         throw new NotCompliantWarningException("JMX class " + cls.getName() + " in MX4J implementation implements too many tag interfaces: " + interfaces);
      else
         throw new NotCompliantException("JMX class " + cls.getName() + " in MX4J implementation implements too many interfaces: " + interfaces);
   }

   private void checkSameConstructors(final Class jmxri, Class mx4j) throws NotCompliantException
   {
      checkSameObjectMethod(new ObjectClass.Constructor(jmxri), new ObjectClass.Constructor(mx4j));
   }

   private void checkSameMethods(Class jmxri, Class mx4j) throws NotCompliantException
   {
      checkSameObjectMethod(new ObjectClass.Method(jmxri), new ObjectClass.Method(mx4j));
   }

   private void checkSameObjectMethod(ObjectClass jmxri, ObjectClass mx4j) throws NotCompliantException
   {
      // Public methods first
      Set jmxriMethods = wrapMethods(jmxri.getMethods());
      Set mx4jMethods = wrapMethods(mx4j.getMethods());
      checkSameMethods(jmxri.getName(), jmxriMethods, mx4jMethods);

      // Protected methods now. I should walk the inheritance hierarchy.
      jmxriMethods.clear();
      mx4jMethods.clear();
      for (ObjectClass jmxriParent = jmxri, mx4jParent = mx4j; jmxriParent != null; jmxriParent = jmxriParent.getSuperclass(), mx4jParent = mx4jParent.getSuperclass())
      {
         ObjectMethod[] methods = jmxriParent.getDeclaredMethods();
         for (int i = 0; i < methods.length; ++i)
         {
            if (Modifier.isProtected(methods[i].getModifiers()))
            {
               jmxriMethods.add(wrapMethod(methods[i]));
            }
         }

         methods = mx4jParent.getDeclaredMethods();
         for (int i = 0; i < methods.length; ++i)
         {
            if (Modifier.isProtected(methods[i].getModifiers()))
            {
               mx4jMethods.add(wrapMethod(methods[i]));
            }
         }
      }
      checkSameMethods(jmxri.getName(), jmxriMethods, mx4jMethods);
   }

   private void checkSameFields(Class jmxri, Class mx4j) throws NotCompliantException
   {
      // Public fields first
      Set jmxriFields = wrapFields(jmxri.getFields());
      Set mx4jFields = wrapFields(mx4j.getFields());
      checkSameFields(jmxri.getName(), jmxriFields, mx4jFields);

      // Protected fields now. I should walk the inheritance hierarchy.
      jmxriFields.clear();
      mx4jFields.clear();
      for (Class jmxriParent = jmxri, mx4jParent = mx4j; jmxriParent != null; jmxriParent = jmxriParent.getSuperclass(), mx4jParent = mx4jParent.getSuperclass())
      {
         Field[] fields = jmxriParent.getDeclaredFields();
         for (int i = 0; i < fields.length; ++i)
         {
            if (Modifier.isProtected(fields[i].getModifiers()))
            {
               jmxriFields.add(wrapField(fields[i]));
            }
         }

         fields = mx4jParent.getDeclaredFields();
         for (int i = 0; i < fields.length; ++i)
         {
            if (Modifier.isProtected(fields[i].getModifiers()))
            {
               mx4jFields.add(wrapField(fields[i]));
            }
         }
      }
      checkSameFields(jmxri.getName(), jmxriFields, mx4jFields);
   }

   private void checkSameSerialVersionUID(Class jmxriClass, Class mx4jClass) throws NotCompliantException
   {
      try
      {
         Field jmxriField = jmxriClass.getField("serialVersionUID");
         jmxriField.setAccessible(true);
         Field mx4jField = mx4jClass.getField("serialVersionUID");
         mx4jField.setAccessible(true);
         long jmxriValue = jmxriField.getLong(null);
         long mx4jValue = jmxriField.getLong(null);
         if (jmxriValue != mx4jValue) throw new NotCompliantException("JMX class " + jmxriClass.getName() + " in MX4J implementation does not have the same serialVersionUID: expecting " + jmxriValue + ", found " + mx4jValue);
      }
      catch (NoSuchFieldException ignored)
      {
         // If the class did not change between JMX 1.0 and JMX 1.1, then the serialVersionUID is not present
      }
      catch (NotCompliantException x)
      {
         throw x;
      }
      catch (Exception x)
      {
         x.printStackTrace();
         throw new NotCompliantException("Unknown problems in checking serialVersionUID: " + x);
      }
   }

   private Set wrapMethods(ObjectMethod[] methods)
   {
      Set set = new HashSet();
      for (int i = 0; i < methods.length; ++i)
      {
         set.add(wrapMethod(methods[i]));
      }
      return set;
   }

   private MethodWrapper wrapMethod(ObjectMethod method)
   {
      return new MethodWrapper(method);
   }

   private Set wrapFields(Field[] fields)
   {
      HashSet set = new HashSet();
      for (int i = 0; i < fields.length; ++i)
      {
         set.add(wrapField(fields[i]));
      }
      return set;
   }

   private FieldWrapper wrapField(Field field)
   {
      return new FieldWrapper(field);
   }

   private void checkSameMethods(String name, Set jmxri, Set mx4j) throws NotCompliantException
   {
      if (!jmxri.containsAll(mx4j))
      {
         checkDifferentMethods(name, mx4j, jmxri);
      }

      if (!mx4j.containsAll(jmxri))
      {
         checkDifferentMethods(name, jmxri, mx4j);
      }
   }

   private void checkDifferentMethods(String name, Set set1, Set set2) throws NotCompliantException
   {
      set1.removeAll(set2);

      boolean warning = false;
      boolean error = false;
      ArrayList warnings = new ArrayList();
      ArrayList errors = new ArrayList();
      for (Iterator i = set1.iterator(); i.hasNext();)
      {
         MethodWrapper method1 = (MethodWrapper)i.next();
         boolean found = false;
         for (Iterator j = set2.iterator(); j.hasNext();)
         {
            MethodWrapper method2 = (MethodWrapper)j.next();
            if (method1.isSameMethod(method2))
            {
               if (!method1.sameSignatureModifiers(method2))
               {
                  warning = true;
                  warnings.add(method1);
                  warnings.add(method2);
               }
               else
               {
                  if (method1.throwsClauseDifferForRuntimeExceptionsOnly(method2))
                  {
                     warning = true;
                     warnings.add(method1);
                     warnings.add(method2);
                  }
                  else
                  {
                     error = true;
                     errors.add(method1);
                     errors.add(method2);
                  }
               }
               found = true;
               break;
            }
         }
         if (!found) throw new NotCompliantException("JMX class " + name + " in MX4J implementation has different interface: " + set1);
      }

      if (error) throw new NotCompliantException("JMX class " + name + " in MX4J implementation has different signature: " + errors);
      if (warning) throw new NotCompliantWarningException("JMX class " + name + " in MX4J implementation has different signature: " + warnings);
      throw new IllegalStateException();
   }

   private void checkSameFields(String name, Set jmxri, Set mx4j) throws NotCompliantException
   {
      if (!jmxri.containsAll(mx4j))
      {
         mx4j.removeAll(jmxri);
         throw new NotCompliantException("JMX class " + name + " in MX4J implementation has too many fields: " + mx4j);
      }
      if (!mx4j.containsAll(jmxri))
      {
         jmxri.removeAll(mx4j);
         throw new NotCompliantException("JMX class " + name + " in MX4J implementation does not have the required fields: " + jmxri);
      }
   }
}
