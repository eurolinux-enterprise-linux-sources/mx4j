/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.server;

import java.lang.reflect.Method;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import java.security.SecureClassLoader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import mx4j.log.Log;
import mx4j.log.Logger;
import org.apache.bcel.Constants;
import org.apache.bcel.generic.ARRAYLENGTH;
import org.apache.bcel.generic.ArrayType;
import org.apache.bcel.generic.BranchInstruction;
import org.apache.bcel.generic.ClassGen;
import org.apache.bcel.generic.InstructionConstants;
import org.apache.bcel.generic.InstructionFactory;
import org.apache.bcel.generic.InstructionHandle;
import org.apache.bcel.generic.InstructionList;
import org.apache.bcel.generic.LocalVariableGen;
import org.apache.bcel.generic.MethodGen;
import org.apache.bcel.generic.ObjectType;
import org.apache.bcel.generic.PUSH;
import org.apache.bcel.generic.ReferenceType;
import org.apache.bcel.generic.Type;

/**
 * MBeanInvoker that generates on-the-fly implementations to call standard MBeans directly, instead of using reflection.
 * <br />
 * It uses the <a href="http://jakarta.apache.org/bcel">BCEL</a> to generate the required bytecode on-the-fly.
 * The generated class is named "mx4j.server.BCELMBeanInvokerGenerated", and it's loaded into the JVM by a different
 * classloader for each MBean. This classloader has the MBean classloader as parent. <br>
 * Below is an example of the generated code; beware that the management interface and all parameter's classes must be
 * public, otherwise an IllegalAccessError is thrown and the invocation falls back to use reflection (but with a significant
 * overhead - throwing an exception is expensive).
 * <pre>
 * public interface ServiceMBean
 * {
 *    public void start();
 *    public Collection getServices(ServiceSelector selector);
 * }
 * <p/>
 * public class BCELMBeanInvokerGenerated extends BCELMBeanInvoker
 * {
 *    protected Object invokeImpl(MBeanMetaData metadata, String method, String[] signature, Object[] args)
 *          throws Throwable
 *    {
 *       if (method.equals("start") && args.length == 0)
 *       {
 *          try
 *          {
 *             ((ServiceMBean)metadata.mbean).start();
 *             return null;
 *          }
 *          catch (ClassCastException x) {}
 *          catch (IllegalAccessError x) {}
 *       }
 *       else if (method.equals("getServices") && args.length == 1)
 *       {
 *          try
 *          {
 *             return ((ServiceMBean)metadata.mbean).getServices((ServiceSelector)args[0]);
 *          }
 *          catch (ClassCastException x) {}
 *          catch (IllegalAccessError x) {}
 *       }
 *       return super.invokeImpl(metadata, method, signature, args);
 *    }
 * }
 * </pre>
 *
 * @version $Revision: 1.14 $
 */
public class BCELMBeanInvoker extends CachingReflectionMBeanInvoker
{
   private static final String LOGGER_CATEGORY = BCELMBeanInvoker.class.getName();
/*
	public static void main(String[] args) throws Exception
	{
		MBeanMetaData metadata = new MBeanMetaData();
		metadata.classloader = ClassLoader.getSystemClassLoader();
		metadata.management = Object.class;
		Object generated = create(metadata);
		System.out.println("generated = " + generated);
	}

   private static void dump(ClassGen classGen)
   {
      try
      {
         java.io.FileOutputStream fos = new java.io.FileOutputStream(new java.io.File("C:\\Simon\\OpenSource\\mx4j\\mx4j\\classes\\core\\mx4j\\server\\BCELMBeanInvokerGenerated.class"));
         classGen.getJavaClass().dump(fos);
         fos.close();
      }
      catch (java.io.IOException x)
      {
         x.printStackTrace();
      }
   }
*/
   protected BCELMBeanInvoker()
   {
   }

   /**
    * Creates a new MBeanInvoker created on-the-fly by using BCEL.
    * It must be synchronized since BCEL is not thread safe, and uses static variables
    * (refer to org.apache.bcel.generic.Type.getArgumentTypes(...) for further details)
    */
   public synchronized static MBeanInvoker create(final MBeanMetaData metadata)
   {
      String parentName = BCELMBeanInvoker.class.getName();
      final String name = parentName + "Generated";
      ClassGen classGen = new ClassGen(name, // Qualified class name
                                       parentName, // Qualified superclass name
                                       "<generated-on-the-fly>", // File name
                                       Constants.ACC_PUBLIC | Constants.ACC_FINAL | Constants.ACC_SUPER, // Modifiers
                                       null); // Implemented interfaces

      classGen.addEmptyConstructor(Constants.ACC_PUBLIC);
      classGen.addMethod(createInvokeImpl(metadata, classGen, name));

      // For debug purposes only
//		dump(classGen);

      final byte[] bytes = classGen.getJavaClass().getBytes();

      try
      {
         // Must shield clients, since creating a new classloader requires a permission
         return (BCELMBeanInvoker)AccessController.doPrivileged(new PrivilegedExceptionAction()
         {
            public Object run() throws Exception
            {
               Class cls = new BCELClassLoader(metadata.getClassLoader(), bytes).loadClass(name);
               return cls.newInstance();
            }
         });
      }
      catch (Throwable x)
      {
         Logger logger = Log.getLogger(LOGGER_CATEGORY);
         if (logger.isEnabledFor(Logger.INFO)) logger.info("Cannot create on-the-fly MBeanInvoker class, going with reflection MBeanInvoker", x);
         return new CachingReflectionMBeanInvoker();
      }
   }

   private static org.apache.bcel.classfile.Method createInvokeImpl(MBeanMetaData metadata, ClassGen classGen, String clsName)
   {
      InstructionList implementation = new InstructionList();

      ObjectType metadataType = new ObjectType(MBeanMetaData.class.getName());
      Type[] signature = new Type[]{metadataType, Type.STRING, new ArrayType(Type.STRING, 1), new ArrayType(Type.OBJECT, 1)};

      // Method definition
      MethodGen mthd = new MethodGen(Constants.ACC_PROTECTED, // Modifiers
                                     Type.OBJECT, // Return type
                                     signature, // Signature
                                     new String[]{"metadata", "method", "params", "args"}, // Parameter names
                                     "invokeImpl", // Method name
                                     clsName, // Class name
                                     implementation, // Implementation
                                     classGen.getConstantPool()); // Pool
      mthd.addException("java.lang.Throwable");

      // Now I should create the implementation
      InstructionFactory factory = new InstructionFactory(classGen);

      Method[] methods = metadata.getMBeanInterface().getMethods();
      List tests = new ArrayList();
      List catches = new ArrayList();
      for (int i = 0; i < methods.length; ++i)
      {
         Method method = methods[i];
         catches.addAll(generateDirectInvokeBranch(classGen, mthd, implementation, factory, metadata.getMBeanInterface().getName(), method, tests));
      }

      // To close the last branch, I must jump to super.invokeImpl(), so I need its first instruction here
      InstructionHandle invokeSuper = implementation.append(factory.createThis());
      for (int i = 0; i < tests.size(); ++i)
      {
         BranchInstruction branch = (BranchInstruction)tests.get(i);
         branch.setTarget(invokeSuper);
      }
      tests.clear();
      for (int i = 0; i < catches.size(); ++i)
      {
         BranchInstruction branch = (BranchInstruction)catches.get(i);
         branch.setTarget(invokeSuper);
      }
      catches.clear();

      //
      // return super.invokeImpl(metadata, method, params, args);
      //
      // Again, it's invokeImpl(super, args) instead of super.invokeImpl(args)
      // Use 'this' as first argument, and invokespecial instead of invokevirtual to call super
      // 'this' is created above, to close the last branch
      implementation.append(factory.createLoad(metadataType, 1));
      implementation.append(factory.createLoad(Type.STRING, 2));
      implementation.append(factory.createLoad(new ArrayType(Type.STRING, 1), 3));
      implementation.append(factory.createLoad(new ArrayType(Type.OBJECT, 1), 4));
      implementation.append(factory.createInvoke(BCELMBeanInvoker.class.getName(), "invokeImpl", Type.OBJECT, signature, Constants.INVOKESPECIAL));
      implementation.append(factory.createReturn(Type.OBJECT));

      mthd.setMaxStack();

      org.apache.bcel.classfile.Method method = mthd.getMethod();

      // Reuse instruction handles
      implementation.dispose();

      return method;
   }

   private static List generateDirectInvokeBranch(ClassGen classGen, MethodGen methodGen, InstructionList implementation, InstructionFactory factory, String management, Method method, List tests)
   {
      ArrayList catches = new ArrayList();

      //
      // if (method.equals("<operation>") && args.length == <num>)
      //
      // The first instruction, will be where I should go if the previous branch fails
      InstructionHandle startTest = implementation.append(factory.createLoad(Type.STRING, 2));

      // The first time it will be empty, the second time will be the first time's uninitialized jump instructions
      for (int i = 0; i < tests.size(); ++i)
      {
         BranchInstruction branch = (BranchInstruction)tests.get(i);
         // Initialize previous jump instruction to jump to the next branch
         branch.setTarget(startTest);
      }
      tests.clear();

      implementation.append(new PUSH(classGen.getConstantPool(), method.getName()));
      implementation.append(factory.createInvoke(String.class.getName(), "equals", Type.BOOLEAN, new Type[]{Type.OBJECT}, Constants.INVOKEVIRTUAL));
      // IFEQ compares the stack with 0, which means "if the previous comparison is false, ..."
      BranchInstruction test1 = factory.createBranchInstruction(Constants.IFEQ, null);
      tests.add(test1);
      implementation.append(test1);

      implementation.append(factory.createLoad(new ArrayType(Type.OBJECT, 1), 4));
      implementation.append(new ARRAYLENGTH());
      implementation.append(new PUSH(classGen.getConstantPool(), method.getParameterTypes().length));
      // Here I should test if args.length == <num>, if not equal then go to the next branch
      // Create branch instructions with no offset, since it cannot be handled now, see above
      BranchInstruction test2 = factory.createBranchInstruction(Constants.IF_ICMPNE, null);
      tests.add(test2);
      implementation.append(test2);

      // Here I am on the right method, unless someone created 2 methods with same names and same number of
      // parameters but of different type. In this last case if we're lucky it's the right method and we go
      // via direct call, otherwise we will have a class cast exception and go via reflection

      // Cast and invoke
      // Put the metadata on the stack, to access its 'mbean' field, that will be put on the stack
      // It's also the start of the try block
      InstructionHandle tryStart = implementation.append(factory.createLoad(new ObjectType(MBeanMetaData.class.getName()), 1));
      implementation.append(factory.createInvoke(MBeanMetaData.class.getName(), "getMBean", Type.OBJECT, new Type[0], Constants.INVOKEVIRTUAL));
      // Cast the 'mbean' field to the proper type, the stack will contain the casted mbean
      implementation.append(factory.createCheckCast(new ObjectType(management)));

      // Now add all the arguments to the stack
      Class[] signature = method.getParameterTypes();
      Type[] invokeSignature = new Type[signature.length];
      for (int i = 0; i < signature.length; ++i)
      {
         Class param = signature[i];

         // Load all args on the stack
         implementation.append(factory.createLoad(new ArrayType(Type.OBJECT, 1), 4));
         // I want index 'i'
         implementation.append(new PUSH(classGen.getConstantPool(), i));
         // Now on the stack there is args[i]
         implementation.append(factory.createArrayLoad(Type.OBJECT));

         // Save the signature for the invocation
         invokeSignature[i] = convertClassToType(param);

         if (param.isPrimitive())
         {
            // On the stack I have the wrapper object, I have to convert them to primitive
            replaceObjectWithPrimitive(param, implementation, factory);
         }
         else if (param.isArray())
         {
            // Cast args[i] to the proper class
            implementation.append(factory.createCheckCast((ReferenceType)invokeSignature[i]));
         }
         else
         {
            // Cast args[i] to the proper class
            implementation.append(factory.createCheckCast((ReferenceType)invokeSignature[i]));
         }
      }

      Class returnClass = method.getReturnType();
      Type returnType = convertClassToType(returnClass);

      // On the stack we now have the casted mbean and all the casted arguments, invoke
      implementation.append(factory.createInvoke(management, method.getName(), returnType, invokeSignature, Constants.INVOKEINTERFACE));

      if (returnClass == Void.TYPE)
      {
         implementation.append(InstructionConstants.ACONST_NULL);
      }
      else if (returnClass.isArray())
      {
         // Thanks to the fact that we can assign any array to Object, we do nothing here
      }
      else if (returnClass.isPrimitive())
      {
         replacePrimitiveWithObject(returnClass, methodGen, implementation, factory);
      }

      InstructionHandle tryEnd = implementation.append(factory.createReturn(Type.OBJECT));

      // In case of class cast exception, eat the exception and call super (hence using reflection)
      // catch (ClassCastException x) {/* do nothing */}
      // On the stack there is the exception, we assign it to local variable 'x'
      ObjectType exceptionTypeCCE = new ObjectType("java.lang.ClassCastException");
      LocalVariableGen x = methodGen.addLocalVariable("x", exceptionTypeCCE, null, null);
      InstructionHandle handler = implementation.append(factory.createStore(exceptionTypeCCE, x.getIndex()));
      x.setStart(handler);
      x.setEnd(handler);
      methodGen.addExceptionHandler(tryStart, tryEnd, handler, exceptionTypeCCE);
      // This catch block is followed by another one, and I don't exit with a throw or a return
      BranchInstruction skip = factory.createBranchInstruction(Constants.GOTO, null);
      catches.add(skip);
      implementation.append(skip);

      // An IllegalAccessError is thrown if the MBean interface or a parameter class is not public
      // We eat it and fall back to call super (hence using reflection)
      // catch (IllegalAccessError x) {/* do nothing */}
      ObjectType errorTypeIAE = new ObjectType("java.lang.IllegalAccessError");
      x = methodGen.addLocalVariable("x", errorTypeIAE, null, null);
      handler = implementation.append(factory.createStore(errorTypeIAE, x.getIndex()));
      x.setStart(handler);
      x.setEnd(handler);
      methodGen.addExceptionHandler(tryStart, tryEnd, handler, errorTypeIAE);
      // This catch block is followed by another one, and I don't exit with a throw or a return,
      skip = factory.createBranchInstruction(Constants.GOTO, null);
      catches.add(skip);
      implementation.append(skip);

      return catches;
   }

   private static Type convertClassToType(Class cls)
   {
      if (cls == void.class) return Type.VOID;
      if (cls == boolean.class) return Type.BOOLEAN;
      if (cls == byte.class) return Type.BYTE;
      if (cls == char.class) return Type.CHAR;
      if (cls == short.class) return Type.SHORT;
      if (cls == int.class) return Type.INT;
      if (cls == long.class) return Type.LONG;
      if (cls == float.class) return Type.FLOAT;
      if (cls == double.class) return Type.DOUBLE;
      if (cls == Object.class) return Type.OBJECT;
      if (cls == String.class) return Type.STRING;
      if (cls.isArray())
      {
         int dimensions = 0;
         Class c = null;
         while ((c = cls.getComponentType()) != null)
         {
            ++dimensions;
            cls = c;
         }
         Type t = convertClassToType(cls);
         return new ArrayType(t, dimensions);
      }
      return new ObjectType(cls.getName());
   }

   private static void replaceObjectWithPrimitive(Class type, InstructionList implementation, InstructionFactory factory)
   {
      // Put as first the most common ones
      if (type == int.class)
      {
         // Cast the operand in the stack and get the value
         implementation.append(factory.createCheckCast(new ObjectType(Integer.class.getName())));
         implementation.append(factory.createInvoke(Integer.class.getName(), "intValue", Type.INT, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
      }
      else if (type == boolean.class)
      {
         // Cast the operand in the stack and get the value
         implementation.append(factory.createCheckCast(new ObjectType(Boolean.class.getName())));
         implementation.append(factory.createInvoke(Boolean.class.getName(), "booleanValue", Type.BOOLEAN, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
      }
      else if (type == long.class)
      {
         // Cast the operand in the stack and get the value
         implementation.append(factory.createCheckCast(new ObjectType(Long.class.getName())));
         implementation.append(factory.createInvoke(Long.class.getName(), "longValue", Type.LONG, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
      }
      else if (type == byte.class)
      {
         // Cast the operand in the stack and get the value
         implementation.append(factory.createCheckCast(new ObjectType(Byte.class.getName())));
         implementation.append(factory.createInvoke(Byte.class.getName(), "byteValue", Type.BYTE, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
      }
      else if (type == char.class)
      {
         // Cast the operand in the stack and get the value
         implementation.append(factory.createCheckCast(new ObjectType(Character.class.getName())));
         implementation.append(factory.createInvoke(Character.class.getName(), "charValue", Type.CHAR, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
      }
      else if (type == short.class)
      {
         // Cast the operand in the stack and get the value
         implementation.append(factory.createCheckCast(new ObjectType(Short.class.getName())));
         implementation.append(factory.createInvoke(Short.class.getName(), "shortValue", Type.SHORT, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
      }
      else if (type == float.class)
      {
         // Cast the operand in the stack and get the value
         implementation.append(factory.createCheckCast(new ObjectType(Float.class.getName())));
         implementation.append(factory.createInvoke(Float.class.getName(), "floatValue", Type.FLOAT, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
      }
      else
      {
         // Cast the operand in the stack and get the value
         implementation.append(factory.createCheckCast(new ObjectType(Double.class.getName())));
         implementation.append(factory.createInvoke(Double.class.getName(), "doubleValue", Type.DOUBLE, Type.NO_ARGS, Constants.INVOKEVIRTUAL));
      }
   }

   private static void replacePrimitiveWithObject(Class type, MethodGen methodGen, InstructionList implementation, InstructionFactory factory)
   {
      // Put as first the most common ones
      if (type == int.class)
      {
         // Create a new instance of the wrapper
         LocalVariableGen i = methodGen.addLocalVariable("i", Type.INT, null, null);
         i.setStart(implementation.append(factory.createStore(Type.INT, i.getIndex())));
         implementation.append(factory.createNew(new ObjectType(Integer.class.getName())));
         implementation.append(InstructionConstants.DUP);
         implementation.append(factory.createLoad(Type.INT, i.getIndex()));
         i.setEnd(implementation.append(factory.createInvoke(Integer.class.getName(), "<init>", Type.VOID, new Type[]{Type.INT}, Constants.INVOKESPECIAL)));
      }
      else if (type == boolean.class)
      {
         // Create a new instance of the wrapper
         LocalVariableGen b = methodGen.addLocalVariable("b", Type.BOOLEAN, null, null);
         b.setStart(implementation.append(factory.createStore(Type.BOOLEAN, b.getIndex())));
         implementation.append(factory.createNew(new ObjectType(Boolean.class.getName())));
         implementation.append(InstructionConstants.DUP);
         implementation.append(factory.createLoad(Type.BOOLEAN, b.getIndex()));
         b.setEnd(implementation.append(factory.createInvoke(Boolean.class.getName(), "<init>", Type.VOID, new Type[]{Type.BOOLEAN}, Constants.INVOKESPECIAL)));
      }
      else if (type == long.class)
      {
         // Create a new instance of the wrapper
         LocalVariableGen l = methodGen.addLocalVariable("l", Type.LONG, null, null);
         l.setStart(implementation.append(factory.createStore(Type.LONG, l.getIndex())));
         implementation.append(factory.createNew(new ObjectType(Long.class.getName())));
         implementation.append(InstructionConstants.DUP);
         implementation.append(factory.createLoad(Type.LONG, l.getIndex()));
         l.setEnd(implementation.append(factory.createInvoke(Long.class.getName(), "<init>", Type.VOID, new Type[]{Type.LONG}, Constants.INVOKESPECIAL)));
      }
      else if (type == byte.class)
      {
         // Create a new instance of the wrapper
         LocalVariableGen b = methodGen.addLocalVariable("b", Type.BYTE, null, null);
         b.setStart(implementation.append(factory.createStore(Type.BYTE, b.getIndex())));
         implementation.append(factory.createNew(new ObjectType(Byte.class.getName())));
         implementation.append(InstructionConstants.DUP);
         implementation.append(factory.createLoad(Type.BYTE, b.getIndex()));
         b.setEnd(implementation.append(factory.createInvoke(Byte.class.getName(), "<init>", Type.VOID, new Type[]{Type.BYTE}, Constants.INVOKESPECIAL)));
      }
      else if (type == char.class)
      {
         // Create a new instance of the wrapper
         LocalVariableGen c = methodGen.addLocalVariable("c", Type.CHAR, null, null);
         c.setStart(implementation.append(factory.createStore(Type.CHAR, c.getIndex())));
         implementation.append(factory.createNew(new ObjectType(Character.class.getName())));
         implementation.append(InstructionConstants.DUP);
         implementation.append(factory.createLoad(Type.CHAR, c.getIndex()));
         c.setEnd(implementation.append(factory.createInvoke(Character.class.getName(), "<init>", Type.VOID, new Type[]{Type.CHAR}, Constants.INVOKESPECIAL)));
      }
      else if (type == short.class)
      {
         // Create a new instance of the wrapper
         LocalVariableGen s = methodGen.addLocalVariable("s", Type.SHORT, null, null);
         s.setStart(implementation.append(factory.createStore(Type.SHORT, s.getIndex())));
         implementation.append(factory.createNew(new ObjectType(Short.class.getName())));
         implementation.append(InstructionConstants.DUP);
         implementation.append(factory.createLoad(Type.SHORT, s.getIndex()));
         s.setEnd(implementation.append(factory.createInvoke(Short.class.getName(), "<init>", Type.VOID, new Type[]{Type.SHORT}, Constants.INVOKESPECIAL)));
      }
      else if (type == float.class)
      {
         // Create a new instance of the wrapper
         LocalVariableGen f = methodGen.addLocalVariable("f", Type.FLOAT, null, null);
         f.setStart(implementation.append(factory.createStore(Type.FLOAT, f.getIndex())));
         implementation.append(factory.createNew(new ObjectType(Float.class.getName())));
         implementation.append(InstructionConstants.DUP);
         implementation.append(factory.createLoad(Type.FLOAT, f.getIndex()));
         f.setEnd(implementation.append(factory.createInvoke(Float.class.getName(), "<init>", Type.VOID, new Type[]{Type.FLOAT}, Constants.INVOKESPECIAL)));
      }
      else
      {
         // Create a new instance of the wrapper
         LocalVariableGen d = methodGen.addLocalVariable("d", Type.DOUBLE, null, null);
         d.setStart(implementation.append(factory.createStore(Type.DOUBLE, d.getIndex())));
         implementation.append(factory.createNew(new ObjectType(Double.class.getName())));
         implementation.append(InstructionConstants.DUP);
         implementation.append(factory.createLoad(Type.DOUBLE, d.getIndex()));
         d.setEnd(implementation.append(factory.createInvoke(Double.class.getName(), "<init>", Type.VOID, new Type[]{Type.DOUBLE}, Constants.INVOKESPECIAL)));
      }
   }

   private Logger getLogger()
   {
      return Log.getLogger(LOGGER_CATEGORY);
   }

   protected Object invokeImpl(MBeanMetaData metadata, String method, String[] signature, Object[] args) throws Throwable
   {
      Logger logger = getLogger();
      if (logger.isEnabledFor(Logger.INFO))
      {
         logger.info("BCEL invocation failed for method " + method + "" + Arrays.asList(signature) + ", using reflection");
      }
      return super.invokeImpl(metadata, method, signature, args);
   }

   private static class BCELClassLoader extends SecureClassLoader
   {
      private byte[] m_bytes;

      private BCELClassLoader(ClassLoader parent, byte[] bytecode)
      {
         super(parent);
         m_bytes = bytecode;
      }

      protected Class findClass(final String name) throws ClassNotFoundException
      {
         try
         {
            return (Class)AccessController.doPrivileged(new PrivilegedExceptionAction()
            {
               public Object run() throws ClassNotFoundException
               {
                  try
                  {
                     return defineClass(name, m_bytes, 0, m_bytes.length, BCELClassLoader.this.getClass().getProtectionDomain());
                  }
                  catch (ClassFormatError x)
                  {
                     throw new ClassNotFoundException("Class Format Error", x);
                  }
               }
            }, null);
         }
         catch (PrivilegedActionException x)
         {
            throw (ClassNotFoundException)x.getException();
         }
      }
   }
}
