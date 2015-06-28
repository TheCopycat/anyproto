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
import java.util.ArrayList;
import java.util.List;

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
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    public T getObject(String dataStr) {
        if (dataStr != null) {
            return getObject(dataStr.getBytes());
        } else {
            return null;
        }
    }

    @Override
    public T getObject(byte[] data) {
        if (data != null) {
            return getObject(new ByteArrayInputStream(data));
        } else {
            return null;
        }
    }

    @Override
    public List<T> getRepeated(InputStream input) {
        try {
            return getRepeated(documentBuilder.parse(input).getDocumentElement());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public List<T> getRepeated(String dataStr) {
        if (dataStr != null) {
            return getRepeated(dataStr.getBytes());
        } else {
            return null;
        }
    }

    @Override
    public List<T> getRepeated(byte[] data) {
        if (data != null) {
            return getRepeated(new ByteArrayInputStream(data));
        } else {
            return null;
        }
    }

    @Override
    public Object getObjectOrList(InputStream input) {
        try {
            Node root = documentBuilder.parse(input).getDocumentElement();
            if (root.getNodeName().equalsIgnoreCase(clazz.getSimpleName()+"s")) {
                return getRepeated(root);
            } else if (root.getNodeName().equalsIgnoreCase(clazz.getSimpleName())) {
                return getObject(root);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public Object getObjectOrList(String dataStr) {
        if (dataStr != null) {
            return getObjectOrList(dataStr.getBytes());
        } else {
            return null;
        }
    }

    @Override
    public Object getObjectOrList(byte[] data) {
        if (data != null) {
            return getObjectOrList(new ByteArrayInputStream(data));
        } else {
            return null;
        }
    }

    protected List<T> getRepeated(Node element) {
        List<T> result = new ArrayList<>();
        NodeList nodeList = element.getChildNodes();
        for (int nodeIndex = 0; nodeIndex < nodeList.getLength(); nodeIndex++) {
            result.add(getObject(nodeList.item(nodeIndex)));
        }
        return result;
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
