package com.thenetcircle.services.media.service.impl;

import com.thenetcircle.services.common.Feedbacks;
import com.thenetcircle.services.media.service.impl.requesthandler.Callback;
import com.thenetcircle.services.media.service.impl.requesthandler.DefaultCallbackExecutor;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

/**
 * User: julius.yu
 * Date: 9/28/12
 */
public class RetryCallbackTest {

  @Test
  public void testRetryCallBack() throws InterruptedException {
    final Callback callback = Callback.createFeedBackCallback("http://localhost:232",
                                                              "1",
                                                              new Feedbacks.Feedback("test"));
    new DefaultCallbackExecutor().callback(callback);

    Thread.sleep(10000);

    assertTrue(callback.retriesTimes >= 1);
  }

  @Test
  public void testOK() throws InterruptedException {
    final Callback callback = Callback.createFeedBackCallback("http://localhost:8088",
                                                              "1",
                                                              new Feedbacks.Feedback("test"));
    new DefaultCallbackExecutor().callback(callback);
    Thread.sleep(1000);
  }
}
