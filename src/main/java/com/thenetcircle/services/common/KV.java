package com.thenetcircle.services.common;


/*
 * A data structure in the format <key,value>
 */
public class KV<K, V>
{
  public K key;
  public V value;

  public KV(final K key, final V value)
  {
    this.key=key;
    this.value=value;
  }

  public static <K, V> KV<K, V> a(final K key, final V value)
  {
    return new KV<K, V>(key, value);
  }


  public static KV<?, ?>[] some(final Object... objs)
  {
    final int length=objs.length;
    if(length%2!=0)
    {
      throw Oops.causedBy("failed to create multiple key-value pair "+
        "due to the size of parameters is not odd");
    }
    final int sizeOfKV=length/2;
    final KV<?, ?>[] kvs=new KV[sizeOfKV];
    for(int i=0; i<length; i=i+2)
    {
      kvs[i/2]=new KV<>(objs[i], objs[i+1]);
    }
    return kvs;
  }

  public static KV<?, ?>[] append(final KV<?, ?>[] kvs, final Object key, final Object value)
  {
    final KV<?, ?>[] appendedKVs=new KV<?, ?>[kvs.length+1];
    System.arraycopy(kvs, 0, appendedKVs, 0, kvs.length);
    appendedKVs[kvs.length]=KV.a(key, value);
    return appendedKVs;
  }

  public String toString()
  {
    return key+":"+value;
  }
}
