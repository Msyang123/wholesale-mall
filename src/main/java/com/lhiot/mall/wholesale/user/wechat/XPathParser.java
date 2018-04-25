package com.lhiot.mall.wholesale.user.wechat;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * XPath操作XML工具类
 * 
 * @author leon
 * @version 1.0 2016年11月9日下午3:28:03
 */
public class XPathParser {
	private DocumentBuilder builder;
	private Document document;
	private boolean validation = false;
	private EntityResolver entityResolver = new NullEntityResolver();
	private Map<String, Object> variables;
	private XPath xpath = XPathFactory.newInstance().newXPath();

	public XPathParser(String xmlString) {
		this(new InputSource(new StringReader(xmlString)));
	}

	public XPathParser(InputStream inputStream) {
		this(new InputSource(inputStream));
	}

	public XPathParser(Document document) {
		this.document = document;
	}

	public XPathParser(InputSource input) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setValidating(this.validation);
			builder = factory.newDocumentBuilder();
			builder.setEntityResolver(this.entityResolver);
			builder.setErrorHandler(new DefaultErrorHandler());
			this.document = builder.parse(input);
		} catch (Exception e) {
			throw new RuntimeException("Error creating document instance.  Cause: " + e, e);
		}
	}

	public XNode getRoot() {
		return new XNode(this, this.document.getDocumentElement(), variables);
	}

	public List<XNode> evalNodes(String expression) {
		return evalNodes(document, expression);
	}

	public List<XNode> evalNodes(Object root, String expression) {
		List<XNode> xnodes = new ArrayList<XNode>();
		NodeList nodes = (NodeList) evaluate(expression, root, XPathConstants.NODESET);
		for (int i = 0; i < nodes.getLength(); i++) {
			xnodes.add(new XNode(this, nodes.item(i), variables));
		}
		return xnodes;
	}

	public XNode evalNode(String expression) {
		return evalNode(document, expression);
	}

	public XNode evalNode(Object root, String expression) {
		Node node = (Node) evaluate(expression, root, XPathConstants.NODE);
		if (node == null) {
			return null;
		}
		return new XNode(this, node, variables);
	}

	private Object evaluate(String expression, Object root, QName returnType) {
		try {
			return xpath.evaluate(expression, root, returnType);
		} catch (Exception e) {
			throw new RuntimeException("Error evaluating XPath.  Cause: " + e, e);
		}
	}

	public void setVariables(Map<String, Object> variables) {
		this.variables = variables;
	}

	public void setEntityResolver(EntityResolver entityResolver) {
		this.entityResolver = entityResolver;
	}

	public class NullEntityResolver implements EntityResolver {
		public InputSource resolveEntity(String publicId, String systemId) throws SAXException, IOException {
			return null;
		}
	}

	public class DefaultErrorHandler implements ErrorHandler {
		public void warning(SAXParseException exception) throws SAXException {
		}

		public void fatalError(SAXParseException exception) throws SAXException {
			throw exception;
		}

		public void error(SAXParseException exception) throws SAXException {
			throw exception;
		}
	}
}
