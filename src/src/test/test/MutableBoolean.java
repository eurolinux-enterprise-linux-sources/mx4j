/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test;

/**
 * @version $Revision: 1.4 $
 * @see
 */
public class MutableBoolean
{
   private boolean m_value;

   public MutableBoolean(boolean value)
   {
      set(value);
   }

   public boolean get()
   {
      return m_value;
   }

   public void set(boolean value)
   {
      m_value = value;
   }
}