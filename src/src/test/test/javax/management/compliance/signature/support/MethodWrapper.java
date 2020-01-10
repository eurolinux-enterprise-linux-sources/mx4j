/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.compliance.signature.support;

import java.lang.reflect.Modifier;
import java.util.ArrayList;

/**
 * @version $Revision: 1.4 $
 */
public class MethodWrapper extends MemberWrapper
{
   private ArrayList signature;
   private ArrayList exceptions;
   private ObjectMethod method;

   public MethodWrapper(ObjectMethod method)
   {
      // Clear synchronized modifier, not relevant
      int mods = method.getModifiers();
      if (Modifier.isSynchronized(mods)) mods -= Modifier.SYNCHRONIZED;
      modifiers = mods;
      type = method.getReturnType().getName();
      name = method.getName();
      signature = convert(method.getParameterTypes(), false);
      exceptions = convert(method.getExceptionTypes(), true);
      this.method = method;
   }

   public boolean isSameMethod(MethodWrapper other)
   {
      int mask = Modifier.PUBLIC | Modifier.PROTECTED | Modifier.STATIC | Modifier.ABSTRACT;
      if ((modifiers & mask) != (other.modifiers & mask)) return false;
      return name.equals(other.name) && type.equals(other.type) && signature.equals(other.signature);
   }

   public boolean sameSignatureModifiers(MethodWrapper other)
   {
      return modifiers == other.modifiers;
   }

   public boolean throwsClauseDiffer(MethodWrapper other)
   {
      ArrayList thisExceptions = convert(method.getExceptionTypes(), true);
      ArrayList otherExceptions = convert(other.method.getExceptionTypes(), true);
      if (thisExceptions.equals(otherExceptions)) return false;
      return true;
   }

   public boolean throwsClauseDifferForRuntimeExceptionsOnly(MethodWrapper other)
   {
      Class[] thisTypes = method.getExceptionTypes();
      ArrayList thisExceptions = convert(thisTypes, true);
      Class[] otherTypes = other.method.getExceptionTypes();
      ArrayList otherExceptions = convert(otherTypes, true);

      ArrayList thisCopy = (ArrayList)thisExceptions.clone();

      thisExceptions.removeAll(otherExceptions);
      if (!thisExceptions.isEmpty())
      {
         if (containsCheckedException(thisExceptions, thisTypes)) return false;
      }

      otherExceptions.removeAll(thisCopy);
      if (!otherExceptions.isEmpty())
      {
         if (containsCheckedException(otherExceptions, otherTypes)) return false;
      }

      return true;
   }

   private boolean containsCheckedException(ArrayList exceptions, Class[] types)
   {
      for (int i = 0; i < exceptions.size(); ++i)
      {
         String name = (String)exceptions.get(i);
         boolean found = false;
         for (int j = 0; j < types.length; ++j)
         {
            Class type = types[j];
            if (name.equals(type.getName()))
            {
               found = true;
               if (!RuntimeException.class.isAssignableFrom(type)) return true;
            }
         }
         if (!found) throw new IllegalStateException();
      }
      return false;
   }

   public String toString()
   {
      if (toString == null)
      {
         StringBuffer buffer = new StringBuffer(super.toString());
         buffer.append("(");
         for (int i = 0; i < signature.size(); ++i)
         {
            if (i > 0) buffer.append(",");
            buffer.append(signature.get(i));
         }
         buffer.append(")");
         if (exceptions.size() > 0)
         {
            buffer.append(" throws ");
            for (int i = 0; i < exceptions.size(); ++i)
            {
               if (i > 0) buffer.append(",");
               buffer.append(exceptions.get(i));
            }
         }
         toString = buffer.toString();
      }
      return toString;
   }
}
