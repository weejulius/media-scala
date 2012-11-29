package com.thenetcircle.services.common;


import com.thenetcircle.services.media.service.impl.Image;
import com.thenetcircle.services.media.service.impl.transform.Images;
import org.junit.Test;
import processing.core.PImage;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class ImageLoadingTest
{

  /*
   * The test relies on the webdav
   */
  @Test
  public void testLoadingJPEG()
  {
    Image image=Images.load("samples/sample_300_300.jpeg");
    assertEquals(300, ((PImage)image.get()).width);
    assertTrue(((PImage)image.get()).width>0);
    image=Images.load("samples/cmyk.jpg");
    image=Images.load("http://localhost:8088/webdav/cmyk.jpg");
    image=Images.load("http://localhost:8088/webdav/big_image.jpeg");
    image=Images.load("samples/big_image.jpeg");

  }

}
