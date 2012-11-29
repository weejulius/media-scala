package com.thenetcircle.services.common;


public final class Enums
{
  private Enums()
  {
  }

  public static boolean getEnumByName(final Object[] objs, final String name)
  {
    boolean result=false;

    if(objs!=null)
    {
      for(Object o : objs)
      {
        if(o.toString().equalsIgnoreCase(name))
        {
          result=true;
          break;
        }
      }
    }
    return result;
  }

}
