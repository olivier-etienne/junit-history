package com.francetelecom.orangetv.junithistory.server.dto;

import com.francetelecom.orangetv.junithistory.server.util.ListLines;

public class DtoHtmlPage implements IDto {

	// nom de la page avec son extension
	private final String pageName;
	// nom de la page sans son extension
	private final String title;

	// short name (not required)
	private String shortTitle;

	private ListLines listLines;

	// ------------------------- constructor

	public DtoHtmlPage(String title, String pageName) {
		this.pageName = pageName;
		this.title = title;
	}

	// --------------------------- accessor

	public String getPageName() {
		return pageName;
	}

	public String getShortTitle() {
		return shortTitle;
	}

	public void setShortTitle(String shortTitle) {
		this.shortTitle = shortTitle;
	}

	public void setListLines(ListLines listLines) {
		this.listLines = listLines;
	}

	public String getTitle() {
		return title;
	}

	public ListLines getListLines() {
		return listLines;
	}

}
