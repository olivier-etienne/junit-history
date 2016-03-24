package com.francetelecom.orangetv.junithistory.client.view;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.AppController.MainPanelViewEnum;
import com.francetelecom.orangetv.junithistory.client.panel.analysis.TestInfoPanel;
import com.francetelecom.orangetv.junithistory.client.presenter.AnalysisPresenter.IAnalysisView;
import com.francetelecom.orangetv.junithistory.client.presenter.AnalysisPresenter.TestActionButtonEnum;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndListWidget;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.IVoId;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdName;
import com.francetelecom.orangetv.junithistory.shared.vo.VoInitDefectDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoListTestsSameNameDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoResultSearchTestDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSearchDefectDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestDistinctName;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestInstanceForEdit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AnalysisView extends AbstractMainView implements IAnalysisView {
	private final static Logger log = Logger.getLogger("AnalysisView");

	private ListBox lbGroups = new ListBox();
	private final LabelAndListWidget wlistGroups = new LabelAndListWidget("STB", 50, 212, lbGroups, 1);

	private final LabelAndBoxWidget wSearchBox = new LabelAndBoxWidget("search", 50, 300);

	private final ListBox lbSearchResult = new ListBox();
	private final LabelAndListWidget wlistSearchResult = new LabelAndListWidget("", 0, 200, lbSearchResult, 20);

	private final Panel defectPanel = new SimplePanel();

	private final LabelAndBoxWidget wTestNameBox = new LabelAndBoxWidget("Test name", 100, 250);

	private final ListBox lbTClasses = new ListBox();
	private final LabelAndListWidget wlistTClasses = new LabelAndListWidget("TClass", 100, 250, this.lbTClasses, 1);

	private ClickHandler actionClickHandler;
	private final List<TestInfoPanel> listTestInfoPanels = new ArrayList<>();

	// ------------------------------- implementing IAnalysisView
	@Override
	public void resetUpdatingMode() {

		// for each test panel
		for (TestInfoPanel testInfoPanel : this.listTestInfoPanels) {
			testInfoPanel.resetUpdatingMode();
		}
	}

	@Override
	public void setTestActionClickHandler(ClickHandler actionClickHandler) {
		this.actionClickHandler = actionClickHandler;
	}

	@Override
	public void setInitDatas(VoInitDefectDatas initDatas) {

		if (initDatas == null) {
			return;
		}
		log.config("setInitDatas()");
		super.populateList(this.lbGroups, initDatas.getListGroups());

	}

	@Override
	public void setSelectTClassHandler(ChangeHandler selectTClassHandler) {
		this.wlistTClasses.addChangeHandler(selectTClassHandler);
	}

	@Override
	public void setSelectTestHandler(ChangeHandler selectTestHandler) {
		this.wlistSearchResult.addChangeHandler(selectTestHandler);
	}

	@Override
	public void setSearchHandler(final ChangeHandler searchHandler) {
		this.wlistGroups.addChangeHandler(searchHandler);
		this.wSearchBox.addChangeHandler(searchHandler);

		final KeyUpHandler keyUpHandler = new KeyUpHandler() {

			@Override
			public void onKeyUp(KeyUpEvent event) {
				searchHandler.onChange(new MyChangeEvent());
			}
		};
		this.wSearchBox.addKeyUpHandler(keyUpHandler);
	}

	private class MyChangeEvent extends ChangeEvent {

		public MyChangeEvent() {
			super();
		}

	}

	@Override
	public VoSearchDefectDatas getSearchDatas() {

		int groupId = ValueHelper.getIntValue(this.wlistGroups.getListUserInput(), IVo.ID_UNDEFINED);
		String search = this.wSearchBox.getBoxUserInput();

		return new VoSearchDefectDatas(groupId, search);
	}

	@Override
	public VoSearchDefectDatas getTestDatas() {

		int groupId = ValueHelper.getIntValue(this.wlistGroups.getListUserInput(), IVo.ID_UNDEFINED);
		String testName = this.wlistSearchResult.getListUserInput();

		int tclassId = (this.wlistTClasses.getListBox().getItemCount() <= 0) ? IVo.ID_UNDEFINED : ValueHelper
				.getIntValue(this.wlistTClasses.getListUserInput(), IVo.ID_UNDEFINED);

		return new VoSearchDefectDatas(groupId, tclassId, testName);
	}

	@Override
	public void setTestDatas(VoListTestsSameNameDatas testDatas) {

		if (testDatas == null || testDatas.getListTestsSameName() == null) {
			return;
		}

		final VerticalPanel vpTests = new VerticalPanel();
		vpTests.setSpacing(PANEL_SPACING);
		vpTests.setWidth(MAX_WIDTH);

		// for each test
		log.config("tests count: " + testDatas.getListTestsSameName().size());
		for (VoTestInstanceForEdit voTest : testDatas.getListTestsSameName()) {

			TestInfoPanel testPanel = new TestInfoPanel();
			testPanel.setTestActionClickHandler(this.actionClickHandler);
			vpTests.add(testPanel);

			testPanel.setDatas(voTest);
			this.listTestInfoPanels.add(testPanel);
		}

		this.defectPanel.add(vpTests);

	}

	@Override
	public void clearListTestPanel() {
		this.defectPanel.clear();
		this.listTestInfoPanels.clear();
	}

	@Override
	public void setTestTClasses(String testName, List<VoIdName> listTClasses) {

		this.clearListClasses();
		this.clearListTestPanel();
		if (listTClasses == null) {
			return;
		}

		this.wTestNameBox.setValue(testName);
		// for each TClass
		for (VoIdName voTClass : listTClasses) {
			this.lbTClasses.addItem(voTClass.getName(), voTClass.getId() + "");
		}

	}

	@Override
	public void setResultSearchDatas(VoResultSearchTestDatas datas) {

		this.reinit();
		if (datas == null || datas.getListTests() == null) {
			return;
		}

		// for each test
		for (VoTestDistinctName voName : datas.getListTests()) {
			this.lbSearchResult.addItem(voName.getDistinctName());
		}

	}

	// ------------------------- constructor
	public AnalysisView() {
		super();
		super.init("Analyse des tests");
	}

	// ----------------------------- implementing IMainView
	@Override
	public MainPanelViewEnum getViewType() {
		return MainPanelViewEnum.analysis;
	}

	// ----------------------------- implementing IView
	@Override
	public void reinit() {
		this.lbSearchResult.clear();
		this.clearListTestPanel();
	}

	@Override
	public void lock() {

		this.enableButtonAndField(false);
	}

	@Override
	public void unlock() {

		this.enableButtonAndField(true);

	}

	// ----------------------------- implementing AbstractView
	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected void buildBodyPanel() {

		final HorizontalPanel hpStbAndSearch = new HorizontalPanel();
		hpStbAndSearch.setSpacing(PANEL_SPACING);
		hpStbAndSearch.add(this.wlistGroups);
		hpStbAndSearch.add(this.wSearchBox);
		this.main.add(hpStbAndSearch);

		final HorizontalPanel hpTitle = new HorizontalPanel();
		hpTitle.setWidth(MAX_WIDTH);
		hpTitle.setSpacing(PANEL_SPACING);
		hpTitle.add(this.wTestNameBox);
		hpTitle.add(this.wlistTClasses);

		final VerticalPanel vpInfoTest = new VerticalPanel();
		vpInfoTest.setWidth(MAX_WIDTH);
		vpInfoTest.setSpacing(PANEL_SPACING);
		vpInfoTest.add(hpTitle);
		vpInfoTest.add(this.defectPanel);

		final HorizontalPanel hpResult = new HorizontalPanel();
		hpResult.setWidth(MAX_WIDTH);
		hpResult.setSpacing(PANEL_SPACING);
		hpResult.add(this.wlistSearchResult);
		hpResult.add(vpInfoTest);

		this.main.add(hpResult);

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

		this.lbSearchResult.setStyleName(LIST_TEST_NAMES);
		this.wTestNameBox.setEnabled(false);
		this.wTestNameBox.setBoxStyleName(LIST_TEST_NAMES);

		this.lbTClasses.setStyleName(LIST_TEST_NAMES);

	}

	// ----------------------------------- private methods

	private void clearListClasses() {
		this.wTestNameBox.setValue("");
		this.lbTClasses.clear();
	}

	private void enableButtonAndField(boolean enabled) {

		this.wlistGroups.setEnabled(enabled);
		this.wlistSearchResult.setEnabled(enabled);
		this.wlistTClasses.setEnabled(enabled);
	}

	// ===================================== INNER CLASS
	public static class CreateCommentButton extends TestActionButton {

		public CreateCommentButton(int testId) {
			super(testId, IVoId.ID_UNDEFINED, TestActionButtonEnum.createComment, "Create a comment", true);
			this.addButtonStyleName(STYLE_IMG_CREATE_COMMENT);
		}
	}

	public static class DeleteCommentButton extends TestActionButton {

		public DeleteCommentButton(int testId, int tcommentId) {
			super(testId, tcommentId, TestActionButtonEnum.deleteComment, "Delete the comment", false);
			this.addButtonStyleName(STYLE_IMG_DELETE);
		}
	}

	public static class EditCommentButton extends TestActionButton {

		public EditCommentButton(int testId, int tcommentId) {
			super(testId, tcommentId, TestActionButtonEnum.editComment, "Edit the comment", true);
			this.addButtonStyleName(STYLE_IMG_EDIT);
		}
	}

	public static abstract class TestActionButton extends Button {

		private final int testId;
		private final int tcommentId;
		private final boolean updatingMode;
		private final TestActionButtonEnum action;

		public TestActionButtonEnum getAction() {
			return this.action;
		}

		public int getTestId() {
			return this.testId;
		}

		public int getTCommentId() {
			return this.tcommentId;
		}

		public boolean isUpdatingMode() {
			return this.updatingMode;
		}

		private TestActionButton(int testId, int tcommentId, TestActionButtonEnum action, String title,
				boolean updatingMode) {
			this.testId = testId;
			this.tcommentId = tcommentId;
			this.action = action;
			if (title != null) {
				super.setTitle(title);
			}
			this.updatingMode = updatingMode;
		}

		protected void addButtonStyleName(String stylename) {
			this.addStyleName(STYLE_IMG_ACTION + " " + stylename);
		}

		protected void removeButtonStyleName(String stylename) {
			this.removeStyleName(STYLE_IMG_ACTION + " " + stylename);
		}

	}

}
