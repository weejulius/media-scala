package com.thenetcircle.services.media.service.impl.transform.processing;

import processing.core.PGraphics;

public interface TextEffect
{

  void render(PGraphics pg, char[] charArray, int xOfText, int yOfText, int i);

}
