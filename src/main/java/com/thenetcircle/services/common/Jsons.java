package com.thenetcircle.services.common;


import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser.Feature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

import java.io.IOException;
import java.io.InputStream;


public final class Jsons
{
  private Jsons()
  {
  }

  private static final ObjectMapper MAPPER=new ObjectMapper();

  static
  {
    MAPPER.configure(Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
  }


  public static JsonNode read(final InputStream input)
  {
    if(input==null)
    {
      throw Oops.causedBy("reading json however the input stream is empty");
    }
    try
    {
      return MAPPER.readValue(input, JsonNode.class);
    }
    catch(IOException e)
    {
      throw Oops.causedBy(e, "reading json stream");
    }
  }

  public static JsonNode read(final String raw)
  {
    if(raw==null)
    {
      throw Oops.causedBy("reading json however the json raw is empty");
    }

    try
    {
      return MAPPER.readValue(raw, JsonNode.class);
    }
    catch(IOException e)
    {
      throw Oops.causedBy(e, "reading json raw");
    }
  }

  public static String toString(final Object obj)
  {
    try
    {
      MAPPER.setSerializationInclusion(Include.NON_NULL);
      MAPPER.configure(SerializationFeature.INDENT_OUTPUT, true);
      MAPPER.configure(JsonGenerator.Feature.QUOTE_FIELD_NAMES, true);
      MAPPER.configure(JsonGenerator.Feature.ESCAPE_NON_ASCII, false);
      return MAPPER.writeValueAsString(obj);
    }
    catch(IOException e)
    {
      throw Oops.causedBy(e, "deserialing json to string");
    }
  }

  public static JsonNode mandatoryField(final JsonNode root, final String fieldName)
  {
    final JsonNode field=root.get(fieldName);
    if(field==null)
    {
      throw Oops.causedBy("retrieving the field {} in the json, however it is not existing",
        fieldName);
    }
    return field;
  }

  public static String fieldText(final JsonNode root, final String fieldName)
  {
    return mandatoryField(root, fieldName).asText();
  }
}
