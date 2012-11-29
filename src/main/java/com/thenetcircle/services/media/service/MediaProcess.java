package com.thenetcircle.services.media.service;

import java.io.IOException;

/**
 * User: julius.yu
 * Date: 11/28/12
 */
public interface MediaProcess {
  void process(final Media originImage) throws IOException;
}
