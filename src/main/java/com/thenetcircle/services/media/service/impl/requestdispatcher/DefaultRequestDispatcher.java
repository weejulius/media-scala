package com.thenetcircle.services.media.service.impl.requestdispatcher;


import com.thenetcircle.services.media.service.ProcessRequest;
import com.thenetcircle.services.media.service.RequestDispatcher;
import com.thenetcircle.services.media.service.RequestHandler;
import com.thenetcircle.services.media.service.impl.Settings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Calendar;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;


/**
 * Utilized to dispatch the image dispatch requests in a fixed queue
 */
public final class DefaultRequestDispatcher implements RequestDispatcher
{

  private static Semaphore requestSize;
  private static ExecutorService pool;
  private static final Logger LOG=LoggerFactory.getLogger(DefaultRequestDispatcher.class);
  private static DefaultRequestDispatcher INSTANCE = new DefaultRequestDispatcher();

  private DefaultRequestDispatcher()
  {
    init();
  }

  public static DefaultRequestDispatcher itself(){
    return INSTANCE;
  }

  private void init()
  {
    requestSize=new Semaphore(Settings.maxRequestsInProcess, true);
    pool=Executors.newFixedThreadPool(Settings.threadNumOfPool);
  }

  private  void processInSynchronizeMode(final ProcessRequest request)
  {
    final long start=Calendar.getInstance().getTimeInMillis();

    try {
      requestSize.acquire();
      LOG.debug("{} permits are available to be acquired", requestSize.availablePermits());
      pool.submit(new ProcessRequestCallable(request)).get(Settings.maxSecondPerProcess, TimeUnit.SECONDS);
    } catch (Exception e) {
      LOG.debug("failed to dispatch the request " + request.getId(), e);
      //todo request.feedback.logFailure(e);
      LOG.debug("the dispatch of request is failed ", e.getMessage());
    } finally {
      final long end = Calendar.getInstance().getTimeInMillis();
      LOG.debug(" total elapsed time including waiting {}", end - start);
      requestSize.release();
    }
  }

  private  void processInAsynchronousMode(final ProcessRequest request)
  {
    AsynchronizedMode.run(new Runnable() {
      @Override
      public void run() {
        processInSynchronizeMode(request);
      }
    });
  }

  public void dispatch(final String re){}

  /*
   * return Accepted if request is processed in asynchronous mode, the result of dispatch will be
   * notified via callback
   */
  public void dispatch(final ProcessRequest request)
  {
    if(request.isRunSynchronously())
    {
      processInSynchronizeMode(request);
    }
    else
    {
      processInAsynchronousMode(request);
      //todo request.feedback.type= FeedbackType.Accepted;
    }
  }

  @Override
  public void addProcessor(final RequestHandler requestHandler) {
    //To change body of implemented methods use File | Settings | File Templates.
  }

  private static class ProcessRequestCallable implements Callable<Void> {

    private final ProcessRequest request;

    public ProcessRequestCallable(final ProcessRequest request) {
      this.request = request;
    }

    @Override
    public Void call() throws Exception {
      final long start = Calendar.getInstance().getTimeInMillis();
     //todo  request.process();
      final long end = Calendar.getInstance().getTimeInMillis();
      LOG.debug(" total dispatch elapsed time {}", end - start);
      return null;
    }
  }

}
