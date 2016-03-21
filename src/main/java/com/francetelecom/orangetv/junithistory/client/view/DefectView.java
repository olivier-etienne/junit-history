package com.francetelecom.orangetv.junithistory.client.view;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.AppController.MainPanelViewEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.DefectPresenter.IDefectView;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndListWidget;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;
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

	private ListBox lbSearchResult = new ListBox();
	private final LabelAndListWidget wlistSearchResult = new LabelAndListWidget("", 0, 200, lbSearchResult, 20);

	private final TabPanel tabPanel = new TabPanel();

	private final Panel defectPanel = new SimplePanel();
	private final Panel commentPanel = new SimplePanel();

	// ------------------------------- implementing IDefectView
	@Override
	public void setInitDatas(VoInitDefectDatas initDatas) {

		if (initDatas == null) {
			return;
		}
		log.config("setInitDatas()");
		super.populateList(this.lbGroups, initDatas.getListGroups());

	}

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

		int groupId = ValueHelper.getIntValue(this.wlistGroups.getListUserInput(), -1);
		String search = this.wSearchBox.getBoxUserInput();

		return new VoSearchDefectDatas(groupId, search);
	}

	@Override
	public VoSearchDefectDatas getTestDatas() {

		int groupId = ValueHelper.getIntValue(this.wlistGroups.getListUserInput(), -1);
		String testName = this.wlistSearchResult.getListUserInput();

		return new VoSearchDefectDatas(groupId, testName);
	}

	@Override
	public void setTestDatas(VoListTestsSameNameDatas testDatas) {

		this.clearTestPanels();

		if (testDatas == null || testDatas.getListTestsSameName() == null) {
			return;
		}

		final VerticalPanel vpTests = new VerticalPanel();
		vpTests.setSpacing(PANEL_SPACING);

		// for each test
		for (VoTestInstanceForEdit voTest : testDatas.getListTestsSameName()) {

			TestInformationPanel testPanel = new TestInformationPanel();
			vpTests.add(testPanel);

			testPanel.setDatas(voTest);
		}

		this.defectPanel.add(vpTests);
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

		this.tabPanel.setWidth(MAX_WIDTH);
		this.tabPanel.setHeight(MAX_WIDTH);
		this.tabPanel.add(this.defectPanel, "output");
		this.tabPanel.add(this.commentPanel, "comments");
		this.tabPanel.selectTab(0);

		final HorizontalPanel hpResult = new HorizontalPanel();
		hpResult.add(this.wlistSearchResult);
		hpResult.add(this.tabPanel);
		hpResult.setWidth(MAX_WIDTH);
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
		// TODO Auto-generated method stub

	}

	// ----------------------------------- private methods
	private void clearTestPanels() {
		this.defectPanel.clear();
		this.commentPanel.clear();
	}

	private void enableButtonAndField(boolean enabled) {

		this.wlistGroups.setEnabled(enabled);
		this.wSearchBox.setEnabled(enabled);
		this.wlistSearchResult.setEnabled(enabled);
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

		private Label labelSuiteName = new Label();
		private Label labelSuiteDate = new Label();
		private Label labelTestStatus = new Label();

		private LabelAndBoxWidget wTypeBox = new LabelAndBoxWidget("Type", 50, 200);
		private LabelAndBoxWidget wMessageBox = new LabelAndBoxWidget("Message", 50, 200);

		private TextArea taOutputLogs = new TextArea();
		private TextArea taStackTrace = new TextArea();

		// ------------------------------ constructor
		private TestInformationPanel() {
			this.initComposants();
			this.add(this.buildBodyPanel());
		}

		// ---------------------------- private methods
		private void initComposants() {
			this.taOutputLogs.setWidth(MAX_WIDTH);
			this.taOutputLogs.setHeight("50px");
			this.taOutputLogs.setEnabled(false);

			this.taStackTrace.setWidth(MAX_WIDTH);
			this.taStackTrace.setHeight("50px");
			this.taStackTrace.setEnabled(false);

			this.wTypeBox.setEnabled(false);
			this.wMessageBox.setEnabled(false);

		}

		private Panel buildBodyPanel() {

			final VerticalPanel vpPanel = new VerticalPanel();
			vpPanel.setSpacing(PANEL_SPACING);
			vpPanel.setBorderWidth(1);

			final HorizontalPanel hpTitle = new HorizontalPanel();
			hpTitle.setSpacing(PANEL_SPACING);
			hpTitle.add(this.labelSuiteName);
			hpTitle.add(this.labelSuiteDate);
			hpTitle.add(this.labelTestStatus);
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

			this.labelSuiteName.setText(voTest.getSuiteName());
			this.labelSuiteDate.setText(voTest.getSuiteDate());
			this.labelTestStatus.setText(voTest.getStatus());

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
