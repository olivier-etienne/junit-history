package com.francetelecom.orangetv.junithistory.client.view.admin;

import java.util.List;

import com.francetelecom.orangetv.junithistory.client.presenter.PageAdminPresenter.TabViewEnum;
import com.francetelecom.orangetv.junithistory.client.view.IView;
import com.francetelecom.orangetv.junithistory.shared.vo.AbstractVoIdName;
import com.google.gwt.event.dom.client.ClickHandler;

public interface IAdminSubView<T extends AbstractVoIdName> extends IView {

	public TabViewEnum getType();

	public void setGridActionClickHandler(ClickHandler actionClickHandler);

	public void setDatas(List<T> listItems);

}
