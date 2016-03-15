package com.francetelecom.orangetv.junithistory.client.view.admin;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.presenter.PageAdminPresenter.TabViewEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.EditGroupPresenter.IEditGroupView;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.junithistory.shared.vo.VoGroupForEdit;
import com.francetelecom.orangetv.junithistory.shared.vo.VoGroupProtection;

/**
 * View pour l'edition d'un group
 */
public class EditGroupView extends AbstractEditView implements IEditGroupView {

	private final static Logger log = Logger.getLogger("EditUserView");

	private final static String TITLE = "STB group";

	private final LabelAndBoxWidget tbName = new LabelAndBoxWidget("name:", 80, 300);
	private final LabelAndBoxWidget tbStb = new LabelAndBoxWidget("STB:", 80, 300);
	private final LabelAndBoxWidget tbPrefix = new LabelAndBoxWidget("PREFIX:", 80, 300);

	private VoGroupForEdit group;

	// ---------------------------- constructor
	public EditGroupView() {
		super("stb");
		super.init(TITLE);
	}

	// ---------------------------- implementing IEditItemView
	@Override
	public TabViewEnum getType() {
		return TabViewEnum.tabGroup;
	}

	// --------------------------- overriding IEditUserView
	@Override
	public VoGroupForEdit getVoDatas() {
		this.group.setName(this.tbName.getBoxUserInput());
		this.group.setStb(this.tbStb.getBoxUserInput());
		this.group.setPrefix(this.tbPrefix.getBoxUserInput());
		return this.group;
	}

	@Override
	public void setDatas(VoGroupForEdit voGroup) {

		super.changeTitle(TITLE, voGroup.isIdUndefined());
		this.group = voGroup;
		this.tbName.setValue(voGroup.getName());
		this.tbStb.setValue(voGroup.getStb());
		this.tbPrefix.setValue(voGroup.getPrefix());

		VoGroupProtection groupProtection = voGroup.getGroupProtection();
		this.tbPrefix.setEnabled(groupProtection == null ? true : groupProtection.canUpdatePrefix());

	}

	// --------------------------- overriding AbstractView
	@Override
	public void reinit() {
		this.tbName.setValue(null);
		this.tbStb.setValue(null);
		this.tbPrefix.setValue(null);
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected void buildBodyPanel() {

		this.main.add(this.tbName);
		this.main.add(this.tbStb);
		this.main.add(this.tbPrefix);

	}

}
