package com.francetelecom.orangetv.junithistory.client.view.admin;

import com.francetelecom.orangetv.junithistory.client.presenter.admin.IEditItemPresenter.ViewActionEnum;
import com.francetelecom.orangetv.junithistory.client.view.AbstractView;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public abstract class AbstractEditView extends AbstractView implements IEditItemView {

	private final ButtonViewAction btUpdate;

	private final ButtonViewAction btCancel;

	private boolean locked;

	private ClickHandler actionClickHandler;

	// --------------------------- constructor
	protected AbstractEditView(String itemName) {
		this.btUpdate = new ButtonViewAction("Update", ViewActionEnum.update.name(), "Update " + itemName);
		this.btCancel = new ButtonViewAction("Cancel", ViewActionEnum.cancel.name(), "Cancel update");
	}

	// ---------------------------- implementing IEditItemView

	@Override
	public void setGridActionClickHandler(final ClickHandler actionClickHandler) {
		this.actionClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (!locked) {
					actionClickHandler.onClick(event);
				}
			}
		};
		this.btUpdate.addClickHandler(this.actionClickHandler);
		this.btCancel.addClickHandler(this.actionClickHandler);
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

	// ------------------------------------ private methods
	private void enableButtonAndField(boolean enabled) {
		this.btUpdate.setEnabled(enabled);
		this.btCancel.setEnabled(enabled);
	}
}
