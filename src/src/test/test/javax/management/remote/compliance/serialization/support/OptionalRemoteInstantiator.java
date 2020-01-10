/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote.compliance.serialization.support;

import javax.management.remote.message.CloseMessage;
import javax.management.remote.message.HandshakeBeginMessage;
import javax.management.remote.message.HandshakeEndMessage;
import javax.management.remote.message.HandshakeErrorMessage;
import javax.management.remote.message.MBeanServerRequestMessage;
import javax.management.remote.message.MBeanServerResponseMessage;
import javax.management.remote.message.SASLMessage;
import javax.management.remote.message.TLSMessage;

/**
 * @version $Revision: 1.5 $
 */
public class OptionalRemoteInstantiator
{
/*
   public ConnectionClosedException createConnectionClosedException()
   {

   }

   public JMXMPConnector createJMXMPConnector()
   {
      return null;
   }
*/
   public CloseMessage createCloseMessage()
   {
      return null;
   }

   public HandshakeBeginMessage createHandshakeBeginMessage()
   {
      return null;
   }

   public HandshakeEndMessage createHandshakeEndMessage()
   {
      return null;
   }

   public HandshakeErrorMessage createHandshakeErrorMessage()
   {
      return null;
   }
/*
   public JMXMPMessage createJMXMPMessage()
   {
      return null;
   }
*/
   public MBeanServerRequestMessage createMBeanServerRequestMessage()
   {
      return null;
   }

   public MBeanServerResponseMessage createMBeanServerResponseMessage()
   {
      return null;
   }
/*
   public NotificationRequestMessage createNotificationRequestMessage()
   {

   }

   public NotificationResponseMessage createNotificationResponseMessage()
   {

   }
*/
   public SASLMessage createSASLMessage()
   {
      return null;
   }

   public TLSMessage createTLSMessage()
   {
      return null;
   }
}
