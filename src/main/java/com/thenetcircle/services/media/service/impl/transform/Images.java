package com.thenetcircle.services.media.service.impl.transform;


import com.thenetcircle.services.common.Enums;
import com.thenetcircle.services.common.FileNames;
import com.thenetcircle.services.common.Location;
import com.thenetcircle.services.common.Locations;
import com.thenetcircle.services.common.Oops;
import com.thenetcircle.services.media.service.impl.Image;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;


/*
 * Utilized to load image
 */
public final class Images
{
  private static final Logger LOG=LoggerFactory.getLogger(Images.class);
  private static final Map<Location, Image> CACHED_IMAGES=new HashMap<Location, Image>();


  public static enum Extension
  {
    JPEG,
    JPG,
    PNG;
  }

  private Images()
  {

  }

  public static Image load(final Location resource)
  {
    final long start=Calendar.getInstance().getTimeInMillis();
    Image image=null;
    final String extension=FileNames.extension(resource.toString());

    if(!Enums.getEnumByName(Extension.values(), extension))
    {
      throw Oops.causedBy("the image type {} is not supported", extension);
    }

    final InputStream input=resource.read();

    final String name=FileNames.getName(resource.toString());

    try
    {
      final long startRead=Calendar.getInstance().getTimeInMillis();
      final BufferedImage bImage=ImageIO.read(input);
      final long endRead=Calendar.getInstance().getTimeInMillis();
      LOG.debug("elapsed time to read input {} : {}", resource, (endRead-startRead));
      image=asImage(bImage, name);
    }
    catch(Exception e)
    {
      LOG.warn(
        "failed to load image {}, attempt to convert color space to rgb if it is cmyk",
        resource.toString(), e);
      image=CMYKImageReaderFilter.get().filter(resource);
    }

    if(image==null)
    {
      throw Oops.causedBy("the image {} can not be loaded", resource.toString());
    }
    final long end=Calendar.getInstance().getTimeInMillis();
    LOG.debug("elapsed time to load image {} : {}", resource, (end-start));

    return image;
  }
  public static Image load(final String resource)
  {
    return load(Locations.get(resource));
  }

  public static Image asImage(final BufferedImage image, final String name)
  {
    return new Image(PImageBufferedImageConverter.toPImage(image, FileNames.extension(name)), name);
  }

  public static Image loadFromCache(final String resource)
  {
    final Location location=Locations.get(resource);
    Image result=CACHED_IMAGES.get(location);
    if(result==null)
    {
      result=load(location);
      CACHED_IMAGES.put(location, result);
    }
    return result;
  }

}
