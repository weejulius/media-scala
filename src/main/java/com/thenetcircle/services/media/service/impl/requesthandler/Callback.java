package com.thenetcircle.services.media.service.impl.requesthandler;

import com.thenetcircle.services.common.Feedbacks;
import com.thenetcircle.services.common.KV;
import com.thenetcircle.services.common.Oops;
import com.thenetcircle.services.common.PlaceHolder;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * User: julius.yu
 * Date: 9/28/12
 */
public class Callback implements Serializable {
  /**
   * if the url is empty, the callback is ignored
   */
  public String url;
  public KV<?, ?>[] parameters;
  Date lastTriedDate;
  public int retriesTimes;
  public Date createdDate;
  public static String IgnoredCallback="";

  public Callback(final String url, final KV<?, ?>[] parameters) {
    if(url==null){
      throw new IllegalArgumentException("the callback url is null");
    }
    this.url = url;
    this.parameters = parameters;
    createdDate= Calendar.getInstance().getTime();
  }

  public static Callback createFeedBackCallback(final String callback, final String imageRequestId,
                                                final Feedbacks.Feedback feedback){
    return new Callback(callback,translateToHttpFeedback(imageRequestId, feedback));
  }

  public String toString() {
    return PlaceHolder.get("{", "}").replace("url : {}, parameters:{}", url, Arrays.toString(parameters));
  }


  /**
   * return the http parameters as feedback including {
   * id  : image request id
   * cause : root cause if the request is failed
   * status : 200 //or 500 and so on
   * }
   *
   * @param
   * @return
   */
  private static KV<?, ?>[]  translateToHttpFeedback(final String imageRequestId,
                                              final Feedbacks.Feedback feedback) {
    if (feedback == null) {
      throw Oops.causedBy("the feed back is not passed in the call back {}");
    }
   // final Response response = Feedbacks.toResponse(feedback);
   // final int status = response.getStatus();
    KV<?, ?>[] queryParameters = new KV<?, ?>[2];
   // if (status >= 300) {
  //    queryParameters = new KV<?, ?>[3];
 //     queryParameters[2] = KV.a("cause", response.getEntity().toString());
 //   }
    queryParameters[0] = KV.a("id", imageRequestId);
 //   queryParameters[1] = KV.a("status", status);
    return queryParameters;
  }

  public boolean isIgnored() {
    return IgnoredCallback.equals(url);
  }
}
