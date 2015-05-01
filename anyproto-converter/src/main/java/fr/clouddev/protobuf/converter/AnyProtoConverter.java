package fr.clouddev.protobuf.converter;

import com.google.protobuf.Message;
import fr.clouddev.anyproto.core.AnyProto;
import fr.clouddev.anyproto.core.reader.JsonReader;
import fr.clouddev.anyproto.core.reader.XmlReader;
import retrofit.converter.ConversionException;
import retrofit.converter.Converter;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

import java.io.IOException;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;

/**
 * Created by CopyCat on 27/04/15.
 */
public class AnyProtoConverter implements Converter
{
    private static final String MEDIA_TYPE_JSON = "application/json";
    private static final String MEDIA_TYPE_XML = "application/xml";

    private static final int BUFFER_SIZE = 4096;


    @Override
    public Object fromBody(TypedInput body, Type type) throws ConversionException {

        if (!(type instanceof Class<?>)) {
            throw new IllegalArgumentException("Expected a raw Class<?> but was " + type);
        }
        Class<? extends Message> c = (Class<? extends Message>) type;

        try {
            if (MEDIA_TYPE_JSON.equals(body.mimeType())) {
                return new JsonReader(c).getObject(body.in());
            }
            if (MEDIA_TYPE_XML.equals(body.mimeType())) {
                return new XmlReader(c).getObject(body.in());
            }
        } catch (Exception ioe) {
            ioe.printStackTrace();
        }

        return null;
    }

    public Message.Builder getBuilder(Class<? extends Message> messageClass)
            throws NoSuchMethodException,IllegalAccessException,InvocationTargetException {
        Method m = messageClass.getDeclaredMethod("newBuilder");
        return (Message.Builder) m.invoke(null);
    }

    @Override
    public TypedOutput toBody(Object object) {

        String jsonMessage = new AnyProto(object.getClass()).toJsonString((Message) object);
        return new TypedOutput() {

            @Override
            public String fileName() {
                return null;
            }

            @Override
            public String mimeType() {
                return MEDIA_TYPE_JSON;
            }

            @Override
            public long length() {
                return jsonMessage.getBytes().length;
            }

            @Override
            public void writeTo(OutputStream out) throws IOException {
                out.write(jsonMessage.getBytes());
            }
        } ;

    }
}
