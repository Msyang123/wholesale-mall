package com.lhiot.mall.wholesale.user.wechat;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.w3c.dom.CharacterData;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * XML节点抽象
 * @author leon 
 * @version 1.0  2016年11月9日下午3:28:03
 */
public final class XNode extends HashMap<String, Object> implements Map<String, Object>
{
    private static final long serialVersionUID = -1224199906894638184L;

    private Node node;
    private String nodeName;
    private String body;
    private Map<String, Object> variables;
    private XPathParser xpathParser;

    public XNode(final XPathParser xpathParser, final Node node, final Map<String, Object> variables)
    {
        this.xpathParser = xpathParser;
        this.node = node;
        this.nodeName = node.getNodeName();
        this.variables = variables;
        this.body = parseBody(node);

        NamedNodeMap attributeNodes = node.getAttributes();
        if (attributeNodes != null)
        {
            for (int i = 0; i < attributeNodes.getLength(); i++)
            {
                Node attribute = attributeNodes.item(i);
                this.put(attribute.getNodeName(), attribute.getNodeValue());
            }
        }

        if (!this.containsKey("name"))
        {
            this.put("name", nodeName);
        }
    }

    public XNode newXNode(final Node node) {
        return new XNode(xpathParser, node, variables);
    }

    public XNode getParent() {
        Node parent = this.node().getParentNode();
        if (parent == null || !(parent instanceof Element))
        {
            return null;
        } else
        {
            return new XNode(xpathParser, parent, variables);
        }
    }

    public String getPath() {
        StringBuilder builder = new StringBuilder();
        Node current = this.node();
        while (current != null && current instanceof Element)
        {
            if (current != this.node())
            {
                builder.insert(0, "/");
            }
            builder.insert(0, current.getNodeName());
            current = current.getParentNode();
        }
        return builder.toString();
    }

    public String getValueBasedIdentifier() {
        StringBuilder builder = new StringBuilder();
        XNode current = this;
        while (current != null)
        {
            if (current != this)
            {
                builder.insert(0, "_");
            }
            String value = current.get("id", current.get("value", current.get("property", null)));
            if (value != null)
            {
                value = value.replace('.', '_');
                builder.insert(0, "]");
                builder.insert(0, value);
                builder.insert(0, "[");
            }
            builder.insert(0, current.name());
            current = current.getParent();
        }
        return builder.toString();
    }

    public List<XNode> evalNodes(final String expression) {
        return xpathParser.evalNodes(this.node(), expression);
    }

    public XNode evalNode(final String expression) {
        return xpathParser.evalNode(this.node(), expression);
    }

    public Node node() {
        return node;
    }

    public String name() {
        return nodeName;
    }

    public String body() {
        return body(null);
    }

    public String body(final String def) {
        return body == null ? def : body;
    }

    public <T extends Enum<T>> T get(final Class<T> enumType, final String name) {
        return get(enumType, name, null);
    }

    public <T extends Enum<T>> T get(final Class<T> enumType, final String name, final T def) {
        String value = (String) get(name);
        if (value == null)
        {
            return def;
        } else
        {
            return Enum.valueOf(enumType, value);
        }
    }

    public String get(final String name, final String def) {
        String value = (String) this.get(name);
        return value == null ? def : value;
    }

    public XNode add(final String tagName, final Map<String, String> attrs, final String content) {
        Document doc = this.node().getOwnerDocument();
        Element newNode = doc.createElement(tagName);
        if (content != null && content.trim().length() > 0)
        {
            newNode.setTextContent(content);
        }
        if (attrs != null)
        {
            for (Entry<String, String> attr : attrs.entrySet())
            {
                newNode.setAttribute(attr.getKey(), attr.getValue());
            }
        }
        return this.add(newNode);
    }

    public XNode add(final Node newChild) {
        this.node().appendChild(newChild);
        return new XNode(xpathParser, newChild, variables);
    }

    public void empty() {
        List<XNode> nodeList = this.childs();
        if (nodeList != null)
        {
            for (XNode xNode : nodeList)
            {
                this.node().removeChild(xNode.node());
            }
        }
    }

    public List<XNode> childs() {
        List<XNode> children = new ArrayList<XNode>();
        NodeList nodeList = node().getChildNodes();
        if (nodeList != null)
        {
            for (int i = 0, n = nodeList.getLength(); i < n; i++)
            {
                Node node = nodeList.item(i);
                if (node.getNodeType() == Node.ELEMENT_NODE)
                {
                    children.add(new XNode(xpathParser, node, variables));
                }
            }
        }
        return children;
    }

    public String toXmlString() {
        StringBuilder builder = new StringBuilder();
        builder.append("<");
        builder.append(nodeName);
        for (Entry<String, Object> entry : this.entrySet())
        {
            builder.append(" ");
            builder.append(entry.getKey());
            builder.append("=\"");
            builder.append(entry.getValue());
            builder.append("\"");
        }
        List<XNode> children = this.childs();
        if (children.size() > 0)
        {
            builder.append(">\n");
            for (XNode node : children)
            {
                builder.append(node.toString());
            }
            builder.append("</");
            builder.append(nodeName);
            builder.append(">");
        } else if (body != null)
        {
            builder.append(">");
            builder.append(body);
            builder.append("</");
            builder.append(nodeName);
            builder.append(">");
        } else
        {
            builder.append("/>");
        }
        return builder.toString();
    }

    private String parseBody(final Node node) {
        String data = getBodyData(node);
        if (data == null)
        {
            NodeList children = node.getChildNodes();
            for (int i = 0; i < children.getLength(); i++)
            {
                Node child = children.item(i);
                data = getBodyData(child);
                if (data != null)
                    break;
            }
        }
        return data;
    }

    private String getBodyData(final Node child) {
        if (child.getNodeType() == Node.CDATA_SECTION_NODE || child.getNodeType() == Node.TEXT_NODE)
        {
            return ((CharacterData) child).getData();
        }
        return null;
    }
}
