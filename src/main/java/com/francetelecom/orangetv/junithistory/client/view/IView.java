package com.francetelecom.orangetv.junithistory.client.view;

import com.francetelecom.orangetv.junithistory.client.view.AbstractView.ButtonViewAction;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;

public interface IView {

	public enum LogStatus {
		success, warning, error
	}

	public Widget asWidget();

	public void reinit();

	public void setActionResult(String text, LogStatus logStatus);

	public void waiting(boolean waiting);

	public void lock();

	public void unlock();

	public void selectButton(ButtonViewAction button);

	public void activeButtons(String... actions);

	public void activeAllButtons();

	public void setViewActionClickHandler(ClickHandler clickHandler);

}
