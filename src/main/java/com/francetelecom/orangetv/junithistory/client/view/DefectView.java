package com.francetelecom.orangetv.junithistory.client.view;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.AppController.MainPanelViewEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.DefectPresenter.IDefectView;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndListWidget;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;
import com.francetelecom.orangetv.junithistory.shared.vo.VoInitDefectDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoResultDefectTestDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSearchDefectDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestInstanceForList;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.KeyUpEvent;
import com.google.gwt.event.dom.client.KeyUpHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TabPanel;

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
	public VoSearchDefectDatas getDatas() {

		int groupId = ValueHelper.getIntValue(this.wlistGroups.getListUserInput(), -1);
		String search = this.wSearchBox.getBoxUserInput();

		return new VoSearchDefectDatas(groupId, search);
	}

	@Override
	public void setResultSearchDatas(VoResultDefectTestDatas datas) {

		this.reinit();
		if (datas == null || datas.getListTests() == null) {
			return;
		}

		// for each test
		for (VoTestInstanceForList test : datas.getListTests()) {
			this.lbSearchResult.addItem(test.getName());
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
	}

	@Override
	public void lock() {
		// TODO Auto-generated method stub

	}

	@Override
	public void unlock() {
		// TODO Auto-generated method stub

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
		hpResult.setSpacing(PANEL_SPACING);
		hpResult.setWidth(MAX_WIDTH);
		hpResult.add(this.wlistSearchResult);
		hpResult.add(this.tabPanel);
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

}
