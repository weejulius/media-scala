package com.thenetcircle.services.common;


import com.thenetcircle.services.media.service.impl.FeedbackType;

import java.util.ArrayList;
import java.util.List;


/**
 * Used to hold information as a feedback return to the user
 * @author julius.yu@thenetcircle.com
 */
public final class Feedbacks
{
  private Feedbacks()
  {
  }

  private static final PlaceHolder PLACEHOLDER=PlaceHolder.get("{", "}");

  public static Feedback when(final String event, final String... args)
  {
    return new Feedback(PLACEHOLDER.replace(event, args));
  }


  public static class Feedback
  {
    public String event;
    public FeedbackType type=FeedbackType.OK;
    public String cause;
    public Object content;
    public List<String> messages;

    public Feedback(final String event)
    {
      this.event=event;
    }

    public Feedback logFailure(final Exception exception)
    {
      cause=exception.getMessage();
      type=FeedbackType.ServerError;
      if(exception instanceof Oops)
      {
        final Oops oops=(Oops)exception;
        if(oops.type!=null)
        {
          type=oops.type;
        }
      }
      return this;
    }

    public String toString()
    {
      String result="";
      if(content==null)
      {
        result=Jsons.toString(this);
      }
      else
      {
        result=Jsons.toString(content);
      }
      return result;
    }

    public Feedback setContent(final Object str)
    {
      this.content=str;
      return this;
    }

    public Feedback addMessage(final String message)
    {
      if(messages==null)
      {
        messages=new ArrayList<String>();
      }
      messages.add(message);
      return this;
    }

    public void andThen(final Feedback nextFeedback)
    {
      event+=" => "+nextFeedback.event;
      cause=nextFeedback.cause;
      content=nextFeedback.content;
      messages.addAll(nextFeedback.messages);
    }

    public Feedback noContent()
    {
      type=FeedbackType.NoContent;
      return this;
    }
  }


}
