/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.modelmbean.support;

import java.util.ArrayList;
import java.util.List;

import test.MutableInteger;

/**
 * @version $Revision: 1.3 $
 * @see
 */
public class ModelMBeanTarget
{
   private MutableInteger m_counter;
   private String m_content;
   private int m_value;
   private String[] m_array;

   public ModelMBeanTarget(MutableInteger integer)
   {
      m_counter = integer;
   }

   public String getFixedContent()
   {
      m_counter.set(m_counter.get() + 1);
      return "FIXED_CONTENT";
   }

   public String getMutableContent()
   {
      return m_content;
   }

   public void setMutableContent(String content)
   {
      m_content = content;
   }

   public int getMutableContent2()
   {
      return m_value;
   }

   public void setMutableContent2(int value)
   {
      m_value = value;
   }

   public String[] getArrayAttribute()
   {
      return m_array;
   }

   public void setArrayAttribute(String[] array)
   {
      m_array = array;
   }

   public List operation1(char c, short s, float[] f, Object[][] obj)
   {
      m_counter.set(m_counter.get() + 1);
      ArrayList list = new ArrayList();
      Character ch = new Character(c);
      Short sh = new Short(s);
      list.add(ch);
      list.add(sh);
      list.add(f);
      list.add(obj);
      return list;
   }

   public static class TargetBean
   {
      public List operation1(char c, short s, float[] f, Object[][] obj)
      {
         // Add in reverse order
         ArrayList list = new ArrayList();
         Character ch = new Character(c);
         Short sh = new Short(s);
         list.add(obj);
         list.add(f);
         list.add(sh);
         list.add(ch);
         return list;
      }
   }
}
