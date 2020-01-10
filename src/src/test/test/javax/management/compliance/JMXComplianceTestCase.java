/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.compliance;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.jar.JarFile;

/**
 * @version $Revision: 1.4 $
 */
public abstract class JMXComplianceTestCase extends ComplianceTestCase
{
   public JMXComplianceTestCase(String s)
   {
      super(s);
   }

   protected ClassLoader createClassLoader() throws MalformedURLException
   {
      return createJMXRIWithTestsClassLoader();
   }

   protected JarFile loadJar() throws IOException
   {
      File jmxri = new File("dist/test/jmxri.jar");
      if (!jmxri.exists()) fail("JMXRI jar is not available");
      JarFile jmxriJar = new JarFile(jmxri);
      return jmxriJar;
   }

   public void test_AndQueryExp() throws Exception
   {
      check("AndQueryExp");
   }

   public void test_Attribute() throws Exception
   {
      check("Attribute");
   }

   public void test_AttributeChangeNotification() throws Exception
   {
      check("AttributeChangeNotification");
   }

   public void test_AttributeChangeNotificationFilter() throws Exception
   {
      check("AttributeChangeNotificationFilter");
   }

   public void test_AttributeList() throws Exception
   {
      check("AttributeList");
   }

   public void test_AttributeNotFoundException() throws Exception
   {
      check("AttributeNotFoundException");
   }

   public void test_AttributeValueExp() throws Exception
   {
      check("AttributeValueExp");
   }

   public void test_BadAttributeValueExpException() throws Exception
   {
      check("BadAttributeValueExpException");
   }

   public void test_BadBinaryOpValueExpException() throws Exception
   {
      check("BadBinaryOpValueExpException");
   }

   public void test_BadStringOperationException() throws Exception
   {
      check("BadStringOperationException");
   }

   public void test_BetweenQueryExp() throws Exception
   {
      check("BetweenQueryExp");
   }

   public void test_BinaryOpValueExp() throws Exception
   {
      check("BinaryOpValueExp");
   }

   public void test_BinaryRelQueryExp() throws Exception
   {
      check("BinaryRelQueryExp");
   }

   public void test_BooleanValueExp() throws Exception
   {
      check("BooleanValueExp");
   }

   public void test_ClassAttributeValueExp() throws Exception
   {
      check("ClassAttributeValueExp");
   }

   public void test_DefaultLoaderRepository() throws Exception
   {
      check("DefaultLoaderRepository");
   }

   public void test_Descriptor() throws Exception
   {
      check("Descriptor");
   }

   public void test_DescriptorAccess() throws Exception
   {
      check("DescriptorAccess");
   }

   public void test_DynamicMBean() throws Exception
   {
      check("DynamicMBean");
   }

   public void test_InQueryExp() throws Exception
   {
      check("InQueryExp");
   }

   public void test_InstanceAlreadyExistsException() throws Exception
   {
      check("InstanceAlreadyExistsException");
   }

   public void test_InstanceNotFoundException() throws Exception
   {
      check("InstanceNotFoundException");
   }

   public void test_IntrospectionException() throws Exception
   {
      check("IntrospectionException");
   }

   public void test_InvalidApplicationException() throws Exception
   {
      check("InvalidApplicationException");
   }

   public void test_InvalidAttributeValueException() throws Exception
   {
      check("InvalidAttributeValueException");
   }

   public void test_JMException() throws Exception
   {
      check("JMException");
   }

   public void test_JMRuntimeException() throws Exception
   {
      check("JMRuntimeException");
   }

   public void test_ListenerNotFoundException() throws Exception
   {
      check("ListenerNotFoundException");
   }

   public void test_MalformedObjectNameException() throws Exception
   {
      check("MalformedObjectNameException");
   }

   public void test_MatchQueryExp() throws Exception
   {
      check("MatchQueryExp");
   }

   public void test_MBeanAttributeInfo() throws Exception
   {
      check("MBeanAttributeInfo");
   }

   public void test_MBeanConstructorInfo() throws Exception
   {
      check("MBeanConstructorInfo");
   }

   public void test_MBeanException() throws Exception
   {
      check("MBeanException");
   }

   public void test_MBeanFeatureInfo() throws Exception
   {
      check("MBeanFeatureInfo");
   }

   public void test_MBeanInfo() throws Exception
   {
      check("MBeanInfo");
   }

   public void test_MBeanNotificationInfo() throws Exception
   {
      check("MBeanNotificationInfo");
   }

   public void test_MBeanOperationInfo() throws Exception
   {
      check("MBeanOperationInfo");
   }

   public void test_MBeanParameterInfo() throws Exception
   {
      check("MBeanParameterInfo");
   }

   public void test_MBeanPermission() throws Exception
   {
      check("MBeanPermission");
   }

   public void test_MBeanRegistration() throws Exception
   {
      check("MBeanRegistration");
   }

   public void test_MBeanRegistrationException() throws Exception
   {
      check("MBeanRegistrationException");
   }

   public void test_MBeanServer() throws Exception
   {
      check("MBeanServer");
   }

   public void test_MBeanServerBuilder() throws Exception
   {
      check("MBeanServerBuilder");
   }

   public void test_MBeanServerConnection() throws Exception
   {
      check("MBeanServerConnection");
   }

   public void test_MBeanServerDelegate() throws Exception
   {
      check("MBeanServerDelegate");
   }

   public void test_MBeanServerDelegateMBean() throws Exception
   {
      check("MBeanServerDelegateMBean");
   }

   public void test_MBeanServerFactory() throws Exception
   {
      check("MBeanServerFactory");
   }

   public void test_MBeanServerInvocationHandler() throws Exception
   {
      check("MBeanServerInvocationHandler");
   }

   public void test_MBeanServerNotification() throws Exception
   {
      check("MBeanServerNotification");
   }

   public void test_MBeanServerPermission() throws Exception
   {
      check("MBeanServerPermission");
   }

   public void test_MBeanTrustPermission() throws Exception
   {
      check("MBeanTrustPermission");
   }

   public void test_NotCompliantMBeanException() throws Exception
   {
      check("NotCompliantMBeanException");
   }

   public void test_Notification() throws Exception
   {
      check("Notification");
   }

   public void test_NotificationBroadcaster() throws Exception
   {
      check("NotificationBroadcaster");
   }

   public void test_NotificationBroadcasterSupport() throws Exception
   {
      check("NotificationBroadcasterSupport");
   }

   public void test_NotificationEmitter() throws Exception
   {
      check("NotificationEmitter");
   }

   public void test_NotificationFilter() throws Exception
   {
      check("NotificationFilter");
   }

   public void test_NotificationFilterSupport() throws Exception
   {
      check("NotificationFilterSupport");
   }

   public void test_NotificationListener() throws Exception
   {
      check("NotificationListener");
   }

   public void test_NotQueryExp() throws Exception
   {
      check("NotQueryExp");
   }

   public void test_NumericValueExp() throws Exception
   {
      check("NumericValueExp");
   }

   public void test_ObjectInstance() throws Exception
   {
      check("ObjectInstance");
   }

   public void test_ObjectName() throws Exception
   {
      check("ObjectName");
   }

   public void test_OperationsException() throws Exception
   {
      check("OperationsException");
   }

   public void test_OrQueryExp() throws Exception
   {
      check("OrQueryExp");
   }

   public void test_PersistentMBean() throws Exception
   {
      check("PersistentMBean");
   }

   public void test_QualifiedAttributeValueExp() throws Exception
   {
      check("QualifiedAttributeValueExp");
   }

   public void test_Query() throws Exception
   {
      check("Query");
   }

   public void test_QueryEval() throws Exception
   {
      check("QueryEval");
   }

   public void test_QueryExp() throws Exception
   {
      check("QueryExp");
   }

   public void test_ReflectionException() throws Exception
   {
      check("ReflectionException");
   }

   public void test_RuntimeErrorException() throws Exception
   {
      check("RuntimeErrorException");
   }

   public void test_RuntimeMBeanException() throws Exception
   {
      check("RuntimeMBeanException");
   }

   public void test_RuntimeOperationsException() throws Exception
   {
      check("RuntimeOperationsException");
   }

   public void test_ServiceNotFoundException() throws Exception
   {
      check("ServiceNotFoundException");
   }

   public void test_StandardMBean() throws Exception
   {
      check("StandardMBean");
   }

   public void test_StringValueExp() throws Exception
   {
      check("StringValueExp");
   }

   public void test_ValueExp() throws Exception
   {
      check("ValueExp");
   }

   public void test_loading_ClassLoaderRepository() throws Exception
   {
      check("loading.ClassLoaderRepository");
   }

   public void test_loading_DefaultLoaderRepository() throws Exception
   {
      check("loading.DefaultLoaderRepository");
   }

   public void test_loading_MLet() throws Exception
   {
      check("loading.MLet");
   }

   public void test_loading_MLetMBean() throws Exception
   {
      check("loading.MLetMBean");
   }

   public void test_loading_PrivateClassLoader() throws Exception
   {
      check("loading.PrivateClassLoader");
   }

   public void test_loading_PrivateMLet() throws Exception
   {
      check("loading.PrivateMLet");
   }

   public void test_modelmbean_DescriptorSupport() throws Exception
   {
      check("modelmbean.DescriptorSupport");
   }

   public void test_modelmbean_InvalidTargetObjectTypeException() throws Exception
   {
      check("modelmbean.InvalidTargetObjectTypeException");
   }

   public void test_modelmbean_ModelMBean() throws Exception
   {
      check("modelmbean.ModelMBean");
   }

   public void test_modelmbean_ModelMBeanAttributeInfo() throws Exception
   {
      check("modelmbean.ModelMBeanAttributeInfo");
   }

   public void test_modelmbean_ModelMBeanConstructorInfo() throws Exception
   {
      check("modelmbean.ModelMBeanConstructorInfo");
   }

   public void test_modelmbean_ModelMBeanInfo() throws Exception
   {
      check("modelmbean.ModelMBeanInfo");
   }

   public void test_modelmbean_ModelMBeanInfoSupport() throws Exception
   {
      check("modelmbean.ModelMBeanInfoSupport");
   }

   public void test_modelmbean_ModelMBeanNotificationBroadcaster() throws Exception
   {
      check("modelmbean.ModelMBeanNotificationBroadcaster");
   }

   public void test_modelmbean_ModelMBeanNotificationInfo() throws Exception
   {
      check("modelmbean.ModelMBeanNotificationInfo");
   }

   public void test_modelmbean_ModelMBeanOperationInfo() throws Exception
   {
      check("modelmbean.ModelMBeanOperationInfo");
   }

   public void test_modelmbean_RequiredModelMBean() throws Exception
   {
      check("modelmbean.RequiredModelMBean");
   }

   public void test_modelmbean_XMLParseException() throws Exception
   {
      check("modelmbean.XMLParseException");
   }

   public void test_monitor_CounterMonitor() throws Exception
   {
      check("monitor.CounterMonitor");
   }

   public void test_monitor_CounterMonitorMBean() throws Exception
   {
      check("monitor.CounterMonitorMBean");
   }

   public void test_monitor_GaugeMonitor() throws Exception
   {
      check("monitor.GaugeMonitor");
   }

   public void test_monitor_GaugeMonitorMBean() throws Exception
   {
      check("monitor.GaugeMonitorMBean");
   }

   public void test_monitor_Monitor() throws Exception
   {
      check("monitor.Monitor");
   }

   public void test_monitor_MonitorMBean() throws Exception
   {
      check("monitor.MonitorMBean");
   }

   public void test_monitor_StringMonitor() throws Exception
   {
      check("monitor.StringMonitor");
   }

   public void test_monitor_StringMonitorMBean() throws Exception
   {
      check("monitor.StringMonitorMBean");
   }

   public void test_monitor_MonitorNotification() throws Exception
   {
      check("monitor.MonitorNotification");
   }

   public void test_monitor_MonitorSettingException() throws Exception
   {
      check("monitor.MonitorSettingException");
   }

   public void test_openmbean_ArrayType() throws Exception
   {
      check("openmbean.ArrayType");
   }

   public void test_openmbean_CompositeData() throws Exception
   {
      check("openmbean.CompositeData");
   }

   public void test_openmbean_CompositeDataSupport() throws Exception
   {
      check("openmbean.CompositeDataSupport");
   }

   public void test_openmbean_CompositeType() throws Exception
   {
      check("openmbean.CompositeType");
   }

   public void test_openmbean_InvalidKeyException() throws Exception
   {
      check("openmbean.InvalidKeyException");
   }

   public void test_openmbean_InvalidOpenTypeException() throws Exception
   {
      check("openmbean.InvalidOpenTypeException");
   }

   public void test_openmbean_KeyAlreadyExistsException() throws Exception
   {
      check("openmbean.KeyAlreadyExistsException");
   }

   public void test_openmbean_OpenDataException() throws Exception
   {
      check("openmbean.OpenDataException");
   }

   public void test_openmbean_OpenMBeanAttributeInfo() throws Exception
   {
      check("openmbean.OpenMBeanAttributeInfo");
   }

   public void test_openmbean_OpenMBeanAttributeInfoSupport() throws Exception
   {
      check("openmbean.OpenMBeanAttributeInfoSupport");
   }

   public void test_openmbean_OpenMBeanConstructorInfo() throws Exception
   {
      check("openmbean.OpenMBeanConstructorInfo");
   }

   public void test_openmbean_OpenMBeanConstructorInfoSupport() throws Exception
   {
      check("openmbean.OpenMBeanConstructorInfoSupport");
   }

   public void test_openmbean_OpenMBeanInfo() throws Exception
   {
      check("openmbean.OpenMBeanInfo");
   }

   public void test_openmbean_OpenMBeanInfoSupport() throws Exception
   {
      check("openmbean.OpenMBeanInfoSupport");
   }

   public void test_openmbean_OpenMBeanOperationInfo() throws Exception
   {
      check("openmbean.OpenMBeanOperationInfo");
   }

   public void test_openmbean_OpenMBeanOperationInfoSupport() throws Exception
   {
      check("openmbean.OpenMBeanOperationInfoSupport");
   }

   public void test_openmbean_OpenMBeanParameterInfo() throws Exception
   {
      check("openmbean.OpenMBeanParameterInfo");
   }

   public void test_openmbean_OpenMBeanParameterInfoSupport() throws Exception
   {
      check("openmbean.OpenMBeanParameterInfoSupport");
   }

   public void test_openmbean_OpenType() throws Exception
   {
      check("openmbean.OpenType");
   }

   public void test_openmbean_SimpleType() throws Exception
   {
      check("openmbean.SimpleType");
   }

   public void test_openmbean_TabularData() throws Exception
   {
      check("openmbean.TabularData");
   }

   public void test_openmbean_TabularDataSupport() throws Exception
   {
      check("openmbean.TabularDataSupport");
   }

   public void test_openmbean_TabularType() throws Exception
   {
      check("openmbean.TabularType");
   }

   public void test_relation_InvalidRelationIdException() throws Exception
   {
      check("relation.InvalidRelationIdException");
   }

   public void test_relation_InvalidRelationServiceException() throws Exception
   {
      check("relation.InvalidRelationServiceException");
   }

   public void test_relation_InvalidRelationTypeException() throws Exception
   {
      check("relation.InvalidRelationTypeException");
   }

   public void test_relation_InvalidRoleInfoException() throws Exception
   {
      check("relation.InvalidRoleInfoException");
   }

   public void test_relation_InvalidRoleValueException() throws Exception
   {
      check("relation.InvalidRoleValueException");
   }

   public void test_relation_MBeanServerNotificationFilter() throws Exception
   {
      check("relation.MBeanServerNotificationFilter");
   }

   public void test_relation_Relation() throws Exception
   {
      check("relation.Relation");
   }

   public void test_relation_RelationException() throws Exception
   {
      check("relation.RelationException");
   }

   public void test_relation_RelationNotFoundException() throws Exception
   {
      check("relation.RelationNotFoundException");
   }

   public void test_relation_RelationNotification() throws Exception
   {
      check("relation.RelationNotification");
   }

   public void test_relation_RelationService() throws Exception
   {
      check("relation.RelationService");
   }

   public void test_relation_RelationServiceMBean() throws Exception
   {
      check("relation.RelationServiceMBean");
   }

   public void test_relation_RelationServiceNotRegisteredException() throws Exception
   {
      check("relation.RelationServiceNotRegisteredException");
   }

   public void test_relation_RelationSupport() throws Exception
   {
      check("relation.RelationSupport");
   }

   public void test_relation_RelationSupportMBean() throws Exception
   {
      check("relation.RelationSupportMBean");
   }

   public void test_relation_RelationType() throws Exception
   {
      check("relation.RelationType");
   }

   public void test_relation_RelationTypeNotFoundException() throws Exception
   {
      check("relation.RelationTypeNotFoundException");
   }

   public void test_relation_RelationTypeSupport() throws Exception
   {
      check("relation.RelationTypeSupport");
   }

   public void test_relation_Role() throws Exception
   {
      check("relation.Role");
   }

   public void test_relation_RoleInfo() throws Exception
   {
      check("relation.RoleInfo");
   }

   public void test_relation_RoleInfoNotFoundException() throws Exception
   {
      check("relation.RoleInfoNotFoundException");
   }

   public void test_relation_RoleList() throws Exception
   {
      check("relation.RoleList");
   }

   public void test_relation_RoleNotFoundException() throws Exception
   {
      check("relation.RoleNotFoundException");
   }

   public void test_relation_RoleResult() throws Exception
   {
      check("relation.RoleResult");
   }

   public void test_relation_RoleStatus() throws Exception
   {
      check("relation.RoleStatus");
   }

   public void test_relation_RoleUnresolved() throws Exception
   {
      check("relation.RoleUnresolved");
   }

   public void test_relation_RoleUnresolvedList() throws Exception
   {
      check("relation.RoleUnresolvedList");
   }

   public void test_timer_Timer() throws Exception
   {
      check("timer.Timer");
   }

   public void test_timer_TimerMBean() throws Exception
   {
      check("timer.TimerMBean");
   }

   public void test_timer_TimerAlarmClockNotification() throws Exception
   {
      check("timer.TimerAlarmClockNotification");
   }

   public void test_timer_TimerNotification() throws Exception
   {
      check("timer.TimerNotification");
   }
}
