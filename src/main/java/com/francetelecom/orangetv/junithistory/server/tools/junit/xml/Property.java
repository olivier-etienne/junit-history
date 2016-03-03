package com.francetelecom.orangetv.junithistory.server.tools.junit.xml;

import javax.xml.bind.annotation.XmlAttribute;

/**
 * @author Pierre Smeyers
 */
public class Property {
	@XmlAttribute
	private String name;

	@XmlAttribute
	private String value;

	public String getName() {
		return name;
	}

	public String getValue() {
		return value;
	}

	@Override
	public String toString() {
		return " property '" + name + "':  '" + value + "'";
	}
}
