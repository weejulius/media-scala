package com.thenetcircle.services.media.service.impl;


import com.fasterxml.jackson.databind.JsonNode;
import com.thenetcircle.services.common.ImageProcessHelper;
import com.thenetcircle.services.common.Jsons;
import com.thenetcircle.services.common.Locations;
import com.thenetcircle.services.media.service.impl.imageprocess.ImageProcessRequest;
import com.thenetcircle.services.media.service.impl.parser.ImageProcessRequestParser;
import com.thenetcircle.services.media.service.impl.parser.Schemas;
import com.thenetcircle.services.media.service.impl.requestdispatcher.DefaultRequestDispatcher;
import org.junit.Before;
import org.junit.Test;


public class DefaultImageProcessRequestExecutorTest
{
  private int index;
  private ImageProcessHelper helper;


  @Before
  public void setUp()
  {
    helper=new ImageProcessHelper(5, 5);
  }

  @Test
  public void testScales() throws InterruptedException
  {
//    JsonNode schema=Jsons.read(Locations.get("json/schema2.json").read());
	  JsonNode schema=Jsons.read(Locations.get("json/schema2.json").read());
    Schemas.newOrUpdateSchema("schema2", schema);


    helper.waitUntilFinishThreads(
      new Runnable() {
        @Override
        public void run()
        {
          final JsonNode json=Jsons.read(Locations.get("json/process_request.json").read());
          final ImageProcessRequest ipr=new ImageProcessRequestParser().parse(json);
          //ipr.origin=Images.load(helper.getNextImage().toString());
//          ipr.processes.get(0).destination=FileNames.appendToName(
//            ipr.processes.get(0).destination,
//            "_"+index++);
          DefaultRequestDispatcher.itself().dispatch(ipr);
        }
      });
  }

  @Test
  public void testAsychronizedMode() throws InterruptedException
  {
    JsonNode schema=Jsons.read(Locations.get("json/overlay_schema.json").read());
    Schemas.newOrUpdateSchema("overlay_schema", schema);
    helper.waitUntilFinishThreads(
      new Runnable() {
        @Override
        public void run()
        {
          final JsonNode json=Jsons.read(Locations.get("json/asynchronized_request.json").read());
          final ImageProcessRequest ipr=new ImageProcessRequestParser().parse(json);
          //ipr.origin=Images.load(helper.getNextImage().toString());
//          ipr.processes.get(0).destination=FileNames.appendToName(
//            ipr.processes.get(0).destination,
//            "_"+index++);
          DefaultRequestDispatcher.itself().dispatch(ipr);
        }
      }, 10);
  }

  @Test
  public void testIgnoreCallback() throws InterruptedException
  {
    JsonNode schema=Jsons.read(Locations.get("json/overlay_schema.json").read());
    Schemas.newOrUpdateSchema("overlay_schema", schema);
    helper.waitUntilFinishThreads(
      new Runnable() {
        @Override
        public void run()
        {
          final JsonNode json=Jsons.read(Locations.get("json/ignore_callback_request.json").read());
          final ImageProcessRequest ipr=new ImageProcessRequestParser().parse(json);
          //ipr.origin=Images.load(helper.getNextImage().toString());
//          ipr.processes.get(0).destination=FileNames.appendToName(
//            ipr.processes.get(0).destination,
//            "_"+index++);
          DefaultRequestDispatcher.itself().dispatch(ipr);
        }
      }, 10);
  }

  @Test
  public void testOverlay()
  {
    JsonNode schema=Jsons.read(Locations.get("json/overlay_schema.json").read());
    Schemas.newOrUpdateSchema("overlay_schema", schema);


    helper.waitUntilFinishThreads(
      new Runnable() {
        @Override
        public void run()
        {
          final JsonNode json=Jsons.read(Locations.get("json/overlay_request.json").read());
          final ImageProcessRequest ipr=new ImageProcessRequestParser().parse(json);
          //ipr.origin=helper.getNextImage().toString();
//          ipr.processes.get(0).destination=FileNames.appendToName(
//            ipr.processes.get(0).destination,
//            "_"+index++);
          DefaultRequestDispatcher.itself().dispatch(ipr);
        }
      });
  }

  /**
   * regression test: the dispatch in the thread get exception, the dispatch will be terminated until time out
   */
  @Test
  public void testProcessHasException(){
    JsonNode schema=Jsons.read(Locations.get("json/overlay_schema.json").read());
    Schemas.newOrUpdateSchema("overlay_schema", schema);
    final JsonNode json=Jsons.read(Locations.get("json/invalid_request.json").read());
    final ImageProcessRequest ipr=new ImageProcessRequestParser().parse(json);
    DefaultRequestDispatcher.itself().dispatch(ipr);
  }
}
