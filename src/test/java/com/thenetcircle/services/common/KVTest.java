package com.thenetcircle.services.common;


import static org.junit.Assert.assertEquals;
import org.junit.Test;


public class KVTest
{

  @Test
  public void testAppend()
  {
    KV<?, ?>[] kvs=KV.some("x", 1, "y", 2);
    kvs=KV.append(kvs, "z", 3);

    assertEquals("z", kvs[2].key);
    assertEquals(3, kvs[2].value);

  }
}
