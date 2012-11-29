package com.thenetcircle.services.media.service;

import com.thenetcircle.services.common.Feedbacks;

import java.util.List;

/**
 * Is used to acquire the process of medias by means of the predefined schemas
 *
 * User: julius.yu
 * Date: 11/28/12
 */
public interface ProcessRequest {
  /**
   * true if the request will be dispatch synchronously
   * @return
   */
  boolean isRunSynchronously();

  /**
   * the identifier of the request
   *
   * @return
   */
  String getId();


  List<MediaProcess> getMediaProcesses();

  Feedbacks.Feedback getFeedback();

  String getCallback();
}
