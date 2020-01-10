/**
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package javax.management.openmbean;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @version $Revision: 1.10 $
 */

/**
 * The TabularDataSupport class is the open data class which implements the TabularData and the
 * Map interfaces, and which is internally based on a hash map data structure
 */
public class TabularDataSupport implements TabularData, Map, Cloneable, Serializable
{
   private static final long serialVersionUID = 5720150593236309827L;

   private Map dataMap;
   private TabularType tabularType;

   private transient String[] m_indexNames;

   /**
    * Creates an empty TabularDataSupport instance whose open-type is tabularType, and whose underlying HashMap has a default initial capacity (101) and default load factor (0.75).
    *
    * @param tabularType the TabularTypeBackUP describing this TabularData instance, cannot be null
    */
   public TabularDataSupport(TabularType tabularType)
   {
      this(tabularType, 101, 0.75f);
   }

   /**
    * Creates an empty TabularDataSupport instance whose open-type is tabularType, and whose underlying HashMap has the specified initial capacity and load factor
    *
    * @param tabularType     - the tabular type describing this instance, cannot be null;
    * @param initialCapacity - the initial capacity of the Map
    * @param loadFactor      - the load factor of the Map
    * @throws IllegalArgumentException if the initialCapacity is less than zero, the load factor is negative or the tabularType is null
    */
   public TabularDataSupport(TabularType tabularType, int initialCapacity, float loadFactor)
   {
      if (tabularType == null) throw new IllegalArgumentException("TabularTypeBackUP instance cannot be null.");
      if (initialCapacity < 0) throw new IllegalArgumentException("The initialCapacity cannot be a negative number.");
      if (loadFactor < 0) throw new IllegalArgumentException("The load factor cannot be a negative number.");

      this.tabularType = tabularType;
      this.dataMap = new HashMap(initialCapacity, loadFactor);
      initialize();
   }

   /**
    * can be called by the readObject method to initialize the deserialized instance
    */
   private void initialize()
   {
      // get the index names list from the tabular type as these will act as the unique index into each row of the TabularData instance
      List tabularIndexList = tabularType.getIndexNames();
      m_indexNames = (String[])tabularIndexList.toArray(new String[tabularIndexList.size()]);
   }

   /**
    * get the TabularType that this instance contains
    *
    * @return TabularType describing this TabularData instance
    */
   public TabularType getTabularType()
   {
      return tabularType;
   }

   /**
    * <p>Calculates the index that would be used in this TabularData instance to refer to the specified composite data value parameter is it were added
    * to this instance.This method checks for the type validity of the specified value, but does not check if the calculated index is already used to refer to a
    * value, in this tabular data instance</p>
    *
    * @param value - the CompositeData value whose index in this TabularData instance is to be calculated, must be of the same composite type as this instance's row type;
    *              must not be null.
    * @return - the index that the specified <b><i>value would have</i></b> in this tabularData instance
    * @throws NullPointerException     if value is null
    * @throws InvalidOpenTypeException if value does not conform to this tabular data instances' row type definition
    */
   public Object[] calculateIndex(CompositeData value)
   {
      if (value == null) throw new NullPointerException("CompositeData object cannot be null");
      if (!(value.getCompositeType().equals(tabularType.getRowType()))) throw new InvalidOpenTypeException("Invalid CompositeData object, its' tabularType is not equal to the row type of this TabularType instance");
      return Collections.unmodifiableList(Arrays.asList(value.getAll(m_indexNames))).toArray();
   }

   /**
    * returns true if and only if this tabularData instance contains a compositeData value (i.e. a row) whose index is the specified key. If key cannot be cast to a one dimension
    * array of Object instances, this method returns false, otherwise it returns the result of the call this.containsKey((Object[]) key)
    *
    * @param key - the index value
    * @return true if a CompositeData value is found false otherwise, false if key cannot be cast to an Object[]
    */
   public boolean containsKey(Object key)
   {
      if (!(key instanceof Object[])) return false;
      return containsKey((Object[])key);
   }

   /**
    * Returns true if and only if this TabularData instance contains a CompositeData value (ie a row) whose index is the specified key. If key is null or does not conform to this
    * TabularData instance's TabularType definition, this method simply returns false.
    *
    * @param key the index value whose presence in this TabularData instance is to be tested
    * @return true if it is found false otherwise
    */
   public boolean containsKey(Object[] key)
   {
      if (key == null) return false;
      return dataMap.containsKey(Arrays.asList(key));
   }

   /**
    * Returns true if and only if this TabularData instance contains the specified CompositeData value. If value is null or does not conform to
    * this TabularData instance's row type definition, this method simply returns false.
    *
    * @param value - the row value whose presence in this TabularData instance is to be tested
    * @return true if this instance contains the specified value
    */
   public boolean containsValue(CompositeData value)
   {
      return dataMap.containsValue(value);
   }

   /**
    * Returns true if and only if this TabularData instance contains the specified value.
    *
    * @param value - the row value whose presence in this TabularData instance is to be tested
    * @return true if this TabularData instance contains the specified row value
    */
   public boolean containsValue(Object value)
   {
      return dataMap.containsValue(value);
   }

   /**
    * This method simply calls get((Object[]) key).
    *
    * @param key - the key for which to lookup the value
    * @return the Object found for key "key"
    */
   public Object get(Object key)
   {
      return get((Object[])key);
   }

   /**
    * This method validates the key Object[] parameter. It cannot be null, or empty. It must be the same length as the indexNames of the tabularType passed into this instances
    * constructor, and must be a valid value i.e the value returned by the method call OpenType isValue(Object obj) must be true
    *
    * @param key - the Object[] key stored as a key in this instance
    * @return - the CompositeData value corresponding to the key
    * @throws NullPointerException if key is null or empty
    * @throws InvalidKeyException  if an item in the array returns false for call OpenType isValue(Object o)
    */
   public CompositeData get(Object[] key)
   {
      validateKeys(key);
      return (CompositeData)dataMap.get(Arrays.asList(key));
   }

   /**
    * This method simply calls put((CompositeData) value) and therefore ignores its key parameter which can be null
    *
    * @return the value that is put
    * @throws ClassCastException if value is not an instanceof CompositeData
    */
   public Object put(Object key, Object value)
   {
      put((CompositeData)value);
      return value;
   }

   /**
    * Adds "value" to this TabularData instance, if value's composite type is the same as this instance's row type
    * (ie the composite type returned by this.getTabularType().getRowType()), and if there is not already an existing value in this TabularData
    * instance whose index is the same as the one calculated for the value to be added. The index for value is calculated according to this TabularData instance's
    * TabularType definition @see javax.management.openmbean.TabularType#getIndexNames()
    * <p/>
    * This method calls calculateIndex(CompositeData value) which validates value the returned Object[] is then converted into an unmodifiableList and stored
    *
    * @param value - the composite data value to be added as a new row to this TabularData instance; must be of the same composite type as this instance's row type; must not be null.
    * @throws NullPointerException      if value is null
    * @throws InvalidOpenTypeException  if value does not conform to this TabularData instance's row type definition
    * @throws KeyAlreadyExistsException if the underlying HashMap already contains the calculated key
    */
   public void put(CompositeData value)
   {
      // calculateIndex method tests for null and invalid rowType
      List list = Collections.unmodifiableList(Arrays.asList(calculateIndex(value)));
      if (dataMap.containsKey(list)) throw new KeyAlreadyExistsException("The list of index names already exists in this instance");
      dataMap.put(list, value);
   }

   /**
    * This method simply calls remove((Object[]) key).
    *
    * @param key - the Object to remove note no checks are done if key can be cast to Object[] hence if not a ClassCastException will be thrown
    * @throws ClassCastException if key cannot be cast to an Object[]
    */
   public Object remove(Object key)
   {
      return remove((Object[])key);
   }

   /**
    * Method validates the key checking for null, zero length and if items in the key are invalid as returned by @see OpenType isValue(Object obj)
    *
    * @param key - the index of the value to get in this TabularData instance; must be valid with this TabularData instance's row type definition; must not be null.
    * @return previous value associated with specified key, or null if there was no mapping for key
    * @throws NullPointerException if key is null
    * @throws InvalidKeyException  if the key does not conform to this TabularData instance's TabularType definition
    */
   public CompositeData remove(Object[] key)
   {
      validateKeys(key);
      return (CompositeData)dataMap.remove(Arrays.asList(key));
   }

   /**
    * Add all the values contained in the specified map t to this TabularData instance. This method converts the collection of values contained in this map into an
    * array of CompositeData values, if possible, and then calls the method putAll(CompositeData[]). Note that the keys used in the specified map t are ignored.
    * This method allows, for example to add the content of another TabularData instance with the same row type (but possibly different index names) into this instance.
    *
    * @param t - the map whose values are to be added as new rows to this TabularData instance; if t is null or empty, this method returns without doing anything
    * @throws NullPointerException      - if a "value" in t is null
    * @throws ClassCastException        - if a value in t is not an instanceof CompositeData this is a wrapper around the RuntimeException ArrayStoreException
    *                                   which is generated when an attempt has been made to store the wrong type of object into an array of objects.
    * @throws InvalidOpenTypeException  - if a value in t does not conform to this TabularData instance's row type definition
    * @throws KeyAlreadyExistsException - if the index for a value in t, calculated according to this TabularData instance's TabularType definition
    *                                   already maps to an existing value in this instance, or two values in t have the same index.
    */
   public void putAll(Map t)
   {
      if (t == null || t.size() == 0) return;
      CompositeData[] compositeData;
      try
      {
         compositeData = (CompositeData[])t.values().toArray(new CompositeData[t.size()]);
      }
      catch (ArrayStoreException e)
      {
         throw new ClassCastException("The values contained in t must all be of type CompositeData");
      }
      putAll(compositeData);
   }

   /**
    * Adds all the elements in values to this TabularData instance. If any element in values does not satisfy the constraints defined in put, or if any two elements in values
    * have the same index calculated according to this TabularData instance's TabularType definition, then an exception describing the failure is thrown
    * and no element of values is added, thus leaving this TabularData instance unchanged.
    *
    * @param values - the array of composite data values to be added as new rows to this TabularData instance; if values is null or empty, this method returns without doing anything
    * @throws NullPointerException      - if an element of values is null
    * @throws InvalidOpenTypeException  - if an element of values does not conform to this TabularData instance's row type definition (ie its TabularType definition)
    * @throws KeyAlreadyExistsException - if the index for an element of values, calculated according to this TabularData instance's TabularType definition already
    *                                   maps to an existing value in this instance, or two elements of values have the same index
    */
   public void putAll(CompositeData[] values)
   {
      // validate values
      if (values == null || values.length == 0) return;

      /** creating a list to store all the keys obtained from method calculateIndex this is to check that no keys are duplicated */
      List storeList = validateNoDuplicates(values);

      /** Once we have validated that there are no duplicated keys we can add them to the global map if a duplicate is found an exception is thrown leaving instance unchanged */
      for (int i = 0; i < values.length; i++)
      {
         dataMap.put(storeList.get(i), values[i]);
      }
   }

   /**
    * test that the list returned from calculateIndex for each CompositeData object has no duplicates
    */
   private List validateNoDuplicates(CompositeData[] values)
   {
      List storeList = new ArrayList();
      for (int i = 0; i < values.length; i++)
      {
         List list = Collections.unmodifiableList(Arrays.asList(calculateIndex(values[i])));

         /** check no duplicate keys if there is we have an invalid key */
         if (storeList.contains(list)) throw new KeyAlreadyExistsException("value at [" + i + "] has the same index values as: " + storeList.indexOf(list));

         /** store it to check the next values are not duplicated */
         storeList.add(list);
      }
      return storeList;
   }

   /**
    * clears this instances internal map
    */
   public void clear()
   {
      dataMap.clear();
   }

   /**
    * @return the size of the map
    */
   public int size()
   {
      return dataMap.size();
   }

   /**
    * @return true if the map is empty false if not empty
    */
   public boolean isEmpty()
   {
      return dataMap.isEmpty();
   }

   /**
    * Returns a set view of the keys contained in the underlying map of this TabularDataSupport instance, and used to index the rows.
    * Each key contained in this set is an unmodifiable List. The set is backed by the underlying map of this TabularDataSupport instance,
    * so changes to the TabularDataSupport instance are reflected in the set, and vice-versa. The set supports element removal, which removes the
    * corresponding row from this TabularDataSupport instance, via the Iterator.remove, Set.remove, removeAll, retainAll, and clear operations.
    * It does not support the add or addAll operations
    *
    * @return a set view of the keys used to index the rows of this TabularDataSupport instance
    */
   public Set keySet()
   {
      return dataMap.keySet();
   }

   /**
    * Returns a collection view of the rows contained in this TabularDataSupport instance. The collection is backed by the underlying map,
    * so changes to the TabularDataSupport instance are reflected in the collection, and vice-versa. The collection supports element removal,
    * which removes the corresponding index to row mapping from this TabularDataSupport instance, via the Iterator.remove, Collection.remove, removeAll,
    * retainAll, and clear operations. It does not support the add or addAll operations
    *
    * @return a collection view of the values contained in this TabularDataSupport instance.
    */
   public Collection values()
   {
      return dataMap.values();
   }

   /**
    * Returns a collection view of the index to row mappings contained in this TabularDataSupport instance. Each element in the returned collection is a Map.Entry.
    * The collection is backed by the underlying map of this TabularDataSupport instance, in so changes to the TabularDataSupport instance are reflected the collection, and vice-versa.
    * The collection supports element removal, which removes the corresponding mapping from the map, via the Iterator.remove, Collection.remove, removeAll, retainAll, and clear operations.
    * It does not support the add or addAll operations.
    * <p><b>NOTE</b> Do not use the SetValue method of Map.Entry elements contained in the returned collection view.
    * Doing so would corrupt the index to row mappings contained in this TabularDataSupport instance</p>
    *
    * @return a collection view of the mappings contained in this map
    */
   public Set entrySet()
   {
      return dataMap.entrySet();
   }

   /**
    * Returns a clone of this TabularDataSupport instance: the clone is obtained by calling super.clone(),
    * and then cloning the underlying map. Only a shallow clone of the underlying map is made, i.e. no cloning of the indexes and row values is made as they are immutable!!
    *
    * @return a copy of the TabularDataSupport
    */
   public Object clone()
   {
      TabularDataSupport dataSupportClone = null;
      try
      {
         dataSupportClone = (TabularDataSupport)super.clone();
         dataSupportClone.dataMap = (HashMap)((HashMap)dataMap).clone();
      }
      catch (CloneNotSupportedException e)
      {
         // shouldn't happen
         return null;
      }
      return dataSupportClone;
   }

   /**
    * Compares the specified obj parameter with this TabularDataSupport instance for equality
    *
    * @return true if and only if all of the following statements are true:
    *         <ul>
    *         <li><obj is instanceof TabularData/li>
    *         <li>their tabular types are equal</li>
    *         <li><their contents (ie all CompositeData values) are equal/li>
    *         </ul>
    */
   public boolean equals(Object obj)
   {
      if (!(obj instanceof TabularData)) return false;
      TabularData tabularData = (TabularData)obj;
      for (Iterator i = values().iterator(); i.hasNext();)
      {
         CompositeData compositeData = (CompositeData)i.next();
         if (!(tabularData.containsValue(compositeData))) return false;
      }
      return ((getTabularType().equals(tabularData.getTabularType()) && (size() == tabularData.size())));
   }

   /**
    * The hash code of a TabularDataSupport instance is the sum of the hash codes of all elements of information used in equals comparisons
    * (ie: its tabular type and its content, where the content is defined as all the CompositeData values).
    *
    * @return the calculated hashCode for this object
    */
   public int hashCode()
   {
      int result = tabularType.hashCode();
      for (Iterator i = values().iterator(); i.hasNext();)
      {
         result += i.next().hashCode();
      }
      return result;

   }

   /**
    * The string representation consists of the name of this class (ie com.sun.jdmk.TabularDataSupport), the string representation of the tabular type of this instance,
    * and the string representation of the contents (ie list the key=value mappings as returned by a call to dataMap.toString())
    *
    * @return a string representation of this TabularDataSupport instance
    */
   public String toString()
   {
      return getClass().getName() + "(tabularType = " + tabularType.toString() + ",contains = " + dataMap.toString() + ")";
   }

   /**
    * validate the Object[] of keys they cannot be null or empty. The length must be the same length as the indexNames used for the indexing rows
    */
   private void validateKeys(Object[] key)
   {
      if (key == null || key.length == 0) throw new NullPointerException("Object[] key cannot be null or of zero length");
      if (key.length != m_indexNames.length) throw new InvalidKeyException("Length of Object[] passed in as a parameter is not equal to the number of items: " + m_indexNames.length + " as specified for the indexing rows in this instance.");
      for (int i = 0; i < key.length; i++)
      {
         OpenType openType = tabularType.getRowType().getType(m_indexNames[i]);
         if ((key[i] != null) && (!(openType.isValue(key[i])))) throw new InvalidKeyException("expected value is: " + openType + " at index: " + i + " but got: " + key[i]);
      }
   }

   /**
    * acts as a second constructor so validate all invariants and recreate the object in the correct state
    */
   private void readObject(ObjectInputStream inputStream) throws IOException, ClassNotFoundException
   {
      inputStream.defaultReadObject();
      initialize();
   }
}
