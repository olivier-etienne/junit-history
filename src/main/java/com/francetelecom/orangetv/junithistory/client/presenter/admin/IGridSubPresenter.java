package com.francetelecom.orangetv.junithistory.client.presenter.admin;

import com.francetelecom.orangetv.junithistory.client.presenter.IPresenter;

public interface IGridSubPresenter extends IPresenter {

	public enum ViewActionEnum {
		createItem, refreshList
	}

	public enum GridActionButtonEnum {
		delete, edit

	}

	public void closeDialogBox(boolean updateDone);

	public void refresh();

}
