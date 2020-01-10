/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package javax.management.timer;

/**
 * Do not use, it is kept only for backward compatibility reasons.
 * This is a JMXRI class that creeped into the public JMX API by mistake.
 * MX4J does not use it.
 *
 * @version $Revision: 1.4 $
 * @deprecated
 */
public class TimerAlarmClockNotification extends javax.management.Notification
{
   private static final long serialVersionUID = 0xbcd1186b37930f5fL;

   /**
    * Do not use
    *
    * @deprecated
    */
   public TimerAlarmClockNotification(TimerAlarmClock timer)
   {
      super("", timer, 0);
   }
}
