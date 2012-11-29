package com.thenetcircle.services.media.service.impl;


import com.thenetcircle.services.media.service.impl.parser.ImageProcessRequestParser;
import org.junit.Test;
import com.fasterxml.jackson.databind.JsonNode;
import com.thenetcircle.services.common.Jsons;
import com.thenetcircle.services.common.Locations;
import com.thenetcircle.services.common.Oops;
import com.thenetcircle.services.media.service.impl.imageprocess.ImageProcessRequest;


public class QualityStoreListenerTest
{
  @Test
  public void testIPRWithQuality() throws Exception
  {
    JsonNode json=Jsons.read(Locations.get("json/ipr_with_quality.json").read());
    ImageProcessRequest imageProcess=new ImageProcessRequestParser().parse(json);
   //todo  imageProcess.process();
  }

  @Test(expected=Oops.class)
  public void testQualityLargeThan1() throws Exception
  {
    JsonNode json=Jsons.read(Locations.get("json/invalid_quality.json").read());
    new ImageProcessRequestParser().parse(json);
  }

}
