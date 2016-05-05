package fr.clouddev.anyproto.core.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.WireFormat;
import org.apache.commons.codec.binary.Base64;


import java.util.List;
import java.util.Map;

/**
 * Created by CopyCat on 01/05/15.
 */
public class JsonBuilder<T extends Message> {

    T message;
    List<T> messages;
    Map<Descriptors.FieldDescriptor,Object> fields;

    public JsonBuilder(T message) {
        this.message = message;
        fields = this.message.getAllFields();
    }

    public JsonBuilder(List<T> messages) {
        this.messages = messages;

    }

    public JsonArray toJsonArray() {
        JsonArray result = new JsonArray();
        for (T aMessage : messages) {
            message = aMessage;
            fields = message.getAllFields();
            JsonObject item = toJson();
            result.add(item);
        }
        return result;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        for (Descriptors.FieldDescriptor field : fields.keySet()) {
            if (field.isRepeated()) {
                JsonArray array = new JsonArray();
                for (int index = 0;
                     index < message.getRepeatedFieldCount(field);
                     index++) {
                    array.add(getJsonValue(field.getLiteType(),message.getRepeatedField(field, index)));

                }
                jsonObject.add(field.getName(),array);
            } else {
                jsonObject.add(field.getName(),getJsonValue(field));
            }
        }
        return jsonObject;
    }

    public String toJsonString() {
        if (message != null) {
            return toJson().toString();
        } else if (messages != null) {
            return toJsonArray().toString();
        } else {
            return null;
        }
    }

    private JsonElement getJsonValue(Descriptors.FieldDescriptor field) {
        return getJsonValue(field.getLiteType(),fields.get(field));
    }

    private JsonElement getJsonValue(WireFormat.FieldType type,Object value) {
        switch (type) {

            case DOUBLE:
            case FLOAT:
            case INT64:
            case FIXED64:
            case UINT64:
            case SFIXED64:
            case SINT64:
            case FIXED32:
            case INT32:
            case UINT32:
            case SFIXED32:
            case SINT32:
                return new JsonPrimitive((Number)value);
            case BOOL:
                return new JsonPrimitive((Boolean)value);
            case GROUP:
            case MESSAGE:
                Message item = (Message)value;
                JsonBuilder<Message> parser = new JsonBuilder<>(item);
                return parser.toJson();

            case BYTES:
                String data = Base64.encodeBase64String(((ByteString) value).toByteArray());
                return new JsonPrimitive(data);

            case ENUM:
            case STRING:
                return new JsonPrimitive(value.toString());

        }
        return null;
    }

}
