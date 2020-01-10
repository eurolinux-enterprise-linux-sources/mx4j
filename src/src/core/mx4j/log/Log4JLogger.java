/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.log;


import org.apache.log4j.Level;
import org.apache.log4j.Logger;

/**
 * Log that redirects log calls to a Log4J Logger. <p>
 *
 * @version $Revision: 1.7 $
 * @see org.apache.log4j.Logger at <a href="http://jakarta.apache.org/log4j">Apache's Log4J</a>
 */
public class Log4JLogger extends mx4j.log.Logger
{
   private Logger m_logger;

   /**
    * This constructor is called to create instances of this Logger, following the prototype pattern.
    */
   public Log4JLogger()
   {
   }

   protected void setCategory(String category)
   {
      super.setCategory(category);
      m_logger = Logger.getLogger(getCategory());
   }

   protected void log(int priority, Object message, Throwable t)
   {
      // Convert MX4J priority to log4j priority
      Level l = convertPriority(priority);
      m_logger.log(l, message, t);
   }

   /**
    * Converts MX4J priority to Log4J priority
    */
   protected Level convertPriority(int mx4jPriority)
   {
      Level log4jPriority = Level.DEBUG;
      switch (mx4jPriority)
      {
         case mx4j.log.Logger.FATAL:
            log4jPriority = Level.FATAL;
            break;
         case mx4j.log.Logger.ERROR:
            log4jPriority = Level.ERROR;
            break;
         case mx4j.log.Logger.WARN:
            log4jPriority = Level.WARN;
            break;
         case mx4j.log.Logger.INFO:
            log4jPriority = Level.INFO;
            break;
         case mx4j.log.Logger.DEBUG:
            log4jPriority = Level.DEBUG;
            break;
         case mx4j.log.Logger.TRACE:
            log4jPriority = Level.DEBUG;
            break;
         default:
            log4jPriority = Level.INFO;
            break;
      }
      return log4jPriority;
   }
}
