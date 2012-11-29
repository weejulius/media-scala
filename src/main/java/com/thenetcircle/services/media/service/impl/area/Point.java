package com.thenetcircle.services.media.service.impl.area;


public class Point
{
  public int x;
  public int y;

  public Point(final int x, final int y)
  {
    if(x<0||y<0)
    {
      throw new IllegalArgumentException("invalid point :("+x+","+y+")");
    }
    this.x=x;
    this.y=y;
  }


  public Size minus(final Point topLeft)
  {
    return new Size(x-topLeft.x, y-topLeft.y);
  }

  public String toString()
  {
    return "("+x+","+y+")";
  }
}
