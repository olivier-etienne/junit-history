package com.francetelecom.orangetv.junithistory.client.presenter.admin;

import com.francetelecom.orangetv.junithistory.client.presenter.AbstractEditItemPresenter;
import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.google.web.bindery.event.shared.EventBus;

public abstract class AbstractEditAdminItemPresenter extends AbstractEditItemPresenter implements
		IEditAdminItemPresenter {

	protected IGridSubPresenter gridSubPresenter;

	// ------------------------------------- constructor
	protected AbstractEditAdminItemPresenter(IGwtJUnitHistoryServiceAsync service, EventBus eventBus, String itemName) {
		super(service, eventBus, itemName);
	}

	protected void closeDialog() {
		if (this.gridSubPresenter != null) {
			this.gridSubPresenter.closeDialogBox();
		}
	}

	protected void refreshList() {
		if (this.gridSubPresenter != null) {
			this.gridSubPresenter.refresh();
		}
	}

	// ---------------------------------- implementing IEditItemPresenter
	@Override
	public void setGridSubPresenter(IGridSubPresenter presenter) {
		this.gridSubPresenter = presenter;
	}

}
