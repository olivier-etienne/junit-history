package com.francetelecom.orangetv.junithistory.server.dao;

import com.francetelecom.orangetv.junithistory.server.model.ILazy;
import com.francetelecom.orangetv.junithistory.shared.vo.IVoId;

public abstract class AbstractDbEntry implements IDbEntry {

	private static final long serialVersionUID = 1L;
	private int id = ID_UNDEFINED;

	@Override
	public int getId() {

		if (this.isLazy() && this instanceof ILazy) {
			return ((ILazy) this).getInternalId();
		}

		return this.id;
	}

	@Override
	public boolean isLazy() {
		return false;
	}

	// ------------------------- package methods
	protected void setId(int id) {
		this.id = id;
	}

	// ---------------------------------- public methods
	public boolean isIdUndefined() {
		return this.id == IVoId.ID_UNDEFINED;
	}

	// -------------------------------- overriding Object
	@Override
	public int hashCode() {

		if (this.id != ID_UNDEFINED) {
			int result = 0;
			result = 31 * result + this.getId();

			return result;
		}
		return super.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;

		int oId = ((IDbEntry) o).getId();
		if (this.getId() == ID_UNDEFINED || oId == ID_UNDEFINED) {
			return this == o;
		}
		return this.getId() == ((IDbEntry) o).getId();
	}

}
