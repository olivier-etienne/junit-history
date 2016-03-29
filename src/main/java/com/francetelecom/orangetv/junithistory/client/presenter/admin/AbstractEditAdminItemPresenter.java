package com.francetelecom.orangetv.junithistory.client.presenter.admin;

import java.util.List;

import com.francetelecom.orangetv.junithistory.client.presenter.AbstractEditItemPresenter;
import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.view.IView.LogStatus;
import com.google.web.bindery.event.shared.EventBus;

public abstract class AbstractEditAdminItemPresenter extends AbstractEditItemPresenter implements
		IEditAdminItemPresenter {

	protected IGridSubPresenter gridSubPresenter;

	// ------------------------------------- constructor
	protected AbstractEditAdminItemPresenter(IGwtJUnitHistoryServiceAsync service, EventBus eventBus, String itemName) {
		super(service, eventBus, itemName);
	}

	protected void closeDialog(boolean updateDone) {
		if (this.gridSubPresenter != null) {
			this.gridSubPresenter.closeDialogBox(updateDone);
		}
	}

	protected void refreshList() {
		if (this.gridSubPresenter != null) {
			this.gridSubPresenter.refresh();
		}
	}

	// ---------------------------------- overriding AbstractEditItemPresenter
	@Override
	protected IValidationCallback buildCallbackForUpdateItem(final String name) {

		return new IValidationCallback() {

			@Override
			public void onSuccess() {
				getView().setActionResult("Update " + name + " in success...", LogStatus.success);
				closeDialog(true);
				refreshList(); // specifique pour admin
			}

			@Override
			public void onError(List<String> errorMessages) {
				displayValidationErrors(errorMessages);
			}
		};
	}

	// ---------------------------------- implementing IEditItemPresenter
	@Override
	public void setGridSubPresenter(IGridSubPresenter presenter) {
		this.gridSubPresenter = presenter;
	}

}
