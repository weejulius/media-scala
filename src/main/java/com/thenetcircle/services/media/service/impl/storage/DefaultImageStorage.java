package com.thenetcircle.services.media.service.impl.storage;


import com.thenetcircle.services.common.FileNames;
import com.thenetcircle.services.common.Location;
import com.thenetcircle.services.media.service.impl.Image;
import com.thenetcircle.services.media.service.impl.ImageStorage;
import com.thenetcircle.services.media.service.impl.transform.PImageBufferedImageConverter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import processing.core.PImage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Calendar;


public class DefaultImageStorage implements ImageStorage
{
  private StoreListener listener;
  private static final Logger LOG=LoggerFactory.getLogger(DefaultImageStorage.class);

  @Override
  public Location store(final Image image, final Location location) throws IOException
  {
    final long start=Calendar.getInstance().getTimeInMillis();
    save(location, image);
    final long end=Calendar.getInstance().getTimeInMillis();
    LOG.debug("elapsed time to save image : {}", (end-start));
    return location;
  }

  private void save(final Location location, final Image image)
    throws IOException
  {
    ByteArrayOutputStream bytes;
    final String extension=FileNames.extension(location.toString());
    final BufferedImage bufferedImage= PImageBufferedImageConverter.
      toBufferedImage((PImage) image.get(), extension);


    //FIXME duplicate code
    if(listener==null)
    {
      bytes=new ByteArrayOutputStream();
      ImageIO.write(bufferedImage, extension, bytes);
    }
    else
    {
      try
      {
        bytes=listener.listen(bufferedImage, extension);
      }
      catch(Exception e)
      {
        bytes=new ByteArrayOutputStream();
        ImageIO.write(bufferedImage, extension, bytes);
      }

    }
    location.write(new ByteArrayInputStream(bytes.toByteArray()), bytes.size());
  }

  @Override
  public void addStoreListener(final StoreListener storeListener)
  {
    this.listener=storeListener;
  }
}
