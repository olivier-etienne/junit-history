package com.francetelecom.orangetv.junithistory.server.tools.junit;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.server.dao.DaoTestClassCategory;
import com.francetelecom.orangetv.junithistory.server.model.DbTestClassCategory;
import com.francetelecom.orangetv.junithistory.server.tools.junit.JUnitStatus.JUnitTestCaseStatus;
import com.francetelecom.orangetv.junithistory.server.util.TestStatistics;
import com.francetelecom.orangetv.junithistory.shared.TestSubStatusEnum;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;

public class JUnitStatistics {

	private static final Logger log = Logger.getLogger(JUnitStatistics.class.getName());

	private static final String SUITE_PATH_ROOT = "com.francetelecom.orangetv.gwt.stb.test.";
	private static final int SUITE_PATH_ROOT_SIZE = SUITE_PATH_ROOT.length();

	private static Map<String, DbTestClassCategory> mapTestNameToStatsCategory = new HashMap<>();

	/**
	 * Requete hors cadre transactionnel
	 * 
	 * @param testClassName
	 * @return
	 * @throws JUnitHistoryException
	 */
	public static DbTestClassCategory getStatCategory(String testClassName) throws JUnitHistoryException {
		return getStatCategory(testClassName, null);
	}

	public static void resetMap() {
		mapTestNameToStatsCategory.clear();
	}

	/**
	 * 
	 * @param testClassName
	 * @param listCategories
	 *            pour un cadre transactionnel
	 * @return
	 * @throws JUnitHistoryException
	 */
	public static DbTestClassCategory getStatCategory(String testClassName, List<DbTestClassCategory> listCategories)
			throws JUnitHistoryException {

		if (testClassName == null || testClassName.length() <= SUITE_PATH_ROOT_SIZE) {
			return null;
		}

		// chercher dans la map
		DbTestClassCategory statsCategory = mapTestNameToStatsCategory.get(testClassName);
		if (statsCategory != null) {
			return statsCategory;
		}

		// on cherche la category a partir du nom du test
		String name = testClassName.substring(SUITE_PATH_ROOT_SIZE);

		listCategories = listCategories == null ? DaoTestClassCategory.get().listCategories(true) : listCategories;
		for (DbTestClassCategory category : listCategories) {
			if (category.isDefaultValue()) {
				continue;
			}

			for (String root : category.getSuiteNames()) {
				if (name.startsWith(root)) {
					mapTestNameToStatsCategory.put(testClassName, category);
					return category;
				}
			}
		}
		// rien trouvé - category par defaut
		DbTestClassCategory defaultCategory = DaoTestClassCategory.get().getDefaultCategory(true);
		mapTestNameToStatsCategory.put(testClassName, defaultCategory);
		return defaultCategory;
	}

	public static abstract class AbstractCompteur {

		public abstract int getRunning();

		public abstract int getRunningSuccess();

		public abstract int getRunningFailure();

		public abstract int getRunningError();

		public abstract int getRunningErrorCrash();

		public abstract int getRunningErrorTimeout();

		public abstract int getRunningErrorException();

		public abstract int getSkipped();

		public abstract int getSkippedDependency();

		public abstract int getSkippedProgrammaticaly();

		public int getTotal() {
			return this.getRunning() + this.getSkipped();
		}

		public int getPercentTotal(int maxTot) {
			return this.percent(this.getTotal(), maxTot);
		}

		public int getPercentRunning() {
			int running = this.getRunning();
			return this.percent(running, running + this.getSkipped());
		}

		public int getPercentRunningSuccess() {

			return this.percent(this.getRunningSuccess(), this.getRunning());
		}

		public int getPercentRunningFailure() {
			return this.percent(this.getRunningFailure(), this.getRunning());
		}

		public int getPercentRunningError() {
			return this.percent(this.getRunningError(), this.getRunning());
		}

		/*
		 * ne jamais renvoyer 0 si value > 0
		 */
		private int percent(int value, int radical) {
			if (radical <= 0) {
				return 0;
			}

			int round = Math.round(value * 100 / radical);
			return (round == 0 && value > 0) ? 1 : round;
		}

		public String getDetailTotal() {
			return "TOTAL - running: " + this.getRunning() + " skipped: " + this.getSkipped();
		}

		public String getDetailRunning() {
			return "RUNNING - success: " + this.getRunningSuccess() + " failure: " + this.getRunningFailure()
					+ " error: " + this.getRunningError();
		}

		public String getDetailSuccess() {
			return ("SUCCESS: " + this.getRunningSuccess());
		}

		public String getDetailFailure() {
			return ("FAILURE: " + this.getRunningFailure());
		}

		public String getDetailSkipped() {
			return "SKIPPED - dependency: " + this.getSkippedDependency() + " execution: "
					+ this.getSkippedProgrammaticaly();
		}

		public String getDetailError() {
			return ("ERROR - exception: " + this.getRunningErrorException() + " timeout: "
					+ this.getRunningErrorTimeout() + " crash: " + this.getRunningErrorCrash());
		}

	}

	public static class CategoryCompteur extends AbstractCompteur {

		int running = 0;
		int running_success = 0;
		int running_failure = 0;

		int running_error = 0;
		int running_error_crash = 0;
		int running_error_timeout = 0;
		int running_error_exception = 0;

		int skipped = 0;
		int skipped_dependency = 0;
		int skipped_programmaticaly = 0;

		private boolean up = true;

		void setUp(boolean up) {
			this.up = up;
		}

		boolean getUp() {
			return up;
		}

		final DbTestClassCategory statsCategory;

		CategoryCompteur(DbTestClassCategory statsCategory) {
			this.statsCategory = statsCategory;
		}

		public TestStatistics toTestStatistics() {

			TestStatistics testStatistics = new TestStatistics();
			testStatistics.setRunning(this.getRunning());
			testStatistics.setRunningError(this.getRunningError());
			testStatistics.setRunningErrorCrash(this.getRunningErrorCrash());
			testStatistics.setRunningErrorException(this.getRunningErrorException());
			testStatistics.setRunningErrorTimeout(this.getRunningErrorTimeout());
			testStatistics.setRunningFailure(this.getRunningFailure());
			testStatistics.setRunningSuccess(this.getRunningSuccess());
			testStatistics.setSkipped(this.getSkipped());
			testStatistics.setSkippedDependency(this.getSkippedDependency());
			testStatistics.setSkippedProgrammaticaly(this.getSkippedProgrammaticaly());

			return testStatistics;
		}

		@SuppressWarnings("incomplete-switch")
		void addStatus(JUnitTestCaseStatus testStatus) {

			if (testStatus == null) {
				return;
			}

			if (testStatus.isSkipped()) {

				skipped++;
				if (testStatus.subStatus == TestSubStatusEnum.dependency) {
					skipped_dependency++;
				} else {
					skipped_programmaticaly++;
				}
				return;

			}

			running++;
			if (testStatus.isSuccess()) {
				running_success++;
			} else if (testStatus.isFailure()) {
				running_failure++;
			} else if (testStatus.isError()) {
				running_error++;
				switch (testStatus.subStatus) {
				case crash:
					running_error_crash++;
					break;
				case timeout:
					running_error_timeout++;
					break;

				case error:
					running_error_exception++;
					break;

				}
			}

		}

		// ---------------------------- overriding AbstractCompteur

		@Override
		public int getRunning() {
			return this.running;
		}

		@Override
		public int getRunningSuccess() {
			return this.running_success;
		}

		@Override
		public int getRunningFailure() {
			return this.running_failure;
		}

		@Override
		public int getRunningError() {
			return this.running_error;
		}

		@Override
		public int getRunningErrorCrash() {
			return this.running_error_crash;
		}

		@Override
		public int getRunningErrorTimeout() {
			return this.running_error_timeout;
		}

		@Override
		public int getRunningErrorException() {
			return this.running_error_exception;
		}

		@Override
		public int getSkipped() {
			return this.skipped;
		}

		@Override
		public int getSkippedDependency() {
			return this.skipped_dependency;
		}

		@Override
		public int getSkippedProgrammaticaly() {
			return this.skipped_programmaticaly;
		}

	}

	public static class StatisticCompteur extends CategoryCompteur {

		// map [ClassCategoryEnum - CategoryCompteur]
		private Map<DbTestClassCategory, CategoryCompteur> mapCategoryCompteur = null;

		public StatisticCompteur() {
			super(null);
		}

		public Map<DbTestClassCategory, CategoryCompteur> getCategory2Compteur() {
			return this.mapCategoryCompteur;
		}

		public boolean hasCategoryCompteurs() {
			return !this.mapCategoryCompteur.isEmpty();
		}

		CategoryCompteur getCategoryCompteur(DbTestClassCategory statsCategory) {
			if (this.mapCategoryCompteur != null && !this.mapCategoryCompteur.isEmpty()) {
				return this.mapCategoryCompteur.get(statsCategory);
			}
			return null;
		}

		public void addStatus(JUnitTestCaseStatus testStatus, DbTestClassCategory statsCategory) {

			if (testStatus == null) {
				return;
			}

			// compteur générique
			super.addStatus(testStatus);
			if (statsCategory == null) {
				return;
			}

			if (this.mapCategoryCompteur == null) {
				this.mapCategoryCompteur = new HashMap<DbTestClassCategory, JUnitStatistics.CategoryCompteur>(10);
			}

			// si category alors aussi compteur specifique
			CategoryCompteur categoryCompteur = this.mapCategoryCompteur.get(statsCategory);
			if (categoryCompteur == null) {
				categoryCompteur = new CategoryCompteur(statsCategory);
				this.mapCategoryCompteur.put(statsCategory, categoryCompteur);
			}
			categoryCompteur.addStatus(testStatus);

		}

	}

}
