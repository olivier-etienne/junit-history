package com.francetelecom.orangetv.junithistory.server.model;

import java.util.List;

import com.francetelecom.orangetv.junithistory.server.dao.AbstractDbEntry;
import com.francetelecom.orangetv.junithistory.server.dao.IDaoTestClassCategory;
import com.francetelecom.orangetv.junithistory.shared.util.ObjectUtils;
import com.francetelecom.orangetv.junithistory.shared.vo.VoCategoryForEdit;

/**
 * Category d'une classe de test
 * 
 * @author ndmz2720
 * 
 */
public class DbTestClassCategory extends AbstractDbEntry implements Comparable<DbTestClassCategory> {

	private static final long serialVersionUID = 1L;

	private String name;
	private String[] suiteNames;
	private boolean defaultValue = false;

	// -------------------------------- constructor
	public DbTestClassCategory(String name) {
		this.name = name;
	}

	// ------------------------------------- accessor

	public void setSuiteNames(String... suiteNames) {
		this.suiteNames = suiteNames;
	}

	public boolean isDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(boolean defaultValue) {
		this.defaultValue = defaultValue;
	}

	public String getName() {
		return name;
	}

	public String[] getSuiteNames() {
		return suiteNames;
	}

	// ----------------------------------- public methods
	public String getDescription() {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < suiteNames.length; i++) {
			String suiteName = suiteNames[i];
			sb.append(suiteName);
			if (i != suiteNames.length - 1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	public void update(VoCategoryForEdit voCategory) {
		if (voCategory != null) {
			this.name = voCategory.getName();

			String list = voCategory.getListClassNames();
			if (list != null) {
				final List<String> listItems = ObjectUtils.buildListItems(list,
						IDaoTestClassCategory.CLASSNAME_SEPARATOR + "");
				this.setSuiteNames(ObjectUtils.listToTab(listItems));
			}
		}
	}

	// ------------------------------- overriding Comparable
	@Override
	public int compareTo(DbTestClassCategory o) {
		if (o == null) {
			return 1;
		}
		return this.getName().compareTo(o.getName());
	}

	// ------------------------------- overriding Object
	@Override
	public String toString() {

		return this.name + " - " + this.getDescription() + " - " + this.defaultValue;
	}

}
