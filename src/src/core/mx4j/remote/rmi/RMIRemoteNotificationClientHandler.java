/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.remote.rmi;

import java.io.IOException;
import java.util.Map;
import javax.management.remote.NotificationResult;
import javax.management.remote.rmi.RMIConnection;

import mx4j.remote.AbstractRemoteNotificationClientHandler;
import mx4j.remote.ConnectionNotificationEmitter;
import mx4j.remote.HeartBeat;

/**
 * RMI-specific RemoteNotificationClientHandler.
 *
 * @version $Revision: 1.3 $
 */
public class RMIRemoteNotificationClientHandler extends AbstractRemoteNotificationClientHandler
{
   private final RMIConnection connection;

   public RMIRemoteNotificationClientHandler(RMIConnection connection, ConnectionNotificationEmitter emitter, HeartBeat heartbeat, Map environment)
   {
      super(emitter, heartbeat, environment);
      this.connection = connection;
   }

   protected NotificationResult fetchNotifications(long sequence, int maxNumber, long timeout) throws IOException
   {
      return connection.fetchNotifications(sequence, maxNumber, timeout);
   }
}
