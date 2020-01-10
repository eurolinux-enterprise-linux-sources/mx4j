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
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;

/**
 * @version $Revision: 1.11 $
 */
public class OpenMBeanOperationInfoSupport extends MBeanOperationInfo implements OpenMBeanOperationInfo, Serializable
{
   private static final long serialVersionUID = 4996859732565369366L;

   private OpenType returnOpenType;

   private transient int hashCode = 0;
   private transient String toStringName = null;

   public OpenMBeanOperationInfoSupport(String name, String description, OpenMBeanParameterInfo[] signature, OpenType returntype, int impact)
   {
      super(name, description, (signature == null) ? (MBeanParameterInfo[])Arrays.asList(new OpenMBeanParameterInfo[0]).toArray(new MBeanParameterInfo[0]) : (MBeanParameterInfo[])Arrays.asList(signature).toArray(new MBeanParameterInfo[0]), returntype == null ? "" : returntype.getClassName(), impact);

      // Superclass constructors don't do the necessary validation
      if (name == null || name.length() == 0) throw new IllegalArgumentException("name cannot be null or empty");

      if (description == null || description.length() == 0) throw new IllegalArgumentException("descripiton cannot be null or empty");

      if (returntype == null) throw new IllegalArgumentException("return open type cannont be null");

      if (impact != MBeanOperationInfo.ACTION
          && impact != MBeanOperationInfo.ACTION_INFO
          && impact != MBeanOperationInfo.INFO
          && impact != MBeanOperationInfo.UNKNOWN)
      {
         throw new IllegalArgumentException("invalid impact");
      }

      if (signature != null && signature.getClass().isInstance(MBeanParameterInfo[].class))
      {
         throw new ArrayStoreException("signature elements can't be assigned to MBeanParameterInfo");
      }

      this.returnOpenType = returntype;
   }

   public OpenType getReturnOpenType()
   {
      return returnOpenType;
   }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof OpenMBeanOperationInfo)) return false;

      OpenMBeanOperationInfo other = (OpenMBeanOperationInfo)obj;

      String thisName = getName();
      String otherName = other.getName();
      if (thisName != null ? !thisName.equals(otherName) : otherName != null) return false;

      if (other.getImpact() != getImpact()) return false;

      OpenType thisReturn = getReturnOpenType();
      OpenType otherReturn = other.getReturnOpenType();
      if (thisReturn != null ? !thisReturn.equals(otherReturn) : otherReturn != null) return false;

      if (!Arrays.equals(getSignature(), other.getSignature())) return false;

      return true;
   }

   public int hashCode()
   {
      if (hashCode == 0)
      {
         int result = getName().hashCode();
         result += getReturnOpenType().hashCode();
         result += getImpact();
         result += java.util.Arrays.asList(getSignature()).hashCode();
         hashCode = result;
      }
      return hashCode;
   }

   public String toString()
   {
      if (toStringName == null)
      {
         StringBuffer sb = new StringBuffer();
         sb.append(getClass().getName());
         sb.append("(name=");
         sb.append(getName());
         sb.append(",signature=");
         sb.append(java.util.Arrays.asList(getSignature()).toString());
         sb.append(",returnOpenType=");
         sb.append(returnOpenType.toString());
         sb.append(",impact=");
         sb.append(getImpact());
         sb.append(")");

         toStringName = sb.toString();
      }
      return toStringName;
   }
}
