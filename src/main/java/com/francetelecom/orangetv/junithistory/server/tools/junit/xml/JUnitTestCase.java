package com.francetelecom.orangetv.junithistory.server.tools.junit.xml;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;

/**
 * @author Pierre Smeyers
 */
public class JUnitTestCase {
	@XmlAttribute
	private double time;
	
	@XmlAttribute
	private String name;

	@XmlAttribute
	private String classname;
	
	@XmlElement(required=false)
	private JUnitFailureOrError failure;
	
	@XmlElement(required=false)
	private JUnitFailureOrError error;
	
	@XmlElement(name="system-out", required=false)
	private String logs;
	
	public double getTime() {
		return time;
	}

	public String getName() {
		return name;
	}

	public String getClassname() {
		return classname;
	}
	
	public JUnitFailureOrError getFailure() {
		return failure;
	}
	
	public JUnitFailureOrError getError() {
		return error;
	}
	
	public String getLogs() {
		return logs;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer("testcase " + classname+"."+name);
		if(error != null) {
			sb.append(": ERROR\n");
			sb.append(error);
		} else if (failure != null) {
			sb.append(": FAILURE\n");
			sb.append(failure);
		} else {
			sb.append(": SUCCESS");
		}
		if(logs != null) {
			sb.append("\n LOGS:\n");
			sb.append(logs);
		}
		return sb.toString();
	}
}
