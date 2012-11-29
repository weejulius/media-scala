package com.thenetcircle.services.common;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import org.junit.Test;


public class FileNamesTest
{

  @Test
  public void testGetName()
  {
    assertEquals("a.txt", FileNames.getName("c:\\a.txt"));
    assertEquals("a", FileNames.getName("c:\\a"));
    assertEquals("c.txt", FileNames.getName("/c.txt"));
    assertEquals("b.txt", FileNames.getName("https://www.sample.com/folder/b.txt"));

    try
    {
      FileNames.getName("/");
      fail("does not have file name");
    }
    catch(Oops e)
    {
      e.printStackTrace();
    }
    try
    {
      FileNames.getName("a");
      fail("does not have file name");
    }
    catch(Oops e)
    {
      e.printStackTrace();
    }
    try
    {
      FileNames.getName("http://ww.xxxxx.com/");
      fail(" does not have file name");
    }
    catch(Oops e)
    {
      e.printStackTrace();
    }
    try
    {
      FileNames.getName("c:\\");
      fail(" does not have file name");
    }
    catch(Oops e)
    {
      e.printStackTrace();
    }

  }

  @Test
  public void testLastEntry()
  {
    assertEquals("a.txt", FileNames.lastEntry("c:\\a.txt"));
    assertEquals("a", FileNames.lastEntry("c:\\a"));
  }

  @Test
  public void testAppendToName()
  {
    assertEquals("c:\\ab.txt", FileNames.appendToName("c:\\a.txt", "b"));
    assertEquals("c:\\ab", FileNames.appendToName("c:\\a", "b"));
  }
}
