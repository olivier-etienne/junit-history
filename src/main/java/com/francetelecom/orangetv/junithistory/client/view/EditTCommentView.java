package com.francetelecom.orangetv.junithistory.client.view;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.AppController.MainPanelViewEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.EditTCommentPresenter.IEditTCommentView;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndListWidget;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoEditTCommentDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestCommentForEdit;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextArea;

/**
 * View pour l'edition ou la creation d'un commentaire de test
 * 
 * @author ndmz2720
 *
 */
public class EditTCommentView extends AbstractEditView implements IEditTCommentView {

	private final static Logger log = Logger.getLogger("EditTCommentView");

	private final static String TITLE = "Comment";

	private ListBox lbTesters = new ListBox();
	private final LabelAndListWidget wlistTesters = new LabelAndListWidget("tester", 50, 212, lbTesters, 1);

	private final LabelAndBoxWidget wSuiteTitle = new LabelAndBoxWidget("Suite", 50, 300);
	private final LabelAndBoxWidget wTestTitle = new LabelAndBoxWidget("Test", 50, 300);

	private final LabelAndBoxWidget wCommentTitle = new LabelAndBoxWidget("Title", 50, 300);
	private final TextArea taCommentDesc = new TextArea();

	private VoTestCommentForEdit tcomment;

	// -------------------------------------------- constructor
	public EditTCommentView() {
		super("Comment");
		super.init(TITLE);
	}

	// -------------------------- implementing IEdiCommentView

	@Override
	public VoTestCommentForEdit getVoDatas() {

		this.tcomment.setTitle(this.wCommentTitle.getBoxUserInput());
		this.tcomment.setDescription(this.taCommentDesc.getValue());
		this.tcomment.setTesterId(ValueHelper.getIntValue(this.wlistTesters.getListUserInput(), IVo.ID_UNDEFINED));
		return this.tcomment;
	}

	@Override
	public void setDatas(VoEditTCommentDatas voDatas) {

		this.reinit();
		this.tcomment = voDatas.getTCommentForEdit();

		super.populateList(this.lbTesters, voDatas.getListTesters());
		boolean createMode = this.tcomment.isIdUndefined();

		this.wSuiteTitle.setValue(this.tcomment.getSuiteTitle());
		this.wTestTitle.setValue(this.tcomment.getTestTitle());

		if (!createMode) {
			this.changeTitle(TITLE, false);

			this.wlistTesters.setValue(this.tcomment.getTesterId() + "");

			this.wCommentTitle.setValue(this.tcomment.getTitle());
			this.taCommentDesc.setValue(this.tcomment.getDescription());
		} else {

			this.changeTitle(TITLE, true);
		}
	}

	// -------------------------- implementing IView
	@Override
	public void reinit() {

		this.wTestTitle.setValue(null);
		this.wSuiteTitle.setValue(null);
		this.wCommentTitle.setValue(null);
		this.taCommentDesc.setValue(null);

		this.lbTesters.clear();
		this.wlistTesters.setValue(null);
	}

	// ---------------------------------- implementing IMainView

	@Override
	public MainPanelViewEnum getViewType() {
		return MainPanelViewEnum.editComment;
	}

	// --------------------------- overriding AbstractEditView
	@Override
	protected void initComposants() {
		super.initComposants();
		this.taCommentDesc.setWidth(MAX_WIDTH);
		this.wSuiteTitle.setEnabled(false);
		this.wTestTitle.setEnabled(false);

	}

	// -------------------------- implementing AbstractView
	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected void buildBodyPanel() {

		this.main.add(this.wSuiteTitle);
		this.main.add(this.wTestTitle);

		this.main.add(this.wlistTesters);
		this.main.add(new Label("Comment:"));
		this.main.add(this.wCommentTitle);

		this.main.add(this.taCommentDesc);

	}

}
