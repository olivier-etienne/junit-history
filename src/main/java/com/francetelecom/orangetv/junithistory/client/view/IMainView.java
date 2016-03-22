package com.francetelecom.orangetv.junithistory.client.view;

import com.francetelecom.orangetv.junithistory.client.AppController.MainPanelViewEnum;

/**
 * Contrat d'interface pour toutes les vues
 * 
 * @author ndmz2720
 *
 */
public interface IMainView extends IView {

	public MainPanelViewEnum getViewType();

}
