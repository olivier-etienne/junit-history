package com.francetelecom.orangetv.junithistory.client.view.admin;

import com.francetelecom.orangetv.junithistory.client.view.AbstractEditView;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;

public abstract class AbstractEditAdminView extends AbstractEditView implements IEditItemView {

	private ClickHandler actionClickHandler;

	// --------------------------- constructor
	protected AbstractEditAdminView(String itemName) {
		super(itemName);
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

}
