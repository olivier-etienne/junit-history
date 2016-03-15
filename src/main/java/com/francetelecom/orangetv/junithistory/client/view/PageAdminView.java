package com.francetelecom.orangetv.junithistory.client.view;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.AppController.MainPanelViewEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.PageAdminPresenter.IPageAdminView;
import com.francetelecom.orangetv.junithistory.client.presenter.PageAdminPresenter.TabViewEnum;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;

/**
 * Page d'administration
 * 
 * @author ndmz2720
 * 
 */
public class PageAdminView extends AbstractMainView implements IPageAdminView {

	private final static Logger log = Logger.getLogger("PageAdminView");

	private final TabPanel tabPanel = new TabPanel();

	private final Panel testerPanel = new SimplePanel();
	private final Panel catPanel = new SimplePanel();
	private final Panel groupPanel = new SimplePanel();

	// ------------------------- constructor
	public PageAdminView() {
		super();
		super.init("Admin pages");
	}

	// -------------------------- implementing IView

	@Override
	public MainPanelViewEnum getViewType() {
		return MainPanelViewEnum.admin;
	}

	@Override
	public void reinit() {
		// TODO Auto-generated method stub

	}

	@Override
	public void lock() {
		// TODO Auto-generated method stub

	}

	@Override
	public void unlock() {
		// TODO Auto-generated method stub

	}

	@Override
	protected Logger getLog() {
		return log;
	}

	// -------------------------- overriding AbstractView
	@Override
	protected void buildBodyPanel() {

		this.tabPanel.setWidth(MAX_WIDTH);
		this.tabPanel.add(this.testerPanel, "Testers");
		this.tabPanel.add(this.catPanel, "Categories");
		this.tabPanel.add(this.groupPanel, "STB groups");
		this.main.add(this.tabPanel);
		this.tabPanel.selectTab(0);
	}

	@Override
	protected void buildButtonPanel() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initHandlers() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initComposants() {
		// this.tabPanel.selectTab(0);
	}

	// --------------------- implementing IPageAdminView
	@Override
	public Panel getContainer(TabViewEnum viewType) {

		if (viewType == null) {
			return null;
		}

		log.config("getContainer(): " + viewType);

		switch (viewType) {
		case tabTester:
			return this.testerPanel;
		case tabCategory:
			return this.catPanel;
		case tabGroup:
			return this.groupPanel;
		}

		return null;
	}

	@Override
	public void addSelectionHandler(SelectionHandler<Integer> handler, int indexToSelect) {

		this.tabPanel.addSelectionHandler(handler);
		handler.onSelection(new SelectionEvent<Integer>(indexToSelect) {
		});
	}

}
