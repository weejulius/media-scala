package com.thenetcircle.services.media.service.impl.requesthandler;

import com.thenetcircle.services.common.HttpClients;
import com.thenetcircle.services.common.timer.Schedules;
import com.thenetcircle.services.common.timer.Timeout;
import com.thenetcircle.services.common.timer.TimerTask;
import com.thenetcircle.services.media.service.impl.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * User: julius.yu
 * Date: 9/28/12
 */
public class CallbackRetrier {


  private static final Logger LOG = LoggerFactory.getLogger(CallbackRetrier.class);

  public void retry(final Callback callback) {
    if(callback==null){
      return;
    }

    Schedules.self()
             .newTimeout(new RetryTask(callback),
                         calculateDelayTime(callback.retriesTimes),
                         TimeUnit.MILLISECONDS);
  }

  public class RetryTask implements TimerTask {
    private Callback callback;

    public RetryTask(final Callback callback) {
      this.callback = callback;
    }

    @Override
    public void run(final Timeout timeout) throws Exception {

      callback.lastTriedDate = new Date();
      callback.retriesTimes += 1;
      HttpClients.Code code = null;
      try {
        code = HttpClients.get().usePost(callback.url).addParameters(callback.parameters).responseCode();
        LOG.debug("{}:response code of the retry of call back {}", code, callback);
      } catch (Exception e) {
        LOG.debug("failed to retry callback " + callback.url, e);
      }
      if (failedRetry(code)) {
        scheduleFailedRetry(callback);
      }
    }
  }


  private boolean failedRetry(final HttpClients.Code code) {
    return code == null || code != HttpClients.Code.OK;
  }

  private void scheduleFailedRetry(final Callback callback){
    if (overTheMaxDuration(callback.createdDate)) {
      final long delay = calculateDelayTime(callback.retriesTimes);
      Schedules.self().newTimeout(new RetryTask(callback), delay, TimeUnit.MILLISECONDS);
      LOG.debug("the retry for callback:{} is scheduled after {} mills", callback, delay);
    } else {
      LOG.info("the retry for callback:{} is disabled as it has exceeded the max duration of retry",
               callback);
    }
  }

  private long calculateDelayTime(final int times) {
    if (Settings.isRetryIntervalCursive) {
      return Settings.callbackRetryInterval * (times + 1) * 3;
    }
    return Settings.callbackRetryInterval;
  }

  private static boolean overTheMaxDuration(final Date callbackCreatedTime) {
    return Calendar.getInstance().getTimeInMillis() - callbackCreatedTime.getTime() > Settings.maxRetryDuration;
  }
}
