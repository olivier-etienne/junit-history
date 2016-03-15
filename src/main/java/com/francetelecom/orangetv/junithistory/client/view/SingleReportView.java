package com.francetelecom.orangetv.junithistory.client.view;

import java.util.Date;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.AppController.MainPanelViewEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.SingleReportPresenter.ISingleReportView;
import com.francetelecom.orangetv.junithistory.client.presenter.SingleReportPresenter.ViewActionEnum;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndListWidget;
import com.francetelecom.orangetv.junithistory.client.widget.MyUploader;
import com.francetelecom.orangetv.junithistory.client.widget.MyUploader.UploadHandler;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoInitSingleReportDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSingleReportData;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.datepicker.client.DatePicker;

public class SingleReportView extends AbstractMainView implements ISingleReportView {

	private final static Logger log = Logger.getLogger("SingleReportView");

	private ListBox lbGroups = new ListBox();
	private final LabelAndListWidget wlistGroups = new LabelAndListWidget("STB", 50, 212, lbGroups, 1);

	private final LabelAndBoxWidget wboxFirmware = new LabelAndBoxWidget("firmware", 50, 200);
	private final LabelAndBoxWidget wboxIptvkit = new LabelAndBoxWidget("iptvkit", 50, 200);

	private ListBox lbUsers = new ListBox();
	private final LabelAndListWidget wlistTesters = new LabelAndListWidget("tester", 50, 212, lbUsers, 1);

	private final LabelAndBoxWidget wboxDate = new LabelAndBoxWidget("date", 50, 100);
	private final DatePicker datePicker = new DatePicker();

	private final TextArea taComment = new TextArea();

	private final ButtonViewAction btActionShowReport = new ButtonViewAction("Show HTML report",
			ViewActionEnum.showHtmlReport.name(), "Show html site for the uploaded report");
	private final ButtonViewAction btActionAddToHistory = new ButtonViewAction("Add to history",
			ViewActionEnum.addToHistory.name(), "Add the report in the global history");
	private final ButtonViewAction btActionClear = new ButtonViewAction("Clear all", ViewActionEnum.clearAll.name(),
			"clear user input");

	// -------------- UPLOAD -----------------------------

	private final SimplePanel uploadPanel = new SimplePanel();
	private final MyUploader uploader = new MyUploader(100); // 100 Mo max

	// ---------------------------- implementing IView
	@Override
	public void lock() {
		this.enableButtonAndField(false);
	}

	@Override
	public void unlock() {
		this.enableButtonAndField(true);
	}

	@Override
	public MainPanelViewEnum getViewType() {
		return MainPanelViewEnum.singleReport;
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	// ---------------------------- implementing ISimpleReportView
	@Override
	public void reinit() {

		this.setDate(new Date());
		this.wboxFirmware.setValue(null);
		this.wboxIptvkit.setValue(null);
		this.taComment.setValue(null);

		this.wlistGroups.setValue(null);
		this.wlistTesters.setValue(null);
	}

	// ------------------------- constructor
	public SingleReportView() {
		super();
		super.init("Rapport Gwt JUnit individuel");
	}

	// -------------------------------------- implementing AbstractView

	@Override
	protected void buildBodyPanel() {

		this.main.add(uploadPanel);

		HorizontalPanel hPanel = new HorizontalPanel();
		hPanel.setWidth(MAX_WIDTH);
		hPanel.setSpacing(PANEL_SPACING);

		VerticalPanel vPanelLeft = new VerticalPanel();
		vPanelLeft.setSpacing(PANEL_SPACING);
		vPanelLeft.setWidth(MAX_WIDTH);
		vPanelLeft.add(this.wlistGroups);
		vPanelLeft.add(this.wboxFirmware);
		vPanelLeft.add(this.wboxIptvkit);
		vPanelLeft.add(this.wlistTesters);

		VerticalPanel vPanelCenter = new VerticalPanel();
		vPanelCenter.setWidth(MAX_WIDTH);
		vPanelCenter.setSpacing(PANEL_SPACING);
		vPanelCenter.add(new Label("Comment:"));
		vPanelCenter.add(this.taComment);

		VerticalPanel vPanelRight = new VerticalPanel();
		vPanelRight.setWidth(MAX_WIDTH);
		vPanelRight.setSpacing(PANEL_SPACING);
		vPanelRight.add(this.wboxDate);
		vPanelRight.add(this.datePicker);

		hPanel.add(vPanelLeft);
		hPanel.add(vPanelCenter);
		hPanel.add(vPanelRight);
		this.main.add(hPanel);

	}

	@Override
	protected void initComposants() {

		this.wboxDate.setEnabled(false);
		// this.setDate(new Date());

		this.taComment.addStyleName(SINGLE_REPORT_COMMENT);

	}

	@Override
	protected void initHandlers() {

		this.datePicker.addValueChangeHandler(new ValueChangeHandler<Date>() {

			@Override
			public void onValueChange(ValueChangeEvent<Date> event) {
				Date date = event.getValue();
				setDate(date);

			}
		});

	}

	@Override
	protected void buildButtonPanel() {
		super.addButton(this.btActionShowReport);
		super.addButton(this.btActionAddToHistory);
		super.addButton(this.btActionClear);

	}

	@Override
	public VoSingleReportData getSingleReportData() {
		VoSingleReportData datas = new VoSingleReportData();

		datas.setDate(this.datePicker.getValue());
		datas.setGroupId(ValueHelper.getIntValue(this.wlistGroups.getListUserInput(), IVo.ID_UNDEFINED));
		datas.setFirmware(this.wboxFirmware.getBoxUserInput());
		datas.setIptvkit(this.wboxIptvkit.getBoxUserInput());
		datas.setUserId(ValueHelper.getIntValue(this.wlistTesters.getListUserInput(), IVo.ID_UNDEFINED));
		datas.setComment(this.taComment.getText());

		return datas;
	}

	@Override
	public void setUploadHandler(UploadHandler handler) {
		this.uploadPanel.clear();
		this.uploadPanel.add(this.uploader.getUploadPanel());
		this.uploader.setUploadHandler(handler);
	}

	@Override
	public void setInitDatas(VoInitSingleReportDatas initDatas) {

		if (initDatas == null) {
			return;
		}
		log.config("setInitDatas()");
		super.populateList(this.lbGroups, initDatas.getListGroups());
		super.populateList(this.lbUsers, initDatas.getListUsers());

	}

	@Override
	public void setDatas(int groupId, String version) {

		this.wlistGroups.setValue(groupId + "");
		if (version != null) {
			this.wboxFirmware.setValue(version);
		}
	}

	// -------------------------------------- private methods
	private void setDate(Date date) {
		String dateString = DF.format(date);
		wboxDate.setValue(dateString);
	}

	private void enableButtonAndField(boolean enabled) {
		this.wlistGroups.setEnabled(enabled);
		this.wboxDate.setEnabled(enabled);
		this.wboxFirmware.setEnabled(enabled);
		this.wboxIptvkit.setEnabled(enabled);
		this.wlistTesters.setEnabled(enabled);
		this.taComment.setEnabled(enabled);
		this.uploader.setButtonDisabled(!enabled);

		this.btActionAddToHistory.enableButtonIfActif(enabled);
		this.btActionClear.enableButtonIfActif(enabled);
		this.btActionShowReport.enableButtonIfActif(enabled);
	}

}
