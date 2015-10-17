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

    public T getObjectFromAny(String str) {
        T result = protobufReader.getObject(str);
        if (result != null) return result;
        result = jsonReader.getObject(str);
        if (result != null) return result;
        result = xmlReader.getObject(str);
        return result;
    }

    public T getObjectFromAny(byte[] data) {
        T result = protobufReader.getObject(data);
        if (result != null) return result;
        result = jsonReader.getObject(data);
        if (result != null) return result;
        result = xmlReader.getObject(data);
        return result;
    }

    public T getObjectFromAny(InputStream input) {
        T result = protobufReader.getObject(input);
        if (result != null) return result;
        result = jsonReader.getObject(input);
        if (result != null) return result;
        result = xmlReader.getObject(input);
        return result;
    }

    public List<T> getListFromAny(String str) {
        List<T> result = protobufReader.getRepeated(str);
        if (result != null) return result;
        result = jsonReader.getRepeated(str);
        if (result != null) return result;
        result = xmlReader.getRepeated(str);
        return result;
    }

    public List<T> getListFromAny(byte[] data) {
        List<T> result = protobufReader.getRepeated(data);
        if (result != null) return result;
        result = jsonReader.getRepeated(data);
        if (result != null) return result;
        result = xmlReader.getRepeated(data);
        return result;
    }

    public List<T> getListFromAny(InputStream input) {
        List<T> result = protobufReader.getRepeated(input);
        if (result != null) return result;
        result = jsonReader.getRepeated(input);
        if (result != null) return result;
        result = xmlReader.getRepeated(input);
        return result;
    }

    public Object getObjectOrListFromAny(String str) {
        Object result = protobufReader.getObjectOrList(str);
        if (result != null) return result;
        result = jsonReader.getObjectOrList(str);
        if (result != null) return result;
        result = xmlReader.getObjectOrList(str);
        return result;
    }

    public Object getObjectOrListFromAny(byte[] source) {
        Object result = protobufReader.getObjectOrList(source);
        if (result != null) return result;
        result = jsonReader.getObjectOrList(source);
        if (result != null) return result;
        result = xmlReader.getObjectOrList(source);
        return result;
    }

    public Object getObjectOrListFromAny(InputStream source) {
        Object result = protobufReader.getObjectOrList(source);
        if (result != null) return result;
        result = jsonReader.getObjectOrList(source);
        if (result != null) return result;
        result = xmlReader.getObjectOrList(source);
        return result;
    }


    public List<T> fromProtobufList(String str) throws IOException { return protobufReader.getRepeated(str); }

    public List<T> fromProtobufList(byte[] data) throws IOException { return protobufReader.getRepeated(data); }

    public List<T> fromProtobufList(InputStream input) throws IOException { return protobufReader.getRepeated(input); }

    public T fromProtobuf(String str) throws IOException { return protobufReader.getObject(str); }

    public T fromProtobuf(byte[] data) throws IOException { return protobufReader.getObject(data); }

    public T fromProtobuf(InputStream input) throws IOException { return protobufReader.getObject(input); }

    public byte[] toProtobuf(List<T> list) {
        Generic.ProtobufList.Builder protobufList = Generic.ProtobufList.newBuilder();
        for (T item : list) {
            protobufList.addList(ByteString.copyFrom(item.toByteArray()));
        }
        return protobufList.build().toByteArray();
    }

    public byte[] toProtobuf(T message) { return message.toByteArray(); }

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
