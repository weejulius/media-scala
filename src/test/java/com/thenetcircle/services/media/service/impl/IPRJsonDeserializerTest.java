package com.thenetcircle.services.media.service.impl;


import static org.junit.Assert.assertTrue;

import com.thenetcircle.services.media.service.impl.imageprocess.ImageProcessRequest;
import com.thenetcircle.services.media.service.impl.parser.ImageProcessRequestParser;
import org.junit.Assert;
import org.junit.Test;
import org.junit.matchers.JUnitMatchers;
import com.fasterxml.jackson.databind.JsonNode;
import com.thenetcircle.services.common.Jsons;
import com.thenetcircle.services.common.Locations;
import com.thenetcircle.services.media.service.impl.parser.Schemas;


public class IPRJsonDeserializerTest
{

  @Test
  public void testExceptionMessageWhenInvalidSchema() throws Exception
  {
    JsonNode schema=Jsons.read(Locations.get("json/invalid_schema.json").read());
    Schemas.newOrUpdateSchema("schema2", schema);
    JsonNode json=Jsons.read(Locations.get("json/process_request.json").read());
    try
    {
      new ImageProcessRequestParser().parse(json);
    }
    catch(Exception e)
    {
      Assert.assertThat(
        e.getMessage(),
        JUnitMatchers.containsString("the value 'abc' of property 'percentage' is not float"));
    }
  }

  @Test
  public void testIgnoreCase() throws Exception
  {
    JsonNode schema=Jsons.read(Locations.get("json/schema2.json").read());
    Schemas.newOrUpdateSchema("schema2", schema);
    JsonNode json=Jsons.read(Locations.get("json/process_request.json").read());
    ImageProcessRequest imageProcess=new ImageProcessRequestParser().parse(json);
    //todo assertTrue(imageProcess.process());
  }

  @Test
  public void testIPRWithoutSchema() throws Exception
  {
    JsonNode json=Jsons.read(Locations.get("json/ipr_without_schema.json").read());
    ImageProcessRequest imageProcess=new ImageProcessRequestParser().parse(json);
    //todo assertTrue(imageProcess.process());
  }

  @Test
  public void testVariableParameterInSchema() throws Exception
  {
    JsonNode schema=Jsons.read(Locations.get("json/schema1.json").read());
    Schemas.newOrUpdateSchema("schema1", schema);
    JsonNode json=Jsons.read(Locations.get("json/variable_parameter.json").read());
    ImageProcessRequest imageProcess=new ImageProcessRequestParser().parse(json);
    //todo assertTrue(imageProcess.process());
  }

  @Test
  public void testGroupProcesses() throws Exception
  {
    JsonNode schema=Jsons.read(Locations.get("json/schema1.json").read());
    Schemas.newOrUpdateSchema("schema1", schema);
    schema=Jsons.read(Locations.get("json/schema2.json").read());
    Schemas.newOrUpdateSchema("schema2", schema);
    JsonNode json=Jsons.read(Locations.get("json/ipr_with_schemas.json").read());
    ImageProcessRequest imageProcess=new ImageProcessRequestParser().parse(json);
    //todo assertTrue(imageProcess.process());
  }

}
