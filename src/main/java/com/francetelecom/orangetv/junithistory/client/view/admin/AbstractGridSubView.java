package com.francetelecom.orangetv.junithistory.client.view.admin;

import java.util.List;

import org.gwt.advanced.client.ui.widget.SimpleGrid;

import com.francetelecom.orangetv.junithistory.client.presenter.admin.IGridSubPresenter.GridActionButtonEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.IGridSubPresenter.ViewActionEnum;
import com.francetelecom.orangetv.junithistory.client.view.AbstractView;
import com.francetelecom.orangetv.junithistory.shared.vo.AbstractVoIdName;
import com.francetelecom.orangetv.junithistory.shared.vo.VoItemProtection;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * classe mère pour les page d'administration user, group et categorie
 * 
 * @author sylvie
 * 
 */
public abstract class AbstractGridSubView<T extends AbstractVoIdName> extends AbstractView implements IAdminSubView<T> {

	protected ClickHandler gridActionClickHandler;
	protected boolean locked;

	protected final MySimpleGrid grid;
	private ButtonViewAction btActionCreateItem;
	private final ButtonViewAction btActionRefreshList = new ButtonViewAction("Refresh",
			ViewActionEnum.refreshList.name(), "refresh list");
	private final String itemName;

	// ----------------------------------- constructor
	public AbstractGridSubView(String itemName) {
		super();
		this.itemName = itemName;
		this.grid = this.createGrid();
	}

	// ---------------------------------- abstract methods

	protected abstract boolean isColCentred(int col);

	protected abstract HeaderLabel[] getHeaderList();

	protected abstract MySimpleGrid createGrid();

	// ---------------------------------- overriding AbstractView
	@Override
	public void reinit() {
		this.grid.clearDatas();
	}

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
	protected void buildBodyPanel() {
		this.main.add(this.grid);
	}

	@Override
	protected void buildButtonPanel() {
		super.addButton(this.getCreateButton());
		super.addButton(this.btActionRefreshList);
	}

	@Override
	protected void initHandlers() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void initComposants() {
		// TODO Auto-generated method stub

	}

	// ---------------------------------- implementing IAdminItemSubView
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
	public void setDatas(List<T> listItems) {

		this.grid.clearDatas();

		if (listItems == null || listItems.isEmpty()) {
			return;
		}

		// for each item
		int row = 0;
		for (T item : listItems) {
			getLog().config("[" + row + "] user: " + item.getName());
			this.grid.addRow(row++, item);
		}

	}

	// ---------------------------- private methods

	private void enableButtonAndField(boolean enabled) {
		this.getCreateButton().setEnabled(enabled);
		this.btActionRefreshList.setEnabled(enabled);
	}

	protected ButtonViewAction getCreateButton() {
		if (this.btActionCreateItem == null) {
			this.btActionCreateItem = new ButtonViewAction("Create " + this.itemName, ViewActionEnum.createItem.name(),
					"Create a new " + this.itemName);
		}
		return this.btActionCreateItem;
	}

	// ================================ INNER CLASS
	protected abstract class MySimpleGrid extends SimpleGrid {

		private final String itemName;

		private boolean pairImpair = true;
		private String STYLE[] = new String[] { GRID_ROW_PAIR, GRID_ROW_IMPAIR };

		private int currentCol = 0;

		protected abstract void addOtherColumns(int row, T item);

		protected MySimpleGrid(String itemName) {

			this.itemName = itemName;
			this.setStyleName(GRID_SUITE);
			this.initHeaders();
			// enable verticall scrolling
			this.enableVerticalScrolling(true);
			this.setCellPadding(2);
		}

		@Override
		public void setWidget(int row, int column, Widget widget) {
			super.setWidget(row, column, widget);
		}

		private void addRow(int row, T item) {

			this.currentCol = 0;
			int itemId = item.getId();
			VoItemProtection protection = item.getProtection();

			this.setWidget(row, this.currentCol, new ValueLabel(itemId, isColCentred(this.currentCol)));

			this.getNextCol();
			this.setWidget(row, this.currentCol, new ValueLabel(item.getName(), isColCentred(this.currentCol)));

			this.getNextCol();
			boolean canEdit = protection.canEdit();
			GridActionButton editButton = canEdit ? new GridEditActionButton(itemId, this.itemName)
					: new EmptyActionButton(itemId);
			if (canEdit) {
				editButton.addClickHandler(AbstractGridSubView.this.gridActionClickHandler);
			}
			this.setWidget(row, this.currentCol, editButton);

			// colonnes intermediaires...
			this.addOtherColumns(row, item);

			this.getNextCol();
			boolean canDelete = protection.canDelete();
			final GridActionButton deleteButton = (canDelete) ? new GridDeleteActionButton(itemId, this.itemName)
					: new EmptyActionButton(itemId);
			if (canDelete) {
				deleteButton.addClickHandler(AbstractGridSubView.this.gridActionClickHandler);
			}
			this.setWidget(row, this.currentCol, deleteButton);

			Element tr = getBodyTable().getCellFormatter().getElement(row, 0).getParentElement();
			tr.addClassName(STYLE[pairImpair ? 1 : 0]);

			pairImpair = !pairImpair;
		}

		protected int getNextCol() {
			this.currentCol += 1;
			return this.currentCol;
		}

		private void initHeaders() {

			int col = 0;
			for (HeaderLabel header : AbstractGridSubView.this.getHeaderList()) {
				this.setHeaderWidget(col, header);
				col++;
			}
		}

		private void clearDatas() {
			super.removeAllRows();
		}
	}

	// ================================= INNER CLASS
	public static class ImgOn extends SimplePanel {

		public ImgOn() {
			this.addStyleName(STYLE_IMG_ACTION + " " + STYLE_IMG_ENABLED);
		}
	}

	public static abstract class GridActionButton extends Button {

		private final int itemId;
		private final GridActionButtonEnum action;

		public GridActionButtonEnum getAction() {
			return this.action;
		}

		public int getItemId() {
			return this.itemId;
		}

		private GridActionButton(int itemiId, GridActionButtonEnum action, String title) {
			this.itemId = itemiId;
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

	static class GridDeleteActionButton extends GridActionButton {

		GridDeleteActionButton(int itemId, String itemName) {
			super(itemId, GridActionButtonEnum.delete, "Delete the current " + itemName);
			this.addButtonStyleName(STYLE_IMG_DELETE);
		}
	}

	static class GridEditActionButton extends GridActionButton {

		GridEditActionButton(int itemId, String itemName) {
			super(itemId, GridActionButtonEnum.edit, "Edit the current " + itemName);
			this.addButtonStyleName(STYLE_IMG_EDIT);
		}
	}

	static class EmptyActionButton extends GridActionButton {
		EmptyActionButton(int itemId) {
			super(itemId, null, null);
			this.addButtonStyleName(STYLE_IMG_EMPTY);
		}
	}

}
