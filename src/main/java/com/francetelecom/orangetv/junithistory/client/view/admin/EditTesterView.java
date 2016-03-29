package com.francetelecom.orangetv.junithistory.client.view.admin;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.presenter.PageAdminPresenter.TabAdminViewEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.EditTesterPresenter.IEditTesterView;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUserForEdit;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUserProtection;

public class EditTesterView extends AbstractEditAdminView implements IEditTesterView {

	private final static Logger log = Logger.getLogger("EditTesterView");

	private final static String TITLE = "tester";

	private final LabelAndBoxWidget tbName = new LabelAndBoxWidget("name:", 80, 300);
	private final LabelAndBoxWidget tbDescription = new LabelAndBoxWidget("description:", 80, 300);

	private VoUserForEdit tester;

	// ---------------------------- constructor
	public EditTesterView() {
		super(TITLE);
		super.init(TITLE);
	}

	// ---------------------------- implementing IEditItemView
	@Override
	public TabAdminViewEnum getType() {
		return TabAdminViewEnum.tabTester;
	}

	// --------------------------- overriding IEditUserView
	@Override
	public VoUserForEdit getVoDatas() {
		this.tester.setName(this.tbName.getBoxUserInput());
		this.tester.setDescription(this.tbDescription.getBoxUserInput());
		return this.tester;
	}

	@Override
	public void setDatas(VoUserForEdit voTester) {

		super.changeTitle(TITLE, voTester.isIdUndefined());

		VoUserProtection protection = voTester.getUserProtection();
		this.tester = voTester;
		this.tbName.setValue(voTester.getName());
		this.tbDescription.setValue(voTester.getDescription());

		this.tbName.setEnabled(protection == null ? true : protection.canUpdateName());
	}

	// --------------------------- overriding AbstractView
	@Override
	public void reinit() {
		this.tbName.setValue(null);
		this.tbDescription.setValue(null);
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected void buildBodyPanel() {

		this.main.add(this.tbName);
		this.main.add(this.tbDescription);

	}
}
