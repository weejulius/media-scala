package com.thenetcircle.services.media.service.impl.requestdispatcher;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public final class AsynchronizedMode
{
  private AsynchronizedMode()
  {
  }

  private static final ExecutorService POOL=Executors.newCachedThreadPool();

  public static void run(final Runnable runnable)
  {
    POOL.submit(runnable);
  }

}
