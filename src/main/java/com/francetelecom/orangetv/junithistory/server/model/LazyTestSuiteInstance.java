package com.francetelecom.orangetv.junithistory.server.model;

public class LazyTestSuiteInstance extends DbTestSuiteInstance implements ILazy {

	private static final long serialVersionUID = 1L;
	private boolean lazy = true;

	// private final int suiteId;

	// --------------------- constructor
	public LazyTestSuiteInstance(int suiteId) {
		super.setId(suiteId);
	}

	// ---------------------- implementing IDbEntry
	@Override
	public boolean isLazy() {
		return this.lazy;
	}

	// -------------------------------- implementing ILazy

	@Override
	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	// @Override
	// public int getInternalId() {
	// return this.suiteId;
	// }

}
