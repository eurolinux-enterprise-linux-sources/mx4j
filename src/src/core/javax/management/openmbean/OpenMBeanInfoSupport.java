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
import java.util.HashSet;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;

/**
 * @version $Revision: 1.11 $
 */
public class OpenMBeanInfoSupport extends MBeanInfo implements OpenMBeanInfo, Serializable
{
   private static final long serialVersionUID = 4349395935420511492L;

   // No non-transient data members allowed
   private transient int hashCode = 0;

   public OpenMBeanInfoSupport(String className, String description, OpenMBeanAttributeInfo[] openAttributes, OpenMBeanConstructorInfo[] openConstructors, OpenMBeanOperationInfo[] openOperations, MBeanNotificationInfo[] notifications)
   {
      // We cant pass this directly because OpenMBean*Info
      // and friends isn't a direct subclass of their MBean*Info
      // counterpart but the *Support. We need to do an arraycopy
      // for this to work and the implementation should be a
      // subclass of thir MBean*Support counterpart
      super(className, description, createMBeanAttributes(openAttributes), createMBeanConstructors(openConstructors), createMBeanOperations(openOperations), notifications);
   }

   public boolean equals(Object obj)
   {
      if (obj == null) return false;
      if (obj == this) return true;
      if (!(obj instanceof OpenMBeanInfo)) return false;

      OpenMBeanInfo other = (OpenMBeanInfo)obj;
      String thisClassName = getClassName();
      String otherClassName = other.getClassName();
      if (thisClassName != null ? !thisClassName.equals(otherClassName) : otherClassName != null) return false;

      if (!compare(getConstructors(), other.getConstructors())) return false;
      if (!compare(getAttributes(), other.getAttributes())) return false;
      if (!compare(getOperations(), other.getOperations())) return false;
      if (!compare(getNotifications(), other.getNotifications())) return false;

      return true;
   }

   private boolean compare(Object[] o1, Object[] o2) {
      return new HashSet(Arrays.asList(o1)).equals(new HashSet(Arrays.asList(o2)));
   }

   public int hashCode()
   {
      if (hashCode == 0)
      {
         int hash = getClassName() == null ? 0 : getClassName().hashCode();
         if (getConstructors() != null) hash += new HashSet(Arrays.asList(getConstructors())).hashCode();
         if (getAttributes() != null) hash += new HashSet(Arrays.asList(getAttributes())).hashCode();
         if (getOperations() != null) hash += new HashSet(Arrays.asList(getOperations())).hashCode();
         if (getNotifications() != null) hash += new HashSet(Arrays.asList(getNotifications())).hashCode();
         hashCode = hash;
      }
      return hashCode;
   }

   /**
    * Helper Method for OpenMBeanAttributeInfo[] to MBeanAttributeInfo[]
    */
   private static MBeanAttributeInfo[] createMBeanAttributes(OpenMBeanAttributeInfo[] attributes) throws ArrayStoreException
   {
      if (attributes == null) return null;
      MBeanAttributeInfo[] attrInfo = new MBeanAttributeInfo[attributes.length];
      System.arraycopy(attributes, 0, attrInfo, 0, attrInfo.length);
      return attrInfo;
   }

   /**
    * Helper Method for OpenMBeanConstructorInfo[] to MBeanConstructorInfo[]
    */
   private static MBeanConstructorInfo[] createMBeanConstructors(OpenMBeanConstructorInfo[] constructors) throws ArrayStoreException
   {
      if (constructors == null) return null;
      MBeanConstructorInfo[] constInfo = new MBeanConstructorInfo[constructors.length];
      System.arraycopy(constructors, 0, constInfo, 0, constInfo.length);
      return constInfo;
   }

   /**
    * Helper Method for OpenMBeanOperationsInfo[] to MBeanOperationsInfo[]
    */
   private static MBeanOperationInfo[] createMBeanOperations(OpenMBeanOperationInfo[] operations) throws ArrayStoreException
   {
      if (operations == null) return null;
      MBeanOperationInfo[] operInfo = new MBeanOperationInfo[operations.length];
      System.arraycopy(operations, 0, operInfo, 0, operInfo.length);
      return operInfo;
   }
}
