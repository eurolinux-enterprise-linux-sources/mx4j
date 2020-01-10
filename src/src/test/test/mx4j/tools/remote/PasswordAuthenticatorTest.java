/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.mx4j.tools.remote;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.security.Principal;
import java.util.Set;
import javax.security.auth.Subject;

import mx4j.tools.remote.PasswordAuthenticator;
import test.MX4JTestCase;

/**
 * @version $Revision: 1.3 $
 */
public class PasswordAuthenticatorTest extends MX4JTestCase
{
   public PasswordAuthenticatorTest(String s)
   {
      super(s);
   }

   public void testAuthenticationWithNullCredentials() throws Exception
   {
      PasswordAuthenticator authenticator = new PasswordAuthenticator(preparePasswords(new String[0]));
      Object credentials = null;
      try
      {
         authenticator.authenticate(credentials);
         fail();
      }
      catch (SecurityException x)
      {
      }
   }

   public void testAuthenticationWithBadCredentials() throws Exception
   {
      PasswordAuthenticator authenticator = new PasswordAuthenticator(preparePasswords(new String[0]));
      Object credentials = new Object();
      try
      {
         authenticator.authenticate(credentials);
         fail();
      }
      catch (SecurityException x)
      {
      }
   }

   public void testAuthenticationWithCredentialsNull() throws Exception
   {
      PasswordAuthenticator authenticator = new PasswordAuthenticator(preparePasswords(new String[0]));
      Object credentials = new String[2];
      try
      {
         authenticator.authenticate(credentials);
         fail();
      }
      catch (SecurityException x)
      {
      }
   }

   public void testAuthenticationWithUnknwonCredentials() throws Exception
   {
      PasswordAuthenticator authenticator = new PasswordAuthenticator(preparePasswords(new String[]{"user1", "password1"}));
      Object credentials = new String[]{"dummy", null};
      try
      {
         authenticator.authenticate(credentials);
         fail();
      }
      catch (SecurityException x)
      {
      }
   }

   public void testAuthenticationWithWrongCredentials() throws Exception
   {
      String user = "user1";
      PasswordAuthenticator authenticator = new PasswordAuthenticator(preparePasswords(new String[]{user, "password1"}));
      Object credentials = new String[]{user, null};
      try
      {
         authenticator.authenticate(credentials);
         fail();
      }
      catch (SecurityException x)
      {
      }
   }

   public void testAuthenticationPlainSentClear() throws Exception
   {
      String user = "user1";
      String password = "password1";
      PasswordAuthenticator authenticator = new PasswordAuthenticator(preparePasswords(new String[]{user, password}));
      Object credentials = new String[]{user, password};
      // Send the password in clear
      Subject subject = authenticator.authenticate(credentials);
      assertNotNull(subject);
      Set principals = subject.getPrincipals();
      assertEquals(principals.size(), 1);
      Principal principal = (Principal)principals.iterator().next();
      assertEquals(principal.getName(), user);
   }

   public void testAuthenticationPlainSentObfuscated() throws Exception
   {
      String user = "user1";
      String password = "password1";
      PasswordAuthenticator authenticator = new PasswordAuthenticator(preparePasswords(new String[]{user, password}));
      Object credentials = new String[]{user, PasswordAuthenticator.obfuscatePassword(password)};
      // Send the password obfuscated
      Subject subject = authenticator.authenticate(credentials);
      assertNotNull(subject);
      Set principals = subject.getPrincipals();
      assertEquals(principals.size(), 1);
      Principal principal = (Principal)principals.iterator().next();
      assertEquals(principal.getName(), user);
   }

   public void testAuthenticationObfuscatedSentClear() throws Exception
   {
      String user = "user1";
      String password = "password1";
      PasswordAuthenticator authenticator = new PasswordAuthenticator(preparePasswords(new String[]{user, PasswordAuthenticator.obfuscatePassword(password)}));
      Object credentials = new String[]{user, password};
      // Send the password in clear
      Subject subject = authenticator.authenticate(credentials);
      assertNotNull(subject);
      Set principals = subject.getPrincipals();
      assertEquals(principals.size(), 1);
      Principal principal = (Principal)principals.iterator().next();
      assertEquals(principal.getName(), user);
   }

   public void testAuthenticationObfuscatedSentObfuscated() throws Exception
   {
      String user = "user1";
      String password = "password1";
      PasswordAuthenticator authenticator = new PasswordAuthenticator(preparePasswords(new String[]{user, PasswordAuthenticator.obfuscatePassword(password)}));
      Object credentials = new String[]{user, PasswordAuthenticator.obfuscatePassword(password)};
      // Send the password in clear
      Subject subject = authenticator.authenticate(credentials);
      assertNotNull(subject);
      Set principals = subject.getPrincipals();
      assertEquals(principals.size(), 1);
      Principal principal = (Principal)principals.iterator().next();
      assertEquals(principal.getName(), user);
   }

   private InputStream preparePasswords(String[] pairs) throws Exception
   {
      StringWriter sw = new StringWriter();
      BufferedWriter bw = new BufferedWriter(sw);
      for (int i = 0; i < pairs.length; i += 2)
      {
         String user = pairs[i];
         String password = pairs[i + 1];
         bw.write(user);
         bw.write(':');
         bw.write(password);
         bw.newLine();
      }
      bw.close();

      return new BufferedInputStream(new ByteArrayInputStream(sw.toString().getBytes()));
   }
}
