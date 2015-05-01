package fr.clouddev.anyproto.core;

import fr.clouddev.anyproto.core.builder.ProtobufBuilder;
import fr.clouddev.anyproto.core.test.Test.*;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by CopyCat on 30/04/15.
 */
public class ProtobufBuilderTest extends TestCase {

    ProtobufBuilder<TemplateMessage> parser;

    @Before
    public void setUp() {
        parser = new ProtobufBuilder<>(TemplateMessage.newBuilder());
    }

    @Test
    public void testSetBoolean() {
        parser = parser.setField("boolfield","true");
        TemplateMessage message = parser.getObject();
        assertEquals(true,message.getBoolField());
    }

    @Test
    public void testSetInteger() {
        parser = parser.setField("iNtFiEld","12345");
        TemplateMessage message = parser.getObject();
        assertEquals(12345,message.getIntField());

    }

    @Test
    public void testSetString(){
        parser = parser.setField("textField","this is a string");
        TemplateMessage message = parser.getObject();
        assertEquals("this is a string",message.getTextField());
    }

    @Test
    public void testSetLong() {
        parser = parser.setField("longfiELD","123456789012345");
        TemplateMessage message = parser.getObject();
        assertEquals(123456789012345L,message.getLongField());
    }

    @Test
    public void testSetEnum() {
        parser = parser.setField("enumfield","vAlUe1");
        TemplateMessage message = parser.getObject();
        assertEquals(EnumField.VALUE1,message.getEnumField());

        parser = parser.setField("enumfield","2");
        message = parser.getObject();
        assertEquals(EnumField.VALUE2,message.getEnumField());
    }

    @Test
    public void testRepeated() {
        parser = parser.setField("repeatedfield","value 1");
        parser = parser.setField("repeaTeDField","value 2");
        TemplateMessage message = parser.getObject();
        assertEquals(message.getRepeatedfield(0),"value 1");
        assertEquals(message.getRepeatedfield(1),"value 2");
    }

    @Test
    public void testSubItem() {
        parser.setField("submessage",SubMessage.newBuilder().setId(325).setName("lolà").build());
        TemplateMessage message = parser.getObject();
        assertEquals(325,message.getSubMessage().getId());
        assertEquals("lolà",message.getSubMessage().getName());
    }

    @Test
    public void testSetUnknownField() {
        parser.setField("unknownfield","empty value");
    }

}