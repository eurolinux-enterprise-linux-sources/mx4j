/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management;

import java.util.List;
import javax.management.MBeanServer;
import javax.management.MBeanServerFactory;

import junit.framework.TestCase;

/**
 * @version $Revision: 1.7 $
 */
public class MBeanServerFactoryTest extends TestCase
{
   public MBeanServerFactoryTest(String s)
   {
      super(s);
   }

   public void testCreate() throws Exception
   {
      MBeanServer server1 = null;
      try
      {
         System.out.print("Creating MBeanServer 1 ... ");
         server1 = MBeanServerFactory.createMBeanServer();
         System.out.println("OK");
         System.out.print("Finding MBeanServer... ");
         List l = MBeanServerFactory.findMBeanServer(null);
         if (l.size() != 1)
         {
            fail("MBeanServer creation is not tracked");
         }
         if (!server1.equals(l.get(0)))
         {
            fail("MBeanServer found is different");
         }
         System.out.println("Found, OK");
      }
      finally
      {
         MBeanServerFactory.releaseMBeanServer(server1);
      }
   }


   public void testNew() throws Exception
   {
      MBeanServer server1 = null;
      try
      {
         System.out.print("Creating MBeanServer 1 ... ");
         server1 = MBeanServerFactory.createMBeanServer();
         System.out.println("OK");

// newMBeanServer must not keep track of the MBeanServer it creates
         System.out.print("Newing MBeanServer 2 ... ");
         MBeanServer server2 = MBeanServerFactory.newMBeanServer();
         System.out.println("OK");
         System.out.print("Finding MBeanServer... ");
         List l = MBeanServerFactory.findMBeanServer(null);
         if (l.size() != 1)
         {
            fail("newMBeanServer tracks creation");
         }
         if (!server1.equals(l.get(0)))
         {
            fail("MBeanServer found is different");
         }
         System.out.println("Found number 1, OK");
      }
      finally
      {
         MBeanServerFactory.releaseMBeanServer(server1);
      }
   }


   public void testCreateWithDomain()
   {
      MBeanServer server1 = null;
      MBeanServer server3 = null;
      try
      {
         System.out.print("Creating MBeanServer 1 ... ");
         server1 = MBeanServerFactory.createMBeanServer();
         System.out.println("OK");

         String domain = "test";

         System.out.print("Creating MBeanServer 3 ... ");
         server3 = MBeanServerFactory.createMBeanServer(domain);
         System.out.println("OK");
         System.out.print("Finding MBeanServer... ");
         List l = MBeanServerFactory.findMBeanServer(null);
         if (l.size() != 2)
         {
            fail("MBeanServer creation is not tracked");
         }
         MBeanServer found1 = (MBeanServer)l.get(0);
         if (!found1.getDefaultDomain().equals(server1.getDefaultDomain()))
         {
            fail("MBeanServer found is different");
         }
         if (!server3.equals(l.get(1)))
         {
            fail("MBeanServer found is different");
         }
         if (!server3.getDefaultDomain().equals(domain))
         {
            fail("Domain is different");
         }
         System.out.println("Found number 1 & 3, OK");
      }
      finally
      {
         MBeanServerFactory.releaseMBeanServer(server1);
         MBeanServerFactory.releaseMBeanServer(server3);
      }
   }


   public void testRelease() throws Exception
   {
      System.out.print("Creating MBeanServer 1 ... ");
      MBeanServer server1 = MBeanServerFactory.createMBeanServer();
      System.out.println("OK");

      System.out.print("Creating MBeanServer 2 ... ");
      String domain = "test";
      MBeanServer server2 = MBeanServerFactory.createMBeanServer(domain);
      System.out.println("OK");

      System.out.print("Releasing MBeanServer 1 ... ");
      List l = MBeanServerFactory.findMBeanServer(null);
      MBeanServer found1 = (MBeanServer)l.get(0);
      MBeanServerFactory.releaseMBeanServer(found1);
      System.out.println("OK");

      System.out.print("Finding MBeanServer... ");
      l = MBeanServerFactory.findMBeanServer(null);
      if (l.size() != 1)
      {
         fail("Removed MBeanServer still present");
      }
      MBeanServer found2 = (MBeanServer)l.get(0);
      if (!found2.getDefaultDomain().equals(domain))
      {
         fail("Removed wrong MBeanServer");
      }
      System.out.println("Found number 2, OK");

      System.out.print("Releasing MBeanServer 2 ... ");
      MBeanServerFactory.releaseMBeanServer(found2);
      System.out.println("OK");

      System.out.print("Finding MBeanServer... ");
      l = MBeanServerFactory.findMBeanServer(null);
      if (l.size() != 0)
      {
         fail("Removed MBeanServer still present");
      }
      System.out.println("Found none, OK");
   }
}
