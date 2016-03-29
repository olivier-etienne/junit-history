package com.francetelecom.orangetv.junithistory.client.presenter;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.view.IMainView;
import com.francetelecom.orangetv.junithistory.client.view.IView.LogStatus;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoDatasValidation;
import com.francetelecom.orangetv.junithistory.shared.vo.VoEditReportDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdUtils;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestSuiteForEdit;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUser;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class EditReportPresenter extends AbstractEditItemPresenter implements IMainPresenter {

	private final static Logger log = Logger.getLogger("EditReportPresenter");

	public enum ViewActionEnum {
		update, cancel;
	}

	private final IEditReportView view;
	private ClickHandler closeDialogClickHandler;
	private int suiteId;

	private Map<Integer, VoUser> mapId2Users = new HashMap<>();

	// ----------------------------------- overriding IPresenter

	@Override
	public IMainView getView() {
		return this.view;
	}

	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected Widget getViewAsWidget() {
		return this.view == null ? null : this.view.asWidget();
	}

	// ------------------------------- constructor
	public EditReportPresenter(IGwtJUnitHistoryServiceAsync service, EventBus eventBus, IEditReportView view) {
		super(service, eventBus, "Report ");
		this.view = view;
		this.bind();
	}

	// ----------------------------------- implementing IMainPresenter
	@Override
	public IMainView getMainView() {
		return this.view;
	}

	// ---------------------------------- overriding AbstractEditItemPresenter
	@Override
	protected String[] getUpdateDescription() {

		VoTestSuiteForEdit datas = view.getEditReportData();

		final VoUser user = this.mapId2Users.get(datas.getUserId());

		String[] description = new String[3];
		description[0] = "IPTVKIT : " + ((datas.getIptvkit() == null) ? "undefined" : datas.getIptvkit());
		description[1] = "USER    : " + ((user == null) ? "undefined" : user.getName());
		description[2] = "DATE    : " + ((datas.getDate() == null) ? "undefined" : DF.format(datas.getDate()));
		return description;
	}

	@Override
	protected void doUpdateItem(final IValidationCallback validationCallback) {
		log.config("doUpdateItem()");
		this.view.waiting(true);

		VoTestSuiteForEdit suiteToUpdate = view.getEditReportData();
		final String message = " when updating single report information (" + suiteToUpdate.getName() + ")!";
		this.rpcService.updateTestSuiteInfo(suiteToUpdate, new MyAsyncCallback<VoDatasValidation>("Error " + message) {

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

		VoTestSuiteForEdit suiteToUpdate = view.getEditReportData();
		final String message = " when validating single report information(" + suiteToUpdate.getName() + ")!";
		this.rpcService.validTestSuiteInfo(suiteToUpdate, new MyAsyncCallback<VoDatasValidation>("Error " + message) {

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
	protected void closeDialog(boolean updateDone) {
		this.closeDialogClickHandler.onClick(new UpdateClickEvent(updateDone));
	}

	// @Override
	// protected void refreshList() {
	// // TODO Auto-generated method stub
	//
	// }

	// ----------------------------------------- overriding AbstractPresenter

	@Override
	protected void loadDatas(boolean forceRefresh) {
		this.doGetReportDatas();
	}

	// ---------------------------------------- public methods
	public void setCloseDialogClickHandler(ClickHandler closeDialogClickHandler) {

		this.closeDialogClickHandler = closeDialogClickHandler;
	}

	// --------------------------------------- private methods

	private void doGetReportDatas() {
		log.config("doGetReportDatas()");

		Integer currentSuiteId = this.params.containsKey(PARAMS_SUITE_ID) ? (Integer) this.params.get(PARAMS_SUITE_ID)
				: IVo.ID_UNDEFINED;
		suiteId = currentSuiteId.intValue();

		final String message = " when loading report!";
		this.rpcService.getEditTestSuiteDatas(suiteId, new MyAsyncCallback<VoEditReportDatas>("Error " + message) {

			@Override
			public void onSuccess(VoEditReportDatas datas) {
				mapId2Users = VoIdUtils.getMapId2Item(datas.getListUsers());
				view.setActionResult("Success " + message, LogStatus.success);
				view.setDatas(datas);

				if (datas.getSuiteForEdit().isReadOnly()) {
					view.activeButtons(ViewActionEnum.cancel.name());
				} else {
					view.activeButtons(ViewActionEnum.cancel.name(), ViewActionEnum.update.name());
				}

			}
		});
	}

	// -------------------------------- VIEW
	public interface IEditReportView extends IMainView {

		public void setDatas(VoEditReportDatas datas);

		public VoTestSuiteForEdit getEditReportData();

	}

}
