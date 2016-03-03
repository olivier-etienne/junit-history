package com.francetelecom.orangetv.junithistory.server.manager;

import java.io.File;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

import com.francetelecom.orangetv.junithistory.server.util.FileUtils;

public class SessionManager implements HttpSessionListener {

	private static final Logger log = Logger.getLogger(SessionManager.class.getName());

	// -------------------------- implementing HttpSessionListener
	@Override
	public void sessionCreated(HttpSessionEvent event) {
		final String sessionId = event.getSession().getId();
		log.info("Session created: " + sessionId);
		SessionSubscription.get().sessionCreated(sessionId);
	}

	@Override
	public void sessionDestroyed(HttpSessionEvent event) {
		final String sessionId = event.getSession().getId();
		log.info("Session destroyed: " + sessionId);

		SessionSubscription.get().sessionDestroyed(sessionId);
		this.deleteDirectoryAndContent(sessionId);
	}

	// --------------------------------------- private methods

	/**
	 * Supprime les répertoires liés à la session et leur contenu
	 * - upload/session
	 * -historic/html/session
	 * 
	 * @param sessionId
	 */
	private void deleteDirectoryAndContent(String sessionId) {

		// supprimer le contenu de /upload/<idSession> si existe
		String completeUploadPath = PathManager.get().getUploadPathnameForSession(sessionId);
		log.config("deleteDirectoryAndAllContent(): " + completeUploadPath);

		File uploadDir = new File(completeUploadPath);
		FileUtils.deleteDirectoryAndAllContent(uploadDir);

		// supprimer le contenu de /historic/html/<idsession> si existe
		String relatifHistoricHtmlPath = PathManager.get().getHistoryHtmlNameForSession(sessionId);
		log.config("deleteDirectoryAndAllContent(): " + relatifHistoricHtmlPath);
		FileUtils.deleteDirectoryAndAllContent(new File(PathManager.get().getContextCompletePathname(
				relatifHistoricHtmlPath)));
	}

	// ======================================== INNER CLASS
	public static class SessionSubscription implements IJUnitHistorySessionListener {

		// ----------------------------- singleon
		private static SessionSubscription instance;

		public static SessionSubscription get() {
			if (instance == null) {
				instance = new SessionSubscription();
			}
			return instance;
		}

		private SessionSubscription() {
		}

		// --------------------------------------

		private final Set<IJUnitHistorySessionListener> sessionListeners = new HashSet<>();

		// --------------------------------------- public method
		public void addSessionListener(IJUnitHistorySessionListener listener) {
			this.sessionListeners.add(listener);
		}

		public void removeSessionListener(IJUnitHistorySessionListener listener) {
			this.sessionListeners.remove(listener);
		}

		// ------------------- implementing JunitHistorySessionListener
		@Override
		public void sessionCreated(String sessionId) {

			for (IJUnitHistorySessionListener listener : sessionListeners) {
				listener.sessionCreated(sessionId);
			}

		}

		@Override
		public void sessionDestroyed(String sessionId) {
			for (IJUnitHistorySessionListener listener : sessionListeners) {
				listener.sessionDestroyed(sessionId);
			}
		}

	}

	public interface IJUnitHistorySessionListener {

		public void sessionCreated(String sessionId);

		public void sessionDestroyed(String sessionId);
	}

}
