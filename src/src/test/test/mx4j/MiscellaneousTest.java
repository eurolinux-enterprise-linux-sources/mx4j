/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j;

import mx4j.util.Utils;
import test.MX4JTestCase;

/**
 * @version $Revision: 1.8 $
 */
public class MiscellaneousTest extends MX4JTestCase
{
   public MiscellaneousTest(String s)
   {
      super(s);
   }

   private boolean wildcardMatch(String pattern, String value) throws Exception
   {
      return Utils.wildcardMatch(pattern, value);
   }

   public void testRegexpMatch() throws Exception
   {
      String p1 = "*";
      if (!wildcardMatch(p1, ""))
      {
         fail();
      }
      if (!wildcardMatch(p1, "a"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "abc"))
      {
         fail();
      }
      System.out.print(".");

      p1 = "?";
      if (!wildcardMatch(p1, "a"))
      {
         fail();
      }
      if (wildcardMatch(p1, ""))
      {
         fail();
      }
      if (wildcardMatch(p1, "aa"))
      {
         fail();
      }
      System.out.print(".");

      p1 = "ab";
      if (!wildcardMatch(p1, "ab"))
      {
         fail();
      }
      if (wildcardMatch(p1, "abcd"))
      {
         fail();
      }
      System.out.print(".");

      p1 = "*?";
      if (!wildcardMatch(p1, "a"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "aa"))
      {
         fail();
      }
      if (wildcardMatch(p1, ""))
      {
         fail();
      }
      System.out.print(".");

      p1 = "?*";
      if (!wildcardMatch(p1, "a"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "aa"))
      {
         fail();
      }
      if (wildcardMatch(p1, ""))
      {
         fail();
      }
      System.out.print(".");

      p1 = "*a";
      if (!wildcardMatch(p1, "a"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "aa"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "ba"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "bca"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "caba"))
      {
         fail();
      }
      if (wildcardMatch(p1, "b"))
      {
         fail();
      }
      if (wildcardMatch(p1, "ab"))
      {
         fail();
      }
      if (wildcardMatch(p1, "cab"))
      {
         fail();
      }
      System.out.print(".");

      p1 = "a*";
      if (!wildcardMatch(p1, "a"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "aa"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "ab"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "abc"))
      {
         fail();
      }
      if (wildcardMatch(p1, "ba"))
      {
         fail();
      }
      if (wildcardMatch(p1, "bca"))
      {
         fail();
      }
      if (wildcardMatch(p1, "bcab"))
      {
         fail();
      }
      System.out.print(".");

      p1 = "?ab";
      if (!wildcardMatch(p1, "aab"))
      {
         fail();
      }
      if (wildcardMatch(p1, "ab"))
      {
         fail();
      }
      if (wildcardMatch(p1, "abc"))
      {
         fail();
      }
      if (wildcardMatch(p1, ""))
      {
         fail();
      }
      System.out.print(".");

      p1 = "ab?";
      if (!wildcardMatch(p1, "aba"))
      {
         fail();
      }
      if (wildcardMatch(p1, "ab"))
      {
         fail();
      }
      if (wildcardMatch(p1, "cab"))
      {
         fail();
      }
      if (wildcardMatch(p1, ""))
      {
         fail();
      }
      System.out.print(".");

      p1 = "a*b";
      if (!wildcardMatch(p1, "aab"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "ab"))
      {
         fail();
      }
      if (wildcardMatch(p1, ""))
      {
         fail();
      }
      if (wildcardMatch(p1, "aaba"))
      {
         fail();
      }
      System.out.print(".");

      p1 = "a?b";
      if (!wildcardMatch(p1, "aab"))
      {
         fail();
      }
      if (wildcardMatch(p1, "ab"))
      {
         fail();
      }
      if (wildcardMatch(p1, ""))
      {
         fail();
      }
      if (wildcardMatch(p1, "aaba"))
      {
         fail();
      }
      System.out.print(".");

      p1 = "*ab*";
      if (!wildcardMatch(p1, "ab"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "abc"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "cabd"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "aabbc"))
      {
         fail();
      }
      if (wildcardMatch(p1, "ba"))
      {
         fail();
      }
      if (wildcardMatch(p1, "bca"))
      {
         fail();
      }
      System.out.print(".");

      p1 = "?ab?";
      if (!wildcardMatch(p1, "cabd"))
      {
         fail();
      }
      if (wildcardMatch(p1, "aab"))
      {
         fail();
      }
      if (wildcardMatch(p1, "ab"))
      {
         fail();
      }
      if (wildcardMatch(p1, "abb"))
      {
         fail();
      }
      if (wildcardMatch(p1, "aabbc"))
      {
         fail();
      }
      System.out.print(".");

      p1 = "*a*b*";
      if (!wildcardMatch(p1, "ab"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "abc"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "cabd"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "caebd"))
      {
         fail();
      }
      if (wildcardMatch(p1, "ba"))
      {
         fail();
      }
      if (wildcardMatch(p1, "bca"))
      {
         fail();
      }
      if (wildcardMatch(p1, "a"))
      {
         fail();
      }
      if (wildcardMatch(p1, ""))
      {
         fail();
      }
      System.out.print(".");

      p1 = "?a?b?";
      if (!wildcardMatch(p1, "aabbc"))
      {
         fail();
      }
      if (wildcardMatch(p1, ""))
      {
         fail();
      }
      if (wildcardMatch(p1, "ab"))
      {
         fail();
      }
      if (wildcardMatch(p1, "aazb"))
      {
         fail();
      }
      System.out.print(".");

      p1 = "*?ab";
      if (!wildcardMatch(p1, "aab"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "aaab"))
      {
         fail();
      }
      if (wildcardMatch(p1, ""))
      {
         fail();
      }
      if (wildcardMatch(p1, "ab"))
      {
         fail();
      }
      System.out.print(".");

      p1 = "?*ab";
      if (!wildcardMatch(p1, "aab"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "aaab"))
      {
         fail();
      }
      if (wildcardMatch(p1, ""))
      {
         fail();
      }
      if (wildcardMatch(p1, "ab"))
      {
         fail();
      }
      System.out.print(".");

      p1 = "ab*?";
      if (!wildcardMatch(p1, "abb"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "abbb"))
      {
         fail();
      }
      if (wildcardMatch(p1, ""))
      {
         fail();
      }
      if (wildcardMatch(p1, "ab"))
      {
         fail();
      }
      System.out.print(".");

      p1 = "ab?*";
      if (!wildcardMatch(p1, "abb"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "abbb"))
      {
         fail();
      }
      if (wildcardMatch(p1, ""))
      {
         fail();
      }
      if (wildcardMatch(p1, "ab"))
      {
         fail();
      }
      System.out.print(".");

      p1 = "a*?b";
      if (!wildcardMatch(p1, "abb"))
      {
         fail();
      }
      if (!wildcardMatch(p1, "abbb"))
      {
         fail();
      }
      if (wildcardMatch(p1, ""))
      {
         fail();
      }
      if (wildcardMatch(p1, "ab"))
      {
         fail();
      }
      System.out.print(".");

      System.out.println();
   }
}
