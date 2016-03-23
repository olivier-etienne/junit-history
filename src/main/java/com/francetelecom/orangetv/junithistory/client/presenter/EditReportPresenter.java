package com.francetelecom.orangetv.junithistory.client.presenter;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.service.IActionCallback;
import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.util.WidgetUtils;
import com.francetelecom.orangetv.junithistory.client.view.AbstractView.ButtonViewAction;
import com.francetelecom.orangetv.junithistory.client.view.IMainView;
import com.francetelecom.orangetv.junithistory.client.view.IView.LogStatus;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoDatasValidation;
import com.francetelecom.orangetv.junithistory.shared.vo.VoEditReportDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdUtils;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestSuiteForEdit;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUser;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.rpc.AsyncCallback;
import com.google.gwt.user.client.ui.DialogBox;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

public class EditReportPresenter extends AbstractMainPresenter {

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
		super(service, eventBus);
		this.view = view;
		this.bind();
	}

	// ----------------------------------------- overriding AbstractPresenter

	@Override
	protected void loadDatas(boolean forceRefresh) {
		this.doInitDatas();
	}

	// ---------------------------------------- public methods
	public void setCloseDialogClickHandler(ClickHandler closeDialogClickHandler) {

		this.closeDialogClickHandler = closeDialogClickHandler;
	}

	// --------------------------------------- private methods

	private void bind() {

		final ClickHandler buttonClickHandler = new ClickHandler() {

			@Override
			public void onClick(ClickEvent event) {
				Object source = event.getSource();
				if (source != null && source instanceof ButtonViewAction) {
					view.setActionResult(null, null);
					ButtonViewAction button = (ButtonViewAction) source;
					view.selectButton(button);

					ViewActionEnum viewActionEnum = ViewActionEnum.valueOf(button.getViewAction());
					switch (viewActionEnum) {
					case update:
						doBeforeUpdateReport();
						break;
					case cancel:
						doCancel();
						break;
					}
				}
			}

		};
		this.view.setViewActionClickHandler(buttonClickHandler);
	}

	private void doInitDatas() {
		log.config("doInitDatas()");

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

	private void doCancel() {
		log.config("cancel update report.");
		this.closeDialog();
	}

	private void doBeforeUpdateReport() {
		log.config("Confirme before update report.");

		String[] description = this.getUpdateDescription();
		int size = 1 + description.length;
		String[] arguments = new String[size];
		arguments[0] = "Confirmer la modification du rapport ";
		for (int i = 0; i < description.length; i++) {
			String line = description[i];
			arguments[i + 1] = line;
		}

		DialogBox dialogBox = WidgetUtils.buildDialogBoxWithOkFocused("Update report ", arguments, null, true,
				new IActionCallback() {

					@Override
					public void onCancel() {
						view.setActionResult("Update report canceled!", LogStatus.warning);
					}

					@Override
					public void onOk() {
						view.setActionResult("Update report in progres...", LogStatus.warning);
						doUpdateReport();
					}

				});
		WidgetUtils.centerDialogAndShow(dialogBox);

	}

	private String[] getUpdateDescription() {

		VoTestSuiteForEdit datas = view.getEditReportData();

		final VoUser user = this.mapId2Users.get(datas.getUserId());

		String[] description = new String[3];
		description[0] = "IPTVKIT : " + ((datas.getIptvkit() == null) ? "undefined" : datas.getIptvkit());
		description[1] = "USER    : " + ((user == null) ? "undefined" : user.getName());
		description[2] = "DATE    : " + ((datas.getDate() == null) ? "undefined" : DF.format(datas.getDate()));
		return description;
	}

	private void doUpdateReport() {

		log.config("doUpdateReport()");
		this.view.waiting(true);

		VoTestSuiteForEdit suiteToUpdate = view.getEditReportData();
		final String message = " when updating single report information (" + suiteToUpdate.getName() + ")!";
		this.rpcService.updateTestSuiteInfo(suiteToUpdate, new MyAsyncCallback<VoDatasValidation>("") {

			@Override
			public void onSuccess(VoDatasValidation result) {

				if (!result.isValid()) {
					view.setActionResult("Failure " + message, LogStatus.warning);
				} else {
					view.setActionResult("Success " + message, LogStatus.success);
					closeDialog();
				}
				view.waiting(false);

			}
		});

	}

	private void closeDialog() {
		this.closeDialogClickHandler.onClick(new ClickEvent() {
		});
	}

	// -------------------------------- VIEW
	public interface IEditReportView extends IMainView {

		public void setDatas(VoEditReportDatas datas);

		public VoTestSuiteForEdit getEditReportData();

	}

	// ===================================== INNER CLASS
	private abstract class MyAsyncCallback<T> implements AsyncCallback<T> {

		private final String errorMessage;

		private MyAsyncCallback(String errorMessage) {
			this.errorMessage = errorMessage;
		}

		@Override
		public void onFailure(Throwable caught) {

			if (this.errorMessage != null) {
				String errorMessage = this.errorMessage + "<br/>" + caught.getMessage();
				view.setActionResult(errorMessage, LogStatus.error);
				log.severe(errorMessage);
				view.waiting(false);
			}
		}

	}
}
