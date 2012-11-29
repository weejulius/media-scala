package com.thenetcircle.services.media.service.impl.location;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import com.thenetcircle.services.common.Location;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.thenetcircle.services.common.Oops;
import com.thenetcircle.services.media.service.impl.location.RemoteLocation.Status;


/**
 * Used to manipulate resource in the local
 * @author julius.yu@thenetcircle.com
 */
public class LocalLocation implements Location
{
  private final Path path;
  private final String pathAsStr;
  private static final Logger LOG=LoggerFactory.getLogger(LocalLocation.class);
  private int defaultBufferSize=819200;
  private int maxBufferSize = 1024000;

  public LocalLocation(final Path path)
  {
    this.path=path;
    this.pathAsStr=path.toString();
  }

  @Override
  public Location resolve(final String location)
  {
    return new LocalLocation(path.resolve(location));
  }

  @Override
  public Status isExist()
  {
    return Status.get(path.toFile().exists());
  }

  @Override
  public Status isDirectory()
  {
    return Status.get(path.toFile().isDirectory());
  }

  @Override
  public List<Location> list()
  {
    final String[] files=path.toFile().list();

    if(files==null)
    {
      throw Oops.causedBy(
        "{} has no files, may be it is not a directory or encountered exception",
        pathAsStr);
    }

    final int length=files.length;

    final List<Location> locations=new ArrayList<Location>(length);
    for(String file : files)
    {
      locations.add(new LocalLocation(path.resolve(file)));
    }
    return locations;
  }

  @Override
  public Status delete()
  {
    Boolean isDeleted=null;
    final File file=path.toFile();

    if(file.exists())
    {
      deleteDir(file);
      isDeleted=true;
    }
    return Status.get(isDeleted);

  }

  private boolean deleteDir(final File dir)
  {
    if(dir.isDirectory())
    {
      final String[] children=dir.list();
      for(int i=0; i<children.length; i++)
      {
        if(!deleteDir(new File(dir, children[i])))
        {
          return false;
        }
      }
    }
    return dir.delete();
  }

  @Override
  public Status mkdirs()
  {
    return Status.get(path.toFile().mkdirs());
  }


  @Override
  public InputStream read()
  {
    try
    {
      final InputStream input=new FileInputStream(path.toFile());
      final int size=input.available();
      return new BufferedInputStream(input, size);
    }
    catch(Exception e)
    {
      throw Oops.causedBy(e, "{} is not found", pathAsStr);
    }
  }

  @Override
  public Status write(final InputStream input, final int length)
  {
    Boolean isWritten=false;
    try
    {
      if(isExist()==Status.No)
      {
        final File parent=path.toFile().getParentFile();
        if(!parent.exists())
        {
          parent.mkdirs();
        }
      }
      BufferedOutputStream out=new BufferedOutputStream(
        new FileOutputStream(pathAsStr));
      final byte[] buf=new byte[getBufferSize(length)];
      int len;
      while((len=input.read(buf))>0)
      {
        out.write(buf, 0, len);
      }
      out.close();
      isWritten=true;
      input.close();
    }
    catch(IOException e)
    {
      LOG.warn("event:write local file,result:failed,detail: io exception {}", e.getMessage());
      throw Oops.causedBy(e, "writing file at {}", pathAsStr);
    }

    return Status.get(isWritten);
  }
  
  private int getBufferSize(final int length){
    if(length<defaultBufferSize){
      return length;
    }
    return maxBufferSize;
  }
  
  @Override
  public boolean equals(final Object location)
  {
    if(this==location)
    {
      return true;
    }

    if(location==null||location.getClass()!=this.getClass())
    {
      return false;
    }
    return path.equals(((LocalLocation)location).path);
  }

  @Override
  public int hashCode()
  {
    int hash=7;
    hash=31*hash+(null==path?0:path.hashCode());
    return hash;
  }

  @Override
  public String toString()
  {
    return pathAsStr;
  }

  @Override
  public Object get()
  {
    return path.toFile();
  }

}
