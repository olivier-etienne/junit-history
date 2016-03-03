package com.francetelecom.orangetv.junithistory.server.tools;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.francetelecom.orangetv.junithistory.server.tools.junit.xml.JUnitTestCase;
import com.francetelecom.orangetv.junithistory.server.tools.junit.xml.JUnitTestSuite;
import com.francetelecom.orangetv.junithistory.server.tools.junit.xml.Property;

public interface IReportTestSuite {

	public abstract String getSuiteName();

	public abstract void changeSuiteName(String suiteName);

	// root name du fichier xml
	public abstract String getRootName();

	public abstract List<JUnitTestCase> getTestCases();

	public abstract Map<String, List<JUnitTestCase>> getClassname2Tests();

	public abstract int getTestCount();

	public abstract int getSkipped();

	public abstract int getFailures();

	public abstract int getErrors();

	public abstract double getTime();

	public abstract File getParentDirectory();

	public abstract List<Property> getProperties();

	/*
	 * Encapsule une unique testSuite
	 * @author ndmz2720
	 *
	 */
	public static class SingleJUnitTestSuite implements IReportTestSuite {

		private final JUnitTestSuite initialJUnitTestSuite;
		private String suiteName;
		private final File parentDirectory;
		private final String rootName;

		protected SingleJUnitTestSuite(String suiteName, JUnitTestSuite testSuite, File parentDirectory, String rootName) {
			this.initialJUnitTestSuite = testSuite;
			this.suiteName = suiteName;
			this.parentDirectory = parentDirectory;
			this.rootName = rootName;
		}

		// --------------------------- overriding IReportJUnitTestSuite

		@Override
		public int getFailures() {
			return this.initialJUnitTestSuite.getFailures();
		}

		@Override
		public int getErrors() {
			return this.initialJUnitTestSuite.getErrors();
		}

		@Override
		public int getSkipped() {
			return this.initialJUnitTestSuite.getSkipped();
		}

		@Override
		public List<JUnitTestCase> getTestCases() {
			return this.initialJUnitTestSuite.getTestCases();
		}

		@Override
		public String getSuiteName() {
			return this.suiteName;
		}

		@Override
		public String getRootName() {
			// JUnitReport.getFileNameNoExt(this.initialJUnitTestSuite.getFile());
			return this.rootName;
		}

		@Override
		public Map<String, List<JUnitTestCase>> getClassname2Tests() {
			return this.initialJUnitTestSuite.getSortedTestCases();
		}

		@Override
		public int getTestCount() {
			return this.initialJUnitTestSuite.getTests();
		}

		@Override
		public void changeSuiteName(String suiteName) {
			this.suiteName = suiteName;
		}

		@Override
		public double getTime() {
			return this.initialJUnitTestSuite.getTime();
		}

		@Override
		public File getParentDirectory() {
			return this.parentDirectory;
		}

		@Override
		public List<Property> getProperties() {
			return this.initialJUnitTestSuite.getProperties();
		}

	}

	/*
	 * Regroupe une liste de JUnitTestSuite devant etre considérées comme une seule JUnitTestSuite
	 * (à utiliser pour les merges)
	 */
	public static class AgregatedJUnitTestSuite implements IReportTestSuite {

		private final File parentDirectory;
		private String suiteName;
		private final String rootName;
		private double time;
		private int count = 0;
		private int skippeds = 0;
		private int failures = 0;
		private int errors = 0;

		private Map<String, List<JUnitTestCase>> classname2Tests = new HashMap<String, List<JUnitTestCase>>();
		private Map<String, List<JUnitTestCase>> tempoMap = new HashMap<String, List<JUnitTestCase>>();

		private List<JUnitTestCase> listTestCase = new ArrayList<JUnitTestCase>();
		private List<JUnitTestCase> tempoList1 = new ArrayList<JUnitTestCase>();

		private List<Property> listProperties = new ArrayList<Property>();
		private List<Property> tempoList2 = new ArrayList<Property>();

		private boolean isEmpty() {
			return listTestCase.isEmpty();
		}

		public AgregatedJUnitTestSuite(String suiteName, String rootName, File parentDirectory,
				List<JUnitTestSuite> listsuite) {

			this.suiteName = suiteName;
			this.rootName = rootName;
			this.parentDirectory = parentDirectory;
			if (listsuite != null) {

				for (JUnitTestSuite testSuite : listsuite) {
					count += testSuite.getTests();
					time += testSuite.getTime();
					skippeds += testSuite.getSkipped();
					failures += testSuite.getFailures();
					errors += testSuite.getErrors();

					tempoMap = testSuite.getSortedTestCases();
					if (tempoMap != null) {
						classname2Tests.putAll(tempoMap);
					}

					tempoList1 = testSuite.getTestCases();
					if (tempoList1 != null) {
						listTestCase.addAll(tempoList1);
					}

					tempoList2 = testSuite.getProperties();
					if (tempoList2 != null) {
						listProperties.addAll(tempoList2);
					}
				}
			}
		}

		@Override
		public String getSuiteName() {
			return this.suiteName;
		}

		@Override
		public void changeSuiteName(String suiteName) {
			this.suiteName = suiteName;
		}

		@Override
		public String getRootName() {
			return this.rootName;
		}

		@Override
		public List<JUnitTestCase> getTestCases() {
			return this.listTestCase;
		}

		@Override
		public Map<String, List<JUnitTestCase>> getClassname2Tests() {
			return this.classname2Tests;
		}

		@Override
		public int getTestCount() {
			return this.count;
		}

		@Override
		public int getSkipped() {
			return this.skippeds;
		}

		@Override
		public int getFailures() {
			return this.failures;
		}

		@Override
		public int getErrors() {
			return this.errors;
		}

		@Override
		public double getTime() {
			return time;
		}

		@Override
		public File getParentDirectory() {
			return this.parentDirectory;
		}

		@Override
		public List<Property> getProperties() {
			return this.listProperties;
		}

	}

}