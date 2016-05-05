package fr.clouddev.anyproto.core;

import fr.clouddev.anyproto.core.reader.XmlReader;
import org.junit.Test;
import fr.clouddev.anyproto.core.test.Test.*;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

/**
 * Created by CopyCat on 18/10/2015.
 */
public class XmlTests extends TestValues {

    AnyProto<User> anyProto = new AnyProto<>(User.class);
    AnyProto<TemplateMessage> templateAnyProto = new AnyProto<>(TemplateMessage.class);

    @Test
    public void testConvertFromXmlObjectOrList() {
        List<User> users = (List<User>) anyProto.fromXml(listUserXml);
        assertNotSame(referenceUserList,users);
        assertEquals(referenceUserList.size(),users.size());
        for(int i=0; i< users.size();i++) {
            assertNotSame(referenceUserList.get(i),users.get(i));
            assertEquals(referenceUserList.get(i), users.get(i));
        }

        User user = (User) anyProto.fromXml(userXml);
        assertNotSame(referenceUser,user);
        assertEquals(referenceUser, user);

        TemplateMessage message = (TemplateMessage)templateAnyProto.fromXml(templateXml);
        System.out.println(message.toString());
        assertNotSame(referenceMessage, message);
        assertEquals(referenceMessage,message);
    }

    @Test
    public void testNullXml() {
        User user = (User)anyProto.fromXml(null);
        assertNull(user);

        user = anyProto.fromXmlObject(null);
        assertNull(user);

        assertNull(anyProto.fromXmlList(null));

        assertNull(anyProto.fromXml(""));
        assertNull(anyProto.fromXml(templateJson));

        XmlReader xmlReader = new XmlReader(User.class);
        assertNull(xmlReader.getObject((byte[]) null));
        assertNull(xmlReader.getObject((InputStream) null));
        assertNull(xmlReader.getObjectOrList((byte[]) null));
        assertNull(xmlReader.getObjectOrList((InputStream) null));
        assertNull(xmlReader.getRepeated((byte[]) null));
        assertNull(xmlReader.getRepeated((InputStream) null));
    }

    @Test
    public void testConvertFromXml() throws Exception {
        User user = anyProto.fromXmlObject(userXml);
        String templateXml = templateAnyProto.toXml(referenceMessage);
        assertNotSame(referenceUser, user);
        assertEquals(referenceUser, user);
        TemplateMessage message = templateAnyProto.fromXmlObject(templateXml);
        assertEquals(referenceMessage, message);
        assertEquals(templateXml, templateAnyProto.toXml(message));
        XmlReader<TemplateMessage> reader = new XmlReader<>(TemplateMessage.class);
        message = reader.getObject(new ByteArrayInputStream(templateXml.getBytes("UTF-8")));
        assertEquals(referenceMessage,message);
    }

    @Test
    public void testConvertFromXmlList() {
        List<User> users = anyProto.fromXmlList(listUserXml);
        assertNotSame(referenceUserList,users);
        assertEquals(referenceUserList.size(),users.size());
        for(int i=0; i< users.size();i++) {
            assertNotSame(referenceUserList.get(i),users.get(i));
            assertEquals(referenceUserList.get(i),users.get(i));
        }
    }


    @Test
    public void testXmlUnknownElement() {
        String falseXml = "<item><email>totolol</email><value1>test</value1><value2></value2><subElem><age>23</age></subElem></item>";
        fr.clouddev.anyproto.core.test.Test.User user = anyProto.fromXmlObject(falseXml);
        assertEquals("totolol",user.getEmail());
    }

    @Test
    public void testXmlEscape(){
        String xml = "<User><email>&quot;&apos;&lt;&gt;&amp;</email></User>";
        User userTest = anyProto.fromXmlObject(xml);
        assertEquals("\"'<>&", userTest.getEmail());
        assertEquals(xml, anyProto.toXml(userTest));
    }

    @Test
    public void testConvertToXml() throws Exception {
        String referenceUserStr = anyProto.toXml(referenceUser);
        assertEquals(userXml,referenceUserStr);

    }

}
