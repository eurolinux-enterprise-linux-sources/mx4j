/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */
package test.mx4j.util;

import junit.framework.TestCase;
import mx4j.util.Utils;

/**
 * @version $Revision: 1.1 $
 */
public class WildcardMatchTest extends TestCase
{
   public void testSingleWildcard()
   {
      assertTrue(Utils.wildcardMatch("*", ""));
      assertTrue(Utils.wildcardMatch("*", "A"));
      assertTrue(Utils.wildcardMatch("*", "ABC"));
   }

   public void testTwoWildcards()
   {
      assertFalse(Utils.wildcardMatch("*D*", ""));
      assertTrue(Utils.wildcardMatch("*D*", "aaaaDbbb"));
      assertTrue(Utils.wildcardMatch("*D*", "aaaaD"));
      assertTrue(Utils.wildcardMatch("*D*", "Dbbb"));
      assertFalse(Utils.wildcardMatch("*D*", "aaabbb"));
   }
}
