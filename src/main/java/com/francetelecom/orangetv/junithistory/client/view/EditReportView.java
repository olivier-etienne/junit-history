package com.francetelecom.orangetv.junithistory.client.view;

import java.util.Date;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.AppController.MainPanelViewEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.EditReportPresenter.IEditReportView;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndDateWidget;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndListWidget;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoEditReportDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestSuiteForEdit;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;

/**
 * Vue pour l'edition d'un rapport existant
 * 
 * @author ndmz2720
 * 
 */
public class EditReportView extends AbstractEditView implements IEditReportView {

	private final static Logger log = Logger.getLogger("EditReportView");

	private final LabelAndBoxWidget wboxName = new LabelAndBoxWidget("name", 50, 200);

	private final LabelAndBoxWidget wboxFirmware = new LabelAndBoxWidget("firmware", 50, 200);
	private final LabelAndBoxWidget wboxIptvkit = new LabelAndBoxWidget("iptvkit", 50, 200);

	private ListBox lbTesters = new ListBox();
	private final LabelAndListWidget wlistTesters = new LabelAndListWidget("tester", 50, 212, lbTesters, 1);

	private final LabelAndDateWidget wboxDate = new LabelAndDateWidget("Date", 50, 200, DF, null);

	private final TextArea taComment = new TextArea();

	private VoTestSuiteForEdit currentTestSuite;

	// ------------------------- constructor
	public EditReportView() {
		super("report");
		super.init("Edit report");
	}

	// ---------------------------------- implementing IMainView

	@Override
	public MainPanelViewEnum getViewType() {
		return MainPanelViewEnum.editReport;
	}

	// --------------------------------- overriding AbstractView
	@Override
	public void reinit() {
		this.wboxDate.setValue(new Date());
		this.wboxName.setValue(null);
		this.wboxFirmware.setValue(null);
		this.wboxIptvkit.setValue(null);
		this.taComment.setValue(null);

		this.lbTesters.clear();
		this.wlistTesters.setValue(null);

	}

	// --------------------------------------- implementing IEditReportView
	@Override
	protected void buildBodyPanel() {

		this.main.add(this.wboxName);
		this.main.add(this.wboxFirmware);

		this.main.add(this.wboxIptvkit);
		this.main.add(this.wboxDate);

		this.main.add(this.wlistTesters);
		this.main.add(this.taComment);

	}

	@Override
	public void setDatas(VoEditReportDatas datas) {

		this.currentTestSuite = datas.getSuiteForEdit();
		this.enableButtonAndField(!this.currentTestSuite.isReadOnly());

		this.reinit();
		if (this.currentTestSuite == null) {
			return;
		}

		this.wboxName.setValue(this.currentTestSuite.getName());
		this.wboxFirmware.setValue(this.currentTestSuite.getFirmware());
		this.wboxIptvkit.setValue(this.currentTestSuite.getIptvkit());
		this.wboxDate.setValue(this.currentTestSuite.getDate());
		this.taComment.setValue(this.currentTestSuite.getComment());
		super.populateList(this.lbTesters, datas.getListUsers());
		this.wlistTesters.setValue(this.currentTestSuite.getUserId() + "");
	}

	@Override
	public VoTestSuiteForEdit getEditReportData() {

		this.currentTestSuite.setComment(this.taComment.getValue());
		this.currentTestSuite.setDate(this.wboxDate.getUserInput());
		this.currentTestSuite
				.setUserId(ValueHelper.getIntValue(this.wlistTesters.getListUserInput(), IVo.ID_UNDEFINED));
		this.currentTestSuite.setIptvkit(this.wboxIptvkit.getBoxUserInput());
		this.currentTestSuite.setFirmware(this.wboxFirmware.getBoxUserInput());

		return this.currentTestSuite;
	}

	@Override
	protected void initHandlers() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initComposants() {
		this.wboxName.setEnabled(false);
		this.wboxFirmware.setEnabled(false);

		this.taComment.addStyleName(SINGLE_REPORT_COMMENT);
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	// ------------------------------ private methods

	protected void enableButtonAndField(boolean enabled) {
		this.wboxDate.setEnabled(enabled);
		this.wboxIptvkit.setEnabled(enabled);
		this.wlistTesters.setEnabled(enabled);
		this.taComment.setEnabled(enabled);

		super.enableButtonAndField(enabled);
	}

}
