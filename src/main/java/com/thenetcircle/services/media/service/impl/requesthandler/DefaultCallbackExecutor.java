package com.thenetcircle.services.media.service.impl.requesthandler;


import com.thenetcircle.services.common.HttpClients;
import com.thenetcircle.services.common.HttpClients.Code;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/*
 * the callback will be executed and retried if any error
 */
public class DefaultCallbackExecutor implements CallbackExecutor {
  private static final Logger LOG = LoggerFactory.getLogger(DefaultCallbackExecutor.class);
  private final CallbackRetrier callbackRetrier = new CallbackRetrier();

  @Override
  public void callback(final Callback callback) {
    if (callback == null || callback.isIgnored()) {
      return;
    }
    final Code code = sendCallbackByHttp(callback);

    if (code != Code.OK) {
      scheduleRetry(callback);
    }
  }

  private void scheduleRetry(final Callback callback) {
    callbackRetrier.retry(callback);
  }

  private Code sendCallbackByHttp(final Callback callback) {
    try {
      final Code code = HttpClients.get()
                                   .usePost(callback.url)
                                   .addParameters(callback.parameters)
                                   .responseCode();
      LOG.info("{}:response code of call back {}", code, callback);

      return code;
    } catch (Exception e) {
      LOG.info("failed to send callback by http : " + callback.toString(), e);
      return Code.ServerError;
    }
  }
}
