package fr.clouddev.anyproto.core;

import fr.clouddev.anyproto.core.test.Test.TemplateMessage;
import fr.clouddev.anyproto.core.test.Test.User;
import org.junit.Test;

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