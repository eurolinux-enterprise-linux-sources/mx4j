/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote.rmi;


/**
 * @version $Revision: 1.6 $
 */
public interface RMITestCase
{
   public abstract void startNaming() throws Exception;

   public abstract void stopNaming() throws Exception;

   public abstract int getNamingPort();
}
