/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */


package test.javax.management.relation;

// Java imports

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;
import javax.management.ObjectName;
import javax.management.relation.RelationService;
import javax.management.relation.RelationTypeNotFoundException;
import javax.management.relation.RelationTypeSupport;
import javax.management.relation.Role;
import javax.management.relation.RoleInfo;
import javax.management.relation.RoleList;
import javax.management.relation.RoleResult;
import javax.management.relation.RoleStatus;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.18 $
 */
public class RelationServiceTest extends TestCase
{
   private MBeanServer m_server = null;
   private ObjectName m_relationServiceObjectName = null;
   private RelationService m_relationService;

   // convenience MBean ObjectNames
   ObjectName mbeanObjectName1;
   ObjectName mbeanObjectName2;
   ObjectName mbeanObjectName3;
   ObjectName mbeanObjectName4;
   ObjectName mbeanObjectName5;
   ObjectName mbeanObjectName6;

   public RelationServiceTest(String s)
   {
      super(s);
   }

   protected void setUp()
   {
      m_server = MBeanServerFactory.createMBeanServer();
      // create and register relation service
      try
      {
         m_relationServiceObjectName = new ObjectName("DefaultDomain:type=javax.management.relation.RelationService");
      }
      catch (Exception ex)
      {
         ex.printStackTrace();
      }
   }

   public void tearDown() throws Exception
   {
      m_server.unregisterMBean(m_relationServiceObjectName);
      MBeanServerFactory.releaseMBeanServer(m_server);
   }

   public void testIsActive()
   {
      try
      {
         registerRelationService(true);
         m_relationService.isActive();
      }
      catch (Exception e)
      {
         fail("Relation Service should be active");
      }
   }

   public void testGetFalsePurgeFlag() throws Exception
   {
      registerRelationService(false);
      assertTrue(m_relationService.getPurgeFlag() == false);
   }


   public void testGetTruePurgeFlag() throws Exception
   {
      registerRelationService(true);
      assertTrue(m_relationService.getPurgeFlag());
   }

   public void testCreateRelationType()
   {
      try
      {
         registerRelationService(true);
         RoleInfo[] roleInfos = createRoleInfos("contained", "container");
         m_relationService.createRelationType("relationTypeName1", roleInfos);
         // create one relation type expect 1 returned from call getAllRelationTypeNames
         assertTrue(m_relationService.getAllRelationTypeNames().size() == 1);
         assertEquals("relationTypeName1", m_relationService.getAllRelationTypeNames().get(0));
      }
      catch (Exception e)
      {
         fail("Valid call to createRelationType");
      }
   }

   public void testAddRelationType()
   {
      try
      {
         registerRelationService(true);

         String relationTypeName = "RelationTypeUnitTest";
         m_relationService.addRelationType(new SimpleRelationType(relationTypeName));
         assertTrue(m_relationService.getAllRelationTypeNames().size() == 1);
         assertEquals(relationTypeName, m_relationService.getAllRelationTypeNames().get(0));


      }
      catch (Exception e)
      {
         fail("Valid call to createRelationType");
      }
   }

   public void testGetAllRelationTypeNames() throws Exception
   {
      registerRelationService(true);
      String relationTypeName1 = "TestRelation1";
      String relationTypeName2 = "TestRelation2";
      String relationTypeName3 = "TestRelation3";
      String relationTypeName4 = "TestRelation4";
      m_relationService.addRelationType(new SimpleRelationType(relationTypeName1));
      m_relationService.addRelationType(new SimpleRelationType(relationTypeName2));
      m_relationService.addRelationType(new SimpleRelationType(relationTypeName3));
      m_relationService.addRelationType(new SimpleRelationType(relationTypeName4));

      assertTrue(m_relationService.getAllRelationTypeNames().size() == 4);
   }

   public void testDuplicateRelationTypeNameThrowsException() throws Exception
   {
      registerRelationService(true);
      String relationTypeName1 = "TestRelation1";
      String relationTypeName2 = "TestRelation1";
      try
      {
         m_relationService.addRelationType(new SimpleRelationType(relationTypeName1));
      }
      catch (Exception e)
      {
         fail("first one should be ok as no duplicates");
      }

      try
      {
         m_relationService.addRelationType(new SimpleRelationType(relationTypeName2));
         fail("Should have had an exception");
      }
      catch (Exception expected)
      {
         // should be an exception 2 relationTypeNames the same!!
      }
   }

   public void testGetRoleInfos() throws Exception
   {
      registerRelationService(true);
      registerMBeans();
      m_relationService.createRelationType("aRelationTypeName", createRoleInfos("mother", "child"));

      RoleList roleList = new RoleList();

      createRoleList(mbeanObjectName1, "mother", roleList);
      createRoleList(mbeanObjectName2, "child", roleList);

      m_relationService.createRelation("relationId1", "aRelationTypeName", roleList);
      List l = m_relationService.getRoleInfos("aRelationTypeName");
      assertTrue(l.size() == 2);
   }

   public void testRemoveRelationType() throws Exception
   {
      registerRelationService(true);
      registerMBeans();

      RoleList roleList = new RoleList();

      createRoleList(mbeanObjectName1, "owner", roleList);
      createRoleList(mbeanObjectName2, "car", roleList);

      m_relationService.createRelationType("testValidRelationTypeNameRemoval", createRoleInfos("car", "owner"));
      m_relationService.createRelation("relationID", "testValidRelationTypeNameRemoval", roleList);

      try
      {
         m_relationService.removeRelationType("testValidRelationTypeNameRemoval");
      }
      catch (Exception e)
      {
         fail("No exception expected as relationType registered and valid");
      }

      assertTrue(m_relationService.getAllRelationTypeNames().size() == 0);
   }

   public void testRemoveRelationType_NonExistent() throws Exception
   {
      registerRelationService(true);
      try
      {
         m_relationService.removeRelationType("invalidRelationType_notCreated");
         fail("Expected a RelationTypeNotFoundException to be thrown");
      }
      catch (RelationTypeNotFoundException expected)
      {
         // ok
      }
      catch (Exception e)
      {
         fail("Wrong exception this was not expected");
      }
   }

   public void testAddRelationMBeanNotRegistered() throws Exception
   {
      registerRelationService(true);
      ObjectName mbeanObjectName = new ObjectName("domain:name=testMbeanNotRegistered");
      try
      {
         m_relationService.addRelation(mbeanObjectName);
         fail("MBean not registered should throw an exception");
      }
      catch (InstanceNotFoundException expected)
      {
         //ok exception expected
      }
      catch (Exception e)
      {
         fail("not expected");
      }
   }

   /**
    * Call RelationService.addService passing an object name
    * of a Relation with a set of roles.
    * <p/>
    * Call RelationService.addService passing an object name
    * of another relation with a different set of roles.
    * <p/>
    * Call RelationService.getAllRoles passing the relation id of
    * the second relation added.
    * <p/>
    * You will see that the roles returned are actually those of
    * the first relation added.
    *
    * @throws Exception
    */
   public void testAddRelation_correctRolesReturned() throws Exception
   {
      registerRelationService(true);
      registerMBeans();

      RoleList roleList = new RoleList();
      createRoleList(mbeanObjectName1, "owner", roleList);
      createRoleList(mbeanObjectName2, "car", roleList);

      ObjectName relationSupportObjectName = new ObjectName("relationDomain:name=aRelationSupport");
      SimpleRelationTestSupport relationSupport = new SimpleRelationTestSupport("relationID",
                                                                                m_relationServiceObjectName,
                                                                                "relationTypeName",
                                                                                roleList);

      m_server.registerMBean(relationSupport, relationSupportObjectName);

      /* create the relationType */
      m_relationService.createRelationType("relationTypeName", createRoleInfos("car", "owner"));

      /* now add the relation */
      m_relationService.addRelation(relationSupportObjectName);

      ////////////////////////////////////////////////////////////////////////////////////////////////

      RoleList roleList1 = new RoleList();
      createRoleList(mbeanObjectName3, "parent", roleList1);
      createRoleList(mbeanObjectName4, "child", roleList1);

      ObjectName relationSupportObjectName2 = new ObjectName("relationDomain:name=aRelationSupport2");
      SimpleRelationTestSupport relationSupport2 = new SimpleRelationTestSupport("relationID2",
                                                                                 m_relationServiceObjectName,
                                                                                 "relationTypeName2",
                                                                                 roleList1);

      m_server.registerMBean(relationSupport2, relationSupportObjectName2);

      m_relationService.createRelationType("relationTypeName2", createRoleInfos("parent", "child"));
      m_relationService.addRelation(relationSupportObjectName2);

      ///////////////// validate querying roles for relationId2(added second) returns the correct roles ////////////////////////////////
      RoleResult result1 = m_relationService.getAllRoles("relationID2");
      RoleList actual1 = result1.getRoles();
      // we have to do this as role does not have an equals or hashcode, must check if it is added it will break compatibility!!!!
      final Role role = (Role)roleList1.get(0);
      boolean success = false;
      for (Iterator iterator = actual1.iterator(); iterator.hasNext();)
      {
         Role role1 = (Role)iterator.next();
         String roleName = role1.getRoleName();
         if (roleName.equals(role.getRoleName())) success = true;
      }
      assertTrue(success);

      ///////////////////// validate querying roles for relaionID (added first) returns the expected roles ///////////////////////////////
      RoleResult result = m_relationService.getAllRoles("relationID");
      RoleList actual = result.getRoles();

      final Role role2 = (Role)roleList.get(0);
      boolean success2 = false;
      for (Iterator iterator = actual.iterator(); iterator.hasNext();)
      {
         Role role1 = (Role)iterator.next();
         String roleName = role1.getRoleName();
         if (roleName.equals(role2.getRoleName())) success2 = true;
      }
      assertTrue(success2);

      //assertTrue(roleList.contains(actual.get(0)));
   }

   public void testAddRelation() throws Exception
   {
      registerRelationService(true);
      registerMBeans();

      RoleList roleList = new RoleList();
      createRoleList(mbeanObjectName1, "owner", roleList);
      createRoleList(mbeanObjectName2, "car", roleList);

      ObjectName relationSupportObjectName = new ObjectName("relationDomain:name=aRelationSupport");
      SimpleRelationTestSupport relationSupport = new SimpleRelationTestSupport("relationID",
                                                                                m_relationServiceObjectName,
                                                                                "relationTypeName",
                                                                                roleList);

      m_server.registerMBean(relationSupport, relationSupportObjectName);

      /* create the relationType */
      m_relationService.createRelationType("relationTypeName", createRoleInfos("car", "owner"));
      try
      {
         /* now add the relation */
         m_relationService.addRelation(relationSupportObjectName);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   public void testGetRoleInfo() throws Exception
   {
      registerRelationService(true);
      registerMBeans();

      RoleList roleList = new RoleList();
      createRoleList(mbeanObjectName1, "owner", roleList);
      createRoleList(mbeanObjectName2, "car", roleList);

      ObjectName relationSupportObjectName = new ObjectName("relationDomain:name=aRelationSupport");
      SimpleRelationTestSupport relationSupport = new SimpleRelationTestSupport("relationID",
                                                                                m_relationServiceObjectName,
                                                                                "relationTypeName",
                                                                                roleList);

      m_server.registerMBean(relationSupport, relationSupportObjectName);

      RoleInfo[] roleInfos = createRoleInfos("car", "owner");

      /* create the relationType */
      m_relationService.createRelationType("relationTypeName", roleInfos);

      RoleInfo info = m_relationService.getRoleInfo("relationTypeName", "owner");

      assertEquals(info, roleInfos[1]);
   }

   public void testIsRelationMBean() throws Exception
   {
      String relationID = "relationID";
      try
      {
         ObjectName relationSupportObjectName = new ObjectName("relationDomain:name=aRelationSupport");
         setUpRelationServiceForQueryTesting(relationID, relationSupportObjectName);
         assertTrue(m_relationService.isRelationMBean(relationID).equals(relationSupportObjectName));
      }
      catch (Exception e)
      {
         e.printStackTrace();  //To change body of catch statement use Options | File Templates.
      }
   }

   public void testIsRelation()
   {
      String relationID = "relationID";
      try
      {
         ObjectName relationSupportObjectName = new ObjectName("relationDomain:name=aRelationSupport");
         setUpRelationServiceForQueryTesting(relationID, relationSupportObjectName);
         assertTrue(m_relationService.isRelation(relationSupportObjectName).equals(relationID));
      }
      catch (Exception e)
      {
         e.printStackTrace();  //To change body of catch statement use Options | File Templates.
      }
   }

   public void testHasRelation()
   {
      String relationID = "relationID";
      try
      {
         ObjectName relationSupportObjectName = new ObjectName("relationDomain:name=aRelationSupport");
         setUpRelationServiceForQueryTesting(relationID, relationSupportObjectName);
         assertTrue((m_relationService.hasRelation(relationID)).booleanValue());
      }
      catch (Exception e)
      {
         e.printStackTrace();  //To change body of catch statement use Options | File Templates.
      }
   }

   public void tesetGetAllRelationIds() throws Exception
   {
      registerRelationService(true);
      registerMBeans();

      RoleList roleList = new RoleList();
      createRoleList(mbeanObjectName1, "owner", roleList);
      createRoleList(mbeanObjectName2, "car", roleList);

      /* create the relationType first then creaste the relation*/
      m_relationService.createRelationType("relationTypeName1", createRoleInfos("car", "owner"));
      m_relationService.createRelationType("relationTypeName2", createRoleInfos("car", "owner"));
      m_relationService.createRelationType("relationTypeName3", createRoleInfos("car", "owner"));

      m_relationService.createRelation("relationID1", "relationTypeName1", roleList);
      m_relationService.createRelation("relationID2", "relationTypeName2", roleList);
      m_relationService.createRelation("relationID3", "relationTypeName3", roleList);

      List allIds = m_relationService.getAllRelationIds();

      assertTrue(allIds.size() == 3);
   }

   public void testRoleReading0() throws Exception
   {
      registerRelationService(true);
      registerMBeans();
      String relationTypeName = "relationTypeName";
      m_relationService.addRelationType(new SimpleRelationType(relationTypeName));
      Integer value = m_relationService.checkRoleReading("primary", relationTypeName);
      // role is a ok returns 0
      assertEquals(value.intValue(), 0);
   }

   public void testRoleReading1() throws Exception
   {
      registerRelationService(true);
      registerMBeans();
      String relationTypeName = "relationTypeName";
      m_relationService.addRelationType(new SimpleRelationType(relationTypeName));
      Integer value = m_relationService.checkRoleReading("book", relationTypeName);
      // RoleStatus.NO_ROLE_WITH_NAME = 1
      assertEquals(value.intValue(), RoleStatus.NO_ROLE_WITH_NAME);
   }

   public void testRoleReading2() throws Exception
   {
      registerRelationService(true);
      registerMBeans();
      String relationTypeName = "relationTypeName";
      m_relationService.addRelationType(new TestRelationType(relationTypeName));
      Integer value = m_relationService.checkRoleReading("primary", relationTypeName);
      // RoleStatus.ROLE_NOT_READABLE = 2
      assertEquals(value.intValue(), RoleStatus.ROLE_NOT_READABLE);
   }

   public void testCreateRelation() throws Exception
   {
      registerRelationService(true);
      registerMBeans();
      RoleList roleList = new RoleList();
      createRoleList(mbeanObjectName1, "owner", roleList);
      createRoleList(mbeanObjectName2, "car", roleList);

      /* create the relationType first then creaste the relation*/
      String relationTypeName = "relationTypeName";
      RoleInfo[] roleInfos = createRoleInfos("car", "owner");
      Object[] params1 = {relationTypeName, roleInfos};
      String[] signature1 = {"java.lang.String", roleInfos.getClass().getName()};

      m_server.invoke(m_relationServiceObjectName, "createRelationType", params1, signature1);
      String relationId = "relationId1";

      Object[] params = {relationId, relationTypeName, roleList};
      String[] signature = {"java.lang.String", "java.lang.String", "javax.management.relation.RoleList"};
      m_server.invoke(m_relationServiceObjectName, "createRelation", params, signature);
   }

   private void setUpRelationServiceForQueryTesting(String relationID,
                                                    ObjectName mbeanObjectName) throws Exception
   {
      registerRelationService(true);
      registerMBeans();

      RoleList roleList = new RoleList();
      createRoleList(mbeanObjectName1, "owner", roleList);
      createRoleList(mbeanObjectName2, "car", roleList);

      //ObjectName relationSupportObjectName = new ObjectName("relationDomain:name=aRelationSupport");
      SimpleRelationTestSupport relationSupport = new SimpleRelationTestSupport(relationID,
                                                                                m_relationServiceObjectName,
                                                                                "relationTypeName",
                                                                                roleList);

      m_server.registerMBean(relationSupport, mbeanObjectName);

      /* create the relationType */
      try
      {
         m_relationService.createRelationType("relationTypeName", createRoleInfos("car", "owner"));

         /* now add the relation */
         m_relationService.addRelation(mbeanObjectName);
      }
      catch (Exception e)
      {
         e.printStackTrace();  //To change body of catch statement use Options | File Templates.
      }
   }

   private void registerMBeans()
   {
      try
      {
         // create and build 6 MBeans in the server to act as relations in the relationService
         String mbeanClassName = "test.javax.management.relation.SimpleStandard";
         mbeanObjectName1 = new ObjectName("domain:type=SimpleStandard_1");
         mbeanObjectName2 = new ObjectName("domain:type=SimpleStandard_2");
         mbeanObjectName3 = new ObjectName("domain:type=SimpleStandard_3");
         mbeanObjectName4 = new ObjectName("domain:type=SimpleStandard_4");
         mbeanObjectName5 = new ObjectName("domain:type=SimpleStandard_5");
         mbeanObjectName6 = new ObjectName("domain:type=SimpleStandard_6");

         m_server.createMBean(mbeanClassName, mbeanObjectName1, null);
         m_server.createMBean(mbeanClassName, mbeanObjectName2, null);
         m_server.createMBean(mbeanClassName, mbeanObjectName3, null);
         m_server.createMBean(mbeanClassName, mbeanObjectName4, null);
         m_server.createMBean(mbeanClassName, mbeanObjectName5, null);
         m_server.createMBean(mbeanClassName, mbeanObjectName6, null);
      }
      catch (Exception e)
      {
         e.printStackTrace();
      }
   }

   private void registerRelationService(boolean purge) throws Exception
   {
      m_relationService = new RelationService(purge);
      m_server.registerMBean(m_relationService, m_relationServiceObjectName);
   }

   private RoleInfo[] createRoleInfos(String roleName1,
                                      String roleName2) throws Exception
   {
      RoleInfo[] roleInfos = new RoleInfo[2];
      roleInfos[0] = new RoleInfo(roleName1, "test.javax.management.relation.SimpleStandard", true, true, 1, -1, null);
      roleInfos[1] = new RoleInfo(roleName2, "test.javax.management.relation.SimpleStandard", true, true, 0, -1, null);
      return roleInfos;
   }

   private RoleList createRoleList(ObjectName mbeanObjectName,
                                   String roleName,
                                   RoleList roleList)
   {
      ArrayList roleValue = new ArrayList();
      roleValue.add(mbeanObjectName);
      Role role = new Role(roleName, roleValue);
      roleList.add(role);
      return roleList;
   }

   class TestRelationType extends RelationTypeSupport
   {
      public TestRelationType(String relationTypeName)
      {
         super(relationTypeName);
         try
         {
            RoleInfo primaryRoleInfo = new RoleInfo("primary",
                                                    "test.javax.management.relation.SimpleStandard",
                                                    false, //read
                                                    true, //write
                                                    2,
                                                    2,
                                                    "Primary :)");
            addRoleInfo(primaryRoleInfo);

            RoleInfo secondaryRoleInfo = new RoleInfo("secondary",
                                                      "test.javax.management.relation.SimpleStandard",
                                                      true,
                                                      false,
                                                      2,
                                                      2,
                                                      "Secondary");
            addRoleInfo(secondaryRoleInfo);
         }
         catch (Exception ex)
         {
            throw new RuntimeException(ex.getMessage());
         }
      }
   }

   public void testSimpleRoleListCtor() throws Exception
   {
      registerRelationService(true);
      registerMBeans();
      RoleList rl = new RoleList();
      assertTrue("New RoleList isn't empty", rl.isEmpty());
      rl = new RoleList(42);
      assertTrue("New RoleList(42) isn't empty", rl.isEmpty());
   }

   public void testRoleListCopyCtor() throws Exception
   {
      registerRelationService(true);
      registerMBeans();
      RoleList rl = new RoleList();
      rl.add(new Role("romeo", new ArrayList()));
      rl.add(new Role("juliet", new ArrayList()));
      RoleList rlcopy = new RoleList(rl);
      assertTrue("Bogus copy", rl.equals(rlcopy));

      try
      {
         new RoleList(null);
         fail("Expecting IllegalArgumentException");
      }
      catch (IllegalArgumentException x)
      {
         assertTrue(true);
      }
   }

   public void testUpdateRoleMap() throws Exception
   {
      List roleValue = new ArrayList();
      roleValue.add(new ObjectName("domain:type=SimpleStandard_2"));
      Role role = new Role("myTestRoleName", roleValue);

      String relationID = "myTestRelation";
      setUpRelationServiceForQueryTesting(relationID, new ObjectName("domain:type=testType"));

      m_relationService.updateRoleMap("domain:type=SimpleStandard_1", role, new ArrayList());
      Map result = m_relationService.getReferencedMBeans(relationID);
      assertTrue("The referenced mbeans are not as expected, 2 were added but only " + result.size() + " found", result.size() == 2);
   }

   public void testFindReferencingMBeans() throws Exception
   {
      //basic init...
      registerRelationService(true);
      registerMBeans();

      //this is going to be our referenced bean
      ObjectName targetObjectName = mbeanObjectName1;

      //create constraint
      m_relationService.createRelationType("relationType1", createRoleInfos("roleName1", "roleName2"));

      RoleList roleList = new RoleList();
      ArrayList values = new ArrayList();
      values.add(targetObjectName); //our test target
      values.add(mbeanObjectName2);

      roleList.add(new Role("roleName1", values));

      //create first referencing relation (to meanObjectName1);
      m_relationService.createRelation("relationID1", "relationType1", roleList);

      roleList = new RoleList();
      values = new ArrayList();
      values.add(targetObjectName); //our test target
      values.add(mbeanObjectName3);
      roleList.add(new Role("roleName1", values));
      roleList.add(new Role("roleName2", values));

      //create second referencing relation (to meanObjectName1);
      m_relationService.createRelation("relationID2", "relationType1", roleList);

      Map result = m_relationService.findReferencingRelations(targetObjectName, null, null); //our test target should have 2 references by now....

      //dsamsonoff - under 575066 this would fail - map size would always return 1
      assertTrue("The referencing mbeans are not as expected, 2 were added but only " + result.size() + " found", result.size() == 2);

   }
}
