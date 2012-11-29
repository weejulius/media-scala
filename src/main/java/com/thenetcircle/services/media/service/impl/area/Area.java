package com.thenetcircle.services.media.service.impl.area;


import com.thenetcircle.services.media.service.impl.area.Padding.PaddingBuilder;
import com.thenetcircle.services.media.service.impl.area.Size.SizeBuilder;


/*
 * Used to create an area or create an internal area based on an area
 */
public class Area
{
  public Point leftTop;
  public Point rightBottom;
  public Size size;

  public Area(final Point topLeft, final Point bottomRight)
  {
    this.leftTop=topLeft;
    this.rightBottom=bottomRight;
    size=bottomRight.minus(topLeft);
  }

  public Area(final int width, final int height)
  {
    this(new Point(0, 0), new Size(width, height));
  }

  public Area(final Size size)
  {
    this(new Point(0, 0), size);
  }

  public Area(final Point topLeft, final Size size)
  {
    this.leftTop=topLeft;
    this.rightBottom=new Point(leftTop.x+size.width, leftTop.y+size.height);
    this.size=size;
  }

  public AreaBuilder create()
  {
    return new AreaBuilder(this);
  }

  public String toString()
  {
    return leftTop.toString()+","+rightBottom.toString();
  }


  private Point moveDownLeftTop(final int width, final int height)
  {
    final int newX=leftTop.x+width;
    final int newY=leftTop.y+height;
    if(newX>rightBottom.x||newY>rightBottom.y)
    {
      throw new IllegalArgumentException("there is no enough area to move down point "+
        leftTop.toString()+" by ("+width+"*"+height+") on area "+this.toString());
    }
    return new Point(newX, newY);
  }


  private Point moveUpRightBottom(final int width, final int height)
  {
    if(rightBottom.x<width||rightBottom.y<height)
    {
      throw new IllegalArgumentException("there is no enough area to move up point "+
        rightBottom.toString()+" by ("+width+"*"+height+") on area "+this.toString());
    }

    return new Point(rightBottom.x-width, rightBottom.y-height);
  }


  public static class AreaBuilder
  {
    private Area container;
    public PaddingBuilder paddingBuillder=Padding.builder();
    public SizeBuilder sizeBuilder=Size.builder();
    private String toString;

    public AreaBuilder(final Area area)
    {
      this.container=area;
    }

    public AreaBuilder()
    {
      super();
    }

    public AreaBuilder withIn(final Area area)
    {
      container=area;
      return this;
    }

    public Area createArea()
    {
      final Size size=sizeBuilder.build(container);
      final Padding padding=paddingBuillder.build(container);

      if(size==null&&padding!=null)
      {
        return new Area(
          container.moveDownLeftTop(padding.left, padding.top),
          container.moveUpRightBottom(padding.right, padding.bottom));
      }

      if(padding==null&&size!=null)
      {
        return new Area(size);
      }
      if(isRightBottomPadding(padding))
      {
        return new Area(
          container.moveUpRightBottom(size.width+padding.right, size.height+padding.bottom),
          size);
      }

      if(isLeftTopPadding(padding))
      {
        return new Area(container.moveDownLeftTop(padding.left, padding.top), size);
      }

      throw new IllegalArgumentException(
        "it is not able to create the area: "+this.toString(padding, size));
    }
    private boolean isRightBottomPadding(final Padding padding)
    {
      return padding!=null&&padding.left==null&&padding.top==null&&
        padding.right!=null&&padding.bottom!=null;
    }

    private boolean isLeftTopPadding(final Padding padding)
    {
      return padding!=null&&padding.right==null&&padding.bottom==null&&
        padding.top!=null&&padding.left!=null;
    }

    public String toString()
    {
      return toString;
    }

    private String toString(final Padding padding, final Size size)
    {
      toString="container: "+emptyThen(container, "null")+
        ", padding: "+emptyThen(padding, "null")+",size: "+
        emptyThen(size, "null");
      return toString();
    }

    private String emptyThen(final Object obj, final String str)
    {
      String result=str;
      if(obj!=null)
      {
        result=obj.toString();
      }
      return result;
    }
  }

  public static AreaBuilder builder()
  {
    return new AreaBuilder();
  }
}
