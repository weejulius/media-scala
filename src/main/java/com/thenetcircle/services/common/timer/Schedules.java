package com.thenetcircle.services.common.timer;


/**
 * User: julius.yu
 * Date: 7/13/12
 */
public class Schedules {

  private static final Timer timeWheel = new HashedWheelTimer();

  private Schedules() {
  }

  public static Timer self() {
    return timeWheel;
  }

}
