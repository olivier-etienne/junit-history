package com.francetelecom.orangetv.junithistory.client.view.admin;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.presenter.PageAdminPresenter.TabViewEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.EditCategoryPresenter.IEditCategoryView;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.junithistory.shared.vo.VoCategoryForEdit;

/**
 * View pour l'edition d'une category de tclass
 * 
 * @author ndmz2720
 *
 */
public class EditCategoryView extends AbstractEditView implements IEditCategoryView {

	private final static Logger log = Logger.getLogger("EditCategoryView");

	private final static String TITLE = "category";

	private final LabelAndBoxWidget tbName = new LabelAndBoxWidget("name:", 80, 300);
	private final LabelAndBoxWidget tbListClasses = new LabelAndBoxWidget("classe names:", 80, 300);

	private VoCategoryForEdit category;

	// ---------------------------- constructor
	public EditCategoryView() {
		super(TITLE);
		super.init(TITLE);
	}

	// ---------------------------- implementing IEditItemView
	@Override
	public TabViewEnum getType() {
		return TabViewEnum.tabCategory;
	}

	// --------------------------- overriding IEditUserView
	@Override
	public VoCategoryForEdit getVoDatas() {
		this.category.setName(this.tbName.getBoxUserInput());
		this.category.setListClassNames(this.tbListClasses.getBoxUserInput());
		return this.category;
	}

	@Override
	public void setDatas(VoCategoryForEdit voCategory) {

		super.changeTitle(TITLE, voCategory.isIdUndefined());

		this.category = voCategory;
		this.tbName.setValue(voCategory.getName());
		this.tbListClasses.setValue(voCategory.getListClassNames());
	}

	// --------------------------- overriding AbstractView
	@Override
	public void reinit() {
		this.tbName.setValue(null);
		this.tbListClasses.setValue(null);
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected void buildBodyPanel() {

		this.main.add(this.tbName);
		this.main.add(this.tbListClasses);

	}

}
