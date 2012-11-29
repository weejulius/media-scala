package com.thenetcircle.services.media.service.impl;


public enum FeedbackType
{
  NotFound("not found"),
  ServerError("server error"),
  BadRequest("bad request"),
  OK("ok"),
  CreateExistingEntity("create an existing object"),
  NoContent("ok without content"),
  Updated("object has been updated"),
  Created("object has been created"),
  Accepted("accepted but unimplemented");

  public String description;

  private FeedbackType(final String des)
  {
    this.description=des;
  }
}
