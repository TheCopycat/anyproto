package fr.clouddev.anyproto.core.reader;

import com.google.gson.*;
import com.google.protobuf.Message;
import fr.clouddev.anyproto.core.AbstractReader;
import fr.clouddev.anyproto.core.builder.ProtobufBuilder;

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

    JsonParser jsonParser = new JsonParser();


    public JsonReader(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public T getObject(InputStream input) {
        JsonObject jsonObject = jsonParser.parse(new InputStreamReader(input)).getAsJsonObject();
        return getObject(jsonObject);
    }

    @Override
    public T getObject(String dataStr) {
        JsonObject jsonObject = jsonParser.parse(dataStr).getAsJsonObject();
        return getObject(jsonObject);
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
        JsonObject jsonObject = jsonParser.parse(new InputStreamReader(new ByteArrayInputStream(data))).getAsJsonObject();
        return getObject(jsonObject);
    }

    public List<T> getRepeated(InputStream input) {
        JsonArray jsonArray = jsonParser.parse(new InputStreamReader(input)).getAsJsonArray();
        return getRepeated(jsonArray);
    }

    @Override
    public List<T> getRepeated(String dataStr) {
        JsonArray jsonArray = jsonParser.parse(new InputStreamReader(new ByteArrayInputStream(dataStr.getBytes()))).getAsJsonArray();
        return getRepeated(jsonArray);
    }

    @Override
    public List<T> getRepeated(byte[] data) {
        JsonArray jsonArray = jsonParser.parse(new InputStreamReader(new ByteArrayInputStream(data))).getAsJsonArray();
        return getRepeated(jsonArray);
    }

}
