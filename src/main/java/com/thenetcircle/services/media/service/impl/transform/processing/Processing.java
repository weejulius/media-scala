package com.thenetcircle.services.media.service.impl.transform.processing;


import processing.core.PApplet;


public final class Processing
{
  private Processing()
  {
  }

  private static final PApplet APPLET=new PApplet();

  public static PApplet get()
  {
    return APPLET;
  }


}
