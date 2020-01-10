/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote.support;

/**
 * @version $Revision: 1.3 $
 */
public interface MarshallingMBean
{
   public Unknown unknownReturnValue();

   public void unknownArgument(Unknown u);

   public Unknown getUnknownAttribute();

   public void setUnknownAttribute(Unknown u);
}
