/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.support;

import javax.management.NotificationBroadcasterSupport;

/**
 * @version $Revision: 1.3 $
 */
public class NotificationSupport
{
   public interface EmitterMBean
   {
   }

   public static class Emitter extends NotificationBroadcasterSupport implements EmitterMBean
   {
   }
}
