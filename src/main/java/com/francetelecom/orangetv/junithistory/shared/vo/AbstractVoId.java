package com.francetelecom.orangetv.junithistory.shared.vo;

public abstract class AbstractVoId implements IVoId {

	private static final long serialVersionUID = 1L;

	private int id;

	// --------------------------------- accessor
	@Override
	public int getId() {
		return id;
	}

	// ---------------------------- constructor
	public AbstractVoId() {
		this(ID_UNDEFINED);
	}

	public AbstractVoId(int id) {
		this.id = id;
	}

	// ---------------------------------- public methods
	public boolean isIdUndefined() {
		return this.id == IVoId.ID_UNDEFINED;
	}

}
