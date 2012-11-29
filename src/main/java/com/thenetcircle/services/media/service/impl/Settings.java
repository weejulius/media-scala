package com.thenetcircle.services.media.service.impl;


import com.thenetcircle.services.common.Oops;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.Map.Entry;
import java.util.Properties;


public final class Settings {
  private Settings() {
  }

  private static final Logger LOG = LoggerFactory.getLogger(Settings.class);

  /**
   * the number of bytes for the buffer when writing output stream
   */
  public static int outputStreamBufferSize = 1024 * 4;

  /*
   * The user name of web dav account
   */
  public static String webDavUserName = "julius.yu";

  /*
   * The password of web dav account
   */
  public static String webDavPassword = "julius";

  /*
   * the time out of http connection
   */
  public static int connectionTimeOut = 3000;


  /*
   * the max number of requests to dispatch at the same time
   */
  public static int maxRequestsInProcess = 15;

  /*
   * the number of threads in the pool for image dispatch
   */
  public static int threadNumOfPool = 6;

  /*
   * the time out (second unit) for each image request dispatch
   */
  public static long maxSecondPerProcess = 30;

  /*
   * the default font to render over laid text
   */
  public static String defaultFont = "Copperplate-Bold-20.vlw";

  /*
   * the max duration to retry for each failed call back,the unit is mills
   */
  public static long maxRetryDuration = 2*24*60*60*1000;

  /*
   * the default ICC profile
   */
  public static String defaultICCProfile = "CoatedFOGRA39.icc";

  /*
   * the default opacity for overlaid image
   */
  public static float defaultOpacity = 0.7f;

  /*
   * the min size of font to render overlaid text
   */
  public static int minFontSize = 16;

  /*
   * the referred font size to adjust the size of font
   */
  public static int referredFontSize = 18;

  /*
   * the referred image height, for example the referred font size is 16 given the referred image
   * height is 800, the font size will be adjusted referring to referredFontSize/referredImageHeight
   */
  public static int referredImageHeight = 800;

  /**
   * the callback will be retried after interval(mills)
   */
  public static int callbackRetryInterval = 1000;

  /**
   * the interval of retry is not be fixed and  increased cursive based on callbackRetryInterval
   */
  public static boolean isRetryIntervalCursive=true;



  public static void update(final Properties properties) {

    for (Entry<Object, Object> property : properties.entrySet()) {
      Field field;
      try {
        field = Settings.class.getField(property.getKey().toString());
      } catch (NoSuchFieldException | SecurityException e) {
        throw Oops.causedBy("the field {} is not existing ", property.getKey().toString());
      }
      final String type = field.getType().getSimpleName();
      final String name = field.getName();
      field.setAccessible(true);
      try {
        switch (type) {
          case "int":

            field.setInt(null, Integer.parseInt(properties.get(name).toString()));

            break;
          case "float":
            field.setFloat(null, Float.parseFloat(properties.get(name).toString()));
            break;
          case "long":
            field.setLong(null, Long.parseLong(properties.get(name).toString()));
            break;
          case "String":
            field.set(null, properties.get(name).toString());
            break;
          default:
            throw Oops.causedBy("unsupported field type", property.toString());
        }
      } catch (Exception e) {
        LOG.warn("failed to set value for field " + name, e);
      }
    }
  }


}
