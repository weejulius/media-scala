package com.thenetcircle.services.media.service.impl.transform.processing;


import processing.core.PGraphics;


/*
 * Used to make text effect
 */
public class StrokenTextEffect implements TextEffect
{
  public void render(
    final PGraphics pg, final char[] message, final int x, final int y, final int border)
  {
    pg.fill(120, 160);
    pg.text(message, 0, message.length, x, y+1f);
    pg.fill(120, 160);
    pg.text(message, 0, message.length, x+1, y);
    //
    //        pg.text(message, 0, message.length, x+border, y);
    //        pg.text(message, 0, message.length, x+border, y+border);
    //        pg.text(message, 0, message.length, x+border, y-border);
    //        pg.text(message, 0, message.length, x-border, y);
    //        pg.text(message, 0, message.length, x-border, y+border);
    //        pg.text(message, 0, message.length, x-border, y-border);
    //        pg.text(message, 0, message.length, x, y+border);
    pg.fill(250, 255);
    pg.text(message, 0, message.length, x, y);
  }
}
