package com.thenetcircle.services.common;


import com.thenetcircle.services.common.WatchedLocation.SettingsWatcher;
import com.thenetcircle.services.media.service.impl.Settings;
import org.junit.Test;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.StandardWatchEventKinds;
import java.util.Properties;

import static org.junit.Assert.assertEquals;


public class WatchedPropertiesTest
{

  @Test
  public void loadPropertiesTest() throws IOException, InterruptedException
  {
    Location location=Locations.get("settings.properties");
    new WatchedLocation(location.toString(), StandardWatchEventKinds.ENTRY_CREATE,
      StandardWatchEventKinds.ENTRY_MODIFY).
      watchNow(new SettingsWatcher());
    Thread.sleep(5000);

    Properties properties=new Properties();
    properties.load(location.read());
    properties.setProperty("outputStreamBufferSize", "8192");
    OutputStream out=new FileOutputStream(location.toString());
    properties.store(out, null);
    out.flush();
    out.close();
    Thread.sleep(12000);
    assertEquals("8192", properties.getProperty("outputStreamBufferSize"));
    assertEquals("8192", ""+Settings.outputStreamBufferSize);
    properties.setProperty("outputStreamBufferSize", "4081");
    out=new FileOutputStream(location.toString());
    properties.store(out, null);
    out.flush();
    out.close();
    Thread.sleep(12000);
    assertEquals("4081", ""+Settings.outputStreamBufferSize);

  }
}
