package com.francetelecom.orangetv.junithistory.server.manager;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

import com.francetelecom.orangetv.junithistory.server.dao.DaoTestClassCategory;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestSuiteGroup;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestUser;
import com.francetelecom.orangetv.junithistory.server.dao.IDbEntry;
import com.francetelecom.orangetv.junithistory.server.dto.DtoTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbStatsCategoryInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestClass;
import com.francetelecom.orangetv.junithistory.server.model.DbTestClassCategory;
import com.francetelecom.orangetv.junithistory.server.model.DbTestInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestMessage;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteGroup;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestUser;
import com.francetelecom.orangetv.junithistory.server.tools.IReportTestSuite;
import com.francetelecom.orangetv.junithistory.server.tools.IReportTestSuite.AgregatedJUnitTestSuite;
import com.francetelecom.orangetv.junithistory.server.tools.junit.JUnitStatistics;
import com.francetelecom.orangetv.junithistory.server.tools.junit.JUnitStatistics.CategoryCompteur;
import com.francetelecom.orangetv.junithistory.server.tools.junit.JUnitStatistics.StatisticCompteur;
import com.francetelecom.orangetv.junithistory.server.tools.junit.JUnitStatus;
import com.francetelecom.orangetv.junithistory.server.tools.junit.JUnitStatus.JUnitTestCaseStatus;
import com.francetelecom.orangetv.junithistory.server.tools.junit.xml.JUnitFailureOrError;
import com.francetelecom.orangetv.junithistory.server.tools.junit.xml.JUnitTestCase;
import com.francetelecom.orangetv.junithistory.server.tools.junit.xml.JUnitTestSuite;
import com.francetelecom.orangetv.junithistory.server.util.FileUtils;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.francetelecom.orangetv.junithistory.shared.util.ObjectUtils;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSingleReportData;

/**
 * Transforme un rapport JUnit xml en un model d'objet
 * 
 * @author ndmz2720
 * 
 */
public class ReportManager implements IManager {

	private static final Logger log = Logger.getLogger(ReportManager.class.getName());

	// ---------------------------------- Singleton
	private static ReportManager instance;

	public static final ReportManager get() {
		if (instance == null) {
			instance = new ReportManager();
		}
		return instance;
	}

	private ReportManager() {
	}

	// ---------------------- INIT ---------------------------

	// ---------------------------- public methods
	/**
	 * Construit un DtoTestSuiteInstance à partir des fichiers recement uploades
	 * et des infos fournies par l'utilisateur
	 * 
	 * @param singleReportDatas
	 * @param session
	 * @return
	 * @throws JUnitHistoryException
	 */
	public DtoTestSuiteInstance buildDtoSuiteInstanceFromUploadPath(VoSingleReportData singleReportDatas,
			String uploadPath) throws JUnitHistoryException {

		log.config("buildDtoSuiteInstanceFromUploadPath()");

		String path = uploadPath;
		if (path != null) {
			log.info("upload repository in session: " + path);

			// on s'assure que le repertoire existe
			File directory = new File(path);
			if (FileUtils.verifyDirectory(directory, false)) {

				// filtrer et controler les fichiers xml telechage
				List<File> listXmlFiles = this.verifyAndBuildListXmlFilesForSingleReport(directory, true);
				return this.buildDtoSuiteInstanceFromListXmlReport(singleReportDatas, listXmlFiles);

			} else {
				throw new JUnitHistoryException("uploaded repository do not exists or is not a directory!");
			}

		} else {
			throw new JUnitHistoryException("uploaded repository not in session");

		}

	}

	public String getVersionFromDtoTestSuiteInstance(DtoTestSuiteInstance dtoSuite) {

		DbTestSuiteGroup group = dtoSuite.getTestSuiteInstance().getTestSuiteGroup();
		String suitename = dtoSuite.getTestSuiteInstance().getName();

		String version = (group != null && suitename != null) ? FileUtils.getVersionFromRootName(group.getPrefix(),
				suitename) : null;

		return version;
	}

	// -------------------------------- package methods
	/**
	 * Permet de récupérer tous les rapport xml existants et d'alimenter la base
	 * de données
	 * 
	 * @param uploadPath
	 * @return
	 * @throws JUnitHistoryException
	 */
	Map<DtoTestSuiteInstance, List<File>> buildListDtoSuiteInstanceFromArchive(String archivePath)
			throws JUnitHistoryException {

		Map<DtoTestSuiteInstance, List<File>> mapDtoSuite2ListFiles = new HashMap<DtoTestSuiteInstance, List<File>>();
		final File archiveDir = new File(archivePath);
		if (FileUtils.verifyDirectory(archiveDir, false)) {

			// on récupère tous les fichiers qui nous intéresse (xml, xml.partx,
			// txt, log)
			List<File> allReportFiles = FileUtils.getListFile(archiveDir, REG_ROOT_XML_XMLPART_LOG_TXT);

			// on les trie par ordre alphabetique
			Collections.sort(allReportFiles, FileUtils.FILENAME_COMPARATOR);

			// on parcours la liste et on cherche les groupe de meme rootname

			String rootname = null;
			// for each file
			for (File reportFile : allReportFiles) {

				String root = FileUtils.getFileNameNoExt(reportFile, PATTERN_ROOT_XML_XMLPART_LOG_TXT);
				if (rootname == null || !rootname.equals(root)) {
					// on demarre une nouvelle serie
					log.info("new serie: " + root + "...");
					rootname = root;
					// on cherche tous les fichiers avec ce rootname
					final String regex = FileUtils.createRegexXmlXmlPartLogTxt(rootname);
					final List<File> listFileSameRootname = FileUtils.getListFile(archiveDir, regex);
					if (listFileSameRootname != null) {

						// on copy les fichiers dans un repertoire temporaire
						File tempDir = new File(archiveDir, "dir_" + rootname);
						FileUtils.copyFilesToTargetDir(listFileSameRootname, tempDir, true);

						// on recupère les fichiers xml necessaires au rapport
						List<File> xmlFiles = this.verifyAndBuildListXmlFilesForSingleReport(tempDir, false);
						//
						// on construit le rapport individuel
						VoSingleReportData voSingleReportData = new VoSingleReportData();
						// ajouter user et iptvkit version
						voSingleReportData.setIptvkit("unknown");
						DbTestUser userAdmin = DaoTestUser.get().getDefaultUser();
						voSingleReportData.setUserId((userAdmin == null) ? IDbEntry.ID_UNDEFINED : userAdmin.getId());
						final DtoTestSuiteInstance dtoSuite = this.buildDtoSuiteInstanceFromListXmlReport(
								voSingleReportData, xmlFiles);
						// on garde ces infos dans la map
						mapDtoSuite2ListFiles.put(dtoSuite, listFileSameRootname);
						// on supprime le rep temporaire et son contenu
						FileUtils.deleteDirectoryAndAllContent(tempDir);

					}
				}
			}

		} else {
			throw new JUnitHistoryException("archive repository do not exists or is not a directory!");
		}

		return mapDtoSuite2ListFiles;
	}

	DtoTestSuiteInstance buildDtoSuiteInstanceFromListXmlReport(VoSingleReportData singleReportDatas,
			List<File> listXmlFiles) throws JUnitHistoryException {

		log.config("buildDtoSuiteInstanceFromListXmlReport()");

		// on cree la representation Object du fichier
		DtoTestSuiteInstance dtoDatas = ReportManager.get().readXmlReport(listXmlFiles);

		DbTestSuiteInstance testSuiteInstance = dtoDatas.getTestSuiteInstance();

		testSuiteInstance.setDate(singleReportDatas.getDate());

		// si le firmware est indetermine, on essaie de le deduire du rootname
		if (ValueHelper.isStringEmptyOrNull(singleReportDatas.getFirmware())) {
			testSuiteInstance.setFirmware(this.getVersionFromDtoTestSuiteInstance(dtoDatas));
		} else {
			testSuiteInstance.setFirmware(singleReportDatas.getFirmware());
		}

		testSuiteInstance.setIptvkit(singleReportDatas.getIptvkit());

		testSuiteInstance.setUser(DaoTestUser.get().getById(singleReportDatas.getUserId()));
		testSuiteInstance.setComment(singleReportDatas.getComment());

		// si group non determine a partir du rootName des
		// fichiers alors setter le group choisi par l'utilisateur
		// (attention peut etre undefined)
		int groupIdFromDatas = singleReportDatas.getGroupId();
		if (testSuiteInstance.getTestSuiteGroup() == null) {
			// group indeterminé. On prend le choix de l'utilisateur
			testSuiteInstance.setTestSuiteGroup(DaoTestSuiteGroup.get().getById(groupIdFromDatas, true));
		} else {
			// si group choisi par l'utilisateur est différent du group
			// déterminé par le rootname des fichiers
			// alors exception
			int groupId = testSuiteInstance.getTestSuiteGroup().getId();
			if (singleReportDatas.getGroupId() != IDbEntry.ID_UNDEFINED && groupId != groupIdFromDatas) {
				final DbTestSuiteGroup groupFromDatas = DaoTestSuiteGroup.get().getById(groupIdFromDatas, true);
				if (groupFromDatas != null) {
					throw new JUnitHistoryException("Chosen STB " + groupFromDatas.getName()
							+ " not consistent with rapport name!");
				}
			}
		}

		return dtoDatas;
	}

	/**
	 * Construit pour un rapport JUnit rootname.xml
	 * <ul>
	 * <li>testSuite
	 * <li>la liste des DbTestSuiteInstance (test)
	 * <li>statistiques par categorie
	 * </ul>
	 * 
	 * @param xmlFile
	 * @return
	 * @throws JUnitHistoryException
	 */
	DtoTestSuiteInstance readXmlReport(File xmlFile) throws JUnitHistoryException {

		if (!FileUtils.verifyFile(xmlFile, false)) {
			throw new JUnitHistoryException("Invalid xml file: "
					+ ((xmlFile == null) ? "null" : xmlFile.getAbsolutePath()));
		}
		log.config("readXmlReport(" + xmlFile.getAbsolutePath() + ")");
		// nom du groupe de suite (can be null)
		DbTestSuiteGroup testSuiteGroup = this.findTestSuiteGroup(xmlFile.getName());

		final List<File> files = ObjectUtils.createList(xmlFile);

		// nom du fichier sans son extension
		final String rootName = FileUtils.getFileNameNoExt(xmlFile, PATTERN_ROOT_XML_AND_XMLPART);
		log.config("rootName: " + rootName);

		return this.readXmlReport(testSuiteGroup, rootName, files);
	}

	/**
	 * Construit pour une liste de rapport JUnit rootname.xml.partx
	 * <ul>
	 * <li>testSuite
	 * <li>la liste des DbTestSuiteInstance (test)
	 * <li>statistiques par categorie
	 * </ul>
	 * 
	 * @param xmlFile
	 * @return
	 * @throws JUnitHistoryException
	 */
	DtoTestSuiteInstance readXmlReport(List<File> listXmlFiles) throws JUnitHistoryException {

		if (listXmlFiles == null || listXmlFiles.isEmpty()) {
			throw new JUnitHistoryException("list of xml files cannot be null or empty! ");
		}

		File firstFile = listXmlFiles.get(0);
		// nom du groupe de suite (can be null)
		DbTestSuiteGroup testSuiteGroup = this.findTestSuiteGroup(firstFile.getName());

		// nom du fichier sans son extension
		final String rootName = FileUtils.getFileNameNoExt(firstFile, PATTERN_ROOT_XML_AND_XMLPART);

		DtoTestSuiteInstance dtoSuiteInstance = this.readXmlReport(testSuiteGroup, rootName, listXmlFiles);

		// Verify si le fichier de log est présent
		File logFile = PathManager.get().buildLogFile(firstFile.getParentFile(), rootName);
		if (FileUtils.verifyFile(logFile, false)) {
			dtoSuiteInstance.getTestSuiteInstance().setLogExists(true);
		}
		return dtoSuiteInstance;
	}

	// ------------------------------- private methods

	/*
	 * directory contient les fichiers xml, log et txt d'un rapport individuel
	 * Etablit la liste des fichiers xml utilises dans un rapport individuel -
	 * soit un seul fichier <rootname>.xml - soit une liste de fichiers
	 * <root_name>.xml.partx
	 */
	private List<File> verifyAndBuildListXmlFilesForSingleReport(File directory, boolean throwex)
			throws JUnitHistoryException {

		String errorMessage = null;
		List<File> allFiles = FileUtils.getListAllFiles(directory);
		if (!FileUtils.verifySameRootnames(allFiles)) {
			throw new JUnitHistoryException("All files must have the same root name for a single report!");
		}

		// on cherche d'abord les fichier xml, il doit y en avoir qu'un seul
		List<File> xmlFiles = FileUtils.getListFile(directory, REG_ROOT_XML);

		// on cherche les fichier avec *.xml.partx pattern
		List<File> xmlPartFiles = FileUtils.getListFile(directory, REG_ROOT_XMLPART);

		if (xmlFiles != null && !xmlFiles.isEmpty()) {

			if (xmlFiles.size() > 1) {
				throw new JUnitHistoryException("There must be only one xml for a single report!");
			} else {

				if (xmlPartFiles != null && !xmlPartFiles.isEmpty()) {
					throw new JUnitHistoryException(
							"xml file and xml.part file cannot be mingled  for a single report!");
				}
				return xmlFiles;
			}
		}

		if (xmlPartFiles == null || xmlPartFiles.isEmpty()) {
			errorMessage = "There must be at least one *.xml.part file or a list of *.xml.part files for a single report!";
			log.warning(errorMessage);
			if (throwex) {
				throw new JUnitHistoryException(errorMessage);
			}
		}

		return xmlPartFiles;
	}

	private DtoTestSuiteInstance readXmlReport(DbTestSuiteGroup testSuiteGroup, String rootName, List<File> listXmlFiles)
			throws JUnitHistoryException {
		// Représentation mémoire des fichiers xml
		IReportTestSuite reportTestSuite = this.buildAgregatedTestSuite((testSuiteGroup == null) ? null
				: testSuiteGroup.getName(), rootName, listXmlFiles);

		if (reportTestSuite == null) {
			return null;
		}

		// statistiques pour tous les tests de la suite (groupement par
		// categorie de test)
		final StatisticCompteur statisticCompteur = new StatisticCompteur();

		// Suite de test Instance
		DbTestSuiteInstance testSuiteInstance = this.createDbTestSuiteInstance(testSuiteGroup, reportTestSuite);

		// liste des tests
		List<DbTestInstance> listDbTestInstances = this.createListDbTestInstances(testSuiteInstance, reportTestSuite,
				statisticCompteur);

		// statistiques
		List<DbStatsCategoryInstance> listDbStatsCategoryInstances = this.createListDbStatsCategoryInstances(
				testSuiteInstance, statisticCompteur);

		return new DtoTestSuiteInstance(testSuiteInstance, listDbTestInstances, listDbStatsCategoryInstances);
	}

	private DbTestSuiteInstance createDbTestSuiteInstance(DbTestSuiteGroup testSuiteGroup,
			IReportTestSuite reportTestSuite) {

		DbTestSuiteInstance testSuiteInstance = new DbTestSuiteInstance(testSuiteGroup);
		testSuiteInstance.setName(reportTestSuite.getRootName());
		testSuiteInstance.setTime(new Double(reportTestSuite.getTime()).longValue());
		return testSuiteInstance;
	}

	private List<DbTestInstance> createListDbTestInstances(DbTestSuiteInstance testSuiteInstance,
			IReportTestSuite reportTestSuite, StatisticCompteur statisticCompteur) throws JUnitHistoryException {

		// [className - list or JUnitTestCase]
		Map<String, List<JUnitTestCase>> classname2Tests = reportTestSuite.getClassname2Tests();

		List<DbTestInstance> listTestInstance = new ArrayList<>();
		// for each Classe de test
		for (String classname : classname2Tests.keySet()) {

			DbTestClass testClass = new DbTestClass(classname);
			testClass.setCategory(JUnitStatistics.getStatCategory(classname));

			List<DbTestInstance> listTestInstanceForClassName = this.createListDbTestInstanceForClass(
					testSuiteInstance, testClass, classname2Tests.get(classname), statisticCompteur);

			listTestInstance.addAll(listTestInstanceForClassName);

		}

		return listTestInstance;
	}

	/**
	 * Construction des statistiques de la suite à partir des informations du
	 * compteur
	 * 
	 * @param testSuiteInstance
	 * @param statisticCompteur
	 * @return
	 */
	private List<DbStatsCategoryInstance> createListDbStatsCategoryInstances(DbTestSuiteInstance testSuiteInstance,
			StatisticCompteur statisticCompteur) throws JUnitHistoryException {

		List<DbStatsCategoryInstance> listStatsCategoryInstance = new ArrayList<>();

		List<DbTestClassCategory> listCategories = DaoTestClassCategory.get().listCategories(true);

		Map<DbTestClassCategory, CategoryCompteur> mapCategory2Compteur = statisticCompteur.getCategory2Compteur();
		if (mapCategory2Compteur != null) {

			// for each category
			for (DbTestClassCategory category : mapCategory2Compteur.keySet()) {

				CategoryCompteur compteur = mapCategory2Compteur.get(category);
				DbStatsCategoryInstance statsCategoryInstance = new DbStatsCategoryInstance(testSuiteInstance, category);
				statsCategoryInstance.setTestStatistics(compteur.toTestStatistics());
				listStatsCategoryInstance.add(statsCategoryInstance);
			}
		}

		return listStatsCategoryInstance;
	}

	/*
	 * Construit la liste des tests d'une classe de test ainsi que les
	 * statistiques associées Construit un block html des test pour une category
	 * et une classe de test ex Network_wan_lan /
	 * com.francetelecom.orangetv.gwt.stb.test.GwtTestLanRemote
	 */
	private List<DbTestInstance> createListDbTestInstanceForClass(DbTestSuiteInstance testSuiteInstance,
			DbTestClass tClass, List<JUnitTestCase> tests, StatisticCompteur statsCompteur) {

		final DbTestClassCategory statsCategory = tClass.getCategory();

		final List<DbTestInstance> listTestInstances = (tests == null) ? new ArrayList<DbTestInstance>(0)
				: new ArrayList<DbTestInstance>(tests.size());

		// for each test
		for (int i = 0; i < tests.size(); i++) {

			DbTestInstance testInstance = new DbTestInstance(testSuiteInstance, tClass);
			JUnitTestCase test = tests.get(i);

			boolean testinit = TEST_INIT_NAME.equals(test.getName());
			if (testinit) {
				continue; // next test
			}

			JUnitTestCaseStatus testStatus = JUnitStatus.findTestStatus(test);
			testInstance.setStatus(testStatus.getSubStatus());
			if (!testinit) {

				statsCompteur.addStatus(testStatus, statsCategory);
			}

			testInstance.setName(test.getName());
			// time en ms
			testInstance.setTime(new Double(test.getTime() * 1000).longValue());
			testInstance.setMessage(this.buildDbTestMessage(test));

			listTestInstances.add(testInstance);
		}

		return listTestInstances;

	}

	/*
	 * charge n JUnit xml report et les encapsule dans un IReportTestSuite
	 * contenant une ou plusieur JUnitTestSuite
	 */
	private IReportTestSuite buildAgregatedTestSuite(String groupName, String rootName, List<File> files)
			throws JUnitHistoryException {

		if (files == null || files.isEmpty()) {
			return null;
		}
		log.config("Current root name: " + rootName + " - count: " + files.size());
		File firstFile = files.get(0);
		File parentDirectory = firstFile.getParentFile();
		String currentFilename = null;
		try {
			// JUnitTestSuite <--> xml file
			List<JUnitTestSuite> listsuite = new ArrayList<>(files.size());
			for (File file : files) {
				currentFilename = file.getAbsolutePath();
				listsuite.add(this.load(file));
			}
			return new AgregatedJUnitTestSuite(groupName, rootName, parentDirectory, listsuite);

		} catch (Exception ex) {
			String errorMessage = "Error in buildAgregatedTestSuite() with file " + currentFilename;
			throw new JUnitHistoryException(errorMessage + ((ex.getMessage() != null) ? ex.getMessage() : ""));
		}
	}

	/**
	 * Contruit si il y a lieu le message d'erreur avec stacktrace et output
	 * logs
	 * 
	 * @param testCase
	 * @return
	 */
	private DbTestMessage buildDbTestMessage(JUnitTestCase testCase) {

		DbTestMessage testMessage = null;

		JUnitFailureOrError foe = null;
		if (testCase.getFailure() != null) {
			foe = testCase.getFailure();
		} else if (testCase.getError() != null) {
			foe = testCase.getError();
		}
		if (foe != null) {
			testMessage = new DbTestMessage(foe.getType());
			testMessage.setMessage(this.getMessage(foe));

			if (foe.getStack() != null && !foe.getStack().isEmpty()) {
				testMessage.setStackTrace(foe.getStack());
			}
			if (testCase.getLogs() != null && !testCase.getLogs().isEmpty()) {
				testMessage.setOutputLog(testCase.getLogs());
			}
		}

		return testMessage;
	}

	private String getMessage(JUnitFailureOrError foe) {
		/*
		 * trick: message:
		 * "[Remote test failed at bla bla bla] Dependency testPause(com.francetelecom.orangetv.gwt.stb.test.GwtTestTimeShift) not passed: ERROR"
		 * stack: "junit.framework.AssertionFailedError: Remote test failed at
		 * bla bla bla\n "rest of stack" not easy to find the end of the
		 * "Remote test failed ..." message unless
		 */
		String message = foe.getMessage();
		if (message.startsWith("Remote test failed at")) {
			String stack = foe.getStack();
			int idxStart = stack.indexOf(':');
			if (idxStart > 0) {
				idxStart += 2;
				int idxEnd = stack.indexOf('\n', idxStart);
				if (idxEnd > 0) {
					String sub = stack.substring(idxStart, idxEnd);
					if (sub.startsWith("Remote test failed at")) {
						// we got it: strip string from message
						return message.length() <= sub.length() ? "" : message.substring(sub.length());

					}
				}
			}
		}
		return (message.length() >= 255) ? message.substring(0, 255) : message;
	}

	/*
	 * charge le rapport JUnit xml en mémoire et l'encapsule dans la class
	 * JUnitTestSuite xml ----> JUnitTestSuite
	 */
	private JUnitTestSuite load(File xml) throws JAXBException, SAXException, IOException, ParserConfigurationException {
		JAXBContext jaxbContext = JAXBContext.newInstance(JUnitTestSuite.class);
		Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
		JUnitTestSuite suite = (JUnitTestSuite) unmarshaller.unmarshal(xml);
		suite.setFile(xml);
		return suite;
	}

	/*
	 * Recherche le group auquel appartient la testSuite. Si aucun trouvé,
	 * return null
	 */
	private DbTestSuiteGroup findTestSuiteGroup(String xmlFileName) throws JUnitHistoryException {

		if (DaoTestSuiteGroup.get().listGroups(true) == null) {
			return null;
		}
		for (DbTestSuiteGroup group : DaoTestSuiteGroup.get().listGroups(true)) {
			if (xmlFileName.startsWith(group.getPrefix())) {
				return group;
			}
		}
		return null;
	}
}
