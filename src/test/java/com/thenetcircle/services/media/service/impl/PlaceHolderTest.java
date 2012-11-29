package com.thenetcircle.services.media.service.impl;


import java.util.HashMap;
import java.util.Map;
import org.junit.Assert;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.thenetcircle.services.common.PlaceHolder;


public class PlaceHolderTest
{
  private final Logger log=LoggerFactory.getLogger(this.getClass());

  @Test
  public void testPlaceHolder()
  {
    PlaceHolder placeHolder=PlaceHolder.get("${", "}");

    Assert.assertEquals(
      "fsdf } ${ sfsdfsdf s${ hello world",
      placeHolder.replace("fsdf } ${ sfsdfsdf s${ ${sdfs}", "hello world"));

    Assert.assertEquals(
      "fsdf ${ sfsdfsdf s${ hello world",
      placeHolder.replace("fsdf ${ sfsdfsdf s${ ${sdfs}", "hello world"));

    Assert.assertEquals(
      "hello world",
      placeHolder.replace("${a}", "hello world"));

    Assert.assertEquals(
      "fsdf ${ sfsdfsdf s${ ",
      placeHolder.replace("fsdf ${ sfsdfsdf s${ ", "hello world"));


    Assert.assertEquals(
      "fsdf ${ sfsdfsdf s${ hello world ${ sdfsdfsdf good morning",
      placeHolder.replace(
        "fsdf ${ sfsdfsdf s${ ${sdfs} ${ sdfsdfsdf ${}",
        "hello world",
        "good morning"));

    Assert.assertEquals(
      "good morning",
      placeHolder.replace(
        "good morning"));
  }

  @Test
  public void testPlaceHolderWithMap()
  {
    PlaceHolder placeHolder=PlaceHolder.get("${", "}");
    Map<String, String> values=new HashMap<String, String>();
    values.put("v1", "hello world");
    values.put("v2", "good morning");
    Assert.assertEquals(
      "fsdf ${ sfsdfsdf s${ hello world",
      placeHolder.replace("fsdf ${ sfsdfsdf s${ ${v1}", "hello world"));

    Assert.assertEquals(
      "fsdf ${ sfsdfsdf s${ hello world ${ sdfsdfsdf good morning",
      placeHolder.replace(
        "fsdf ${ sfsdfsdf s${ ${v1} ${ sdfsdfsdf ${v2}", values));

    Assert.assertEquals(
      "i do not know how to say hello world and good morning }",
      placeHolder.replace(
        "i do not know how to say ${v1} and ${v2} }", values));


    Assert.assertEquals(
      "good morning",
      placeHolder.replace(
        "${v2}", values));
  }
  @Test
  public void testPerformance()
  {
    String sentence="I go shopping in a mall and I meet a classmate superisely,"+
      " and we want to have a chat and go to a coffee bar, the name is";
    long start=System.currentTimeMillis();
    for(int i=0; i<300000; i++)
    {
      String a="today "+sentence+" amy";
    }
    long end=System.currentTimeMillis();

    log.debug("round 1:{}", end-start);

    PlaceHolder holder=PlaceHolder.get("{", "}");

    sentence="{} "+sentence+" {}";

    start=System.currentTimeMillis();
    for(int i=0; i<300000; i++)
    {
      holder.replace(sentence, "today", "amy");
    }
    end=System.currentTimeMillis();
    log.debug("round 2:{}", end-start);

  }
}
