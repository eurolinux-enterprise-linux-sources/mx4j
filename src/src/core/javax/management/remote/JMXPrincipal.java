/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package javax.management.remote;

import java.io.Serializable;
import java.security.Principal;

/**
 * @version $Revision: 1.6 $
 */
public class JMXPrincipal implements Principal, Serializable
{
   /**
    * @serial The name of this principal
    */
   private String name;

   public JMXPrincipal(String name)
   {
      if (name == null) throw new NullPointerException("Principal name cannot be null");
      this.name = name;
   }

   public String getName()
   {
      return name;
   }

   public int hashCode()
   {
      return getName().hashCode();
   }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;

      try
      {
         JMXPrincipal other = (JMXPrincipal)obj;
         return getName().equals(other.getName());
      }
      catch (ClassCastException x)
      {
      }
      return false;
   }

   public String toString()
   {
      return getName();
   }
}
