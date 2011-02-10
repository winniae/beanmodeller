package com.coremedia.beanmodeller.maven;

/**
 * A generic Exception if something is wrong in the plugin.
 * <p/>
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 07.02.11
 * Time: 13:00
 */
public class PluginException extends Exception {

  public PluginException() {
  }

  public PluginException(String s) {
    super(s);
  }

  public PluginException(String s, Throwable throwable) {
    super(s, throwable);
  }

  public PluginException(Throwable throwable) {
    super(throwable);
  }
}
