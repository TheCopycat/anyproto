package fr.clouddev.anyproto.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.clouddev.anyproto.core.builder.JsonBuilder;
import junit.framework.TestCase;
import fr.clouddev.anyproto.core.test.Test.*;
import org.junit.Test;

import java.io.ByteArrayInputStream;

/**
 * Created by CopyCat on 29/04/15.
 */
public class AnyProtoTest extends TestCase {
    AnyProto<User> anyProto = new AnyProto<>(User.class);
    AnyProto<TemplateMessage> templateAnyProto = new AnyProto<>(TemplateMessage.class);

    static String userJson =  "{\"email\":\"toto@toto.fr\",\"name\":\"toto\",\"age\":25}";
    static String userXml = "<User><email>toto@toto.fr</email><name>toto</name><age>25</age></User>";

    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    static String templateJson = "{\"intField\":12345,\"longField\":123456789012345,\"textField\":\"string 1\",\"enumField\":\"VALUE2\",\"boolField\":true,\"repeatedfield\":[\"value 1\",\"value 2\"],\"subMessage\":{\"id\":325,\"name\":\"lolà\"}}";
    static String templateXml = "<TemplateMessage><intField>12345</intField><longField>123456789012345</longField><textField>string 1</textField><enumField>VALUE2</enumField><boolField>true</boolField><repeatedfield>value 1</repeatedfield><repeatedfield>value 2</repeatedfield><subMessage><id>325</id><name>lolà</name></subMessage></TemplateMessage>";

    static User referenceUser = User.newBuilder()
            .setEmail("toto@toto.fr")
            .setName("toto")
            .setAge(25)
            .build();
    
    static TemplateMessage referenceMessage = TemplateMessage.newBuilder()
            .setBoolField(true)
            .setEnumField(EnumField.VALUE2)
            .setIntField(12345)
            .setLongField(123456789012345L)
            .setTextField("string 1")
            .addRepeatedfield("value 1")
            .addRepeatedfield("value 2")
            .setSubMessage(SubMessage.newBuilder()
                .setId(325)
                .setName("lolà").build())
            .build();
    
    @Test
    public void testCreateConverter() {
         AnyProto<User> anyProto = new AnyProto<>(User.class);
    }

    @Test
    public void testConverterBuilder() {

        assertTrue("converter builder instance of User.Builder", anyProto.newBuilder() instanceof User.Builder);
    }
    @Test
    public void testConvertFromProtobuf() throws Exception {
        

        User userParsed = anyProto.convert(referenceUser.toByteArray());
        assertNotSame(referenceUser, userParsed);
        assertEquals(referenceUser,userParsed);

        User userParsed2 = anyProto.convert(new ByteArrayInputStream(referenceUser.toByteArray()));
        assertNotSame(referenceUser, userParsed2);
        assertEquals(referenceUser,userParsed2);

        User userParsed3 = anyProto.convert(new String(referenceUser.toByteArray()));
        assertNotSame(referenceUser, userParsed3);
        assertEquals(referenceUser, userParsed3);
    }
    
    @Test
    public void testConvertFromJson() {
        User user = anyProto.fromJson(userJson);
        assertNotSame(referenceUser,user);
        assertEquals(referenceUser, user);

        TemplateMessage message = templateAnyProto.fromJson(templateJson);
        System.out.println(message.toString());
        assertNotSame(referenceMessage, message);
    }

    @Test
    public void testConvertToJson() {
        String jsonString = anyProto.toJsonString(referenceUser);
        JsonObject json = new JsonBuilder<User>(referenceUser).toJson();
        assertEquals(userJson, jsonString);
        System.out.println(gson.toJson(new JsonParser().parse(jsonString)));
        String templateJsonString = templateAnyProto.toJsonString(referenceMessage);
        assertEquals(templateJson, templateJsonString);
        System.out.println(gson.toJson(new JsonParser().parse(templateJsonString)));
    }
    
    @Test
    public void testConvertFromXml() {
        User user = anyProto.fromXml(userXml);
        String templateXml = templateAnyProto.toXml(referenceMessage);
        assertNotSame(referenceUser, user);
        assertEquals(referenceUser, user);
        TemplateMessage message = templateAnyProto.fromXml(templateXml);
        assertEquals(referenceMessage, message);
        assertEquals(templateXml, templateAnyProto.toXml(message));
    }

    @Test
    public void testXmlUnknownElement() {
        String falseXml = "<item><email>totolol</email><value1>test</value1><value2></value2><subElem><age>23</age></subElem></item>";
        User user = anyProto.fromXml(falseXml);
        assertEquals("totolol",user.getEmail());
    }

    @Test
    public void testJsonUnknownElement() {
        String fakeJson = "{fakeField:\"lol\",email:\"kjfdkjqsdkjdjklfs\",fakelem:{\"first\":1,second:true}}";
        User user = anyProto.fromJson(fakeJson);
        assertEquals("kjfdkjqsdkjdjklfs",user.getEmail());
    }

    @Test
    public void testXmlEscape(){
        String xml = "<User><email>&quot;&apos;&lt;&gt;&amp;</email></User>";
        User userTest = anyProto.fromXml(xml);
        assertEquals("\"'<>&", userTest.getEmail());
        assertEquals(xml, anyProto.toXml(userTest));
    }

    @Test
    public void testJsonEscape() {
        String escapeJson = "{\"email\":\"\\\\and\\\"\"}";
        User user = anyProto.fromJson(escapeJson);
        assertEquals("\\and\"",user.getEmail());

        assertEquals(escapeJson, anyProto.toJsonString(user));
    }

    @Test
    public void testConvertToXml() throws Exception {
        String referenceUserStr = anyProto.toXml(referenceUser);
        assertEquals(userXml,referenceUserStr);

    }

    public void assertEquals(User expected, User actual) {
        assertNotSame(expected,actual);
        assertEquals(expected.getAge(),actual.getAge());
        assertEquals(expected.getEmail(),actual.getEmail());
        assertEquals(expected.getName(),actual.getName());
    }

    public void assertEquals(TemplateMessage expected, TemplateMessage actual) {
        assertNotSame(expected,actual);
        assertTrue(expected.equals(actual));
    }

}