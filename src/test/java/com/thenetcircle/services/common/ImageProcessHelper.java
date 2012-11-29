package com.thenetcircle.services.common;


import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class ImageProcessHelper
{
  private ExecutorService threadPool;
  private List<Path> images;
  private Path resultRootPath;
  private final AtomicInteger picIndex=new AtomicInteger(0);
  private static final Logger LOG=LoggerFactory.getLogger(ImageProcessHelper.class);
  private DateFormat formatter=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

  int numOfRounds;
  int numOfThreads;

  public ImageProcessHelper(final int numOfThreads, final int rounds)
  {
    numOfRounds=rounds;
    this.numOfThreads=numOfThreads;
  }
  public Path getNextImage()
  {
    if(resultRootPath==null)
    {
      resultRootPath=Paths.get("/Users/julius.yu/images/result/");
    }
    if(images==null)
    {
      images=getPathOfFiles("/Users/julius.yu/images/");
    }
    return images.get(picIndex.getAndIncrement()%images.size());
  }

  private List<Path> getPathOfFiles(final String path)
  {
    List<Path> paths=new ArrayList<Path>();
    for(File file : Paths.get(path).toFile().listFiles(new FilenameFilter() {

      @Override
      public boolean accept(final File dir, final String name)
      {
        return name.endsWith(".jpg")||name.endsWith(".jpeg")||name.endsWith(".png");
      }
    }))
    {
      paths.add(file.toPath());
    }
    Collections.sort(paths);
    return paths;
  }

  public void waitUntilFinishThreads(final Runnable runnable, final int waitSeconds)
  {
    threadPool=Executors.newFixedThreadPool(numOfThreads);
    long start=Calendar.getInstance().getTimeInMillis();
    for(int i=0; i<numOfRounds; i++)
    {

      threadPool.execute(runnable);
    }
    try
    {
      Thread.sleep(waitSeconds*1000);
    }
    catch(InterruptedException e)
    {
      // TODO Auto-generated catch block
      e.printStackTrace();
    }
    threadPool.shutdown(); // Disable new tasks from being submitted
    try
    {
      // Wait a while for existing tasks to terminate
      if(!threadPool.awaitTermination(10, TimeUnit.MINUTES))
      {
        threadPool.shutdownNow(); // Cancel currently executing tasks
        // Wait a while for tasks to respond to being cancelled
        if(!threadPool.awaitTermination(10, TimeUnit.MINUTES))
          LOG.warn("Pool did not terminate");
      }
      LOG.debug("elapsed time: {}", Calendar.getInstance().getTimeInMillis()-start);
    }
    catch(InterruptedException ie)
    {
      // (Re-)Cancel if current thread also interrupted
      threadPool.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }
  }


  public void waitUntilFinishThreads(final Runnable runnable)
  {
    threadPool=Executors.newFixedThreadPool(numOfThreads);
    long start=Calendar.getInstance().getTimeInMillis();
    for(int i=0; i<numOfRounds; i++)
    {

      threadPool.execute(runnable);
    }

    threadPool.shutdown(); // Disable new tasks from being submitted
    try
    {
      // Wait a while for existing tasks to terminate
      if(!threadPool.awaitTermination(10, TimeUnit.MINUTES))
      {
        threadPool.shutdownNow(); // Cancel currently executing tasks
        // Wait a while for tasks to respond to being cancelled
        if(!threadPool.awaitTermination(10, TimeUnit.MINUTES))
          LOG.warn("Pool did not terminate");
      }
      LOG.debug("elapsed time: {}", Calendar.getInstance().getTimeInMillis()-start);
    }
    catch(InterruptedException ie)
    {
      // (Re-)Cancel if current thread also interrupted
      threadPool.shutdownNow();
      // Preserve interrupt status
      Thread.currentThread().interrupt();
    }
  }

  public Path createDirectoryToStoreResult(final String path) throws IOException
  {
    Path directory=resultRootPath.resolve(path);

    if(directory.toFile().exists())
    {
      deleteDir(directory.toFile());
    }

    Files.createDirectory(directory);

    LOG.debug(
      "Test scenario {} with {} threads and {} rounds at {}",
      new Object[] {path.toUpperCase(),
        numOfThreads,
        numOfRounds, formatter.format(Calendar.getInstance().getTime())});
    LOG.debug("free memory {}", Runtime.getRuntime().freeMemory());
    LOG.debug("available cores {}", Runtime.getRuntime().availableProcessors());

    return directory;
  }

  private boolean deleteDir(final File dir)
  {
    if(dir.isDirectory())
    {
      String[] children=dir.list();
      for(int i=0; i<children.length; i++)
      {
        boolean success=deleteDir(new File(dir, children[i]));
        if(!success)
        {
          return false;
        }
      }
    }
    return dir.delete();
  }

  public void setTestImageType(final String... suffixs)
  {
    List<Path> result=new ArrayList<Path>();
    for(Path image : images)
    {
      for(String suffix : suffixs)
      {
        if(image.toString().endsWith(suffix))
        {
          result.add(image);
        }
      }
    }
    images=result;
  }

}
