package com.francetelecom.orangetv.junithistory.client.presenter.admin;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.presenter.ClientFactory;
import com.francetelecom.orangetv.junithistory.client.presenter.PageAdminPresenter.TabViewEnum;
import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.view.admin.IAdminSubView;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;
import com.francetelecom.orangetv.junithistory.shared.vo.VoGroupForGrid;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter pour la view d'administration Group
 * 
 * @author sylvie
 * 
 */
public class GroupSubPresenter extends AbstractGridSubPresenter<VoGroupForGrid> {

	private final static Logger log = Logger.getLogger("GroupSubPresenter");

	// -------------------------------- constructor
	public GroupSubPresenter(ClientFactory clientFactory, IGwtJUnitHistoryServiceAsync service, EventBus eventBus,
			IGroupSubView view) {
		super(clientFactory, service, eventBus, view);
	}

	// ---------------------------------- implementing AbstractGridSubPresenter
	@Override
	protected TabViewEnum getType() {
		return TabViewEnum.tabGroup;
	}

	@Override
	protected void doDeleteItem(int groupId, IDeleteCallback callback) {

		this.rpcService.deleteTestGroup(groupId, new MyDeleteAsyncCallback(" when deleting STB group!", callback));

	}

	@Override
	protected String[] getItemDescription(VoGroupForGrid group) {
		String[] description = new String[3];

		description[0] = "NAME : " + (ValueHelper.isStringEmptyOrNull(group.getName()) ? "undefined" : group.getName());
		description[1] = "STB    : " + (ValueHelper.isStringEmptyOrNull(group.getStb()) ? "undefined" : group.getStb());
		description[2] = "PREFIX    : "
				+ (ValueHelper.isStringEmptyOrNull(group.getPrefix()) ? "undefined" : group.getPrefix());
		return description;
	}

	// ---------------------------- overriding AbstractPresenter

	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected void loadDatas(boolean forceRefresh) {

		this.rpcService.getListGroups(super.buildGetListCallback(" when getting list of groups."));

	}

	// =========================== VIEW
	public static interface IGroupSubView extends IAdminSubView<VoGroupForGrid> {

	}
}
