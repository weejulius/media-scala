package com.thenetcircle.services.media.service.impl.imageprocess;

import com.thenetcircle.services.common.Oops;
import com.thenetcircle.services.media.service.MediaProcess;
import com.thenetcircle.services.media.service.ProcessRequest;
import com.thenetcircle.services.media.service.RequestHandler;
import com.thenetcircle.services.media.service.impl.Image;
import com.thenetcircle.services.media.service.impl.transform.Images;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: julius.yu
 * Date: 11/28/12
 */
public class ImageHandler implements RequestHandler {

  private static final Logger LOG = LoggerFactory.getLogger(ImageProcessRequest.class);

  @Override
  public void handle(final ProcessRequest request) {
    Image originImage = null;
    try {
      for (MediaProcess process : request.getMediaProcesses()) {
        final String origin=((ImageProcessRequest) request).origin;
        originImage=readImageOfOrigin(origin,originImage);
        process.process(originImage);
      }
    } catch (Exception e) {
      request.getFeedback().logFailure(e);
      LOG.info("the request {} is failed to process ", toString());
      LOG.debug("failed to process request " + request.getId(), e);
      LOG.info(request.getFeedback().toString());
    }
  }

  /*
  * Clone image for each process if there are multiple processes for the origin image
  */
  private Image readImageOfOrigin(final String origin, final Image existingOrigin) throws
                                                                                   CloneNotSupportedException {
    Image originImage = null;
    if (existingOrigin == null) {
      originImage = Images.load(origin);
      if (originImage == null) {
        throw Oops.causedBy("Oops,the origin image <{}> cannot be loaded", origin);
      }
    } else {
      originImage = new Image(existingOrigin.clone(), existingOrigin.name());
    }

    return originImage;
  }


}
