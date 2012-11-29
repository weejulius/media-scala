package com.thenetcircle.services.media.service.impl.transform;


import com.thenetcircle.services.common.FileNames;
import com.thenetcircle.services.common.Location;
import com.thenetcircle.services.common.Locations;
import com.thenetcircle.services.common.Oops;
import com.thenetcircle.services.media.service.impl.Image;
import com.thenetcircle.services.media.service.impl.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.awt.*;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorConvertOp;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.util.Iterator;


public final class CMYKImageReaderFilter
{
  private static final Logger LOG=LoggerFactory.getLogger(CMYKImageReaderFilter.class);
  private final ColorSpace cmykCS;
  private final ColorSpace rgbCS;
  private final RenderingHints hints;
  private static final CMYKImageReaderFilter ME=new CMYKImageReaderFilter();

  private CMYKImageReaderFilter()
  {
    try
    {
      hints=
        new RenderingHints(
          RenderingHints.KEY_INTERPOLATION,
          RenderingHints.VALUE_INTERPOLATION_BILINEAR);
      hints.put(
        RenderingHints.KEY_RENDERING,
        RenderingHints.VALUE_RENDER_QUALITY);
      hints.put(
        RenderingHints.KEY_ANTIALIASING,
        RenderingHints.VALUE_ANTIALIAS_ON);
      hints.put(
        RenderingHints.KEY_DITHERING,
        RenderingHints.VALUE_DITHER_ENABLE);
      hints.put(
        RenderingHints.KEY_COLOR_RENDERING,
        RenderingHints.VALUE_COLOR_RENDER_QUALITY);
      hints.put(
        RenderingHints.KEY_FRACTIONALMETRICS,
        RenderingHints.VALUE_FRACTIONALMETRICS_ON);
      cmykCS=new ICC_ColorSpace(ICC_Profile.getInstance(
        Locations.get(Settings.defaultICCProfile).toString()));
      rgbCS=ICC_ColorSpace.getInstance(ColorSpace.CS_sRGB);
    }
    catch(Exception e)
    {
      throw Oops.causedBy(e, "failed to initialize the cmyk filter");
    }

  }

  public static CMYKImageReaderFilter get()
  {
    return ME;
  }

  /*
   * convert the cmyk to rgb, however we do not detect the color space here due to bug in java,
   * and assume the color space is cmyk if the num of bands is 4
   * FIXME
   */
  public Image filter(final Location image)
  {
    Image result=null;
    try
    {
      final String extension=FileNames.extension(image.toString());
      final ImageReader reader=createNativeJPEGReader(extension);
      final ImageInputStream inputStream=ImageIO.createImageInputStream(image.read());
      reader.setInput(inputStream);
      //      IIOMetadata metadata=reader.getImageMetadata(0);
      //      String metadataFormat=metadata.getNativeMetadataFormatName();
      //      IIOMetadataNode iioNode=(IIOMetadataNode)metadata.getAsTree(metadataFormat);
      //      NodeList children=iioNode.getElementsByTagName("app14Adobe");
      //      if(children.getLength()>0)
      //      {
      //        iioNode=(IIOMetadataNode)children.item(0);
      //        int transform=Integer.parseInt(iioNode.getAttribute("transform"));
      //      }
      final Raster cmykRaster=reader.readRaster(0, null);
      if(cmykRaster.getNumBands()==4)
      {

        final BufferedImage rgbImage=new BufferedImage(cmykRaster.getWidth(),
          cmykRaster.getHeight(), BufferedImage.TYPE_INT_RGB);
        final WritableRaster resultRaster=rgbImage.getRaster();
        final WritableRaster convertedRaster=Raster.createWritableRaster(
          cmykRaster.getSampleModel(), cmykRaster.getDataBuffer(), null);
        ycckTocmyk(convertedRaster, true);
        final ColorConvertOp cmykToRgb=new ColorConvertOp(cmykCS, rgbCS, hints);
        cmykToRgb.filter(convertedRaster, resultRaster);
        result=Images.asImage(rgbImage, FileNames.getName(image.toString()));
      }
    }
    catch(Exception e)
    {
      LOG.debug("failed to read image in cmyk model", e);
      LOG.info("failed to read image in cmyk model {} due to {}", image, e.getMessage());
    }
    return result;
  }
  public void ycckTocmyk(final WritableRaster rast, final boolean invertedColors)
  {
    final int w=rast.getWidth(), h=rast.getHeight();
    double c, m, y, k;
    double Y, Cb, Cr, K;

    //turn YCCK in Raster to CYMK using formula
    int[] pixels=null;
    for(int row=0; row<h; row++)
    {
      pixels=rast.getPixels(0, row, w, 1, pixels);

      for(int i=0; i<pixels.length; i+=4)
      {
        Y=pixels[i];
        Cb=pixels[i+1];
        Cr=pixels[i+2];
        K=pixels[i+3];

        c=255-(Y+1.402*Cr-179.456);
        m=255-(Y-0.34414*Cb-0.71414*Cr+135.45984);
        y=255-(Y+1.7718d*Cb-226.816);
        k=K;

        //clamp
        c=Math.min(255, Math.max(0, c));
        m=Math.min(255, Math.max(0, m));
        y=Math.min(255, Math.max(0, y));

        if(invertedColors)
        {
          pixels[i]=(byte)(255-c);
          pixels[i+1]=(byte)(255-m);
          pixels[i+2]=(byte)(255-y);
          pixels[i+3]=(byte)(255-k);
        }
        else
        {
          pixels[i]=(byte)c;
          pixels[i+1]=(byte)m;
          pixels[i+2]=(byte)y;
          pixels[i+3]=(byte)k;
        }
      }
      rast.setPixels(0, row, w, 1, pixels);
    }
  }

  private ImageReader createNativeJPEGReader(final String extension)
  {
    ImageReader reader=null;
    for(final Iterator<ImageReader> i=
      ImageIO.getImageReadersByFormatName(extension); i.hasNext();)
    {
      final ImageReader r=i.next();
      if(r.canReadRaster())
      {
        reader=r;
      }
    }

    return reader;
  }

}
