package fr.clouddev.anyproto.core.reader;

import com.google.protobuf.ByteString;
import com.google.protobuf.Message;
import fr.clouddev.anyproto.core.AbstractReader;
import fr.clouddev.anyproto.core.utils.Generic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Created by CopyCat on 16/10/15.
 */
public class ProtobufReader<T extends Message> extends AbstractReader<T> {

    static Logger logger = LoggerFactory.getLogger(ProtobufReader.class);

    public ProtobufReader(Class<T> clazz) {
        super(clazz);
    }

    @Override
    public T getObject(InputStream input) {
        try {
            return (T)newBuilder().getBuilder().mergeFrom(input).build();
        } catch (Exception e) {
            logger.error("could not parse message from input stream because : "+e.getMessage());
        }
        return null;
    }

    @Override
    public T getObject(String dataStr) {
        return getObject(Base64.getDecoder().decode(dataStr));

    }

    @Override
    public T getObject(byte[] data) {
        return getObject(new ByteArrayInputStream(data));
    }

    @Override
    public List<T> getRepeated(InputStream input) {
        List<T> result = null;
        try {
            Generic.ProtobufList protobufList = Generic.ProtobufList.newBuilder().mergeFrom(input).build();
            result = new ArrayList<>();
            for (ByteString bs : protobufList.getListList()) {
                result.add((T)newBuilder().getBuilder().mergeFrom(bs.toByteArray()).build());
            }
        } catch (Exception e) {
            logger.error("could not parse protobuf message from input stream because : "+e.getMessage());
        }
        return result;
    }

    @Override
    public List<T> getRepeated(String dataStr) {
        return getRepeated(Base64.getDecoder().decode(dataStr));
    }

    @Override
    public List<T> getRepeated(byte[] data) {
        return getRepeated(new ByteArrayInputStream(data));
    }

    @Override
    public Object getObjectOrList(InputStream input) {
        Object result = getObject(input);
        if (result == null) {
            return getRepeated(input);
        }
        return result;
    }

    @Override
    public Object getObjectOrList(String dataStr) {
        return getObjectOrList(Base64.getDecoder().decode(dataStr));
    }

    @Override
    public Object getObjectOrList(byte[] data) {
        return getObjectOrList(new ByteArrayInputStream(data));
    }
}
