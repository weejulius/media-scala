package com.thenetcircle.services.media.service.impl.transform.processing;


import com.thenetcircle.services.media.service.impl.transform.Transform;
import processing.core.PImage;
import com.thenetcircle.services.media.service.impl.Image;
import com.thenetcircle.services.media.service.impl.area.Area;
import com.thenetcircle.services.media.service.impl.area.Area.AreaBuilder;


/*
 * Used to crop an image
 */
public class CropTransform implements Transform
{
  private final AreaBuilder builder;

  public CropTransform(final AreaBuilder builder)
  {
    this.builder=builder;
  }

  public Image transform(final Image inputImage)
  {
    final PImage image=(PImage)inputImage.get();

    final Area imageArea=new Area(image.width, image.height);

    final Area cropedArea=builder.withIn(imageArea).createArea();
    inputImage.set(image.get(
      cropedArea.leftTop.x,
      cropedArea.leftTop.y,
      cropedArea.size.width,
      cropedArea.size.height));
    return inputImage;
  }
}
