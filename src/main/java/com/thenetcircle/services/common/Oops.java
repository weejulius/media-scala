package com.thenetcircle.services.common;


import com.thenetcircle.services.media.service.impl.FeedbackType;

import java.util.ArrayList;
import java.util.List;


public class Oops extends RuntimeException
{
  private static final long serialVersionUID=-6108680584951792087L;
  private static final PlaceHolder PLACEHOLDER=PlaceHolder.get("{", "}");
  public Throwable cause;
  public FeedbackType type;
  public List<String> events;

  public static Oops causedBy(final String event, final String... params)
  {
    return causedBy(null, event, params);
  }

  public static Oops causedBy(final Throwable cause, final String event, final String... parms)
  {
    final Oops oops=new Oops();
    oops.cause=cause;
    oops.events=new ArrayList<String>();
    oops.events.add(PLACEHOLDER.replace(event, parms));
    return oops;
  }

  public Throwable getCause()
  {
    return cause;
  }

  public String getMessage()
  {
    return printEvents();
  }

  private String printEvents()
  {
    final int length=events.size();
    final StringBuffer result=new StringBuffer();
    for(int i=0; i<length-2; i++)
    {
      result.append(events.get(i)).append(" => ");
    }

    result.append(events.get(length-1));

    if(cause!=null)
    {
      result.append(" => ");
      if(cause.getMessage()==null)
      {
        result.append("Unknown cause");
      }
      else
      {
        result.append(cause.getClass().getName()).append(":").append(cause.getMessage
          ());
      }
    }


    return result.toString();
  }

  public Oops when(final String event)
  {
    events.add(event);
    return this;
  }

  public static Oops asCreateExistingEntity(final String event, final String... params)
  {
    final Oops oops=causedBy(event, params);
    oops.type=FeedbackType.CreateExistingEntity;
    return oops;
  }

  public static Oops asBadRequest(final String event, final String... params)
  {
    final Oops oops=causedBy(event, params);
    oops.type=FeedbackType.BadRequest;
    return oops;
  }

  public static Oops asNotFound(final String event, final String... params)
  {
    final Oops oops=causedBy(event, params);
    oops.type=FeedbackType.NotFound;
    return oops;
  }
}
