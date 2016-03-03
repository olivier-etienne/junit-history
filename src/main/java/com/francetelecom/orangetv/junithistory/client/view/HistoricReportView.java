package com.francetelecom.orangetv.junithistory.client.view;

import java.util.logging.Logger;

import org.gwt.advanced.client.ui.widget.SimpleGrid;

import com.francetelecom.orangetv.junithistory.client.AppController.MainPanelViewEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.HistoricReportPresenter.GridActionButtonEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.HistoricReportPresenter.IHistoricReportView;
import com.francetelecom.orangetv.junithistory.client.presenter.HistoricReportPresenter.ViewActionEnum;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndListWidget;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoInitHistoricReportDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoItemProtection;
import com.francetelecom.orangetv.junithistory.shared.vo.VoListSuiteForGrid;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestSuiteForGrid;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.ListBox;

public class HistoricReportView extends AbstractMainView implements IHistoricReportView {

	private final static Logger log = Logger.getLogger("HistoricReportView");

	private static final HeaderLabel[] HEADERS = new HeaderLabel[] { // ...

	new HeaderLabel("id"), // ...
			new HeaderLabel("name"), // ...
			new HeaderLabel("edit", true), // ...
			new HeaderLabel("date", true), // ...
			new HeaderLabel("firmware"), // ...
			new HeaderLabel("iptvkit"), // ...
			new HeaderLabel("user", true), // ...
			new HeaderLabel("show", true), // ...
			new HeaderLabel("del", true), // ...
			new HeaderLabel("url", true) // ...
	};

	private ListBox lbGroups = new ListBox();
	private final LabelAndListWidget wlistGroups = new LabelAndListWidget("STB", 50, 212, lbGroups, 1);

	private final ButtonViewAction btActionShowGroupReport = new ButtonViewAction("Show STB reports",
			ViewActionEnum.showGroupReport.name(), "Show Html site for the current STB");
	private final ButtonViewAction btActionShowAllReport = new ButtonViewAction("Show all reports",
			ViewActionEnum.showAllReport.name(), "show all reports");
	private final ButtonViewAction btActionBuildUrl = new ButtonViewAction("Get public url",
			ViewActionEnum.buildUrl.name(), "show public url in order to access directly to html site");

	private final MySimpleGrid grid = new MySimpleGrid();

	private ClickHandler gridActionClickHandler;
	private VoItemProtection protection;

	private boolean locked;

	@Override
	public MainPanelViewEnum getViewType() {
		return MainPanelViewEnum.historicReport;
	}

	// ------------------------ implementing IHistoricReportView

	@Override
	public void setInitDatas(VoInitHistoricReportDatas initDatas) {

		if (initDatas == null) {
			return;
		}
		super.populateList(this.lbGroups, initDatas.getListGroups());

	}

	@Override
	public int getCurrentGroupId() {

		return ValueHelper.getIntValue(this.wlistGroups.getListUserInput(), IVo.ID_UNDEFINED);

	}

	@Override
	public void setCurrentGroup(int groupId) {

		this.wlistGroups.setValue(groupId + "");
	}

	@Override
	public void setGridActionClickHandler(final ClickHandler actionClickHandler) {
		this.gridActionClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				if (!locked) {
					actionClickHandler.onClick(event);
				}
			}
		};

	}

	@Override
	public HasClickHandlers getGroupHasClickHandler() {
		return this.lbGroups;
	}

	@Override
	public void setDatas(VoListSuiteForGrid voTestSuites) {
		this.grid.clearDatas();

		if (voTestSuites == null || voTestSuites.getListTestSuites() == null) {
			return;
		}

		// protection
		this.protection = voTestSuites.getProtection();
		if (protection != null && !this.protection.canDelete()) {
			log.warning("all suites protected!");
		}

		// for each suite
		int row = 0;
		for (VoTestSuiteForGrid suite : voTestSuites.getListTestSuites()) {
			log.config("[" + row + "] suite: " + suite.getName());
			this.grid.addRow(row++, suite);
		}
	}

	// -------------------------- constructor
	public HistoricReportView() {
		super();
		super.init("Historic Gwt JUnit reports");
	}

	// ---------------------------- implementing IView
	@Override
	public void lock() {
		locked = true;
		this.enableButtonAndField(false);
	}

	@Override
	public void unlock() {
		locked = false;
		this.enableButtonAndField(true);
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	// -------------------------- overriding AbstractView
	@Override
	protected void buildBodyPanel() {

		this.main.add(this.wlistGroups);
		this.main.add(this.grid);
	}

	@Override
	protected void initHandlers() {

	}

	@Override
	protected void initComposants() {

		// tempo
		this.btActionShowAllReport.setVisible(false);
	}

	@Override
	protected void buildButtonPanel() {
		super.addButton(this.btActionShowGroupReport);
		super.addButton(this.btActionShowAllReport);
		super.addButton(this.btActionBuildUrl);
	}

	@Override
	public void reinit() {
		this.grid.clearDatas();
	}

	// --------------------------------- private methods
	private void enableButtonAndField(boolean enabled) {

		this.btActionShowAllReport.enableButtonIfActif(enabled);
		this.btActionShowGroupReport.enableButtonIfActif(enabled);
		this.btActionBuildUrl.enableButtonIfActif(enabled);

		this.wlistGroups.setEnabled(enabled);
	}

	private boolean canDeleteTestSuite() {
		return this.protection == null ? false : this.protection.canDelete();
	}

	// ================================== INNER CLASS
	private static class EmptyActionButton extends GridActionButton {
		private EmptyActionButton(int suiteId) {
			super(suiteId, GridActionButtonEnum.delete, null);
			this.addButtonStyleName(STYLE_IMG_EMPTY);
		}
	}

	private static class GridDeleteActionButton extends GridActionButton {

		private GridDeleteActionButton(int suiteId) {
			super(suiteId, GridActionButtonEnum.delete, "Delete the current report");
			this.addButtonStyleName(STYLE_IMG_DELETE);
		}
	}

	private static class GridUrlActionButton extends GridActionButton {

		private GridUrlActionButton(int suiteId) {
			super(suiteId, GridActionButtonEnum.url, "Show the public url for the current report");
			this.addButtonStyleName(STYLE_IMG_LINK);
		}

	}

	private static class GridEditActionButton extends GridActionButton {

		private GridEditActionButton(int suiteId) {
			super(suiteId, GridActionButtonEnum.edit, "Edit the current report");
			this.addButtonStyleName(STYLE_IMG_EDIT);
		}
	}

	private static class GridShowActionButton extends GridActionButton {

		private GridShowActionButton(int suiteId) {
			super(suiteId, GridActionButtonEnum.show, "Show html site for the current report");
			this.addButtonStyleName(STYLE_IMG_SHOW);
		}
	}

	public static abstract class GridActionButton extends Button {

		private final int suiteId;
		private final GridActionButtonEnum action;

		public GridActionButtonEnum getAction() {
			return this.action;
		}

		public int getSuiteId() {
			return this.suiteId;
		}

		private GridActionButton(int suiteId, GridActionButtonEnum action, String title) {
			this.suiteId = suiteId;
			this.action = action;
			if (title != null) {
				super.setTitle(title);
			}
		}

		protected void addButtonStyleName(String stylename) {
			this.addStyleName(STYLE_IMG_ACTION + " " + stylename);
		}

		protected void removeButtonStyleName(String stylename) {
			this.removeStyleName(STYLE_IMG_ACTION + " " + stylename);
		}

	}

	private class MySimpleGrid extends SimpleGrid {

		private boolean pairImpair = true;
		private String STYLE[] = new String[] { GRID_ROW_PAIR, GRID_ROW_IMPAIR };

		private MySimpleGrid() {

			this.setStyleName(GRID_SUITE);
			this.initHeaders();
			// enable verticall scrolling
			this.enableVerticalScrolling(true);
			this.setCellPadding(2);
		}

		private void addRow(int row, VoTestSuiteForGrid suite) {

			int suiteId = suite.getId();
			int col = 0;
			this.setWidget(row, col, new ValueLabel(suite.getId(), isColCentred(col++)));
			this.setWidget(row, col, new ValueLabel(suite.getName(), isColCentred(col++)));
			GridActionButton editButton = new GridEditActionButton(suiteId);
			editButton.addClickHandler(HistoricReportView.this.gridActionClickHandler);
			this.setWidget(row, col++, editButton);

			String date = suite.getDate();
			this.setWidget(row, col, new ValueLabel((date == null) ? "" : suite.getDate(), isColCentred(col++)));
			this.setWidget(row, col, new ValueLabel(suite.getFirmware(), isColCentred(col++)));
			this.setWidget(row, col, new ValueLabel(suite.getIptvkit(), isColCentred(col++)));
			this.setWidget(row, col, new ValueLabel(suite.getUser(), isColCentred(col++)));

			final GridShowActionButton showButton = new GridShowActionButton(suiteId);
			showButton.addClickHandler(HistoricReportView.this.gridActionClickHandler);
			this.setWidget(row, col++, showButton);

			boolean canDelete = canDeleteTestSuite() && !suite.isReadonly();
			final GridActionButton deleteButton = (canDelete) ? new GridDeleteActionButton(suiteId)
					: new EmptyActionButton(suiteId);
			if (canDelete) {
				deleteButton.addClickHandler(HistoricReportView.this.gridActionClickHandler);
			}
			this.setWidget(row, col++, deleteButton);

			final GridUrlActionButton urlButton = new GridUrlActionButton(suiteId);
			urlButton.addClickHandler(HistoricReportView.this.gridActionClickHandler);
			this.setWidget(row, col++, urlButton);

			Element tr = getBodyTable().getCellFormatter().getElement(row, 0).getParentElement();
			tr.addClassName(STYLE[pairImpair ? 1 : 0]);

			pairImpair = !pairImpair;
		}

		private void initHeaders() {

			int col = 0;
			for (HeaderLabel header : HEADERS) {
				this.setHeaderWidget(col, header);
				col++;
			}
		}

		private void clearDatas() {
			super.removeAllRows();
		}
	}

	private boolean isColCentred(int col) {
		return HEADERS[col].isCentred();
	}

}
