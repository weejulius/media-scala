package com.thenetcircle.services.common;


import com.thenetcircle.services.media.service.impl.location.LocalLocation;
import com.thenetcircle.services.media.service.impl.location.RemoteLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Locale;


public final class Locations
{
  private static Path rootPath;
  private static final Logger LOG=LoggerFactory.getLogger(Locations.class);
  private static boolean isBundleLocation=false;

  private Locations()
  {
  }

  static
  {
    ClassLoader classLoader=Thread.currentThread().getContextClassLoader();
    if(classLoader==null)
    {
      classLoader=Locations.class.getClassLoader();
    }

    final URL url=classLoader.getResource("");

    if(url!=null)
    {
      if(url.getProtocol().equals("bundleresource"))
      {
        isBundleLocation=true;
        LOG.info("the locations are bundle resources , please set the relative root path manually");
      }
      else
      {
        //TODO the url.getFile might return /C:/a/b/c in windows, so we need to remove the prefix char '/'
        rootPath=Paths.get(url.getFile());
        LOG.info("the locations are relative to the path "+rootPath);
      }
    }
  }

  public static void setRelativeRootPath(final Path relativeRootPath)
  {
    rootPath=relativeRootPath;
    LOG.debug("the root path of bundle is set to "+rootPath);
  }

  //added by fan@thenetcircle.com
  //for using the webdav account from the request
	public static Location get(final String aResource, String userName, String password) {
		if (!Strings.isNullOrBlank(aResource) && isHttpProtocol(aResource)) {
			try {
				return new RemoteLocation(new URL(aResource.trim()), userName,
						password);
			} catch (Exception e) {
				throw Oops
						.causedBy(e, "unrecognized url location {}", aResource);
			}
		}
		return get(aResource);
	}
  
  public static Location get(final String aResource)
  {
    Location result;
    final String resource=aResource.trim();

    if(isHttpProtocol(resource))
    {
      try
      {
        result=new RemoteLocation(new URL(resource));
      }
      catch(Exception e)
      {
        throw Oops.causedBy(e,
          "unrecognized url location {}", resource);
      }
    }
    else if(isRelativePath(resource))
    {
      if(isBundleLocation&&rootPath==null)
      {
        throw Oops.causedBy(
          "locating the bundle resource {}, however the root path is not set", aResource);
      }
      result=new LocalLocation(rootPath.resolve(resource));
    }
    else
    {
      result=new LocalLocation(Paths.get(resource));
    }

    return result;
  }

  private static boolean isHttpProtocol(final String resource)
  {
    return resource.toLowerCase(Locale.ENGLISH).startsWith("http://")||
      resource.toLowerCase(Locale.ENGLISH).startsWith("https://");
  }

  private static boolean isRelativePath(final String resource)
  {
    return !('/'==resource.charAt(0))&&!resource.contains(":");
  }
}
