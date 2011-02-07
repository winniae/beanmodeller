package com.coremedia.beanmodeller.maven;

import org.apache.maven.plugin.logging.Log;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * This class simplifies logging for any class that is used as active component in the maven mojo.
 * So any
 * Telekom .COM Relaunch 2011
 * User: marcus
 * Date: 02.02.11
 * Time: 15:15
 */
public class MavenProcessor {

  private Log log = new SystemOutLog();

  public void setLog(Log log) {
    this.log = log;
  }

  public Log getLog() {
    return log;
  }

  /**
   * A simple maven compatible logger logging to System.out to ensure that logging is possible everywhere
   */
  private class SystemOutLog implements Log {

    private void log(String level, CharSequence content, Throwable error) {
      StringBuffer message = new StringBuffer();
      message.append(level);
      message.append(": ");
      if (content != null) {
        message.append(content);
        message.append("\n");
      }
      if (error != null) {
        StringWriter stringWriter = new StringWriter();
        PrintWriter buffer = new PrintWriter(stringWriter);
        error.printStackTrace(buffer);
        buffer.flush();
        message.append(stringWriter.toString());
        buffer.close();
      }
    }

    @Override
    public boolean isDebugEnabled() {
      return true;
    }

    @Override
    public void debug(CharSequence content) {
      debug(content, null);
    }

    @Override
    public void debug(CharSequence content, Throwable error) {
      this.log("DEBUG", content, error);
    }

    @Override
    public void debug(Throwable error) {
      debug(null, error);
    }

    @Override
    public boolean isInfoEnabled() {
      return true;
    }

    @Override
    public void info(CharSequence content) {
      info(content, null);
    }

    @Override
    public void info(CharSequence content, Throwable error) {
      log("INFO", content, error);
    }

    @Override
    public void info(Throwable error) {
      info(null, error);
    }

    @Override
    public boolean isWarnEnabled() {
      return true;
    }

    @Override
    public void warn(CharSequence content) {
      warn(content, null);
    }

    @Override
    public void warn(CharSequence content, Throwable error) {
      log("WARNING", content, error);
    }

    @Override
    public void warn(Throwable error) {
      warn(null, error);
    }

    @Override
    public boolean isErrorEnabled() {
      return true;
    }

    @Override
    public void error(CharSequence content) {
      error(content, null);
    }

    @Override
    public void error(CharSequence content, Throwable error) {
      log("ERROR", content, error);
    }

    @Override
    public void error(Throwable error) {
      error(null, error);
    }
  }
}
