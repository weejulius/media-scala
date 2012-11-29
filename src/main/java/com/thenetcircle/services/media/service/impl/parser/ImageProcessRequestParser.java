package com.thenetcircle.services.media.service.impl.parser;


import com.fasterxml.jackson.databind.JsonNode;
import com.thenetcircle.services.common.Jsons;
import com.thenetcircle.services.common.Oops;
import com.thenetcircle.services.common.PlaceHolder;
import com.thenetcircle.services.media.service.RequestParsing;
import com.thenetcircle.services.media.service.impl.imageprocess.ImageProcessRequest;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;


/*
 * A json deserializer for image dispatch request (a.k.a IPR)
 */
public class ImageProcessRequestParser implements RequestParsing
{
  /*
   * The variable values for all the schemas are placed here
   */
  private final Map<String, Map<String, String>> variableValues=new HashMap
    <String, Map<String, String>>();

  private Map<String, String> currentSchemaVariables;
  private static final PlaceHolder PLACEHOLDER=PlaceHolder.get("${", "}");


  public ImageProcessRequest parse(final JsonNode root)
  {
    //final JsonNode root = Jsons.read(request);
    final ImageProcessRequest ipr=new ImageProcessRequest();
    
    return decode(root, ipr);
  }

  
  /**
   * refactory by fan@thenetcircle.com
   * <p>in order to keep code consistent and 
   * <p>allow other parts to have chance to enhande the ImageProcessRequest
	 * @param root
	 * @param ipr
	 * @return
	 */
  public ImageProcessRequest decode(final JsonNode root, final ImageProcessRequest ipr) {
    ipr.origin=value(Jsons.mandatoryField(root, Field.ORIGIN.toString()));

    final JsonNode processesNodes=root.get(Field.PROCESSES.toString());

    if(processesNodes==null)
    {
      ipr.processes.add(new ImageProcessDeserializer(this).deserialize(root));
    }
    else
    {
      final Iterator<JsonNode> processNodes=processesNodes.elements();
      while(processNodes.hasNext())
      {
        ipr.processes.add(new ImageProcessDeserializer(this).deserialize(processNodes.next()));
      }
    }

    final JsonNode callbackNode=root.get("callback");

    if(callbackNode!=null)
    {
      ipr.populateCallbackURL(value(callbackNode));
    }

    return ipr;
  }


  enum Field
  {
    ORIGIN("origin"),
    PROCESSES("processes"),
    SCHEMA("schema"),
    OPTIONS("options"),
    VARIABLES("variables"),
    DESTINATION("destination"),
    TRANSFORMS("transforms"),
    CALLBACK("callback"),
    
    //added by fan@thenetcircle.com
    //to get webdav account from the json
    USERNAME("userName"),
    PASSWORD("password");

    private String name;

    Field(final String aName)
    {
      this.name=aName;
    }

    public String toString()
    {
      return name;
    }
  }

  public String value(final JsonNode node)
  {
    return replaceVariables(node.asText());
  }

  public void initVariableValues(final String schemaName, final JsonNode root)
  {
    final JsonNode variables=root.get(Field.VARIABLES.toString());
    if(variables!=null)
    {
      final Map<String, String> variableValuesOfSchema=new HashMap<String, String>();
      final Iterator<Entry<String, JsonNode>> fields=variables.fields();
      while(fields.hasNext())
      {
        final Entry<String, JsonNode> field=fields.next();
        variableValuesOfSchema.put(field.getKey(), convertValueToStr(field.getValue()));
      }
      variableValues.put(schemaName, variableValuesOfSchema);
    }
  }

  private String convertValueToStr(final JsonNode jsonNode){
    if(jsonNode.isObject()){
      throw Oops.causedBy("the replacement of variable should not be object like {}",jsonNode.toString());
    }
    if(jsonNode.isArray()){
      return jsonNode.toString();
    }
    return jsonNode.asText();
  }


  public void prepareSchemaHolderPlaces(final String name)
  {
    currentSchemaVariables=variableValues.get(name);
  }
  private String replaceVariables(final String str)
  {
    if(currentSchemaVariables==null)
    {
      currentSchemaVariables=new HashMap<String, String>();
    }
    return PLACEHOLDER.replace(str, currentSchemaVariables);
  }


}
