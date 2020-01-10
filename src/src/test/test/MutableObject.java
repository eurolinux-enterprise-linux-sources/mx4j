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
 */
public class MutableObject
{
   private Object object;

   public MutableObject(Object object)
   {
      this.object = object;
   }

   public synchronized Object get()
   {
      return object;
   }

   public synchronized void set(Object object)
   {
      this.object = object;
   }
}
