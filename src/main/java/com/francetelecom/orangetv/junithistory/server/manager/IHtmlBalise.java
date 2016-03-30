package com.francetelecom.orangetv.junithistory.server.manager;

import java.text.MessageFormat;

public interface IHtmlBalise {

	public static final String SUFFIXE_STATS = " _stats";

	public final static String LINK_CSS = "<link rel='stylesheet' type='text/css' href='style.css'>";
	public final static String META_CHARSET = "<meta http-equiv='Content-Type' content='text/html; charset=UTF-8' />";

	public final static String HTML_BEGIN = "<html>";
	public final static String HTML_END = "</html>";

	public final static String TABLE_BEGIN = "<table>";
	public final static String TABLE_END = "</table>";

	public final static String TBODY_BEGIN = "<tbody>";
	public final static String TBODY_END = "</tbody>";

	public final static String TH_BEGIN = "<th>";
	public final static String TH_END = "</th>";

	public final static String TR_BEGIN = "<tr>";
	public final static String TR_END = "</tr>";

	public final static String TD_BEGIN = "<td>";
	public final static String TD_END = "</td>";

	public final static String HEAD_BEGIN = "<head>";
	public final static String HEAD_END = "</head>";

	public final static String TITLE_BEGIN = "<title>";
	public final static String TITLE_END = "</title>";

	public final static String BODY_BEGIN = "<body>";
	public final static String BODY_END = "</body>";

	public final static String SPAN_BEGIN = "<span>";
	public final static String SPAN_END = "</span>";

	public final static String TT_BEGIN = "<tt>";
	public final static String TT_END = "</tt>";

	public final static String DIV_BEGIN = "<div>";
	public final static String DIV_END = "</div>";

	public final static String BR = "<br>";
	public final static String SPACE = "&nbsp";
	public final static String INF = "&lt;";
	public final static String SUP = "&gt;";
	public final static String ESPERLUETTE = "&amp;";
	public final static String DOUBLE_QUOTE = "&quot;";
	public final static String SIMPLE_QUOTE = "&#39;";

	public final static String H1_BEGIN = "<h1>";
	public final static String H1_END = "</h1>";

	public final static String H2_BEGIN = "<h2>";
	public final static String H2_END = "</h2>";

	public final static String H3_BEGIN = "<h3>";
	public final static String H3_END = "</h3>";

	public final static String P_BEGIN = "<p>";
	public final static String P_END = "</p>";

	public final static String LABEL_BEGIN = "<label>";
	public final static String LABEL_END = "</label>";

	public final static String PRE_BEGIN = "<pre>";
	public final static String PRE_END = "</pre>";

	public final static String LINK_BEGIN = "<a>";
	public final static String LINK_END = "</a>";

	public final static String END = ">";

	public final static String CLASS_ANALYSIS = "analysis";
	public final static String CLASS_SUITE = "suite";
	public final static String CLASS_LOGS = "logs";
	public final static String CLASS_STATS = "stats";
	public final static String CLASS_LIST = "list";
	public final static String CLASS_COUNT = "count";
	public final static String CLASS_EVOL = "evol";
	public final static String CLASS_SUMMARY = "summary";
	public final static String CLASS_UP = "imgArrowUp";
	public final static String CLASS_DOWN = "imgArrowDown";
	public final static String CLASS_CAT = "cat";

	public final static String W_80PX = "80px";

	public final static String P = "p";
	public final static String TD = "td";
	public final static String TR = "tr";
	public final static String TH = "th";
	public final static String DIV = "div";
	public final static String SPAN = "span";
	public final static String TABLE = "table";
	public final static String PRE = "pre";

	public final static MessageFormat MF_BALISE_WIDTH_TITLE_VALUE = new MessageFormat(
			"<{0} width=\"{1}\" title=\"{2}\">{3}</{0}>");

	public final static MessageFormat MF_BALISE_CLASS_TITLE_VALUE = new MessageFormat(
			"<{0} class=\"{1}\" title=\"{2}\">{3}</{0}>");

	public final static MessageFormat MF_BALISE_WIDTH_CLASS_BEGIN = new MessageFormat(
			"<{0} width=\"{1}\" class=\"{2}\" >");
	public final static MessageFormat MF_BALISE_WIDTH_CLASS_TITLE_BEGIN = new MessageFormat(
			"<{0} width=\"{1}\" class=\"{2}\" title=\"{3}\" >");
	public final static MessageFormat MF_BALISE_WIDTH_TITLE_BEGIN = new MessageFormat(
			"<{0} width=\"{1}\" title=\"{2}\">");
	public final static MessageFormat MF_BALISE_CLASS_TITLE_BEGIN = new MessageFormat(
			"<{0} class=\"{1}\" title=\"{2}\">");

	public final static MessageFormat MF_BALISE_CLASS_BEGIN = new MessageFormat("<{0} class=\"{1}\">");
	public final static MessageFormat MF_BALISE_WIDTH_BEGIN = new MessageFormat("<{0} width=\"{1}\">");
	public final static MessageFormat MF_BALISE_MINWIDTH_BEGIN = new MessageFormat("<{0} min-width=\"{1}\">");

	public final static MessageFormat MF_LINK_NAME = new MessageFormat("<a  name=\"{0}\">");
	public final static MessageFormat MF_LINK_HREF = new MessageFormat("<a  href=\"{0}\">");
	public final static MessageFormat MF_LINK_CLASS_HREF = new MessageFormat("<a  class=\"{0}\" href=\"{1}\">");

}
