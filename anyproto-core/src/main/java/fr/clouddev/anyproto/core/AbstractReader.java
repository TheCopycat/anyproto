package fr.clouddev.anyproto.core;

import com.google.protobuf.Descriptors;
import com.google.protobuf.Message;
import fr.clouddev.anyproto.core.builder.ProtobufBuilder;

import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Set;

/**
 * Created by CopyCat on 01/05/15.
 */
public abstract class AbstractReader<T extends Message> {

    protected Class<T> clazz;

    public AbstractReader(Class<T> clazz) {
        this.clazz = clazz;

    }

    protected ProtobufBuilder<T> newBuilder() {
        try {
            T.Builder builder = (T.Builder)clazz.getDeclaredMethod("newBuilder").invoke(null);
            return new ProtobufBuilder<>(builder);
        } catch (NoSuchMethodException nsme) {
            throw new RuntimeException("Protobuf message does not contain newBuilder method");
        } catch (IllegalAccessException iae) {
            throw new RuntimeException("newBuilder method is not accessible");
        } catch (InvocationTargetException ite) {
            throw new RuntimeException("cannot invoke newBuilder on static class");
        } catch (ClassCastException cce) {
            throw new RuntimeException("newBuilder does not return a builder of type "+clazz.getSimpleName()+".Builder");
        }
    }

    public abstract T getObject(InputStream input);

    public abstract T getObject(String dataStr);

    public abstract T getObject(byte[] data);

    public abstract List<T> getRepeated(InputStream input);

    public abstract List<T> getRepeated(String dataStr);

    public abstract List<T> getRepeated(byte[] data);

    public abstract Object getObjectOrList(InputStream input);

    public abstract Object getObjectOrList(String dataStr);

    public abstract Object getObjectOrList(byte[] data);


}
