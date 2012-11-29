package com.thenetcircle.services.media.service.impl;


import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;

import com.thenetcircle.services.media.service.impl.transform.PImageBufferedImageConverter;
import org.junit.Test;
import com.thenetcircle.services.common.Locations;


public class BufferedImagePImageConvertorTest
{
  @Test
  public void testToPImage() throws Exception
  {
    BufferedImage img=ImageIO.read(Locations.get("samples/regress_2.jpeg").read());
    PImageBufferedImageConverter.toPImage(img, "jpeg");
  }

}
