package com.thenetcircle.services.media.entity;


/**
 * A raw to present the schema,like a json raw is used to be parsed to be a schema.
 */

public class SchemaRaw
{

  private String name;

  private String raw;

  protected SchemaRaw()
  {
    super();
  }

  public SchemaRaw(final String name, final String schemaRawAsStr)
  {
    this.name=name;
    this.raw=schemaRawAsStr;
  }

  public String getRaw()
  {
    return raw;
  }

  public void setRaw(final String schemaRaw)
  {
    this.raw=schemaRaw;
  }

  public String getName()
  {
    return name;
  }

  public void setName(final String name)
  {
    this.name=name;
  }
}
