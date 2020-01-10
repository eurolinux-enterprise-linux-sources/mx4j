/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package javax.management.relation;

import java.io.Serializable;
import java.util.List;

/**
 * @version $Revision: 1.5 $
 */
public interface RelationType extends Serializable
{
   public String getRelationTypeName();

   public RoleInfo getRoleInfo(String roleInfoName) throws RoleInfoNotFoundException;

   public List getRoleInfos();
}