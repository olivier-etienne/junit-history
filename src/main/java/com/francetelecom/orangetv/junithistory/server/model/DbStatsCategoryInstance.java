package com.francetelecom.orangetv.junithistory.server.model;

import com.francetelecom.orangetv.junithistory.server.dao.AbstractDbEntry;
import com.francetelecom.orangetv.junithistory.server.util.TestStatistics;

/**
 * Statistiques pour une TestSuiteInstance et pour une ClassCategory
 * 
 * @author ndmz2720
 *
 */
public class DbStatsCategoryInstance extends AbstractDbEntry implements Comparable<DbStatsCategoryInstance> {

	private static final long serialVersionUID = 1L;

	private final DbTestSuiteInstance testSuiteInstance; // lazy required
	private final DbTestClassCategory classCategory; // required

	private TestStatistics testStatistics;

	// ---------------------------------------- constructor
	public DbStatsCategoryInstance(DbTestSuiteInstance testSuiteInstance, DbTestClassCategory classCategory) {
		this.testSuiteInstance = testSuiteInstance;
		this.classCategory = classCategory;
	}

	// --------------------------------------- accessors
	public void setTestStatistics(TestStatistics testStatistics) {
		this.testStatistics = testStatistics;
	}

	public DbTestSuiteInstance getTestSuiteInstance() {
		return testSuiteInstance;
	}

	public DbTestClassCategory getTClassCategory() {
		return classCategory;
	}

	public TestStatistics getTestStatistics() {
		return testStatistics;
	}

	// ----------------------------- overriding Object
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("stats ");
		sb.append(this.testSuiteInstance.getName());
		sb.append("category: ");
		sb.append(this.classCategory.getName());
		sb.append(" - ");
		sb.append(super.toString());
		return sb.toString();
	}

	// ------------------------------ implementing Comparable
	@Override
	public int compareTo(DbStatsCategoryInstance o) {
		if (o == null) {
			return 1;
		}
		if (classCategory == null && o.getTClassCategory() == null) {
			return 0;
		}
		if (this.classCategory == null) {
			return 1;
		}

		return this.classCategory.compareTo(o.getTClassCategory());
	}

}
