package fr.clouddev.anyproto.core.builder;

import com.google.protobuf.ByteString;
import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import org.apache.commons.codec.binary.Base64;

import java.util.Map;

/**
 * Created by CopyCat on 01/05/15.
 */
public class XmlBuilder<T extends Message> {

    private T message;

    Map<Descriptors.FieldDescriptor,Object> fields;

    public XmlBuilder(T message) {
        this.message = message;
        fields = this.message.getAllFields();
    }

    public String toXml() {
        return toXml(true);
    }

    private String toXml(boolean withName) {
        StringBuilder builder = new StringBuilder();
        if (withName) builder.append("<"+message.getClass().getSimpleName()+">");
        for (Descriptors.FieldDescriptor field : fields.keySet()) {
           if (field.isRepeated()) {
               for (int index = 0; index < message.getRepeatedFieldCount(field);index ++) {
                   builder.append("<"+field.getName()+">");
                   builder.append(getFieldValue(field,message.getRepeatedField(field,index)));
                   builder.append("</"+field.getName()+">");
               }
           } else {
               builder.append("<"+field.getName()+">");
               builder.append(getFieldValue(field,fields.get(field)));
               builder.append("</"+field.getName()+">");
           }
        }
        if (withName) builder.append("</"+message.getClass().getSimpleName()+">");
        return builder.toString();
    }

    public String getFieldValue(Descriptors.FieldDescriptor field,Object value) {
        switch(field.getLiteType()) {
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
            case BOOL:
                return value+"";
            case GROUP:
            case MESSAGE:
                Message item = (Message)value;
                XmlBuilder<Message> parser = new XmlBuilder<>(item);
                return parser.toXml(false);
            case BYTES:
                return Base64.encodeBase64String(((ByteString) value).toByteArray());
            case ENUM:
                return value.toString();
            case STRING:
                return escapeValue(value.toString());
        }
        return null;
    }

    public String escapeValue(String value) {
        return value
                .replace("&","&amp;")
                .replace("\"","&quot;")
                .replace("'","&apos;")
                .replace("<","&lt;")
                .replace(">","&gt;");
    }
}
