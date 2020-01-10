/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.compliance.serialization.support;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import javax.management.Attribute;
import javax.management.AttributeChangeNotification;
import javax.management.AttributeChangeNotificationFilter;
import javax.management.AttributeList;
import javax.management.AttributeNotFoundException;
import javax.management.AttributeValueExp;
import javax.management.BadAttributeValueExpException;
import javax.management.BadBinaryOpValueExpException;
import javax.management.BadStringOperationException;
import javax.management.Descriptor;
import javax.management.InstanceAlreadyExistsException;
import javax.management.InstanceNotFoundException;
import javax.management.IntrospectionException;
import javax.management.InvalidApplicationException;
import javax.management.InvalidAttributeValueException;
import javax.management.JMException;
import javax.management.JMRuntimeException;
import javax.management.ListenerNotFoundException;
import javax.management.MBeanAttributeInfo;
import javax.management.MBeanConstructorInfo;
import javax.management.MBeanException;
import javax.management.MBeanFeatureInfo;
import javax.management.MBeanInfo;
import javax.management.MBeanNotificationInfo;
import javax.management.MBeanOperationInfo;
import javax.management.MBeanParameterInfo;
import javax.management.MBeanPermission;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServerDelegate;
import javax.management.MBeanServerNotification;
import javax.management.MBeanServerPermission;
import javax.management.MBeanTrustPermission;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.Notification;
import javax.management.NotificationBroadcasterSupport;
import javax.management.NotificationFilterSupport;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.OperationsException;
import javax.management.Query;
import javax.management.QueryExp;
import javax.management.ReflectionException;
import javax.management.RuntimeErrorException;
import javax.management.RuntimeMBeanException;
import javax.management.RuntimeOperationsException;
import javax.management.ServiceNotFoundException;
import javax.management.StringValueExp;
import javax.management.ValueExp;
import javax.management.modelmbean.DescriptorSupport;
import javax.management.modelmbean.InvalidTargetObjectTypeException;
import javax.management.modelmbean.ModelMBean;
import javax.management.modelmbean.ModelMBeanAttributeInfo;
import javax.management.modelmbean.ModelMBeanConstructorInfo;
import javax.management.modelmbean.ModelMBeanInfo;
import javax.management.modelmbean.ModelMBeanInfoSupport;
import javax.management.modelmbean.ModelMBeanNotificationBroadcaster;
import javax.management.modelmbean.ModelMBeanNotificationInfo;
import javax.management.modelmbean.ModelMBeanOperationInfo;
import javax.management.modelmbean.XMLParseException;
import javax.management.monitor.MonitorNotification;
import javax.management.monitor.MonitorSettingException;
import javax.management.openmbean.ArrayType;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.openmbean.CompositeType;
import javax.management.openmbean.InvalidKeyException;
import javax.management.openmbean.InvalidOpenTypeException;
import javax.management.openmbean.KeyAlreadyExistsException;
import javax.management.openmbean.OpenDataException;
import javax.management.openmbean.OpenMBeanAttributeInfoSupport;
import javax.management.openmbean.OpenMBeanConstructorInfoSupport;
import javax.management.openmbean.OpenMBeanInfoSupport;
import javax.management.openmbean.OpenMBeanOperationInfo;
import javax.management.openmbean.OpenMBeanOperationInfoSupport;
import javax.management.openmbean.OpenMBeanParameterInfo;
import javax.management.openmbean.OpenMBeanParameterInfoSupport;
import javax.management.openmbean.OpenType;
import javax.management.openmbean.SimpleType;
import javax.management.openmbean.TabularDataSupport;
import javax.management.openmbean.TabularType;
import javax.management.relation.InvalidRelationIdException;
import javax.management.relation.InvalidRelationServiceException;
import javax.management.relation.InvalidRelationTypeException;
import javax.management.relation.InvalidRoleInfoException;
import javax.management.relation.InvalidRoleValueException;
import javax.management.relation.MBeanServerNotificationFilter;
import javax.management.relation.Relation;
import javax.management.relation.RelationException;
import javax.management.relation.RelationNotFoundException;
import javax.management.relation.RelationNotification;
import javax.management.relation.RelationService;
import javax.management.relation.RelationServiceNotRegisteredException;
import javax.management.relation.RelationType;
import javax.management.relation.RelationTypeNotFoundException;
import javax.management.relation.RelationTypeSupport;
import javax.management.relation.Role;
import javax.management.relation.RoleInfo;
import javax.management.relation.RoleInfoNotFoundException;
import javax.management.relation.RoleList;
import javax.management.relation.RoleNotFoundException;
import javax.management.relation.RoleResult;
import javax.management.relation.RoleStatus;
import javax.management.relation.RoleUnresolved;
import javax.management.relation.RoleUnresolvedList;
import javax.management.timer.TimerNotification;

/**
 * @version $Revision: 1.12 $
 */
public class Instantiator
{
   //
   // JAVAX.MANAGEMENT
   //

   public QueryExp createAndQueryExp()
   {
      return Query.and(createBetweenQueryExp(), createBinaryRelQueryExp());
   }

   public Attribute createAttribute()
   {
      return new Attribute("attr-name", "attr-value");
   }

   public AttributeChangeNotification createAttributeChangeNotification()
   {
      AttributeChangeNotification notification = new AttributeChangeNotification("notif-source", 13L, System.currentTimeMillis(), "notif-message", "attr-name", "attr-type", "old", "new");
      notification.setUserData("notif-user");
      return notification;
   }

   public AttributeChangeNotificationFilter createAttributeChangeNotificationFilter()
   {
      AttributeChangeNotificationFilter filter = new AttributeChangeNotificationFilter();
      filter.enableAttribute("attribute-name");
      return filter;
   }

   public AttributeList createAttributeList()
   {
      AttributeList list = new AttributeList();
      list.add(createAttribute());
      return list;
   }

   public AttributeNotFoundException createAttributeNotFoundException()
   {
      return new AttributeNotFoundException("AttributeNotFoundException");
   }

   public AttributeValueExp createAttributeValueExp()
   {
      return new AttributeValueExp("attribute");
   }

   public BadAttributeValueExpException createBadAttributeValueExpException()
   {
      return new BadAttributeValueExpException("BadAttributeValueExpException");
   }

   public BadBinaryOpValueExpException createBadBinaryOpValueExpException()
   {
      return new BadBinaryOpValueExpException(createStringValueExp());
   }

   public BadStringOperationException createBadStringOperationException()
   {
      return new BadStringOperationException("BadStringOperationException");
   }

   public QueryExp createBetweenQueryExp()
   {
      return Query.between(Query.value(5), Query.value(3), Query.value(7));
   }

   public ValueExp createBinaryOpValueExp()
   {
      return Query.plus(Query.value(2), Query.value(5));
   }

   public QueryExp createBinaryRelQueryExp()
   {
      return Query.eq(Query.value("simon"), Query.value("simon"));
   }

   public ValueExp createBooleanValueExp()
   {
      return Query.value(true);
   }

   public ValueExp createClassAttributeValueExp()
   {
      return Query.classattr();
   }

   public Descriptor createDescriptor()
   {
      throw new Error();
   }

   public QueryExp createInQueryExp()
   {
      return Query.in(Query.value(4), new ValueExp[]{Query.value(3), Query.value(4), Query.value(5)});
   }

   public InstanceAlreadyExistsException createInstanceAlreadyExistsException()
   {
      return new InstanceAlreadyExistsException("InstanceAlreadyExistsException");
   }

   public InstanceNotFoundException createInstanceNotFoundException()
   {
      return new InstanceNotFoundException("InstanceNotFoundException");
   }

   public IntrospectionException createIntrospectionException()
   {
      return new IntrospectionException("IntrospectionException");
   }

   public InvalidApplicationException createInvalidApplicationException()
   {
      return new InvalidApplicationException("InvalidApplicationException");
   }

   public InvalidAttributeValueException createInvalidAttributeValueException()
   {
      return new InvalidAttributeValueException("InvalidAttributeValueException");
   }

   public JMException createJMException()
   {
      return new JMException("JMException");
   }

   public JMRuntimeException createJMRuntimeException()
   {
      return new JMRuntimeException("JMRuntimeException");
   }

   public ListenerNotFoundException createListenerNotFoundException()
   {
      return new ListenerNotFoundException("ListenerNotFoundException");
   }

   public MalformedObjectNameException createMalformedObjectNameException()
   {
      return new MalformedObjectNameException("MalformedObjectNameException");
   }

   public QueryExp createMatchQueryExp()
   {
      return Query.match(createAttributeValueExp(), createStringValueExp());
   }

   public MBeanAttributeInfo createMBeanAttributeInfo()
   {
      return new MBeanAttributeInfo("name", "boolean", "description", true, true, true);
   }

   public MBeanConstructorInfo createMBeanConstructorInfo()
   {
      return new MBeanConstructorInfo("name", "description", new MBeanParameterInfo[]{createMBeanParameterInfo()});
   }

   public MBeanException createMBeanException()
   {
      return new MBeanException(new NullPointerException("NullPointerException"), "MBeanException");
   }

   public MBeanFeatureInfo createMBeanFeatureInfo()
   {
      return new MBeanFeatureInfo("name", "description");
   }

   public MBeanInfo createMBeanInfo()
   {
      return new MBeanInfo("my.class.name",
                           "description",
                           new MBeanAttributeInfo[]{createMBeanAttributeInfo()},
                           new MBeanConstructorInfo[]{createMBeanConstructorInfo()},
                           new MBeanOperationInfo[]{createMBeanOperationInfo()},
                           new MBeanNotificationInfo[]{createMBeanNotificationInfo()});
   }

   public MBeanNotificationInfo createMBeanNotificationInfo()
   {
      return new MBeanNotificationInfo(new String[]{"type1", "type2"}, "name", "description");
   }

   public MBeanOperationInfo createMBeanOperationInfo()
   {
      return new MBeanOperationInfo("name", "description", new MBeanParameterInfo[]{createMBeanParameterInfo()}, "java.lang.Exception", MBeanOperationInfo.UNKNOWN);
   }

   public MBeanParameterInfo createMBeanParameterInfo()
   {
      return new MBeanParameterInfo("name", "java.lang.Object", "description");
   }

   public MBeanPermission createMBeanPermission() throws MalformedObjectNameException
   {
      return new MBeanPermission("className", "methodName", createObjectName(), "instantiate,registerMBean");
   }

   public MBeanRegistrationException createMBeanRegistrationException()
   {
      return new MBeanRegistrationException(new NullPointerException("NullPointerException"), "MBeanRegistrationException");
   }

   public MBeanServerDelegate createMBeanServerDelegate()
   {
      return new MBeanServerDelegate();
   }

   public MBeanServerNotification createMBeanServerNotification() throws MalformedObjectNameException
   {
      MBeanServerNotification n = new MBeanServerNotification(MBeanServerNotification.REGISTRATION_NOTIFICATION, "notif-source", 13L, new ObjectName(":key=value"));
      n.setUserData("user-data");
      return n;
   }

   public MBeanServerPermission createMBeanServerPermission()
   {
      return new MBeanServerPermission("newMBeanServer");
   }

   public MBeanTrustPermission createMBeanTrustPermission()
   {
      return new MBeanTrustPermission("register");
   }

   public NotCompliantMBeanException createNotCompliantMBeanException()
   {
      return new NotCompliantMBeanException("NotCompliantMBeanException");
   }

   public Notification createNotification()
   {
      Notification notification = new Notification("notif-type", "notif-source", 13L, System.currentTimeMillis(), "notif-message");
      notification.setUserData("notif-user");
      return notification;
   }

   public NotificationBroadcasterSupport createNotificationBroadcasterSupport()
   {
      return new NotificationBroadcasterSupport();
   }

   public NotificationFilterSupport createNotificationFilterSupport()
   {
      NotificationFilterSupport filter = new NotificationFilterSupport();
      filter.enableType("notif-type");
      return filter;
   }

   public QueryExp createNotQueryExp()
   {
      return Query.not(Query.eq(Query.value("simon"), Query.value("bordet")));
   }

   public ValueExp createNumericValueExp()
   {
      return Query.value(1);
   }

   public ObjectInstance createObjectInstance() throws MalformedObjectNameException
   {
      return new ObjectInstance(new ObjectName("domain:p1=v1"), "java.lang.Object");
   }

   public ObjectName createObjectName() throws MalformedObjectNameException
   {
      return new ObjectName("domain?:p2=v2,*,p1=v1");
   }

   public OperationsException createOperationsException()
   {
      return new OperationsException("OperationsException");
   }

   public QueryExp createOrQueryExp()
   {
      return Query.or(createBetweenQueryExp(), createBinaryRelQueryExp());
   }

   public AttributeValueExp createQualifiedAttributeValueExp()
   {
      return Query.attr("mx4j.Foo", "attribute");
   }

   public ReflectionException createReflectionException()
   {
      return new ReflectionException(new NullPointerException("NullPointerException"), "ReflectionException");
   }

   public RuntimeErrorException createRuntimeErrorException()
   {
      return new RuntimeErrorException(new Error("Error"), "RuntimeErrorException");
   }

   public RuntimeMBeanException createRuntimeMBeanException()
   {
      return new RuntimeMBeanException(new NullPointerException("NullPointerException"), "RuntimeMBeanException");
   }

   public RuntimeOperationsException createRuntimeOperationsException()
   {
      return new RuntimeOperationsException(new NullPointerException("NullPointerException"), "RuntimeOperationsException");
   }

   public ServiceNotFoundException createServiceNotFoundException()
   {
      return new ServiceNotFoundException("ServiceNotFoundException");
   }

   public StringValueExp createStringValueExp()
   {
      return new StringValueExp("StringValueExp");
   }


   //
   // JAVAX.MANAGEMENT.MODELMBEAN
   //

   public DescriptorSupport createDescriptorSupport()
   {
      return new DescriptorSupport(new String[]{"name"}, new Object[]{"value"});
   }

   public InvalidTargetObjectTypeException createInvalidTargetObjectTypeException()
   {
      return new InvalidTargetObjectTypeException(new NullPointerException("NullPointerException"), "InvalidTargetObjectTypeException");
   }

   public ModelMBean createModelMBean()
   {
      throw new Error();
   }

   public ModelMBeanAttributeInfo createModelMBeanAttributeInfo()
   {
      String[] names = new String[]{"name", "descriptortype", "value", "iterable", "displayname"};
      Object[] values = new Object[]{"name", "attribute", null, "false", "name"};
      DescriptorSupport descriptor = new DescriptorSupport(names, values);
      return new ModelMBeanAttributeInfo("name", "boolean", "description", true, true, true, descriptor);
   }

   public ModelMBeanConstructorInfo createModelMBeanConstructorInfo()
   {
      String[] names = new String[]{"name", "descriptortype", "role", "displayname"/*, "lastReturnedTimeStamp"*/};
      Object[] values = new Object[]{"name", "operation", "constructor", "name"/*, "0"*/};
      DescriptorSupport descriptor = new DescriptorSupport(names, values);
      return new ModelMBeanConstructorInfo("name", "description", new MBeanParameterInfo[]{createMBeanParameterInfo()}, descriptor);
   }

   public ModelMBeanInfo createModelMBeanInfo()
   {
      throw new Error();
   }

   public ModelMBeanInfoSupport createModelMBeanInfoSupport()
   {
      String[] names = new String[]{"name", "descriptortype", "displayname", "persistpolicy", "log", "export", "visibility"};
      Object[] values = new Object[]{"name", "MBean", "name", "Never", "false", "F", "1"};
      DescriptorSupport descriptor = new DescriptorSupport(names, values);
      return new ModelMBeanInfoSupport("my.class.name",
                                       "description",
                                       new ModelMBeanAttributeInfo[]{createModelMBeanAttributeInfo()},
                                       new ModelMBeanConstructorInfo[]{createModelMBeanConstructorInfo()},
                                       new ModelMBeanOperationInfo[]{createModelMBeanOperationInfo()},
                                       new ModelMBeanNotificationInfo[]{createModelMBeanNotificationInfo()}, descriptor);
   }

   public ModelMBeanNotificationBroadcaster createModelMBeanNotificationBroadcaster()
   {
      throw new Error();
   }

   public ModelMBeanNotificationInfo createModelMBeanNotificationInfo()
   {
      String[] names = new String[]{"name", "descriptortype", "severity", "displayname"/*, "messageId", "log", "logfile"*/};
      Object[] values = new Object[]{"name", "notification", "5", "name"/*, "0", "???", "???"*/};
      DescriptorSupport descriptor = new DescriptorSupport(names, values);
      return new ModelMBeanNotificationInfo(new String[]{"type1", "type2"}, "name", "description", descriptor);
   }

   public ModelMBeanOperationInfo createModelMBeanOperationInfo()
   {
      String[] names = new String[]{"name", "descriptortype", "role", "displayname"/*, "lastReturnedTimeStamp"*/};
      Object[] values = new Object[]{"name", "operation", "operation", "name"/*, "0"*/};
      DescriptorSupport descriptor = new DescriptorSupport(names, values);
      return new ModelMBeanOperationInfo("name", "description", new MBeanParameterInfo[]{createMBeanParameterInfo()}, "java.lang.Object", MBeanOperationInfo.UNKNOWN, descriptor);
   }

   public XMLParseException createXMLParseException()
   {
      return new XMLParseException("XMLParseException");
   }


   //
   // JAVAX.MANAGEMENT.MONITOR
   //

   public MonitorNotification createMonitorNotification() throws Exception
   {
      Constructor ctor = MonitorNotification.class.getDeclaredConstructor(new Class[]{String.class, Object.class, long.class, long.class, String.class, ObjectName.class, String.class, Object.class, Object.class});
      ctor.setAccessible(true);
      MonitorNotification notification = (MonitorNotification)ctor.newInstance(new Object[]{"type", "source", new Long(13), new Long(System.currentTimeMillis()), "message", new ObjectName("domain:p1=v1"), "attribute", "gauge", "trigger"});
      notification.setUserData("user");
      return notification;
   }

   public MonitorSettingException createMonitorSettingException()
   {
      return new MonitorSettingException("MonitorSettingException");
   }


   //
   // JAVAX.MANAGEMENT.OPENMBEAN
   //

   public ArrayType createArrayType() throws OpenDataException
   {
      return new ArrayType(5, SimpleType.BIGDECIMAL);
   }

   public CompositeDataSupport createCompositeDataSupport() throws OpenDataException
   {
      CompositeType ct = createCompositeType();
      return new CompositeDataSupport(ct, new String[]{"key1", "key2"}, new Object[]{"value1", "value2"});
   }

   public CompositeType createCompositeType() throws OpenDataException
   {
      return new CompositeType("composite1", "description1", new String[]{"key1", "key2"}, new String[]{"d1", "d2"}, new OpenType[]{SimpleType.STRING, SimpleType.STRING});
   }

   public InvalidKeyException createInvalidKeyException()
   {
      return new InvalidKeyException("InvalidKeyException");
   }

   public InvalidOpenTypeException createInvalidOpenTypeException()
   {
      return new InvalidOpenTypeException("InvalidOpenTypeException");
   }

   public KeyAlreadyExistsException createKeyAlreadyExistsException()
   {
      return new KeyAlreadyExistsException("KeyAlreadyExistsException");
   }

   public OpenDataException createOpenDataException()
   {
      return new OpenDataException("OpenDataException");
   }

   public OpenMBeanAttributeInfoSupport createOpenMBeanAttributeInfoSupport()
   {
      return new OpenMBeanAttributeInfoSupport("attrib", "An Attribute", SimpleType.STRING, true, true, false);
   }

   public OpenMBeanConstructorInfoSupport createOpenMBeanConstructorInfoSupport()
   {
      return new OpenMBeanConstructorInfoSupport("const", "a constructor", new OpenMBeanParameterInfoSupport[]{createOpenMBeanParameterInfoSupport()});
   }

   public OpenMBeanInfoSupport createOpenMBeanInfoSupport() throws OpenDataException
   {
      OpenMBeanAttributeInfoSupport[] attrInfo = new OpenMBeanAttributeInfoSupport[]{createOpenMBeanAttributeInfoSupport()};
      OpenMBeanConstructorInfoSupport[] ctorInfo = new OpenMBeanConstructorInfoSupport[]{createOpenMBeanConstructorInfoSupport()};
      OpenMBeanOperationInfo[] operInfo = new OpenMBeanOperationInfoSupport[]{createOpenMBeanOperationInfoSupport()};
      MBeanNotificationInfo[] notifInfo = new MBeanNotificationInfo[]
      {
         new MBeanNotificationInfo(new String[]{"notif1", "notif2"}, "name", "description")
      };
      return new OpenMBeanInfoSupport("test.class.Name", "description1", attrInfo, ctorInfo, operInfo, notifInfo);
   }

   public OpenMBeanOperationInfoSupport createOpenMBeanOperationInfoSupport()
   {
      return new OpenMBeanOperationInfoSupport("operation", "A operation", new OpenMBeanParameterInfo[]{createOpenMBeanParameterInfoSupport()}, SimpleType.STRING, MBeanOperationInfo.ACTION);
   }

   public OpenMBeanParameterInfoSupport createOpenMBeanParameterInfoSupport()
   {
      return new OpenMBeanParameterInfoSupport("param1", "A param", SimpleType.STRING);
   }

   public SimpleType createSimpleType()
   {
      return SimpleType.STRING;
   }

   public TabularDataSupport createTabularDataSupport() throws OpenDataException
   {
      return new TabularDataSupport(createTabularType());
   }

   public TabularType createTabularType() throws OpenDataException
   {
      return new TabularType("typename", "descr1", createCompositeType(), new String[]{"key1", "key2"});
   }


   //
   // JAVAX.MANAGEMENT.RELATION
   //

   public InvalidRelationIdException createInvalidRelationIdException()
   {
      return new InvalidRelationIdException("InvalidRelationIdException");
   }

   public InvalidRelationServiceException createInvalidRelationServiceException()
   {
      return new InvalidRelationServiceException("InvalidRelationServiceException");
   }

   public InvalidRelationTypeException createInvalidRelationTypeException()
   {
      return new InvalidRelationTypeException("InvalidRelationTypeException");
   }

   public InvalidRoleInfoException createInvalidRoleInfoException()
   {
      return new InvalidRoleInfoException("InvalidRoleInfoException");
   }

   public InvalidRoleValueException createInvalidRoleValueException()
   {
      return new InvalidRoleValueException("InvalidRoleValueException");
   }

   public MBeanServerNotificationFilter createMBeanServerNotificationFilter() throws MalformedObjectNameException
   {
      MBeanServerNotificationFilter filter = new MBeanServerNotificationFilter();
      filter.enableType("notif-type");
      filter.disableAllObjectNames();
      filter.enableObjectName(new ObjectName("domain:key=val"));
      return filter;
   }

   public Relation createRelation()
   {
      throw new Error();
   }

   public RelationException createRelationException()
   {
      return new RelationException("RelationException");
   }

   public RelationNotFoundException createRelationNotFoundException()
   {
      return new RelationNotFoundException("RelationNotFoundException");
   }

   public RelationNotification createRelationNotification() throws MalformedObjectNameException
   {
      try
      {
         // MX4J version
         RelationNotification n = new RelationNotification(RelationNotification.RELATION_BASIC_UPDATE,
                                                           "source",
                                                           13L,
                                                           System.currentTimeMillis(),
                                                           "message",
                                                           "relation-id",
                                                           "relation-type",
                                                           new ObjectName(":key=value"),
                                                           "role-name",
                                                           createRoleList(),
                                                           createRoleUnresolvedList());
         n.setUserData("user-data");
         return n;
      }
      catch (IllegalArgumentException x)
      {
         // JMXRI version
         RelationNotification n = new RelationNotification(RelationNotification.RELATION_BASIC_UPDATE,
                                                           new RelationService(true),
                                                           13L,
                                                           System.currentTimeMillis(),
                                                           "message",
                                                           "relation-id",
                                                           "relation-type",
                                                           new ObjectName(":key=value"),
                                                           "role-name",
                                                           createRoleList(),
                                                           createRoleUnresolvedList());
         n.setUserData("user-data");
         n.setSource("source");
         return n;
      }
   }

   public RelationServiceNotRegisteredException createRelationServiceNotRegisteredException()
   {
      return new RelationServiceNotRegisteredException("RelationServiceNotRegisteredException");
   }

   public RelationTypeNotFoundException createRelationTypeNotFoundException()
   {
      return new RelationTypeNotFoundException("RelationTypeNotFoundException");
   }

   public RelationType createRelationType()
   {
      throw new Error();
   }

   public RelationTypeSupport createRelationTypeSupport() throws InvalidRelationTypeException, InvalidRoleInfoException, ClassNotFoundException, NotCompliantMBeanException
   {
      return new RelationTypeSupport("relation-type", new RoleInfo[]{createRoleInfo()});
   }

   public Role createRole() throws MalformedObjectNameException
   {
      ArrayList list = new ArrayList();
      list.add(new ObjectName("domain:key=value"));
      return new Role("Role", list);
   }

   public RoleInfo createRoleInfo() throws InvalidRoleInfoException, ClassNotFoundException, NotCompliantMBeanException
   {
      return new RoleInfo("RoleInfo", "javax.management.MBeanServerDelegate", true, true, 13, 17, "Description");
   }

   public RoleInfoNotFoundException createRoleInfoNotFoundException()
   {
      return new RoleInfoNotFoundException("RoleInfoNotFoundException");
   }

   public RoleList createRoleList() throws MalformedObjectNameException
   {
      RoleList list = new RoleList();
      list.add(createRole());
      return list;
   }

   public RoleNotFoundException createRoleNotFoundException()
   {
      return new RoleNotFoundException("RoleNotFoundException");
   }

   public RoleResult createRoleResult() throws MalformedObjectNameException
   {
      return new RoleResult(createRoleList(), createRoleUnresolvedList());
   }

   public RoleUnresolved createRoleUnresolved() throws MalformedObjectNameException
   {
      ArrayList list = new ArrayList();
      list.add(new ObjectName("domain:key=value"));
      return new RoleUnresolved("RoleName", list, RoleStatus.NO_ROLE_WITH_NAME);
   }

   public RoleUnresolvedList createRoleUnresolvedList() throws MalformedObjectNameException
   {
      RoleUnresolvedList list = new RoleUnresolvedList();
      list.add(createRoleUnresolved());
      return list;
   }


   //
   // JAVAX.MANAGEMENT.TIMER
   //

   public TimerNotification createTimerNotification() throws Exception
   {
      // First try MX4J version
      try
      {
         Constructor ctor = TimerNotification.class.getDeclaredConstructor(new Class[]{String.class, Object.class, String.class, Integer.class});
         ctor.setAccessible(true);
         TimerNotification notification = (TimerNotification)ctor.newInstance(new Object[]{"type", "source", "message", new Integer(13)});
         notification.setUserData("user");
         return notification;
      }
      catch (NoSuchMethodException x)
      {
         // Then try JMX RI version
         Constructor ctor = TimerNotification.class.getDeclaredConstructor(new Class[]{String.class, Object.class, long.class, long.class, String.class, Integer.class});
         ctor.setAccessible(true);
         TimerNotification notification = (TimerNotification)ctor.newInstance(new Object[]{"type", "source", new Long(0), new Long(0), "message", new Integer(13)});
         notification.setUserData("user");
         return notification;
      }
   }
}
