package com.thenetcircle.services.common;


import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import com.thenetcircle.services.media.service.impl.transform.Images;


public class EnumsTest
{

  @Test
  public void testEnumsContains()
  {
    assertTrue(Enums.getEnumByName(Images.Extension.values(), "JPEG"));
    assertFalse(Enums.getEnumByName(Images.Extension.values(), "JP1EG"));
  }
}
