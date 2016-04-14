package fr.clouddev.anyproto.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.protobuf.ByteString;
import java.util.Base64;
import fr.clouddev.anyproto.core.builder.JsonBuilder;
import fr.clouddev.anyproto.core.reader.JsonReader;
import fr.clouddev.anyproto.core.reader.XmlReader;
import junit.framework.TestCase;
import fr.clouddev.anyproto.core.test.Test.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import static fr.clouddev.anyproto.core.TestValues.*;

/**
 * Created by CopyCat on 29/04/15.
 */
public class AnyProtoTest extends TestValues {
    AnyProto<User> anyProto = new AnyProto<>(User.class);
    AnyProto<TemplateMessage> templateAnyProto = new AnyProto<>(TemplateMessage.class);


    @Test
    public void testCreateConverter()  {
    }

    @Test
    public void testConverterBuilder() {

        assertTrue("converter builder instance of User.Builder", anyProto.newBuilder() instanceof User.Builder);
    }


}