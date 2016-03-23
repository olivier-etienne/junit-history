package com.francetelecom.orangetv.junithistory.client.view;

import com.francetelecom.orangetv.junithistory.client.presenter.IEditItemPresenter.ViewActionEnum;

public abstract class AbstractEditView extends AbstractView {

	protected final ButtonViewAction btUpdate;

	protected final ButtonViewAction btCancel;

	protected boolean locked;

	// --------------------------- constructor
	protected AbstractEditView(String itemName) {
		this.btUpdate = new ButtonViewAction("Update", ViewActionEnum.update.name(), "Update " + itemName);
		this.btCancel = new ButtonViewAction("Cancel", ViewActionEnum.cancel.name(), "Cancel update");
	}

	// --------------------------- overriding AbstractView

	@Override
	public void lock() {
		locked = true;
		this.enableButtonAndField(false);
	}

	@Override
	public void unlock() {
		locked = false;
		this.enableButtonAndField(true);

	}

	@Override
	protected void buildButtonPanel() {
		super.addButton(this.btUpdate);
		super.addButton(this.btCancel);
	}

	@Override
	protected void initHandlers() {

	}

	@Override
	protected void initComposants() {
		// TODO Auto-generated method stub

	}

	// ------------------------------------- protected
	protected void changeTitle(String title, boolean create) {

		final String newTitle = ((create) ? "Create " : "Edit ") + title;
		this.labelTitle.setText(newTitle);
	}

	protected void enableButtonAndField(boolean enabled) {
		this.btUpdate.setEnabled(enabled);
		this.btCancel.setEnabled(enabled);
	}
}
