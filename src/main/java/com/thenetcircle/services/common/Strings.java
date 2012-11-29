package com.thenetcircle.services.common;


public final class Strings
{
  private Strings()
  {
  }

  public static boolean isNullOrBlank(final String str)
  {
    return str==null||str.trim().length()<=0;
  }
}
