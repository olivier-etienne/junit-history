package com.francetelecom.orangetv.junithistory.client.view.admin;

import com.francetelecom.orangetv.junithistory.client.presenter.PageAdminPresenter.TabAdminViewEnum;
import com.francetelecom.orangetv.junithistory.client.view.IView;
import com.google.gwt.event.dom.client.ClickHandler;

/**
 * Interface commune à toutes les vues d'edition des pages admin
 * 
 * @author sylvie
 * 
 */
public interface IEditItemView extends IView {

	public TabAdminViewEnum getType();

	public void setGridActionClickHandler(final ClickHandler actionClickHandler);

}
