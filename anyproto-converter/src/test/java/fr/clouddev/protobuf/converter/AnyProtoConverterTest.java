package fr.clouddev.protobuf.converter;

import com.google.protobuf.Message;
import fr.clouddev.protobuf.converter.proto.Test;
import junit.framework.TestCase;
import retrofit.mime.TypedInput;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by CopyCat on 27/04/15.
 */
public class AnyProtoConverterTest extends TestCase {

    private AnyProtoConverter converter;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        converter = new AnyProtoConverter();
    }

    public void testGetBuilder() throws Exception {
        Message.Builder builder =  converter.getBuilder(Test.User.class);
        Test.User.Builder userBuilder = (Test.User.Builder) builder;
        assertNotNull(userBuilder);

    }

    public void testGetBody() throws Exception {

        Object obj = converter.fromBody(new JsonBody(), Test.User.class);
        assertEquals(obj.getClass(),Test.User.class);
        Test.User user = (Test.User)obj ;
        assertEquals(user.getEmail(),"toto@toto.fr");
        assertEquals(user.getAge(),12);

        obj = converter.fromBody(new XmlBody(),Test.User.class);
        assertTrue("instance of user", obj instanceof Test.User);
        user = (Test.User)obj ;
        assertEquals(user.getEmail(),"toto@toto.fr");
        assertEquals(user.getAge(), 12);
    }

    public void testFromBody() {
        Test.User user = Test.User.newBuilder().setEmail("toto@toto.fr").setAge(12).build();
    }

    private static class JsonBody implements TypedInput {

        String json = "{ email:\"toto@toto.fr\",age: 12 }";
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

        String xml = "<user>\n" +
                "<email>toto@toto.fr</email>\n" +
                "<age>12</age>\n" +
                "</user>";
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