package fr.clouddev.anyproto.core;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.protobuf.ByteString;
import fr.clouddev.anyproto.core.test.Test;
import junit.framework.TestCase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by CopyCat on 18/10/2015.
 */
public abstract class TestValues extends TestCase {
    static String userJson =  "{\"email\":\"toto@toto.fr\",\"name\":\"toto\",\"age\":25}";
    static String userXml = "<User><email>toto@toto.fr</email><name>toto</name><age>25</age></User>";

    static String listUserXml = "<Users>"+userXml+userXml+"</Users>";
    static String listUserJson = "["+userJson+","+userJson+"]";
    static Gson gson = new GsonBuilder().setPrettyPrinting().create();

    static String templateJson = "{\"intField\":12345,\"longField\":123456789012345,\"textField\":\"string 1\",\"enumField\":\"VALUE2\",\"boolField\":true,\"repeatedfield\":[\"value 1\",\"value 2\"],\"subMessage\":{\"id\":325,\"name\":\"lolà\"},\"byteMessage\":\"AAQIDBA=\"}";
    static String templateXml = "<TemplateMessage><intField>12345</intField><longField>123456789012345</longField><textField>string 1</textField><enumField>VALUE2</enumField><boolField>true</boolField><repeatedfield>value 1</repeatedfield><repeatedfield>value 2</repeatedfield><subMessage><id>325</id><name>lolà</name></subMessage><byteMessage>AAQIDBA=</byteMessage></TemplateMessage>";

    static Test.User referenceUser = Test.User.newBuilder()
            .setEmail("toto@toto.fr")
            .setName("toto")
            .setAge(25)
            .build();
    static List<Test.User> referenceUserList = new ArrayList<>();
    static {
        referenceUserList.add(referenceUser);
        referenceUserList.add(referenceUser);
    }

    static byte[] data = new byte[]{0x00,0x04,0x08,0x0C,0x10};

    static Test.TemplateMessage referenceMessage = Test.TemplateMessage.newBuilder()
            .setBoolField(true)
            .setEnumField(Test.EnumField.VALUE2)
            .setIntField(12345)
            .setLongField(123456789012345L)
            .setTextField("string 1")
            .addRepeatedfield("value 1")
            .addRepeatedfield("value 2")
            .setSubMessage(Test.SubMessage.newBuilder()
                    .setId(325)
                    .setName("lolà").build())
            .setByteMessage(ByteString.copyFrom(data))
            .build();

    public static void assertEquals(Test.User expected, Test.User actual) {
        assertNotSame(expected,actual);
        assertEquals(expected.getAge(),actual.getAge());
        assertEquals(expected.getEmail(),actual.getEmail());
        assertEquals(expected.getName(),actual.getName());
    }

    public static void assertEquals(Test.TemplateMessage expected, Test.TemplateMessage actual) {
        assertNotSame(expected, actual);
        assertEquals((Object)expected,(Object)actual);
    }

}
