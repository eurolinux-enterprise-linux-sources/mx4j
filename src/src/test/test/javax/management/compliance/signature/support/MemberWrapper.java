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
import java.util.Collections;

/**
 * @version $Revision: 1.3 $
 */
public abstract class MemberWrapper
{
   protected int modifiers;
   protected String type;
   protected String name;
   protected String toString;

   protected MemberWrapper()
   {
   }

   public int hashCode()
   {
      return toString().hashCode();
   }

   public boolean equals(Object obj)
   {
      if (obj == this) return true;
      if (obj == null) return false;
      return toString().equals(obj.toString());
   }

   public String toString()
   {
      if (toString == null)
      {
         StringBuffer buffer = new StringBuffer(Modifier.toString(modifiers)).append(" ");
         buffer.append(type).append(" ");
         buffer.append(name);
         toString = buffer.toString();
      }
      return toString;
   }

   protected ArrayList convert(Class[] classes, boolean sort)
   {
      ArrayList list = new ArrayList();
      for (int i = 0; i < classes.length; ++i)
      {
         list.add(classes[i].getName());
      }

      if (sort) Collections.sort(list);

      return list;
   }
}
