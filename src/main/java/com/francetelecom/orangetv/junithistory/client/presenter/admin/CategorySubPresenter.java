package com.francetelecom.orangetv.junithistory.client.presenter.admin;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.presenter.ClientFactory;
import com.francetelecom.orangetv.junithistory.client.presenter.PageAdminPresenter.TabViewEnum;
import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.view.admin.IAdminSubView;
import com.francetelecom.orangetv.junithistory.shared.util.ObjectUtils;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;
import com.francetelecom.orangetv.junithistory.shared.vo.VoCategoryForGrid;
import com.google.web.bindery.event.shared.EventBus;

/**
 * Presenter pour la view d'administration Categories
 * 
 * @author sylvie
 * 
 */
public class CategorySubPresenter extends AbstractGridSubPresenter<VoCategoryForGrid> {

	private final static Logger log = Logger.getLogger("CategorySubPresenter");

	// -------------------------------- constructor
	public CategorySubPresenter(ClientFactory clientFactory, IGwtJUnitHistoryServiceAsync service, EventBus eventBus,
			ICategorySubView view) {
		super(clientFactory, service, eventBus, view);
	}

	// ---------------------------------- implementing AbstractGridSubPresenter
	@Override
	protected TabViewEnum getType() {
		return TabViewEnum.tabCategory;
	}

	@Override
	protected void doDeleteItem(int categoryId, IDeleteCallback callback) {

		this.rpcService.deleteCategory(categoryId, new MyDeleteAsyncCallback(" when deleting category!", callback));

	}

	@Override
	protected String[] getItemDescription(VoCategoryForGrid category) {

		String[] description = new String[3];
		description[0] = "NAME : "
				+ ((ValueHelper.isStringEmptyOrNull(category.getName())) ? "undefined" : category.getName());
		String list = ObjectUtils.tabToString(category.getSuiteNames(), VoCategoryForGrid.SEPARATOR);
		description[1] = "LIST    : " + ((ValueHelper.isStringEmptyOrNull(list)) ? "undefined" : list);

		return description;
	}

	// -------------------------------- overriding AbstractPresenter

	@Override
	protected Logger getLog() {
		return log;
	}

	@Override
	protected void loadDatas(boolean forceRefresh) {

		this.rpcService.getListTestClassCategories(super.buildGetListCallback(" when getting list of categories."));
	}

	// ===================== VIEW
	public static interface ICategorySubView extends IAdminSubView<VoCategoryForGrid> {

	}

}
