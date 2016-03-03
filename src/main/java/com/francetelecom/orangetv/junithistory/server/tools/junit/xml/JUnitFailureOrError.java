package com.francetelecom.orangetv.junithistory.server.tools.junit.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlValue;

/**
 * @author Pierre Smeyers
 */
public class JUnitFailureOrError {
	@XmlAttribute
	private String type;

	@XmlAttribute
	private String message;
	
	@XmlValue
	private String stack;

	public String getType() {
		return type;
	}

	public String getMessage() {
		return message;
	}
	
	public String getStack() {
		return stack;
	}

	@Override
	public String toString() {
		return " fail/error (" + type+ "):  '" + message + "': \n"+stack;
	}
}
