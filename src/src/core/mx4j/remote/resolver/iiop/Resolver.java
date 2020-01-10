/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.remote.resolver.iiop;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIIIOPServerImpl;
import javax.management.remote.rmi.RMIServer;
import javax.management.remote.rmi.RMIServerImpl;
import javax.rmi.CORBA.Stub;
import javax.rmi.PortableRemoteObject;

import org.omg.CORBA.BAD_OPERATION;
import org.omg.CORBA.ORB;

/**
 * @version $Revision: 1.1 $
 */
public class Resolver extends mx4j.remote.resolver.rmi.Resolver
{
   private static final String IOR_CONTEXT = "/ior/";

   private ORB orb;


//********************************************************************************************************************//
// CLIENT METHODS


   protected RMIServer decodeStub(JMXServiceURL url, Map environment) throws IOException
   {
      String path = url.getURLPath();
      String ior = IOR_CONTEXT;
      if (path.startsWith(ior))
      {
         String encoded = path.substring(ior.length());
         ORB orb = getORB(environment);
         Object object = orb.string_to_object(encoded);
         return narrowRMIServerStub(object);
      }
      throw new MalformedURLException("Unsupported binding: " + url);
   }

   protected RMIServer narrowRMIServerStub(Object stub)
   {
      return (RMIServer)PortableRemoteObject.narrow(stub, RMIServer.class);
   }

   public Object bindClient(Object client, Map environment) throws IOException
   {
      Stub stub = (Stub)client;
      ORB orb = null;
      try
      {
         orb = stub._orb();
      }
      catch (BAD_OPERATION x)
      {
         // The stub is not connected to an ORB, go on
      }

      if (orb == null)
      {
         orb = getORB(environment);
         stub.connect(orb);
      }
      return stub;
   }

//********************************************************************************************************************//
// SERVER METHODS


   protected RMIServerImpl createRMIServer(JMXServiceURL url, Map environment) throws IOException
   {
      return new RMIIIOPServerImpl(environment);
   }

   public JMXServiceURL bindServer(Object server, JMXServiceURL url, Map environment) throws IOException
   {
      RMIServerImpl rmiServer = (RMIServerImpl)server;
      Stub stub = (Stub)PortableRemoteObject.toStub(rmiServer);
      stub.connect(getORB(environment));
      return super.bindServer(server, url, environment);
   }

   protected String encodeStub(RMIServerImpl rmiServer, Map environment) throws IOException
   {
      Stub stub = (Stub)bindClient(rmiServer.toStub(), environment);
      String ior = getORB(environment).object_to_string(stub);
      return IOR_CONTEXT + ior;
   }

   /**
    * Creates a new ORB, if not already created.
    * This method is accessed from both client and server.
    */
   private synchronized ORB getORB(Map environment)
   {
      if (orb == null)
      {
         Properties props = new Properties();
         // Using putAll() on a Properties is discouraged, since it expects only Strings
         for (Iterator i = environment.entrySet().iterator(); i.hasNext();)
         {
            Map.Entry entry = (Map.Entry)i.next();
            Object key = entry.getKey();
            Object value = entry.getValue();
            if (key instanceof String && value instanceof String)
            {
               props.setProperty((String)key, (String)value);
            }
         }
         orb = ORB.init((String[])null, props);
      }
      return orb;
   }

   protected boolean isEncodedForm(JMXServiceURL url)
   {
      String path = url.getURLPath();
      if (path != null && path.startsWith(IOR_CONTEXT)) return true;
      return super.isEncodedForm(url);
   }

   public void destroyServer(Object server, JMXServiceURL url, Map environment) throws IOException
   {
      if (!isEncodedForm(url)) return;
      if (orb != null)
      {
         orb.shutdown(true);
         orb.destroy();
      }
   }
}
