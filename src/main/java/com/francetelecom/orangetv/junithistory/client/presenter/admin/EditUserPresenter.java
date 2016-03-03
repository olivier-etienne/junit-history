package com.francetelecom.orangetv.junithistory.client.presenter.admin;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.view.IView;
import com.francetelecom.orangetv.junithistory.client.view.IView.LogStatus;
import com.francetelecom.orangetv.junithistory.client.view.admin.IEditItemView;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;
import com.francetelecom.orangetv.junithistory.shared.vo.VoDatasValidation;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUserForEdit;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter pour la vue d'edition d'un utilisateur
 * 
 * @author sylvie
 * 
 */
public class EditUserPresenter extends AbstractEditItemPresenter implements IEditItemPresenter {

	private final static Logger log = Logger.getLogger("EditUserPresenter");

	private final IEditUserView view;

	// -------------------------------- constructor
	public EditUserPresenter(IGwtJUnitHistoryServiceAsync service, EventBus eventBus, IEditUserView view) {
		super(service, eventBus, "user");
		this.view = view;
		this.bind();
	}

	// --------------------------- overriding AbstractEditItemPresenter
	@Override
	protected String[] getUpdateDescription() {

		VoUserForEdit datas = view.getVoDatas();

		String[] description = new String[3];
		description[0] = "NAME : "
				+ ((ValueHelper.isStringEmptyOrNull(datas.getName())) ? "undefined" : datas.getName());
		description[1] = "DESCRIPTION    : "
				+ ((ValueHelper.isStringEmptyOrNull(datas.getDescription())) ? "undefined" : datas.getDescription());
		return description;
	}

	@Override
	protected void doUpdateItem(final IValidationCallback validationCallback) {
		final String message = " when updating user!";
		this.rpcService.createOrUpdateTestUser(view.getVoDatas(), new MyAsyncCallback<VoDatasValidation>(message) {

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

		final String message = " when validating user!";
		this.rpcService.validTestUser(view.getVoDatas(), new MyAsyncCallback<VoDatasValidation>(message) {

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

	// ---------------------- overriding AbstractPresenter
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
			Integer userId = (Integer) this.params.get(PARAMS_ITEM_ID);
			this.doGetUserInfo(userId);
		} else {
			this.view.setDatas(new VoUserForEdit());
		}
	}

	// -------------------------------- private methods

	private void doGetUserInfo(int userId) {

		final String message = " when loading user!";
		this.rpcService.getUserForEdit(userId, new MyAsyncCallback<VoUserForEdit>(message) {

			@Override
			public void onSuccess(VoUserForEdit result) {
				view.setActionResult("Success " + message, LogStatus.success);
				view.setDatas(result);

			}
		});
	}

	// ------------------------------------ private methods

	// ====================== VIEW ============
	public static interface IEditUserView extends IEditItemView {

		public void setDatas(VoUserForEdit voUser);

		public VoUserForEdit getVoDatas();
	}

}
