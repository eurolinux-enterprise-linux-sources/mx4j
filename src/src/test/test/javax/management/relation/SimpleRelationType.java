/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.relation;

import javax.management.relation.RelationTypeSupport;
import javax.management.relation.RoleInfo;

/**
 * @version $Revision: 1.5 $
 */
public class SimpleRelationType extends RelationTypeSupport
{
   public SimpleRelationType(String relationTypeName)
   {
      super(relationTypeName);
      try
      {
         RoleInfo primaryRoleInfo = new RoleInfo("primary",
                                                 "test.javax.management.relation.SimpleStandard",
                                                 true, //read
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
