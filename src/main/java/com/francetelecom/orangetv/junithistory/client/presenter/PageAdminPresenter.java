package com.francetelecom.orangetv.junithistory.client.presenter;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.view.IMainView;
import com.francetelecom.orangetv.junithistory.client.view.IProfilMainView;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter de la page d'administration (user admin only)
 * 
 * @author ndmz2720
 * 
 */
public class PageAdminPresenter extends AbstractProfilMainPresenter {

	private final static Logger log = Logger.getLogger("PageAdminPresenter");

	public enum TabAdminViewEnum {
		tabTester("tester"), tabCategory("category"), tabGroup("STB group");

		private final String itemName;

		public String getItemName() {
			return this.itemName;
		}

		private TabAdminViewEnum(String itemName) {
			this.itemName = itemName;
		}
	}

	private static final int INDEX_TAB_USER = 0;
	private static final int INDEX_TAB_CAT = 1;
	private static final int INDEX_TAB_GROUP = 2;

	private IPageAdminView view;
	private ClientFactory clientFactory;

	// ------------------------------- constructor
	public PageAdminPresenter(ClientFactory clientFactory, IGwtJUnitHistoryServiceAsync service, EventBus eventBus,
			IPageAdminView view) {
		super(service, eventBus);
		this.view = view;
		this.clientFactory = clientFactory;
		this.bind();
	}

	// ----------------------------------------- overriding AbstractPresenter

	@Override
	protected void loadDatas(boolean forceRefresh) {
		// this.doInitDatas();
		log.config("loadDatas() - forceRefresh: " + forceRefresh);
	}

	// ----------------------------------- Overriding IPresenter
	@Override
	public IMainView getView() {
		return this.view;
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected Widget getViewAsWidget() {
		return this.view.asWidget();
	}

	// ------------------------------------- private methods

	private void bind() {

		SelectionHandler<Integer> tabSelectHandler = new SelectionHandler<Integer>() {

			@Override
			public void onSelection(SelectionEvent<Integer> event) {

				int item = event.getSelectedItem();
				switch (item) {
				case INDEX_TAB_USER:
					displaySubView(TabAdminViewEnum.tabTester);
					break;
				case INDEX_TAB_CAT:
					displaySubView(TabAdminViewEnum.tabCategory);
					break;
				case INDEX_TAB_GROUP:
					displaySubView(TabAdminViewEnum.tabGroup);
					break;

				}

			}
		};

		this.view.addSelectionHandler(tabSelectHandler, INDEX_TAB_USER);
	}

	// ------------------------------ private methods
	private void displaySubView(TabAdminViewEnum viewType) {

		log.config("displaySubView(): " + viewType);
		IPresenter presenter = clientFactory.getAdminSubPresenter(viewType);
		if (presenter == null) {
			presenter = clientFactory.buildAdminSubPresenter(clientFactory.getAdminSubView(viewType));
			presenter.go(this.view.getContainer(viewType));
		}

	}

	// --------------------------------------- View
	public static interface IPageAdminView extends IProfilMainView {

		public Panel getContainer(TabAdminViewEnum viewType);

		public void addSelectionHandler(SelectionHandler<Integer> handler, int indexToSelect);
	}

}
