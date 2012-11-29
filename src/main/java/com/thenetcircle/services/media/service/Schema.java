package com.thenetcircle.services.media.service;


import com.thenetcircle.services.media.service.impl.storage.StoreListener;
import com.thenetcircle.services.media.service.impl.transform.Transform;

import java.util.List;


/**
 * Used to define transforms can be reused giving a name
 */
public class Schema
{
  public Schema()
  {
    super();
  }

  public String name;
  public List<Transform> transforms;
  /*
   * This listener can override the image dispatch request's store listener
   */
  public StoreListener storeListener;

}
