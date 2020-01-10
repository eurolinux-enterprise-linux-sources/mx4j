/**
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package javax.management.openmbean;

import java.io.Serializable;
import java.util.Arrays;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanParameterInfo;

/**
 * @version $Revision: 1.6 $
 */
public class OpenMBeanConstructorInfoSupport extends MBeanConstructorInfo implements OpenMBeanConstructorInfo, Serializable
{
   private static final long serialVersionUID = -4400441579007477003L;

   // No non-transient fields allowed
   private transient int m_hashcode = 0;

   public OpenMBeanConstructorInfoSupport(String name, String description, OpenMBeanParameterInfo[] signature)
   {
      super(name, description, signature == null ? null : (MBeanParameterInfo[])Arrays.asList(signature).toArray(new MBeanParameterInfo[0]));
      if (name == null || name.trim().length() == 0) throw new IllegalArgumentException("name parameter cannot be null or an empty string");
      if (description == null || description.trim().length() == 0) throw new IllegalArgumentException("description parameter cannot be null or an empty string");
   }

   public boolean equals(Object obj)
   {
      if (!(obj instanceof OpenMBeanConstructorInfo)) return false;
      OpenMBeanConstructorInfo toCompare = (OpenMBeanConstructorInfo)obj;
      return (getName().equals(toCompare.getName()) && Arrays.equals(getSignature(), toCompare.getSignature()));
   }

   public int hashCode()
   {
      if (m_hashcode == 0)
      {
         int result = getName().hashCode();
         result += Arrays.asList(getSignature()).hashCode();
         m_hashcode = result;
      }
      return m_hashcode;
   }

   public String toString()
   {
      return (getClass().getName() + " ( name = " + getName() + " signature = " + Arrays.asList(getSignature()).toString() + " )");
   }
}
