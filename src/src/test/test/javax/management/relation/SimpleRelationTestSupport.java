/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.relation;

import javax.management.ObjectName;
import javax.management.relation.InvalidRoleValueException;
import javax.management.relation.RelationSupport;
import javax.management.relation.RoleList;

/**
 * @version $Revision: 1.7 $
 */
public class SimpleRelationTestSupport extends RelationSupport
{
   public SimpleRelationTestSupport(String relationId, ObjectName relationServiceName, String relationTypeName,
                                    RoleList roleList) throws InvalidRoleValueException, IllegalArgumentException
   {
      super(relationId, relationServiceName, relationTypeName, roleList);
   }
}
