package fr.clouddev.anyproto.core;

import fr.clouddev.anyproto.core.test.Test.*;
import org.junit.Test;

import java.io.InputStream;

/**
 * Created by CopyCat on 18/10/2015.
 */
public abstract class ReaderTests extends TestValues {

    protected AbstractReader<User> userReader;
    protected String referenceString;
    protected byte[] referenceBytes;
    protected abstract InputStream getReferenceInput();


    @Test
    public void TestNullValue() {
        assertNull(userReader.getObject((byte[])null));
        assertNull(userReader.getObject((InputStream)null));
        assertNull(userReader.getObjectOrList((byte[]) null));
        assertNull(userReader.getObjectOrList((InputStream) null));
        assertNull(userReader.getRepeated((byte[]) null));
        assertNull(userReader.getRepeated((InputStream) null));
    }

    @Test
    public void testGetObjectFromInput() {
        User user = userReader.getObject(getReferenceInput());
        assertEquals(referenceUser,user);
    }

    @Test
    public void testGetObjectFromBytes () {

    }
}
