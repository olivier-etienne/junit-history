package com.francetelecom.orangetv.junithistory.client.presenter.admin;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.francetelecom.orangetv.junithistory.client.presenter.AbstractPresenter;
import com.francetelecom.orangetv.junithistory.client.presenter.ClientFactory;
import com.francetelecom.orangetv.junithistory.client.presenter.PageAdminPresenter.TabAdminViewEnum;
import com.francetelecom.orangetv.junithistory.client.service.IActionCallback;
import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.util.WidgetUtils;
import com.francetelecom.orangetv.junithistory.client.util.WidgetUtils.MyDialogView;
import com.francetelecom.orangetv.junithistory.client.view.AbstractView.ButtonViewAction;
import com.francetelecom.orangetv.junithistory.client.view.IView;
import com.francetelecom.orangetv.junithistory.client.view.IView.LogStatus;
import com.francetelecom.orangetv.junithistory.client.view.admin.AbstractGridSubView.GridActionButton;
import com.francetelecom.orangetv.junithistory.client.view.admin.IAdminSubView;
import com.francetelecom.orangetv.junithistory.client.view.admin.IEditItemView;
import com.francetelecom.orangetv.junithistory.shared.util.ObjectUtils;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdName;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Classe mere pour les presenter des pages d'administration
 * 
 * @author sylvie
 * 
 */
public abstract class AbstractGridSubPresenter<T extends VoIdName> extends AbstractPresenter implements
		IGridSubPresenter {

	private final IAdminSubView<T> view;
	private Map<Integer, T> mapId2Item = new HashMap<>(0);
	protected final ClientFactory clientFactory;
	protected MyDialogView dialogBox;

	// ------------------------------------- constructor
	protected AbstractGridSubPresenter(ClientFactory clientFactory, IGwtJUnitHistoryServiceAsync service,
			EventBus eventBus, IAdminSubView<T> view) {
		super(service, eventBus);
		this.clientFactory = clientFactory;
		this.view = view;
		this.bind();
	}

	// -------------------------------- abstract methods

	protected abstract void doDeleteItem(int itemId, IDeleteCallback callback);

	protected abstract TabAdminViewEnum getType();

	protected abstract String[] getItemDescription(T item);

	// -------------------------- implementing IGridSubPresenter
	@Override
	public void closeDialogBox(boolean updateDone) {
		if (this.dialogBox != null) {
			this.dialogBox.hide();
		}
	}

	@Override
	public void refresh() {
		this.loadDatas(true);
	}

	// -------------------------------- overriding AbstractPresenter
	@Override
	public IView getView() {
		return this.view;
	}

	@Override
	protected Widget getViewAsWidget() {
		return this.view.asWidget();
	}

	// -------------------------- protected methods
	protected void afterGetListOnSuccess(List<T> list, String message) {

		getLog().config("loadDatas() - onSuccess...");
		mapId2Item = VoIdUtils.getMapId2Item(list);
		view.setDatas(list);
		view.setActionResult("Success" + message, LogStatus.success);
		view.activeAllButtons();

	}

	protected MyListAsyncCallback buildGetListCallback(final String message) {

		return new MyListAsyncCallback(message) {

			@Override
			public void onSuccess(List<T> list) {
				afterGetListOnSuccess(list, message);
			}

		};

	}

	// ------------------------- private methods

	private void showDialogCreateItem() {

		IEditItemView view = this.clientFactory.getEditView(this.getType());

		IEditAdminItemPresenter presenter = this.clientFactory.getEditPresenter(this.getType());
		if (presenter == null) {
			presenter = this.clientFactory.buildEditPresenter(view);
			presenter.setGridSubPresenter(this);
		}
		this.dialogBox = WidgetUtils.buildDialogView("");

		presenter.go(this.dialogBox);
		WidgetUtils.centerDialogAndShow(this.dialogBox);

	}

	private void showDialogEditItem(int itemId) {

		IEditItemView view = this.clientFactory.getEditView(this.getType());

		IEditAdminItemPresenter presenter = this.clientFactory.getEditPresenter(this.getType());
		if (presenter == null) {
			presenter = this.clientFactory.buildEditPresenter(view);
			presenter.setGridSubPresenter(this);
		}
		this.dialogBox = WidgetUtils.buildDialogView("");

		presenter.go(this.dialogBox, ObjectUtils.buildMapWithOneItem(PARAMS_ITEM_ID, itemId));
		WidgetUtils.centerDialogAndShow(this.dialogBox);

	}

	private void beforeDeleteItem(final int itemId) {

		getLog().config("Confirme before delete item.");
		final String itemName = this.getType().getItemName();

		String[] description = this.getItemDescription(this.mapId2Item.get(itemId));
		int size = 1 + description.length;
		String[] arguments = new String[size];
		arguments[0] = "Confirmer la suppression de " + itemName;
		for (int i = 0; i < description.length; i++) {
			String line = description[i];
			arguments[i + 1] = line;
		}

		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Delete " + itemName + " ", arguments, null,
				true, new IActionCallback() {

					@Override
					public void onCancel() {
						getView().setActionResult("Delete " + itemName + " canceled!", LogStatus.warning);
					}

					@Override
					public void onOk() {
						getView().setActionResult("Delete " + itemName + " in progres...", LogStatus.warning);

						// on lance la suppression de l'item
						doDeleteItem(itemId, new IDeleteCallback() {

							@Override
							public void onSuccess(Boolean result) {
								if (result) {
									getView().setActionResult("Delete " + itemName + " in success...",
											LogStatus.success);
								} else {
									getView().setActionResult("Delete " + itemName + " in failure...",
											LogStatus.warning);
								}
								refresh();
							}

							@Override
							public void onError(String errorMessage) {
								getView().setActionResult(errorMessage, LogStatus.error);
							}
						});
					}

				});
		WidgetUtils.centerDialogAndShow(dialogBox);

	}

	private void bind() {

		// click handler for view button action
		final ClickHandler viewActionClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				Object source = event.getSource();
				if (source != null && source instanceof ButtonViewAction) {
					view.setActionResult(null, null);
					ButtonViewAction button = (ButtonViewAction) source;
					view.selectButton(button);

					ViewActionEnum action = ViewActionEnum.valueOf(button.getViewAction());
					if (action != null) {

						switch (action) {
						case createItem:
							showDialogCreateItem();
							break;
						case refreshList:
							loadDatas(true);
							break;
						}

					}
				}

			}

		};
		this.view.setViewActionClickHandler(viewActionClickHandler);

		// click handler for grid action ( edit, delete)
		final ClickHandler gridActionClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				Object source = (event == null) ? null : event.getSource();
				if (source != null && source instanceof GridActionButton) {

					GridActionButton button = (GridActionButton) source;
					GridActionButtonEnum action = button.getAction();
					int itemId = button.getItemId();

					switch (action) {

					case edit:
						showDialogEditItem(itemId);
						break;

					case delete:
						beforeDeleteItem(itemId);
						break;
					}

				}
			}
		};
		this.view.setGridActionClickHandler(gridActionClickHandler);
	}

	// =============================== INNER CLASS
	protected class MyDeleteAsyncCallback extends MyAsyncCallback<Boolean> {

		private final IDeleteCallback callback;

		protected MyDeleteAsyncCallback(String errorMessage, IDeleteCallback callback) {
			super(errorMessage);
			this.callback = callback;
		}

		@Override
		public void onSuccess(Boolean result) {

			if (callback != null) {
				callback.onSuccess(result);
			}

		}

		@Override
		public void onFailure(Throwable caught) {
			if (callback != null) {
				callback.onError(caught.getMessage());
			}
		}

	}

	protected abstract class MyListAsyncCallback extends MyAsyncCallback<List<T>> {
		protected MyListAsyncCallback(String errorMessage) {
			super(errorMessage);
		}

	}

	private abstract class MyAsyncCallback<T> implements AsyncCallback<T> {

		protected final String errorMessage;

		protected MyAsyncCallback(String errorMessage) {
			this.errorMessage = errorMessage;
		}

		@Override
		public void onFailure(Throwable caught) {

			if (this.errorMessage != null) {
				String errorMessage = this.errorMessage + "<br/>" + caught.getMessage();
				view.setActionResult(errorMessage, LogStatus.error);
				getLog().severe(errorMessage);
				view.waiting(false);
			}
		}

	}

	// =========================================== INNER CLASS
	protected interface IDeleteCallback {

		public void onSuccess(Boolean result);

		public void onError(String errorMessage);
	}
}
