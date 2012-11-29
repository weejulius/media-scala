package com.thenetcircle.services.common;


public final class FileNames
{
  private FileNames()
  {
  }

  /**
   * File name if the path has
   * runtime exception if last entry is not file
   * @param filePath
   * @return
   */
  public static String getName(final String resource)
  {
    int startPosition=resource.lastIndexOf('/');
    if(startPosition<0)
    {
      startPosition=resource.lastIndexOf('\\');
    }

    if(startPosition==-1||startPosition==resource.length()-1)
    {
      throw Oops.causedBy("{} has no file name", resource);
    }

    return resource.substring(startPosition+1);

  }

  /**
   * extension if the file has
   * blank if not
   * @param fileName
   * @return
   */
  public static String extension(final String fileName)
  {
    String result="";
    if(fileName!=null)
    {
      final int lastDot=fileName.lastIndexOf('.');
      if(lastDot>0)
      {
        result=fileName.substring(lastDot+1);
      }
    }
    return result;
  }

  /*
   * Last entry of the resource, it might be directory or file
   */
  public static String lastEntry(final String resource)
  {
    String result="";
    int startPosition=resource.lastIndexOf('/');
    if(startPosition<0)
    {
      startPosition=resource.lastIndexOf('\\');
    }

    if(startPosition==resource.length()-1)
    {
      result=lastEntry(resource.substring(0, resource.length()-1));
    }
    else if(startPosition==-1)
    {
      throw Oops.causedBy("{} has no entry at all", resource);
    }
    else
    {
      result=resource.substring(startPosition+1);
    }

    return result;

  }

  /**
   * Append strings to the name, for example append 'abc' to 'afile.txt', and then the file name is
   * to be 'afileabc.txt', if there is no extension the 'abc' will be follow the file name directly.
   * This operation does not guarantee the file name appended is valid.
   * @param fileName
   * @param append
   * @return
   */
  public static String appendToName(final String fileName, final String append)
  {
    String result="";
    final int lastDot=fileName.lastIndexOf('.');
    if(lastDot<0)
    {
      result=fileName+append;
    }
    else
    {
      result=fileName.substring(0, lastDot)+append+fileName.substring(lastDot);
    }
    return result;
  }
}
