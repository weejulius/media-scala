package com.thenetcircle.services.common;


import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardWatchEventKinds;
import java.nio.file.WatchEvent;
import java.nio.file.WatchEvent.Kind;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.thenetcircle.services.media.service.impl.Settings;


/*
 * FIXME prevent from watching one location more than 1 times
 */
public class WatchedLocation
{
  private final Path directory;
  private Path[] watchedFileNames;
  private final WatchEvent.Kind<?>[] kinds;
  private static final Logger LOG=LoggerFactory.getLogger(WatchedLocation.class);
  private static final int WAITING_TIME=2000;
  private static final List<WatchedLocation> WATCHED_LOCATIONS=new ArrayList<WatchedLocation>();
  private static final ExecutorService POOL=Executors.newCachedThreadPool();

  public WatchedLocation(final String location, final WatchEvent.Kind<?>... kinds)
  {
    final Path watchedFile=Paths.get(location);
    if(watchedFile.toFile().isDirectory())
    {
      LOG.info("watching directory {}", watchedFile);
      directory=watchedFile;
    }
    else
    {
      directory=watchedFile.getParent();
      watchedFileNames=new Path[] {watchedFile.getFileName()};
      LOG.info("watching files {} in directory {}", Arrays.toString(watchedFileNames), directory);
    }
    this.kinds=kinds;
  }


  public void watchNow(final AbstractHandleEvent eventHandler)
  {
    if(WATCHED_LOCATIONS.contains(this))
    {
      LOG.warn("{} has been watched already", directory);
      return;
    }
    POOL.submit(new Runnable() {

      public void run()
      {
        WatchService watchService;
        try
        {
          LOG.debug("start to watch directory {}", directory);
          watchService=directory.getFileSystem().newWatchService();
          directory.register(
            watchService, kinds);
          WatchKey watch=null;
          while(true)
          {
            try
            {
              watch=watchService.take();
            }
            catch(InterruptedException ex)
            {
              LOG.debug("Interrupted when taking watch", ex);
            }
            final List<WatchEvent<?>> events=watch.pollEvents();
            watch.reset();
            for(WatchEvent<?> event : events)
            {
              eventHandler.handle(event, directory, watchedFileNames);
            }
            if(!watch.reset())
            {
              LOG.warn("stop watching {} due to invalid reset", directory);
              watch.cancel();
              watchService.close();
              break;
            }
          }
        }
        catch(Exception e)
        {
          LOG.debug("failed to watching "+directory.getFileName(), e);
        }
      }
    }, "WatchingLocationThread_"+directory);

    WATCHED_LOCATIONS.add(this);
  }


  public abstract static class AbstractHandleEvent
  {
    public abstract void handle(WatchEvent<?> event, Path directory, Path[] watchedFileNames);

    protected boolean isWatchedFile(final Path fileName, final Path[] watchedFileNames)
    {
      boolean result=false;
      for(Path watchedFileName : watchedFileNames)
      {
        if(watchedFileName.equals(fileName))
        {
          result=true;
          break;
        }
      }
      return result;
    }
  }


  @SuppressWarnings("unchecked")
  public static class SettingsWatcher extends AbstractHandleEvent
  {

    public void handle(
      final WatchEvent<?> event, final Path directory, final Path[] watchedFileNames)
    {
      try
      {
        final Kind<Path> kind=(Kind<Path>)event.kind();
        final Path context=(Path)event.context();
        LOG.debug("Modified properties: "+context.getFileName());
        if(isWatchedFile(context.getFileName(), watchedFileNames)&&isRegisteredKind(kind))
        {
          final Properties properties=new Properties();
          final Path file=directory.resolve(context.getFileName().toString());
          Thread.sleep(WAITING_TIME);
          final InputStream input=new BufferedInputStream(new FileInputStream(file.toFile()));
          properties.load(input);
          input.close();
          LOG.info("reading properties: "+properties.toString());
          if(properties.size()>1)
          {
            LOG.info("updating settings : "+properties.toString());
            Settings.update(properties);
          }
          else
          {
            LOG.info(
              "failed to update settings due to invalid properties {} try it again",
              properties.toString());
          }
        }

      }
      catch(Exception e)
      {
        LOG.warn("failed to handel watch event ", e);
      }
    }
    private boolean isRegisteredKind(final Kind<Path> kind)
    {
      return kind.equals(StandardWatchEventKinds.ENTRY_MODIFY)||
        kind.equals(StandardWatchEventKinds.ENTRY_CREATE);
    }
  }


  @Override
  public boolean equals(final Object watchedLocation)
  {
    if(this==watchedLocation)
    {
      return true;
    }

    if(watchedLocation==null||watchedLocation.getClass()!=this.getClass())
    {
      return false;
    }

    return directory.equals(((WatchedLocation)watchedLocation).directory);
  }

  @Override
  public int hashCode()
  {
    int hash=7;
    hash=31*hash+(null==directory?0:directory.hashCode());
    return hash;
  }


}
