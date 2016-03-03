package com.francetelecom.orangetv.junithistory.server.manager;

import java.sql.SQLException;
import java.util.logging.Logger;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import com.francetelecom.orangetv.junithistory.server.manager.ProfileManager.ProfilCredential;

/**
 * Manage load and unload of context
 * 
 * @author ndmz2720
 *
 */
public class ContextManager implements ServletContextListener {

	private static final Logger log = Logger.getLogger(ContextManager.class.getName());

	private static final String KEY_JDBC_DRIVER = "jdbcDriver";
	private static final String KEY_DB_URL = "bddUrl";
	private static final String KEY_DB_USER = "bddUser";
	private static final String KEY_DB_PWD = "bddPwd";

	private static final String KEY_LOGIN_MANAGER = "login_manager";
	private static final String KEY_PWD_MANAGER = "pwd_manager";

	private static final String KEY_LOGIN_ADMIN = "login_admin";
	private static final String KEY_PWD_ADMIN = "pwd_admin";

	@Override
	public void contextDestroyed(ServletContextEvent event) {

		log.info("===========================================");
		log.info(" ..........CONTEXT JUnitHistory DESTROYED");
		log.info("===========================================");

		PathManager.get().unInit();
		try {
			DatabaseManager.get().unregister();
		} catch (SQLException e) {
			log.severe("Database.unregister in error: " + e.getMessage());
		}
	}

	@Override
	public void contextInitialized(ServletContextEvent event) {

		log.info("===========================================");
		log.info(" CONTEXT JUnitHistory INITIALIZED......");
		log.info("===========================================");

		ServletContext servletContext = event.getServletContext();

		// ============================
		// CREDENTIALS
		// ============================
		ProfileManager.get().setAdminCredential(
				new ProfilCredential(servletContext.getInitParameter(KEY_LOGIN_ADMIN), servletContext
						.getInitParameter(KEY_PWD_ADMIN)));
		ProfileManager.get().setQualifCredential(
				new ProfilCredential(servletContext.getInitParameter(KEY_LOGIN_MANAGER), servletContext
						.getInitParameter(KEY_PWD_MANAGER)));

		// ============================
		// BDD
		// ============================
		String jdbcDriver = servletContext.getInitParameter(KEY_JDBC_DRIVER);
		String dbUrl = servletContext.getInitParameter(KEY_DB_URL);
		String dbUser = servletContext.getInitParameter(KEY_DB_USER);
		String dbPwd = servletContext.getInitParameter(KEY_DB_PWD);

		log.info("init(): " + jdbcDriver + " - " + dbUrl);
		if (jdbcDriver != null) {
			DatabaseManager.get().init(jdbcDriver, dbUrl, dbUser, dbPwd);
		}

		// ============================
		// DIVERS
		// ============================
		PathManager.get().init(servletContext);
	}

}
