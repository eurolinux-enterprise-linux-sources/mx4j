/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.server.interceptor;

import mx4j.MBeanDescriptionAdapter;

/**
 * Management interface description for the DefaultMBeanServerInterceptor MBean
 *
 * @version $Revision: 1.3 $
 */
public class DefaultMBeanServerInterceptorMBeanDescription extends MBeanDescriptionAdapter
{
   public String getMBeanDescription()
   {
      return "MBeanServer interceptor";
   }

   public String getAttributeDescription(String attribute)
   {
      if (attribute.equals("Enabled"))
      {
         return "The enable status of this interceptor";
      }
      if (attribute.equals("Type"))
      {
         return "The type of this interceptor";
      }
      return super.getAttributeDescription(attribute);
   }
}
