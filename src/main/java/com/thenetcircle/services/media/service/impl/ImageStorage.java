package com.thenetcircle.services.media.service.impl;


import com.thenetcircle.services.common.Location;
import com.thenetcircle.services.media.service.impl.storage.StoreListener;

import java.io.IOException;


public interface ImageStorage
{

  /**
   * Store the image to the location
   * It is able to change the format of image from png to JPEG or from JPEG to png.
   * Issues: if png has alpha channel, it will be lost for JPEG
   *
   * @param image
   * @param location
   * @return
   * @throws IOException
   */
  Location store(Image image, Location location) throws IOException;

  void addStoreListener(StoreListener storeListener);

}
