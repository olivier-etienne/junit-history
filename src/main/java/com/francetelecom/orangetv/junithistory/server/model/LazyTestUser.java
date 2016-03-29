package com.francetelecom.orangetv.junithistory.server.model;

/**
 * Encapsule un TestUser
 * 
 * @author ndmz2720
 *
 */
public class LazyTestUser extends DbTestUser implements ILazy {

	private static final long serialVersionUID = 1L;

	private boolean lazy = true;

	// --------------------- constructor
	public LazyTestUser(int id) {
		super.setId(id);
	}

	// ---------------------- implementing IDbEntry
	@Override
	public boolean isLazy() {
		return lazy;
	}

	// ---------------------- implementing ILazy

	@Override
	public void setLazy(boolean lazy) {
		this.lazy = lazy;
	}

	// ---------------------- overriding DtTestMessage

	// TODO mettre les autres
	// ----------------------- accessor
}
