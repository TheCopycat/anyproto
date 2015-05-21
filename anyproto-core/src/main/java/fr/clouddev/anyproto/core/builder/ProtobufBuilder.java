package fr.clouddev.anyproto.core.builder;

import com.google.protobuf.*;

import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by CopyCat on 29/04/15.
 */
public class ProtobufBuilder<T extends Message> {

    Map<String,Descriptors.FieldDescriptor> fieldDescriptors = new HashMap<>();

    T.Builder builder;

    public ProtobufBuilder(T.Builder builderInstance) {
        this.builder = builderInstance;
        Descriptors.Descriptor desc = builderInstance.getDescriptorForType();
        for (Descriptors.FieldDescriptor descriptor : desc.getFields()) {
            fieldDescriptors.put(descriptor.getName().toLowerCase(),descriptor);
        }

    }

    public Descriptors.FieldDescriptor getFieldByName(String name) {
        return fieldDescriptors.get(name.toLowerCase());
    }

    public Class<? extends Message> getClassForField(String fieldName) {
        Descriptors.FieldDescriptor field = getFieldByName(fieldName);
        if (field != null && WireFormat.FieldType.MESSAGE.equals(field.getLiteType())) {
            return (Class<? extends Message>) builder.newBuilderForField(getFieldByName(fieldName)).getClass().getEnclosingClass();
        } else {
            return null;
        }
    }

    public ProtobufBuilder<T> setField(String name, Object value) {
        Descriptors.FieldDescriptor field = getFieldByName(name);
        if (field == null) {
            return this;
        }
        Object objValue = convertToType(field, value);
        if (objValue == null) {
            return this;
        }
        if (field.isRepeated()) {
            builder.addRepeatedField(field, objValue);
        } else {
            builder.setField(field, objValue);
        }
        return this;
    }

    protected Object convertToType(Descriptors.FieldDescriptor field, Object value) {
        switch(field.getLiteType()) {
            case DOUBLE:
                return Double.valueOf(value+"");

            case FLOAT:
                return Float.valueOf(value+"");

            case INT64:
            case UINT64:
            case FIXED64:
            case SINT64:
            case SFIXED64:
                return Long.valueOf(value+"");

            case UINT32:
            case INT32:
            case FIXED32:
            case SFIXED32:
            case SINT32:
                return Integer.valueOf(value+"");

            case BOOL:
                return Boolean.valueOf(value+"");

            case STRING:
                return value;

            case GROUP:
            case MESSAGE:
                //Assuming we have a correct object
                return value;
            case BYTES:
                return ByteString.copyFrom(Base64.getDecoder().decode(value+""));

            case ENUM:
                try {
                    int intValue = Integer.parseInt(value+"");
                    return field.getEnumType().findValueByNumber(intValue);
                } catch (NumberFormatException nfe) {
                    Descriptors.EnumValueDescriptor desc = null;
                    for (Descriptors.EnumValueDescriptor enumVal
                            : field.getEnumType().getValues()) {
                        if (enumVal.getName().equalsIgnoreCase(value+"")) {
                            return enumVal;
                        }
                    }
                    return null;
                }



        }
        return null;
    }

    public T getObject() {
        return (T)builder.build();
    }
}
