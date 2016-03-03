package com.francetelecom.orangetv.junithistory.server.service;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.francetelecom.orangetv.junithistory.server.manager.IHtmlBalise;
import com.francetelecom.orangetv.junithistory.server.manager.PathManager;
import com.francetelecom.orangetv.junithistory.server.manager.ShowHtmlManager;
import com.francetelecom.orangetv.junithistory.server.manager.ShowHtmlManager.HttpAddress;
import com.francetelecom.orangetv.junithistory.server.util.SessionHelper;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoListReportResponse;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSingleReportResponse;

/**
 * Redirection sur une url contenant un site temporaire (lié à la session)
 * 
 * @author ndmz2720
 *
 */
public class ShowHtmlServlet extends AbstractServlet implements IHtmlBalise {

	private static final long serialVersionUID = 1L;

	private static final Logger log = Logger.getLogger(ShowHtmlServlet.class.getName());

	private static final String PARAM_REQUEST_SUITE_ID = "suiteId";
	private static final String PARAM_REQUEST_GROUP_ID = "groupId";
	private static final String PARAM_REQUEST_FORCE_REFRESH = "forceRefresh";

	private static final String ERROR_MESSAGE = "Wrong params!!";

	// ----------------------------- singleton
	private static ShowHtmlServlet instance;

	public static ShowHtmlServlet get() {
		if (instance == null) {
			instance = new ShowHtmlServlet();
		}
		return instance;
	}

	// ------------------------------------- public methods (singleton)

	public URL buildUrlForGroupId(int groupId, HttpAddress httpAddress, boolean forceRefresh)
			throws JUnitHistoryException {

		return this.buildUrlForId(PARAM_REQUEST_GROUP_ID, groupId, httpAddress, forceRefresh);
	}

	public URL buildUrlForSuiteId(int suiteId, HttpAddress httpAddress, boolean forceRefresh)
			throws JUnitHistoryException {

		return this.buildUrlForId(PARAM_REQUEST_SUITE_ID, suiteId, httpAddress, forceRefresh);
	}

	// --------------------------------------- implementing HttpServlet

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.info("doGet()");

		ActionResponse actionResponse = this.doAction(req);
		if (!actionResponse.success) {

			resp.setHeader("Access-Control-Allow-Origin", "*");

			PrintWriter out = resp.getWriter();
			out.println(HTML_BEGIN);
			out.println(BODY_BEGIN);
			out.println(H1_BEGIN + "Show Html reports..." + H1_END);
			out.println(H2_BEGIN + actionResponse.errorMessage + H2_END);
			out.println(BODY_END);
			out.println(HTML_END);

		} else {

			resp.sendRedirect(actionResponse.url);
		}

	}

	// -------------------------------------------- private methods
	private URL buildUrlForId(String param, int id, HttpAddress httpAddress, boolean forceRefresh)
			throws JUnitHistoryException {

		String relativePath = "showhtml?" + param + "=" + id;
		if (forceRefresh) {
			relativePath += "&" + PARAM_REQUEST_FORCE_REFRESH + "=true";
		}
		return PathManager.get().buildUrl(httpAddress, relativePath);
	}

	/**
	 * on construit le site sauf si il a déjà été construit recemment dans la
	 * meme session
	 */
	private ActionResponse doAction(HttpServletRequest req) throws IOException {

		// parametre de la requete groupId
		int groupId = ValueHelper.getIntValue(req.getParameter(PARAM_REQUEST_GROUP_ID), IVo.ID_UNDEFINED);

		if (groupId != IVo.ID_UNDEFINED) {
			return this.doActionForGroupId(groupId, req);
		}

		// parametre de la requete suiteId
		int suiteId = ValueHelper.getIntValue(req.getParameter(PARAM_REQUEST_SUITE_ID), IVo.ID_UNDEFINED);

		if (suiteId != IVo.ID_UNDEFINED) {
			return this.doActionForSuiteId(suiteId, req);
		}

		return new ActionResponse(false, ERROR_MESSAGE);
	}

	/*
	 * Construit un site html pour un groupId
	 */
	private ActionResponse doActionForGroupId(int groupId, HttpServletRequest req) throws IOException {

		String errorMessage = ERROR_MESSAGE;
		try {

			String url = null;

			// from session
			url = this.findUrlFromSession(this.getSession(req), PARAM_REQUEST_GROUP_ID, groupId);

			if (url == null) {
				// on contruit les pages html
				VoListReportResponse voResponse = ShowHtmlManager.get().showHtmlListReportForGroup(groupId,
						this.getSessionId(req), this.getHttpAddress(req));
				errorMessage = voResponse.getComment();

				// on construit l'url
				url = voResponse.getUrl();
			}

			if (url == null) {
				return new ActionResponse(false, errorMessage);
			}

			// on memorise les info sur la session pour la prochaine requete
			// identique
			this.saveUrlInSession(this.getSession(req), PARAM_REQUEST_GROUP_ID, groupId, url);

			// on redirige vers cette url
			return new ActionResponse(url);

		} catch (JUnitHistoryException ex) {
			log.severe("Error in doActionForGroupId(): " + ex.toString());
		}

		return new ActionResponse(false, ERROR_MESSAGE);
	}

	/*
	 * Construit un site html pour une suiteId
	 */
	private ActionResponse doActionForSuiteId(int suiteId, HttpServletRequest req) throws IOException {

		String errorMessage = "Wrong params!!";
		try {

			String url = null;

			// from session
			url = this.findUrlFromSession(this.getSession(req), PARAM_REQUEST_SUITE_ID, suiteId);

			if (url == null) {
				// on contruit les pages html
				VoSingleReportResponse voResponse = ShowHtmlManager.get().showHtmlSingleReport(suiteId,
						this.getSession(req).getId(), this.getHttpAddress(req));
				// errorMessage = voResponse.getComment();

				// on construit l'url
				url = voResponse.getUrl();
			}

			if (url == null) {
				return new ActionResponse(false, errorMessage);
			}

			// on memorise les info sur la session pour la prochaine requete
			// identique
			this.saveUrlInSession(this.getSession(req), PARAM_REQUEST_SUITE_ID, suiteId, url);

			// on redirige vers cette url
			return new ActionResponse(url);

		} catch (JUnitHistoryException ex) {
			log.severe("Error in doActionForSuiteId(): " + ex.toString());
		}

		return new ActionResponse(false, errorMessage);
	}

	/*
	 * Recupère une url déjà stockée en session
	 * - meme param
	 * - meme id
	 */
	private String findUrlFromSession(HttpSession session, String param, int id) {

		boolean forceRefresh = SessionHelper.getBooleanAttribute(session, PARAM_REQUEST_FORCE_REFRESH, false);
		if (!forceRefresh) {
			String paramInSession = SessionHelper.getStringAttribute(session, KEY_SESSION_PARAM, null);

			if (paramInSession != null && paramInSession.equals(param)) {
				int idInSession = SessionHelper.getIntAttribute(session, KEY_SESSION_ITEM_ID, IVo.ID_UNDEFINED);
				if (idInSession == id) {
					// on recupère une url recente pour le meme id
					return SessionHelper.getStringAttribute(session, KEY_SESSION_URL, null);
				}
			}
		}
		return null;
	}

	private void saveUrlInSession(HttpSession session, String param, int id, String url) {

		session.setAttribute(KEY_SESSION_PARAM, param);
		session.setAttribute(KEY_SESSION_ITEM_ID, id);
		session.setAttribute(KEY_SESSION_URL, url);

	}

	// ============================================== INNER CLASS
	private static final class ActionResponse {

		private final boolean success;
		private String errorMessage;
		private String url;

		private ActionResponse(boolean success, String errorMessage) {
			this.success = success;
			this.errorMessage = errorMessage;
		}

		private ActionResponse(String url) {
			this.success = url != null;
			this.url = url;
		}
	}

}
