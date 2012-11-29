package com.thenetcircle.services.media.service.impl.requesthandler;

import com.thenetcircle.services.media.service.ProcessRequest;
import com.thenetcircle.services.media.service.RequestHandler;

/**
 * User: julius.yu
 * Date: 11/28/12
 */
public class PersistRequest implements RequestHandler {

  @Override
  public void handle(final ProcessRequest request) {
    if(!request.isRunSynchronously()){

    }
  }
}
