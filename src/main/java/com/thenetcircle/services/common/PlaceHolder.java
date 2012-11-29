package com.thenetcircle.services.common;


import java.util.HashMap;
import java.util.Map;


/**
 * Used to replace the holders with values in the expression
 * @author julius.yu@thenetcircle.com
 */
public final class PlaceHolder
{
  private final String delimiterHead, delimiterTail;
  private final int lengthOfDelimiterHead;
  private static final Map<String, PlaceHolder> PLACEHOLDERS=new HashMap<String, PlaceHolder>();

  private PlaceHolder(final String aDelimiterHead, final String aDelimiterTail)
  {
    if(aDelimiterHead==null||aDelimiterTail==null)
    {
      throw Oops.causedBy(
        "initializing place holder however delimiter head or tail is null");
    }

    lengthOfDelimiterHead=aDelimiterHead.length();
    this.delimiterTail=aDelimiterTail;
    this.delimiterHead=aDelimiterHead;
  }

  public static PlaceHolder get(final String aDelimiterHead, final String aDelimiterTail)
  {
    final String id=generateID(aDelimiterHead, aDelimiterTail);
    PlaceHolder result=PLACEHOLDERS.get(id);
    if(result==null)
    {
      result=new PlaceHolder(aDelimiterHead, aDelimiterTail);
      PLACEHOLDERS.put(id, result);
    }
    return result;
  }

  private static String generateID(final String aDelimiterHead, final String aDelimiterTail)
  {
    return aDelimiterHead+"0"+aDelimiterTail;
  }

  public String replace(final String str, final Values values)
  {

    int tailIndex=str.indexOf(delimiterTail);

    if(tailIndex<0)
    {
      return str;
    }

    final StringBuffer result=new StringBuffer();

    int headIndex=0;
    while(tailIndex>=0)
    {
      final String head=str.substring(headIndex, tailIndex);
      final int currentHeadIndex=head.lastIndexOf(delimiterHead);
      if(currentHeadIndex>=0)
      {
        final String variableName=head.substring(currentHeadIndex+lengthOfDelimiterHead);
        result.append(head.substring(0, currentHeadIndex)).append(values.get(variableName));
      }
      else
      {
        result.append(str.substring(headIndex, tailIndex+1));
      }
      headIndex=tailIndex+1;
      tailIndex=str.indexOf(delimiterTail, tailIndex+1);
    }
    result.append(str.substring(headIndex));
    return result.toString();
  }
  public String replace(final String str, final Map<String, String> values)
  {
    return replace(str, new Values() {

      @Override
      public String get(final String variableName)
      {
        final String value=values.get(variableName);
        if(value==null)
        {
          throw Oops.causedBy(
            "replacing holders however the value of variable "+variableName+" is not found");
        }
        return value;
      }

      @Override
      public boolean isEmpty()
      {
        return values==null||values.isEmpty();
      }

    });
  }

  public String replace(final String str, final String... values)
  {
    return replace(str, new Values() {

      private int index=0;
      private int length=values.length;

      @Override
      public String get(final String variableName)
      {
        if(index>=length)
        {
          throw Oops.causedBy(
            "replacing holders however the number of values is less than the holders");
        }
        return values[index++];
      }

      @Override
      public boolean isEmpty()
      {
        return values==null||values.length==0;
      }

    });

  }


  public interface Values
  {

    String get(String variableName);

    boolean isEmpty();
  }
}
