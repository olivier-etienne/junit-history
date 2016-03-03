package com.francetelecom.orangetv.junithistory.client;

import com.francetelecom.orangetv.junithistory.client.presenter.ClientFactory;
import com.francetelecom.orangetv.junithistory.client.presenter.ClientFactoryImpl;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.RootPanel;

/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GwtJUnitHistoryEntryPoint implements EntryPoint {
	/**
	 * This is the entry point method.
	 */
	public void onModuleLoad() {

		final ClientFactory clientFactory = GWT.create(ClientFactoryImpl.class);
		AppController controller = new AppController(clientFactory);
		controller.go(RootPanel.get("container"));

	}
}
