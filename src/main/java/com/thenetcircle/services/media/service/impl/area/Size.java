package com.thenetcircle.services.media.service.impl.area;


import com.thenetcircle.services.common.Oops;


/*
 * The size of an area, is represented by height and size
 */
public final class Size
{

  public int width;
  public int height;

  public Size(final int aWidth, final int aHeight)
  {
    if(aWidth<=0||aHeight<=0)
    {
      throw Oops.causedBy(
        "invalid size :({}*{})",
        Integer.toString(aWidth),
        Integer.toString(aHeight));
    }

    width=aWidth;
    height=aHeight;
  }

  public Size increase(final int w, final int h)
  {
    return new Size(width+w, height+h);
  }

  public String toString()
  {
    return "("+width+" * "+height+")";
  }


  public static class SizeBuilder
  {
    private float width=-1;
    private float height=-1;

    public SizeBuilder width(final float aWidth)
    {
      if(aWidth<=0)
      {
        throw Oops.causedBy("invalid width of size {}", Float.toString(aWidth));
      }
      width=aWidth;
      return this;
    }

    public SizeBuilder height(final float aHeight)
    {
      if(aHeight<=0)
      {
        throw Oops.causedBy("invalid height of size {}", Float.toString(aHeight));
      }
      height=aHeight;
      return this;
    }

    private Integer calculateValue(final float num, final int referedNum)
    {
      if(num>=1||num==-1)
      {
        return (int)num;
      }
      else if(num>0&&num<1&&referedNum>0)
      {

        return Math.round(num*referedNum);
      }
      else
      {
        throw Oops.causedBy(
          "the size ({},{}) is invalid",
          Float.toString(width),
          Float.toString(height));
      }
    }

    public Size build(final Area area)
    {
      int areaWidth=0;
      int areaHeight=0;
      float ratio=0;

      if(area!=null)
      {
        areaWidth=area.size.width;
        areaHeight=area.size.height;
        ratio=areaWidth/(float)areaHeight;
      }

      width=calculateValue(width, areaWidth);
      height=calculateValue(height, areaHeight);

      if(isNotIntialized())
      {
        return null;
      }

      if(ratioByHeight())
      {
        width=Math.round(height*ratio);
      }

      if(ratioByWidth())
      {
        height=Math.round(width/ratio);
      }

      if(width<=0||height<=0)
      {
        throw Oops.causedBy(
          "the size ({},{}) is invalid",
          Float.toString(width),
          Float.toString(height));
      }
      return new Size((int)width, (int)height);
    }
    private boolean ratioByHeight()
    {
      return height!=-1&&width==-1;
    }
    private boolean ratioByWidth()
    {
      return height==-1&&width!=-1;
    }
    private boolean isNotIntialized()
    {
      return height==-1&&width==-1;
    }
  }


  public static SizeBuilder builder()
  {
    return new SizeBuilder();
  }


}
