package com.thenetcircle.services.media.service.impl.transform.processing;


import com.thenetcircle.services.media.service.impl.transform.Transform;
import processing.core.PApplet;
import processing.core.PGraphics;
import processing.core.PImage;
import com.thenetcircle.services.media.service.impl.Image;
import com.thenetcircle.services.media.service.impl.Settings;
import com.thenetcircle.services.media.service.impl.area.Area;
import com.thenetcircle.services.media.service.impl.area.Area.AreaBuilder;
import com.thenetcircle.services.media.service.impl.area.Size;


/*
 * Used to overlay an image on one image, we can define the opacity of the overlay
 */
public class OverlayImageTransform implements Transform
{
  private float opacity=Settings.defaultOpacity;
  private final Image overlayImage;
  private final AreaBuilder builder;
  private static final int MAX_GRAY=255;

  public OverlayImageTransform(final AreaBuilder builder, final Image overlayImage)
  {
    super();
    this.builder=builder;
    this.overlayImage=overlayImage;
  }

  public OverlayImageTransform(final AreaBuilder builder, final Image overlayImage,
    final Float opacity)
  {
    super();
    this.builder=builder;
    this.overlayImage=overlayImage;
    if(opacity!=null)
    {
      this.opacity=opacity;
    }
  }

  public Image transform(final Image mainImage)
  {
    final PImage mainPImage=(PImage)mainImage.get();
    final PImage overlayPImage=(PImage)overlayImage.get();

    final Area mainArea=new Area(new Size(mainPImage.width, mainPImage.height));

    builder.withIn(mainArea).sizeBuilder.width(overlayPImage.width).height(overlayPImage.height);
    final Area overlayArea=builder.createArea();

    final PGraphics drawer=Processing.get().createGraphics(mainPImage.width, mainPImage.height,
      PApplet.JAVA2D);

    drawer.beginDraw();
    drawer.image(mainPImage, 0, 0);
    drawer.tint(MAX_GRAY, opacity*MAX_GRAY);
    drawer.image(overlayPImage, overlayArea.leftTop.x, overlayArea.leftTop.y);
    drawer.endDraw();
    mainImage.set(drawer);
    return mainImage;
  }

}
