package com.thenetcircle.services.common.timer;

/**
 * User: julius.yu
 * Date: 8/24/12
 */
public interface TimerTask {
  void run(Timeout timeout) throws Exception;
}
