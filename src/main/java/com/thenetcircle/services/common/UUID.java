package com.thenetcircle.services.common;


public final class UUID
{
  private static int next;

  private UUID()
  {
  }

  /*
   * The UUID is consist of second , thread id, and auto increment,
   * It is unique locally
   */
  public static String get() {
    next = ++next % 1000000;
    return "" + ((System.currentTimeMillis() / 1000) * 100000000 +
                 Thread.currentThread().getId() * 1000000 + next);
  }

}
