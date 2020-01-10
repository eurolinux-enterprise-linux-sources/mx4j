/*
 * Copyright (C) The MX4J Contributors.
 * All rights reserved.
 *
 * This software is distributed under the terms of the MX4J License version 1.0.
 * See the terms of the MX4J License in the documentation provided with this software.
 */

package mx4j.tools.adaptor.http;

import java.util.Locale;

/**
 * Management interface for the XSLTProcessor MBean.
 *
 * @version $Revision: 1.3 $
 */
public interface XSLTProcessorMBean extends ProcessorMBean
{
   /**
    * Sets the jar/zip file or the directory where to find the XSL files
    *
    * @see #getFile
    */
   public void setFile(String file);

   /**
    * Returns the jar/zip file or the directory where XSL files are loaded
    *
    * @see #setFile
    */
   public String getFile();

   /**
    * Returns the path of the XSL templates inside a jar file.
    *
    * @see #setPathInJar
    */
   public String getPathInJar();

   /**
    * Specifies the path of the XSL templates inside a jar file.
    *
    * @see #getPathInJar
    */
   public void setPathInJar(String path);

   /**
    * Returns the default start page
    *
    * @see #setDefaultPage
    */
   public String getDefaultPage();

   /**
    * Sets the default start page, serverbydomain as a default
    *
    * @see #getDefaultPage
    */
   public void setDefaultPage(String defaultPage);

   /**
    * Returns if the XSL files are contained in a jar/zip file.
    *
    * @see #isUsePath
    * @see #setFile
    */
   boolean isUseJar();

   /**
    * Returns if the XSL files are contained in a path.
    *
    * @see #isUseJar
    * @see #setFile
    */
   boolean isUsePath();

   /**
    * Maps a given extension with a specified MIME type
    */
   public void addMimeType(String extension, String type);

   /**
    * Sets the caching of the XSL Templates.
    */
   public void setUseCache(boolean useCache);

   /**
    * Returns if the XSL Templates are cached
    */
   boolean isUseCache();

   /**
    * Returns the Locale used to internationalize the output
    */
   public Locale getLocale();

   /**
    * Sets the locale used to internationalize the output
    */
   public void setLocale(Locale locale);

   /**
    * Sets the locale used to internationalize the output, as a string
    */
   public void setLocaleString(String locale);
}
