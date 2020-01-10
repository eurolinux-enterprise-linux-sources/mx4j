/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package javax.management.monitor;

import javax.management.JMRuntimeException;

/**
 * @version $Revision: 1.5 $
 */
public class MonitorSettingException extends JMRuntimeException
{
   private static final long serialVersionUID = -8807913418190202007L;

   public MonitorSettingException()
   {
   }

   public MonitorSettingException(String message)
   {
      super(message);
   }
}
