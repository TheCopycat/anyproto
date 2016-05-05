package fr.clouddev.anyproto.core;

import fr.clouddev.anyproto.core.test.Test.User;
import org.apache.commons.codec.binary.Base64;
import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.List;
/**
 * Created by CopyCat on 18/10/2015.
 */
public class ProtobufTests extends TestValues {

    AnyProto<User> anyProto = new AnyProto(User.class);

    @Test
    public void testConvertFromProtobuf() throws Exception {


        User userParsed = anyProto.fromProtobuf(referenceUser.toByteArray());
        assertNotSame(referenceUser, userParsed);
        assertEquals(referenceUser,userParsed);

        User userParsed2 = anyProto.fromProtobuf(new ByteArrayInputStream(referenceUser.toByteArray()));
        assertNotSame(referenceUser, userParsed2);
        assertEquals(referenceUser,userParsed2);

        User userParsed3 = anyProto.fromProtobuf(Base64.encodeBase64String(referenceUser.toByteArray()));
        assertNotSame(referenceUser, userParsed3);
        assertEquals(referenceUser, userParsed3);
    }

    @Test
    public void testConvertFromProtobufList() throws Exception {
        fr.clouddev.anyproto.core.test.Test.Users users =
                fr.clouddev.anyproto.core.test.Test.Users.newBuilder()
                        .addUsers(anyProto.fromJsonObject(userJson))
                        .addUsers(anyProto.fromJsonObject(userJson))
                        .build();
        List<fr.clouddev.anyproto.core.test.Test.User> listOfUsers = anyProto.fromProtobufList(users.toByteArray());
        assertEquals(2, listOfUsers.size());
        assertEquals(referenceUser,listOfUsers.get(0));
        assertEquals(referenceUser,listOfUsers.get(1));

    }

    @Test
    public void testConvertToProtobufList() throws Exception {
        fr.clouddev.anyproto.core.test.Test.Users users = fr.clouddev.anyproto.core.test.Test.Users.parseFrom(anyProto.toProtobuf(referenceUserList));
        assertEquals(2,users.getUsersCount());
        assertEquals(referenceUser,users.getUsers(0));
        assertEquals(referenceUser,users.getUsers(1));
    }

}
