/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote.support;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;

/**
 * @version $Revision: 1.3 $
 */
public class Unknown implements Serializable
{
   private transient ClassLoader loader = getClass().getClassLoader();

   private void readObject(ObjectInputStream ois) throws IOException, ClassNotFoundException
   {
      ois.defaultReadObject();
      loader = getClass().getClassLoader();
   }
}
