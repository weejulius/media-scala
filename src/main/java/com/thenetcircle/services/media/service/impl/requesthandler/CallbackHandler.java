package com.thenetcircle.services.media.service.impl.requesthandler;

import com.thenetcircle.services.media.service.ProcessRequest;
import com.thenetcircle.services.media.service.RequestHandler;

/**
 * User: julius.yu
 * Date: 11/28/12
 */
public class CallbackHandler implements RequestHandler {

  private final CallbackExecutor callbackExecutor = new DefaultCallbackExecutor();

  @Override
  public void handle(final ProcessRequest request) {

    if (!request.isRunSynchronously()) {
      callbackExecutor.callback(Callback.createFeedBackCallback(request.getCallback(),
                                                                request.getId(),
                                                                request.getFeedback()));
    }
  }
}
