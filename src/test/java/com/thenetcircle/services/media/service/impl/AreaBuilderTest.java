package com.thenetcircle.services.media.service.impl;


import static org.junit.Assert.assertEquals;
import org.junit.Test;
import com.thenetcircle.services.media.service.impl.area.Area;
import com.thenetcircle.services.media.service.impl.area.Padding;


public class AreaBuilderTest
{

  @Test
  public void testPercentagePadding()
  {
    Padding padding=Padding.builder().left(0.1f).top(20).right(0.2f).bottom(30).build(
      new Area(200, 200));

    assertEquals("20", ""+padding.left);
    assertEquals("20", ""+padding.top);
    assertEquals("40", ""+padding.right);
    assertEquals("30", ""+padding.bottom);
  }


}
