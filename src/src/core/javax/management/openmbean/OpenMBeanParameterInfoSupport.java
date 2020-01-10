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
import javax.management.MBeanParameterInfo;

/**
 * @version $Revision: 1.9 $
 */
public class OpenMBeanParameterInfoSupport extends MBeanParameterInfo implements OpenMBeanParameterInfo, Serializable
{
   private static final long serialVersionUID = -7235016873758443122L;

   private OpenType openType = null;
   private Object defaultValue = null;
   private Set legalValues = null;
   private Comparable minValue = null;
   private Comparable maxValue = null;

   private transient int m_hashcode = 0;

   public OpenMBeanParameterInfoSupport(String name, String description, OpenType openType)
   {
      super(name, openType == null ? "" : openType.getClassName(), description);
      if (name == null || name.trim().length() == 0) throw new IllegalArgumentException("name parameter cannot be null or an empty string.");
      if (description == null || description.trim().length() == 0) throw new IllegalArgumentException("description parameter cannot be null or an empty string.");
      if (openType == null) throw new IllegalArgumentException("OpenType parameter cannot be null.");
      this.openType = openType;
   }

   public OpenMBeanParameterInfoSupport(String name, String description, OpenType openType, Object defaultValue) throws OpenDataException
   {
      this(name, description, openType);
      if (defaultValue != null)
      {
         if (openType.isArray() || openType instanceof TabularType) throw new OpenDataException("openType should not be an ArrayType or a TabularType when a default value is required.");
         if (!(openType.isValue(defaultValue))) throw new OpenDataException("defaultValue class name " + defaultValue.getClass().getName() + " does not match the one defined in openType.");
         this.defaultValue = defaultValue;
      }
   }

   public OpenMBeanParameterInfoSupport(String name, String description, OpenType openType, Object defaultValue, Object[] legalValues) throws OpenDataException
   {
      this(name, description, openType, defaultValue);
      if (legalValues != null && legalValues.length > 0)
      {
         if (openType.isArray() || openType instanceof TabularType) throw new OpenDataException("legalValues not supported if openType is an ArrayType or an instanceof TabularType");
         for (int i = 0; i < legalValues.length; i++)
         {
            if (!(openType.isValue(legalValues[i]))) throw new OpenDataException("The element at index " + i + " of type " + legalValues[i] + " is not an value specified in openType.");
         }
         // all checked assign Object[] legalValues to set legalValues
         assignLegalValues(legalValues);
         if (hasDefaultValue() && hasLegalValues() && !(this.legalValues.contains(defaultValue))) throw new OpenDataException("LegalValues must contain the defaultValue");
      }
   }

   public OpenMBeanParameterInfoSupport(String name, String description, OpenType openType, Object defaultValue, Comparable minValue, Comparable maxValue) throws OpenDataException
   {
      this(name, description, openType, defaultValue);
      if (minValue != null)
      {
         /** test is a valid value for the specified openType */
         if (!(openType.isValue(minValue))) throw new OpenDataException("Comparable value of " + minValue.getClass().getName() + " does not match the openType value of " + openType.getClassName());
         this.minValue = minValue;
      }
      if (maxValue != null)
      {
         if (!(openType.isValue(maxValue))) throw new OpenDataException("Comparable value of " + maxValue.getClass().getName() + " does not match the openType value of " + openType.getClassName());
         this.maxValue = maxValue;
      }
      if (hasMinValue() && hasMaxValue() && minValue.compareTo(maxValue) > 0) throw new OpenDataException("minValue cannot be greater than maxValue.");
      if (hasDefaultValue() && hasMinValue() && minValue.compareTo(defaultValue) > 0) throw new OpenDataException("minValue cannot be greater than defaultValue.");
      if (hasDefaultValue() && hasMaxValue() && ((Comparable)defaultValue).compareTo(maxValue) > 0) throw new OpenDataException("defaultValue cannot be greater than maxValue.");
   }

   /**
    * Assigns the validated Object[] into the set legal values as the constructor states
    * this is unmodifiable will create an unmodifiable set
    */
   private void assignLegalValues(Object[] legalValues)
   {
      HashSet modifiableSet = new HashSet();
      for (int i = 0; i < legalValues.length; i++)
      {
         modifiableSet.add(legalValues[i]);
      }
      this.legalValues = Collections.unmodifiableSet(modifiableSet);
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
      // if any object is null and they have a defaultValue then isValue is true for anything else null obj returns false (must be first test) as the rest will return false for the null object
      if (hasDefaultValue() && obj == null) return true;

      if (!(openType.isValue(obj))) return false;
      if (hasLegalValues() && (!(legalValues.contains(obj)))) return false;
      if (hasMinValue() && minValue.compareTo(obj) > 0) return false;
      if (hasMaxValue() && maxValue.compareTo(obj) < 0) return false;
      return true;
   }

   public boolean equals(Object obj)
   {
      if (obj == this) return true;
      if (obj == null) return false;
      if (!(obj instanceof OpenMBeanParameterInfo)) return false;

      OpenMBeanParameterInfo paramObj = (OpenMBeanParameterInfo)obj;

      if (!getName().equals(paramObj.getName())) return false;
      if (!getOpenType().equals(paramObj.getOpenType())) return false;

      if (hasDefaultValue() && (!getDefaultValue().equals(paramObj.getDefaultValue()))) return false;
      if (!hasDefaultValue() && paramObj.hasDefaultValue()) return false;

      if (hasMinValue() && !(getMinValue().equals(paramObj.getMinValue()))) return false;
      if (!hasMinValue() && paramObj.hasMinValue()) return false;

      if (hasMaxValue() && !(getMaxValue().equals(paramObj.getMaxValue()))) return false;
      if (!hasMaxValue() && paramObj.hasMaxValue()) return false;

      if (hasLegalValues() && !(getLegalValues().equals(paramObj.getLegalValues()))) return false;
      if (!hasLegalValues() && paramObj.hasLegalValues()) return false;

      return true;
   }

   public int hashCode()
   {
      if (m_hashcode == 0)
      {
         int result = getName().hashCode();
         result += getOpenType().hashCode();
         result += (hasDefaultValue() == false) ? 0 : getDefaultValue().hashCode();
         result += (hasLegalValues() == false) ? 0 : getLegalValues().hashCode();
         result += (hasMinValue() == false) ? 0 : getMinValue().hashCode();
         result += (hasMaxValue() == false) ? 0 : getMaxValue().hashCode();
         m_hashcode = result;
      }
      return m_hashcode;
   }

   public String toString()
   {
      StringBuffer buf = new StringBuffer(getClass().getName());
      buf.append("\t(name = ");
      buf.append(getName());
      buf.append("\topenType = ");
      buf.append(openType.toString());
      buf.append("\tdefault value = ");
      buf.append(String.valueOf(defaultValue));
      buf.append("\tmin value = ");
      buf.append(String.valueOf(minValue));
      buf.append("\tmax value = ");
      buf.append(String.valueOf(maxValue));
      buf.append("\tlegal values = ");
      buf.append(String.valueOf(legalValues));
      buf.append(")");
      return buf.toString();
   }
}
