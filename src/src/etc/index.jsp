<%--
Copyright (C) The MX4J Contributors.
All rights reserved.

This software is distributed under the terms of the MX4J License version 1.0.
See the terms of the MX4J License in the documentation provided with this software.
--%>

<%--
Purpose of this JSP is to provide an example of how to start a SOAPConnectorServer
from within a running servlet container.
The assumption is that Axis has already been deployed in the servlet container by
defining and mapping Axis' servlets in web.xml (see the sample web.xml bundled with
this WAR for details).
--%>
<%@ page import="java.util.*" %>
<%@ page import="javax.management.*" %>
<%@ page import="javax.management.remote.*" %>
<%@ page import="mx4j.tools.remote.http.HTTPConnectorServer" %>
<%
   // The URLPath of this JMXServiceURL must match with the one in web.xml:
   // Axis is mapped to /services/* and the 'jmxconnector' is the name of the
   // remote MBeanServer web service.
   String path = request.getContextPath() + "/services/jmxconnector";
   JMXServiceURL address = new JMXServiceURL("soap", null, request.getServerPort(), path);

   Map environment = new HashMap();
   // Don't start a new web container, since it's already started
   environment.put(HTTPConnectorServer.USE_EXTERNAL_WEB_CONTAINER, Boolean.TRUE);

   JMXConnectorServer cntorServer = JMXConnectorServerFactory.newJMXConnectorServer(address, environment, null);

   MBeanServer server = MBeanServerFactory.newMBeanServer();
   ObjectName cntorServerName = ObjectName.getInstance("connectors:protocol=soap");
   server.registerMBean(cntorServer, cntorServerName);

   cntorServer.start();

   // Register here another ton of MBeans you want to manage via the connector server
   // For example:
   ObjectName timerName = new ObjectName(":type=timer");
   server.registerMBean(new javax.management.timer.Timer(), timerName);

   // Now from a browser, you can view the WSDL:
   // http://localhost:8080/mx4j-soap/services/jmxconnector?wsdl
%>
