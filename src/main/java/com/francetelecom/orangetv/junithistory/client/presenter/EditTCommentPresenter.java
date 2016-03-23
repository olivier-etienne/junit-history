package com.francetelecom.orangetv.junithistory.client.presenter;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.view.IMainView;
import com.francetelecom.orangetv.junithistory.client.view.IView;
import com.francetelecom.orangetv.junithistory.client.view.IView.LogStatus;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoDatasValidation;
import com.francetelecom.orangetv.junithistory.shared.vo.VoEditTCommentDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdName;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdUtils;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestCommentForEdit;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUser;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Widget;
import com.google.web.bindery.event.shared.EventBus;

/***
 * Presenter pour l'edition ou la creation d'un commentaire de test
 */
public class EditTCommentPresenter extends AbstractEditItemPresenter implements IMainPresenter {

	private final static Logger log = Logger.getLogger("EditTCommentPresenter");

	private final IEditTCommentView view;
	private ClickHandler closeDialogClickHandler;

	private int testId;
	private int tcommentId;

	private Map<Integer, VoUser> mapId2Users = new HashMap<>();

	// ------------------------------------------- constructor
	public EditTCommentPresenter(IGwtJUnitHistoryServiceAsync service, EventBus eventBus, IEditTCommentView view) {
		super(service, eventBus, "Comment");
		this.view = view;
		this.bind();
	}

	// -------------------------- implementing AbstractEditItemPresenter
	@Override
	protected void refreshList() {

	}

	@Override
	protected void closeDialog() {
		this.closeDialogClickHandler.onClick(new ClickEvent() {
		});
	}

	@Override
	protected String[] getUpdateDescription() {

		VoTestCommentForEdit datas = view.getVoDatas();

		String[] description = new String[3];
		description[0] = "TITLE : "
				+ ((ValueHelper.isStringEmptyOrNull(datas.getTitle())) ? "undefined" : datas.getTitle());
		description[1] = "DESCRIPTION    : "
				+ ((ValueHelper.isStringEmptyOrNull(datas.getDescription())) ? "undefined" : datas.getDescription()
						.substring(10));
		VoIdName tester = this.mapId2Users.get(datas.getTesterId());
		description[2] = "TESTER :" + ((tester == null) ? "undefined" : tester.getName());
		return description;

	}

	@Override
	protected void doUpdateItem(final IValidationCallback validationCallback) {

		final String message = " when updating test comment!";
		this.rpcService.createOrUpdateTComment(view.getVoDatas(), new MyAsyncCallback<VoDatasValidation>(message) {

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

		final String message = " when validating test comment!";
		this.rpcService.validTComment(view.getVoDatas(), new MyAsyncCallback<VoDatasValidation>(message) {

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

	// ----------------------------- implementing IMainPresenter
	@Override
	public IMainView getMainView() {
		// TODO Auto-generated method stub
		return null;
	}

	// ------------------------------- Implementing IPresenter
	@Override
	public IView getView() {
		return this.view;
	}

	// ------------------------------- overriding AbstractPresenter
	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected Widget getViewAsWidget() {
		return this.view == null ? null : this.view.asWidget();
	}

	@Override
	protected void loadDatas(boolean forceRefresh) {

		// get params
		Integer currentTestId = this.params.containsKey(PARAMS_TEST_ID) ? (Integer) this.params.get(PARAMS_TEST_ID)
				: IVo.ID_UNDEFINED;
		testId = currentTestId.intValue();

		Integer currentTCommentId = this.params.containsKey(PARAMS_TCOMMENT_ID) ? (Integer) this.params
				.get(PARAMS_TCOMMENT_ID) : IVo.ID_UNDEFINED;
		tcommentId = currentTCommentId.intValue();

		if (currentTestId == IVo.ID_UNDEFINED) {
			view.setActionResult("Test is undefined!", LogStatus.error);
			return;
		}

		this.doLoadTCommentDatas(testId, tcommentId);
	}

	// ---------------------------------------- public methods
	public void setCloseDialogClickHandler(ClickHandler closeDialogClickHandler) {

		this.closeDialogClickHandler = closeDialogClickHandler;
	}

	// ---------------------------------- private methods

	private void doLoadTCommentDatas(int testId, int tcommentId) {

		this.view.waiting(true);

		final String message = " when loading test comment!";
		this.rpcService.getTCommentDatas(testId, tcommentId, new MyAsyncCallback<VoEditTCommentDatas>("Error "
				+ message) {

			@Override
			public void onSuccess(VoEditTCommentDatas datas) {
				mapId2Users = VoIdUtils.getMapId2Item(datas.getListTesters());
				view.setActionResult("Success " + message, LogStatus.success);
				view.setDatas(datas);

				if (datas.getTCommentForEdit().isReadOnly()) {
					view.activeButtons(ViewActionEnum.cancel.name());
				} else {
					view.activeButtons(ViewActionEnum.cancel.name(), ViewActionEnum.update.name());
				}

				view.waiting(false);
			}
		});
	}

	// ================================= VIEW
	public interface IEditTCommentView extends IMainView {

		public void setDatas(VoEditTCommentDatas voDatas);

		public VoTestCommentForEdit getVoDatas();

	}

}
