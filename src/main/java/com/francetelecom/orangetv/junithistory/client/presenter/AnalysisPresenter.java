package com.francetelecom.orangetv.junithistory.client.presenter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.AppController.MainPanelViewEnum;
import com.francetelecom.orangetv.junithistory.client.event.ViewReportEvent;
import com.francetelecom.orangetv.junithistory.client.service.IActionCallback;
import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.util.WidgetUtils;
import com.francetelecom.orangetv.junithistory.client.view.AnalysisView.TestActionButton;
import com.francetelecom.orangetv.junithistory.client.view.IMainView;
import com.francetelecom.orangetv.junithistory.client.view.IView;
import com.francetelecom.orangetv.junithistory.client.view.IView.LogStatus;
import com.francetelecom.orangetv.junithistory.shared.UserProfile;
import com.francetelecom.orangetv.junithistory.shared.util.ObjectUtils;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdName;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdUtils;
import com.francetelecom.orangetv.junithistory.shared.vo.VoInitDefectDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoListTestsSameNameDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoResultSearchTestDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSearchDefectDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestInstanceForEdit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class AnalysisPresenter extends AbstractProfilMainPresenter {
	private final static Logger log = Logger.getLogger("AnalysisPresenter");

	public enum TestActionButtonEnum {
		createComment, deleteComment, editComment;

	}

	private final IAnalysisView view;

	private Map<Integer, VoIdName> mapId2Groups = new HashMap<>();
	private Map<Integer, VoIdName> mapId2TClass = new HashMap<>();
	private Map<Integer, VoTestInstanceForEdit> mapId2Test;
	private boolean searchRunning = false;

	private VoSearchDefectDatas currentSearch = null;
	private VoSearchDefectDatas currentDatas;

	// ------------------------------- constructor
	public AnalysisPresenter(IGwtJUnitHistoryServiceAsync service, EventBus eventBus, IAnalysisView view) {
		super(service, eventBus);
		this.view = view;
		this.bind();
		this.doInitView();
	}

	// --------------------------- overriding AbstractProfilMainPresenter
	@Override
	public void manageUserProfil(UserProfile userProfile, boolean forceRefresh) {
		super.manageUserProfil(userProfile, forceRefresh);

		// rafraichir list test
		if (forceRefresh) {
			this.refreshListTests();
		}
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

		log.config("loadDatas(" + forceRefresh + ")");
		if (forceRefresh) {

			this.refreshListTests();
		} else {
			this.view.resetUpdatingMode();
		}

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

	private void bind() {

		// handler pour les action sur les test comments
		this.view.setTestActionClickHandler(new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				Object src = event == null ? null : event.getSource();
				if (src != null && src instanceof TestActionButton) {
					TestActionButton testActionButton = (TestActionButton) src;
					TestActionButtonEnum action = testActionButton.getAction();
					int testId = testActionButton.getTestId();
					int tcommentId = testActionButton.getTCommentId();

					switch (action) {
					case createComment:
					case editComment:
						doEditOrCreateComment(testId, tcommentId);
						break;

					case deleteComment:
						beforeDeleteComment(testId, tcommentId);
						break;

					}
				}

			}
		});

		// handler pour la selection d'une tclass de test
		this.view.setSelectTClassHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				refreshListTests();

			}
		});

		// handler pour la selection d'un test name dans la liste des nom des
		// tests
		// (resultat du search)
		this.view.setSelectTestHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				VoSearchDefectDatas selectTestDatas = view.getTestDatas();
				currentDatas = selectTestDatas;
				doGetListTClasses(selectTestDatas);
			}
		});

		// handler pour la saisie dans la zone de recherche ou la modification
		// de la STB dans la listbox
		this.view.setSearchHandler(new ChangeHandler() {

			@Override
			public void onChange(ChangeEvent event) {

				VoSearchDefectDatas searchDatas = view.getSearchDatas();
				currentDatas = searchDatas;
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

	private void refreshListTests() {

		VoSearchDefectDatas selectTestDatas = view.getTestDatas();

		if (selectTestDatas.getTClassId() != IVo.ID_UNDEFINED) {
			currentDatas = selectTestDatas;
			doGetListTests(selectTestDatas);
		}

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

		log.fine("doSearch(" + searchDatas.toString() + ")");
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
	 * Edition ou creation du commentaire pour le test testId 
	 */
	private void doEditOrCreateComment(int testId, int tcommentId) {

		log.config("doEditComment(" + testId + ")");
		ViewReportEvent event = new ViewReportEvent(MainPanelViewEnum.editComment);
		Map<String, Object> params = ObjectUtils.buildMapWithOneItem(PARAMS_TEST_ID, new Integer(testId));
		params.put(PARAMS_TCOMMENT_ID, tcommentId);

		event.setParams(params);
		this.eventBus.fireEvent(event);
	}

	private void beforeDeleteComment(final int testId, final int tcommentId) {

		log.config("beforeDeleteComment(): " + tcommentId);

		VoTestInstanceForEdit voTest = this.mapId2Test.get(testId);
		VoIdName tclass = this.mapId2TClass.get(this.currentDatas.getTClassId());

		String[] message = new String[] { "Confirmer la suppression du commentaire du", // ...
				"Test: " + tclass.getName() + " - " + voTest.getName(), // ...
				"Suite " + voTest.getSuiteName() + " du " + voTest.getSuiteDate() // ...
		};

		final String comment = "";
		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused(comment, message, null, true,
				new IActionCallback() {

					@Override
					public void onCancel() {
						view.setActionResult(comment + " canceled!", LogStatus.warning);
					}

					@Override
					public void onOk() {
						view.setActionResult(comment + " in progres...", LogStatus.warning);
						doDeleteComment(testId, tcommentId);
					}

				});
		WidgetUtils.centerDialogAndShow(dialogBox);

	}

	/*
	 * Suppression commentaire pour le test testId 
	 */
	private void doDeleteComment(int testId, int tcommentId) {

		this.view.waiting(true);

		final String message = " when deleting test comment!";
		this.rpcService.deleteTComment(tcommentId, new MyAsyncCallback<Boolean>("Error " + message) {

			@Override
			public void onSuccess(Boolean result) {

				if (result) {
					view.setActionResult("Success " + message, LogStatus.success);
					loadDatas(true);
				} else {
					view.setActionResult("Failure " + message, LogStatus.warning);
				}
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

		this.rpcService.listTClassesForGroupIdAndTestName(testDatas, new MyAsyncCallback<List<VoIdName>>(
				"Error loading list of Test class!") {

			@Override
			public void onSuccess(List<VoIdName> listTClasses) {

				mapId2TClass = VoIdUtils.getMapId2Item(listTClasses);
				view.setActionResult("Success gettting list of tclass for " + testDatas.toString(), LogStatus.success);
				view.setTestTClasses(testDatas.getSearch(), listTClasses);

				searchRunning = false;
				view.waiting(false);

				// récupérer la liste des tests pour la première tclass de la
				// box
				if (listTClasses != null && listTClasses.size() > 0) {
					refreshListTests();
				}
			}

		});

	}

	/*
	 * Récupère la liste des tests du nom choisi par l'utilisateur pour le groupId et le tclassId courant 
	 */
	private void doGetListTests(final VoSearchDefectDatas testDatas) {

		if (searchRunning) {
			return;
		}

		log.config("doGetListTests(): groupId " + testDatas.getGroupId() + " - tclassId: " + testDatas.getTClassId()
				+ " - search: " + testDatas.getSearch());
		this.searchRunning = true;
		this.view.waiting(true);
		this.view.clearListTestPanel();
		this.rpcService.getListTestsForGroupIdTClassIdAndTestName(testDatas,
				new MyAsyncCallback<VoListTestsSameNameDatas>("Error getting list of tests!") {

					@Override
					public void onSuccess(VoListTestsSameNameDatas datas) {

						view.setActionResult("Success gettting list of tests for " + testDatas.toString(),
								LogStatus.success);

						if (datas != null) {
							mapId2Test = VoIdUtils.getMapId2Item(datas.getListTestsSameName());
						}
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
	public static interface IAnalysisView extends IMainView {

		public void setInitDatas(VoInitDefectDatas datas);

		public void setSearchHandler(ChangeHandler searchHandler);

		public void setSelectTestHandler(ChangeHandler selectTestHandler);

		public void setSelectTClassHandler(ChangeHandler selectTClassHandler);

		public VoSearchDefectDatas getSearchDatas();

		public VoSearchDefectDatas getTestDatas();

		public void setResultSearchDatas(VoResultSearchTestDatas datas);

		public void setTestDatas(VoListTestsSameNameDatas testDatas);

		public void setTestTClasses(String testName, List<VoIdName> listTClasses);

		public void setTestActionClickHandler(ClickHandler actionClickHandler);

		public void clearListTestPanel();

		void resetUpdatingMode();
	}

}
