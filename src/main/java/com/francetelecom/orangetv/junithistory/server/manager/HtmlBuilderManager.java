package com.francetelecom.orangetv.junithistory.server.manager;

import java.io.File;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.server.dao.DaoTestClassCategory;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestMessage;
import com.francetelecom.orangetv.junithistory.server.dto.DtoHtmlPage;
import com.francetelecom.orangetv.junithistory.server.dto.DtoListHtmlPages;
import com.francetelecom.orangetv.junithistory.server.dto.DtoTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbStatsCategoryInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestClass;
import com.francetelecom.orangetv.junithistory.server.model.DbTestClassCategory;
import com.francetelecom.orangetv.junithistory.server.model.DbTestInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestMessage;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteGroup;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.TestStatusEnum;
import com.francetelecom.orangetv.junithistory.server.model.TestSubStatusEnum;
import com.francetelecom.orangetv.junithistory.server.util.HtmlUtils;
import com.francetelecom.orangetv.junithistory.server.util.ListLines;
import com.francetelecom.orangetv.junithistory.server.util.TestStatistics;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;

/**
 * Manager pour la construction de page Html à partir du model
 * 
 * @author ndmz2720
 *
 */
public class HtmlBuilderManager implements IHtmlBalise, IManager {

	private static final Logger log = Logger.getLogger(HtmlBuilderManager.class.getName());

	public static final String RESOURCE_STYLE_CSS = "style.css";
	public static final String RESOURCE_ARROW_DOWN = "arrow-down.png";
	public static final String RESOURCE_ARROW_UP = "arrow-up.png";

	// private String logDirPathname;
	// private String relativePathFromHtmlToLog;

	private boolean hideSkipped = true;

	// ---------------------------------- Singleton
	private static HtmlBuilderManager instance;

	public static final HtmlBuilderManager get() {
		if (instance == null) {
			instance = new HtmlBuilderManager();
		}
		return instance;
	}

	private HtmlBuilderManager() {
	}

	// ---------------------------------------- public methods

	/**
	 * Recupere la resource style.css
	 * 
	 * @return
	 */
	public InputStream getResourceInputStream(String resourceName) {

		return this.getClass().getResourceAsStream(resourceName);
	}

	/*
	* Page OVERVIEW
	* Une suiteName par ex [PLR2-Sagem IPTV Kit] Test Suite
	* reunit toutes les TestSuite portant ce nom.
	* On cree une page html pour chaque suiteName
	* (convient aussi pour la page des dernieres suites de chaque testsuite et page failure)
	*/
	public DtoListHtmlPages buildHtmlPageForListTestSuite(String groupName,
			List<DtoTestSuiteInstance> listDtoTestSuites, String relatifPathForLog) throws JUnitHistoryException {

		// page principale
		DtoHtmlPage mainPage = this.createDtoHtmlPage(groupName);

		// liste des rapports individuels
		final List<DtoHtmlPage> listHtmlPageSuite = this.buildListDtoHtmlPages(listDtoTestSuites, relatifPathForLog);

		final DtoHtmlPage htmlPageStats = this.buildHtmlPageStatsForListSuite(groupName, mainPage, listDtoTestSuites);

		ListLines listLines = new ListLines();

		listLines.addLine(HEAD_BEGIN);
		listLines.addLine(TITLE_BEGIN, "Test Suites History", TITLE_END);
		listLines.addLine("<link rel='stylesheet' type='text/css' href='style.css'>");
		listLines.addLine(HEAD_END);

		listLines.addLine(BODY_BEGIN);
		listLines.addLine(H1_BEGIN, "Test Suites History", H1_END);

		listLines.addLine(this.format(MF_BALISE_CLASS_BEGIN, DIV, CLASS_SUITE));
		listLines.addLine(H2_BEGIN, groupName, H2_END);

		// lien sur la page de statistique
		if (htmlPageStats != null) {
			listLines.addLine(BR);
			listLines.addLine(this.format(MF_BALISE_CLASS_BEGIN, P, CLASS_LOGS));
			listLines
					.addLine(this.format(MF_LINK_HREF, htmlPageStats.getPageName()), "Voir les statistiques", LINK_END);
			listLines.addLine(P_END, BR);
		}

		// list des noms des category avec hyperlien
		List<DbTestClassCategory> listCategories = DaoTestClassCategory.get().listCategories(true);
		listLines.addLine(H2_BEGIN, "Tests by category", H2_END);
		ListLines htmlTableCategories = new ListLines();
		htmlTableCategories.addLine(this.format(MF_BALISE_CLASS_BEGIN, TABLE, CLASS_STATS), TR_BEGIN);
		for (DbTestClassCategory category : listCategories) {

			// nom de la category de test
			htmlTableCategories.addLine(this.format(MF_BALISE_WIDTH_CLASS_TITLE_BEGIN, TH, "150px", CLASS_CAT,
					category.getDescription()));
			htmlTableCategories.addLine(this.format(MF_LINK_CLASS_HREF, CLASS_CAT, "#" + category.getName()));
			htmlTableCategories.addLine(category.getName());
			htmlTableCategories.addLine(LINK_END, TH_END);
		}
		htmlTableCategories.addLine(TR_END, TABLE_END, BR, BR);
		listLines.addLines(htmlTableCategories);

		//
		// ligne de titre de table de category
		ListLines htmlTitles = new ListLines();
		htmlTitles.addLine(TR_BEGIN, this.format(MF_BALISE_WIDTH_BEGIN, TH, "200px"), "Class", TH_END,
				this.format(MF_BALISE_WIDTH_BEGIN, TH, "300px"), "Name", TH_END);
		for (DtoHtmlPage htmlSuite : listHtmlPageSuite) {
			htmlTitles.addLine(this.format(MF_BALISE_CLASS_BEGIN, TH, CLASS_LIST),
					this.buildHtmlLinkForHtmlPage(htmlSuite, true), LINK_END, TH_END);
		}
		htmlTitles.addLine(TR_END);

		// lignes des tests par categorie
		listLines.addLines(this.buildHtmlTableTestListSuite(listDtoTestSuites, htmlTitles));

		listLines.addLine(DIV_END);
		listLines.addLine(BODY_END);

		mainPage.setListLines(listLines);

		DtoListHtmlPages dtoListHtmlPages = new DtoListHtmlPages(mainPage);
		dtoListHtmlPages.setListHtmlPages(listHtmlPageSuite);
		dtoListHtmlPages.getListHtmlPages().add(htmlPageStats);
		return dtoListHtmlPages;

	}

	/**
	 * Page présentant un rapport d'une suite de tests particulière <br>
	 * ex page pour FrR4-ss-6.51.33-B
	 * <ul>
	 * <li>statistiques par category
	 * <li>liste des tests par category </u>
	 */
	public DtoHtmlPage buildHtmPageFromTestSuite(DtoTestSuiteInstance dtoTestSuiteInstance, String relatifPathForLog)
			throws JUnitHistoryException {

		if (dtoTestSuiteInstance == null || dtoTestSuiteInstance.getTestSuiteInstance() == null) {
			throw new JUnitHistoryException("Invalid DtoTestSuiteInstance!");
		}

		final DbTestSuiteInstance testSuiteInstance = dtoTestSuiteInstance.getTestSuiteInstance();
		final DbTestSuiteGroup testSuiteGroup = testSuiteInstance.getTestSuiteGroup();
		final List<DbTestInstance> listTestInstances = dtoTestSuiteInstance.getListDbTestInstances();
		final List<DbStatsCategoryInstance> listStatsCategoryInstances = dtoTestSuiteInstance
				.getListDbStatsCategoryInstances();

		final ListLines lines = new ListLines();

		// enntete
		lines.addLine(HEAD_BEGIN);
		lines.addLine(TITLE_BEGIN, testSuiteInstance.getName(), TITLE_END);
		lines.addLine(LINK_CSS);
		lines.addLine(HEAD_END);

		lines.addLine(BODY_BEGIN);
		lines.addLine(H1_BEGIN, "Suite de tests", SPACE, testSuiteInstance.getName(), H1_END);

		// statistiques & description & logs
		this.completeLineInfos(dtoTestSuiteInstance, lines, relatifPathForLog);

		// table des stats par category
		lines.addLine(H2_BEGIN, "Statistics by category", H2_END);
		lines.addLines(this.buildHtmlTableStatsGraphForDetailedReport(listStatsCategoryInstances));

		// liste des tests
		lines.addLines(this.buildHtmlTableTestsDetailedReport(testSuiteInstance, listTestInstances));

		// list des errors
		lines.addLines(buildHtmlListErrors(testSuiteInstance, listTestInstances));

		// fin
		lines.addLine(BODY_END);

		DtoHtmlPage htmlPage = this.createDtoHtmlPage(testSuiteInstance.getName());
		htmlPage.setShortTitle(testSuiteInstance.getFirmware());
		htmlPage.setListLines(lines);
		return htmlPage;
	}

	// ----------------------------------------- private methods
	private void completeLineInfos(DtoTestSuiteInstance dtoTestSuiteInstance, ListLines lines, String relatifPathForLog)
			throws JUnitHistoryException {

		final DbTestSuiteInstance testSuiteInstance = dtoTestSuiteInstance.getTestSuiteInstance();
		final List<DbStatsCategoryInstance> listStatsCategoryInstances = dtoTestSuiteInstance
				.getListDbStatsCategoryInstances();

		// TestSuite Info
		lines.addLine("<div id=info>");
		lines.addLine(H2_BEGIN, "Test Suite Info", H2_END);

		lines.addLine(LABEL_BEGIN, "Test:", LABEL_END);

		// statistiques globales de la testSuite
		TestStatistics statistic = this.buildSuiteTestStatistics(listStatsCategoryInstances);

		lines.addLine(SPAN_BEGIN, statistic.getTotal() + "", SPAN_END);
		lines.addLine(BR);
		lines.addLine(LABEL_BEGIN, "Time:", LABEL_END);
		lines.addLine(SPAN_BEGIN, testSuiteInstance.getTime() + " seconds", SPAN_END);
		lines.addLine(BR);

		lines.addLine(MF_BALISE_WIDTH_CLASS_BEGIN.format(new String[] { TABLE, "80%", CLASS_SUMMARY }), TR_BEGIN);

		lines.addLine(MF_BALISE_WIDTH_CLASS_BEGIN.format(new String[] { TD, "50%", CLASS_STATS }));
		// skipped
		lines.addLine(LABEL_BEGIN, "#Skipped:", LABEL_END, SPACE);
		lines.addLine(SPAN_BEGIN, statistic.getSkipped() + "", SPAN_END);
		lines.addLine(LABEL_BEGIN, "(dependency:", LABEL_END, SPACE);
		lines.addLine(SPAN_BEGIN, statistic.getSkippedDependency() + "", SPAN_END, SPACE, SPACE);
		lines.addLine(LABEL_BEGIN, "execution:", LABEL_END, SPACE);
		lines.addLine(SPAN_BEGIN, statistic.getSkippedProgrammaticaly() + ")", SPAN_END);
		lines.addLine(BR);

		// running
		lines.addLine(LABEL_BEGIN, "#Running:", LABEL_END, SPACE);
		lines.addLine(SPAN_BEGIN, statistic.getRunning() + "", SPAN_END, SPACE, SPACE);
		lines.addLine(LABEL_BEGIN, "(Success:", LABEL_END, SPACE);
		lines.addLine(SPAN_BEGIN, statistic.getRunningSuccess() + "", SPAN_END, SPACE, SPACE);
		lines.addLine(LABEL_BEGIN, "Failures:", LABEL_END, SPACE);
		lines.addLine(SPAN_BEGIN, statistic.getRunningFailure() + "", SPAN_END, SPACE, SPACE);
		lines.addLine(LABEL_BEGIN, "Errors:", LABEL_END);
		lines.addLine(SPAN_BEGIN, statistic.getRunningError() + ")", SPAN_END);

		lines.addLine(TD_END);

		// graph statistic
		lines.addLine(this.format(MF_BALISE_WIDTH_CLASS_BEGIN, TD, "50%", CLASS_STATS));
		lines.addLines(buildHtmlGraphStatistic(statistic));
		lines.addLine(TD_END, TR_END, TABLE_END);

		// infos
		lines.addLine(BR);
		lines.addLine(this.format(MF_BALISE_WIDTH_CLASS_BEGIN, DIV, "100%", "description"));
		lines.addLine(LABEL_BEGIN, "Date:", SPACE, LABEL_END,
				testSuiteInstance.getDate() == null ? "" : DATE_FORMAT.format(testSuiteInstance.getDate()));
		lines.addLine(DIV_BEGIN, LABEL_BEGIN, "Firmware:", SPACE, LABEL_END,
				ValueHelper.getStringValue(testSuiteInstance.getFirmware(), ""), DIV_END);
		lines.addLine(LABEL_BEGIN, "User:", SPACE, LABEL_END, testSuiteInstance.getUser() == null ? ""
				: testSuiteInstance.getUser().getName());
		lines.addLine(DIV_BEGIN, LABEL_BEGIN, "IPTVKIT:", SPACE, LABEL_END,
				ValueHelper.getStringValue(testSuiteInstance.getIptvkit(), ""), DIV_END);

		// is there a description ?
		String desc = testSuiteInstance.getComment();
		if (!ValueHelper.isStringEmptyOrNull(desc)) {
			lines.addLine(BR);
			lines.addLine(LABEL_BEGIN, "Description: ", LABEL_END);
			lines.addLine(DIV_BEGIN, BR, HtmlUtils.encode2HTML(desc), DIV_END);
		}
		lines.addLine(DIV_END);

		// logs
		/*
		* <br><br><p class='logs'><a href='PLSag-pl-07.03.38-A.log'>Voir les
		logs</a></p>
		*/
		if (testSuiteInstance.isLogExists()) {

			// fichier de log
			String logFilename = PathManager.get().buildLogFile(new File(""), testSuiteInstance.getName()).getName();

			lines.addLine(this.format(MF_BALISE_CLASS_BEGIN, P, "logs"),
					this.format(MF_LINK_HREF, relatifPathForLog + logFilename), "Voir les logs", LINK_END, P_END);
		}

	}

	/*
	 * Regroupe les TClass en category
	 * ordonnées par ordre alphabetique
	 */
	private Map<DbTestClassCategory, List<DbTestClass>> buildMapCategory2ListTClasses(List<DbTestClass> listTClasses)
			throws JUnitHistoryException {

		if (listTClasses == null) {
			return null;
		}
		final Map<DbTestClassCategory, List<DbTestClass>> mapCategory2ListTClasses = new HashMap<>();

		for (DbTestClass dbTestClass : listTClasses) {
			List<DbTestClass> listTClassesForCategory = mapCategory2ListTClasses.get(dbTestClass.getCategory());
			if (listTClassesForCategory == null) {
				listTClassesForCategory = new ArrayList<>();
				listTClassesForCategory.add(dbTestClass);
				mapCategory2ListTClasses.put(dbTestClass.getCategory(), listTClassesForCategory);
			} else {
				if (!listTClassesForCategory.contains(dbTestClass)) {
					listTClassesForCategory.add(dbTestClass);
				}
			}
		}

		// reordonner par ordre alphabetique le categories et les tclasses
		for (DbTestClassCategory category : mapCategory2ListTClasses.keySet()) {

			final List<DbTestClass> listTClassesForCategory = mapCategory2ListTClasses.get(category);
			Collections.sort(listTClassesForCategory);
			mapCategory2ListTClasses.put(category, listTClassesForCategory);
		}

		return mapCategory2ListTClasses;
	}

	private static final String SEPARATOR_TCLASS_TEST = ";";

	/*
	 * Regroupe les noms des tests de plusieurs suites par TClass
	 */
	private Map<DbTestClass, List<String>> buildMapTClass2ListTestNames(List<DtoTestSuiteInstance> listDtoTestSuites) {

		final Map<DbTestClass, List<String>> mapTClass2ListTestNames = new HashMap<>();

		// for each testSuite
		for (DtoTestSuiteInstance dtoTestSuite : listDtoTestSuites) {

			// map [TClass.shortname;Test.name - TestInstance]
			final Map<String, DbTestInstance> mapTClassAndTestname2DbTestInstance = new HashMap<>();
			dtoTestSuite.setMapTClassAndTestname2DbTestInstance(mapTClassAndTestname2DbTestInstance);

			// for each Tests
			for (DbTestInstance test : dtoTestSuite.getListDbTestInstances()) {

				DbTestClass tclass = test.gettClass();
				if (tclass.getShortName() == null) {
					tclass.setShortName(tclass.getName().substring(PACKAGE_TO_CUT_LENGTH));
				}

				// liste des nom de test par TClass
				String testName = test.getName();
				// map interne à la suite
				mapTClassAndTestname2DbTestInstance.put(tclass.getShortName() + SEPARATOR_TCLASS_TEST + testName, test);

				List<String> testNamesForTClass = mapTClass2ListTestNames.get(tclass);
				if (testNamesForTClass == null) {
					testNamesForTClass = new ArrayList<>();
					testNamesForTClass.add(testName);
					mapTClass2ListTestNames.put(tclass, testNamesForTClass);
				} else {
					if (!testNamesForTClass.contains(testName)) {
						testNamesForTClass.add(testName);
					}
				}

			}

		}

		return mapTClass2ListTestNames;
	}

	/**
	 * Constuit en html la table des tests suite par category/classname
	 * pour une liste de testSuite
	 * 
	 */
	private ListLines buildHtmlTableTestListSuite(List<DtoTestSuiteInstance> listDtoTestSuites, ListLines htmlTitles)
			throws JUnitHistoryException {
		ListLines tableTest = new ListLines();

		// on regroupe les tests par TClass
		final Map<DbTestClass, List<String>> mapTClass2ListTestnames = this
				.buildMapTClass2ListTestNames(listDtoTestSuites);

		// on regroupe les TClass par category
		final List<DbTestClass> listTClasses = new ArrayList<>(mapTClass2ListTestnames.keySet());
		final Map<DbTestClassCategory, List<DbTestClass>> mapCategory2ListTClasses = this
				.buildMapCategory2ListTClasses(listTClasses);

		// Enfin on ecrit les tables par category
		final List<DbTestClassCategory> listCategories = new ArrayList<>(mapCategory2ListTClasses.keySet());
		Collections.sort(listCategories);
		for (DbTestClassCategory category : listCategories) {

			boolean odd = false;
			List<ListLines> listMinitables = this.buildHtmlBlockCategoryForListTestSuite(
					mapCategory2ListTClasses.get(category), mapTClass2ListTestnames, listDtoTestSuites);
			if (listMinitables == null || listMinitables.isEmpty()) {
				continue; // next
			}

			tableTest.addLine(H2_BEGIN, this.format(MF_LINK_NAME, category.getName()), LINK_END);
			tableTest.addLine("Category ", category.getName(), ":", H2_END);
			tableTest.addLine(H3_BEGIN, category.getDescription(), H3_END, BR);

			tableTest.addLine(TABLE_BEGIN);
			tableTest.addLines(htmlTitles);

			// for each testClass
			for (ListLines tclassName : listMinitables) {
				tclassName.replaceAll("&odd", (odd ? "odd" : "even"));
				tableTest.addLines(tclassName);
				odd = !odd;
			}
			tableTest.addLine(TABLE_END, BR, BR);
		}

		// tableTest.addLine(DIV_END);

		// =================================================================

		return tableTest;

	}

	/**
	 * Constuit en html la table des tests suite par category/classname
	 * Pour une suite unique
	 */
	private ListLines buildHtmlTableTestsDetailedReport(DbTestSuiteInstance testSuiteInstance,
			List<DbTestInstance> listTestInstances) throws JUnitHistoryException {

		ListLines tableTest = new ListLines();
		// =================================================================
		tableTest.addLine("<div id=tests>");
		tableTest.addLine(H2_BEGIN, "Tests Overview ", testSuiteInstance.getName(), H2_END);

		// on regroupe les tests par classe
		Map<DbTestClass, List<DbTestInstance>> mapTClass2Tests = new HashMap<>();

		// for each test
		for (DbTestInstance testInstance : listTestInstances) {

			DbTestClass testClass = testInstance.gettClass();
			if (testClass == null) {
				continue; // next test
			}
			List<DbTestInstance> listTestForClass = mapTClass2Tests.get(testClass);
			if (listTestForClass == null) {
				listTestForClass = new ArrayList<>();
				mapTClass2Tests.put(testClass, listTestForClass);
			}
			listTestForClass.add(testInstance);
		}

		// on groupe les classes par category
		final List<DbTestClass> listTClasses = new ArrayList<>(mapTClass2Tests.keySet());
		final Map<DbTestClassCategory, List<DbTestClass>> mapCategory2ClassTests = this
				.buildMapCategory2ListTClasses(listTClasses);

		// Enfin on ecrit les tables par category
		final List<DbTestClassCategory> listCategories = new ArrayList<>(mapCategory2ClassTests.keySet());
		Collections.sort(listCategories);
		for (DbTestClassCategory category : listCategories) {

			boolean odd = false;
			List<ListLines> listMinitables = this.buildHtmlBlockCategoryForTestSuite(
					mapCategory2ClassTests.get(category), mapTClass2Tests);
			if (listMinitables == null || listMinitables.isEmpty()) {
				continue; // next
			}

			tableTest.addLine(this.format(MF_LINK_NAME, category.getName()));
			tableTest.addLine(H2_BEGIN, "Category ", category.getName(), ":", SPACE, H3_BEGIN,
					category.getDescription());
			tableTest.addLine(LINK_END);
			tableTest.addLine(BR, BR);

			tableTest.addLine(this.format(MF_BALISE_WIDTH_BEGIN, TABLE, "50%"));
			tableTest.addLine(TR_BEGIN, this.format(MF_BALISE_WIDTH_BEGIN, TH, "40%"), "Class", TH_END,
					this.format(MF_BALISE_WIDTH_BEGIN, TH, "40%"), "Name", TH_END,
					this.format(MF_BALISE_WIDTH_BEGIN, TH, "10%"), "Time", TH_END,
					this.format(MF_BALISE_WIDTH_BEGIN, TH, "10%"), "Status", TH_END, TR_END);

			// for each testClass
			for (ListLines tclassName : listMinitables) {
				tclassName.replaceAll("&odd", (odd ? "odd" : "even"));
				tableTest.addLines(tclassName);
				odd = !odd;
			}
			tableTest.addLine(TABLE_END, BR, BR);
		}

		tableTest.addLine(H3_END, H2_END, DIV_END);

		// =================================================================

		return tableTest;

	}

	/*
	 * Contruction de la table des statistiques par category  pour un rapport individuel
	 * Graphe par categorie
	 */
	private ListLines buildHtmlTableStatsGraphForDetailedReport(List<DbStatsCategoryInstance> listStatsCategoryInstances) {

		ListLines lines = new ListLines();
		if (listStatsCategoryInstances == null) {
			return lines;
		}
		Collections.sort(listStatsCategoryInstances);

		lines.addLine(this.format(MF_BALISE_WIDTH_CLASS_BEGIN, TABLE, "100%", CLASS_STATS));
		lines.addLine(TH_BEGIN, "category", TH_END, TH_BEGIN, "total", TH_END, TH_BEGIN, "run", TH_END, TH_BEGIN,
				"statistiques [success - failure - error - skipped] - percent / running tests", TH_END);

		// chercher le nombre max de tests pour une category
		int maxCountTests = 0;

		// for each category
		for (DbStatsCategoryInstance statsCategory : listStatsCategoryInstances) {

			TestStatistics statistic = statsCategory.getTestStatistics();
			if (statistic != null && statistic.getTotal() > maxCountTests) {
				maxCountTests = statistic.getTotal();
			}
		}
		// for each category
		for (DbStatsCategoryInstance statsCategory : listStatsCategoryInstances) {

			DbTestClassCategory category = statsCategory.getTClassCategory();
			TestStatistics statistic = statsCategory.getTestStatistics();
			if (statistic != null) {

				lines.addLines(this.buildHtmlTableRowStatistic(statistic, maxCountTests, category.getName(),
						category.getDescription(), "#" + category.getName()));
			}
		}

		lines.addLine(TABLE_END);
		return lines;

	}

	private String format(MessageFormat mf, Object... values) {

		if (values == null || values.length == 0) {
			return "";
		}
		final String[] tabValues = new String[values.length];
		for (int i = 0; i < values.length; i++) {
			tabValues[i] = values[i].toString();

		}
		return mf.format(tabValues);
	}

	/**
	 * Somme des statistiques des categorie pour obtenir la statistique de la
	 * suite de test
	 * 
	 * @param listStatsCategoryInstances
	 * @return
	 */
	private TestStatistics buildSuiteTestStatistics(List<DbStatsCategoryInstance> listStatsCategoryInstances) {

		TestStatistics suiteStatistics = new TestStatistics();
		if (listStatsCategoryInstances == null) {
			return suiteStatistics;
		}
		for (DbStatsCategoryInstance statsCategoryInstance : listStatsCategoryInstances) {
			suiteStatistics.addTestStatistics(statsCategoryInstance.getTestStatistics());
		}

		return suiteStatistics;
	}

	/*
	 * construction d'une ligne de la table des statistiques
	 */
	private ListLines buildHtmlTableRowStatistic(TestStatistics statistic, int maxCountTests, String colName,
			String description, String link) {

		ListLines htmlrow = new ListLines();

		if (statistic != null) {

			htmlrow.addLine(TR_BEGIN);

			// nom de la category de test
			String titleDescription = description == null ? "" : description;

			htmlrow.addLine(this.format(MF_BALISE_WIDTH_TITLE_BEGIN, TH, "150px", titleDescription));
			htmlrow.addLine(this.format(MF_LINK_HREF, link), colName, LINK_END, TH_END);

			// nombre total de tests: running + skipped
			htmlrow.addLine(this.format(MF_BALISE_CLASS_TITLE_VALUE, TD, CLASS_COUNT, statistic.getDetailTotal(),
					statistic.getTotal()));

			// nombre de tests joues: running
			htmlrow.addLine(this.format(MF_BALISE_CLASS_TITLE_VALUE, TD, CLASS_COUNT, statistic.getDetailRunning(),
					statistic.getRunning()));

			String td_stats = this.format(MF_BALISE_CLASS_BEGIN, TD, CLASS_STATS);
			htmlrow.addLine(td_stats);

			int percentTot = statistic.getPercentTotal(maxCountTests);

			htmlrow.addLine(this.format(MF_BALISE_WIDTH_CLASS_BEGIN, TABLE, percentTot + "%", CLASS_STATS));
			htmlrow.addLine(TR_BEGIN, td_stats);

			// graph statistic
			htmlrow.addLines(buildHtmlGraphStatistic(statistic));
			htmlrow.addLine(TD_END, TR_END, TABLE_END);
			htmlrow.addLine(TD_END);

			htmlrow.addLine(TR_END);
		}

		return htmlrow;

	}

	/**
	 * Contruit une table linéaire représentant les valeurs sous forme barre
	 * couleur de taille proportionnelle à value
	 * 
	 * @param values
	 * @param stylenames
	 * @return
	 */
	private ListLines buildHtmlGraphStatistic(TestStatistics statistic) {
		int[] values = { statistic.getPercentRunningSuccess(), statistic.getPercentRunningFailure(),
				statistic.getPercentRunningError() };
		String[] stylenames = { TestStatusEnum.Success.getStyleName(), TestStatusEnum.Failure.getStyleName(),
				TestStatusEnum.Error.getStyleName() };
		String[] titles = { statistic.getDetailSuccess(), statistic.getDetailFailure(), statistic.getDetailError() };
		return buildHtmlGraph(values, stylenames, statistic.getPercentRunning(), titles, statistic.getDetailSkipped());
	}

	private ListLines buildHtmlGraph(int[] values, String[] stylenames, int tableSize, String[] titles,
			String titleDefault) {

		ListLines table = new ListLines();

		table.addLine(this.format(MF_BALISE_CLASS_TITLE_BEGIN, DIV, TestStatusEnum.Skipped.getStyleName(), titleDefault));

		table.addLine("<table style=\"text-align: left; width: " + tableSize
				+ "%;\" border=\"1\" cellpadding=\"2\" cellspacing=\"2\">");

		table.addLine(TBODY_BEGIN, TR_BEGIN);

		for (int i = 0; i < values.length; i++) {
			int value = values[i];
			if (value > 0) {
				ListLines block = new ListLines();
				String title = (titles[i] != null) ? " title=\"" + titles[i] + "\"" : "";
				block.addLine("<td class= \"", stylenames[i], "\" style=\"vertical-align: top; width: ", value
						+ "%;\" ", title, ">", value + "%", TD_END);
				table.addLines(block);
			}
		}

		table.addLine(TR_END, TBODY_END, TABLE_END);
		table.addLine(DIV_END);
		return table;
	}

	/*
	 * Ensemble des blocs tclass pour une categorie et une liste de suites
	 */
	private List<ListLines> buildHtmlBlockCategoryForListTestSuite(List<DbTestClass> listTestClasses,
			Map<DbTestClass, List<String>> mapTClass2ListTestnames, List<DtoTestSuiteInstance> listDtoSuites) {

		if (listTestClasses == null || listTestClasses.isEmpty()) {
			return null;
		}

		List<ListLines> listMinitables = new ArrayList<>(listTestClasses.size());
		// for each classTest
		for (DbTestClass testClass : listTestClasses) {

			log.fine("TestClass: " + testClass.getName());
			ListLines miniTable = this.buildHtmlBlockTClassForListTestSuite(testClass,
					mapTClass2ListTestnames.get(testClass), listDtoSuites);
			if (miniTable != null) {
				listMinitables.add(miniTable);
			}
		}

		return listMinitables;
	}

	/*
	 * Ensemble des blocs tclass pour une categorie et une suite
	 */
	private List<ListLines> buildHtmlBlockCategoryForTestSuite(List<DbTestClass> listTClasses,
			Map<DbTestClass, List<DbTestInstance>> mapClass2Tests) {

		if (listTClasses == null || listTClasses.isEmpty()) {
			return null;
		}

		List<ListLines> listMinitables = new ArrayList<>(listTClasses.size());
		// for each classTest
		for (DbTestClass testClass : listTClasses) {

			List<DbTestInstance> listTests = mapClass2Tests.get(testClass);

			if (listTests != null) {
				ListLines miniTable = this.buildHtmlBlockTClassForTestSuite(testClass, listTests);
				if (miniTable != null) {
					listMinitables.add(miniTable);
				}
			}
		}

		return listMinitables;

	}

	/*
	 * Block s'insérant dans une table category d'une liste de rapport
	 * Construit un block html des test pour une category et une classe de test
	 * ex Network_wan_lan / com.francetelecom.orangetv.gwt.stb.test.GwtTestLanRemote 
	 * 
	 * @return list lines or null if all tests skipped
	 */
	private ListLines buildHtmlBlockTClassForListTestSuite(DbTestClass testClass, List<String> listTestnames,
			List<DtoTestSuiteInstance> listDtoSuites) {

		int countTestByClass = 0;

		String shortClassname = testClass.getShortName() == null ? testClass.getName().substring(PACKAGE_TO_CUT_LENGTH)
				: testClass.getShortName();

		String trClassOdd = this.format(MF_BALISE_CLASS_BEGIN, TR, "&odd");
		ListLines listLines = new ListLines();
		listLines.addLine(trClassOdd, "<td rowspan='&countTestByClass'>", shortClassname, TD_END);

		boolean atLeastOneTestRunning = false;

		int floor = 0;
		// for each testname (une ligne de la table)
		for (int i = 0; i < listTestnames.size(); i++) {
			String testname = listTestnames.get(i);

			boolean testinit = TEST_INIT_NAME.equals(testname);

			// on n'affiche pas les testInit
			if (testinit) {
				if (i == 0) {
					floor++;
				}
				continue; // next test
			}

			countTestByClass++;
			String floorLine = (i > floor) ? trClassOdd : "";
			listLines.addLine(floorLine, TD_BEGIN, testname, TD_END);

			// for each suite
			for (DtoTestSuiteInstance dtoTestSuiteInstance : listDtoSuites) {

				String suitePageName = this.createDtoHtmlPage(dtoTestSuiteInstance.getTestSuiteInstance().getName())
						.getPageName();

				// Recuperer le test avec le nom combine:
				// tclass.shortname;test.name
				String key = shortClassname + SEPARATOR_TCLASS_TEST + testname;
				DbTestInstance test = dtoTestSuiteInstance.getMapTClassAndTestname2DbTestInstance().get(key);
				atLeastOneTestRunning = (atLeastOneTestRunning) ? true : test != null
						&& test.getStatus().getStatus() != TestStatusEnum.Skipped;
				listLines.addLines(buildHtmlTestStatus(test, suitePageName, true));
			}
			listLines.addLine(TR_END);
		}

		if (countTestByClass > 0) {
			listLines.replaceFirst("&countTestByClass", "" + countTestByClass);
		} else {
			listLines = null;
		}

		return atLeastOneTestRunning ? listLines : null;
	}

	/*
	 * Block s'insérant dans une table category d'un rapport individuel
	 * Construit un block html des test pour une category et une classe de test
	 * ex Network_wan_lan / com.francetelecom.orangetv.gwt.stb.test.GwtTestLanRemote 
	 */
	private ListLines buildHtmlBlockTClassForTestSuite(DbTestClass testClass, List<DbTestInstance> listTestInstances) {

		int countTestByClass = 0;

		String shortClassname = testClass.getName().substring(PACKAGE_TO_CUT_LENGTH);

		String trClassOdd = this.format(MF_BALISE_CLASS_BEGIN, TR, "&odd");
		ListLines listLines = new ListLines();
		listLines.addLine(trClassOdd, "<td rowspan='&countTestByClass'>", shortClassname, TD_END);

		int floor = 0;
		// for each test
		for (int i = 0; i < listTestInstances.size(); i++) {
			DbTestInstance test = listTestInstances.get(i);

			boolean testinit = TEST_INIT_NAME.equals(test.getName());

			TestSubStatusEnum subStatus = test.getStatus();

			// on n'affiche pas les skipped!!! ni les testInit
			if (testinit || (this.hideSkipped && subStatus.getStatus() == TestStatusEnum.Skipped)) {
				if (i == 0) {
					floor++;
				}
				continue; // next test
			}

			countTestByClass++;
			String floorLine = (i > floor) ? trClassOdd : "";
			listLines.addLine(floorLine, TD_BEGIN, test.getName(), TD_END);

			double timeInSec = new Double(test.getTime()) / 1000;
			listLines.addLine(TD_BEGIN, TIME_SEC_FORMAT.format(timeInSec), TD_END);
			listLines.addLines(buildHtmlTestStatus(test, "", true));
			listLines.addLine(TR_END);

		}

		if (countTestByClass > 0) {
			listLines.replaceFirst("&countTestByClass", "" + countTestByClass);
		} else {
			listLines = null;
		}

		return listLines;

	}

	private ListLines buildHtmlTestStatus(DbTestInstance test, String suiteUrl, boolean cell) {

		ListLines sb = new ListLines();

		if (test == null) {
			sb.addLine(this.format(MF_BALISE_CLASS_TITLE_BEGIN, cell ? TD : SPAN, "Status NotAvail", "no such test"),
					"---", cell ? TD_END : SPAN_END);
			return sb;
		}

		DbTestMessage testMessage = test.getMessage();
		String detail = "";
		if (testMessage != null) {
			detail = testMessage.getType() + "\n" + testMessage.getMessage();
		}

		TestSubStatusEnum subStatus = test.getStatus();
		sb.addLine(this.format(MF_BALISE_CLASS_TITLE_BEGIN, (cell ? TD : SPAN), subStatus.getStatus().getStyleName(),
				detail));
		if (subStatus.getStatus() == TestStatusEnum.Success) {
			sb.addLine("Ok", TD_END);
			return sb;
		}

		if (cell) {
			sb.addLine(this.format(MF_LINK_HREF, suiteUrl + "#" + test.gettClass().getName() + "." + test.getName()),
					subStatus.getLabel(), LINK_END);
		} else {
			sb.addLine(subStatus.getLabel());
		}
		sb.addLine(cell ? TD_END : SPAN_END);

		return sb;
	}

	/**
	 * Construit la table html des errors
	 * 
	 * @param reportTestSuite
	 * @param mapTestCaseAndStatus
	 * @return
	 */
	private ListLines buildHtmlListErrors(DbTestSuiteInstance testSuiteInstance, List<DbTestInstance> listTestInstances)
			throws JUnitHistoryException {

		ListLines tableError = new ListLines();
		tableError.addLine("<div id=details>");
		tableError.addLine(H2_BEGIN, "Tests In Error", H2_END);
		tableError.addLine(BR);

		// for each test
		for (DbTestInstance test : listTestInstances) {

			if (test.gettClass() == null) {
				continue; // next test
			}
			TestSubStatusEnum subStatus = test.getStatus();
			DbTestMessage message = test.getMessage();

			if (message != null && message.isLazy()) {
				message = DaoTestMessage.get().getById(message.getId());
			}

			if (message != null) {
				tableError.addLine(this.format(MF_LINK_NAME, test.gettClass().getName() + "." + test.getName()));
				tableError.addLines(buildHtmlTestStatus(test, null, false));
				tableError.addLine(H3_BEGIN, format(MF_BALISE_CLASS_BEGIN, SPAN, "classname"), test.gettClass()
						.getName(), SPAN_END, ".", format(MF_BALISE_CLASS_BEGIN, SPAN, "testname"), test.getName()
						+ SPAN_END, H3_END);

				tableError.addLine(LABEL_BEGIN, "Type:", LABEL_END);
				tableError.addLine(SPAN_BEGIN, TT_BEGIN + message.getType(), TT_END, SPAN_END, BR);

				// String message = JUnitReport.getMessage(foe);
				if (message.getMessage() != null) {
					tableError.addLine(BR, LABEL_BEGIN, "Message:", LABEL_END);
					tableError.addLine(PRE_BEGIN, HtmlUtils.encode2HTML(message.getMessage()), PRE_END);
				}

				if (subStatus.getStatus() == TestStatusEnum.Skipped) {
					// aucun interet a afficher la stacktrace
					tableError.addLine(BR);
					continue; // next test
				}

				if (message.getStackTrace() != null && !message.getStackTrace().isEmpty()) {
					tableError.addLine(BR, LABEL_BEGIN, "Stack Trace:", LABEL_END);
					tableError.addLine(PRE_BEGIN, HtmlUtils.encode2HTML(message.getStackTrace()), PRE_END);
				}

				if (message.getOutputLog() != null && !message.getOutputLog().isEmpty()) {
					tableError.addLine(BR, LABEL_BEGIN, "Output Logs:", LABEL_END);
					tableError.addLine(PRE_BEGIN, HtmlUtils.encodeOutput2HTML(message.getOutputLog()), PRE_END);
				}

				tableError.addLine(BR);
			}
		}
		tableError.addLine(DIV_END);
		// =================================================================

		return tableError;
	}

	private List<DtoHtmlPage> buildListDtoHtmlPages(List<DtoTestSuiteInstance> suites, String relatifPathForLog)
			throws JUnitHistoryException {

		if (suites == null || suites.isEmpty()) {
			return null;
		}
		// // TODO trier avec les plus recents en premier
		final List<DtoHtmlPage> listPages = new ArrayList<>(suites.size());
		// for each suite on construit le rapport individuel
		for (DtoTestSuiteInstance dtoSuite : suites) {

			listPages.add(this.buildHtmPageFromTestSuite(dtoSuite, relatifPathForLog));
		}

		return listPages;
	}

	/*
	 *  Page de statistique liée à une liste de suites
	 *  ex statistique de FrR4-Samsung - IPTV Kit Test Suite
	 */
	private DtoHtmlPage buildHtmlPageStatsForListSuite(String groupName, DtoHtmlPage mainHtmlPage,
			List<DtoTestSuiteInstance> listDtoTestSuites) throws JUnitHistoryException {

		ListLines listLines = new ListLines();

		listLines.addLine(HEAD_BEGIN);
		listLines.addLine(TITLE_BEGIN, "Statistics ", groupName, TITLE_END);
		listLines.addLine(LINK_CSS, HEAD_END);

		listLines.addLine(BODY_BEGIN);

		listLines.addLine(H2_BEGIN, "Statistics for STB: ", groupName, H2_END);

		// Table des graphes de stats par suitename
		String divClassSuite = this.format(MF_BALISE_CLASS_BEGIN, DIV, CLASS_SUITE);
		listLines.addLine(divClassSuite);
		listLines.addLines(this.buildHtmlTableStatsGraphForListSuite(listDtoTestSuites));
		listLines.addLine(DIV_END);

		// table des % success par category
		listLines.addLine(H1_BEGIN, "% success and evolution...", H1_END);
		listLines.addLine(divClassSuite);
		listLines.addLines(this.buildHtmlTableStatsCategorySuccessForListSuite(mainHtmlPage, listDtoTestSuites));

		listLines.addLine(DIV_END, BODY_END);

		DtoHtmlPage page = this.createDtoHtmlPageStats(groupName);
		page.setListLines(listLines);

		return page;
	}

	/*
	 * Page Html (vide) pour une suite 
	 */
	private DtoHtmlPage createDtoHtmlPageStats(String groupName) {

		return this.createDtoHtmlPage(groupName + SUFFIXE_STATS);
	}

	/*
	 * Page Html (vide) pour une suite 
	 */
	private DtoHtmlPage createDtoHtmlPage(String name) {

		return new DtoHtmlPage(name, name + HTML_EXT);
	}

	/*
	 * Tableau suite/row & category/col
	 * pourcentage de reussite et evolution
	 * appellé depuis une page de liste de suite (mainHtmlPage)
	 */
	private ListLines buildHtmlTableStatsCategorySuccessForListSuite(DtoHtmlPage mainHtmlPage,
			List<DtoTestSuiteInstance> listDtoTestSuites) throws JUnitHistoryException {

		List<DbTestClassCategory> listCategories = DaoTestClassCategory.get().listCategories(true);

		ListLines listLines = new ListLines();

		// ligne de titre du tableau -------------------------
		listLines.addLine(this.format(MF_BALISE_CLASS_BEGIN, TABLE, CLASS_STATS));
		listLines.addLine(TH_BEGIN, "suite", TH_END, TH_BEGIN, "run", TH_END);

		// liste des categories
		for (DbTestClassCategory statsCategory : listCategories) {
			listLines.addLine(TH_BEGIN);
			listLines.addLine(this.format(MF_LINK_HREF, mainHtmlPage.getPageName() + "#" + statsCategory.getName()));
			listLines.addLine(statsCategory.getName());
			listLines.addLine(LINK_END);
			listLines.addLine(TH_END);
		}
		// -----------------------------------------------------

		// determiner l'évolution pour une meme category d'une suite à l'autre
		// ordre du plus ancien au plus recent
		Collections.reverse(listDtoTestSuites);

		// for each category
		for (DbTestClassCategory statsCategory : listCategories) {

			// System.out.println("\ncategory :" + statsCategory.name());
			int previousValue = -1;
			// for each suite
			for (DtoTestSuiteInstance dtoSuite : listDtoTestSuites) {
				List<DbStatsCategoryInstance> listStats = dtoSuite.getListDbStatsCategoryInstances();
				if (listStats == null) {
					continue;
				}
				// on cherche les stats pour la category
				for (DbStatsCategoryInstance dbStatsCategoryInstance : listStats) {
					if (dbStatsCategoryInstance.getTClassCategory().equals(statsCategory)) {

						TestStatistics testStatistics = dbStatsCategoryInstance.getTestStatistics();

						if (testStatistics == null) {
							continue; // next
						}
						if (previousValue != 1) {
							boolean up = testStatistics.getPercentRunningSuccess() >= previousValue;
							testStatistics.setUp(up);
						}
						previousValue = testStatistics.getPercentRunningSuccess();

					}
				}

			}

		}

		// ordre du plus recent au plus ancien
		Collections.reverse(listDtoTestSuites);
		// for each suite
		for (int i = 0; i < listDtoTestSuites.size(); i++) {

			boolean lastRow = i == listDtoTestSuites.size() - 1;
			DtoTestSuiteInstance dtoSuite = listDtoTestSuites.get(i);

			List<DbStatsCategoryInstance> stats = dtoSuite.getListDbStatsCategoryInstances();
			DtoHtmlPage suitePage = this.createDtoHtmlPage(dtoSuite.getTestSuiteInstance().getName());
			listLines.addLines(this.buildHtmlTableRowCategorySuccess(stats, buildHtmlLinkForHtmlPage(suitePage, false),
					lastRow));
		}

		listLines.addLine(TABLE_END);

		return listLines;

	}

	/*
	 * Retourne le lien vers un rapport individuel
	 */
	private String buildHtmlLinkForHtmlPage(DtoHtmlPage htmlPage, boolean shortTitle) {
		return this.format(MF_LINK_HREF, htmlPage.getPageName())
				+ (shortTitle && htmlPage.getShortTitle() != null ? htmlPage.getShortTitle() : htmlPage.getTitle())
				+ LINK_END;
	}

	/*
	 * valeur des pourcentage de success par category pour une suite
	 * Retourne une ligne de table HTML
	 */

	private ListLines buildHtmlTableRowCategorySuccess(List<DbStatsCategoryInstance> listStats, String title,
			boolean lastRow) throws JUnitHistoryException {

		ListLines htmlrow = new ListLines();

		if (listStats != null) {

			// préparation pour les stats par category
			final Map<DbTestClassCategory, DbStatsCategoryInstance> mapCategory2Stats = new HashMap<>(listStats.size());
			final TestStatistics testStatsTotal = new TestStatistics();
			for (DbStatsCategoryInstance statsCategory : listStats) {

				mapCategory2Stats.put(statsCategory.getTClassCategory(), statsCategory);
				testStatsTotal.addTestStatistics(statsCategory.getTestStatistics());
			}

			List<DbTestClassCategory> listCategories = DaoTestClassCategory.get().listCategories(true);

			htmlrow.addLine(TR_BEGIN);

			// nom de la category de test
			htmlrow.addLine(this.format(MF_BALISE_WIDTH_BEGIN, TH, "150px"));
			htmlrow.addLine(title);
			htmlrow.addLine(TH_END);

			// nombre total de tests: running + skipped
			htmlrow.addLine(this.format(MF_BALISE_CLASS_TITLE_BEGIN, TD, CLASS_COUNT, testStatsTotal.getDetailRunning()));
			htmlrow.addLine(testStatsTotal.getRunning());
			htmlrow.addLine(TD_END);

			final String tdNoValue = this.format(MF_BALISE_CLASS_BEGIN, TD, CLASS_EVOL) + "-" + TD_END;
			// For each category >> cell
			for (DbTestClassCategory category : listCategories) {

				DbStatsCategoryInstance stats = mapCategory2Stats.get(category);
				if (stats != null) {

					// pourcentage de success pour cette category
					TestStatistics testStatistics = stats.getTestStatistics();
					String upOrDown = CLASS_EVOL + " " + ((testStatistics.isUp()) ? CLASS_UP : CLASS_DOWN);

					int percentSuccess = testStatistics.getPercentRunningSuccess();

					if (testStatistics.getRunning() > 0) {

						htmlrow.addLine(this.format(MF_BALISE_CLASS_TITLE_BEGIN, TD, (lastRow) ? CLASS_EVOL : upOrDown,
								testStatistics.getDetailRunning()), percentSuccess + "%", TD_END);
					} else {
						// non running
						htmlrow.addLine(tdNoValue);
					}

				} else {
					// si stats non trouve alors valeur par defaut
					htmlrow.addLine(tdNoValue);

				}

			}
			htmlrow.addLine(TR_END);
		}
		return htmlrow;

	}

	/*
	 * Graphe de statistique pour une liste de suite
	 * Une ligne par suite
	 */
	private ListLines buildHtmlTableStatsGraphForListSuite(List<DtoTestSuiteInstance> listDtoTestSuites) {

		final ListLines htmlTable = new ListLines();

		htmlTable.addLine(this.format(MF_BALISE_WIDTH_CLASS_BEGIN, TABLE, "100%", CLASS_STATS));
		htmlTable.addLine(TH_BEGIN, "suite", TH_END, TH_BEGIN, "total", TH_END, TH_BEGIN, "run", TH_END, TH_BEGIN,
				"statistiques [success - failure - error - skipped] - percent / running tests", TH_END);

		// chercher le nombre max de tests pour une suite
		int maxCountTests = 0;
		TestStatistics testStatsTotal = null;

		// map [suite - stats tot]
		final Map<DbTestSuiteInstance, TestStatistics> mapSuite2StatsTotal = new HashMap<>(listDtoTestSuites.size());

		// for each suite
		for (DtoTestSuiteInstance dtoSuite : listDtoTestSuites) {

			testStatsTotal = new TestStatistics();
			for (DbStatsCategoryInstance statsCategory : dtoSuite.getListDbStatsCategoryInstances()) {
				testStatsTotal.addTestStatistics(statsCategory.getTestStatistics());
			}
			mapSuite2StatsTotal.put(dtoSuite.getTestSuiteInstance(), testStatsTotal);

			// TestStatistics statistic = suite.get
			if (testStatsTotal.getTotal() > maxCountTests) {
				maxCountTests = testStatsTotal.getTotal();
			}
		}

		// iteration sur chaque suite de test
		for (DtoTestSuiteInstance dtoSuite : listDtoTestSuites) {

			final TestStatistics statsForSuite = mapSuite2StatsTotal.get(dtoSuite.getTestSuiteInstance());

			DtoHtmlPage dtoHtmlPage = this.createDtoHtmlPage(dtoSuite.getTestSuiteInstance().getName());
			htmlTable.addLines(this.buildHtmlTableRowStatistic(statsForSuite, maxCountTests, dtoHtmlPage.getTitle(),
					null, dtoHtmlPage.getPageName()));

		}
		htmlTable.addLine(TABLE_END);
		return htmlTable;

	}

}
