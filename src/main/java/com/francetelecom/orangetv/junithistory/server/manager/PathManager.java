package com.francetelecom.orangetv.junithistory.server.manager;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Logger;

import javax.servlet.ServletContext;

import com.francetelecom.orangetv.junithistory.server.manager.ShowHtmlManager.HttpAddress;
import com.francetelecom.orangetv.junithistory.server.service.IMyServices;
import com.francetelecom.orangetv.junithistory.server.util.FileUtils;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;

/**
 * Regroupe toute la gestion des fichiers et repertoires de l'application
 * 
 * @author ndmz2720
 *
 */
public class PathManager implements IMyServices {

	private static final Logger log = Logger.getLogger(PathManager.class.getName());

	private static final String UPLOAD_PATH = "upload";
	private static final String HISTORY_PATH = "history";
	private static final String HTML_PATH = "html";
	private static final String XML_PATH = "xml";
	private static final String SLASH = "/";

	private static PathManager instance;

	public static PathManager get() {
		if (instance == null) {
			instance = new PathManager();
		}
		return instance;
	}

	private PathManager() {
	}

	// ---------------------------- accessor

	private boolean initDone = false;
	private String contextRootPathname;
	private String contextName;

	private boolean testMode;

	// chemin de sauvegarde des fichiers xml & log & txt uploades
	private String historyBackupPath;

	// ----------------------------------- package methods
	void init(ServletContext servletContext) {

		if (!initDone) {

			String test = servletContext.getInitParameter(KEY_CONTEXT_TEST_MODE);
			this.testMode = (test == null) ? false : Boolean.parseBoolean(test);

			historyBackupPath = servletContext.getInitParameter(KEY_HISTORY_BACKUP_PATH);

			this.contextRootPathname = servletContext.getRealPath("");
			log.config("Context rootPath: " + contextRootPathname);
			String contextName = servletContext.getContextPath();
			log.info("Context path: " + contextName);
			this.contextName = ValueHelper.isStringEmptyOrNull(contextName) ? "" : "/" + contextName;
			this.initDone = true;
		}
	}

	void unInit() {
		this.initDone = false;
	}

	// ---------------------------------------- public methods

	public File buildLogFile(File repository, String rootName) {
		return new File(repository, rootName + ".log");
	}

	// chemin relatif pour l'upload des fichiers xml
	//
	// /upload/<idSession>/xml
	public String getUploadXmlNameForSession(String sessionId) {

		return UPLOAD_PATH + SLASH + sessionId + SLASH + XML_PATH;
	}

	// chemin complet
	// <path>/upload/<idSession>/xml
	public String getUploadPathnameForSession(String sessionId) {
		return getContextCompletePathname(UPLOAD_PATH + SLASH + sessionId);
	}

	public String getHistoryName() {
		return HISTORY_PATH;
	}

	public String getHistoryXmllName() {
		return HISTORY_PATH + SLASH + XML_PATH;
	}

	public String getHistoryHtmlName() {
		return HISTORY_PATH + SLASH + HTML_PATH;
	}

	// chemin relatif pour le site html de session
	// dans le cadre de l'historique
	//
	// /history/html/<idSession>
	public String getHistoryHtmlNameForSession(String sessionId) {
		return this.getHistoryHtmlName() + SLASH + sessionId;
	}

	// chemin relatif pour le site html de session
	// dans le cadre d'un upload

	// /upload/<idSession/html
	public String getUploadHtmlNameForSession(String sessionId) {

		return UPLOAD_PATH + SLASH + sessionId + SLASH + HTML_PATH;
	}

	/**
	 * URL pour l'access à une resource html depuis un navigateur
	 * 
	 * @param request
	 * @param relativePath
	 * @return
	 * @throws JUnitHistoryException
	 */
	public URL buildUrl(HttpAddress httpAddress, String relativePath) throws JUnitHistoryException {

		try {
			relativePath = this.contextName + "/" + relativePath;
			return new URL("http", httpAddress.getAddress(), httpAddress.getPort(), relativePath);

		} catch (MalformedURLException e) {
			throw new JUnitHistoryException("Error in building url! " + e.getMessage());
		}

	}

	// ../xml/
	public String getUploadRelativePathFromHtmlToLog() {
		return ".." + SLASH + XML_PATH + SLASH;
	}

	// ../../xml/
	public String getHistoryRelativePathFromHtmlToLog() {
		return ".." + SLASH + ".." + SLASH + XML_PATH + SLASH;
	}

	public String getUploadXmlPathnameForSession(String sessionId) {

		return this.getContextCompletePathname(this.getUploadXmlNameForSession(sessionId));
	}

	/*
	 * Chemin complet par rapport au context application
	 */
	public String getContextCompletePathname(String relativePathname) {
		return this.getContextCompletePathname(relativePathname, true);
	}

	public String getContextCompletePathname(String relativePathname, boolean forcecreate) {
		try {
			log.config("getCompletePathname(): " + relativePathname);
			File dir = new File(this.contextRootPathname, relativePathname);
			FileUtils.verifyDirectory(dir, forcecreate);

			return dir.getCanonicalPath();

		} catch (IOException e) {
			log.severe(e.toString());
		}
		return null;

	}

	/*
	 * Chemin complet pour la sauvegarde les fichiers uplodes
	 */
	public File getBackupDir() {

		return this.historyBackupPath == null ? null : new File(this.historyBackupPath);

	}

}
