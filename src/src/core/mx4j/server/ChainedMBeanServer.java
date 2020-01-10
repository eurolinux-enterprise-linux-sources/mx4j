/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.server;

import java.io.ObjectInputStream;
import java.util.Set;
import javax.management.Attribute;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidAttributeValueException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanInfo;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.NotCompliantMBeanException;
import javax.management.NotificationFilter;
import javax.management.NotificationListener;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.loading.ClassLoaderRepository;

/**
 * Base class for chained MBeanServers.
 * By default this class delegates all method calls to the nested MBeanServer.
 * Subclass it to add behavior to one or more (or all) methods.
 *
 * @version $Revision: 1.6 $
 */
public class ChainedMBeanServer implements MBeanServer
{
   private MBeanServer m_server;

   /**
    * Creates a new ChainedMBeanServer that will delegate to an MBeanServer specified
    * using {@link #setMBeanServer}
    */
   public ChainedMBeanServer()
   {
      this(null);
   }

   /**
    * Creates a new ChainedMBeanServer that delegates to the specified <code>MBeanServer</code>.
    */
   public ChainedMBeanServer(MBeanServer server)
   {
      setMBeanServer(server);
   }

   /**
    * Returns the nested MBeanServer
    */
   protected synchronized MBeanServer getMBeanServer()
   {
      return m_server;
   }

   protected synchronized void setMBeanServer(MBeanServer server)
   {
      m_server = server;
   }

   public void addNotificationListener(ObjectName observed, NotificationListener listener, NotificationFilter filter, Object handback)
           throws InstanceNotFoundException
   {
      getMBeanServer().addNotificationListener(observed, listener, filter, handback);
   }

   public void addNotificationListener(ObjectName observed, ObjectName listener, NotificationFilter filter, Object handback)
           throws InstanceNotFoundException
   {
      getMBeanServer().addNotificationListener(observed, listener, filter, handback);
   }

   public ObjectInstance createMBean(String className, ObjectName objectName)
           throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException
   {
      return getMBeanServer().createMBean(className, objectName);
   }

   public ObjectInstance createMBean(String className, ObjectName objectName, Object[] args, String[] parameters)
           throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException
   {
      return getMBeanServer().createMBean(className, objectName, args, parameters);
   }

   public ObjectInstance createMBean(String className, ObjectName objectName, ObjectName loaderName)
           throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException
   {
      return getMBeanServer().createMBean(className, objectName, loaderName);
   }

   public ObjectInstance createMBean(String className, ObjectName objectName, ObjectName loaderName, Object[] args, String[] parameters)
           throws ReflectionException, InstanceAlreadyExistsException, MBeanRegistrationException, MBeanException, NotCompliantMBeanException, InstanceNotFoundException
   {
      return getMBeanServer().createMBean(className, objectName, loaderName, args, parameters);
   }

   public ObjectInputStream deserialize(String className, byte[] bytes)
           throws OperationsException, ReflectionException
   {
      return getMBeanServer().deserialize(className, bytes);
   }

   public ObjectInputStream deserialize(String className, ObjectName loaderName, byte[] bytes)
           throws InstanceNotFoundException, OperationsException, ReflectionException
   {
      return getMBeanServer().deserialize(className, loaderName, bytes);
   }

   public ObjectInputStream deserialize(ObjectName objectName, byte[] bytes)
           throws InstanceNotFoundException, OperationsException
   {
      return getMBeanServer().deserialize(objectName, bytes);
   }

   public Object getAttribute(ObjectName objectName, String attribute)
           throws MBeanException, AttributeNotFoundException, InstanceNotFoundException, ReflectionException
   {
      return getMBeanServer().getAttribute(objectName, attribute);
   }

   public AttributeList getAttributes(ObjectName objectName, String[] attributes)
           throws InstanceNotFoundException, ReflectionException
   {
      return getMBeanServer().getAttributes(objectName, attributes);
   }

   public String getDefaultDomain()
   {
      return getMBeanServer().getDefaultDomain();
   }

   public String[] getDomains()
   {
      return getMBeanServer().getDomains();
   }

   public Integer getMBeanCount()
   {
      return getMBeanServer().getMBeanCount();
   }

   public MBeanInfo getMBeanInfo(ObjectName objectName)
           throws InstanceNotFoundException, IntrospectionException, ReflectionException
   {
      return getMBeanServer().getMBeanInfo(objectName);
   }

   public ObjectInstance getObjectInstance(ObjectName objectName)
           throws InstanceNotFoundException
   {
      return getMBeanServer().getObjectInstance(objectName);
   }

   public Object instantiate(String className)
           throws ReflectionException, MBeanException
   {
      return getMBeanServer().instantiate(className);
   }

   public Object instantiate(String className, Object[] args, String[] parameters)
           throws ReflectionException, MBeanException
   {
      return getMBeanServer().instantiate(className, args, parameters);
   }

   public Object instantiate(String className, ObjectName loaderName)
           throws ReflectionException, MBeanException, InstanceNotFoundException
   {
      return getMBeanServer().instantiate(className, loaderName);
   }

   public Object instantiate(String className, ObjectName loaderName, Object[] args, String[] parameters)
           throws ReflectionException, MBeanException, InstanceNotFoundException
   {
      return getMBeanServer().instantiate(className, loaderName, args, parameters);
   }

   public Object invoke(ObjectName objectName, String methodName, Object[] args, String[] parameters)
           throws InstanceNotFoundException, MBeanException, ReflectionException
   {
      return getMBeanServer().invoke(objectName, methodName, args, parameters);
   }

   public boolean isInstanceOf(ObjectName objectName, String className)
           throws InstanceNotFoundException
   {
      return getMBeanServer().isInstanceOf(objectName, className);
   }

   public boolean isRegistered(ObjectName objectname)
   {
      return getMBeanServer().isRegistered(objectname);
   }

   public Set queryMBeans(ObjectName patternName, QueryExp filter)
   {
      return getMBeanServer().queryMBeans(patternName, filter);
   }

   public Set queryNames(ObjectName patternName, QueryExp filter)
   {
      return getMBeanServer().queryNames(patternName, filter);
   }

   public ObjectInstance registerMBean(Object mbean, ObjectName objectName)
           throws InstanceAlreadyExistsException, MBeanRegistrationException, NotCompliantMBeanException
   {
      return getMBeanServer().registerMBean(mbean, objectName);
   }

   public void removeNotificationListener(ObjectName observed, NotificationListener listener)
           throws InstanceNotFoundException, ListenerNotFoundException
   {
      getMBeanServer().removeNotificationListener(observed, listener);
   }

   public void removeNotificationListener(ObjectName observed, ObjectName listener)
           throws InstanceNotFoundException, ListenerNotFoundException
   {
      getMBeanServer().removeNotificationListener(observed, listener);
   }

   public void removeNotificationListener(ObjectName observed, ObjectName listener, NotificationFilter filter, Object handback)
           throws InstanceNotFoundException, ListenerNotFoundException
   {
      getMBeanServer().removeNotificationListener(observed, listener, filter, handback);
   }

   public void removeNotificationListener(ObjectName observed, NotificationListener listener, NotificationFilter filter, Object handback)
           throws InstanceNotFoundException, ListenerNotFoundException
   {
      getMBeanServer().removeNotificationListener(observed, listener, filter, handback);
   }

   public void setAttribute(ObjectName objectName, Attribute attribute)
           throws InstanceNotFoundException, AttributeNotFoundException, InvalidAttributeValueException, MBeanException, ReflectionException
   {
      getMBeanServer().setAttribute(objectName, attribute);
   }

   public AttributeList setAttributes(ObjectName objectName, AttributeList attributes)
           throws InstanceNotFoundException, ReflectionException
   {
      return getMBeanServer().setAttributes(objectName, attributes);
   }

   public void unregisterMBean(ObjectName objectName)
           throws InstanceNotFoundException, MBeanRegistrationException
   {
      getMBeanServer().unregisterMBean(objectName);
   }

   public ClassLoader getClassLoaderFor(ObjectName mbeanName)
           throws InstanceNotFoundException
   {
      return getMBeanServer().getClassLoaderFor(mbeanName);
   }

   public ClassLoader getClassLoader(ObjectName loaderName)
           throws InstanceNotFoundException
   {
      return getMBeanServer().getClassLoader(loaderName);
   }

   public ClassLoaderRepository getClassLoaderRepository()
   {
      return getMBeanServer().getClassLoaderRepository();
   }
}
