package fr.clouddev.anyproto.core.builder;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import com.google.protobuf.WireFormat;


import java.util.Map;

/**
 * Created by CopyCat on 01/05/15.
 */
public class JsonBuilder<T extends Message> {

    T message;
    Map<Descriptors.FieldDescriptor,Object> fields;

    public JsonBuilder(T message) {
        this.message = message;
        fields = this.message.getAllFields();
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
        return toJson().toString();
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
                //TODO find a way
                break;

            case ENUM:
            case STRING:
                return new JsonPrimitive(value.toString());

        }
        return null;
    }

    private void setJsonValue(JsonObject json, Descriptors.FieldDescriptor field) {
        switch (field.getLiteType()) {

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
                json.addProperty(field.getName(),(Number)fields.get(field));
                break;
            case BOOL:
                json.addProperty(field.getName(),(Boolean)fields.get(field));
                break;
            case GROUP:
            case MESSAGE:
                Message item = (Message)fields.get(field);
                JsonBuilder<Message> parser = new JsonBuilder<>(item);
                json.add(field.getName(),parser.toJson());
                break;
            case BYTES:
                //TODO find a way

                break;
            case ENUM:
            case STRING:
                json.addProperty(field.getName(),fields.get(field).toString());
                break;
        }
    }

    public String getXmlValue(String name) {
        return "<"+name+">"+"</"+name+">";
    }
}
