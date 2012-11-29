package com.thenetcircle.services.media.service.impl;


import static org.junit.Assert.assertEquals;
import java.util.Properties;
import org.junit.Test;


public class SettingsTest
{

  @Test
  public void test()
  {
    Properties properties=new Properties();
    properties.put("callbackRetryInterval", 5000);
    Settings.update(properties);

    assertEquals(5000, Settings.callbackRetryInterval);
  }
}
