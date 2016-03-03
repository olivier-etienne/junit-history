package com.francetelecom.orangetv.junithistory.server.dto;

import java.util.List;

/*
 * 
 * Encapsule une page Html principale et une liste de page Html associées
 */
public class DtoListHtmlPages implements IDto {

	private final DtoHtmlPage overviewPage;

	private List<DtoHtmlPage> listHtmlPages;

	// --------------------------------- constructor
	public DtoListHtmlPages(DtoHtmlPage mainPage) {
		this.overviewPage = mainPage;
	}

	// ---------------------------------- accessors
	public DtoHtmlPage getMainPage() {
		return overviewPage;
	}

	public List<DtoHtmlPage> getListHtmlPages() {
		return listHtmlPages;
	}

	public void setListHtmlPages(List<DtoHtmlPage> listHtmlPages) {
		this.listHtmlPages = listHtmlPages;
	}

}
