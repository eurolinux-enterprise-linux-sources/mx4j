/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package test.javax.management.loading;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Iterator;
import javax.management.MBeanRegistration;
import javax.management.MBeanServer;
import javax.management.ObjectInstance;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.loading.MLet;
import javax.management.loading.PrivateMLet;

import mx4j.MX4JSystemKeys;
import mx4j.loading.MLetParseException;
import mx4j.loading.MLetParser;
import mx4j.loading.MLetTag;
import mx4j.server.DefaultClassLoaderRepository;
import test.MX4JTestCase;

/**
 * @version $Revision: 1.19 $
 */
public class MLetTest extends MX4JTestCase
{
   public MLetTest(String s)
   {
      super(s);
   }

   public void testInvalidMLetFileParsing() throws Exception
   {
      MLetParser parser = new MLetParser();

      String content = null;
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

      content = "";
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

      content = " ";
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

      content = " <!--";
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

      content = " -->";
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

      content = "<!-- -->\n<!-- ->";
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

      content = "<!- -->";
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

      // Invalid, missing attributes
      content = "<!-- -->\n<MLET/>";
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

      // Invalid, missing archive attribute
      content = "<MLET CODE = \" test.mx4j.MBeanNormal\"/>";
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

      // Invalid, missing archive attribute
      content = "\n<MLET CODE=\" test.mx4j.MBeanNormal\">\n</MLET>";
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

      // Invalid, missing archive attribute
      content = "\n<MLET OBJECT=\"mx4j-mbean.ser\">\n</MLET>";
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

      // Invalid, missing code or object attribute
      content = "<MLET ARCHIVE = \"..\\lib\"></MLET>";
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

      // Invalid, either code or object attribute must be present
      content = "<MLET CODE=\"test.mx4j.MBeanNormal\" OBJECT=\"mx4j-mbean.ser\" ARCHIVE = \"..\\lib\"></MLET>";
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

      // Invalid arg tag
//      content = "<MLET CODE=\"test.mx4j.MBeanNormal\" ARCHIVE = \"..\\lib\">\n<></MLET>";
//      try {parser.parse(content); fail();}
//      catch (MLetParseException x) {}

      // Invalid arg tag
      content = "<MLET CODE=\"test.mx4j.MBeanNormal\" ARCHIVE = \"..\\lib\">\n<ARG></MLET>";
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

      // Invalid arg tag
      content = "<MLET CODE=\"test.mx4j.MBeanNormal\" ARCHIVE = \"..\\lib\">\n<ARG type=\"int\"></MLET>";
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

      // Invalid arg tag
      content = "<MLET CODE=\"test.mx4j.MBeanNormal\" ARCHIVE = \"..\\lib\">\n<ARG value=\"int\"></MLET>";
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

      // Invalid name tag
      content = "<MLET CODE=\"test.mx4j.MBeanNormal\" ARCHIVE = \"..\\lib\" name>\n</MLET>";
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

      // Invalid name tag
      content = "<MLET CODE=\"test.mx4j.MBeanNormal\" ARCHIVE = \"..\\lib\" name=>\n</MLET>";
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

      // Invalid version tag
      content = "<MLET CODE=\"test.mx4j.MBeanNormal\" ARCHIVE = \"..\\lib\" version>\n</MLET>";
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

      // Invalid version tag
      content = "<MLET CODE=\"test.mx4j.MBeanNormal\" ARCHIVE = \"..\\lib\" version=>\n</MLET>";
      try
      {
         parser.parse(content);
         fail();
      }
      catch (MLetParseException x)
      {
      }

   }

   public void testValidMLetFileParsing() throws Exception
   {
      MLetParser parser = new MLetParser();
      String content = "<MLET CODE=\"test.mx4j.MBeanNormal\" ARCHIVE=\"mx4j-tests.jar\" NAME=\":name=MLetTest1\"/>";
      parser.parse(content);

      content = "<MLET CODE=\"test.mx4j.MBeanNormal\" ARCHIVE=\"mx4j-tests.jar\" NAME=\":name=MLetTest2\">\n\t<ARG TYPE=\"int\" VALUE=\"5\">\n</MLET>\n";
      parser.parse(content);

      content = "<MLET CODE=\"test.mx4j.MBeanNormal\" ARCHIVE=\"mx4j-tests.jar\" CODEBASE=\"dist\\test\\\" NAME=\":name=MLetTest3\">\n\t<ARG TYPE=\"int\" VALUE=\"5\"/>\n</MLET>\n";
      parser.parse(content);
   }

   public void testCompleteMLetFileParsing() throws Exception
   {
      MLetParser parser = new MLetParser();

      StringBuffer content = new StringBuffer();
      content.append("<!-- Comment -->");
      content.append("<MLET \n");
      content.append("      Code=\"mx4j.tools.naming.NamingService\"\n");
      content.append("      archive = \" ../lib \"\n");
      content.append("      CodeBase= \"http://localhost:8080/download\"\n");
      content.append("      NAME=\":name=test\"\n");
      content.append("      Version=\"1\">\n");
      content.append("      <!-- Comment -->");
      content.append("      <ARG \n");
      content.append("           Type=\"boolean\"\n");
      content.append("           VALUE = \"true\">\n");
      content.append("      <!-- Comment -->");
      content.append("      <ARG \n");
      content.append("           TYPE = \"boolean\"\n");
      content.append("           value=\"true\"/>\n");
      content.append("</MLet>");

      parser.parse(content.toString());
   }

   public void testCodebaseForGetMBeansFromURL() throws Exception
   {
      Class cls = Simple.class;
      String className = cls.getName();
      URL url = cls.getProtectionDomain().getCodeSource().getLocation();
      String urlString = url.toExternalForm();
      int index = urlString.lastIndexOf('/') + 1;
      String jar = urlString.substring(index);

      String codebase = ".";
      String content = "<MLET CODE=\"" + className + "\" NAME=\":name=test\" ARCHIVE=\"" + jar + "\" CODEBASE=\"" + codebase + "\"/>";
      MLetParser parser = new MLetParser();
      List tags = parser.parse(content);
      MLetTag tag = (MLetTag)tags.get(0);
      URL mletFileURL = new URL("http://mx4j.sourceforge.net/mlets/mbeans.mlet");
      URL codebaseURL = tag.normalizeCodeBase(mletFileURL);
      assertEquals(codebaseURL.toExternalForm(), "http://mx4j.sourceforge.net/mlets/");

      content = "<MLET CODE=\"" + className + "\" NAME=\":name=test\" ARCHIVE=\"" + jar + "\"/>";
      tags = parser.parse(content);
      tag = (MLetTag)tags.get(0);
      codebaseURL = tag.normalizeCodeBase(mletFileURL);
      assertEquals(codebaseURL.toExternalForm(), "http://mx4j.sourceforge.net/mlets/");

      codebase = "../lib";
      content = "<MLET CODE=\"" + className + "\" NAME=\":name=test\" ARCHIVE=\"" + jar + "\" CODEBASE=\"" + codebase + "\"/>";
      tags = parser.parse(content);
      tag = (MLetTag)tags.get(0);
      codebaseURL = tag.normalizeCodeBase(mletFileURL);
      assertEquals(codebaseURL.toExternalForm(), "http://mx4j.sourceforge.net/lib/");

      codebase = "ftp://mx4j.sourceforge.net/mbeans";
      content = "<MLET CODE=\"" + className + "\" NAME=\":name=test\" ARCHIVE=\"" + jar + "\" CODEBASE=\"" + codebase + "\"/>";
      tags = parser.parse(content);
      tag = (MLetTag)tags.get(0);
      codebaseURL = tag.normalizeCodeBase(mletFileURL);
      assertEquals(codebaseURL.toExternalForm(), codebase + "/");
   }

   public void testGetMBeansFromURL() throws Exception
   {
      Class cls = Simple.class;
      String className = cls.getName();
      URL url = cls.getProtectionDomain().getCodeSource().getLocation();
      String urlString = url.toExternalForm();
      int index = urlString.lastIndexOf('/') + 1;
      String jar = urlString.substring(index);
      String codebase = urlString.substring(0, index);

      // Write an MLet file
      File mletFile = File.createTempFile("mlet", null);
      mletFile.deleteOnExit();
      FileOutputStream fos = new FileOutputStream(mletFile);
      String content = "<MLET CODE=\"" + className + "\" NAME=\":name=test\" ARCHIVE=\"" + jar + "\" CODEBASE=\"" + codebase + "\"/>";
      fos.write(content.getBytes());
      fos.close();

      System.setProperty(MX4JSystemKeys.MX4J_MBEANSERVER_CLASSLOADER_REPOSITORY, CLRWithOnlyMLets.class.getName());
      MBeanServer server = newMBeanServer();

      ObjectName mletName = new ObjectName(":loader=mlet1");

      MLet mlet = new MLet();
      server.registerMBean(mlet, mletName);

      Set mbeans = mlet.getMBeansFromURL(mletFile.toURL());
      if (mbeans.size() != 1) fail("Loaded wrong number of MBeans");
      ObjectInstance instance = (ObjectInstance)mbeans.iterator().next();
      if (!instance.getClassName().equals(className)) fail("Loaded a different MBean");
   }

   public void testGetMBeansFromURLWithNoName() throws Exception
   {
      Class cls = SimpleRegistration.class;
      String className = cls.getName();
      URL url = cls.getProtectionDomain().getCodeSource().getLocation();
      String urlString = url.toExternalForm();
      int index = urlString.lastIndexOf('/') + 1;
      String jar = urlString.substring(index);
      String codebase = urlString.substring(0, index);

      // Write an MLet file
      File mletFile = File.createTempFile("mletnoname", null);
      mletFile.deleteOnExit();
      FileOutputStream fos = new FileOutputStream(mletFile);
      String content = "<MLET CODE=\"" + className + "\" ARCHIVE=\"" + jar + "\" CODEBASE=\"" + codebase + "\"/>";
      fos.write(content.getBytes());
      fos.close();

      System.setProperty(MX4JSystemKeys.MX4J_MBEANSERVER_CLASSLOADER_REPOSITORY, CLRWithOnlyMLets.class.getName());
      MBeanServer server = newMBeanServer();

      ObjectName mletName = new ObjectName(":loader=mlet1");
      MLet mlet = new MLet();
      server.registerMBean(mlet, mletName);

      Set mbeans = mlet.getMBeansFromURL(mletFile.toURL());
      if (mbeans.size() != 1) fail("Loaded wrong number of MBeans");
      ObjectInstance instance = (ObjectInstance)mbeans.iterator().next();
      if (!instance.getClassName().equals(className)) fail("Loaded a different MBean");
   }

   public void testMLetDelegatesToCLR() throws Exception
   {
      mletDelegationToCLR(true);
   }

   public void testMLetDoesNotDelegateToCLR() throws Exception
   {
      try
      {
         mletDelegationToCLR(false);
         fail("MLet does not delegate, cannot load the class");
      }
      catch (ReflectionException ignored)
      {
      }
   }

   public void mletDelegationToCLR(boolean delegates) throws Exception
   {
      System.setProperty(MX4JSystemKeys.MX4J_MBEANSERVER_CLASSLOADER_REPOSITORY, CLRWithOnlyMLets.class.getName());
      MBeanServer server = newMBeanServer();

      ObjectName loaderName = new ObjectName("Loader", "id", "0");
      ObjectName mletName = new ObjectName("Loader", "id", "1");
      ObjectName mbeanName = new ObjectName("MBean", "id", "0");

      Class cls = Simple.class;
      URL url = cls.getProtectionDomain().getCodeSource().getLocation();
      String className = cls.getName();

      MLet loader = new MLet(new URL[]{url}, ClassLoader.getSystemClassLoader().getParent());
      server.registerMBean(loader, loaderName);

      MLet mlet = new MLet(new URL[0], ClassLoader.getSystemClassLoader().getParent(), delegates);

      // Be sure the MLet cannot load the class
      try
      {
         mlet.loadClass(className);
         fail("MLet should not be able to load the class");
      }
      catch (ClassNotFoundException ignored)
      {
      }

      server.registerMBean(mlet, mletName);

      // Try to create the MBean
      server.createMBean(className, mbeanName, mletName);
   }

   public void testSingleMLetLoadClass() throws Exception
   {
      System.setProperty(MX4JSystemKeys.MX4J_MBEANSERVER_CLASSLOADER_REPOSITORY, CLRWithOnlyMLets.class.getName());
      MBeanServer server = newMBeanServer();

      ObjectName loaderName = new ObjectName("Loader", "id", "0");
      ObjectName mbeanName = new ObjectName("MBean", "id", "0");

      Class cls = Simple.class;
      URL url = cls.getProtectionDomain().getCodeSource().getLocation();
      String className = cls.getName();

      MLet mlet = new MLet(new URL[]{url}, ClassLoader.getSystemClassLoader().getParent());
      server.registerMBean(mlet, loaderName);

      server.createMBean(className, mbeanName, loaderName);
   }

   public void testManyMLetLoadClass() throws Exception
   {
      int loaderCount = 100;
      int mbeanCount = 200;

      long elapsed = manyMLetLoadClass(loaderCount, mbeanCount, Simple.class.getName(), false);

      System.out.println("Loading " + mbeanCount + " MBeans with " + loaderCount + " MLets took " + elapsed + " ms, average is " + (elapsed / mbeanCount));
// Assume registering a valid MBean will take no more than 500 ms
      if (elapsed > mbeanCount * 500) fail("Test took too much time, probably a problem in MLet loading");
   }

   public void testMultiMLetLoadNonExistingClass() throws Exception
   {
      int loaderCount = 100;
      int mbeanCount = 200;

      long elapsed = manyMLetLoadClass(loaderCount, mbeanCount, "dummy", true);

      System.out.println("Loading " + mbeanCount + " non-existing MBeans with " + loaderCount + " MLets took " + elapsed + " ms, average is " + (elapsed / mbeanCount));
// We're looking for a non-existing class, so we have to ask to all classloaders in the CLR before the chosen MLet
// The time we spend looking for the class is roughly linear with the number of classloaders in the queried,
// that in average is loaderCount / 2 and at most loaderCount:
// elapsed = mbeanCount * loaderCount * k
// Assume that k > 5 ms is too bad performance
      if (elapsed > mbeanCount * loaderCount * 5) fail("Test took too much time, probably a problem in MLet loading");
   }

   private long manyMLetLoadClass(int loaderCount, int mbeanCount, String className, boolean ignoreExceptionOnCreation) throws Exception
   {
      ObjectName[] loaders = new ObjectName[loaderCount];
      ObjectName[] mbeans = new ObjectName[mbeanCount];

      MBeanServer server = newMBeanServer();

      URL url = getClass().getProtectionDomain().getCodeSource().getLocation();

      // Register some MLet
      for (int i = 0; i < loaderCount; ++i)
      {
         loaders[i] = new ObjectName("Loader", "id", String.valueOf(i));
         MLet mlet = new MLet(new URL[]{url}, ClassLoader.getSystemClassLoader().getParent());
         server.registerMBean(mlet, loaders[i]);
      }

      long start = System.currentTimeMillis();

      Random random = new Random(start);
      for (int i = 0; i < mbeanCount; ++i)
      {
         mbeans[i] = new ObjectName("MBean", "id", String.valueOf(i));

         // Choose an MLet to load the MBean
         int id = random.nextInt(loaderCount);
         ObjectName loader = loaders[id];

         if (ignoreExceptionOnCreation)
         {
            try
            {
               server.createMBean(className, mbeans[i], loader);
            }
            catch (ReflectionException ignored)
            {
            }
         }
         else
         {
            server.createMBean(className, mbeans[i], loader);
         }
      }

      long end = System.currentTimeMillis();

      return end - start;
   }

   public void testChildMLetRegisteredBeforeParentMLet() throws Exception
   {
      // A test to be sure the MLet implementation does not recurse infinitely when loading classes.

      URL url = getClass().getProtectionDomain().getCodeSource().getLocation();
      MLet parent = new MLet(new URL[]{url}, ClassLoader.getSystemClassLoader().getParent());
      MLet child = new MLet(new URL[0], parent);

      ObjectName parentName = new ObjectName("MLet", "type", "parent");
      ObjectName childName = new ObjectName("MLet", "type", "child");

      MBeanServer server = newMBeanServer();

      // First register the child, then the parent.
      server.registerMBean(child, childName);
      server.registerMBean(parent, parentName);

      // Now in the CLR there are: SystemCL, childMLet, parentMLet.
      // If I ask someting to parentMLet that it cannot find, it will delegate to the CLR,
      // and when it comes to the childMLet, the delegation mechanism will ask again to the parentMLet.
      // If we're not smart in the MLet implementation this ends up in an infinite loop.
      try
      {
         parent.loadClass("dummy");
         fail("Class does not exist");
      }
      catch (ClassNotFoundException ignored)
      {
      }
   }

   public void testPrivateMLetNotAddedToCLR() throws Exception
   {
      Class cls = Simple.class;
      URL url = cls.getProtectionDomain().getCodeSource().getLocation();
      PrivateMLet mlet = new PrivateMLet(new URL[]{url}, ClassLoader.getSystemClassLoader().getParent(), true);

      ObjectName mletName = new ObjectName(":MLet=Private");
      ObjectName mbeanName = new ObjectName(":MBean=Simple");

      System.setProperty(MX4JSystemKeys.MX4J_MBEANSERVER_CLASSLOADER_REPOSITORY, CLRWithOnlyMLets.class.getName());
      MBeanServer server = newMBeanServer();

      // The private MLet should not be registered in the CLR
      server.registerMBean(mlet, mletName);

      try
      {
         // Ask the CLR to load the class
         server.createMBean(cls.getName(), mbeanName);
         fail("Class cannot be found by the CLR");
      }
      catch (ReflectionException ignored)
      {
      }
   }

   public void testFindLibrary() throws Exception
   {
      MLet mlet = new MLet();
      Method method = mlet.getClass().getDeclaredMethod("findLibrary", new Class[]{String.class});
      method.setAccessible(true);
      String result = (String)method.invoke(mlet, new Object[]{"stat"});
      if (result != null) fail("MLet can load non-existing libraries");
   }

   public void testFindLibraryWithLibraryDirectoryInClassPath() throws Exception
   {
      // Prepare the library
      String library = "mlet";
      String libraryName = System.mapLibraryName(library);
      File libraryFile = new File(libraryName);
      FileOutputStream fos = new FileOutputStream(libraryFile);
      fos.write("library".getBytes());
      fos.close();

      assertTrue(libraryFile.exists());
      assertTrue(libraryFile.length() > 0);

      MLet mlet = new MLet(new URL[]{libraryFile.getCanonicalFile().getParentFile().toURL()});
      Method method = mlet.getClass().getDeclaredMethod("findLibrary", new Class[]{String.class});
      method.setAccessible(true);
      String result = (String)method.invoke(mlet, new Object[]{library});

      assertNotNull(result);
      assertTrue(libraryFile.exists());
      assertTrue(libraryFile.length() > 0);
   }

   public void testFindLibraryWithLibraryDirectoryNotInClassPath() throws Exception
   {
      // Prepare the library
      String library = "mlet";
      String libraryName = System.mapLibraryName(library);
      File libraryFile = new File(libraryName);
      FileOutputStream fos = new FileOutputStream(libraryFile);
      fos.write("library".getBytes());
      fos.close();

      assertTrue(libraryFile.exists());
      assertTrue(libraryFile.length() > 0);

      MLet mlet = new MLet(new URL[]{libraryFile.getCanonicalFile().getParentFile().toURL()});

      // Set the library directory to some temp directory
      File temp = File.createTempFile("abc", null);
      temp.deleteOnExit();
      mlet.setLibraryDirectory(temp.getCanonicalFile().getParentFile().getCanonicalPath());

      Method method = mlet.getClass().getDeclaredMethod("findLibrary", new Class[]{String.class});
      method.setAccessible(true);
      String result = (String)method.invoke(mlet, new Object[]{library});

      assertNotNull(result);
      assertTrue(libraryFile.exists());
      assertTrue(libraryFile.length() > 0);

      File tempLibrary = new File(mlet.getLibraryDirectory(), libraryName);
      assertTrue(tempLibrary.exists());
      assertTrue(tempLibrary.length() > 0);
   }

   public void testDefaultMletName() throws Exception {
      MBeanServer mbServer = newMBeanServer();
      MLet mlet = new MLet();
      ObjectName mletName = new ObjectName(mbServer.getDefaultDomain(), "type", "MLet");
      assertFalse(mbServer.isRegistered(mletName));
      assertEquals(mletName, mbServer.registerMBean(mlet, null).getObjectName());
      assertTrue(mbServer.isRegistered(mletName));
   }

   public interface SimpleMBean
   {
   }

   public static class Simple implements SimpleMBean
   {
   }

   public static class SimpleRegistration extends Simple implements MBeanRegistration
   {
      public ObjectName preRegister(MBeanServer server, ObjectName name) throws Exception
      {
         return ObjectName.getInstance(":name=simple");
      }

      public void postRegister(Boolean registrationDone)
      {
      }

      public void preDeregister() throws Exception
      {
      }

      public void postDeregister()
      {
      }
   }

   public static class CLRWithOnlyMLets extends DefaultClassLoaderRepository
   {
      protected void addClassLoader(ClassLoader cl)
      {
         if (cl == null) return;
         if (cl.getClass() == MLet.class || cl.getClass() == PrivateMLet.class) super.addClassLoader(cl);
      }
   }
}
