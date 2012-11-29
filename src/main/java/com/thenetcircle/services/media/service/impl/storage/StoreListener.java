package com.thenetcircle.services.media.service.impl.storage;


import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;


/*
 * Used to trigger functionality when storing image, like changing the quality for JPEG
 */
public interface StoreListener
{
  ByteArrayOutputStream listen(BufferedImage image, String extension);
}
