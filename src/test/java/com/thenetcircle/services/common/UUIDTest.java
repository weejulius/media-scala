package com.thenetcircle.services.common;


import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;


public class UUIDTest
{
  @Test
  public void testUUIDBasedOnTime()
  {
    for(int i=0; i<6000; i++)
    {
      assertEquals(18, (""+UUID.get()).length());
      assertTrue(UUID.get()!=UUID.get());
    }

  }

  @Test
  public void testUUIDPerf() {
	  {
		  long t = System.currentTimeMillis();
		  for (int i = 0; i < 10000; i++) {
			  UUID.get();
		  }
		  System.out.println(System.currentTimeMillis() - t);
	  }
	  
	  {
		  long t = System.currentTimeMillis();
		  for (int i = 0; i < 10000; i++) {
			  java.util.UUID.randomUUID();
		  }
		  System.out.println(System.currentTimeMillis() - t);
	  }
  }
}
