package com.francetelecom.orangetv.junithistory.client.presenter.admin;

import com.francetelecom.orangetv.junithistory.client.presenter.IPresenter;

public interface IEditItemPresenter extends IPresenter {

	public enum ViewActionEnum {
		update, cancel
	}

	public void setGridSubPresenter(IGridSubPresenter presenter);

}
