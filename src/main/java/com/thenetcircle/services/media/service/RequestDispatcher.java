package com.thenetcircle.services.media.service;


/**
 * User: julius.yu
 * Date: 11/27/12
 */
public interface RequestDispatcher {

  /**
   * dispatch the request to its corresponding processor
   *
   * @param request
   */
  void dispatch(final String request);

  void addProcessor(final RequestHandler requestHandler);
}
