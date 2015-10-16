package fr.clouddev.anyproto.core;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import fr.clouddev.anyproto.core.builder.JsonBuilder;
import fr.clouddev.anyproto.core.builder.XmlBuilder;
import fr.clouddev.anyproto.core.reader.JsonReader;
import fr.clouddev.anyproto.core.reader.XmlReader;
import fr.clouddev.anyproto.core.utils.Generic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by CopyCat on 29/04/15.
 */
public class AnyProto<T extends Message> {

    Class<T> clazz;

    static Logger logger = LoggerFactory.getLogger(AnyProto.class);

    public AnyProto(Class<T> clazz) {
         this.clazz = clazz;
    }

    public T.Builder newBuilder() {
        try {
            return (T.Builder)clazz.getMethod("newBuilder").invoke(null);
        } catch (Exception e) {
            logger.error("could not instanciate new builder for type {}",clazz.getName());
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

    public List<T> fromProtobufList(byte[] data) throws IOException {
        Generic.ProtobufList protobufList = Generic.ProtobufList.newBuilder().mergeFrom(data).build();
        List<T> result = new ArrayList<>();
        for (ByteString bs : protobufList.getListList()) {
            result.add((T) newBuilder().mergeFrom(bs.toByteArray()).build());
        }
        return result;
    }

    public byte[] toProtobufList(List<T> list) {
        Generic.ProtobufList.Builder protobufList = Generic.ProtobufList.newBuilder();
        for (T item : list) {
            protobufList.addList(ByteString.copyFrom(item.toByteArray()));
        }
        return protobufList.build().toByteArray();
    }

    public Object fromJson(String jsonString) {
        JsonReader<T> reader = new JsonReader<>(clazz);
        return reader.getObjectOrList(jsonString);
    }

    public Object fromJson(byte [] jsonBytes) {
        JsonReader<T> reader = new JsonReader<>(clazz);
        return reader.getObjectOrList(jsonBytes);
    }

    public Object fromJson(InputStream input) {
        JsonReader<T> reader = new JsonReader<>(clazz);
        return reader.getObjectOrList(input);
    }

    public T fromJsonObject(String jsonString) {
        JsonReader<T> reader = new JsonReader<>(clazz);
        return reader.getObject(jsonString);
    }

    public String toJsonString(T message) {
        return new JsonBuilder<T>(message).toJsonString();
    }

    public String toJsonString(List<T> messages) { return new JsonBuilder<T>(messages).toJsonString(); }

    public String toXml(T message) {
        return new XmlBuilder<T>(message).toXml();
    }

    public Object fromXml(String xmlString) {
        return new XmlReader<>(clazz).getObjectOrList(xmlString);
    }

    public T fromXmlObject(String xmlString) {
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
