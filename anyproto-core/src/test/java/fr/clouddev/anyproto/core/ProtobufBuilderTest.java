package fr.clouddev.anyproto.core;

import fr.clouddev.anyproto.core.builder.ProtobufBuilder;
import fr.clouddev.anyproto.core.test.Test.*;
import junit.framework.TestCase;
import org.apache.commons.codec.binary.Base64;
import org.junit.Before;
import org.junit.Test;


/**
 * Created by CopyCat on 30/04/15.
 */
public class ProtobufBuilderTest extends TestCase {

    ProtobufBuilder<TemplateMessage> builder;

    @Before
    public void setUp() {
        builder = new ProtobufBuilder<>(TemplateMessage.newBuilder());
    }

    @Test
    public void testSetBoolean() {
        builder = builder.setField("boolfield","true");
        TemplateMessage message = builder.getObject();
        assertEquals(true,message.getBoolField());
    }

    @Test
    public void testSetInteger() {
        builder = builder.setField("iNtFiEld","12345");
        TemplateMessage message = builder.getObject();
        assertEquals(12345,message.getIntField());

    }

    @Test
    public void testSetString(){
        builder = builder.setField("textField","this is a string");
        TemplateMessage message = builder.getObject();
        assertEquals("this is a string",message.getTextField());
    }

    @Test
    public void testSetLong() {
        builder = builder.setField("longfiELD","123456789012345");
        TemplateMessage message = builder.getObject();
        assertEquals(123456789012345L,message.getLongField());
    }

    @Test
    public void testSetEnum() {
        builder = builder.setField("enumfield","vAlUe1");
        TemplateMessage message = builder.getObject();
        assertEquals(EnumField.VALUE1, message.getEnumField());

        builder = builder.setField("enumfield","2");
        message = builder.getObject();
        assertEquals(EnumField.VALUE2,message.getEnumField());
    }

    @Test
    public void testRepeated() {
        builder = builder.setField("repeatedfield","value 1");
        builder = builder.setField("repeaTeDField","value 2");
        TemplateMessage message = builder.getObject();
        assertEquals(message.getRepeatedfield(0),"value 1");
        assertEquals(message.getRepeatedfield(1),"value 2");
    }

    @Test
    public void testSubItem() {
        builder.setField("submessage", SubMessage.newBuilder().setId(325).setName("lolà").build());
        TemplateMessage message = builder.getObject();
        assertEquals(325,message.getSubMessage().getId());
        assertEquals("lolà",message.getSubMessage().getName());
    }

    @Test
    public void testByteElement() {
        byte[] data = new byte[]{0x00,0x04,0x08,0x0C,0x10};
        String dataString = Base64.encodeBase64String(data);
        builder.setField("byteMessage", dataString);
        TemplateMessage message = builder.getObject();
        byte[] byteMessage = message.getByteMessage().toByteArray();
        assertEquals(data.length,byteMessage.length);
        int index = 0;
        for (byte b: data ){
            assertEquals(b,byteMessage[index]);
            index++;
        }
    }

    @Test
    public void testSetUnknownField() {
        builder.setField("unknownfield", "empty value");
    }

}