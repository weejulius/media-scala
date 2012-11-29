package com.thenetcircle.services.media.service.impl.storage;


import java.awt.image.BufferedImage;
import java.awt.image.IndexColorModel;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Iterator;
import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.stream.ImageOutputStream;
import com.thenetcircle.services.common.Oops;
import com.thenetcircle.services.media.service.impl.storage.StoreListener;


/**
 * reset the qualify of images when saving the jpeg
 * @author julius.yu@thenetcircle.com
 */
public class QualityStoreListener implements StoreListener
{
  private final float quality;
  private static final int W_H_SAMPLE_MODLE=16;

  public QualityStoreListener(final float quality)
  {
    this.quality=quality;
  }

  public ByteArrayOutputStream listen(final BufferedImage image, final String extension)
  {
    final ByteArrayOutputStream os=new ByteArrayOutputStream();
    if(!"jpeg".equalsIgnoreCase(extension)&&!"jpg".equalsIgnoreCase(extension))
    {
      try
      {
        ImageIO.write(image, extension, os);
        return os;
      }
      catch(IOException e)
      {
        throw Oops.causedBy(e, "failed to write image");
      }
    }
    final Iterator<ImageWriter> iter=ImageIO.getImageWritersByFormatName(extension);
    if(iter.hasNext())
    {

      try
      {
        final ImageWriter writer=iter.next();
        final ImageWriteParam iwp=writer.getDefaultWriteParam();
        iwp.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
        iwp.setCompressionQuality(quality);
        iwp.setProgressiveMode(ImageWriteParam.MODE_COPY_FROM_METADATA);

        iwp.setDestinationType(new ImageTypeSpecifier(IndexColorModel
          .getRGBdefault(), IndexColorModel.getRGBdefault()
          .createCompatibleSampleModel(W_H_SAMPLE_MODLE, W_H_SAMPLE_MODLE)));

        final ImageOutputStream imageOutputStream=ImageIO
          .createImageOutputStream(os);
        writer.setOutput(imageOutputStream);
        writer.write(null, new IIOImage(image, null, null), iwp);
        imageOutputStream.close();
        writer.dispose();
      }
      catch(Exception e)
      {
        throw Oops.causedBy(e, "changing quality of image to {}", Float.toString(quality));
      }
      return os;
    }
    throw Oops.causedBy("changing quality of image to {} howerver the writer cannot be retrieved",
      Float.toString(quality));

  }
}
