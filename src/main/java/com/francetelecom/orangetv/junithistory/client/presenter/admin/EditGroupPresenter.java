package com.francetelecom.orangetv.junithistory.client.presenter.admin;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.view.IView;
import com.francetelecom.orangetv.junithistory.client.view.IView.LogStatus;
import com.francetelecom.orangetv.junithistory.client.view.admin.IEditItemView;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;
import com.francetelecom.orangetv.junithistory.shared.vo.VoDatasValidation;
import com.francetelecom.orangetv.junithistory.shared.vo.VoGroupForEdit;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenternpour l'edition d'un group
 */
public class EditGroupPresenter extends AbstractEditItemPresenter implements IEditItemPresenter {

	private final static Logger log = Logger.getLogger("EditGroupPresenter");

	private final IEditGroupView view;

	// -------------------------------------- constructor
	public EditGroupPresenter(IGwtJUnitHistoryServiceAsync service, EventBus eventBus, IEditGroupView view) {
		super(service, eventBus, "category");
		this.view = view;
		this.bind();
	}

	// ------------------------------------- overriding IEditItemPresenter
	@Override
	protected String[] getUpdateDescription() {
		VoGroupForEdit datas = view.getVoDatas();

		String[] description = new String[3];
		description[0] = "NAME : " + (ValueHelper.isStringEmptyOrNull(datas.getName()) ? "undefined" : datas.getName());
		description[1] = "STB    : " + (ValueHelper.isStringEmptyOrNull(datas.getStb()) ? "undefined" : datas.getStb());
		description[2] = "PREFIX    : "
				+ (ValueHelper.isStringEmptyOrNull(datas.getPrefix()) ? "undefined" : datas.getPrefix());
		return description;

	}

	@Override
	protected void doUpdateItem(final IValidationCallback validationCallback) {

		final String message = " when updating group!";
		this.rpcService.createOrUpdateTestGroup(view.getVoDatas(), new MyAsyncCallback<VoDatasValidation>(message) {

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

		final String message = " when validating group!";
		this.rpcService.validTestGroup(view.getVoDatas(), new MyAsyncCallback<VoDatasValidation>(message) {

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
			Integer groupId = (Integer) this.params.get(PARAMS_ITEM_ID);
			this.doGetGroupInfo(groupId);
		} else {
			this.view.setDatas(new VoGroupForEdit());
		}
	}

	// -------------------------------------------- private methods
	private void doGetGroupInfo(Integer groupId) {

		final String message = " when loading group!";
		this.rpcService.getGroupForEdit(groupId, new MyAsyncCallback<VoGroupForEdit>(message) {

			@Override
			public void onSuccess(VoGroupForEdit result) {
				view.setActionResult("Success " + message, LogStatus.success);
				view.setDatas(result);

			}
		});

	}

	// ====================== VIEW ============
	public static interface IEditGroupView extends IEditItemView {

		public void setDatas(VoGroupForEdit voUser);

		public VoGroupForEdit getVoDatas();
	}

}
