package com.thenetcircle.services.media.service.impl;


import com.thenetcircle.services.common.Oops;
import com.thenetcircle.services.media.service.Media;
import processing.core.PImage;


public class Image implements Cloneable,Media
{
  private Object delegator;
  private final String name;

  public Image(final Object image, final String name)
  {
    super();
    if(image==null)
    {
      throw Oops.causedBy("creating an image however the image is empty");
    }
    this.delegator=image;
    this.name=name;
  }

  public String name()
  {
    return name;
  }

  public void set(final Object image)
  {
    this.delegator=image;
  }

  public Object get()
  {
    return delegator;
  }

  public Object clone() throws CloneNotSupportedException
  {
    return ((PImage)delegator).clone();
  }

}
