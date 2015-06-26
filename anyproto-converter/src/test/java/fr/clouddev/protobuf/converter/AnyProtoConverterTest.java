package fr.clouddev.protobuf.converter;

import fr.clouddev.protobuf.converter.proto.Test;
import junit.framework.TestCase;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by CopyCat on 27/04/15.
 */
public class AnyProtoConverterTest extends TestCase {

    private AnyProtoConverter converter;

    final static Test.User testUser = Test.User.newBuilder().setEmail("toto@toto.fr").setAge(12).build();
    final static String jsonTestUser = "{\"email\":\"toto@toto.fr\",\"age\":12}";
    final static String xmlTestUser = "<User><email>toto@toto.fr</email><age>12</age></User>";

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        converter = new AnyProtoConverter();
    }


    public void testGetBody() throws Exception {

        Object obj = converter.fromBody(new JsonBody(), Test.User.class);
        assertEquals(obj.getClass(), Test.User.class);
        Test.User user = (Test.User) obj;
        assertEquals(user.getEmail(), testUser.getEmail());
        assertEquals(user.getAge(), testUser.getAge());

        obj = converter.fromBody(new XmlBody(), Test.User.class);
        assertTrue("instance of user", obj instanceof Test.User);
        user = (Test.User) obj;
        assertEquals(user.getEmail(), testUser.getEmail());
        assertEquals(user.getAge(), testUser.getAge());
    }

    public void testWriteJson() throws Exception {
        AnyProtoConverter jsonConverter = new AnyProtoConverter(AnyProtoConverter.MEDIA_TYPE_JSON);

        TypedOutput json = jsonConverter.toBody(testUser);
        assertEquals(jsonTestUser.length(), json.length());
        ByteArrayOutputStream baos = new ByteArrayOutputStream((int) json.length());
        json.writeTo(baos);
        String jsonString = new String(baos.toByteArray(), "UTF-8");
        assertEquals(jsonTestUser, jsonString);
        assertEquals("application/json", json.mimeType());
    }

    public void testWriteXml() throws Exception {
        AnyProtoConverter xmlConverter = new AnyProtoConverter(AnyProtoConverter.MEDIA_TYPE_XML);

        TypedOutput xml = xmlConverter.toBody(testUser);
        assertEquals(xmlTestUser.length(), xml.length());
        ByteArrayOutputStream baos = new ByteArrayOutputStream((int) xml.length());
        xml.writeTo(baos);
        String xmlString = new String(baos.toByteArray(), "UTF-8");
        assertEquals(xmlTestUser, xmlString);
        assertEquals("application/xml", xml.mimeType());
    }

    public void testFromBody() {

    }

    private static class JsonBody implements TypedInput {

        String json = jsonTestUser;

        @Override
        public String mimeType() {
            return "application/json";
        }

        @Override
        public long length() {
            return json.getBytes().length;
        }

        @Override
        public InputStream in() throws IOException {
            return new ByteArrayInputStream(json.getBytes());
        }
    }

    private static class XmlBody implements TypedInput {

        String xml = xmlTestUser;

        @Override
        public String mimeType() {
            return "application/xml";
        }

        @Override
        public long length() {
            return xml.getBytes().length;
        }

        @Override
        public InputStream in() throws IOException {
            return new ByteArrayInputStream(xml.getBytes());
        }
    }
}

