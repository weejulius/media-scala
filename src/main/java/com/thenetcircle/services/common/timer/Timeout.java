package com.thenetcircle.services.common.timer;

/**
 * User: julius.yu
 * Date: 8/23/12
 */
public interface Timeout {

    /**
     * Returns the {@link com.thenetcircle.services.common.timer.Timer} that created this dispatch.
     */
    Timer getTimer();


    /**
     * Returns the {@link TimerTask} which is associated with this dispatch.
     */
    TimerTask getTask();


    /**
     * Returns {@code true} if and only if the {@link TimerTask} associated with
     * this dispatch has been expired.
     */
    boolean isExpired();


    /**
     * Returns {@code true} if and only if the {@link TimerTask} associated with
     * this dispatch has been cancelled.
     */
    boolean isCancelled();


    /**
     * Cancels the {@link TimerTask} associated with this dispatch. It the task
     * has been executed or cancelled already, it will return with no side
     * effect.
     */
    void cancel();
  }