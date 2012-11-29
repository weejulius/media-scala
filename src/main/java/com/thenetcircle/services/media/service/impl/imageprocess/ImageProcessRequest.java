package com.thenetcircle.services.media.service.impl.imageprocess;


import com.thenetcircle.services.media.service.MediaProcess;
import com.thenetcircle.services.media.service.impl.DefaultMediaProcessRequest;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/*
 * A request to process image, the origin is the location of image to be processed,
 * and the callback is the address to notify the request result to communities
 */
public class ImageProcessRequest extends DefaultMediaProcessRequest implements Serializable {
  public String origin;
  public final List<MediaProcess> processes = new ArrayList<MediaProcess>();

  @Override
  public List<MediaProcess> getMediaProcesses() {
    return processes;
  }

  @Override
  public String toString() {
    return "(request id:" + id + ", origin:" + origin + ")";
  }

}
