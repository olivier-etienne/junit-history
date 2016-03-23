package com.francetelecom.orangetv.junithistory.client.presenter;

import java.util.List;

import com.francetelecom.orangetv.junithistory.client.service.IActionCallback;
import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.util.WidgetUtils;
import com.francetelecom.orangetv.junithistory.client.view.AbstractView.ButtonViewAction;
import com.francetelecom.orangetv.junithistory.client.view.IView.LogStatus;
import com.francetelecom.orangetv.junithistory.shared.util.ObjectUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.web.bindery.event.shared.EventBus;

public abstract class AbstractEditItemPresenter extends AbstractPresenter implements IEditItemPresenter {

	private final String itemName;

	// ------------------------------ abstract methods
	protected abstract String[] getUpdateDescription();

	protected abstract void doUpdateItem(IValidationCallback callback);

	protected abstract void doValidItem(IValidationCallback callback);

	protected abstract void closeDialog();

	protected abstract void refreshList();

	// ------------------------------------- constructor
	protected AbstractEditItemPresenter(IGwtJUnitHistoryServiceAsync service, EventBus eventBus, String itemName) {
		super(service, eventBus);
		this.itemName = itemName;
	}

	// ------------------------- protected methods

	protected boolean containsItemIdInParams() {

		return this.params != null && this.params.containsKey(PARAMS_ITEM_ID);
	}

	protected void bind() {

		final ClickHandler actionClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {

				Object source = event.getSource();
				if (source != null && source instanceof ButtonViewAction) {
					getView().setActionResult(null, null);
					ButtonViewAction button = (ButtonViewAction) source;
					getView().selectButton(button);

					ViewActionEnum action = ViewActionEnum.valueOf(button.getViewAction());

					switch (action) {
					case update:

						// on commence par pre-valider les donnees saisies
						doValidItem(new IValidationCallback() {

							@Override
							public void onSuccess() {
								// ensuite on demande à l'utilisateur de
								// confirmer la modification
								beforeUpdateItem();
							}

							@Override
							public void onError(List<String> errorMessages) {
								displayValidationErrors(errorMessages);
							}
						});
						break;

					case cancel:
						closeDialog();
						break;
					}

				}
			}

		};
		this.getView().setViewActionClickHandler(actionClickHandler);

	}

	// ------------------------------------ private methods
	/*
	 * Affichage d'une boite de dialogue avec la liste des erreurs de validation
	 * @param errorMessages
	 */
	private void displayValidationErrors(List<String> errorMessages) {

		if (errorMessages == null) {
			errorMessages = ObjectUtils.createList("Unexpected error!");
		}
		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Validation errors",
				ObjectUtils.listToTab(errorMessages), null, false, null);
		WidgetUtils.centerDialogAndShow(dialogBox);
	}

	private void beforeUpdateItem() {

		getLog().config("Confirme before update item.");

		String[] description = this.getUpdateDescription();
		int size = 1 + description.length;
		String[] arguments = new String[size];
		arguments[0] = "Confirmer la modification de " + this.itemName;
		for (int i = 0; i < description.length; i++) {
			String line = description[i];
			arguments[i + 1] = line;
		}

		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Update " + itemName + " ", arguments, null,
				true, new IActionCallback() {

					@Override
					public void onCancel() {
						getView().setActionResult("Update " + itemName + " canceled!", LogStatus.warning);
					}

					@Override
					public void onOk() {
						getView().setActionResult("Update " + itemName + " in progres...", LogStatus.warning);

						// on lance l'update des donnees puis on ferme la box
						doUpdateItem(new IValidationCallback() {

							@Override
							public void onSuccess() {
								getView().setActionResult("Update " + itemName + " in success...", LogStatus.success);
								closeDialog();
								refreshList(); // FIXME a enlever
							}

							@Override
							public void onError(List<String> errorMessages) {
								displayValidationErrors(errorMessages);
							}
						});
					}

				});
		WidgetUtils.centerDialogAndShow(dialogBox);
	}

	// =============================== INNER CLASS
	protected interface IValidationCallback {

		public void onSuccess();

		public void onError(List<String> errorMessages);
	}

	protected abstract class MyAsyncCallback<T> implements AsyncCallback<T> {

		private final String errorMessage;

		protected MyAsyncCallback(String errorMessage) {
			this.errorMessage = errorMessage;
		}

		@Override
		public void onFailure(Throwable caught) {

			if (this.errorMessage != null) {
				String errorMessage = this.errorMessage + "<br/>" + caught.getMessage();
				getView().setActionResult(errorMessage, LogStatus.error);
				getLog().severe(errorMessage);
				getView().waiting(false);
			}
		}

	}

}
