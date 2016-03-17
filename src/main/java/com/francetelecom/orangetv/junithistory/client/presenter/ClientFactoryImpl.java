package com.francetelecom.orangetv.junithistory.client.presenter;

import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.client.AppController.MainPanelViewEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.DefectPresenter.IDefectView;
import com.francetelecom.orangetv.junithistory.client.presenter.EditReportPresenter.IEditReportView;
import com.francetelecom.orangetv.junithistory.client.presenter.HistoricReportPresenter.IHistoricReportView;
import com.francetelecom.orangetv.junithistory.client.presenter.PageAdminPresenter.IPageAdminView;
import com.francetelecom.orangetv.junithistory.client.presenter.PageAdminPresenter.TabViewEnum;
import com.francetelecom.orangetv.junithistory.client.presenter.SingleReportPresenter.ISingleReportView;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.CategorySubPresenter;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.CategorySubPresenter.ICategorySubView;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.EditCategoryPresenter;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.EditCategoryPresenter.IEditCategoryView;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.EditGroupPresenter;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.EditGroupPresenter.IEditGroupView;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.EditTesterPresenter;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.EditTesterPresenter.IEditUserView;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.GroupSubPresenter;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.GroupSubPresenter.IGroupSubView;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.IEditItemPresenter;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.TesterSubPresenter;
import com.francetelecom.orangetv.junithistory.client.presenter.admin.TesterSubPresenter.IUserSubView;
import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryService;
import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryServiceAsync;
import com.francetelecom.orangetv.junithistory.client.view.DefectView;
import com.francetelecom.orangetv.junithistory.client.view.EditReportView;
import com.francetelecom.orangetv.junithistory.client.view.HistoricReportView;
import com.francetelecom.orangetv.junithistory.client.view.IMainView;
import com.francetelecom.orangetv.junithistory.client.view.PageAdminView;
import com.francetelecom.orangetv.junithistory.client.view.SingleReportView;
import com.francetelecom.orangetv.junithistory.client.view.admin.CategorySubView;
import com.francetelecom.orangetv.junithistory.client.view.admin.EditCategoryView;
import com.francetelecom.orangetv.junithistory.client.view.admin.EditGroupView;
import com.francetelecom.orangetv.junithistory.client.view.admin.EditTesterView;
import com.francetelecom.orangetv.junithistory.client.view.admin.GroupSubView;
import com.francetelecom.orangetv.junithistory.client.view.admin.IAdminSubView;
import com.francetelecom.orangetv.junithistory.client.view.admin.IEditItemView;
import com.francetelecom.orangetv.junithistory.client.view.admin.TesterSubView;
import com.google.gwt.core.client.GWT;
import com.google.web.bindery.event.shared.EventBus;
import com.google.web.bindery.event.shared.SimpleEventBus;

public class ClientFactoryImpl implements ClientFactory {

	private final static Logger log = Logger.getLogger("ClientFactoryImpl");

	private final EventBus eventBus = new SimpleEventBus();
	private final IGwtJUnitHistoryServiceAsync service = GWT.create(IGwtJUnitHistoryService.class);

	private SingleReportPresenter singleReportPresenter;
	private ISingleReportView singleReportView;

	private HistoricReportPresenter historicReportPresenter;
	private IHistoricReportView historicReportView;

	private IEditReportView editReportView;
	private EditReportPresenter editReportPresenter;

	private IPageAdminView pageAdminView;
	private PageAdminPresenter pageAdminPresenter;

	private TesterSubPresenter userSubPresenter;
	private IUserSubView userSubView;

	private CategorySubPresenter categorySubPresenter;
	private ICategorySubView categorySubView;

	private GroupSubPresenter groupSubPresenter;
	private IGroupSubView groupSubView;

	private EditTesterView editUserView;
	private EditTesterPresenter editUserPresenter;

	private EditCategoryView editCategoryView;
	private EditCategoryPresenter editCategoryPresenter;

	private EditGroupView editGroupView;
	private EditGroupPresenter editGroupPresenter;

	private DefectView defectView;
	private DefectPresenter defectPresenter;

	// ----------------------------- implementing ClientFactory
	@Override
	public EventBus getEventBus() {
		return this.eventBus;
	}

	@Override
	public IGwtJUnitHistoryServiceAsync getService() {
		return this.service;
	}

	@Override
	public IMainView getMainView(MainPanelViewEnum viewType) {

		if (viewType == null) {
			return null;
		}
		switch (viewType) {
		case admin:
			return this.getPageAdminView();
		case defect:
			return this.getDefectView();
		case editReport:
			return this.getEditReportView();
		case historicReport:
			return this.getHistoricReportView();
		case singleReport:
			return this.getSingleReportView();

		}
		return null;
	}

	@Override
	public IAdminSubView<?> getAdminSubView(TabViewEnum viewType) {

		if (viewType == null) {
			return null;
		}
		switch (viewType) {
		case tabTester:
			return this.getUserSubView();

		case tabCategory:
			return this.getCategorySubView();

		case tabGroup:
			return this.getGroupSubView();

		}
		return null;
	}

	@Override
	public IEditItemView getEditView(TabViewEnum viewType) {

		if (viewType == null) {
			return null;
		}
		switch (viewType) {
		case tabTester:
			return this.getEditUserView();

		case tabCategory:
			return this.getEditCategoryView();

		case tabGroup:
			return this.getEditGroupView();

		}
		return null;

	}

	@Override
	public IMainPresenter getMainPresenter(MainPanelViewEnum view) {

		if (view == null) {
			return null;
		}
		switch (view) {
		case admin:
			return this.pageAdminPresenter;
		case defect:
			return this.defectPresenter;
		case editReport:
			return this.editReportPresenter;
		case historicReport:
			return this.historicReportPresenter;
		case singleReport:
			return this.singleReportPresenter;

		}
		return null;
	}

	@Override
	public IMainPresenter buildMainPresenter(IMainView view) {

		if (view == null) {
			return null;
		}
		MainPanelViewEnum viewType = view.getViewType();
		switch (viewType) {
		case admin:

			return this.buildPageAdminPresenter((IPageAdminView) view);
		case defect:
			return this.buildDefectPresenter((IDefectView) view);
		case editReport:
			return this.buildEditReportPresenter((IEditReportView) view);
		case historicReport:
			return this.buildHistoricReportPresenter((IHistoricReportView) view);
		case singleReport:
			return this.buildSingleReportPresenter((ISingleReportView) view);

		}
		return null;
	}

	@Override
	public IPresenter buildAdminSubPresenter(IAdminSubView<?> view) {

		if (view == null) {
			return null;
		}
		TabViewEnum viewType = view.getType();

		switch (viewType) {
		case tabTester:
			return this.buildUserSubPresenter((IUserSubView) view);

		case tabCategory:
			return this.buildCategorySubPresenter((ICategorySubView) view);

		case tabGroup:
			return this.buildGroupSubPresenter((IGroupSubView) view);

		}

		return null;
	}

	@Override
	public IEditItemPresenter buildEditPresenter(IEditItemView view) {

		if (view == null) {
			return null;
		}
		TabViewEnum viewType = view.getType();

		switch (viewType) {
		case tabTester:
			return this.buildEditUserPresenter((IEditUserView) view);

		case tabCategory:
			return this.buildEditCategoryPresenter((IEditCategoryView) view);

		case tabGroup:
			return this.buildEditGroupPresenter((IEditGroupView) view);

		}

		return null;

	}

	@Override
	public IPresenter getAdminSubPresenter(TabViewEnum viewType) {

		if (viewType == null) {
			return null;
		}

		switch (viewType) {
		case tabTester:
			return this.userSubPresenter;

		case tabCategory:
			return this.categorySubPresenter;

		case tabGroup:
			return this.groupSubPresenter;

		}

		return null;

	}

	@Override
	public IEditItemPresenter getEditPresenter(TabViewEnum viewType) {

		if (viewType == null) {
			return null;
		}

		switch (viewType) {
		case tabTester:
			return this.editUserPresenter;

		case tabCategory:
			return this.editCategoryPresenter;

		case tabGroup:
			return null;

		}

		return null;

	}

	// ---------------------------------------- private methods

	private ISingleReportView getSingleReportView() {
		if (this.singleReportView == null) {
			this.singleReportView = new SingleReportView();
		}
		return this.singleReportView;
	}

	private IHistoricReportView getHistoricReportView() {
		if (this.historicReportView == null) {
			this.historicReportView = new HistoricReportView();
		}
		return this.historicReportView;
	}

	private IEditReportView getEditReportView() {
		if (this.editReportView == null) {
			this.editReportView = new EditReportView();
		}
		return this.editReportView;
	}

	private IPageAdminView getPageAdminView() {
		if (this.pageAdminView == null) {
			this.pageAdminView = new PageAdminView();
		}
		return this.pageAdminView;
	}

	private IDefectView getDefectView() {
		if (this.defectView == null) {
			this.defectView = new DefectView();
		}
		return this.defectView;
	}

	private IUserSubView getUserSubView() {
		if (this.userSubView == null) {
			this.userSubView = new TesterSubView();
		}
		return this.userSubView;
	}

	private ICategorySubView getCategorySubView() {
		if (this.categorySubView == null) {
			this.categorySubView = new CategorySubView();
		}
		return this.categorySubView;
	}

	private IGroupSubView getGroupSubView() {
		if (this.groupSubView == null) {
			this.groupSubView = new GroupSubView();
		}
		return this.groupSubView;
	}

	private IEditItemView getEditUserView() {
		if (this.editUserView == null) {
			this.editUserView = new EditTesterView();
		}
		return this.editUserView;
	}

	private IEditItemView getEditCategoryView() {

		if (this.editCategoryView == null) {
			this.editCategoryView = new EditCategoryView();
		}
		return this.editCategoryView;
	}

	private IEditItemView getEditGroupView() {
		if (this.editGroupView == null) {
			this.editGroupView = new EditGroupView();
		}
		return this.editGroupView;
	}

	private IPresenter buildGroupSubPresenter(IGroupSubView view) {
		this.groupSubPresenter = new GroupSubPresenter(this, service, eventBus, view);
		return this.groupSubPresenter;
	}

	private IPresenter buildCategorySubPresenter(ICategorySubView view) {
		this.categorySubPresenter = new CategorySubPresenter(this, service, eventBus, view);
		return this.categorySubPresenter;
	}

	private IPresenter buildUserSubPresenter(IUserSubView view) {

		this.userSubPresenter = new TesterSubPresenter(this, service, eventBus, view);
		return this.userSubPresenter;
	}

	private SingleReportPresenter buildSingleReportPresenter(ISingleReportView view) {
		this.singleReportPresenter = new SingleReportPresenter(service, eventBus, view);
		return this.singleReportPresenter;
	}

	private HistoricReportPresenter buildHistoricReportPresenter(IHistoricReportView view) {
		this.historicReportPresenter = new HistoricReportPresenter(service, eventBus, view);
		return this.historicReportPresenter;
	}

	private EditReportPresenter buildEditReportPresenter(IEditReportView view) {
		this.editReportPresenter = new EditReportPresenter(service, eventBus, view);
		return this.editReportPresenter;
	}

	private PageAdminPresenter buildPageAdminPresenter(IPageAdminView view) {

		this.pageAdminPresenter = new PageAdminPresenter(this, service, eventBus, view);
		return this.pageAdminPresenter;
	}

	private EditTesterPresenter buildEditUserPresenter(IEditUserView view) {
		this.editUserPresenter = new EditTesterPresenter(service, eventBus, view);
		return this.editUserPresenter;
	}

	private EditCategoryPresenter buildEditCategoryPresenter(IEditCategoryView view) {
		this.editCategoryPresenter = new EditCategoryPresenter(service, eventBus, view);
		return this.editCategoryPresenter;
	}

	private EditGroupPresenter buildEditGroupPresenter(IEditGroupView view) {
		this.editGroupPresenter = new EditGroupPresenter(service, eventBus, view);
		return this.editGroupPresenter;
	}

	private DefectPresenter buildDefectPresenter(IDefectView view) {
		this.defectPresenter = new DefectPresenter(service, eventBus, view);
		return this.defectPresenter;
	}

}
