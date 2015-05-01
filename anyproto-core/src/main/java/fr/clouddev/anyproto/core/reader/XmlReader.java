package fr.clouddev.anyproto.core.reader;

import com.google.protobuf.Message;
import fr.clouddev.anyproto.core.AbstractReader;
import fr.clouddev.anyproto.core.builder.ProtobufBuilder;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by CopyCat on 01/05/15.
 */
public class XmlReader<T extends Message> extends AbstractReader<T> {

    DocumentBuilder documentBuilder;
    public XmlReader(Class<T> clazz) {
        super(clazz);

        try {
            documentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to create DocumentBuilder");
        }

    }

    @Override
    public T getObject(InputStream input) {

        try {
            return getObject(documentBuilder.parse(input).getDocumentElement());

        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public T getObject(String dataStr) {

        return getObject(dataStr.getBytes());
    }

    @Override
    public T getObject(byte[] data) {
        try {
            return getObject(documentBuilder.parse(new ByteArrayInputStream(data)).getDocumentElement());
        } catch (SAXException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public T getObject(Node element) {
        ProtobufBuilder<T> protobufBuilder = newBuilder();
        NodeList nodeList = element.getChildNodes();
        for (int nodeIndex = 0; nodeIndex < nodeList.getLength(); nodeIndex ++) {
            Node node = nodeList.item(nodeIndex);
            Node child = node.getFirstChild();
            if (child != null) {
                switch (child.getNodeType()) {
                    case Node.ELEMENT_NODE:
                        Class<? extends Message> clazz =
                                protobufBuilder.getClassForField(node.getNodeName());
                        if (clazz != null) {
                            XmlReader<? extends Message> reader = new XmlReader<>(clazz);
                            Message message = reader.getObject(node);
                            protobufBuilder.setField(node.getNodeName(), message);
                        }
                        break;
                    case Node.TEXT_NODE:
                        protobufBuilder.setField(node.getNodeName(), child.getNodeValue());
                        break;
                    default:
                        System.out.println("not supported");
                }
            } else {
                System.out.println("no child for node : "+node.getNodeName());

            }
        }

        return protobufBuilder.getObject();
    }

}
