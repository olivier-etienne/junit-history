package com.francetelecom.orangetv.junithistory.client.presenter;

import com.francetelecom.orangetv.junithistory.client.AppController.MainPanelViewEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.PageAdminPresenter.TabAdminViewEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.IEditAdminItemPresenter;
import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.view.IMainView;
import com.francetelecom.orangetv.junithistory.client.view.admin.IAdminSubView;
import com.francetelecom.orangetv.junithistory.client.view.admin.IEditItemView;
import com.google.web.bindery.event.shared.EventBus;

public interface ClientFactory {

	// ------------------- DIVERS -------------------
	public EventBus getEventBus();

	public IGwtJUnitHistoryServiceAsync getService();

	// ------------------- VIEW -------------------

	public IMainView getMainView(MainPanelViewEnum view);

	public IAdminSubView<?> getAdminSubView(TabAdminViewEnum viewType);

	public IEditItemView getEditView(TabAdminViewEnum viewType);

	// ------------------- GET PRESENTER -------------------
	public IMainPresenter getMainPresenter(MainPanelViewEnum viewType);

	public IPresenter getAdminSubPresenter(TabAdminViewEnum viewType);

	public IEditAdminItemPresenter getEditPresenter(TabAdminViewEnum viewType);

	// ------------------- BUILD PRESENTER -------------------
	public IMainPresenter buildMainPresenter(IMainView view);

	public IPresenter buildAdminSubPresenter(IAdminSubView<?> view);

	public IEditAdminItemPresenter buildEditPresenter(IEditItemView view);
}
