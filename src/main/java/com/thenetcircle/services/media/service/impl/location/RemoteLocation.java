package com.thenetcircle.services.media.service.impl.location;


import com.googlecode.sardine.DavResource;
import com.googlecode.sardine.Sardine;
import com.googlecode.sardine.SardineFactory;
import com.googlecode.sardine.impl.SardineImpl;
import com.thenetcircle.services.common.Location;
import com.thenetcircle.services.common.Oops;
import com.thenetcircle.services.media.service.impl.Settings;
import org.apache.http.entity.InputStreamEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;


/*
 * Manipulate the remote resource by http or webdav
 */
public class RemoteLocation implements Location
{
  private final URL url;
  private final String urlAsStr;
  private Boolean isExist;

  //added by fan@thenetcircle.com
  //for webdav account from request
  //but however this isn't a nice design
  //next time, if more settings required for webdav are needed to set
  //we can't just add, have this class to bear the functions for sardine
  private String userName;
  private String password;

  private static final Logger LOG=LoggerFactory.getLogger(RemoteLocation.class);


  public RemoteLocation(final URL aUrl)
  {
    url=aUrl;
    urlAsStr=url.toString();
  }

  public RemoteLocation(URL aUrl, String userName, String password)
  {
    this(aUrl);
    this.userName=userName;
    this.password=password;
  }

  @Override
  public Location resolve(final String location)
  {
    try
    {
      return new RemoteLocation(new URL(url, location));
    }
    catch(MalformedURLException e)
    {
      throw Oops.causedBy(e, "malformed url {}", urlAsStr);
    }
  }


  public enum Status
  {
    Yes,
    No,
    Unknown;

    public static Status get(final Boolean isExist)
    {
      if(isExist==null)
        return Status.Unknown;
      if(isExist)
        return Status.Yes;
      if(!isExist)
        return Status.No;
      return Unknown;
    }
  }

  @Override
  public Status isExist()
  {
    if(isExist==null)
    {
      try
      {
        final HttpURLConnection conn=(HttpURLConnection)url.openConnection();
        conn.setRequestMethod("HEAD");
        conn.setConnectTimeout(Settings.connectionTimeOut);
        conn.connect();
        isExist=conn.getResponseCode()==HttpURLConnection.HTTP_OK;
        conn.disconnect();
      }
      catch(IOException e)
      {
        LOG.warn("{} is not existing due to {}", urlAsStr, e.getMessage());
        Oops.causedBy(e, "checking the url {} is existing", urlAsStr);
      }
    }
    return Status.get(isExist);
  }

  @Override
  public Status isDirectory()
  {

    Boolean isDirectory=null;
    try
    {
      final Sardine sardine=begin();
      final List<DavResource> resources=sardine.list(url.toString());
      isDirectory=resources.size()==1&&resources.get(0).isDirectory();
    }
    catch(Exception e)
    {
      LOG.warn("event: is directory {},result:failed,detail:{}", urlAsStr, e.getMessage());
      Oops.causedBy(e, "checking the url {} is directory", urlAsStr);
    }
    return Status.get(isDirectory);

  }

  @Override
  public List<Location> list()
  {
    try
    {
      final Sardine sardine=begin();
      final List<DavResource> resources=sardine.list(url.toString());
      final List<Location> locations=new ArrayList<Location>(resources.size());
      for(DavResource resource : resources)
      {
        locations.add(new RemoteLocation(resource.getHref().toURL()));
      }
      return locations;
    }
    catch(Exception e)
    {
      LOG.warn("event: list directory {},result:failed,detail:{}", urlAsStr, e.getMessage());
      return new ArrayList<Location>(0);
    }
  }

  @Override
  public Status delete()
  {

    Boolean isDeleted=false;
    try
    {
      final Sardine sardine=begin();
      sardine.delete(url.toString());
      isDeleted=true;
    }
    catch(Exception e)
    {
      LOG.warn("failed to delete resource {} due to {}", urlAsStr, e.getMessage());
    }
    return Status.get(isDeleted);
  }

  @Override
  public Status mkdirs()
  {
    return Status.No;
  }

  private String createParentDirs() throws IOException
  {
    final int endIndexOfHost=urlAsStr.indexOf('/', 8);
    final String host=urlAsStr.substring(0, endIndexOfHost);
    final String[] paths=urlAsStr.substring(endIndexOfHost+1).split("/");
    final StringBuffer currentPath=new StringBuffer(host);
    final Sardine sardine=begin();
    for(int i=0; i<paths.length-1; i++)
    {
      currentPath.append('/').append(paths[i]);
      final String currentPathAsStr=currentPath.toString();
      final Status isCurrentPathExist=new RemoteLocation(new URL(currentPathAsStr)).isExist();
      switch(isCurrentPathExist)
      {
        case No:
          try
          {
            sardine.createDirectory(currentPath.toString());
            break;
          }
          catch(Exception e)
          {
            LOG.debug("failed to creating directory "+currentPathAsStr, e);
            throw new IllegalArgumentException("the url "+url.toString()+
              " cannot be manipulated even we try to create the missing directory "+paths[i], e);
          }
        case Yes:
          break;
        default:
          throw new IllegalArgumentException("the parent dirs cannot be created as we do not know the status of path "+currentPathAsStr);
      }
    }
    return currentPath.toString();
  }

  @Override
  public InputStream read()
  {
    try
    {
      final Sardine sardine=begin();
      return sardine.get(url.toString());
    }
    catch(Exception e)
    {
      throw Oops.causedBy(e, "reading url {} however it is not accessiable", urlAsStr);
    }
  }

  /**
   * write input stream by webdav
   * retry if any error
   * make parent directories if they are not existing
   */
  @Override
  public Status write(final InputStream inputStream, final int length)
  {
    boolean isWritten=false;
    Sardine sardine=null;
    try
    {
      sardine=begin();
      putToWebDav(sardine, inputStream, length);
      isWritten=true;
    }
    catch(Exception e)
    {
      LOG.debug("failed to put to webdav at first time", e);
      try
      {
        switch(isExist())
        {
          case Yes:
            putToWebDav(sardine, inputStream, length);
            break;
          case No:
            createParentDirs();
            putToWebDav(sardine, inputStream, length);
            break;
          default:
            break;
        }
      }
      catch(Exception e1)
      {
        throw Oops.causedBy(e1,
          "writing inputstream to {} however it is failed even tried twice", urlAsStr);
      }

    }
    return Status.get(isWritten);

  }

  private void putToWebDav(final Sardine sardine, final InputStream inputStream, final int length)
    throws IOException
  {
    LOG.debug("puting stream to webdav");
    // A length of -1 means "go until end of stream"
    InputStreamEntity entity=new InputStreamEntity(inputStream, length);
    ((SardineImpl)sardine).put(url.toString(), entity, null, true);
    LOG.debug("end up putting stream to webdav");
  }

  @Override
  public boolean equals(final Object location)
  {
    if(this==location)
      return true;

    if(location==null||location.getClass()!=this.getClass())
    {
      return false;
    }
    return url.equals(((RemoteLocation)location).url);
  }
  @Override
  public int hashCode()
  {
    int hash=7;
    hash=31*hash+(null==url?0:url.hashCode());
    return hash;
  }

  @Override
  public Object get()
  {
    return url;
  }

  @Override
  public String toString()
  {
    return url.toString();
  }


  private Sardine begin()
  {
    if(isBlank(password)||isBlank(userName))
    {
      return SardineFactory.begin(Settings.webDavUserName, Settings.webDavPassword);
    }
    return SardineFactory.begin(userName, password);
  }


  private boolean isBlank(final String str){
      return str==null || str.isEmpty();
  }


}
