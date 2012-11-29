package com.thenetcircle.services.media.service.impl.area;


import com.thenetcircle.services.common.Oops;


/*
 * The padding of the inner area relative to the outer area
 */
public class Padding
{
  public Integer top;
  public Integer right;
  public Integer bottom;
  public Integer left;


  public Padding(final Integer left, final Integer top, final Integer right, final Integer bottom)
  {
    super();
    this.top=top;
    this.right=right;
    this.bottom=bottom;
    this.left=left;
  }

  public static PaddingBuilder builder()
  {
    return new PaddingBuilder();
  }


  public static class PaddingBuilder
  {
    private float top=-1;
    private float right=-1;
    private float bottom=-1;
    private float left=-1;

    public PaddingBuilder left(final float left)
    {
      if(left<0)
      {
        throw Oops.causedBy("invalid left padding {}", Float.toString(left));
      }
      this.left=left;
      return this;
    }
    public PaddingBuilder top(final float top)
    {
      if(top<0)
      {
        throw Oops.causedBy("invalid top padding {}", Float.toString(top));
      }
      this.top=top;
      return this;
    }
    public PaddingBuilder right(final float right)
    {
      if(right<0)
      {
        throw Oops.causedBy("invalid right padding {}", Float.toString(right));
      }
      this.right=right;
      return this;
    }
    public PaddingBuilder bottom(final float bottom)
    {
      if(bottom<0)
      {
        throw Oops.causedBy("invalid bottom padding {}", Float.toString(bottom));
      }
      this.bottom=bottom;
      return this;
    }

    public PaddingBuilder padding(final Float[] nums)
    {
      return left(nums[0]).top(nums[1]).right(nums[2]).bottom(nums[3]);
    }

    public PaddingBuilder left(final int left)
    {
      return left((float)left);
    }
    public PaddingBuilder top(final int top)
    {
      return top((float)top);
    }
    public PaddingBuilder right(final int right)
    {
      return right((float)right);
    }
    public PaddingBuilder bottom(final int bottom)
    {
      return bottom((float)bottom);
    }

    public PaddingBuilder padding(final int left, final int top, final int right, final int bottom)
    {
      return left(left).top(top).right(right).bottom(bottom);
    }

    public Padding build(final Area area)
    {
      int width=0;
      int height=0;

      if(area!=null)
      {
        width=area.size.width;
        height=area.size.height;
      }

      if(left+top+right+bottom==-4)
      {
        return null;
      }

      return new Padding(calculateValue(left, width),
        calculateValue(top, height),
        calculateValue(right, width),
        calculateValue(bottom, height));

    }
    private Integer calculateValue(final float num, final int referedNum)
    {
      if(num==-1)
      {
        return null;
      }
      else if(num>=1||num==0)
      {
        return (int)num;
      }
      else if(num>0&&num<1&&referedNum>0)
      {

        return Math.round(num*referedNum);
      }
      else
      {
        throw Oops.causedBy("the padding ({},{},{},{}) is invalid",
          Float.toString(left), Float.toString(top), Float.toString(right), Float.toString(bottom));
      }
    }
  }


}
