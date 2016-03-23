package com.francetelecom.orangetv.junithistory.client.presenter;

import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.view.IMainView;
import com.google.web.bindery.event.shared.EventBus;

public abstract class AbstractMainPresenter extends AbstractPresenter implements IMainPresenter {

	// -------------------------------------- constructor
	protected AbstractMainPresenter(IGwtJUnitHistoryServiceAsync service, EventBus eventBus) {
		super(service, eventBus);
	}

	// ------------------------------ implemnenting IMainPresenter
	@Override
	public IMainView getMainView() {
		return (IMainView) this.getView();
	}
}
