package com.thenetcircle.services.media.service.impl;


import com.fasterxml.jackson.databind.JsonNode;
import com.thenetcircle.services.common.Feedbacks;
import com.thenetcircle.services.common.Feedbacks.Feedback;
import com.thenetcircle.services.common.Jsons;
import com.thenetcircle.services.common.Locations;
import com.thenetcircle.services.common.Oops;
import com.thenetcircle.services.media.service.impl.imageprocess.ImageProcessRequest;
import com.thenetcircle.services.media.service.impl.parser.ImageProcessRequestParser;
import com.thenetcircle.services.media.service.impl.parser.Schemas;
import org.junit.Test;


public class ImageProcessRequestTest
{

  @Test
  public void testToString()
  {
    JsonNode schema=Jsons.read(Locations.get("json/overlay_schema.json").read());
    Schemas.newOrUpdateSchema("overlay_schema", schema);
    ImageProcessRequest ipr=new ImageProcessRequestParser().parse(Jsons.read(Locations.get(
      "json/overlay_request.json").read()));
    Feedback feedback=Feedbacks.when("reading ipr '{}'", ipr.toString());

    feedback.logFailure(Oops.causedBy("serializing schema", ipr.toString()));
  }

  @Test
  public void testHasIntArrayInVariables(){
    JsonNode schema=Jsons.read(Locations.get("json/variable_schema.json").read());
    Schemas.newOrUpdateSchema("variable_schema", schema);
    ImageProcessRequest ipr=new ImageProcessRequestParser().parse(Jsons.read(Locations.get(
      "json/int_array_in_request.json").read()));
    Feedback feedback=Feedbacks.when("reading ipr '{}'", ipr.toString());

    feedback.logFailure(Oops.causedBy("serializing schema", ipr.toString()));
  }

}
