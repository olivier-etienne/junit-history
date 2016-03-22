package com.francetelecom.orangetv.junithistory.client.view.admin;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.presenter.PageAdminPresenter.TabAdminViewEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.TesterSubPresenter.IUserSubView;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUserForGrid;

/*
 * Vue simple sans panel de connection
 */
public class TesterSubView extends AbstractGridSubView<VoUserForGrid> implements IUserSubView {

	private final static Logger log = Logger.getLogger("TesterSubView");

	private static final HeaderLabel[] HEADERS = new HeaderLabel[] { // ...

	new HeaderLabel("id"), // ...
			new HeaderLabel("name"), // ...
			new HeaderLabel("edit", true), // ...
			new HeaderLabel("description", true), // ...
			new HeaderLabel("admin", true), // ...
			new HeaderLabel("del", true) // ...
	};

	// ------------------------- constructor
	public TesterSubView() {
		super("tester");
		super.init("Testers");
	}

	// ---------------------------- overriding AbstractView

	@Override
	protected Logger getLog() {
		return log;
	}

	// ---------------------------- implementing IAdminSubView
	@Override
	protected MySimpleGrid createGrid() {

		MySimpleGrid grid = new MySimpleGrid("tester") {

			@Override
			protected void addOtherColumns(int row, VoUserForGrid item) {

				int col = this.getNextCol();
				this.setWidget(row, col, new ValueLabel(item.getDescription(), isColCentred(col)));

				col = this.getNextCol();
				this.setWidget(row, col, item.isAdmin() ? new ImgOn() : new ValueLabel("", isColCentred(col)));

			}

		};

		return grid;
	}

	@Override
	public TabAdminViewEnum getType() {
		return TabAdminViewEnum.tabTester;
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
