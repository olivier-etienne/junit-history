package com.francetelecom.orangetv.junithistory.server.manager;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.http.HttpSession;

import com.francetelecom.orangetv.junithistory.server.dao.DaoTestSuiteGroup;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.dao.IDbEntry;
import com.francetelecom.orangetv.junithistory.server.dto.DtoHtmlPage;
import com.francetelecom.orangetv.junithistory.server.dto.DtoListHtmlPages;
import com.francetelecom.orangetv.junithistory.server.dto.DtoTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteGroup;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.util.FileUtils;
import com.francetelecom.orangetv.junithistory.server.util.ListLines;
import com.francetelecom.orangetv.junithistory.server.util.SessionHelper;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoListReportResponse;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSingleReportData;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSingleReportResponse;

/**
 * Creation de page html
 * 
 * @author ndmz2720
 *
 */
public class ShowHtmlManager implements IManager {

	private static final Logger log = Logger.getLogger(ShowHtmlManager.class.getName());

	// -------------------------- singleton
	private static ShowHtmlManager instance;

	public static ShowHtmlManager get() {
		if (instance == null) {
			instance = new ShowHtmlManager();
		}
		return instance;
	}

	private ShowHtmlManager() {
	}

	// ----------------------------------------

	public VoListReportResponse showHtmlListReportForGroup(int groupId, String sessionId, HttpAddress httpAddress)
			throws JUnitHistoryException {

		log.config("showHtmlListReportForGroup() - groupId: " + groupId);
		if (groupId == IDbEntry.ID_UNDEFINED) {
			throw new JUnitHistoryException("Group id is undefined!");
		}

		DbTestSuiteGroup group = DaoTestSuiteGroup.get().getById(groupId, true);

		if (group == null) {
			throw new JUnitHistoryException("Group " + groupId + " is undefined!");
		}

		// chemin relatif /historic/<idsession>/html
		String relatifHtmlPath = PathManager.get().getHistoryHtmlNameForSession(sessionId);
		// chemin relatif des log
		String relatifPathForLog = PathManager.get().getHistoryRelativePathFromHtmlToLog();

		// liste des suites d'un group par ordre decroissant (les + recents en
		// premier)
		final List<DbTestSuiteInstance> listSuites = DaoTestSuiteInstance.get().listSuitesByGroup(group.getId());

		if (listSuites != null && !listSuites.isEmpty()) {
			// liste des DtoSuite
			final List<DtoTestSuiteInstance> listDtoDatas = new ArrayList<>(listSuites.size());
			for (DbTestSuiteInstance suite : listSuites) {
				listDtoDatas.add(DaoManager.get().loadTestSuite(suite));
			}

			URL url = this.buildURLForListTestSuite(group.getName(), listDtoDatas, relatifHtmlPath, relatifPathForLog,
					httpAddress);

			return new VoListReportResponse(((url == null) ? null : url.toExternalForm()), null);
		}

		return new VoListReportResponse(null, "No suite for this group!");

	}

	public VoSingleReportResponse showHtmlSingleReport(int suiteId, String sessionId, HttpAddress httpAddress)
			throws JUnitHistoryException {

		log.config("showHtmlSingleReport() - suiteId: " + suiteId);
		if (suiteId == IDbEntry.ID_UNDEFINED) {
			throw new JUnitHistoryException("Suite id is undefined!");
		}

		DtoTestSuiteInstance dtoTestSuiteInstance = DaoManager.get().loadTestSuite(suiteId);
		if (dtoTestSuiteInstance == null) {
			throw new JUnitHistoryException("Suite " + suiteId + " is undefined!");
		}

		// chemin relatif /historic/<idsession>/html
		String relatifHtmlPath = PathManager.get().getHistoryHtmlNameForSession(sessionId);
		// chemin relatif des log
		String relatifPathForLog = PathManager.get().getHistoryRelativePathFromHtmlToLog();
		URL url = this.buildURLForTestSuite(dtoTestSuiteInstance, relatifHtmlPath, relatifPathForLog, httpAddress);

		VoSingleReportResponse response = new VoSingleReportResponse();
		response.setUrl(((url == null) ? null : url.toExternalForm()));
		return response;
	}

	public VoSingleReportResponse showHtmlSingleReport(VoSingleReportData singleReportDatas, HttpSession session,
			HttpAddress httpAddress) throws JUnitHistoryException {

		log.config("showHtmlSingleReport()");
		if (!SessionHelper.getBooleanAttribute(session, KEY_SESSION_EVENT_UPLOAD_ENDED, false)) {
			throw new JUnitHistoryException("Xml files must have been uploaded before building report!");
		}
		DtoTestSuiteInstance dtoDatas = this.buildDtoSuiteInstanceFromUploadPath(singleReportDatas, session);

		// chemin relatif /upload/<idSession>/html pour la construction de l'url
		String relatifHtmlPath = PathManager.get().getUploadHtmlNameForSession(session.getId());
		// chemin relatif html to log
		String relatifPathForLog = PathManager.get().getUploadRelativePathFromHtmlToLog();
		URL url = this.buildURLForTestSuite(dtoDatas, relatifHtmlPath, relatifPathForLog, httpAddress);

		VoSingleReportResponse response = new VoSingleReportResponse();

		DbTestSuiteGroup group = dtoDatas.getTestSuiteInstance().getTestSuiteGroup();
		response.setUrl(((url == null) ? null : url.toExternalForm()));
		response.setGroupId(group == null ? IVo.ID_UNDEFINED : group.getId());
		response.setSuiteName(dtoDatas.getTestSuiteInstance().getName());

		response.setVersion(ReportManager.get().getVersionFromDtoTestSuiteInstance(dtoDatas));
		return response;
	}

	public DtoTestSuiteInstance buildDtoSuiteInstanceFromUploadPath(VoSingleReportData singleReportDatas,
			HttpSession session) throws JUnitHistoryException {

		log.config("buildDtoSuiteInstanceFromXmlReport()");
		if (!SessionHelper.getBooleanAttribute(session, KEY_SESSION_EVENT_UPLOAD_ENDED, false)) {
			throw new JUnitHistoryException("Xml files must have been uploaded before building report!");
		}

		String uploadPath = SessionHelper.getStringAttribute(session, KEY_SESSION_GWTUPLOADED_DIR, null);
		return ReportManager.get().buildDtoSuiteInstanceFromUploadPath(singleReportDatas, uploadPath);

	}

	// ------------------------------------------------ private methods

	/*
	 * 
	 * @param dtoDatas
	 * @param htmlPath: chemin relatif pour l'enregistrement du fichier html et css
	 * @return
	 */
	private URL buildURLForTestSuite(DtoTestSuiteInstance dtoDatas, String relatifHtmlPath, String relatifPathForLog,
			HttpAddress httpAddress) throws JUnitHistoryException {

		// on cree la page html
		final DtoHtmlPage htmlPage = HtmlBuilderManager.get().buildHtmPageFromTestSuite(dtoDatas, relatifPathForLog);
		return this.writeHtmlPage(htmlPage, relatifHtmlPath, relatifPathForLog, true, httpAddress);

	}

	/*
	 * Cree toutes les pages html pour une synthese d'une liste de suite (group)
	 * et retourne l'url de la page principale
	 */
	private URL buildURLForListTestSuite(String groupName, List<DtoTestSuiteInstance> listDtoDatas,
			String relatifHtmlPath, String relatifPathForLog, HttpAddress httpAddress) throws JUnitHistoryException {

		DtoListHtmlPages listHtmlPages = HtmlBuilderManager.get().buildHtmlPageForListTestSuite(groupName,
				listDtoDatas, relatifPathForLog);

		// page principale
		URL url = this
				.writeHtmlPage(listHtmlPages.getMainPage(), relatifHtmlPath, relatifPathForLog, true, httpAddress);

		// on enregistre ensuite toutes les autres pages HTML
		for (DtoHtmlPage htmlPage : listHtmlPages.getListHtmlPages()) {
			this.writeHtmlPage(htmlPage, relatifHtmlPath, relatifPathForLog, false, httpAddress);
		}

		return url;

	}

	private URL writeHtmlPage(DtoHtmlPage htmlPage, String relatifHtmlPath, String relatifPathForLog,
			boolean copyResources, HttpAddress httpAddress) throws JUnitHistoryException {

		final ListLines htmlLines = htmlPage.getListLines();

		String htmlPath = PathManager.get().getContextCompletePathname(relatifHtmlPath);

		// on l'enregistre dans le repertoire htmlPath
		File htmlFile = new File(htmlPath, htmlPage.getPageName());
		log.config("write html file: " + htmlFile.getAbsolutePath());
		FileUtils.writeFile(htmlFile, htmlLines);

		if (copyResources) {

			// on y enregistre également le fichier style.css
			this.copyResource(HtmlBuilderManager.RESOURCE_STYLE_CSS, htmlPath);

			// et les deux images arrow-up && arrow-down
			this.copyResource(HtmlBuilderManager.RESOURCE_ARROW_DOWN, htmlPath);
			this.copyResource(HtmlBuilderManager.RESOURCE_ARROW_UP, htmlPath);

		}

		return (htmlFile == null) ? null : PathManager.get().buildUrl(httpAddress,
				relatifHtmlPath + "/" + htmlFile.getName());

	}

	private void copyResource(String resourceName, String htmlPath) throws JUnitHistoryException {
		InputStream in = HtmlBuilderManager.get().getResourceInputStream(resourceName);
		FileUtils.copyInputStream(in, new File(htmlPath, resourceName));
	}

	// ============================================= INNER CLASS
	public static final class HttpAddress {

		private final int port;
		private final String address;

		public HttpAddress(int port, String address) {
			this.port = port;
			this.address = address;
		}

		public int getPort() {
			return port;
		}

		public String getAddress() {
			return address;
		}

	}

}
