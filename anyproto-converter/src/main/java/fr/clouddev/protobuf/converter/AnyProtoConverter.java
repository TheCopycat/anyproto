package fr.clouddev.protobuf.converter;

import com.google.protobuf.Message;
import fr.clouddev.anyproto.core.AnyProto;
import fr.clouddev.anyproto.core.reader.JsonReader;
import fr.clouddev.anyproto.core.reader.XmlReader;
import retrofit.RestAdapter;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by CopyCat on 27/04/15.
 */
public class AnyProtoConverter implements Converter
{
    public static final String MEDIA_TYPE_JSON = "application/json";
    public static final String MEDIA_TYPE_XML = "application/xml";
    public static final String MEDIA_TYPE_PROTOBUF = "application/x-protobuf";

    private static final int BUFFER_SIZE = 4096;

    private String sendingType = MEDIA_TYPE_JSON;

    public AnyProtoConverter(){}

    public AnyProtoConverter(String sendingType) {
        if (MEDIA_TYPE_JSON.equals(sendingType)
                || MEDIA_TYPE_XML.equals(sendingType)
                || MEDIA_TYPE_PROTOBUF.equals(sendingType)) {
            this.sendingType = sendingType;
        }
    }

    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {
        Class<? extends Message> c ;
        if (type instanceof Class<?>) {
            c = (Class<? extends Message>) type;
        } else if (type instanceof ParameterizedType) {
            if (List.class.equals(((ParameterizedType) type).getRawType())) {
                c = (Class<? extends Message>) ((ParameterizedType) type).getActualTypeArguments()[0];
            } else {
                throw new IllegalArgumentException("Expected a List<Class<?>> but was " + type);
            }
        } else {
            throw new IllegalArgumentException("Expected a raw Class<?> or List<Class<?>> but was " + type);
        }

        AnyProto anyProto = new AnyProto(c);
        try {
            if (body.mimeType()!= null && body.mimeType().contains(MEDIA_TYPE_JSON)) {
                return new JsonReader(c).getObjectOrList(body.in());
            }
            if (body.mimeType()!= null && body.mimeType().contains(MEDIA_TYPE_XML)) {
                return new XmlReader(c).getObjectOrList(body.in());
            }
            if (body.mimeType()!= null && body.mimeType().contains(MEDIA_TYPE_PROTOBUF)) {
                return anyProto.convert(body.in());
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        } finally {
            try {
                body.in().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return null;


    }

    @Override
    public TypedOutput toBody(Object object) {

        AnyProto anyProto = new AnyProto(object.getClass());
        Message message = (Message)object;
        return new TypedOutput() {

            @Override
            public String fileName() {
                return null;
            }

            @Override
            public String mimeType() {
                return sendingType;
            }

            @Override
            public long length() {
                switch(sendingType) {
                    case MEDIA_TYPE_JSON:
                        return anyProto.toJsonString(message).length();
                    case MEDIA_TYPE_PROTOBUF:
                        return message.getSerializedSize();
                    case MEDIA_TYPE_XML:
                        return anyProto.toXml(message).length();
                    default:
                        return 0;
                }
            }

            @Override
            public void writeTo(OutputStream out) throws IOException {
                switch (sendingType) {
                    case MEDIA_TYPE_JSON:
                        out.write(anyProto.toJsonString(message).getBytes("UTF-8"));
                        break;
                    case MEDIA_TYPE_PROTOBUF:
                        out.write(message.toByteArray());
                    case MEDIA_TYPE_XML:
                        out.write(anyProto.toXml(message).getBytes("UTF-8"));
                    default:
                        out.write("".getBytes());
                }
            }
        } ;

    }
}
