package com.thenetcircle.services.media.service.impl.transform.processing;


import com.thenetcircle.services.media.service.impl.transform.Transform;
import processing.core.PImage;
import com.thenetcircle.services.media.service.impl.Image;
import com.thenetcircle.services.media.service.impl.area.Area;
import com.thenetcircle.services.media.service.impl.area.Area.AreaBuilder;


/**
 * Used to scale the image
 */
public class ScaleTransform implements Transform
{
  private final AreaBuilder bulder;

  public ScaleTransform(final AreaBuilder bulder)
  {
    this.bulder=bulder;
  }

  public Image transform(final Image inputImage)
  {
    final PImage image=(PImage)inputImage.get();

    final Area area=bulder.withIn(new Area(image.width, image.height)).createArea();

    image.resize(area.size.width, area.size.height);

    return inputImage;
  }


}
