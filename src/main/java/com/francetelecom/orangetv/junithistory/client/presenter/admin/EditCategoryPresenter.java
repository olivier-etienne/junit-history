package com.francetelecom.orangetv.junithistory.client.presenter.admin;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.view.IView;
import com.francetelecom.orangetv.junithistory.client.view.IView.LogStatus;
import com.francetelecom.orangetv.junithistory.client.view.admin.IEditItemView;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;
import com.francetelecom.orangetv.junithistory.shared.vo.VoCategoryForEdit;
import com.francetelecom.orangetv.junithistory.shared.vo.VoDatasValidation;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter pour l'edition d'une category de tclass
 * 
 * @author ndmz2720
 *
 */
public class EditCategoryPresenter extends AbstractEditItemPresenter implements IEditItemPresenter {

	private final static Logger log = Logger.getLogger("EditCategoryPresenter");

	private final IEditCategoryView view;

	// -------------------------------------- constructor
	public EditCategoryPresenter(IGwtJUnitHistoryServiceAsync service, EventBus eventBus, IEditCategoryView view) {
		super(service, eventBus, "category");
		this.view = view;
		this.bind();
	}

	// ------------------------------------- overriding IEditItemPresenter
	@Override
	protected String[] getUpdateDescription() {
		VoCategoryForEdit datas = view.getVoDatas();

		String[] description = new String[3];
		description[0] = "NAME : "
				+ ((ValueHelper.isStringEmptyOrNull(datas.getName())) ? "undefined" : datas.getName());
		description[1] = "LIST    : "
				+ ((ValueHelper.isStringEmptyOrNull(datas.getListClassNames())) ? "undefined" : datas
						.getListClassNames());
		return description;

	}

	@Override
	protected void doUpdateItem(final IValidationCallback validationCallback) {

		final String message = " when updating category!";
		this.rpcService.createOrUpdateTestCategory(view.getVoDatas(), new MyAsyncCallback<VoDatasValidation>(message) {

			@Override
			public void onSuccess(VoDatasValidation result) {
				if (result != null && result.isValid()) {
					validationCallback.onSuccess();
				} else {
					validationCallback.onError(result != null ? result.getErrorMessages() : null);
				}
			}
		});
	}

	@Override
	protected void doValidItem(final IValidationCallback validationCallback) {

		final String message = " when validating category!";
		this.rpcService.validTestCategory(view.getVoDatas(), new MyAsyncCallback<VoDatasValidation>(message) {

			@Override
			public void onSuccess(VoDatasValidation result) {

				if (result != null && result.isValid()) {
					validationCallback.onSuccess();
				} else {
					validationCallback.onError(result != null ? result.getErrorMessages() : null);
				}
			}
		});

	}

	// ------------------------------------- overriding AbstractPresenter

	@Override
	public IView getView() {
		return this.view;
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected Widget getViewAsWidget() {
		return this.view.asWidget();
	}

	@Override
	protected void loadDatas(boolean forceRefresh) {

		if (this.containsItemIdInParams()) {
			Integer categoryId = (Integer) this.params.get(PARAMS_ITEM_ID);
			this.doGetCategoryInfo(categoryId);
		} else {
			this.view.setDatas(new VoCategoryForEdit());
		}
	}

	// -------------------------------------------- private methods
	private void doGetCategoryInfo(Integer categoryId) {

		final String message = " when loading category!";
		this.rpcService.getCategoryForEdit(categoryId, new MyAsyncCallback<VoCategoryForEdit>(message) {

			@Override
			public void onSuccess(VoCategoryForEdit result) {
				view.setActionResult("Success " + message, LogStatus.success);
				view.setDatas(result);

			}
		});

	}

	// ====================== VIEW ============
	public static interface IEditCategoryView extends IEditItemView {

		public void setDatas(VoCategoryForEdit voUser);

		public VoCategoryForEdit getVoDatas();
	}

}
