package com.francetelecom.orangetv.junithistory.server.tools.junit.xml;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @author Pierre Smeyers
 */
@XmlRootElement(name = "testsuite")
public class JUnitTestSuite {

	private File file;
	@XmlAttribute
	private String time;
	@XmlAttribute
	private int failures;
	@XmlAttribute
	private int errors;
	@XmlAttribute
	private int skipped;
	@XmlAttribute
	private int tests;
	@XmlAttribute
	private String name;

	@XmlElementWrapper(name = "properties")
	@XmlElement(name = "property")
	private List<Property> properties = new ArrayList<Property>();

	@XmlElement(name = "testcase")
	private List<JUnitTestCase> testCases = new ArrayList<JUnitTestCase>();

	public double getTime() {
		return Double.parseDouble(time.replace(",", ""));
	}

	public int getFailures() {
		return failures;
	}

	public int getErrors() {
		return errors;
	}

	public int getSkipped() {
		return skipped;
	}

	public int getTests() {
		return tests;
	}

	public String getName() {
		return name;
	}

	public List<Property> getProperties() {
		return properties;
	}

	public List<JUnitTestCase> getTestCases() {
		return testCases;
	}

	public File getFile() {
		return file;
	}

	public void setFile(File file) {
		this.file = file;
	}

	/*
	 * FIXME: not sorted!
	 */
	public Map<String, List<JUnitTestCase>> getSortedTestCases() {
		Map<String, List<JUnitTestCase>> classname2Tests = new LinkedHashMap<String, List<JUnitTestCase>>();
		for (JUnitTestCase tc : getTestCases()) {
			List<JUnitTestCase> tests = classname2Tests.get(tc.getClassname());
			if (tests == null) {
				tests = new ArrayList<JUnitTestCase>();
				classname2Tests.put(tc.getClassname(), tests);
			}
			tests.add(tc);
		}
		return classname2Tests;
	}

	@Override
	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append("test suite");
		sb.append(" name: " + name);
		sb.append(", tests: " + tests);
		sb.append(", skipped: " + skipped);
		sb.append(", failures: " + failures);
		sb.append(", errors: " + errors);
		sb.append(", time: " + time);
		sb.append(", properties: ");
		for (Property p : properties) {
			sb.append("\n	");
			sb.append(p);
		}
		sb.append(", test cases: ");
		for (JUnitTestCase t : testCases) {
			sb.append("\n	");
			sb.append(t);
		}
		return sb.toString();
	}

}
