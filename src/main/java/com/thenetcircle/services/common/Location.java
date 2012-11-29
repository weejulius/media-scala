package com.thenetcircle.services.common;


import com.thenetcircle.services.media.service.impl.location.RemoteLocation.Status;

import java.io.InputStream;
import java.util.List;


/**
 * Used to unify the interface to operate files for webdav and local file system and so on
 * @author julius.yu@thenetcircle.com
 */
public interface Location
{
  /**
   * Get a new location relative to the current location
   * For example location1.resolve(entry) will get a location location1/entry
   */
  Location resolve(final String location);

  /**
   * Status.Yes if the location is existing
   * Status.No if the location is not existing
   * Status.Unknown if it is not able to check the existing of the location
   */
  Status isExist();

  /**
   * Status.Yes if the resource is directory which might contain the other resources
   * @return
   */
  Status isDirectory();

  /**
   * return a list of location under the current location if the location is directory
   * throw exception if it is not directory
   * @return
   */
  List<Location> list();

  /**
   * delete the current location recursively, it will delete all the sub locations
   * @return
   */
  Status delete();

  /**
   * make directories if the directories is not existing
   * if all of a,b,c is not existing, the location a/b/c will create directories a,b,c respectively
   * invoking mkdirs
   * @return
   */
  Status mkdirs();

  /**
   * read the location if it is existing
   * throw runtime exception if the location is not existing or readable
   * @return
   */
  InputStream read();

  /**
   * write an input stream into the location,if it is not directory
   * @param imageInputStream
   * @return
   */
  Status write(InputStream imageInputStream, int length);

  /**
   * return url if it is remote location otherwise a file
   * @return
   */
  Object get();

}
