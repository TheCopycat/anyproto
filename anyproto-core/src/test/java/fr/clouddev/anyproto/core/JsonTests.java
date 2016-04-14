package fr.clouddev.anyproto.core;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import fr.clouddev.anyproto.core.builder.JsonBuilder;
import fr.clouddev.anyproto.core.reader.JsonReader;
import org.junit.Test;

import java.io.InputStream;
import java.util.List;

import fr.clouddev.anyproto.core.test.Test.*;

/**
 * Created by CopyCat on 18/10/2015.
 */
public class JsonTests extends TestValues {
    AnyProto<User> anyProto = new AnyProto<>(User.class);
    AnyProto<TemplateMessage> templateAnyProto = new AnyProto<>(TemplateMessage.class);

    @Test
    public void testConvertFromJson() {
        User user = anyProto.fromJsonObject(userJson);
        assertNotSame(referenceUser,user);
        assertEquals(referenceUser, user);

        TemplateMessage message = templateAnyProto.fromJsonObject(templateJson);
        System.out.println(message.toString());
        assertNotSame(referenceMessage, message);
        assertEquals(referenceMessage,message);
    }

    @Test
    public void testConvertFromJsonList() {
        List<User> users = anyProto.fromJsonList(listUserJson);
        assertNotSame(referenceUserList,users);
        assertEquals(referenceUserList.size(),users.size());
        for(int i=0; i< users.size();i++) {
            assertNotSame(referenceUserList.get(i),users.get(i));
            assertEquals(referenceUserList.get(i), users.get(i));
        }
    }

    @Test
    public void testConvertFromJsonObjectOrList() {
        List<User> users = (List<User>) anyProto.fromJson(listUserJson);
        assertNotSame(referenceUserList,users);
        assertEquals(referenceUserList.size(),users.size());
        for(int i=0; i< users.size();i++) {
            assertNotSame(referenceUserList.get(i),users.get(i));
            assertEquals(referenceUserList.get(i), users.get(i));
        }

        User user = (User) anyProto.fromJson(userJson);
        assertNotSame(referenceUser,user);
        assertEquals(referenceUser, user);

        TemplateMessage message = (TemplateMessage)templateAnyProto.fromJson(templateJson);
        System.out.println(message.toString());
        assertNotSame(referenceMessage, message);
        assertEquals(referenceMessage,message);
    }

    @Test
    public void testNullJson() {
        User user = (User)anyProto.fromJson((String)null);
        assertNull(user);

        user = anyProto.fromJsonObject(null);
        assertNull(user);

        assertNull(anyProto.fromJsonList(null));

        assertNull(anyProto.fromJson(""));
        assertNull(anyProto.fromJson(templateJson.substring(10)));

        JsonReader jsonReader = new JsonReader<>(User.class);
        assertNull(jsonReader.getObject((byte[])null));
        assertNull(jsonReader.getObject((InputStream)null));
        assertNull(jsonReader.getObjectOrList((byte[]) null));
        assertNull(jsonReader.getObjectOrList((InputStream) null));
        assertNull(jsonReader.getRepeated((byte[]) null));
        assertNull(jsonReader.getRepeated((InputStream) null));
    }

    @Test
    public void testConvertToJson() {
        String jsonString = anyProto.toJson(referenceUser);
        JsonObject json = new JsonBuilder<User>(referenceUser).toJson();
        assertEquals(userJson, jsonString);
        System.out.println(gson.toJson(new JsonParser().parse(jsonString)));
        String templateJsonString = templateAnyProto.toJson(referenceMessage);
        assertEquals(templateJson, templateJsonString);
        System.out.println(gson.toJson(new JsonParser().parse(templateJsonString)));
    }

    @Test
    public void testJsonUnknownElement() {
        String fakeJson = "{fakeField:\"lol\",email:\"kjfdkjqsdkjdjklfs\",fakelem:{\"first\":1,second:true}}";
        User user = anyProto.fromJsonObject(fakeJson);
        assertEquals("kjfdkjqsdkjdjklfs",user.getEmail());
    }

    @Test
    public void testJsonEscape() {
        String escapeJson = "{\"email\":\"\\\\and\\\"\"}";
        User user = anyProto.fromJsonObject(escapeJson);
        assertEquals("\\and\"", user.getEmail());

        assertEquals(escapeJson, anyProto.toJson(user));
    }

}
