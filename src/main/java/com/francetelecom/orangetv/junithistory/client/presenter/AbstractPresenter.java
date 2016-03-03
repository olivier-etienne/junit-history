package com.francetelecom.orangetv.junithistory.client.presenter;

import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.AppController;
import com.francetelecom.orangetv.junithistory.client.service.IActionCallback;
import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.util.CssConstants;
import com.francetelecom.orangetv.junithistory.client.util.WidgetUtils;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public abstract class AbstractPresenter implements CssConstants, IPresenter {

	protected IGwtJUnitHistoryServiceAsync rpcService;
	protected EventBus eventBus;

	protected AppController appController;

	protected Map<String, Object> params;

	// ----------------------------- abstract methods

	protected abstract Logger getLog();

	protected abstract Widget getViewAsWidget();

	protected abstract void loadDatas(boolean forceRefresh);

	// ------------------------------- constructor
	protected AbstractPresenter(IGwtJUnitHistoryServiceAsync service,
			EventBus eventBus) {
		this.rpcService = service;
		this.eventBus = eventBus;
	}

	// ------------------------------- implementing IPresenter

	@Override
	public void go(HasWidgets container) {
		this.go(container, null);
	}

	@Override
	public void go(HasWidgets container, Map<String, Object> params) {
		getLog().config("go()...");
		container.clear();
		Widget widget = this.getViewAsWidget();
		if (widget != null) {
			container.add(widget);
		}
		this.params = params;
		this.loadDatas(this.isForceRefresh());
	}

	// ----------------------------- protected methods

	protected void showInformation(String message) {
		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Info",
				new String[] { message }, null, false, null);
		WidgetUtils.centerDialogAndShow(dialogBox);
	}

	protected void showError(String errorMessage,
			IActionCallback actionCallback, Widget ankor) {

		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Error",
				new String[] { errorMessage }, null, false, actionCallback);
		if (ankor == null) {
			WidgetUtils.centerDialogAndShow(dialogBox);
		} else {
			dialogBox.showRelativeTo(ankor);
		}
	}

	protected String buildOnFailureMessage(Throwable caught) {

		JUnitHistoryException eitException = null;
		if (caught instanceof JUnitHistoryException) {
			eitException = (JUnitHistoryException) caught;
		}

		String errorMessage = (eitException != null) ? eitException
				.getErrorMessage() : caught.getMessage();

		if (eitException == null && caught.getCause() != null) {
			errorMessage += " (" + caught.getCause().getMessage() + ")";
		}
		// log.warning("onFailure(): " + errorMessage);
		return errorMessage;
	}

	// -------------------------------------------- private methods
	private boolean isForceRefresh() {

		if (this.params != null) {
			if (this.params.containsKey(PARAMS_FORCE_REFRESH)) {
				return (Boolean) this.params.get(PARAMS_FORCE_REFRESH);
			}
		}
		return false;
	}

}
