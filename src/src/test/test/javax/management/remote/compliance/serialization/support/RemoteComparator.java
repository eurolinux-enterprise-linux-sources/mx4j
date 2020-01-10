/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote.compliance.serialization.support;

import javax.management.Notification;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXProviderException;
import javax.management.remote.JMXServerErrorException;
import javax.management.remote.NotificationResult;
import javax.management.remote.SubjectDelegationPermission;
import javax.management.remote.TargetedNotification;
import javax.management.remote.rmi.RMIConnector;

/**
 * @version $Revision: 1.6 $
 */
public class RemoteComparator
{

   public void compareJMXConnectionNotification(Object obj1, Object obj2)
   {
      JMXConnectionNotification jcn1 = (JMXConnectionNotification)obj1;
      JMXConnectionNotification jcn2 = (JMXConnectionNotification)obj2;
      boolean valid = jcn1.getConnectionId().equals(jcn2.getConnectionId());
      compareNotifications(jcn1, jcn2);
      if (!valid) throw new RuntimeException();
   }

   public void compareJMXProviderException(Object obj1, Object obj2)
   {
      JMXProviderException jpe1 = (JMXProviderException)obj1;
      JMXProviderException jpe2 = (JMXProviderException)obj2;
      boolean valid = true;
      /*
      if (jpe1.getCause() != null)
        valid = valid && (jpe1.getCause().equals(jpe2.getCause()));
        */
      if (jpe1.getLocalizedMessage() != null)
         valid = valid && (jpe1.getLocalizedMessage().equals(jpe2.getLocalizedMessage()));
      if (jpe1.getMessage() != null)
         valid = valid && (jpe1.getMessage().equals(jpe2.getMessage()));

      valid = valid && (jpe1.toString().equals(jpe2.toString()));
      if (!valid) throw new RuntimeException();
   }

   public void compareJMXServerErrorException(Object obj1, Object obj2)
   {
      JMXServerErrorException jse1 = (JMXServerErrorException)obj1;
      JMXServerErrorException jse2 = (JMXServerErrorException)obj2;
      boolean valid = true; // jse1.getCause().getClass().equals(jse2.getCause().getClass());
      valid = valid && (jse1.getLocalizedMessage().equals(jse2.getLocalizedMessage()));
      valid = valid && (jse1.getMessage().equals(jse2.getMessage()));
      valid = valid && (jse1.toString().equals(jse2.toString()));
      if (!valid) throw new RuntimeException();
   }

   public void compareNotificationResult(Object obj1, Object obj2)
   {
      NotificationResult nr1 = (NotificationResult)obj1;
      NotificationResult nr2 = (NotificationResult)obj2;
      boolean valid = nr1.getEarliestSequenceNumber() == nr2.getEarliestSequenceNumber();
      valid = valid && (nr1.getNextSequenceNumber() == nr2.getNextSequenceNumber());
      TargetedNotification[] tns1 = nr1.getTargetedNotifications();
      TargetedNotification[] tns2 = nr2.getTargetedNotifications();

      if (tns1.length != tns2.length)
         throw new RuntimeException();

      for (int i = 0; i < tns1.length; i++)
      {
         compareTargetedNotification(tns1[i], tns2[i]);
      }

      if (!valid) throw new RuntimeException();
   }

   public void compareSubjectDelegationPermission(Object obj1, Object obj2)
   {
      SubjectDelegationPermission sdp1 = (SubjectDelegationPermission)obj1;
      SubjectDelegationPermission sdp2 = (SubjectDelegationPermission)obj2;
      boolean valid = sdp1.equals(sdp2);
      valid = valid && (sdp1.getActions().equals(sdp2.getActions()));
      valid = valid && (sdp1.getName().equals(sdp2.getName()));
      valid = valid && (sdp1.implies(sdp2));

      if (!valid) throw new RuntimeException();
   }

   public void compareTargetedNotification(Object obj1, Object obj2)
   {
      TargetedNotification tn1 = (TargetedNotification)obj1;
      TargetedNotification tn2 = (TargetedNotification)obj2;
      boolean valid = tn1.getListenerID().equals(tn2.getListenerID());
      compareNotifications(tn1.getNotification(), tn2.getNotification());

      if (!valid) throw new RuntimeException();
   }

   private void compareNotifications(Notification not1, Notification not2)
   {
      boolean valid = (not1.getMessage().equals(not2.getMessage()));
      valid = valid && (not1.getSequenceNumber() == not2.getSequenceNumber());
      valid = valid && (not1.getSource().equals(not2.getSource()));
      valid = valid && (not1.getType().equals(not2.getType()));
      valid = valid && (not1.getUserData().equals(not2.getUserData()));
      if (!valid) throw new RuntimeException();
   }

   public void compareRMIConnector(Object obj1, Object obj2)
   {
      RMIConnector rc1 = (RMIConnector)obj1;
      RMIConnector rc2 = (RMIConnector)obj2;
      boolean valid = true; // rc1.getConnectionId().equals(rc2.getConnectionId());

      if (!valid) throw new RuntimeException();
   }
}
