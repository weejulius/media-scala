package com.thenetcircle.services.media.service.impl.parser;


import com.fasterxml.jackson.databind.JsonNode;
import com.thenetcircle.services.common.Oops;

import java.util.HashMap;
import java.util.Map;


/*
 * Schema cache, which is reused, before we use the schema we should put it here firstly
 */
public final class Schemas {

  private static final Map<String, JsonNode> SCHEMAS = new HashMap<String, JsonNode>();

  private Schemas() {
  }

  /*
   * Load all the persisted schemas to pool initially
   */
  public static void init() {
  }


  public static synchronized void newOrUpdateSchema(final String name, final JsonNode node) {
    SCHEMAS.put(name, node);
  }


  public static JsonNode getSchema(final String name) {
    final JsonNode node = SCHEMAS.get(name);
    if (node == null) {
      throw Oops.causedBy("the schema {} is not existing in the schema cache pool", name);
    }
    return node;
  }

  public static void removeSchema(final String name) {
    SCHEMAS.remove(name);
  }

}
