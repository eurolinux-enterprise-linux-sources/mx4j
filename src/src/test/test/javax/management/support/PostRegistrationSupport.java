/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.support;

import java.util.Set;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;

/**
 * @version $Revision: 1.3 $
 */
public class PostRegistrationSupport implements PostRegistrationSupportMBean, MBeanRegistration
{
   private MBeanServer server;
   private ObjectName name;

   public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception
   {
      this.server = server;
      this.name = name;
      return name;
   }

   public void postRegister(Boolean registrationDone)
   {
      if (registrationDone.booleanValue())
      {
         Set mbeans = server.queryMBeans(name, null);
         if (mbeans.size() != 1) throw new Error();
         ObjectInstance instance = (ObjectInstance)mbeans.iterator().next();
         if (instance == null) throw new Error();
      }
   }

   public void preDeregister() throws Exception
   {
   }

   public void postDeregister()
   {
   }
}
