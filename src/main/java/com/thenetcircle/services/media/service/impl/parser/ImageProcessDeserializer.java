package com.thenetcircle.services.media.service.impl.parser;


import com.fasterxml.jackson.databind.JsonNode;
import com.thenetcircle.services.common.Oops;
import com.thenetcircle.services.media.service.Schema;
import com.thenetcircle.services.media.service.impl.area.Area.AreaBuilder;
import com.thenetcircle.services.media.service.impl.area.Size.SizeBuilder;
import com.thenetcircle.services.media.service.impl.imageprocess.ImageProcess;
import com.thenetcircle.services.media.service.impl.parser.ImageProcessRequestParser.Field;
import com.thenetcircle.services.media.service.impl.storage.QualityStoreListener;
import com.thenetcircle.services.media.service.impl.storage.StoreListener;
import com.thenetcircle.services.media.service.impl.transform.Images;
import com.thenetcircle.services.media.service.impl.transform.Transform;
import com.thenetcircle.services.media.service.impl.transform.processing.CropTransform;
import com.thenetcircle.services.media.service.impl.transform.processing.OverlayImageTransform;
import com.thenetcircle.services.media.service.impl.transform.processing.OverlayTextTransform;
import com.thenetcircle.services.media.service.impl.transform.processing.RotateImageTransform;
import com.thenetcircle.services.media.service.impl.transform.processing.ScaleTransform;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;


public class ImageProcessDeserializer {
  private ImageProcessRequestParser iprDeser;

  public ImageProcessDeserializer(final ImageProcessRequestParser iprDeser) {
    this.iprDeser = iprDeser;
  }

  public ImageProcess deserialize(final JsonNode root) {
    final ImageProcess imageProcess = new ImageProcess();
    final Iterator<Entry<String, JsonNode>> nodes = root.fields();

    while (nodes.hasNext()) {
      final Entry<String, JsonNode> node = nodes.next();
      final String fieldName = node.getKey();
      final JsonNode value = node.getValue();

      switch (getEnumByName(fieldName)) {
        case SCHEMA:
          final String schemaName = value(value);
          iprDeser.initVariableValues(schemaName, root);
          imageProcess.schema = getSchema(value(value));
          break;
        case TRANSFORMS:
          final Schema schema = new Schema();
          schema.transforms = new TransformsJsonDeserializer().deserialize(value);
          imageProcess.schema = schema;
          break;
        case OPTIONS:
          imageProcess.storeListener = new OptionsJsonDeserializer().deserialize(value);
          break;
        case DESTINATION:
          imageProcess.destination = value(value);
          break;
        case USERNAME:
          imageProcess.userName = value(value);
          break;
        case PASSWORD:
          imageProcess.password = value(value);
          break;
        default:
          break;
      }
    }
    return imageProcess;
  }

  private Field getEnumByName(final String name) {
    try {
      return Field.valueOf(name.toUpperCase(Locale.ENGLISH));
    } catch (IllegalArgumentException e) {
      throw Oops.causedBy("unrecognized element '{}'", name);
    }
  }

  private String value(final JsonNode value) {
    return iprDeser.value(value);
  }


  enum Parameter {
    PADDING("padding"),
    SIZE("size"),
    HEIGHT("height"),
    WIDTH("width"),
    LEFT_TOP("left_top"),
    RIGHT_BOTTOM("right_bottom"),
    IMAGE("image"),
    TEXT("text"),

    //added by fan@thenetcircle.com for image rotation
    DEGREE("degree"),

    OPACITY("opacity");

    private String name;

    Parameter(final String aName) {
      this.name = aName;
    }

    public String toString() {
      return name;
    }
  }


  public Schema getSchema(final String name) {
    try {
      iprDeser.prepareSchemaHolderPlaces(name);
      return new SchemaJsonDeserializer().deserialize(Schemas.getSchema(name));
    } catch (Exception e) {
      throw Oops.causedBy(e, "deserializing the schema {}", name);
    }
  }


  public class OptionsJsonDeserializer {
    public StoreListener deserialize(final JsonNode optionsNode) {
      StoreListener option = null;
      final Iterator<Entry<String, JsonNode>> nodeIterator = optionsNode.fields();

      while (nodeIterator.hasNext()) {
        final Entry<String, JsonNode> currentOption = nodeIterator.next();

        final String optionName = currentOption.getKey().trim();

        if ("quality".equalsIgnoreCase(optionName)) {
          option = new QualityStoreListener(pureDecimalValue(currentOption.getValue()));
          break;
        }
        throw Oops.causedBy("unrecognized element '{}'", optionName);
      }
      return option;
    }
  }


  public class TransformsJsonDeserializer {

    public List<Transform> deserialize(final JsonNode transformsNode) {
      final List<Transform> transforms = new ArrayList<Transform>();
      final Iterator<JsonNode> nodeIterator = transformsNode.elements();

      while (nodeIterator.hasNext()) {
        final JsonNode transformNode = nodeIterator.next();
        final Iterator<Entry<String, JsonNode>> transformFields = transformNode.fields();

        final String transformType = transformFields.next().getValue().asText().trim();

        final AreaBuilder builder = parseAndInitAreaBuilder(transformNode);

        switch (transformType.toLowerCase(Locale.ENGLISH)) {
          case "crop":
            transforms.add(new CropTransform(builder));
            break;
          case "scale":
            transforms.add(new ScaleTransform(builder));
            break;
          case "overlay":
            transforms.add(new OverlayTextTransform(builder,
                                                    getTextOfField(transformNode, Parameter.TEXT)));
            break;
          case "overlay image":
            transforms.add(new OverlayImageTransform(builder,
                                                     Images.loadFromCache(getTextOfField(transformNode,
                                                                                         Parameter.IMAGE)),
                                                     getPossibleFloat(transformNode, Parameter.OPACITY)));
            break;

          //added by fan@thenetcircle.com
          case "rotate":
            int degree = transformNode.get("degree").asInt();
            transforms.add(new RotateImageTransform(degree));
            break;
          default:
            throw Oops.causedBy("unrecognized json node ${}", transformType);
        }
      }
      return transforms;
    }

    private AreaBuilder parseAndInitAreaBuilder(final JsonNode node) {
      final AreaBuilder builder = new AreaBuilder();
      boolean hasPadding = false;

      if (node.get(Parameter.PADDING.toString()) != null) {
        builder.paddingBuillder.padding(asFloatArray(node.get(Parameter.PADDING.toString())));
        hasPadding = true;
      }

      final boolean hasSizeParameter = parseAndInitSize(node, builder.sizeBuilder);

      if (hasPadding && hasSizeParameter) {
        throw Oops.causedBy("padding should not be used along with width/height/size");
      }

      if (node.get(Parameter.LEFT_TOP.toString()) != null) {
        final Float[] paddingParameters = asFloatArray(node.get(Parameter.LEFT_TOP.toString()));
        builder.paddingBuillder.left(paddingParameters[0]).top(paddingParameters[1]);
      }
      if (node.get(Parameter.RIGHT_BOTTOM.toString()) != null) {
        final Float[] paddingParameters = asFloatArray(node.get(Parameter.RIGHT_BOTTOM.toString()));
        builder.paddingBuillder.right(paddingParameters[0]).bottom(paddingParameters[1]);
      }
      return builder;
    }
  }


  public class SchemaJsonDeserializer {
    public Schema deserialize(final JsonNode root) {
      final Schema schema = new Schema();
      final Iterator<Entry<String, JsonNode>> nodes = root.fields();
      while (nodes.hasNext()) {
        final Entry<String, JsonNode> node = nodes.next();
        final String fieldName = node.getKey();
        final JsonNode child = node.getValue();

        switch (getEnumByName(fieldName)) {
          case SCHEMA:
            schema.name = value(child);
            break;
          case TRANSFORMS:
            schema.transforms = new TransformsJsonDeserializer().deserialize(child);
            break;
          case OPTIONS:
            schema.storeListener = new OptionsJsonDeserializer().deserialize(child);
            break;
          default:
            throw Oops.causedBy("unrecognized element '{}'", fieldName);
        }
      }
      return schema;
    }
  }

  private boolean parseAndInitSize(final JsonNode node, final SizeBuilder sizeBuilder) {
    boolean isParsed = false;
    final JsonNode size = node.get(Parameter.SIZE.toString());
    if (size == null) {
      if (node.get(Parameter.WIDTH.toString()) != null) {
        sizeBuilder.width(floatFieldValue(node, Parameter.WIDTH));
        isParsed = true;
      }

      if (node.get(Parameter.HEIGHT.toString()) != null) {
        sizeBuilder.height(floatFieldValue(node, Parameter.HEIGHT));
        isParsed = true;
      }
    } else {
      final Float[] sizeParams = asFloatArray(size);
      sizeBuilder.width(sizeParams[0]).height(sizeParams[1]);
      isParsed = true;
    }
    return isParsed;
  }

  private Float getPossibleFloat(final JsonNode currentNode, final Parameter field) {
    Float result = null;
    final JsonNode node = currentNode.get(field.toString());

    if (node != null) {
      result = floatValue(node);
    }
    return result;
  }

  private String getTextOfField(final JsonNode currentNode, final Parameter field) {
    final JsonNode node = currentNode.get(field.toString());
    if (node == null) {
      throw Oops.causedBy("the Parameter {} is not existing", field.name);
    }
    return value(node);
  }

  private Float[] asFloatArray(final JsonNode node) {
    if (node.isTextual()) {
      final String floatArrayStr = value(node);
      return floatArrayFromString(floatArrayStr);
    }
    if (node.isArray()) {
      final Iterator<JsonNode> values = node.elements();
      final List<Float> castedValues = new ArrayList<Float>();
      while (values.hasNext()) {
        final JsonNode value = values.next();
        castedValues.add(floatValue(value));
      }
      return castedValues.toArray(new Float[]{});
    }
    throw Oops.causedBy("the type of the node {} should be an int array", node.toString());
  }

  /**
   * convert float array in str to float array
   * @return
   */
  private Float[] floatArrayFromString(final String str) {
    final String formattedStr = str.trim().replace("[","").replace("]","").replace("\"","");
    final String[] splits = formattedStr.split(",");
    final Float[] result = new Float[splits.length];
    for (int i = 0; i < splits.length; i++) {
      result[i] = Float.parseFloat(splits[i]);
    }
    return result;
  }

  private float pureDecimalValue(final JsonNode node) {
    final float result = floatValue(node);
    if (result >= 1) {
      throw Oops.causedBy("{} should be less than 1", Float.toString(result));
    }
    return result;
  }

  private float floatValue(final JsonNode node) {
    float result;
    if (node.isTextual()) {
      result = Float.parseFloat(value(node));
    } else {
      result = (float) node.asDouble();
    }
    return result;
  }

  private float floatFieldValue(final JsonNode node, final Parameter parameter) {
    final JsonNode floatNode = node.get(parameter.toString());

    try {
      return floatValue(floatNode);
    } catch (Exception e) {
      throw Oops.causedBy(e,
                          "the value '{}' of parameter '{}' is not float",
                          floatNode.asText(),
                          parameter.toString());

    }
  }
}
