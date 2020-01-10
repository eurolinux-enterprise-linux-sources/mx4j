/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.compliance.serialization.support;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;
import javax.management.AttributeChangeNotification;
import javax.management.AttributeChangeNotificationFilter;
import javax.management.AttributeList;
import javax.management.AttributeValueExp;
import javax.management.BadBinaryOpValueExpException;
import javax.management.JMRuntimeException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanFeatureInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanServerNotification;
import javax.management.MBeanTrustPermission;
import javax.management.Notification;
import javax.management.NotificationFilterSupport;
import javax.management.ObjectName;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeOperationsException;
import javax.management.StringValueExp;
import javax.management.ValueExp;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanConstructorInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanNotificationInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import javax.management.monitor.MonitorNotification;
import javax.management.relation.MBeanServerNotificationFilter;
import javax.management.relation.RelationNotification;
import javax.management.relation.RelationTypeSupport;
import javax.management.relation.Role;
import javax.management.relation.RoleInfo;
import javax.management.relation.RoleList;
import javax.management.relation.RoleResult;
import javax.management.relation.RoleUnresolved;
import javax.management.relation.RoleUnresolvedList;
import javax.management.timer.TimerNotification;

/**
 * @version $Revision: 1.7 $
 */
public class Comparator
{
   public void compareAndQueryExp(Object o1, Object o2) throws Exception
   {
      compareQueryExp(o1, o2);
   }

   public void compareAttributeChangeNotification(Object o1, Object o2)
   {
      compareNotification(o1, o2);

      AttributeChangeNotification n1 = (AttributeChangeNotification)o1;
      AttributeChangeNotification n2 = (AttributeChangeNotification)o2;

      if (!n1.getAttributeName().equals(n2.getAttributeName())) throw new RuntimeException();
      if (!n1.getAttributeType().equals(n2.getAttributeType())) throw new RuntimeException();
      if (!n1.getNewValue().equals(n2.getNewValue())) throw new RuntimeException();
      if (!n1.getOldValue().equals(n2.getOldValue())) throw new RuntimeException();
   }

   public void compareAttributeChangeNotificationFilter(Object o1, Object o2)
   {
      AttributeChangeNotificationFilter f1 = (AttributeChangeNotificationFilter)o1;
      AttributeChangeNotificationFilter f2 = (AttributeChangeNotificationFilter)o2;

      if (!f1.getEnabledAttributes().equals(f2.getEnabledAttributes())) throw new RuntimeException();
   }

   public void compareAttributeList(Object o1, Object o2)
   {
      AttributeList a1 = (AttributeList)o1;
      AttributeList a2 = (AttributeList)o2;
      // It's enough AttributeList.equals()
      if (!a1.equals(a2)) throw new RuntimeException();
   }

   public void compareAttributeNotFoundException(Object o1, Object o2)
   {
      compareOperationsException(o1, o2);
   }

   public void compareAttributeValueExp(Object o1, Object o2)
   {
      AttributeValueExp val1 = (AttributeValueExp)o1;
      AttributeValueExp val2 = (AttributeValueExp)o2;

      if (!val1.getAttributeName().equals(val2.getAttributeName())) throw new RuntimeException();
   }

   public void compareBadAttributeValueExpException(Object o1, Object o2)
   {
      // No way to compare them
   }

   public void compareBadBinaryOpValueExpException(Object o1, Object o2)
   {
      BadBinaryOpValueExpException b1 = (BadBinaryOpValueExpException)o1;
      BadBinaryOpValueExpException b2 = (BadBinaryOpValueExpException)o2;
      compareStringValueExp(b1.getExp(), b2.getExp());
   }

   public void compareBadStringOperationException(Object o1, Object o2)
   {
      // No way to compare them
   }

   public void compareBetweenQueryExp(Object o1, Object o2) throws Exception
   {
      compareQueryExp(o1, o2);
   }

   public void compareBinaryOpValueExp(Object o1, Object o2) throws Exception
   {
      ValueExp val1 = (ValueExp)o1;
      ValueExp val2 = (ValueExp)o2;

      compareNumericValueExp(val1.apply(null), val2.apply(null));
   }

   public void compareBinaryRelQueryExp(Object o1, Object o2) throws Exception
   {
      compareQueryExp(o1, o2);
   }

   public void compareBooleanValueExp(Object o1, Object o2) throws Exception
   {
      Field field = o1.getClass().getDeclaredField("val");
      field.setAccessible(true);
      boolean res1 = field.getBoolean(o1);
      boolean res2 = field.getBoolean(o2);
      if (!res1 || !res2) throw new RuntimeException();
   }

   public void compareClassAttributeValueExp(Object o1, Object o2)
   {
      // Nothing to compare
   }

   public void compareInQueryExp(Object o1, Object o2) throws Exception
   {
      compareQueryExp(o1, o2);
   }

   public void compareInstanceAlreadyExistsException(Object o1, Object o2)
   {
      compareOperationsException(o1, o2);
   }

   public void compareInstanceNotFoundException(Object o1, Object o2)
   {
      compareOperationsException(o1, o2);
   }

   public void compareIntrospectionException(Object o1, Object o2)
   {
      compareOperationsException(o1, o2);
   }

   public void compareInvalidApplicationException(Object o1, Object o2)
   {
      // No way to compare them
   }

   public void compareInvalidAttributeValueException(Object o1, Object o2)
   {
      compareOperationsException(o1, o2);
   }

   public void compareJMException(Object o1, Object o2)
   {
      compareException(o1, o2);
   }

   public void compareError(Object o1, Object o2)
   {
      Error x1 = (Error)o1;
      Error x2 = (Error)o2;
      if (!x1.getMessage().equals(x2.getMessage())) throw new RuntimeException();
   }

   public void compareException(Object o1, Object o2)
   {
      Exception x1 = (Exception)o1;
      Exception x2 = (Exception)o2;
      if (!x1.getMessage().equals(x2.getMessage())) throw new RuntimeException();
   }

   public void compareJMRuntimeException(Object o1, Object o2)
   {
      JMRuntimeException x1 = (JMRuntimeException)o1;
      JMRuntimeException x2 = (JMRuntimeException)o2;
      if (!x1.getMessage().equals(x2.getMessage())) throw new RuntimeException();
   }

   public void compareListenerNotFoundException(Object o1, Object o2)
   {
      compareOperationsException(o1, o2);
   }

   public void compareMalformedObjectNameException(Object o1, Object o2)
   {
      compareOperationsException(o1, o2);
   }

   public void compareMatchQueryExp(Object o1, Object o2) throws Exception
   {
      // Cannot be compared, require MBeanServer
   }

   public void compareMBeanAttributeInfo(Object o1, Object o2)
   {
      compareMBeanFeatureInfo(o1, o2);

      MBeanAttributeInfo a1 = (MBeanAttributeInfo)o1;
      MBeanAttributeInfo a2 = (MBeanAttributeInfo)o2;

      if (!a1.getType().equals(a2.getType())) throw new RuntimeException();
      if (a1.isReadable() != a2.isReadable()) throw new RuntimeException();
      if (a1.isWritable() != a2.isWritable()) throw new RuntimeException();
      if (a1.isIs() != a2.isIs()) throw new RuntimeException();
   }

   public void compareMBeanConstructorInfo(Object o1, Object o2)
   {
      compareMBeanFeatureInfo(o1, o2);

      MBeanConstructorInfo c1 = (MBeanConstructorInfo)o1;
      MBeanConstructorInfo c2 = (MBeanConstructorInfo)o2;

      MBeanParameterInfo[] p1 = c1.getSignature();
      MBeanParameterInfo[] p2 = c2.getSignature();

      if (p1.length != p2.length) throw new RuntimeException();
      for (int i = 0; i < p1.length; ++i)
      {
         compareMBeanParameterInfo(p1[i], p2[i]);
      }
   }

   public void compareMBeanException(Object o1, Object o2)
   {
      compareJMException(o1, o2);

      MBeanException x1 = (MBeanException)o1;
      MBeanException x2 = (MBeanException)o2;

      Exception xx1 = x1.getTargetException();
      Exception xx2 = x2.getTargetException();
      compareException(xx1, xx2);
   }

   public void compareMBeanFeatureInfo(Object o1, Object o2)
   {
      MBeanFeatureInfo f1 = (MBeanFeatureInfo)o1;
      MBeanFeatureInfo f2 = (MBeanFeatureInfo)o2;

      if (!f1.getName().equals(f2.getName())) throw new RuntimeException();
      if (!f1.getDescription().equals(f2.getDescription())) throw new RuntimeException();
   }

   public void compareMBeanInfo(Object o1, Object o2)
   {
      MBeanInfo i1 = (MBeanInfo)o1;
      MBeanInfo i2 = (MBeanInfo)o2;

      if (!i1.getClassName().equals(i2.getClassName())) throw new RuntimeException();
      if (!i1.getDescription().equals(i2.getDescription())) throw new RuntimeException();

      MBeanAttributeInfo[] a1 = i1.getAttributes();
      MBeanAttributeInfo[] a2 = i2.getAttributes();
      if (a1.length != a2.length) throw new RuntimeException();
      for (int i = 0; i < a1.length; ++i)
      {
         compareMBeanAttributeInfo(a1[i], a2[i]);
      }

      MBeanConstructorInfo[] c1 = i1.getConstructors();
      MBeanConstructorInfo[] c2 = i2.getConstructors();
      if (c1.length != c2.length) throw new RuntimeException();
      for (int i = 0; i < c1.length; ++i)
      {
         compareMBeanConstructorInfo(c1[i], c2[i]);
      }

      MBeanNotificationInfo[] n1 = i1.getNotifications();
      MBeanNotificationInfo[] n2 = i2.getNotifications();
      if (n1.length != n2.length) throw new RuntimeException();
      for (int i = 0; i < n1.length; ++i)
      {
         compareMBeanNotificationInfo(n1[i], n2[i]);
      }

      MBeanOperationInfo[] op1 = i1.getOperations();
      MBeanOperationInfo[] op2 = i2.getOperations();
      if (op1.length != op2.length) throw new RuntimeException();
      for (int i = 0; i < op1.length; ++i)
      {
         compareMBeanOperationInfo(op1[i], op2[i]);
      }
   }

   public void compareMBeanNotificationInfo(Object o1, Object o2)
   {
      compareMBeanFeatureInfo(o1, o2);

      MBeanNotificationInfo n1 = (MBeanNotificationInfo)o1;
      MBeanNotificationInfo n2 = (MBeanNotificationInfo)o2;

      String[] t1 = n1.getNotifTypes();
      String[] t2 = n2.getNotifTypes();

      if (t1.length != t2.length) throw new RuntimeException();
      for (int i = 0; i < t1.length; ++i)
      {
         if (!t1[i].equals(t2[i])) throw new RuntimeException();
      }
   }

   public void compareMBeanOperationInfo(Object o1, Object o2)
   {
      compareMBeanFeatureInfo(o1, o2);

      MBeanOperationInfo op1 = (MBeanOperationInfo)o1;
      MBeanOperationInfo op2 = (MBeanOperationInfo)o2;

      if (op1.getImpact() != op2.getImpact()) throw new RuntimeException();
      if (!op1.getReturnType().equals(op2.getReturnType())) throw new RuntimeException();

      MBeanParameterInfo[] p1 = op1.getSignature();
      MBeanParameterInfo[] p2 = op2.getSignature();

      if (p1.length != p2.length) throw new RuntimeException();
      for (int i = 0; i < p1.length; ++i)
      {
         compareMBeanParameterInfo(p1[i], p2[i]);
      }
   }

   public void compareMBeanParameterInfo(Object o1, Object o2)
   {
      compareMBeanFeatureInfo(o1, o2);

      MBeanParameterInfo p1 = (MBeanParameterInfo)o1;
      MBeanParameterInfo p2 = (MBeanParameterInfo)o2;

      if (!p1.getType().equals(p2.getType())) throw new RuntimeException();
   }

   public void compareMBeanRegistrationException(Object o1, Object o2)
   {
      compareMBeanException(o1, o2);
   }

   public void compareMBeanServerNotification(Object o1, Object o2)
   {
      compareNotification(o1, o2);
      ObjectName obj1 = ((MBeanServerNotification)o1).getMBeanName();
      ObjectName obj2 = ((MBeanServerNotification)o2).getMBeanName();
      if (!obj1.equals(obj2)) throw new RuntimeException();
   }

   public void compareMBeanTrustPermission(Object o1, Object o2)
   {
      MBeanTrustPermission p1 = (MBeanTrustPermission)o1;
      MBeanTrustPermission p2 = (MBeanTrustPermission)o2;
      if (!p1.getName().equals(p2.getName())) throw new RuntimeException();
   }

   public void compareNotCompliantMBeanException(Object o1, Object o2)
   {
      compareOperationsException(o1, o2);
   }

   public void compareNotification(Object o1, Object o2)
   {
      Notification n1 = (Notification)o1;
      Notification n2 = (Notification)o2;

      if (!n1.getSource().equals(n2.getSource())) throw new RuntimeException();
      if (!n1.getType().equals(n2.getType())) throw new RuntimeException();
      if (n1.getSequenceNumber() != n2.getSequenceNumber()) throw new RuntimeException();
      if (!n1.getUserData().equals(n2.getUserData())) throw new RuntimeException();
      if (!n1.getMessage().equals(n2.getMessage())) throw new RuntimeException();
   }

   public void compareNotificationFilterSupport(Object o1, Object o2)
   {
      NotificationFilterSupport f1 = (NotificationFilterSupport)o1;
      NotificationFilterSupport f2 = (NotificationFilterSupport)o2;

      Vector types1 = f1.getEnabledTypes();
      Vector types2 = f2.getEnabledTypes();
      if (!types1.containsAll(types2) || !types2.containsAll(types1)) throw new RuntimeException();
   }

   public void compareNotQueryExp(Object o1, Object o2) throws Exception
   {
      compareQueryExp(o1, o2);
   }

   public void compareNumericValueExp(Object o1, Object o2) throws Exception
   {
      Method method = o1.getClass().getDeclaredMethod("longValue", new Class[0]);
      method.setAccessible(true);
      Object res1 = method.invoke(o1, new Object[0]);
      Object res2 = method.invoke(o2, new Object[0]);
      if (!res1.equals(res2)) throw new RuntimeException();
   }

   public void compareOperationsException(Object o1, Object o2)
   {
      compareJMException(o1, o2);
   }

   public void compareOrQueryExp(Object o1, Object o2) throws Exception
   {
      compareQueryExp(o1, o2);
   }

   public void compareQualifiedAttributeValueExp(Object o1, Object o2)
   {
      compareAttributeValueExp(o1, o2);
   }

   public void compareQueryExp(Object o1, Object o2) throws Exception
   {
      QueryExp exp1 = (QueryExp)o1;
      QueryExp exp2 = (QueryExp)o2;

      if (!exp1.apply(null)) throw new RuntimeException();
      if (!exp2.apply(null)) throw new RuntimeException();
   }

   public void compareReflectionException(Object o1, Object o2)
   {
      compareJMException(o1, o2);

      ReflectionException x1 = (ReflectionException)o1;
      ReflectionException x2 = (ReflectionException)o2;

      Exception xx1 = x1.getTargetException();
      Exception xx2 = x2.getTargetException();
      compareException(xx1, xx2);
   }

   public void compareRuntimeErrorException(Object o1, Object o2)
   {
      compareJMRuntimeException(o1, o2);

      RuntimeErrorException x1 = (RuntimeErrorException)o1;
      RuntimeErrorException x2 = (RuntimeErrorException)o2;

      Error e1 = x1.getTargetError();
      Error e2 = x2.getTargetError();
      compareError(e1, e2);
   }

   public void compareRuntimeMBeanException(Object o1, Object o2)
   {
      compareJMRuntimeException(o1, o2);

      RuntimeMBeanException x1 = (RuntimeMBeanException)o1;
      RuntimeMBeanException x2 = (RuntimeMBeanException)o2;

      RuntimeException e1 = x1.getTargetException();
      RuntimeException e2 = x2.getTargetException();
      compareException(e1, e2);
   }

   public void compareRuntimeOperationsException(Object o1, Object o2)
   {
      compareJMRuntimeException(o1, o2);

      RuntimeOperationsException x1 = (RuntimeOperationsException)o1;
      RuntimeOperationsException x2 = (RuntimeOperationsException)o2;

      RuntimeException e1 = x1.getTargetException();
      RuntimeException e2 = x2.getTargetException();
      compareException(e1, e2);
   }

   public void compareServiceNotFoundException(Object o1, Object o2)
   {
      compareOperationsException(o1, o2);
   }

   public void compareStringValueExp(Object o1, Object o2)
   {
      StringValueExp s1 = (StringValueExp)o1;
      StringValueExp s2 = (StringValueExp)o2;
      if (!s1.getValue().equals(s2.getValue())) throw new RuntimeException();
   }


   //
   // MODELMBEAN package
   //
   public void compareDescriptorSupport(Object o1, Object o2)
   {
      DescriptorSupport s1 = (DescriptorSupport)o1;
      DescriptorSupport s2 = (DescriptorSupport)o2;

      String[] names1 = s1.getFieldNames();
      String[] names2 = s2.getFieldNames();
      Object[] values1 = s1.getFieldValues(names1);
      Object[] values2 = s2.getFieldValues(names2);

      ArrayList list1 = new ArrayList(Arrays.asList(names1));
      ArrayList list2 = new ArrayList(Arrays.asList(names2));
      if (!list1.containsAll(list2) || !list2.containsAll(list1)) throw new RuntimeException();

      list1 = new ArrayList(Arrays.asList(values1));
      list2 = new ArrayList(Arrays.asList(values2));
      if (!list1.containsAll(list2) || !list2.containsAll(list1)) throw new RuntimeException();
   }

   public void compareInvalidTargetObjectTypeException(Object o1, Object o2)
   {
      // No way to compare them
   }

   public void compareModelMBeanAttributeInfo(Object o1, Object o2)
   {
      compareMBeanAttributeInfo(o1, o2);
      ModelMBeanAttributeInfo a1 = (ModelMBeanAttributeInfo)o1;
      ModelMBeanAttributeInfo a2 = (ModelMBeanAttributeInfo)o2;
      compareDescriptorSupport(a1.getDescriptor(), a2.getDescriptor());
   }

   public void compareModelMBeanConstructorInfo(Object o1, Object o2)
   {
      compareMBeanConstructorInfo(o1, o2);

      ModelMBeanConstructorInfo a1 = (ModelMBeanConstructorInfo)o1;
      ModelMBeanConstructorInfo a2 = (ModelMBeanConstructorInfo)o2;
      compareDescriptorSupport(a1.getDescriptor(), a2.getDescriptor());
   }

   public void compareModelMBeanInfoSupport(Object o1, Object o2) throws MBeanException
   {
      compareMBeanInfo(o1, o2);

      ModelMBeanInfoSupport i1 = (ModelMBeanInfoSupport)o1;
      ModelMBeanInfoSupport i2 = (ModelMBeanInfoSupport)o2;
      compareDescriptorSupport(i1.getMBeanDescriptor(), i2.getMBeanDescriptor());
   }

   public void compareModelMBeanNotificationInfo(Object o1, Object o2)
   {
      compareMBeanNotificationInfo(o1, o2);

      ModelMBeanNotificationInfo n1 = (ModelMBeanNotificationInfo)o1;
      ModelMBeanNotificationInfo n2 = (ModelMBeanNotificationInfo)o2;
      compareDescriptorSupport(n1.getDescriptor(), n2.getDescriptor());
   }

   public void compareModelMBeanOperationInfo(Object o1, Object o2)
   {
      compareMBeanOperationInfo(o1, o2);

      ModelMBeanOperationInfo op1 = (ModelMBeanOperationInfo)o1;
      ModelMBeanOperationInfo op2 = (ModelMBeanOperationInfo)o2;
      compareDescriptorSupport(op1.getDescriptor(), op2.getDescriptor());
   }

   public void compareXMLParseException(Object o1, Object o2)
   {
      // Cannot be compared: JMXRI adds a custom message in addition to the one
      // provided to the XMLParseException constructor.
      // compareException(o1, o2);
   }


   //
   // MONITOR package
   //
   public void compareMonitorNotification(Object o1, Object o2)
   {
      compareNotification(o1, o2);

      MonitorNotification n1 = (MonitorNotification)o1;
      MonitorNotification n2 = (MonitorNotification)o2;

      if (!n1.getDerivedGauge().equals(n2.getDerivedGauge())) throw new RuntimeException();
      if (!n1.getObservedAttribute().equals(n2.getObservedAttribute())) throw new RuntimeException();
      if (!n1.getObservedObject().equals(n2.getObservedObject())) throw new RuntimeException();
      if (!n1.getTrigger().equals(n2.getTrigger())) throw new RuntimeException();
   }

   public void compareMonitorSettingException(Object o1, Object o2)
   {
      compareJMRuntimeException(o1, o2);
   }


   //
   // OPENMBEANS package
   //
   public void compareInvalidKeyException(Object o1, Object o2)
   {
      compareException(o1, o2);
   }

   public void compareInvalidOpenTypeException(Object o1, Object o2)
   {
      compareException(o1, o2);
   }

   public void compareKeyAlreadyExistsException(Object o1, Object o2)
   {
      compareException(o1, o2);
   }

   public void compareOpenDataException(Object o1, Object o2)
   {
      compareJMException(o1, o2);
   }

   //
   // RELATION package
   //
   public void compareInvalidRelationIdException(Object o1, Object o2)
   {
      compareRelationException(o1, o2);
   }

   public void compareInvalidRelationServiceException(Object o1, Object o2)
   {
      compareRelationException(o1, o2);
   }

   public void compareInvalidRelationTypeException(Object o1, Object o2)
   {
      compareRelationException(o1, o2);
   }

   public void compareInvalidRoleInfoException(Object o1, Object o2)
   {
      compareRelationException(o1, o2);
   }

   public void compareInvalidRoleValueException(Object o1, Object o2)
   {
      compareRelationException(o1, o2);
   }

   public void compareMBeanServerNotificationFilter(Object o1, Object o2)
   {
      compareNotificationFilterSupport(o1, o2);

      MBeanServerNotificationFilter f1 = (MBeanServerNotificationFilter)o1;
      MBeanServerNotificationFilter f2 = (MBeanServerNotificationFilter)o2;
      Vector names1 = null;
      try
      {
         names1 = f1.getEnabledObjectNames();
      }
      catch (NullPointerException ignored)
      {
         // JMX RI throws this
      }
      Vector names2 = null;
      try
      {
         names2 = f2.getEnabledObjectNames();
      }
      catch (NullPointerException ignored)
      {
         // JMX RI throws this
      }
      if (names1 != null && (!names1.containsAll(names2) || !names2.containsAll(names1))) throw new RuntimeException();
      if (names1 == null && names2 != null) throw new RuntimeException();

      Vector names3 = null;
      try
      {
         names3 = f1.getDisabledObjectNames();
      }
      catch (NullPointerException ignored)
      {
         // JMX RI throws this
      }
      Vector names4 = null;
      try
      {
         names4 = f1.getDisabledObjectNames();
      }
      catch (NullPointerException ignored)
      {
         // JMX RI throws this
      }
      if (names3 != null && (!names3.containsAll(names4) || !names4.containsAll(names3))) throw new RuntimeException();
      if (names3 == null && names4 != null) throw new RuntimeException();
   }

   public void compareRelationException(Object o1, Object o2)
   {
      compareJMException(o1, o2);
   }

   public void compareRelationNotFoundException(Object o1, Object o2)
   {
      compareRelationException(o1, o2);
   }

   public void compareRelationNotification(Object o1, Object o2)
   {
      compareNotification(o1, o2);

      RelationNotification n1 = (RelationNotification)o1;
      RelationNotification n2 = (RelationNotification)o2;

      if (!n1.getMBeansToUnregister().equals(n2.getMBeansToUnregister())) throw new RuntimeException();
      List news1 = n1.getNewRoleValue();
      List news2 = n2.getNewRoleValue();
      if (news1.size() != news2.size()) throw new RuntimeException();
      if (!n1.getObjectName().equals(n2.getObjectName())) throw new RuntimeException();
      List olds1 = n1.getOldRoleValue();
      List olds2 = n2.getOldRoleValue();
      if (olds1.size() != olds2.size()) throw new RuntimeException();
      if (!n1.getRelationId().equals(n2.getRelationId())) throw new RuntimeException();
      if (!n1.getRelationTypeName().equals(n2.getRelationTypeName())) throw new RuntimeException();
      if (!n1.getRoleName().equals(n2.getRoleName())) throw new RuntimeException();
   }

   public void compareRelationServiceNotRegisteredException(Object o1, Object o2)
   {
      compareRelationException(o1, o2);
   }

   public void compareRelationTypeNotFoundException(Object o1, Object o2)
   {
      compareRelationException(o1, o2);
   }

   public void compareRelationTypeSupport(Object o1, Object o2)
   {
      RelationTypeSupport r1 = (RelationTypeSupport)o1;
      RelationTypeSupport r2 = (RelationTypeSupport)o2;

      if (!r1.getRelationTypeName().equals(r2.getRelationTypeName())) throw new RuntimeException();
      List infos1 = r1.getRoleInfos();
      List infos2 = r2.getRoleInfos();
      // RoleInfo does not override equals() so List.equals() fails; just use size() here
      if (infos1.size() != infos2.size()) throw new RuntimeException();
   }

   public void compareRole(Object o1, Object o2)
   {
      Role r1 = (Role)o1;
      Role r2 = (Role)o2;

      if (!r1.getRoleName().equals(r2.getRoleName())) throw new RuntimeException();
      if (!r1.getRoleValue().equals(r2.getRoleValue())) throw new RuntimeException();
   }

   public void compareRoleInfo(Object o1, Object o2)
   {
      RoleInfo r1 = (RoleInfo)o1;
      RoleInfo r2 = (RoleInfo)o2;

      if (!r1.getDescription().equals(r2.getDescription())) throw new RuntimeException();
      if (r1.getMaxDegree() != r2.getMaxDegree()) throw new RuntimeException();
      if (r1.getMinDegree() != r2.getMinDegree()) throw new RuntimeException();
      if (!r1.getName().equals(r2.getName())) throw new RuntimeException();
      if (!r1.getRefMBeanClassName().equals(r2.getRefMBeanClassName())) throw new RuntimeException();
      if (!r1.isReadable() || !r2.isReadable()) throw new RuntimeException();
      if (!r1.isWritable() || !r2.isWritable()) throw new RuntimeException();
   }

   public void compareRoleInfoNotFoundException(Object o1, Object o2)
   {
      compareRelationException(o1, o2);
   }

   public void compareRoleList(Object o1, Object o2)
   {
      RoleList l1 = (RoleList)o1;
      RoleList l2 = (RoleList)o2;

      // Role does not override equals() so List.equals() fails; just use size() here
      if (l1.size() != l2.size()) throw new RuntimeException();
   }

   public void compareRoleNotFoundException(Object o1, Object o2)
   {
      compareRelationException(o1, o2);
   }

   public void compareRoleResult(Object o1, Object o2)
   {
      RoleResult r1 = (RoleResult)o1;
      RoleResult r2 = (RoleResult)o2;

      compareRoleList(r1.getRoles(), r2.getRoles());
      compareRoleUnresolvedList(r1.getRolesUnresolved(), r2.getRolesUnresolved());
   }

   public void compareRoleUnresolved(Object o1, Object o2)
   {
      RoleUnresolved r1 = (RoleUnresolved)o1;
      RoleUnresolved r2 = (RoleUnresolved)o2;

      if (r1.getProblemType() != r2.getProblemType()) throw new RuntimeException();
      if (!r1.getRoleName().equals(r2.getRoleName())) throw new RuntimeException();
      if (!r1.getRoleValue().equals(r2.getRoleValue())) throw new RuntimeException();
   }

   public void compareRoleUnresolvedList(Object o1, Object o2)
   {
      RoleUnresolvedList l1 = (RoleUnresolvedList)o1;
      RoleUnresolvedList l2 = (RoleUnresolvedList)o2;

      // RoleUnresolved does not override equals() so List.equals() fails; just use size() here
      if (l1.size() != l2.size()) throw new RuntimeException();
   }


   //
   // TIMER package
   //
   public void compareTimerNotification(Object o1, Object o2)
   {
      compareNotification(o1, o2);

      TimerNotification n1 = (TimerNotification)o1;
      TimerNotification n2 = (TimerNotification)o2;

      if (!n1.getNotificationID().equals(n2.getNotificationID())) throw new RuntimeException();
   }
}
