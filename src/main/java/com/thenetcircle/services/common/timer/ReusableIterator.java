package com.thenetcircle.services.common.timer;

import java.util.Iterator;

/**
 * User: julius.yu
 * Date: 8/24/12
 */
public interface ReusableIterator<E> extends Iterator<E> {
  void rewind();
}