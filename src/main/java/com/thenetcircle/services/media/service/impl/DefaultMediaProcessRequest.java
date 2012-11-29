package com.thenetcircle.services.media.service.impl;

import com.thenetcircle.services.common.Feedbacks;
import com.thenetcircle.services.common.UUID;
import com.thenetcircle.services.media.service.ProcessRequest;
import com.thenetcircle.services.media.service.impl.requesthandler.Callback;

/**
 * User: julius.yu
 * Date: 11/28/12
 */
public abstract class DefaultMediaProcessRequest implements ProcessRequest{

  protected String id;
  /**
   * if the callback is given, the request is run asynchronously, if callback is blank,
   * the callback is ignored.
   */
  protected String callback;

  protected Feedbacks.Feedback feedback;


  public DefaultMediaProcessRequest() {
    id = UUID.get();
    feedback = Feedbacks.when("image process request id : {}", id);
  }

  @Override
  public boolean isRunSynchronously() {
    return callback != null;
  }

  public void populateCallbackURL(final String callbackURL) {
    if (callbackURL == null || callbackURL.isEmpty()) {
      this.callback = Callback.IgnoredCallback;
    } else {
      this.callback = callbackURL;
    }
  }

  public Feedbacks.Feedback getFeedback(){
     return feedback;
  }

  public String getCallback() {
    return this.callback;
  }

  public String getId(){
    return id;
  }
}
