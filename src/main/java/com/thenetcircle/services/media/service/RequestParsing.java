package com.thenetcircle.services.media.service;

import com.fasterxml.jackson.databind.JsonNode;

/**
 * User: julius.yu
 * Date: 11/29/12
 */
public interface RequestParsing {

  /**
   * parse the request raw
   *
   * @param requestAsStr
   * @return
   */
  ProcessRequest parse(final JsonNode requestAsStr);
}
