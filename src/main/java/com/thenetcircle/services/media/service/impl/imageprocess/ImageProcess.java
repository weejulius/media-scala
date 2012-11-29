package com.thenetcircle.services.media.service.impl.imageprocess;

import com.thenetcircle.services.common.Locations;
import com.thenetcircle.services.media.service.Media;
import com.thenetcircle.services.media.service.MediaProcess;
import com.thenetcircle.services.media.service.Schema;
import com.thenetcircle.services.media.service.impl.Image;
import com.thenetcircle.services.media.service.impl.ImageStorage;
import com.thenetcircle.services.media.service.impl.storage.DefaultImageStorage;
import com.thenetcircle.services.media.service.impl.storage.StoreListener;
import com.thenetcircle.services.media.service.impl.transform.Transform;

import java.io.IOException;

/**
 * User: julius.yu
 * Date: 11/28/12
 */
public class ImageProcess implements MediaProcess{
  public String destination;
  public Schema schema;
  public StoreListener storeListener;

  //added by fan@thenetcircle.com
  public String userName;
  public String password;

  private final ImageStorage storage = new DefaultImageStorage();

  public void process(final Media originImage) throws IOException {
    Image origin = (Image)originImage;
    for (Transform transform : schema.transforms) {
      origin = transform.transform(origin);
    }
    if (schema.storeListener != null) {
      storeListener = schema.storeListener;
    }
    storage.addStoreListener(storeListener);
    //modified by fan@thenetcircle.com
    //to use the account information from request
    storage.store(origin, Locations.get(destination, userName, password));
  }
}