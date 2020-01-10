/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package javax.management;

import mx4j.AbstractDynamicMBean;
import mx4j.server.MBeanIntrospector;
import mx4j.server.MBeanMetaData;

/**
 * StandardMBean eases the development of MBeans that have a management interface described
 * by a java interface, like plain standard MBeans have; differently from a plain standard
 * MBean, StandardMBean is not tied to the JMX lexical patterns and allows more control on the
 * customization of the MBeanInfo that describes the MBean (for example it allows to describe
 * metadata descriptions). <br>
 * Usage of StandardMBean with a management interface that does not follow the JMX lexical patterns:
 * <pre>
 * public interface Management
 * {
 *    ...
 * }
 * <p/>
 * public class Service implements Management
 * {
 *    ...
 * }
 * <p/>
 * Service service = new Service();
 * StandardMBean mbean = new StandardMBean(service, Management.class);
 * MBeanServer server = ...;
 * ObjectName name = ...;
 * server.registerMBean(mbean, name);
 * </pre>
 * Usage of a subclass of StandardMBean:
 * <pre>
 * public interface Management
 * {
 *    ...
 * }
 * <p/>
 * public class Service extends StandardMBean implements Management
 * {
 *    public Service()
 *    {
 *       super(Manegement.class);
 *    }
 *    ...
 * }
 * <p/>
 * Service mbean = new Service();
 * MBeanServer server = ...;
 * ObjectName name = ...;
 * server.registerMBean(mbean, name);
 * </pre>
 * Usage of StandardMBean with a management interface that follows the JMX lexical patterns
 * (this is similar to plain standard MBeans):
 * <pre>
 * public interface ServiceMBean
 * {
 *    ...
 * }
 * <p/>
 * public class Service implements ServiceMBean
 * {
 *    ...
 * }
 * <p/>
 * Service service = new Service();
 * StandardMBean mbean = new StandardMBean(service, null);
 * MBeanServer server = ...;
 * ObjectName name = ...;
 * server.registerMBean(mbean, name);
 * </pre>
 *
 * @version $Revision: 1.5 $
 * @since JMX 1.2
 */
public class StandardMBean implements DynamicMBean
{
   private MBeanMetaData metadata;
   private MBeanInfo info;
   private DynamicMBean support;

   /**
    * Creates a new StandardMBean.
    *
    * @param implementation The MBean implementation for this StandardMBean
    * @param management     The management interface; if null, the JMX lexical patterns will be used
    * @throws IllegalArgumentException   If <code>implementation</code> is null
    * @throws NotCompliantMBeanException If <code>implementation</code> does not implement <code>managementInterface</code>,
    *                                    or if the management interface is not a valid JMX Management Interface
    * @see #setImplementation
    */
   public StandardMBean(Object implementation, Class management) throws NotCompliantMBeanException
   {
      this(implementation, management, false);
   }

   /**
    * Creates a new StandardMBean using 'this' as implementation.
    *
    * @see #StandardMBean(Object,Class)
    */
   protected StandardMBean(Class managementInterface) throws NotCompliantMBeanException
   {
      this(null, managementInterface, true);
   }

   private StandardMBean(Object implementation, Class management, boolean useThis) throws NotCompliantMBeanException
   {
      if (useThis) implementation = this;
      if (implementation == null) throw new IllegalArgumentException("Implementation cannot be null");
      if (management != null && !management.isInterface()) throw new NotCompliantMBeanException("Class " + management + " is not an interface");

      metadata = introspectMBean(implementation, management);
      if (metadata == null) throw new NotCompliantMBeanException("StandardMBean is not compliant");

      support = new StandardMBeanSupport();
   }

   /**
    * Sets the MBean implementation for this StandardMBean.
    *
    * @param implementation The MBean implementation for this StandardMBean
    * @throws IllegalArgumentException   If <code>implementation</code> is null
    * @throws NotCompliantMBeanException If <code>implementation</code> does not implement the management interface
    *                                    returned by {@link #getMBeanInterface}
    * @see #StandardMBean(Object,Class)
    */
   public void setImplementation(Object implementation) throws NotCompliantMBeanException
   {
      if (implementation == null) throw new IllegalArgumentException("Implementation cannot be null");
      Class management = getMBeanInterface();
      if (!management.isInstance(implementation)) throw new NotCompliantMBeanException("Implementation " + implementation + " does not implement interface " + management);
      metadata.setMBean(implementation);
   }

   /**
    * Returns the implementation supplied to this StandardMBean, or this object if no implementation was supplied
    *
    * @see #StandardMBean(Object,Class)
    * @see #setImplementation
    */
   public Object getImplementation()
   {
      return metadata.getMBean();
   }

   /**
    * Returns the management interface for this MBean. This interface is set at creation time and cannot be changed
    * even if the implementation object can be changed (but it must implement the same interface).
    *
    * @see #StandardMBean(Object,Class)}
    * @see #setImplementation
    */
   public final Class getMBeanInterface()
   {
      return metadata.getMBeanInterface();
   }

   /**
    * Returns the class of the MBean implementation for this StandardMBean, or 'this' (sub)class if no
    * implementation was supplied.
    *
    * @see #StandardMBean(Object,Class)}
    */
   public Class getImplementationClass()
   {
      return metadata.getMBean().getClass();
   }

   public Object getAttribute(String attribute) throws AttributeNotFoundException, MBeanException, ReflectionException
   {
      return support.getAttribute(attribute);
   }

   public void setAttribute(Attribute attribute) throws AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
   {
      support.setAttribute(attribute);
   }

   public AttributeList getAttributes(String[] attributes)
   {
      return support.getAttributes(attributes);
   }

   public AttributeList setAttributes(AttributeList attributes)
   {
      return support.setAttributes(attributes);
   }

   public Object invoke(String method, Object[] arguments, String[] params) throws MBeanException, ReflectionException
   {
      return support.invoke(method, arguments, params);
   }

   /**
    * See {@link DynamicMBean#getMBeanInfo}. <br>
    * By default, the metadata is cached the first time is created; if the caching has been disabled,
    * the metadata is created from scratch each time.
    *
    * @see #getCachedMBeanInfo
    * @see #cacheMBeanInfo
    */
   public MBeanInfo getMBeanInfo()
   {
      MBeanInfo info = getCachedMBeanInfo();
      if (info == null)
      {
         info = setupMBeanInfo(metadata.getMBeanInfo());
         cacheMBeanInfo(info);
      }
      return info;
   }

   /**
    * Returns the class name of this MBean. <br>
    * By default returns {@link MBeanInfo#getClassName info.getClassName()}
    */
   protected String getClassName(MBeanInfo info)
   {
      return info == null ? null : info.getClassName();
   }

   /**
    * Returns the description for this MBean. <br>
    * By default returns {@link MBeanInfo#getDescription info.getDescription()}
    */
   protected String getDescription(MBeanInfo info)
   {
      return info == null ? null : info.getDescription();
   }

   /**
    * Returns the description for the given feature. <br>
    * By default returns {@link MBeanFeatureInfo#getDescription info.getDescription()}
    *
    * @see #getDescription(MBeanAttributeInfo)
    * @see #getDescription(MBeanConstructorInfo)
    * @see #getDescription(MBeanOperationInfo)
    */
   protected String getDescription(MBeanFeatureInfo info)
   {
      return info == null ? null : info.getDescription();
   }

   /**
    * Returns the description for the given attribute. <br>
    * By default calls {@link #getDescription(MBeanFeatureInfo)}
    */
   protected String getDescription(MBeanAttributeInfo info)
   {
      return getDescription((MBeanFeatureInfo)info);
   }

   /**
    * Returns the description for the given constructor. <br>
    * By default calls {@link #getDescription(MBeanFeatureInfo)}
    */
   protected String getDescription(MBeanConstructorInfo info)
   {
      return getDescription((MBeanFeatureInfo)info);
   }

   /**
    * Returns the description for the given operation. <br>
    * By default calls {@link #getDescription(MBeanFeatureInfo)}
    */
   protected String getDescription(MBeanOperationInfo info)
   {
      return getDescription((MBeanFeatureInfo)info);
   }

   /**
    * Returns the description of the (sequence + 1)th parameter (that is: if sequence is 0 returns the description of the first
    * parameter, if sequence is 1 returns the description of the second parameter, and so on) for the given constructor. <br>
    * By default returns {@link MBeanParameterInfo#getDescription param.getDescription()}.
    */
   protected String getDescription(MBeanConstructorInfo constructor, MBeanParameterInfo param, int sequence)
   {
      return param == null ? null : param.getDescription();
   }

   /**
    * Returns the description of the (sequence + 1)th parameter (that is: if sequence is 0 returns the description of the first
    * parameter, if sequence is 1 returns the description of the second parameter, and so on) for the given operation. <br>
    * By default returns {@link MBeanParameterInfo#getDescription param.getDescription()}.
    */
   protected String getDescription(MBeanOperationInfo operation, MBeanParameterInfo param, int sequence)
   {
      return param == null ? null : param.getDescription();
   }

   /**
    * Returns the name of the (sequence + 1)th parameter (that is: if sequence is 0 returns the name of the first
    * parameter, if sequence is 1 returns the name of the second parameter, and so on) for the given constructor. <br>
    * By default returns {@link MBeanParameterInfo#getName param.getName()}.
    */
   protected String getParameterName(MBeanConstructorInfo constructor, MBeanParameterInfo param, int sequence)
   {
      return param == null ? null : param.getName();
   }

   /**
    * Returns the name of the (sequence + 1)th parameter (that is: if sequence is 0 returns the name of the first
    * parameter, if sequence is 1 returns the name of the second parameter, and so on) for the given operation. <br>
    * By default returns {@link MBeanParameterInfo#getName param.getName()}.
    */
   protected String getParameterName(MBeanOperationInfo operation, MBeanParameterInfo param, int sequence)
   {
      return param == null ? null : param.getName();
   }

   /**
    * Returns the impact flag for the given MBeanOperationInfo. <br>
    * By default returns {@link MBeanOperationInfo#getImpact info.getImpact()}
    */
   protected int getImpact(MBeanOperationInfo info)
   {
      return info == null ? MBeanOperationInfo.UNKNOWN : info.getImpact();
   }

   /**
    * Returns, by default, the given <code>constructors</code> if <code>implementation</code>
    * is 'this' object or null, otherwise returns null. <br>
    * Since the MBean that is registered in an MBeanServer is always an instance of StandardMBean,
    * there is no meaning in providing MBeanConstructorInfo if the implementation passed to
    * {@link #StandardMBean(Object,Class)} is not 'this' object.
    */
   protected MBeanConstructorInfo[] getConstructors(MBeanConstructorInfo[] constructors, Object implementation)
   {
      if (implementation == this || implementation == null) return constructors;
      return null;
   }

   /**
    * Returns the cached MBeanInfo, or null if the MBeanInfo is not cached.
    *
    * @see #cacheMBeanInfo
    * @see #getMBeanInfo
    */
   protected MBeanInfo getCachedMBeanInfo()
   {
      return info;
   }

   /**
    * Caches the given MBeanInfo after it has been created, by introspection, with the information
    * provided to constructors. <br>
    * Override to disable caching, or to install different caching policies.
    *
    * @param info The MBeanInfo to cache; if it is null, the cache is cleared.
    * @see #getCachedMBeanInfo
    * @see #getMBeanInfo
    */
   protected void cacheMBeanInfo(MBeanInfo info)
   {
      this.info = info;
   }

   /**
    * This method calls the callbacks provided by this class that allow the user to customize the MBeanInfo
    *
    * @param info The MBeanInfo as it was introspected
    */
   private MBeanInfo setupMBeanInfo(MBeanInfo info)
   {
      String clsName = getClassName(info);
      String description = getDescription(info);
      MBeanConstructorInfo[] ctors = setupConstructors(info.getConstructors());
      MBeanAttributeInfo[] attrs = setupAttributes(info.getAttributes());
      MBeanOperationInfo[] opers = setupOperations(info.getOperations());
      MBeanNotificationInfo[] notifs = setupNotifications(info.getNotifications());
      return new MBeanInfo(clsName, description, attrs, ctors, opers, notifs);
   }

   private MBeanConstructorInfo[] setupConstructors(MBeanConstructorInfo[] originalCtors)
   {
      MBeanConstructorInfo[] ctors = getConstructors(originalCtors, getImplementation());
      if (ctors == null) return null;

      MBeanConstructorInfo[] newCtors = new MBeanConstructorInfo[ctors.length];
      for (int i = 0; i < ctors.length; ++i)
      {
         MBeanConstructorInfo ctor = ctors[i];
         if (ctor == null) continue;

         MBeanParameterInfo[] newParams = null;
         MBeanParameterInfo[] params = ctor.getSignature();
         if (params != null)
         {
            newParams = new MBeanParameterInfo[params.length];
            for (int j = 0; j < params.length; ++j)
            {
               MBeanParameterInfo param = params[j];
               if (param == null) continue;

               String paramName = getParameterName(ctor, param, j);
               String paramDescr = getDescription(ctor, param, j);
               newParams[j] = new MBeanParameterInfo(paramName, param.getType(), paramDescr);
            }
         }

         String ctorDescr = getDescription(ctor);
         newCtors[i] = new MBeanConstructorInfo(ctor.getName(), ctorDescr, newParams);
      }

      return newCtors;
   }

   private MBeanAttributeInfo[] setupAttributes(MBeanAttributeInfo[] attrs)
   {
      if (attrs == null) return null;

      MBeanAttributeInfo[] newAttrs = new MBeanAttributeInfo[attrs.length];
      for (int i = 0; i < attrs.length; ++i)
      {
         MBeanAttributeInfo attr = attrs[i];
         if (attr == null) continue;

         String attrDescr = getDescription(attr);
         newAttrs[i] = new MBeanAttributeInfo(attr.getName(), attr.getType(), attrDescr, attr.isReadable(), attr.isWritable(), attr.isIs());
      }

      return newAttrs;
   }

   private MBeanOperationInfo[] setupOperations(MBeanOperationInfo[] opers)
   {
      if (opers == null) return null;

      MBeanOperationInfo[] newOpers = new MBeanOperationInfo[opers.length];
      for (int i = 0; i < opers.length; ++i)
      {
         MBeanOperationInfo oper = opers[i];
         if (oper == null) continue;

         MBeanParameterInfo[] newParams = null;
         MBeanParameterInfo[] params = oper.getSignature();
         if (params != null)
         {
            newParams = new MBeanParameterInfo[params.length];
            for (int j = 0; j < params.length; ++j)
            {
               MBeanParameterInfo param = params[j];
               if (param == null) continue;

               String paramName = getParameterName(oper, param, j);
               String paramDescr = getDescription(oper, param, j);
               newParams[j] = new MBeanParameterInfo(paramName, param.getType(), paramDescr);
            }
         }

         String operDescr = getDescription(oper);
         int operImpact = getImpact(oper);
         newOpers[i] = new MBeanOperationInfo(oper.getName(), operDescr, newParams, oper.getReturnType(), operImpact);
      }

      return newOpers;
   }

   private MBeanNotificationInfo[] setupNotifications(MBeanNotificationInfo[] notifs)
   {
      return notifs == null ? null : notifs;
   }

   private MBeanMetaData introspectMBean(Object implementation, Class management)
   {
      MBeanMetaData metadata = MBeanMetaData.Factory.create();
      metadata.setMBean(implementation);
      metadata.setClassLoader(implementation.getClass().getClassLoader());
      metadata.setMBeanStandard(true);
      metadata.setMBeanInterface(management);

      MBeanIntrospector introspector = new MBeanIntrospector();
      introspector.introspect(metadata);
      if (!introspector.isMBeanCompliant(metadata)) return null;

      return metadata;
   }

   private class StandardMBeanSupport extends AbstractDynamicMBean
   {
      public synchronized MBeanInfo getMBeanInfo()
      {
         return StandardMBean.this.getMBeanInfo();
      }

      protected Object getResource()
      {
         return StandardMBean.this.getImplementation();
      }
   }
}
