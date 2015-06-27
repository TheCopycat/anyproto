package fr.clouddev.anyproto.core;

import com.google.protobuf.Message;
import fr.clouddev.anyproto.core.builder.JsonBuilder;
import fr.clouddev.anyproto.core.builder.XmlBuilder;
import fr.clouddev.anyproto.core.reader.JsonReader;
import fr.clouddev.anyproto.core.reader.XmlReader;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

/**
 * Created by CopyCat on 29/04/15.
 */
public class AnyProto<T extends Message> {

    Class<T> clazz;

    public AnyProto(Class<T> clazz) {
         this.clazz = clazz;
    }

    public T.Builder newBuilder() {
        try {
            return (T.Builder)clazz.getMethod("newBuilder").invoke(null);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public T convert(String input) throws IOException {
        return convert(input.getBytes());
    }

    public T convert(InputStream source) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(source.available());
        int b;
        while ((b = source.read()) != -1) {
            baos.write(b);
        }
        byte[] data = baos.toByteArray();
        //TODO detect type of data
        //Assuming it is protobuf

        return convert(data);
    }

    public T convert(byte[] data) throws IOException {
        return (T)newBuilder().mergeFrom(data).build();
    }

    public T fromJson(String jsonString) {
        JsonReader<T> reader = new JsonReader<>(clazz);
        return reader.getObject(jsonString);
    }

    public String toJsonString(T message) {
        return new JsonBuilder<T>(message).toJsonString();
    }

    public String toXml(T message) {
        return new XmlBuilder<T>(message).toXml();
    }

    public T fromXml(String xmlString) {
        return new XmlReader<>(clazz).getObject(xmlString);
    }

    public List<T> fromJsonList(String jsonString) {
        JsonReader<T> reader = new JsonReader<>(clazz);
        return reader.getRepeated(jsonString);
    }

    public List<T> fromXmlList(String xmlString) {
        XmlReader<T> reader = new XmlReader<>(clazz);
        return reader.getRepeated(xmlString);
    }
}
