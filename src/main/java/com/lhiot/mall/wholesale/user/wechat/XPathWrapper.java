package com.lhiot.mall.wholesale.user.wechat;

public class XPathWrapper {
	private XPathParser xpath;

	public XPathWrapper(XPathParser xpath) {
		this.xpath = xpath;
	}

	public String get(String key) {
		XNode xNode = this.xpath.evalNode("//" + key);
		if (xNode == null) {
			return null;
		}
		return xNode.body();
	}
}
