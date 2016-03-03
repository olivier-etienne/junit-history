package com.francetelecom.orangetv.junithistory.client.view.admin;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.presenter.PageAdminPresenter.TabViewEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.CategorySubPresenter.ICategorySubView;
import com.francetelecom.orangetv.junithistory.shared.util.ObjectUtils;
import com.francetelecom.orangetv.junithistory.shared.vo.VoCategoryForGrid;

public class CategorySubView extends AbstractGridSubView<VoCategoryForGrid> implements ICategorySubView {

	private final static Logger log = Logger.getLogger("CategorySubView");

	private static final HeaderLabel[] HEADERS = new HeaderLabel[] { // ...

	new HeaderLabel("id"), // ...
			new HeaderLabel("name"), // ...
			new HeaderLabel("edit", true), // ...
			new HeaderLabel("list", true), // ...
			new HeaderLabel("default", true), // ...
			new HeaderLabel("del", true) // ...
	};

	// ------------------------- constructor
	public CategorySubView() {
		super("category");
		super.init("Categories");
	}

	// ---------------------------- overriding AbstractView

	@Override
	protected Logger getLog() {
		return log;
	}

	// ---------------------------- implementing IAdminSubView
	@Override
	protected MySimpleGrid createGrid() {

		MySimpleGrid grid = new MySimpleGrid("category") {

			@Override
			protected void addOtherColumns(int row, VoCategoryForGrid item) {

				int col = this.getNextCol();
				String[] names = item.getSuiteNames();
				this.setWidget(row, col, new ValueLabel(ObjectUtils.tabToString(names, VoCategoryForGrid.SEPARATOR),
						isColCentred(col)));

				col = this.getNextCol();
				this.setWidget(row, col, item.isDefaultValue() ? new ImgOn() : new ValueLabel("", isColCentred(col)));

			}

		};

		return grid;
	}

	@Override
	public TabViewEnum getType() {
		return TabViewEnum.tabCategory;
	}

	@Override
	protected boolean isColCentred(int col) {
		return HEADERS[col].isCentred();
	}

	@Override
	protected HeaderLabel[] getHeaderList() {
		return HEADERS;
	}

}
