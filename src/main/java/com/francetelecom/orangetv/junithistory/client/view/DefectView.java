package com.francetelecom.orangetv.junithistory.client.view;

import java.util.List;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.AppController.MainPanelViewEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.DefectPresenter.IDefectView;
import com.francetelecom.orangetv.junithistory.client.util.StatusUtils;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndListWidget;
import com.francetelecom.orangetv.junithistory.shared.TestSubStatusEnum;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdName;
import com.francetelecom.orangetv.junithistory.shared.vo.VoInitDefectDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoListTestsSameNameDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoResultSearchTestDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSearchDefectDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestDistinctName;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestInstanceForEdit;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class DefectView extends AbstractMainView implements IDefectView {
	private final static Logger log = Logger.getLogger("DefectView");

	private ListBox lbGroups = new ListBox();
	private final LabelAndListWidget wlistGroups = new LabelAndListWidget("STB", 50, 212, lbGroups, 1);

	private final LabelAndBoxWidget wSearchBox = new LabelAndBoxWidget("search", 50, 300);

	private final ListBox lbSearchResult = new ListBox();
	private final LabelAndListWidget wlistSearchResult = new LabelAndListWidget("", 0, 200, lbSearchResult, 20);

	private final TabPanel tabPanel = new TabPanel();

	private final Panel defectPanel = new SimplePanel();
	private final Panel commentPanel = new SimplePanel();

	private final LabelAndBoxWidget wTestNameBox = new LabelAndBoxWidget("Test name", 100, 250);

	private final ListBox lbTClasses = new ListBox();
	private final LabelAndListWidget wlistTClasses = new LabelAndListWidget("TClass", 100, 250, this.lbTClasses, 1);

	// ------------------------------- implementing IDefectView
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

		this.clearTestPanels();

		if (testDatas == null || testDatas.getListTestsSameName() == null) {
			return;
		}

		final VerticalPanel vpTests = new VerticalPanel();
		vpTests.setSpacing(PANEL_SPACING);
		vpTests.setWidth(MAX_WIDTH);

		// for each test
		log.config("tests count: " + testDatas.getListTestsSameName().size());
		for (VoTestInstanceForEdit voTest : testDatas.getListTestsSameName()) {

			log.config("add test: " + voTest.getId());
			TestInformationPanel testPanel = new TestInformationPanel();
			vpTests.add(testPanel);

			testPanel.setDatas(voTest);
		}

		this.defectPanel.add(vpTests);
	}

	@Override
	public void setTestTClasses(String testName, List<VoIdName> listTClasses) {

		this.clearListClasses();
		this.clearTestPanels();
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
	public DefectView() {
		super();
		super.init("Gestion des defects");
	}

	// ----------------------------- implementing IMainView
	@Override
	public MainPanelViewEnum getViewType() {
		return MainPanelViewEnum.defect;
	}

	// ----------------------------- implementing IView
	@Override
	public void reinit() {
		this.lbSearchResult.clear();
		this.clearTestPanels();
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

		// tab panel
		this.tabPanel.setWidth(MAX_WIDTH);
		this.tabPanel.setHeight(MAX_WIDTH);
		this.tabPanel.add(this.defectPanel, "output");
		this.tabPanel.add(this.commentPanel, "comments");
		this.tabPanel.selectTab(0);

		final HorizontalPanel hpTitle = new HorizontalPanel();
		hpTitle.setWidth(MAX_WIDTH);
		hpTitle.setSpacing(PANEL_SPACING);
		hpTitle.add(this.wTestNameBox);
		hpTitle.add(this.wlistTClasses);

		final VerticalPanel vpInfoTest = new VerticalPanel();
		vpInfoTest.setWidth(MAX_WIDTH);
		vpInfoTest.setSpacing(PANEL_SPACING);
		vpInfoTest.add(hpTitle);
		vpInfoTest.add(this.tabPanel);

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
	private void clearTestPanels() {
		this.defectPanel.clear();
		this.commentPanel.clear();

	}

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

	/**
	 * Panel pour les informations d'un TestInstance
	 * 
	 * @author NDMZ2720
	 *
	 */
	private final class TestInformationPanel extends SimplePanel {

		private VerticalPanel vpBody = new VerticalPanel();

		private Panel suiteNamePanel = new SimplePanel();
		private Label labelSuiteDate = new Label();
		private Label labelTestStatus = new Label();

		private LabelAndBoxWidget wTypeBox = new LabelAndBoxWidget("Type", 50, 500);
		private LabelAndBoxWidget wMessageBox = new LabelAndBoxWidget("Message", 50, 500);

		private TextArea taOutputLogs = new TextArea();
		private TextArea taStackTrace = new TextArea();

		// ------------------------------ constructor
		private TestInformationPanel() {
			this.initComposants();
			this.add(this.buildBodyPanel());
		}

		// ---------------------------- private methods
		private void initComposants() {

			this.setStyleName(PANEL_TEST_INFO);

			this.taOutputLogs.setStyleName(TEXT_AREA_TEST_INFO);
			this.taOutputLogs.setEnabled(false);

			this.taStackTrace.setStyleName(TEXT_AREA_TEST_INFO);
			this.taStackTrace.setEnabled(false);

			this.wTypeBox.setEnabled(false);
			this.wMessageBox.setEnabled(false);

			this.suiteNamePanel.addStyleName(STYLE_SUITE_NAME);
			this.labelSuiteDate.addStyleName(STYLE_SUITE_DATE);

		}

		private Panel buildBodyPanel() {

			this.vpBody.setWidth(MAX_WIDTH);

			final VerticalPanel vpPanel = new VerticalPanel();
			vpPanel.setSpacing(PANEL_SPACING);
			vpPanel.setWidth(MAX_WIDTH);
			vpPanel.setBorderWidth(1);

			final HorizontalPanel hpTitle = new HorizontalPanel();
			hpTitle.setSpacing(PANEL_SPACING);
			hpTitle.setWidth(MAX_WIDTH);
			hpTitle.add(this.suiteNamePanel);
			hpTitle.add(this.labelSuiteDate);
			hpTitle.setCellHorizontalAlignment(this.labelSuiteDate, HasHorizontalAlignment.ALIGN_CENTER);
			hpTitle.add(this.labelTestStatus);
			hpTitle.setCellHorizontalAlignment(this.labelTestStatus, HasHorizontalAlignment.ALIGN_RIGHT);
			vpPanel.add(hpTitle);

			// body (peut etre masqué)
			this.vpBody.add(this.wTypeBox);
			this.vpBody.add(this.wMessageBox);

			this.vpBody.add(new Label("output logs:"));
			this.vpBody.add(this.taOutputLogs);

			this.vpBody.add(new Label("stack trace"));
			this.vpBody.add(this.taStackTrace);
			vpPanel.add(this.vpBody);
			return vpPanel;
		}

		private void setDatas(VoTestInstanceForEdit voTest) {

			this.suiteNamePanel.add(new Label(voTest.getSuiteName()));
			this.labelSuiteDate.setText(voTest.getSuiteDate());
			StatusUtils.buildTestStatus(this.labelTestStatus, TestSubStatusEnum.valueOf(voTest.getStatus()));

			if (voTest.getType() != null) {
				this.wTypeBox.setValue(voTest.getType());
				this.wMessageBox.setValue(voTest.getMessage());
				this.taOutputLogs.setValue(voTest.getOutputLog());
				this.taStackTrace.setValue(voTest.getStackTrace());
				this.vpBody.setVisible(true);
			} else {
				this.vpBody.setVisible(false);
			}
		}

	}

}
