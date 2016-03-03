package com.francetelecom.orangetv.junithistory.client.event;

import java.util.Map;

import com.francetelecom.orangetv.junithistory.client.AppController.MainPanelViewEnum;
import com.francetelecom.orangetv.junithistory.client.event.ViewReportEvent.ViewReportHandler;
import com.google.gwt.event.shared.EventHandler;
import com.google.web.bindery.event.shared.Event;

/***
 * Event pour le changement de vue
 */
public class ViewReportEvent extends Event<ViewReportHandler> {

	public static final Type<ViewReportHandler> TYPE = new Type<>();

	private final MainPanelViewEnum viewEnum;
	private Map<String, Object> params;

	// ----------------------------- overriding Event
	@Override
	public Type<ViewReportHandler> getAssociatedType() {
		return TYPE;
	}

	@Override
	protected void dispatch(ViewReportHandler handler) {

		handler.onChangeView(this);
	}

	// --------------------------------- constructor
	public ViewReportEvent(final MainPanelViewEnum viewEnum) {
		this.viewEnum = viewEnum;
	}

	// -------------------------------- public methods
	public MainPanelViewEnum getMainPanelViewEnum() {
		return this.viewEnum;
	}

	public void setParams(Map<String, Object> params) {
		this.params = params;
	}

	public Map<String, Object> getParams() {
		return this.params;
	}

	// =================================== INNER CLASS
	public static interface ViewReportHandler extends EventHandler {

		public void onChangeView(ViewReportEvent event);
	}

}
