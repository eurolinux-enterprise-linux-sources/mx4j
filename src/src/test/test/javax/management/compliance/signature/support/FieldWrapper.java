/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.compliance.signature.support;

import java.lang.reflect.Field;

/**
 * @version $Revision: 1.3 $
 */
public class FieldWrapper extends MemberWrapper
{
   public FieldWrapper(Field field)
   {
      modifiers = field.getModifiers();
      type = field.getType().getName();
      name = field.getName();
   }
}
