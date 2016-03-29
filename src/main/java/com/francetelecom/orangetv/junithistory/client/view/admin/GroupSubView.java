package com.francetelecom.orangetv.junithistory.client.view.admin;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.presenter.PageAdminPresenter.TabAdminViewEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.GroupSubPresenter.IGroupSubView;
import com.francetelecom.orangetv.junithistory.shared.vo.VoGroupForGrid;

public class GroupSubView extends AbstractGridSubView<VoGroupForGrid>
		implements IGroupSubView {

	private final static Logger log = Logger.getLogger("GroupSubView");

	private static final HeaderLabel[] HEADERS = new HeaderLabel[] { // ...

	new HeaderLabel("id"), // ...
			new HeaderLabel("name"), // ...
			new HeaderLabel("edit", true), // ...
			new HeaderLabel("stb"), // ...
			new HeaderLabel("prefix"), // ...
			new HeaderLabel("del", true) // ...
	};

	// ------------------------- constructor
	public GroupSubView() {
		super("STB group");
		super.init("STB groups");
	}

	// ---------------------------- overriding AbstractView

	@Override
	protected Logger getLog() {
		return log;
	}

	// ---------------------------- implementing IAdminSubView
	@Override
	protected MySimpleGrid createGrid() {

		MySimpleGrid grid = new MySimpleGrid("group") {

			@Override
			protected void addOtherColumns(int row, VoGroupForGrid item) {

				int col = this.getNextCol();
				this.setWidget(row, col, new ValueLabel(item.getStb(),
						isColCentred(col)));

				col = this.getNextCol();
				this.setWidget(row, col, new ValueLabel(item.getPrefix(),
						isColCentred(col)));

			}

		};

		return grid;
	}

	@Override
	public TabAdminViewEnum getType() {
		return TabAdminViewEnum.tabGroup;
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
