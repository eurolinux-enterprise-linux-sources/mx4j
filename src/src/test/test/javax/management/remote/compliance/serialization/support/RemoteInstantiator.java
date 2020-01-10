/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.remote.compliance.serialization.support;

import java.net.MalformedURLException;
import java.util.HashMap;
import javax.management.remote.JMXConnectionNotification;
import javax.management.remote.JMXPrincipal;
import javax.management.remote.JMXProviderException;
import javax.management.remote.JMXServerErrorException;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.NotificationResult;
import javax.management.remote.SubjectDelegationPermission;
import javax.management.remote.TargetedNotification;
import javax.management.remote.rmi.RMIConnector;

/**
 * @version $Revision: 1.6 $
 */
public class RemoteInstantiator
{
   public JMXConnectionNotification createJMXConnectionNotification()
   {
      JMXConnectionNotification jcn = new JMXConnectionNotification(JMXConnectionNotification.OPENED,
                                                                    "Source", "ConnectionID",
                                                                    0L, "Message", "UserData");
      return jcn;
   }

   public JMXPrincipal createJMXPrincipal()
   {
      JMXPrincipal jp = new JMXPrincipal(JMXPrincipal.class.getName());
      return jp;
   }

   public JMXProviderException createJMXProviderException()
   {
      JMXProviderException ex = new JMXProviderException();
      return ex;
   }

   public JMXServerErrorException createJMXServerErrorException()
   {
      JMXServerErrorException see = new JMXServerErrorException("Message", new Error());
      return see;
   }

   public JMXServiceURL createJMXServiceURL()
   {
      try
      {
         JMXServiceURL jsu = new JMXServiceURL("rmi", "localhost", 1099);
         return jsu;
      }
      catch (MalformedURLException e)
      {
         throw new RuntimeException();
      }
   }

   public NotificationResult createNotificationResult()
   {
      TargetedNotification[] notifs =
              {createTargetedNotification()};
      NotificationResult result = new NotificationResult(0l, 1l, notifs);
      return result;
   }

   public SubjectDelegationPermission createSubjectDelegationPermission()
   {
      SubjectDelegationPermission sdp = new SubjectDelegationPermission(SubjectDelegationPermission.class.getName());
      return sdp;
   }

   public TargetedNotification createTargetedNotification()
   {
      TargetedNotification tn = new TargetedNotification(createJMXConnectionNotification(), Integer.decode("1"));
      return tn;
   }

   public RMIConnector createRMIConnector()
   {
      RMIConnector rc = new RMIConnector(createJMXServiceURL(), new HashMap());
      return rc;
   }
}
