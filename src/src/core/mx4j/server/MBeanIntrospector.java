/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.server;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.WeakHashMap;
import javax.management.DynamicMBean;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.NotificationBroadcaster;
import javax.management.loading.MLet;

import mx4j.MBeanDescription;
import mx4j.MBeanDescriptionAdapter;
import mx4j.MX4JSystemKeys;
import mx4j.log.Log;
import mx4j.log.Logger;
import mx4j.util.Utils;

/**
 * Introspector for MBeans. <p>
 * Main purposes of this class are:
 * <ul>
 * <li> Given an mbean, gather all information regarding it into a {@link MBeanMetaData} instance, see {@link #introspect}
 * <li> Given an introspected MBeanMetaData, decide if the MBean is compliant or not.
 * <li> Act as a factory for {@link MBeanInvoker}s
 * </ul>
 * <p/>
 * The following system properties are used to control this class' behavior:
 * <ul>
 * <li> mx4j.strict.mbean.interface, if set to 'no' then are treated as standard MBeans also classes that implement
 * management interfaces beloging to different packages or that are inner classes; otherwise are treated as MBeans
 * only classes that implement interfaces whose name if the fully qualified name of the MBean class + "MBean"
 * <li> mx4j.mbean.invoker, if set to the qualified name of an implementation of the {@link MBeanInvoker} interface,
 * then an instance of the class will be used to invoke methods on standard MBeans. By default the generated-on-the-fly
 * MBeanInvoker is used; to revert to the version that uses reflection, for example,
 * use mx4j.mbean.invoker = {@link CachingReflectionMBeanInvoker mx4j.server.CachingReflectionMBeanInvoker}
 * </ul>
 *
 * @version $Revision: 1.34 $
 */
public class MBeanIntrospector
{
   private static final MBeanDescriptionAdapter DEFAULT_DESCRIPTION = new MBeanDescriptionAdapter();
   private static final MBeanConstructorInfo[] EMPTY_CONSTRUCTORS = new MBeanConstructorInfo[0];
   private static final MBeanParameterInfo[] EMPTY_PARAMETERS = new MBeanParameterInfo[0];
   private static final MBeanAttributeInfo[] EMPTY_ATTRIBUTES = new MBeanAttributeInfo[0];
   private static final MBeanNotificationInfo[] EMPTY_NOTIFICATIONS = new MBeanNotificationInfo[0];
   private static final MBeanOperationInfo[] EMPTY_OPERATIONS = new MBeanOperationInfo[0];

   private boolean extendedMBeanInterfaces = false;
   private boolean bcelAvailable = false;
   private String mbeanInvokerClass = null;

   private final WeakHashMap mbeanInfoCache = new WeakHashMap();
   private final WeakHashMap mbeanInvokerCache = new WeakHashMap();

   public MBeanIntrospector()
   {
      String strict = (String)AccessController.doPrivileged(new PrivilegedAction()
      {
         public Object run()
         {
            return System.getProperty(MX4JSystemKeys.MX4J_STRICT_MBEAN_INTERFACE);
         }
      });
      if (strict != null && !Boolean.valueOf(strict).booleanValue())
      {
         extendedMBeanInterfaces = true;
      }

      // Try to see if BCEL classes are present
      try
      {
         ClassLoader loader = getClass().getClassLoader();
         if (loader == null) loader = Thread.currentThread().getContextClassLoader();
         loader.loadClass("org.apache.bcelAvailable.generic.Type");
         bcelAvailable = true;
      }
      catch (Throwable ignored)
      {
      }

      // See if someone specified which MBean invoker to use
      mbeanInvokerClass = (String)AccessController.doPrivileged(new PrivilegedAction()
      {
         public Object run()
         {
            return System.getProperty(MX4JSystemKeys.MX4J_MBEAN_INVOKER);
         }
      });
   }

   /**
    * Introspect the given mbean, storing the results in the given metadata.
    * It expects that the mbean field and the classloader field are not null
    *
    * @see #isMBeanCompliant
    */
   public void introspect(MBeanMetaData metadata)
   {
      introspectType(metadata);
      introspectMBeanInfo(metadata);
   }

   /**
    * Returns whether the given already introspected metadata is compliant.
    * Must be called after {@link #introspect}
    */
   public boolean isMBeanCompliant(MBeanMetaData metadata)
   {
      return isMBeanClassCompliant(metadata) && isMBeanTypeCompliant(metadata) && isMBeanInfoCompliant(metadata);
   }

   /**
    * Used by the test cases, invoked via reflection, keep it private.
    * Introspect the mbean and returns if it's compliant
    */
   private boolean testCompliance(MBeanMetaData metadata)
   {
      introspect(metadata);
      return isMBeanCompliant(metadata);
   }

   private boolean isMBeanClassCompliant(MBeanMetaData metadata)
   {
      // No requirements on the implementation (can be abstract, non public and no accessible constructors)
      // but the management interface must be public
      Logger logger = getLogger();
      if (metadata.getMBeanInterface() != null)
      {
         boolean isPublic = Modifier.isPublic(metadata.getMBeanInterface().getModifiers());
         if (!isPublic && logger.isEnabledFor(Logger.DEBUG)) logger.debug("MBean interface is not public");
         return isPublic;
      }
      return true;
   }

   private boolean isMBeanTypeCompliant(MBeanMetaData metadata)
   {
      Logger logger = getLogger();

      if (metadata.isMBeanStandard() && metadata.isMBeanDynamic())
      {
         if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("MBean is both standard and dynamic");
         return false;
      }
      if (!metadata.isMBeanStandard() && !metadata.isMBeanDynamic())
      {
         if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("MBean is not standard nor dynamic");
         return false;
      }

      return true;
   }

   private boolean isMBeanInfoCompliant(MBeanMetaData metadata)
   {
      Logger logger = getLogger();

      MBeanInfo info = metadata.getMBeanInfo();
      if (info == null)
      {
         if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("MBeanInfo is null");
         return false;
      }
      if (info.getClassName() == null)
      {
         if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("MBeanInfo.getClassName() is null");
         return false;
      }
      return true;
   }

   private void introspectType(MBeanMetaData metadata)
   {
      // Some information is already provided (StandardMBean)
      if (metadata.isMBeanStandard())
      {
         introspectStandardMBean(metadata);
         return;
      }

      if (metadata.getMBean() instanceof DynamicMBean)
      {
         metadata.setMBeanDynamic(true);
         return;
      }
      else
      {
         metadata.setMBeanDynamic(false);
         // Continue and see if it's a plain standard MBean
      }

      // We have a plain standard MBean, introspect it
      introspectStandardMBean(metadata);
   }

   private void introspectStandardMBean(MBeanMetaData metadata)
   {
      Class management = metadata.getMBeanInterface();
      if (management != null)
      {
         // Be sure the MBean implements the management interface
         if (management.isInstance(metadata.getMBean()))
         {
            metadata.setMBeanInvoker(createInvoker(metadata));
            return;
         }
         else
         {
            // Not compliant, reset the values
            metadata.setMBeanStandard(false);
            metadata.setMBeanInterface(null);
            metadata.setMBeanInvoker(null);
            return;
         }
      }
      else
      {
         Class cls = metadata.getMBean().getClass();
         for (Class c = cls; c != null; c = c.getSuperclass())
         {
            Class[] intfs = c.getInterfaces();
            for (int i = 0; i < intfs.length; ++i)
            {
               Class intf = intfs[i];

               if (implementsMBean(c.getName(), intf.getName()))
               {
                  // OK, found the MBean interface for this class
                  metadata.setMBeanStandard(true);
                  metadata.setMBeanInterface(intf);
                  metadata.setMBeanInvoker(createInvoker(metadata));
                  return;
               }
            }
         }

         // Management interface not found, it's not compliant, reset the values
         metadata.setMBeanStandard(false);
         metadata.setMBeanInterface(null);
         metadata.setMBeanInvoker(null);
      }
   }

   private void introspectMBeanInfo(MBeanMetaData metadata)
   {
      if (metadata.isMBeanDynamic())
      {
         metadata.setMBeanInfo(getDynamicMBeanInfo(metadata));
      }
      else if (metadata.isMBeanStandard())
      {
         metadata.setMBeanInfo(createStandardMBeanInfo(metadata));
      }
      else
      {
         // Not a valid MBean, reset the MBeanInfo: this will cause an exception later
         metadata.setMBeanInfo(null);
      }
   }

   private MBeanInfo getDynamicMBeanInfo(MBeanMetaData metadata)
   {
      Logger logger = getLogger();

      MBeanInfo info = null;

      try
      {
         info = ((DynamicMBean)metadata.getMBean()).getMBeanInfo();
      }
      catch (Exception x)
      {
         if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("getMBeanInfo threw: " + x.toString());
      }

      if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Dynamic MBeanInfo is: " + info);

      if (info == null)
      {
         if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("MBeanInfo cannot be null");
         return null;
      }

      return info;
   }

   private MBeanInfo createStandardMBeanInfo(MBeanMetaData metadata)
   {
      synchronized (mbeanInfoCache)
      {
         MBeanInfo info = (MBeanInfo)mbeanInfoCache.get(metadata.getMBean().getClass());
         if (info != null) return info;
      }

      // This is a non-standard extension: description for standard MBeans
      MBeanDescription description = createMBeanDescription(metadata);

      MBeanConstructorInfo[] ctors = createMBeanConstructorInfo(metadata, description);
      if (ctors == null) return null;
      MBeanAttributeInfo[] attrs = createMBeanAttributeInfo(metadata, description);
      if (attrs == null) return null;
      MBeanOperationInfo[] opers = createMBeanOperationInfo(metadata, description);
      if (opers == null) return null;
      MBeanNotificationInfo[] notifs = createMBeanNotificationInfo(metadata);
      if (notifs == null) return null;

      MBeanInfo info = new MBeanInfo(metadata.getMBean().getClass().getName(), description.getMBeanDescription(), attrs, ctors, opers, notifs);
      synchronized (mbeanInfoCache)
      {
         // Overwrite if already present, we've been unlucky
         mbeanInfoCache.put(metadata.getMBean().getClass(), info);
      }
      return info;
   }

   private MBeanDescription createMBeanDescription(MBeanMetaData metadata)
   {
      // TODO: PERFORMANCE: looking up the MBeanDescription is a real performance hotspot for MBean registration
      // TODO: PERFORMANCE: simply remarking the lookup yields to a 3.5x speedup (from 14 s to 4 s for 20k MBeans)
      // TODO: PERFORMANCE: consider using a system property to disable lookup, and/or caching the lookup.

      // This is a non-standard extension

      Logger logger = getLogger();
      if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Looking for standard MBean description...");

      // Use full qualified name only
      String descrClassName = metadata.getMBeanInterface().getName() + "Description";
      // Try to load the class
      try
      {
         Class descrClass = null;
         ClassLoader loader = metadata.getClassLoader();
         if (loader == null) loader = Thread.currentThread().getContextClassLoader();
         // Optimize lookup of the description class in case of MLets: we lookup the description class
         // only in the classloader of the mbean, not in the whole CLR (since MLets delegates to the CLR)
         if (loader.getClass() == MLet.class)
            descrClass = ((MLet)loader).loadClass(descrClassName, null);
         else
            descrClass = loader.loadClass(descrClassName);

         Object descrInstance = descrClass.newInstance();
         if (descrInstance instanceof MBeanDescription)
         {
            MBeanDescription description = (MBeanDescription)descrInstance;
            if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Found provided standard MBean description: " + description);
            return description;
         }
      }
      catch (ClassNotFoundException ignored)
      {
      }
      catch (InstantiationException ignored)
      {
      }
      catch (IllegalAccessException ignored)
      {
      }

      MBeanDescription description = DEFAULT_DESCRIPTION;
      if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Cannot find standard MBean description, using default: " + description);
      return description;
   }

   private MBeanOperationInfo[] createMBeanOperationInfo(MBeanMetaData metadata, MBeanDescription description)
   {
      ArrayList operations = new ArrayList();

      Method[] methods = metadata.getMBeanInterface().getMethods();
      for (int j = 0; j < methods.length; ++j)
      {
         Method method = methods[j];
         if (!Utils.isAttributeGetter(method) && !Utils.isAttributeSetter(method))
         {
            String descr = description == null ? null : description.getOperationDescription(method);
            Class[] params = method.getParameterTypes();
            int paramsNumber = params.length;
            MBeanParameterInfo[] paramsInfo = paramsNumber == 0 ? EMPTY_PARAMETERS : new MBeanParameterInfo[paramsNumber];
            for (int k = 0; k < paramsNumber; ++k)
            {
               Class param = params[k];
               String paramName = description == null ? null : description.getOperationParameterName(method, k);
               String paramDescr = description == null ? null : description.getOperationParameterDescription(method, k);
               paramsInfo[k] = new MBeanParameterInfo(paramName, param.getName(), paramDescr);
            }
            MBeanOperationInfo info = new MBeanOperationInfo(method.getName(), descr, paramsInfo, method.getReturnType().getName(), MBeanOperationInfo.UNKNOWN);
            operations.add(info);
         }
      }

      int opersNumber = operations.size();
      return opersNumber == 0 ? EMPTY_OPERATIONS : (MBeanOperationInfo[])operations.toArray(new MBeanOperationInfo[opersNumber]);
   }

   private MBeanAttributeInfo[] createMBeanAttributeInfo(MBeanMetaData metadata, MBeanDescription description)
   {
      Logger logger = getLogger();

      HashMap attributes = new HashMap();
      HashMap getterNames = new HashMap();

      Method[] methods = metadata.getMBeanInterface().getMethods();
      for (int j = 0; j < methods.length; ++j)
      {
         Method method = methods[j];
         if (Utils.isAttributeGetter(method))
         {
            String name = method.getName();
            boolean isIs = name.startsWith("is");

            String attribute = null;
            if (isIs)
               attribute = name.substring(2);
            else
               attribute = name.substring(3);

            String descr = description == null ? null : description.getAttributeDescription(attribute);

            MBeanAttributeInfo info = (MBeanAttributeInfo)attributes.get(attribute);

            if (info != null)
            {
               // JMX spec does not allow overloading attributes.
               // If an attribute with the same name already exists the MBean is not compliant
               if (!info.getType().equals(method.getReturnType().getName()))
               {
                  if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("MBean is not compliant: has overloaded attribute " + attribute);
                  return null;
               }
               else
               {
                  // They return the same value,
                  if (getterNames.get(name) != null)
                  {
                     // This is the case of an attribute being present in multiple interfaces
                     // Ignore all but the first, since they resolve to the same method anyways
                     continue;
                  }

                  // there is a chance that one is a get-getter and one is a is-getter
                  // for a boolean attribute. In this case, the MBean is not compliant.
                  if (info.isReadable())
                  {
                     if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("MBean is not compliant: has overloaded attribute " + attribute);
                     return null;
                  }

                  // MBeanAttributeInfo is already present due to a setter method, just update its readability
                  info = new MBeanAttributeInfo(attribute, info.getType(), info.getDescription(), true, info.isWritable(), isIs);
               }
            }
            else
            {
               info = new MBeanAttributeInfo(attribute, method.getReturnType().getName(), descr, true, false, isIs);
            }

            // Replace if exists
            attributes.put(attribute, info);
            getterNames.put(name, method);
         }
         else if (Utils.isAttributeSetter(method))
         {
            String name = method.getName();
            String attribute = name.substring(3);

            String descr = description == null ? null : description.getAttributeDescription(attribute);

            MBeanAttributeInfo info = (MBeanAttributeInfo)attributes.get(attribute);

            if (info != null)
            {
               // JMX spec does not allow overloading attributes.
               // If an attribute with the same name already exists the MBean is not compliant
               if (!info.getType().equals(method.getParameterTypes()[0].getName()))
               {
                  if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("MBean is not compliant: has overloaded attribute " + attribute);
                  return null;
               }
               else
               {
                  // MBeanAttributeInfo is already present due to a getter method, just update its writability
                  info = new MBeanAttributeInfo(info.getName(), info.getType(), info.getDescription(), info.isReadable(), true, info.isIs());
               }
            }
            else
            {
               info = new MBeanAttributeInfo(attribute, method.getParameterTypes()[0].getName(), descr, false, true, false);
            }

            // Replace if exists
            attributes.put(attribute, info);
         }
      }

      int size = attributes.size();
      return size == 0 ? EMPTY_ATTRIBUTES : (MBeanAttributeInfo[])attributes.values().toArray(new MBeanAttributeInfo[size]);
   }

   private MBeanNotificationInfo[] createMBeanNotificationInfo(MBeanMetaData metadata)
   {
      MBeanNotificationInfo[] notifs = null;
      Object mbean = metadata.getMBean();
      if (mbean instanceof NotificationBroadcaster)
      {
         notifs = ((NotificationBroadcaster)mbean).getNotificationInfo();
      }
      if (notifs == null || notifs.length == 0) notifs = EMPTY_NOTIFICATIONS;
      return notifs;
   }

   private MBeanConstructorInfo[] createMBeanConstructorInfo(MBeanMetaData metadata, MBeanDescription descrs)
   {
      Class mbeanClass = metadata.getMBean().getClass();

      Constructor[] ctors = mbeanClass.getConstructors();

      int ctorsNumber = ctors.length;
      MBeanConstructorInfo[] constructors = ctorsNumber == 0 ? EMPTY_CONSTRUCTORS : new MBeanConstructorInfo[ctorsNumber];
      for (int i = 0; i < ctorsNumber; ++i)
      {
         Constructor constructor = ctors[i];
         String descr = descrs == null ? null : descrs.getConstructorDescription(constructor);
         Class[] params = constructor.getParameterTypes();
         int paramsNumber = params.length;
         MBeanParameterInfo[] paramsInfo = paramsNumber == 0 ? EMPTY_PARAMETERS : new MBeanParameterInfo[paramsNumber];
         for (int j = 0; j < paramsNumber; ++j)
         {
            Class param = params[j];
            String paramName = descrs == null ? null : descrs.getConstructorParameterName(constructor, j);
            String paramDescr = descrs == null ? null : descrs.getConstructorParameterDescription(constructor, j);
            paramsInfo[j] = new MBeanParameterInfo(paramName, param.getName(), paramDescr);
         }

         String ctorName = constructor.getName();
         MBeanConstructorInfo info = new MBeanConstructorInfo(ctorName, descr, paramsInfo);
         constructors[i] = info;
      }
      return constructors;
   }

   private boolean implementsMBean(String clsName, String intfName)
   {
      if (intfName.equals(clsName + "MBean")) return true;

      if (extendedMBeanInterfaces)
      {
         // Check also that the may be in different packages and/or inner classes

         // Trim packages
         int clsDot = clsName.lastIndexOf('.');
         if (clsDot > 0) clsName = clsName.substring(clsDot + 1);
         int intfDot = intfName.lastIndexOf('.');
         if (intfDot > 0) intfName = intfName.substring(intfDot + 1);
         // Try again
         if (intfName.equals(clsName + "MBean")) return true;

         // Trim inner classes
         int clsDollar = clsName.lastIndexOf('$');
         if (clsDollar > 0) clsName = clsName.substring(clsDollar + 1);
         int intfDollar = intfName.lastIndexOf('$');
         if (intfDollar > 0) intfName = intfName.substring(intfDollar + 1);
         // Try again
         if (intfName.equals(clsName + "MBean")) return true;
      }

      // Give up
      return false;
   }

   private MBeanInvoker createInvoker(MBeanMetaData metadata)
   {
      MBeanInvoker invoker = null;

      synchronized (mbeanInvokerCache)
      {
         invoker = (MBeanInvoker)mbeanInvokerCache.get(metadata.getMBeanInterface());
         if (invoker != null) return invoker;
      }

      Logger logger = getLogger();

      if (mbeanInvokerClass != null)
      {
         if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Custom MBeanInvoker class is: " + mbeanInvokerClass);
         try
         {
            invoker = (MBeanInvoker)Thread.currentThread().getContextClassLoader().loadClass(mbeanInvokerClass).newInstance();
            if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Using custom MBeanInvoker: " + invoker);
         }
         catch (Exception x)
         {
            if (logger.isEnabledFor(Logger.DEBUG)) logger.debug("Cannot instantiate custom MBeanInvoker, using default", x);
         }
      }

      if (invoker == null)
      {
         if (bcelAvailable)
         {
            invoker = BCELMBeanInvoker.create(metadata);
            if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Using default BCEL MBeanInvoker for MBean " + metadata.getObjectName() + ", " + invoker);
         }
         else
         {
            invoker = new CachingReflectionMBeanInvoker();
            if (logger.isEnabledFor(Logger.TRACE)) logger.trace("Using default Reflection MBeanInvoker for MBean " + metadata.getObjectName() + ", " + invoker);
         }
      }

      synchronized (mbeanInvokerCache)
      {
         // Overwrite if already present: we've been unlucky
         mbeanInvokerCache.put(metadata.getMBeanInterface(), invoker);
      }
      return invoker;
   }

   private Logger getLogger()
   {
      return Log.getLogger(getClass().getName());
   }
}
