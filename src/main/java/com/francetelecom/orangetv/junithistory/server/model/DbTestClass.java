package com.francetelecom.orangetv.junithistory.server.model;

import com.francetelecom.orangetv.junithistory.server.dao.AbstractDbEntry;
import com.francetelecom.orangetv.junithistory.server.dao.IDbEntry;

public class DbTestClass extends AbstractDbEntry implements Comparable<DbTestClass> {

	private static final long serialVersionUID = 1L;

	private final String name;

	// non en base (calcule)
	private transient String shortName;

	private DbTestClassCategory category;

	// ------------------------------- constructor
	public DbTestClass(String name) {
		this.name = name;
	}

	// --------------------------- accessors

	public String getName() {
		return this.name;
	}

	public String getShortName() {
		return shortName;
	}

	public void setShortName(String shortName) {
		this.shortName = shortName;
	}

	public DbTestClassCategory getCategory() {
		return category;
	}

	public void setCategory(DbTestClassCategory category) {
		this.category = category;
	}

	// ------------------------- implementing Comparable
	@Override
	public int compareTo(DbTestClass o) {
		if (o == null) {
			return 1;
		}

		return this.getName().compareTo(o.getName());
	}

	// ------------------------------ overriding object
	@Override
	public String toString() {

		StringBuilder sb = new StringBuilder();
		sb.append((this.category != null) ? this.category.getName() : "");
		sb.append(" - ");
		sb.append(this.name);

		return sb.toString();
	}

	@Override
	public int hashCode() {

		if (this.getId() != ID_UNDEFINED) {
			int result = 0;
			result = 31 * result + this.getId();

			return result;
		}

		return this.getName().hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		DbTestClass oTclass = (DbTestClass) o;
		if (this.getId() == ID_UNDEFINED || oTclass.getId() == ID_UNDEFINED) {
			return this.getName().equals(oTclass.getName());
		}
		return this.getId() == ((IDbEntry) o).getId();
	}

}
