package fr.clouddev.anyproto.core.reader;

import com.google.gson.*;
import com.google.protobuf.Message;
import fr.clouddev.anyproto.core.AbstractReader;
import fr.clouddev.anyproto.core.builder.JsonBuilder;
import fr.clouddev.anyproto.core.builder.ProtobufBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by CopyCat on 01/05/15.
 */
public class JsonReader<T extends Message> extends AbstractReader<T> {
    private enum JsonType {
        PRIMITIVE(JsonPrimitive.class),
        OBJECT(JsonObject.class),
        ARRAY(JsonArray.class),
        NULL(JsonNull.class);

        private Class<? extends JsonElement> clazz;
        JsonType(Class<? extends JsonElement> clazz) {
            this.clazz = clazz;
        }

        public static JsonType getType(Class<? extends JsonElement> clazz) {
            for (JsonType value: JsonType.values()) {
                if (value.clazz.equals(clazz))
                    return value;
            }
            return null;
        }
    }

    static Logger logger = LoggerFactory.getLogger(JsonBuilder.class);

    JsonParser jsonParser = new JsonParser();


    public JsonReader(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public T getObject(InputStream input) {
        if (input != null) {
            JsonObject jsonObject = jsonParser.parse(new InputStreamReader(input)).getAsJsonObject();
            return getObject(jsonObject);
        } else {
            return null;
        }
    }

    @Override
    public T getObject(String dataStr) {
        logger.info("processing json string {}",dataStr);
        if (dataStr!=null) {
            return getObject(dataStr.getBytes());
        } else {
            return null;
        }
    }

    protected T getObject(JsonObject object) {
        ProtobufBuilder<T> protoBuilder= newBuilder();
        for (Map.Entry<String,JsonElement> entry: object.entrySet()) {
            parseElement(protoBuilder,entry.getKey(),entry.getValue());
        }
        return protoBuilder.getObject();
    }

    protected List<T> getRepeated(JsonArray array) {
        List<T> result = new ArrayList<>();
        for (JsonElement element: array) {
            JsonObject object = element.getAsJsonObject();
            ProtobufBuilder<T> protoBuilder= newBuilder();
            for (Map.Entry<String,JsonElement> entry: object.entrySet()) {
                parseElement(protoBuilder,entry.getKey(),entry.getValue());
            }
            result.add(protoBuilder.getObject());
        }
        return result;
    }

    protected void parseElement(ProtobufBuilder protoBuilder,String name, JsonElement element) {
        switch (JsonType.getType(element.getClass())) {
            case PRIMITIVE:
                protoBuilder.setField(name,element.getAsString());
                break;
            case OBJECT:
                //Recursive
                Class<? extends Message> clazz= protoBuilder.getClassForField(name);
                if (clazz != null) {
                    JsonReader<? extends Message> reader = new JsonReader<>(clazz);
                    Message message = reader.getObject(element.getAsJsonObject());
                    protoBuilder.setField(name, message);
                }
                break;
            case ARRAY:
                JsonArray array = element.getAsJsonArray();
                for (JsonElement elem : array) {
                   parseElement(protoBuilder,name,elem);
                }
                break;
            case NULL:
                //Do nothing : protobuf would throw a NullPointerException
                break;
        }
    }

    @Override
    public T getObject(byte[] data) {
        if (data != null) {
            return getObject(new ByteArrayInputStream(data));
        } else {
            return null;
        }
    }

    public List<T> getRepeated(InputStream input) {
        if (input != null) {
            JsonArray jsonArray = jsonParser.parse(new InputStreamReader(input)).getAsJsonArray();
            return getRepeated(jsonArray);
        } else {
            return null;
        }
    }

    @Override
    public List<T> getRepeated(String dataStr) {
        logger.info("processing json string {}",dataStr);
        if (dataStr != null) {
            return getRepeated(dataStr.getBytes());
        } else {
            return null;
        }
    }

    @Override
    public List<T> getRepeated(byte[] data) {
        if (data != null) {
            return getRepeated(new ByteArrayInputStream(data));
        } else {
            return null;
        }
    }

    @Override
    public Object getObjectOrList(InputStream input) {
        try {
            JsonElement element = jsonParser.parse(new InputStreamReader(input));
            if (element instanceof JsonArray) {
                return getRepeated(element.getAsJsonArray());
            } else if (element instanceof JsonObject) {
                return getObject(element.getAsJsonObject());
            }
        } catch (Exception e) {
            logger.error("Error while trying to process json",e);
        }
        return null;
    }

    @Override
    public Object getObjectOrList(String dataStr) {
        logger.info("processing json string {}",dataStr);
        if (dataStr != null) {
            return getObjectOrList(dataStr.getBytes());
        } else {
            return null;
        }
    }

    @Override
    public Object getObjectOrList(byte[] data) {
        if (data != null) {
            return getObjectOrList(new ByteArrayInputStream(data));
        } else {
            return null;
        }
    }

}
