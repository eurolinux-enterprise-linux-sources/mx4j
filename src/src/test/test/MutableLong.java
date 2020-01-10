/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test;

/**
 * @version $Revision: 1.3 $
 * @see
 */
public class MutableLong
{
   private long m_value;

   public MutableLong(long value)
   {
      m_value = value;
   }

   public long get()
   {
      return m_value;
   }

   public void set(long value)
   {
      m_value = value;
   }
}
