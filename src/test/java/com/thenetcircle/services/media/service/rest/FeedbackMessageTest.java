package com.thenetcircle.services.media.service.rest;


import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;
import com.thenetcircle.services.common.Feedbacks;


public class FeedbackMessageTest
{

  @Test
  public void testFeedback()
  {
    Assert.assertThat(Feedbacks.when("save schema {} to {}", "schema1", "Pop").event,
      CoreMatchers.equalTo("save schema schema1 to Pop"));
  }


}
