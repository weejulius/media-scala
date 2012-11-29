package com.thenetcircle.services.media.service.impl;

import com.thenetcircle.services.media.service.RequestDispatcher;
import com.thenetcircle.services.media.service.impl.requestdispatcher.DefaultRequestDispatcher;
import org.junit.Before;
import org.junit.Test;

/**
 * User: julius.yu
 * Date: 11/28/12
 */
public class TrackAsynchronousRequestTest {
  private RequestDispatcher dispatcher;

  @Before
  public void setUp(){
    dispatcher = DefaultRequestDispatcher.itself();
  }

   @Test
  public void recordUnprocessedAsynchronousRequestTest(){

     //dispatcher.dispatch();
   }

}
