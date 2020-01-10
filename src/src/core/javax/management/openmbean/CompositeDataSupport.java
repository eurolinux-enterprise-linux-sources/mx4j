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
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

public class CompositeDataSupport implements CompositeData, Serializable
{
   private static final long serialVersionUID = 8003518976613702244L;

   private SortedMap contents = new TreeMap();
   private CompositeType compositeType;

   private transient int m_hashcode = 0;

   /**
    * Constructs a CompositeDataSupport instance with the specified compositeType, whose item values are specified by itemValues[], in the same order as in itemNames[].
    * As a CompositeType does not specify any order on its items, the itemNames[] parameter is used to specify the order in which the values are given in itemValues[].
    * The items contained in this CompositeDataSupport instance are internally stored in a TreeMap, thus sorted in ascending lexicographic order of their names,
    * for faster retrieval of individual item values.
    * The constructor checks that all the constrainsts listed below for each parameter are satisfied, and throws the appropriate exception if they are not
    *
    * @param compositeType - the composite type of this composite data instance; must not be null.
    * @param itemNames     - itemNames must list, in any order, all the item names defined in compositeType;
    *                      the order in which the names are listed, is used to match values in itemValues[]; must not be null or empty.
    * @param itemValues    - the values of the items, listed in the same order as their respective names in itemNames; each item value can be null,
    *                      but if it is non-null it must be a valid value for the open type defined in compositeType for the corresponding item;
    *                      must be of the same size as itemNames; must not be null or empty.
    * @throws IllegalArgumentException if compositeType is null, or itemNames[] or itemValues[] is null or empty, or one of the elements in itemNames[] is a null or empty string,
    *                                  or itemNames[] and itemValues[] are not of the same size.
    * @throws OpenDataException        if itemNames[] or itemValues[]'s size differs from the number of items defined in compositeType, or one of the elements in itemNames[] does not exist
    *                                  as an item name defined in compositeType, or one of the elements in itemValues[] is not a valid value for the corresponding item as defined in compositeType
    */
   public CompositeDataSupport(CompositeType compositeType, String[] itemNames, Object[] itemValues) throws OpenDataException
   {
      init(compositeType, itemNames, itemValues);
   }

   /**
    * Constructs a CompositeDataSupport instance with the specified compositeType, whose items names and corresponding values are given by the mappings in the map items.
    * This constructor converts the keys to a string array and the values to an object array and calls
    * CompositeDataSupport(javax.management.openmbean.CompositeType, java.lang.String[], java.lang.Object[]).
    *
    * @param compositeType - the composite type of this composite data instance; must not be null.
    * @param items         - the mappings of all the item names to their values; items must contain all the item names defined in compositeType; must not be null or empty
    * @throws IllegalArgumentException - if compositeType is null, or items is null or empty, or one of the keys in items is a null or empty string, or one of the values in items is null
    * @throws OpenDataException        - if items' size differs from the number of items defined in compositeType, or one of the keys in items does not exist as an item name defined in compositeType,
    *                                  or one of the values in items is not a valid value for the corresponding item as defined in compositeType.
    * @throws ArrayStoreException      - if any of the keys in items cannot be cast to a String
    */
   public CompositeDataSupport(CompositeType compositeType, Map items) throws OpenDataException
   {
      init(compositeType, items != null ? (String[])items.keySet().toArray(new String[items.size()]) : null, items != null ? items.values().toArray() : null);
   }

   /**
    * do all the work of the constructor so radObject can validate all invariants by calling the method
    */
   private void init(CompositeType compositeType, String[] itemNames, Object[] itemValues) throws OpenDataException
   {
      if (compositeType == null) throw new IllegalArgumentException("Null CompositeType is not an acceptable value");
      if (itemNames == null || itemNames.length == 0) throw new IllegalArgumentException("ItemNames cannot be null or empty (zero length)");
      if (itemValues == null || itemValues.length == 0) throw new IllegalArgumentException("ItemValues cannot be null or empty (zero length)");
      if (itemNames.length != itemValues.length) throw new IllegalArgumentException("Both the itemNames and itemValues arrays must be of equals length");

      // now validate the the contents itemNames must be of the same length and contain all items present in the compositeType keys
      validateTypes(compositeType, itemNames);

      // check itemValues are valid values for openTypes
      validateContents(compositeType, itemNames, itemValues);

      // valid now assign compositeType
      this.compositeType = compositeType;

      // add the validated values and keys to the sortedMap
      createMapData(itemNames, itemValues);
   }

   private void validateContents(CompositeType compositeType,
                                 String[] itemNames,
                                 Object[] itemValues)
           throws OpenDataException
   {
      for (int i = 0; i < itemValues.length; i++)
      {
         if (itemValues[i] != null)
         {
            OpenType openType = compositeType.getType(itemNames[i]);
            if (!(openType.isValue(itemValues[i])))
               throw new OpenDataException("itemValue at index "
                                           + i
                                           + " is not a valid value for itemName "
                                           + itemNames[i]
                                           + " and itemType "
                                           + openType);
         }
      }
   }

   /**
    * validates that the itemNames are present (in full) in the keySet of the compositeType
    */
   private void validateTypes(CompositeType compositeType, String[] itemNames) throws OpenDataException
   {
      for (int i = 0; i < itemNames.length; i++)
      {
         if (itemNames[i] == null || itemNames[i].trim().equals("")) throw new IllegalArgumentException("Value of itemName at [" + i + "] is null or empty, unacceptable values");
      }
      Set keyTypes = compositeType.keySet();
      if (itemNames.length != keyTypes.size()) throw new OpenDataException("The size of array arguments itemNames[] and itemValues[] should be equal to the number of items defined in argument compositeType");
      if (!(Arrays.asList(itemNames).containsAll(keyTypes))) throw new OpenDataException("itemNames[] does not contain all names defined in the compositeType of this instance.");
   }

   /**
    * fill the SortedMap with it's keys and values keys are the String[] itemNames and the values are the Object[] itemValues consisting of openTypes
    */
   private void createMapData(String[] itemNames, Object[] itemValues)
   {
      for (int i = 0; i < itemNames.length; i++)
      {
         contents.put(itemNames[i], itemValues[i]);
      }
   }

   /**
    * @return the composite type of this composite data instance
    */
   public CompositeType getCompositeType()
   {
      return compositeType;
   }

   /**
    * @param key - the key for which to return the value
    * @return - the value of the item whose name is key
    * @throws IllegalArgumentException if key is null or an empty String
    * @throws InvalidKeyException      if key is not an existing item name for this CompositeData instance
    */
   public Object get(String key)
   {
      if (key == null || key.trim().equals("")) throw new IllegalArgumentException("Null or empty key");
      if (!(contents.containsKey(key.trim()))) throw new InvalidKeyException("Key with value " + key + " is not a current stored key in this instance");
      return contents.get(key.trim());
   }

   /**
    * Returns an array of the values of the items whose names are specified by keys, in the same order as keys this method simple calls get for each key
    *
    * @param the array of keys for which to return the corresponding values in the same order as specified in the keys
    * @return the resulting array of values found
    * @throws IllegalArgumentException if an element in keys is null or an empty String
    * @throws InvalidKeyException      if a key is not currently stored in this instances map
    */
   public Object[] getAll(String[] keys)
   {
      /** should this throw an exception or under following conditions merely return an empty object[0] ?? */
      if (keys == null || keys.length == 0) return new Object[0];
      Object[] dataMapValues = new Object[keys.length];
      for (int i = 0; i < keys.length; i++)
      {
         dataMapValues[i] = get(keys[i]);
      }
      return dataMapValues;
   }

   /**
    * Returns true if and only if this CompositeData instance contains an item whose name is key. If key is a null or empty String, this method simply returns false
    *
    * @param key the key for which to find a value
    * @return true if value found false otherwise
    */
   public boolean containsKey(String key)
   {
      if (key == null || key.trim().equals("")) return false;
      return contents.containsKey(key);
   }

   /**
    * Returns true if and only if this CompositeData instance contains an item whose value is value
    *
    * @param value - the value to determine if present
    * @return true if found false otherwise
    */
   public boolean containsValue(Object value)
   {
      return contents.containsValue(value);
   }

   /**
    * Returns an unmodifiable Collection view of the item values contained in this CompositeData instance. The returned collection's iterator will return the values in the
    * ascending lexicographic order of the corresponding item names.
    *
    * @return unmodifiable collection of current values
    */
   public Collection values()
   {
      return Collections.unmodifiableCollection(contents.values());
   }

   /**
    * tests that the Object obj is equal to this compositeData instance
    *
    * @param obj - the Object to test if is equals
    * @return true if and only if
    *         <ul>
    *         <li>obj is an instanceof CompositeData as tested by instanceof CompositeData</li>
    *         <li>all of the name/value pairs in the objects map are equal to the name/value pairs of this instance</li>
    *         <li>if the compositeTypes are equal</li>
    *         </ul>
    */
   public boolean equals(Object obj)
   {
      if (!(obj instanceof CompositeData))
         return false;
      CompositeData compositeData = (CompositeData)obj;
      boolean result =
              getCompositeType().equals(compositeData.getCompositeType());
      if (result)
      {
         Iterator i = contents.entrySet().iterator();
         while (i.hasNext() && result)
         {
            Map.Entry entry = (Map.Entry)i.next();
            String key = (String)entry.getKey();
            Object entryvalue = entry.getValue();
            Object cdvalue = compositeData.get(key);
            if (entryvalue == null)
            {
               result = (cdvalue == null);
            }
            else
            {
               result = entryvalue.equals(cdvalue);
            }
         }
      }
      return result;
   }

   /**
    * Using the same information as in equals test to create the hashcode
    * i.e) The hash code of a CompositeDataSupport instance is the sum of the hash codes of all elements of information used in equals comparisons (ie: its composite type and all the item values).
    *
    * @return the calculated HashCode for this Object
    */
   public int hashCode()
   {
      if (m_hashcode == 0)
      {
         int result = getCompositeType().hashCode();
         for (Iterator i = contents.entrySet().iterator(); i.hasNext();)
         {
            Map.Entry entry = (Map.Entry)i.next();
            if (entry.getValue() != null) result += entry.getValue().hashCode();
         }
         m_hashcode = result;
      }
      return m_hashcode;
   }

   /**
    * The string representation consists of the name of this class (ie javax.management.openmbean.CompositeDataSupport),
    * the string representation of the composite type of this instance, and the string representation of the contents (ie list the itemName=itemValue mappings).
    *
    * @return a string representation of this CompositeDataSupport instance.
    */
   public String toString()
   {
      StringBuffer buffer = new StringBuffer(getClass().getName());
      buffer.append("\tCompositeType = ");
      buffer.append(compositeType.toString());
      buffer.append("\tcontents are: ");
      buffer.append(contents.toString());
      return buffer.toString();
   }
}
