package fr.clouddev.anyproto.core;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import fr.clouddev.anyproto.core.builder.JsonBuilder;
import fr.clouddev.anyproto.core.builder.XmlBuilder;
import fr.clouddev.anyproto.core.reader.JsonReader;
import fr.clouddev.anyproto.core.reader.ProtobufReader;
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

    private XmlReader<T> xmlReader;
    private JsonReader<T> jsonReader;
    private ProtobufReader<T> protobufReader;

    public AnyProto(Class<T> clazz) {
        this.clazz = clazz;
        xmlReader = new XmlReader<>(clazz);
        jsonReader = new JsonReader<>(clazz);
        protobufReader = new ProtobufReader<>(clazz);
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

    public T convert(byte[] data) throws IOException { return (T)newBuilder().mergeFrom(data).build(); }

    public List<T> fromProtobufList(byte[] data) throws IOException { return protobufReader.getRepeated(data); }

    public byte[] toProtobufList(List<T> list) {
        Generic.ProtobufList.Builder protobufList = Generic.ProtobufList.newBuilder();
        for (T item : list) {
            protobufList.addList(ByteString.copyFrom(item.toByteArray()));
        }
        return protobufList.build().toByteArray();
    }

    //Json Conversions
    public Object fromJson(String jsonString) { return jsonReader.getObjectOrList(jsonString); }

    public Object fromJson(byte [] jsonBytes) { return jsonReader.getObjectOrList(jsonBytes); }

    public Object fromJson(InputStream input) { return jsonReader.getObjectOrList(input); }

    public T fromJsonObject(String jsonString) { return jsonReader.getObject(jsonString); }

    public List<T> fromJsonList(String jsonString) { return jsonReader.getRepeated(jsonString); }

    public String toJson(T message) {
        return new JsonBuilder<T>(message).toJsonString();
    }

    public String toJson(List<T> messages) { return new JsonBuilder<T>(messages).toJsonString(); }

    //Xml Conversions
    public String toXml(T message) {
        return new XmlBuilder<T>(message).toXml();
    }

    public Object fromXml(String xmlString) {
        return xmlReader.getObjectOrList(xmlString);
    }

    public T fromXmlObject(String xmlString) {
        return xmlReader.getObject(xmlString);
    }

    public List<T> fromXmlList(String xmlString) { return xmlReader.getRepeated(xmlString); }
}
