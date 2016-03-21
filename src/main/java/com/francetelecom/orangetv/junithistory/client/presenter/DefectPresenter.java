package com.francetelecom.orangetv.junithistory.client.presenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.view.IMainView;
import com.francetelecom.orangetv.junithistory.client.view.IView;
import com.francetelecom.orangetv.junithistory.client.view.IView.LogStatus;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdName;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdUtils;
import com.francetelecom.orangetv.junithistory.shared.vo.VoInitDefectDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoListTestsSameNameDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoResultSearchTestDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSearchDefectDatas;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class DefectPresenter extends AbstractMainPresenter {
	private final static Logger log = Logger.getLogger("DefectPresenter");

	private final IDefectView view;

	private Map<Integer, VoIdName> mapId2Groups = new HashMap<>();
	private boolean searchRunning = false;

	// ------------------------------- constructor
	public DefectPresenter(IGwtJUnitHistoryServiceAsync service, EventBus eventBus, IDefectView view) {
		super(service, eventBus);
		this.view = view;
		this.bind();
		this.doInitView();
	}

	// ------------------------------ implementing IPresenter
	@Override
	public IView getView() {
		return this.view;
	}

	// ------------------------------ implementing AbstractPresenter
	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected Widget getViewAsWidget() {
		return (this.view == null) ? null : this.view.asWidget();
	}

	@Override
	protected void loadDatas(boolean forceRefresh) {
		// TODO Auto-generated method stub

	}

	// ------------------------------ private methods
	private void doInitView() {

		log.config("doInitView()");
		this.rpcService.getVoInitDefectDatas(new MyAsyncCallback<VoInitDefectDatas>("Error when getting init lists!") {

			@Override
			public void onSuccess(VoInitDefectDatas datas) {
				mapId2Groups = VoIdUtils.getMapId2Item(datas.getListGroups());
				view.setInitDatas(datas);
			}

		});
	}

	private VoSearchDefectDatas currentSearch = null;

	private void bind() {

		// handler pour la selection d'une tclass de test
		this.view.setSelectTClassHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				VoSearchDefectDatas selectTestDatas = view.getTestDatas();
				// TODO ajouter la tclass
				doGetListTests(selectTestDatas);

			}
		});

		// handler pour la selection d'un test name dans la liste des nom des
		// tests
		// (resultat du search)
		this.view.setSelectTestHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				VoSearchDefectDatas selectTestDatas = view.getTestDatas();
				doGetListTClasses(selectTestDatas);
			}
		});

		// handler pour la saisie dans la zone de recherche ou la modification
		// de la STB dans la listbox
		this.view.setSearchHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				VoSearchDefectDatas searchDatas = view.getSearchDatas();
				if (searchDatas.getSearch().length() < 3 || searchDatas.getGroupId() == IVo.ID_UNDEFINED) {
					currentSearch = null;
					view.setActionResult("", LogStatus.success);
					view.reinit();
					return;
				}

				// 3 character at least...
				boolean doSearch = false;
				if (currentSearch == null) {
					currentSearch = searchDatas;
					doSearch = true;
				} else {

					// si modification de la recherche
					if (searchDatas.getGroupId() != currentSearch.getGroupId()
							|| !searchDatas.getSearch().equals(currentSearch.getSearch())) {
						currentSearch = searchDatas;
						doSearch = true;
					}
				}
				if (doSearch) {
					doSearch(currentSearch);
				}
			}
		});

	}

	/*
	 * Récupère la liste des nom de tests contenant le mot saisi par l'utilisateur dans la boite de recherche
	 */
	private void doSearch(final VoSearchDefectDatas searchDatas) {

		if (searchRunning) {
			return;
		}
		this.searchRunning = true;
		this.view.waiting(true);

		log.config("doSearch(" + searchDatas.toString() + ")");
		this.rpcService.searchDefectTestList(searchDatas, new MyAsyncCallback<VoResultSearchTestDatas>(
				"Error getting list of test names!") {

			@Override
			public void onSuccess(VoResultSearchTestDatas datas) {
				view.setResultSearchDatas(datas);

				view.setActionResult("Success gettting list of test names for " + searchDatas.toString(),
						LogStatus.success);
				view.setResultSearchDatas(datas);

				searchRunning = false;
				view.waiting(false);
			}
		});
	}

	/*
	 * Récupère la liste des tclass pour le nom de test choisi et le groupid courant
	 */
	private void doGetListTClasses(final VoSearchDefectDatas testDatas) {

		if (searchRunning) {
			return;
		}

		this.searchRunning = true;
		this.view.waiting(true);
		log.config("doGetListTClasses(" + testDatas.toString() + ")");

		this.rpcService.listTClassesForGroupIdAndTestName(testDatas, new MyAsyncCallback<List<VoIdName>>("") {

			@Override
			public void onSuccess(List<VoIdName> listTClasses) {

				view.setActionResult("Success gettting list of tclass for " + testDatas.toString(), LogStatus.success);
				view.setTestTClasses(testDatas.getSearch(), listTClasses);

				searchRunning = false;
				view.waiting(false);

				// récupérer la liste des tests pour la première tclass de la
				// box
				if (listTClasses != null && listTClasses.size() > 0) {
					doGetListTests(view.getTestDatas());
				}
			}

		});

	}

	/*
	 * Récupère la liste des tests du nom choisi par l'utilisateur pour le groupId courant 
	 */
	private void doGetListTests(final VoSearchDefectDatas testDatas) {

		if (searchRunning) {
			return;
		}

		this.searchRunning = true;
		this.view.waiting(true);
		log.config("doGetListTests(" + testDatas.toString() + ")");
		this.rpcService.getListTestsForGroupIdTClassIdAndTestName(testDatas,
				new MyAsyncCallback<VoListTestsSameNameDatas>("Error getting list of tests!") {

					@Override
					public void onSuccess(VoListTestsSameNameDatas datas) {

						view.setActionResult("Success gettting list of tests for " + testDatas.toString(),
								LogStatus.success);
						view.setTestDatas(datas);

						searchRunning = false;
						view.waiting(false);

					}
				});

	}

	// ================================= INNER CLASS
	private abstract class MyAsyncCallback<T> implements AsyncCallback<T> {

		private final String errorMessage;

		private MyAsyncCallback(String errorMessage) {
			this.errorMessage = errorMessage;
		}

		@Override
		public void onFailure(Throwable caught) {

			if (this.errorMessage != null) {
				String errorMessage = this.errorMessage + "<br/>" + caught.getMessage();
				view.setActionResult(errorMessage, LogStatus.error);
				log.severe(errorMessage);
				view.waiting(false);
			}
		}

	}

	// ------------------------------- View
	public interface IDefectView extends IMainView {

		public void setInitDatas(VoInitDefectDatas datas);

		public void setSearchHandler(ChangeHandler searchHandler);

		public void setSelectTestHandler(ChangeHandler selectTestHandler);

		public void setSelectTClassHandler(ChangeHandler selectTClassHandler);

		public VoSearchDefectDatas getSearchDatas();

		public VoSearchDefectDatas getTestDatas();

		public void setResultSearchDatas(VoResultSearchTestDatas datas);

		public void setTestDatas(VoListTestsSameNameDatas testDatas);

		public void setTestTClasses(String testName, List<VoIdName> listTClasses);
	}

}
