package com.francetelecom.orangetv.junithistory.client.view.admin;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.presenter.PageAdminPresenter.TabViewEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.EditUserPresenter.IEditUserView;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUserForEdit;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUserProtection;

public class EditUserView extends AbstractEditView implements IEditUserView {

	private final static Logger log = Logger.getLogger("EditUserView");

	private final LabelAndBoxWidget tbName = new LabelAndBoxWidget("name:", 80, 300);
	private final LabelAndBoxWidget tbDescription = new LabelAndBoxWidget("description:", 80, 300);

	private VoUserForEdit user;

	// ---------------------------- constructor
	public EditUserView() {
		super("user");
		super.init("Edit user");
	}

	// ---------------------------- implementing IEditItemView
	@Override
	public TabViewEnum getType() {
		return TabViewEnum.tabUser;
	}

	// --------------------------- overriding IEditUserView
	@Override
	public VoUserForEdit getVoDatas() {
		this.user.setName(this.tbName.getBoxUserInput());
		this.user.setDescription(this.tbDescription.getBoxUserInput());
		return this.user;
	}

	@Override
	public void setDatas(VoUserForEdit voUser) {

		VoUserProtection protection = voUser.getUserProtection();
		this.user = voUser;
		this.tbName.setValue(voUser.getName());
		this.tbDescription.setValue(voUser.getDescription());

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
