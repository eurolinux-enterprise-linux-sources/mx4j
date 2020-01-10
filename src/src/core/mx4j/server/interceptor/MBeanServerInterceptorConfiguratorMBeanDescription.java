/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.server.interceptor;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

import mx4j.MBeanDescriptionAdapter;

/**
 * Management interface description for the MBeanServerInterceptorConfigurator MBean.
 *
 * @version $Revision: 1.3 $
 */
public class MBeanServerInterceptorConfiguratorMBeanDescription extends MBeanDescriptionAdapter
{
   public String getMBeanDescription()
   {
      return "Configurator for MBeanServer to MBean interceptors";
   }

   public String getConstructorDescription(Constructor ctor)
   {
      if (ctor.toString().equals("public mx4j.server.interceptor.MBeanServerInterceptorConfigurator(javax.management.MBeanServer)"))
      {
         return "Creates a new instance of MBeanServer to MBean interceptor configurator";
      }
      return super.getConstructorDescription(ctor);
   }

   public String getConstructorParameterName(Constructor ctor, int index)
   {
      if (ctor.toString().equals("public mx4j.server.interceptor.MBeanServerInterceptorConfigurator(javax.management.MBeanServer)"))
      {
         switch (index)
         {
            case 0:
               return "server";
         }
      }
      return super.getConstructorParameterName(ctor, index);
   }

   public String getConstructorParameterDescription(Constructor ctor, int index)
   {
      if (ctor.toString().equals("public mx4j.server.interceptor.MBeanServerInterceptorConfigurator(javax.management.MBeanServer)"))
      {
         switch (index)
         {
            case 0:
               return "The MBeanServer that uses this configurator";
         }
      }
      return super.getConstructorParameterDescription(ctor, index);
   }

   public String getAttributeDescription(String attribute)
   {
      if (attribute.equals("Running"))
      {
         return "The running status of the configurator";
      }
      return super.getAttributeDescription(attribute);
   }

   public String getOperationDescription(Method operation)
   {
      String name = operation.getName();
      if (name.equals("addInterceptor"))
      {
         return "Appends an interceptor to the interceptor chain";
      }
      if (name.equals("registerInterceptor"))
      {
         return "Appends an MBean interceptor to the interceptor chain and registers it";
      }
      if (name.equals("clearInterceptors"))
      {
         return "Removes all the interceptors added via addInterceptor(MBeanServerInterceptor interceptor)";
      }
      if (name.equals("start"))
      {
         return "Starts the configurator so that the MBeanServer can accept incoming calls";
      }
      if (name.equals("stop"))
      {
         return "Stops the configurator so that the MBeanServer cannot accept incoming calls";
      }
      return super.getOperationDescription(operation);
   }

   public String getOperationParameterName(Method method, int index)
   {
      String name = method.getName();
      if (name.equals("addInterceptor"))
      {
         switch (index)
         {
            case 0:
               return "interceptor";
         }
      }
      if (name.equals("registerInterceptor"))
      {
         switch (index)
         {
            case 0:
               return "interceptor";
            case 1:
               return "name";
         }
      }
      return super.getOperationParameterName(method, index);
   }

   public String getOperationParameterDescription(Method method, int index)
   {
      String name = method.getName();
      if (name.equals("addInterceptor"))
      {
         switch (index)
         {
            case 0:
               return "The interceptor to be appended to the interceptor chain";
         }
      }
      if (name.equals("registerInterceptor"))
      {
         switch (index)
         {
            case 0:
               return "The interceptor to be appended to the interceptor chain";
            case 1:
               return "The ObjectName under which register the interceptor";
         }
      }
      return super.getOperationParameterDescription(method, index);
   }
}
