package com.thenetcircle.services.media.service.impl.transform.processing;


import com.thenetcircle.services.media.service.impl.transform.Transform;
import processing.core.PApplet;
import processing.core.PFont;
import processing.core.PGraphics;
import processing.core.PImage;
import com.thenetcircle.services.media.service.impl.Image;
import com.thenetcircle.services.media.service.impl.Settings;
import com.thenetcircle.services.media.service.impl.area.Area;
import com.thenetcircle.services.media.service.impl.area.Area.AreaBuilder;


/*
 * Used to over text over an image
 */
public class OverlayTextTransform implements Transform
{

  private final String text;
  private final AreaBuilder builder;
  private PFont font=Processing.get().loadFont(Settings.defaultFont);
  private TextEffect textEffect=new StrokenTextEffect();

  public OverlayTextTransform(final AreaBuilder builder, final PFont font, final String text,
    final TextEffect textEffect)
  {
    this.builder=builder;
    this.font=font;
    this.text=text;
    this.textEffect=textEffect;
  }

  public OverlayTextTransform(final AreaBuilder builder, final String text)
  {
    this.builder=builder;
    this.text=text;
  }

  public Image transform(final Image inputImage)
  {
    final PApplet pApplet=Processing.get();
    final PImage inputPImage=(PImage)inputImage.get();

    final Area imageArea=new Area(inputPImage.width, inputPImage.height);
    final int fontSize=fontSize(imageArea);
    final PGraphics drawer=pApplet.createGraphics(
      inputPImage.width, inputPImage.height, PApplet.JAVA2D);
    drawer.beginDraw();

    drawer.background(inputPImage);
    drawer.smooth();

    drawer.textFont(font, fontSize);

    drawer.textSize(fontSize);

    builder.withIn(imageArea);
    builder.sizeBuilder.width(Math.round(drawer.textWidth(text))+2).height(font.getSize()+2);

    final Area textArea=builder.createArea();

    textEffect.render(drawer, text.toCharArray(), textArea.leftTop.x, textArea.leftTop.y, 1);
    drawer.endDraw();
    inputImage.set(drawer);
    return inputImage;
  }

  private int fontSize(final Area area)
  {
    int fontSize=Math.round(
      area.size.height*Settings.referredFontSize/(float)Settings.referredImageHeight);
    if(fontSize<Settings.minFontSize)
    {
      fontSize=Settings.minFontSize;
    }
    return fontSize;
  }

}
