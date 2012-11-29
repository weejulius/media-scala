package com.thenetcircle.services.media.service.impl.transform;


import com.thenetcircle.services.media.service.impl.Image;

public interface Transform
{
  /*
   * Transform the image and return the processed image
   */
  Image transform(Image inputImage);

}
