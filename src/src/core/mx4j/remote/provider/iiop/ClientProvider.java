/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.remote.provider.iiop;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;

import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorProvider;
import javax.management.remote.JMXServiceURL;
import javax.management.remote.rmi.RMIConnector;

/**
 * @version $Revision: 1.4 $
 */
public class ClientProvider implements JMXConnectorProvider
{
   public JMXConnector newJMXConnector(JMXServiceURL url, Map environment) throws IOException
   {
      String protocol = url.getProtocol();
      if (!"iiop".equals(protocol)) throw new MalformedURLException("Wrong protocol " + protocol + " for provider " + this);
      return new RMIConnector(url, environment);
   }
}
