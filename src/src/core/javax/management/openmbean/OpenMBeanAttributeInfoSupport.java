/**
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package javax.management.openmbean;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import javax.management.MBeanAttributeInfo;

/**
 * @version $Revision: 1.14 $
 */
public class OpenMBeanAttributeInfoSupport extends MBeanAttributeInfo implements OpenMBeanAttributeInfo, Serializable
{
   private static final long serialVersionUID = -4867215622149721849L;

   private OpenType openType;
   private Object defaultValue = null;
   private Set legalValues = null;
   private Comparable minValue = null;
   private Comparable maxValue = null;

   private transient int hashCode = 0;
   private transient String toStringName = null;

   public OpenMBeanAttributeInfoSupport(String name, String description, OpenType openType, boolean isReadable, boolean isWritable, boolean isIs)
   {
      super(name, openType == null ? "" : openType.getClassName(), description, isReadable, isWritable, isIs);
      if (openType == null)
         throw new IllegalArgumentException("OpenType can't be null");
      if (name == null || name.length() == 0 || name.trim().length() == 0)
         throw new IllegalArgumentException("name can't be null or empty");
      if (description == null || description.length() == 0 || description.trim().length() == 0)
         throw new IllegalArgumentException("description can't be null or empty");

      this.openType = openType;
   }

   public OpenMBeanAttributeInfoSupport(String name, String description, OpenType openType, boolean isReadable, boolean isWritable, boolean isIs, Object defaultValue) throws OpenDataException
   {
      this(name, description, openType, isReadable, isWritable, isIs);

      if (openType instanceof ArrayType || openType instanceof TabularType)
      {
         if (defaultValue != null)
            throw new OpenDataException("defaultValue is not supported for ArrayType and TabularType. Should be null");
      }

      if (defaultValue != null && !openType.isValue(defaultValue))
         throw new OpenDataException("defaultValue is not a valid value for the given OpenType");

      this.defaultValue = defaultValue;
   }

   public OpenMBeanAttributeInfoSupport(String name, String description, OpenType openType, boolean isReadable, boolean isWritable, boolean isIs, Object defaultValue, Object[] legalValues) throws OpenDataException
   {
      this(name, description, openType, isReadable, isWritable, isIs, defaultValue);

      if (openType instanceof ArrayType || openType instanceof TabularType)
      {
         if (legalValues != null && legalValues.length > 0)
            throw new OpenDataException("legalValues isn't allowed for ArrayType and TabularType. Should be null or empty array");
      }
      else if (legalValues != null && legalValues.length > 0)
      {
         Set tmpSet = new HashSet(legalValues.length);

         for (int i = 0; i < legalValues.length; i++)
         {
            Object lv = legalValues[i];
            if (openType.isValue(lv))
            {
               tmpSet.add(lv);
            }
            else
            {
               throw new OpenDataException("An Entry in the set of legalValues is not a valid value for the given opentype");
            }
         }

         if (defaultValue != null && !tmpSet.contains(defaultValue))
         {
            throw new OpenDataException("The legal value set must include the default value");
         }

         this.legalValues = Collections.unmodifiableSet(tmpSet);
      }
   }

   public OpenMBeanAttributeInfoSupport(String name, String description, OpenType openType, boolean isReadable, boolean isWritable, boolean isIs, Object defaultValue, Comparable minValue, Comparable maxValue) throws OpenDataException
   {
      this(name, description, openType, isReadable, isWritable, isIs, defaultValue);

      if (minValue != null)
         if (!openType.isValue(minValue))
            throw new OpenDataException("minValue is not a valid value for the specified openType");

      if (maxValue != null)
         if (!openType.isValue(maxValue))
            throw new OpenDataException("maxValue is not a valid value for the specified openType");

      if (minValue != null && maxValue != null)
         if (minValue.compareTo(maxValue) > 0)
            throw new OpenDataException("minValue and/or maxValue is " +
                                        "invalid: minValue is greater than maxValue");
      if (defaultValue != null && minValue != null)
         if (minValue.compareTo(defaultValue) > 0)
            throw new OpenDataException("defaultvalue and/or minValue is invalid: minValue is greater than defaultValue");

      if (defaultValue != null && maxValue != null)
         if (((Comparable)defaultValue).compareTo(maxValue) > 0)
            throw new OpenDataException("defaultvalue and/or maxValue is invalid: defaultValue is greater than maxValue");

      this.minValue = minValue;
      this.maxValue = maxValue;
   }

   public OpenType getOpenType()
   {
      return openType;
   }

   public Object getDefaultValue()
   {
      return defaultValue;
   }

   public Set getLegalValues()
   {
      return legalValues;
   }

   public Comparable getMinValue()
   {
      return minValue;
   }

   public Comparable getMaxValue()
   {
      return maxValue;
   }

   public boolean hasDefaultValue()
   {

      return defaultValue != null;
   }

   public boolean hasLegalValues()
   {
      return legalValues != null;
   }

   public boolean hasMinValue()
   {
      return minValue != null;
   }

   public boolean hasMaxValue()
   {
      return maxValue != null;
   }

   public boolean isValue(Object obj)
   {
      if (defaultValue != null)
      {
         if (openType.isValue(obj)) return true;
      }
      else
      {
         if (obj == null) return true;
      }

      return false;
   }

   public boolean equals(Object obj)
   {
      if (obj == this) return true;
      // obj should not be null
      if (obj == null) return false;
      // obj should implement OpenMBeanAttributeInfo
      if (!(obj instanceof OpenMBeanAttributeInfo)) return false;

      OpenMBeanAttributeInfo other = (OpenMBeanAttributeInfo)obj;
      if (!getName().equals(other.getName())) return false;
      if (!getOpenType().equals(other.getOpenType())) return false;
      if (isReadable() != other.isReadable()) return false;
      if (isWritable() != other.isWritable()) return false;
      if (isIs() != other.isIs()) return false;

      if (hasDefaultValue())
      {
         if (!getDefaultValue().equals(other.getDefaultValue())) return false;
      }
      else
      {
         if (other.hasDefaultValue()) return false;
      }

      if (hasMinValue())
      {
         if (!getMinValue().equals(other.getMinValue())) return false;
      }
      else
      {
         if (other.hasMinValue()) return false;
      }

      if (hasMaxValue())
      {
         if (!getMaxValue().equals(other.getMaxValue())) return false;
      }
      else
      {
         if (other.hasMaxValue()) return false;
      }

      if (hasLegalValues())
      {
         if (!getLegalValues().equals(other.getLegalValues())) return false;
      }
      else
      {
         if (other.hasLegalValues()) return false;
      }

      return true;
   }

   public int hashCode()
   {
      if (hashCode == 0)
      {
         int result = getName().hashCode();
         result += getOpenType().hashCode();
         result += (hasDefaultValue() == false) ? 0 : getDefaultValue().hashCode();
         result += (hasLegalValues() == false) ? 0 : getLegalValues().hashCode();
         result += (hasMinValue() == false) ? 0 : getMinValue().hashCode();
         result += (hasMaxValue() == false) ? 0 : getMaxValue().hashCode();
         hashCode = result;
      }
      return hashCode;
   }

   public String toString()
   {
      if (toStringName == null)
      {
         StringBuffer sb = new StringBuffer(getClass().getName());
         sb.append("(name=");
         sb.append(getName());
         sb.append(", opentype=");
         sb.append(openType.toString());
         sb.append(", defaultValue=");
         sb.append(hasDefaultValue() ? getDefaultValue().toString() : "null");
         sb.append(", minValue=");
         sb.append(hasMinValue() ? getMinValue().toString() : "null");
         sb.append(", maxValue=");
         sb.append(hasMaxValue() ? getMaxValue().toString() : "null");
         sb.append(", legalValues=");
         sb.append(hasLegalValues() ? getLegalValues().toString() : "null");
         sb.append(")");
         toStringName = sb.toString();
      }
      return toStringName;
   }
}
