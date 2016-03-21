package com.francetelecom.orangetv.junithistory.shared.vo;

public class VoIdName implements IVoId, Comparable<VoIdName> {

	private static final long serialVersionUID = 1L;

	private int id;
	private String name;

	private VoItemProtection protection;

	// --------------------------------- accessor
	@Override
	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public VoItemProtection getProtection() {
		return protection;
	}

	public void setProtection(VoItemProtection protection) {
		this.protection = protection;
	}

	// ---------------------------- constructor
	public VoIdName() {
		this(ID_UNDEFINED, null);
	}

	public VoIdName(int id, String name) {
		this.id = id;
		this.name = name;
	}

	// ---------------------------------- public methods
	public boolean isIdUndefined() {
		return this.id == IVoId.ID_UNDEFINED;
	}

	// ------------------------------- protected methods
	protected void setName(String name) {
		this.name = name;
	}

	// --------------------------------- overriding Comparable
	@Override
	public int compareTo(VoIdName o) {
		if (o == null) {
			return 1;
		}
		return this.getName().compareTo(o.getName());
	}

	// -------------------------------- overriding Object
	@Override
	public String toString() {

		return this.id + " - " + this.name;
	}

}
