package com.francetelecom.orangetv.junithistory.server;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.francetelecom.orangetv.junithistory.client.service.IGwtJUnitHistoryService;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestSuiteGroup;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestUser;
import com.francetelecom.orangetv.junithistory.server.dao.IDbEntry;
import com.francetelecom.orangetv.junithistory.server.dto.DtoTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.manager.AdminManager;
import com.francetelecom.orangetv.junithistory.server.manager.AnalysisManager;
import com.francetelecom.orangetv.junithistory.server.manager.DaoManager;
import com.francetelecom.orangetv.junithistory.server.manager.PathManager;
import com.francetelecom.orangetv.junithistory.server.manager.ProfileManager;
import com.francetelecom.orangetv.junithistory.server.manager.ShowHtmlManager;
import com.francetelecom.orangetv.junithistory.server.manager.ShowHtmlManager.HttpAddress;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteGroup;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestUser;
import com.francetelecom.orangetv.junithistory.server.service.IMyServices;
import com.francetelecom.orangetv.junithistory.server.service.ShowHtmlServlet;
import com.francetelecom.orangetv.junithistory.server.util.FileUtils;
import com.francetelecom.orangetv.junithistory.server.util.ListLines;
import com.francetelecom.orangetv.junithistory.server.util.SessionHelper;
import com.francetelecom.orangetv.junithistory.shared.UserProfile;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoCategoryForEdit;
import com.francetelecom.orangetv.junithistory.shared.vo.VoCategoryForGrid;
import com.francetelecom.orangetv.junithistory.shared.vo.VoDatasValidation;
import com.francetelecom.orangetv.junithistory.shared.vo.VoEditReportDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoEditTCommentDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoGroupForEdit;
import com.francetelecom.orangetv.junithistory.shared.vo.VoGroupForGrid;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdName;
import com.francetelecom.orangetv.junithistory.shared.vo.VoInitDefectDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoInitHistoricReportDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoInitSingleReportDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoItemProtection;
import com.francetelecom.orangetv.junithistory.shared.vo.VoListReportResponse;
import com.francetelecom.orangetv.junithistory.shared.vo.VoListSuiteForGrid;
import com.francetelecom.orangetv.junithistory.shared.vo.VoListTestsSameNameDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoResultSearchTestDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSearchDefectDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSingleReportData;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSingleReportProtection;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSingleReportResponse;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestCommentForEdit;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestSuiteForEdit;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestSuiteForGrid;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUser;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUserForEdit;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUserForGrid;
import com.google.gwt.user.server.rpc.RemoteServiceServlet;

/**
 * The server-side implementation of the RPC service.
 */
@SuppressWarnings("serial")
public class GwtJUnitHistoryServiceImpl extends RemoteServiceServlet implements IGwtJUnitHistoryService, IMyServices {

	private static final Logger log = Logger.getLogger(GwtJUnitHistoryServiceImpl.class.getName());

	@Override
	public void init(ServletConfig config) throws ServletException {
		log.info("init()");
		super.init(config);

		// restoration des fichiers de l'historique
		new Thread() {

			@Override
			public void run() {
				try {
					restoreHistoryFiles();
				} catch (JUnitHistoryException e) {
					log.severe("UNABLE TO RESTORE HISTORY FILES!!! : " + e.getMessage());
				}
			}

		}.start();

	}

	/**
	 * Quand on reinstall un war on perd tout le contenu du repertoire /history
	 * Il faut reconstituer l'arborescence et recuperer les fichiers depuis le
	 * repertoire de sauvegarde
	 * 
	 * @throws JUnitHistoryException
	 */
	private void restoreHistoryFiles() throws JUnitHistoryException {

		log.info("restoreHistoryFiles()...");
		String historyPathname = PathManager.get()
				.getContextCompletePathname(PathManager.get().getHistoryName(), false);
		File backupDir = PathManager.get().getBackupDir();
		File historyDir = new File(historyPathname);

		// pas de repertoire history (on vient juste de faire une mise a jour)
		if (!FileUtils.verifyDirectory(historyDir, false)) {

			log.warning("=============================================");
			log.warning("/history missing....");
			// on cree l'arborescence /history/xml
			if (FileUtils.verifyDirectory(historyDir, true)) {

				// on recupere les fichiers sauvegardes si le repertoire
				// sauvegarde existe
				if (FileUtils.verifyDirectory(backupDir, false)) {

					log.warning("copy files from " + backupDir.getAbsolutePath());
					String xmlPathname = PathManager.get().getContextCompletePathname(
							PathManager.get().getHistoryXmllName());
					List<File> filesToCopy = FileUtils.getListAllFiles(backupDir);
					FileUtils.copyFilesToTargetDir(filesToCopy, new File(xmlPathname), true);
				}
			}
			log.warning("=============================================");
		}

	}

	// ----------------------------------------------- implementing

	// ================================================
	// ADMIN TCLASS CATEGORIES
	// ================================================
	@Override
	public boolean deleteCategory(int categoryId) throws JUnitHistoryException {
		this.verifProfileAdminForPageAccess();
		return AdminManager.get().deleteCategory(categoryId);
	}

	@Override
	public List<VoCategoryForGrid> getListTestClassCategories() throws JUnitHistoryException {
		this.verifProfileAdminForPageAccess();
		return AdminManager.get().getListTestClassCategories();
	}

	@Override
	public VoCategoryForEdit getCategoryForEdit(int categoryId) throws JUnitHistoryException {
		this.verifProfileAdminForPageAccess();
		return AdminManager.get().getCategoryForEdit(categoryId);
	}

	@Override
	public VoDatasValidation validTestCategory(VoCategoryForEdit categoryToUpdate) throws JUnitHistoryException {
		this.verifProfileAdminForPageAccess();
		return AdminManager.get().validTestCategory(categoryToUpdate);
	}

	@Override
	public VoDatasValidation createOrUpdateTestCategory(VoCategoryForEdit categoryToUpdate)
			throws JUnitHistoryException {
		this.verifProfileAdminForPageAccess();
		return AdminManager.get().createOrUpdateTestCategory(categoryToUpdate, this.getSessionId());
	}

	// ================================================
	// ADMIN TCLASS USERS
	// ================================================

	@Override
	public boolean deleteTestUser(int userId) throws JUnitHistoryException {
		this.verifProfileAdminForPageAccess();
		return AdminManager.get().deleteTestUser(userId);
	}

	@Override
	public List<VoUserForGrid> getListUsers() throws JUnitHistoryException {

		this.verifProfileAdminForPageAccess();
		return AdminManager.get().getListUsers();
	}

	@Override
	public VoUserForEdit getUserForEdit(int userId) throws JUnitHistoryException {
		this.verifProfileAdminForPageAccess();

		return AdminManager.get().getUserForEdit(userId);
	}

	@Override
	public VoDatasValidation validTestUser(VoUserForEdit userToUpdate) throws JUnitHistoryException {
		this.verifProfileAdminForPageAccess();
		return AdminManager.get().validTestUser(userToUpdate);
	}

	@Override
	public VoDatasValidation createOrUpdateTestUser(VoUserForEdit userToUpdate) throws JUnitHistoryException {
		this.verifProfileAdminForPageAccess();
		return AdminManager.get().createOrUpdateTestUser(userToUpdate);
	}

	// ================================================
	// ADMIN TCLASS GROUPS
	// ================================================
	@Override
	public boolean deleteTestGroup(int groupId) throws JUnitHistoryException {
		this.verifProfileAdminForPageAccess();
		return AdminManager.get().deleteTestGroup(groupId);
	}

	@Override
	public List<VoGroupForGrid> getListGroups() throws JUnitHistoryException {
		this.verifProfileAdminForPageAccess();
		return AdminManager.get().getListGroups();
	}

	@Override
	public VoGroupForEdit getGroupForEdit(int groupId) throws JUnitHistoryException {
		this.verifProfileAdminForPageAccess();
		return AdminManager.get().getGroupForEdit(groupId);
	}

	@Override
	public VoDatasValidation validTestGroup(VoGroupForEdit groupToUpdate) throws JUnitHistoryException {
		this.verifProfileAdminForPageAccess();
		return AdminManager.get().validTestGroup(groupToUpdate);
	}

	@Override
	public VoDatasValidation createOrUpdateTestGroup(VoGroupForEdit groupToUpdate) throws JUnitHistoryException {
		this.verifProfileAdminForPageAccess();
		return AdminManager.get().createOrUpdateTestGroup(groupToUpdate);
	}

	// ================================================
	// PROFILE
	// ================================================

	@Override
	public String authenticateUserProfile(String login, String pwd) {

		UserProfile UserProfile = ProfileManager.get().getUserProfileFromCredential(login, pwd);
		this.getSession().setAttribute(KEY_SESSION_USER_PROFIL, UserProfile);

		return UserProfile.name();

	}

	@Override
	public String getCurrentUserProfile() {

		UserProfile userProfile = ProfileManager.get().getSessionUserProfile(getThreadLocalRequest());
		return (userProfile == null) ? UserProfile.anybody.name() : userProfile.name();

	}

	// ================================================
	// LIST SUITE
	// ================================================

	@Override
	public VoListSuiteForGrid getListTestSuiteByGroup(int groupId) throws JUnitHistoryException {

		VoListSuiteForGrid response = new VoListSuiteForGrid();
		if (groupId == IDbEntry.ID_UNDEFINED) {
			return response;
		}
		URL urlToShare = ShowHtmlServlet.get().buildUrlForGroupId(groupId, getHttpAddress(), false);
		response.setUrlToShare(urlToShare == null ? null : urlToShare.toExternalForm());

		List<DbTestSuiteInstance> listSuites = DaoTestSuiteInstance.get().listSuitesByGroup(groupId);
		if (listSuites != null) {

			final boolean atLeastManager = this.isSessionAtLeastManager();

			final boolean atLeastAdmin = this.isSessionAtLeastAdmin();

			List<VoTestSuiteForGrid> listVos = new ArrayList<>(listSuites.size());

			HttpAddress httpAddress = this.getHttpAddress();
			for (DbTestSuiteInstance suite : listSuites) {

				VoTestSuiteForGrid vo = this.buildVoTestSuiteForGrid(suite, atLeastAdmin);

				URL url = ShowHtmlServlet.get().buildUrlForSuiteId(suite.getId(), httpAddress, false);
				vo.setUrlToShare(url == null ? null : url.toExternalForm());

				listVos.add(vo);
			}

			VoItemProtection protection = new VoItemProtection();
			protection.setCanDelete(atLeastManager);
			protection.setCanEdit(atLeastManager);

			response.setListTestSuites(listVos);
			response.setProtection(protection);
			return response;
		}

		return response;
	}

	private VoTestSuiteForGrid buildVoTestSuiteForGrid(DbTestSuiteInstance suite, boolean profileAdmin) {

		final VoTestSuiteForGrid vo = new VoTestSuiteForGrid(suite.getId(), suite.getName());

		Date date = suite.getDate();
		vo.setDate(date == null ? "" : DATE_FORMAT.format(date));
		vo.setFirmware(suite.getFirmware());
		vo.setIptvkit(suite.getIptvkit());
		vo.setReadonly(profileAdmin ? false : suite.isReadonly());

		DbTestUser user = suite.getUser();
		vo.setUser((user == null) ? "" : user.getName());

		return vo;
	}

	private boolean isSessionAtLeastAdmin() {
		return ProfileManager.get().isSessionAtLeastUserProfile(UserProfile.admin, this.getThreadLocalRequest());
	}

	private boolean isSessionAtLeastManager() {
		return ProfileManager.get().isSessionAtLeastUserProfile(UserProfile.manager, this.getThreadLocalRequest());
	}

	private VoTestSuiteForEdit buildVoTestSuiteForEdit(DbTestSuiteInstance suite) {

		boolean atLeastManager = this.isSessionAtLeastManager();
		boolean profileAdmin = atLeastManager && this.isSessionAtLeastAdmin();

		VoTestSuiteForEdit vo = new VoTestSuiteForEdit(suite.getId(), suite.getName());
		vo.setDate(suite.getDate());
		vo.setFirmware(suite.getFirmware());
		vo.setIptvkit(suite.getIptvkit());

		// update enabled:
		// suite !readonly && atleastManager
		// suite readonly && admin
		boolean canUpdate = (suite.isReadonly() && profileAdmin) || (!suite.isReadonly() && atLeastManager);
		vo.setReadOnly(!canUpdate);

		vo.setComment(suite.getComment());

		vo.setUserId(suite.getUser() != null ? suite.getUser().getId() : IVo.ID_UNDEFINED);

		return vo;
	}

	// ================================================
	// INIT VIEWS
	// ================================================
	@Override
	public VoSingleReportProtection getSingleReportProtection() throws JUnitHistoryException {
		final boolean atLeastManager = this.isSessionAtLeastManager();

		VoSingleReportProtection protection = new VoSingleReportProtection();
		protection.setCanAddToHistory(atLeastManager);
		return protection;
	}

	@Override
	public VoInitSingleReportDatas getVoInitSingleReportDatas() throws JUnitHistoryException {

		VoInitSingleReportDatas vo = new VoInitSingleReportDatas();
		this.populateListGroupsFromBdd(vo.getListGroups());

		vo.getListUsers().addAll(this.buildListVoTesters(false));
		vo.setProtection(this.getSingleReportProtection());

		return vo;

	}

	@Override
	public VoInitHistoricReportDatas getVoInitHistoricReportDatas() throws JUnitHistoryException {

		VoInitHistoricReportDatas vo = new VoInitHistoricReportDatas();
		this.populateListGroupsFromBdd(vo.getListGroups());
		return vo;
	}

	@Override
	public VoInitDefectDatas getVoInitDefectDatas() throws JUnitHistoryException {

		VoInitDefectDatas vo = new VoInitDefectDatas();
		this.populateListGroupsFromBdd(vo.getListGroups());

		return vo;
	}

	// ================================================
	// UPLOAD
	// ================================================

	@Override
	public void afterUploadEnd() throws JUnitHistoryException {
		this.getSession().setAttribute(KEY_SESSION_EVENT_UPLOAD_ENDED, Boolean.TRUE);
	}

	@Override
	public void beforeStartNewUpload() throws JUnitHistoryException {
		this.getSession().setAttribute(KEY_SESSION_EVENT_START_NEW_UPLOAD, Boolean.TRUE);
	}

	// ================================================
	// SUITE
	// ================================================

	@Override
	public boolean deleteTestSuiteFromHistory(int suiteId) throws JUnitHistoryException {

		DbTestSuiteInstance suite = DaoTestSuiteInstance.get().getById(suiteId);

		// suppression de la suite en base
		boolean result = DaoManager.get().deleteTestSuite(suiteId, this.getSessionId());

		if (result) {
			// TODO faire une sauvegarde des fichiers et les supprimer du rep
			// /history/xml
			// pour l'instant on les supprime seulement
			String suiteName = suite.getName();
			String regex = FileUtils.createRegexXmlXmlPartLogTxt(suiteName);

			// /history/xml
			File historyHtmlDir = new File(PathManager.get().getContextCompletePathname(
					PathManager.get().getHistoryXmllName()));
			final List<File> listFileToDelete = FileUtils.getListFile(historyHtmlDir, regex);
			FileUtils.deleteListFiles(listFileToDelete);
		}
		return result;
	}

	@Override
	public VoDatasValidation validTestSuiteInfo(VoTestSuiteForEdit suiteToUpdate) throws JUnitHistoryException {

		if (!this.isSessionAtLeastManager()) {
			throw new JUnitHistoryException("User must be at least manager!");
		}

		DbTestSuiteInstance dbSuite = DaoTestSuiteInstance.get().getById(suiteToUpdate.getId());
		// validation
		return this.getTestSuiteValidation(dbSuite, suiteToUpdate);

	}

	@Override
	public VoDatasValidation updateTestSuiteInfo(VoTestSuiteForEdit suiteToUpdate) throws JUnitHistoryException {

		// validation
		VoDatasValidation validation = this.validTestSuiteInfo(suiteToUpdate);
		if (!validation.isValid()) {
			return validation;
		}

		// update
		if (DaoTestSuiteInstance.get().updateSuiteInfo(suiteToUpdate)) {

			// Get updated suite
			DbTestSuiteInstance suite = DaoTestSuiteInstance.get().getById(suiteToUpdate.getId());
			File xmlHistoryDir = new File(PathManager.get().getContextCompletePathname(
					PathManager.get().getHistoryXmllName()));
			this.appendDescriptionToReport(suite, xmlHistoryDir, true);
		}

		return validation;
	}

	@Override
	public VoEditReportDatas getEditTestSuiteDatas(int suiteId) throws JUnitHistoryException {

		VoEditReportDatas voEditReportDatas = new VoEditReportDatas();
		// en premier lieu liste des users
		voEditReportDatas.setListUsers(this.buildListVoTesters(true));

		// ensuite on recupere les informations sur la suite
		DbTestSuiteInstance suite = DaoTestSuiteInstance.get().getById(suiteId);
		if (suite == null) {
			throw new JUnitHistoryException("Suite " + suiteId + " doesn't exists!");
		}

		VoTestSuiteForEdit voTestSuiteForEdit = this.buildVoTestSuiteForEdit(suite);
		voEditReportDatas.setSuiteForEdit(voTestSuiteForEdit);

		return voEditReportDatas;
	}

	@Override
	public VoSingleReportResponse addSingleReportToHistory(VoSingleReportData singleReportDatas)
			throws JUnitHistoryException {

		log.config("addSingleReportToHistory()");

		VoDatasValidation voValidation = this.validSingleReport(singleReportDatas);
		if (!voValidation.isValid()) {
			throw new JUnitHistoryException("Unvalid datas!");
		}

		DtoTestSuiteInstance dtoDatas = ShowHtmlManager.get().buildDtoSuiteInstanceFromUploadPath(singleReportDatas,
				this.getSession());

		DbTestSuiteInstance suite = dtoDatas.getTestSuiteInstance();
		DbTestSuiteGroup group = suite.getTestSuiteGroup();

		// on construit le nom de la suite à partir du nom du group et de la
		// version du firmware. On verifie son unicite en base
		String suiteName = group.getPrefix() + suite.getFirmware();
		if (DaoTestSuiteInstance.get().countByName(suiteName) > 0) {
			throw new JUnitHistoryException("The suite name: " + suiteName + " exists already!");
		}
		suite.setName(suiteName);

		// on sauvegarde la suite en base de donnees
		DaoManager.get().saveTestSuite(dtoDatas, this.getSessionId());

		// on recopie tous les fichiers xml, txt et log depuis le rep de upload
		// vers le repertoire history/xml
		// en renommant eventuellement les fichiers
		this.copyXmlFilesFromUploadToHistory(suite);

		// Response
		VoSingleReportResponse response = new VoSingleReportResponse();
		response.setGroupId(group.getId());
		response.setSuiteName(suite.getName());
		return response;
	}

	@Override
	public VoDatasValidation validSingleReport(VoSingleReportData dtoDatas) {

		VoDatasValidation voValidation = new VoDatasValidation();

		if (dtoDatas.getGroupId() == IDbEntry.ID_UNDEFINED) {
			voValidation.getErrorMessages().add("The STB group is required before adding to history");
		}
		if (ValueHelper.isStringEmptyOrNull(dtoDatas.getFirmware())) {
			voValidation.getErrorMessages().add("The firmware is required!");
		}
		if (ValueHelper.isStringEmptyOrNull(dtoDatas.getIptvkit())) {
			voValidation.getErrorMessages().add("IPTVKIT version is required!");
		}
		if (dtoDatas.getDate() == null) {
			voValidation.getErrorMessages().add("The date is required!");
		}
		if (dtoDatas.getUserId() == IDbEntry.ID_UNDEFINED) {
			voValidation.getErrorMessages().add("The user is required!");
		}

		return voValidation;
	}

	/*
	 * Le niveau d'info ne doit pas etre inférieur au niveau exitant en base!
	 */
	private VoDatasValidation getTestSuiteValidation(DbTestSuiteInstance dbSuite, VoTestSuiteForEdit voSuite) {

		VoDatasValidation voValidation = new VoDatasValidation();

		if (!ValueHelper.isStringEmptyOrNull(dbSuite.getIptvkit())) {
			if (ValueHelper.isStringEmptyOrNull(voSuite.getIptvkit())) {
				voValidation.getErrorMessages().add("IPTVKIT version is required!");
			}
		}

		if (dbSuite.getDate() != null) {
			if (voSuite.getDate() == null) {
				voValidation.getErrorMessages().add("The date is required!");
			}
		}

		if (dbSuite.getUser() != null) {
			if (voSuite.getUserId() == IDbEntry.ID_UNDEFINED) {
				voValidation.getErrorMessages().add("The user is required!");
			}
		}

		return voValidation;
	}

	/*
	 * 
	 * on recopie tous les fichiers xml, txt et log depuis le rep de upload vers
	 * le repertoire history/xml en renommant avec le newSuiteName Renommer avec
	 * newSuiteName si non null
	 */
	private void copyXmlFilesFromUploadToHistory(DbTestSuiteInstance suite) throws JUnitHistoryException {

		final String rootName = suite.getName();
		// /history/xml
		String destination = PathManager.get().getContextCompletePathname(PathManager.get().getHistoryXmllName());
		File targetDir = new File(destination);
		if (!FileUtils.verifyDirectory(targetDir, true)) {
			throw new JUnitHistoryException("Unable to create dir: " + destination);
		}

		String uploadedPath = SessionHelper.getStringAttribute(this.getSession(), KEY_SESSION_GWTUPLOADED_DIR, null);
		if (uploadedPath != null) {
			log.config("upload repository in session: " + uploadedPath);

			// on s'assure que le repertoire existe
			File directory = new File(uploadedPath);
			if (FileUtils.verifyDirectory(directory, false)) {

				// Recuperer tous les fichiers sauf html et css
				List<File> allFiles = FileUtils.getListFile(directory, REG_ROOT_XML_XMLPART_LOG_TXT);

				// backup path
				File backupDir = PathManager.get().getBackupDir();

				// for each file
				for (File srcFile : allFiles) {

					String ext = FileUtils.getExtFromFile(srcFile, PATTERN_ROOT_XML_XMLPART_LOG_TXT);
					String newFilename = rootName + ext;
					// faire la copy vers /history/xml
					FileUtils.copyFile(srcFile, new File(targetDir, newFilename));

					// faire une copie de sauvegarde
					if (backupDir != null) {
						FileUtils.copyFile(srcFile, new File(backupDir, newFilename));
					}
				}

				// on complete le fichier descriptif (txt)pour ne pas perdre les
				// metadatas
				this.appendDescriptionToReport(suite, targetDir, true);

				return;
			}
		}
		throw new JUnitHistoryException("uploaded path doesn't exist or no files in directory: " + uploadedPath);
	}

	/*
	 * Complete le fichier txt accompagnant les fichiers xml par une description
	 * de la suite
	 */
	private void appendDescriptionToReport(DbTestSuiteInstance suite, File targetDir, boolean backup)
			throws JUnitHistoryException {

		String filename = suite.getName() + FileUtils.TXT_EXT;
		File fileDesc = new File(targetDir, filename);
		ListLines description = new ListLines();
		description.newLine();
		description.addLine("================================================");
		description.addLine("Description added " + DATE_FORMAT.format(new Date()));
		description.addLine("================================================");
		description.addLine("Suite: ", suite.getName());
		description.addLine("Date: ", (suite.getDate() != null) ? DATE_FORMAT.format(suite.getDate()) : "");
		description.addLine("User: ", (suite.getUser() != null) ? suite.getUser().getName() : "");
		description.addLine("Firmware: ", ValueHelper.getStringValue(suite.getFirmware(), ""));
		description.addLine("IPTVKIT: ", ValueHelper.getStringValue(suite.getIptvkit(), ""));
		description.addLine("Comment: ", ValueHelper.getStringValue(suite.getComment(), ""));
		description.addLine("================================================");
		FileUtils.writeFile(fileDesc, description, true);

		// faire une copie de sauvegarde
		if (backup) {
			File backupDir = PathManager.get().getBackupDir();
			if (backupDir != null) {
				FileUtils.copyFile(fileDesc, new File(backupDir, filename));
			}
		}

	}

	private HttpAddress getHttpAddress() {

		final HttpServletRequest request = this.getThreadLocalRequest();
		return new HttpAddress(request.getLocalPort(), request.getLocalAddr());
	}

	private String getSessionId() {
		return this.getThreadLocalRequest().getSession(true).getId();
	}

	private HttpSession getSession() {
		return this.getThreadLocalRequest().getSession(true);
	}

	// ===============================================
	// DEFECTS
	// ===============================================
	@Override
	public VoResultSearchTestDatas searchDefectTestList(VoSearchDefectDatas vo) throws JUnitHistoryException {

		return AnalysisManager.get().searchDefectTestList(vo);
	}

	@Override
	public VoListTestsSameNameDatas getListTestsForGroupIdTClassIdAndTestName(VoSearchDefectDatas vo)
			throws JUnitHistoryException {

		return AnalysisManager.get().getListTestsForGroupIdTClassIdAndTestName(vo, this.isSessionAtLeastManager());
	}

	@Override
	public List<VoIdName> listTClassesForGroupIdAndTestName(VoSearchDefectDatas vo) throws JUnitHistoryException {
		return AnalysisManager.get().listTClassesForGroupIdAndTestName(vo);
	}

	@Override
	public VoEditTCommentDatas getTCommentDatas(int testId, int tcommentId) throws JUnitHistoryException {

		VoEditTCommentDatas voDatas = AnalysisManager.get().getTCommentDatas(testId, tcommentId,
				this.isSessionAtLeastManager());
		// on complete avec la liste des testers
		voDatas.setListTesters(this.buildListVoTesters(this.isSessionAtLeastAdmin()));

		return voDatas;
	}

	@Override
	public VoDatasValidation validTComment(VoTestCommentForEdit voTComment) throws JUnitHistoryException {

		return AnalysisManager.get().validTComment(voTComment);
	}

	@Override
	public VoDatasValidation createOrUpdateTComment(VoTestCommentForEdit voTComment) throws JUnitHistoryException {
		if (!this.isSessionAtLeastManager()) {
			throw new JUnitHistoryException("User must be manager in order to access this page!");
		}

		return AnalysisManager.get().createOrUpdateTComment(voTComment);
	}

	@Override
	public boolean deleteTComment(int tcommentId) throws JUnitHistoryException {
		if (!this.isSessionAtLeastManager()) {
			throw new JUnitHistoryException("User must be manager in order to access this page!");
		}
		return AnalysisManager.get().deleteTComment(tcommentId);
	}

	// ================================================
	// HTML REPORT
	// ================================================
	@Override
	public VoListReportResponse showHtmlListReportForGroup(int groupId) throws JUnitHistoryException {

		return ShowHtmlManager.get().showHtmlListReportForGroup(groupId, this.getSessionId(), this.getHttpAddress());

	}

	// depuis l'historique
	// /historic/<idsession>/html
	@Override
	public VoSingleReportResponse showHtmlSingleReport(int suiteId) throws JUnitHistoryException {

		return ShowHtmlManager.get().showHtmlSingleReport(suiteId, this.getSessionId(), this.getHttpAddress());
	}

	// après upload
	// /upload/<idSession>/html
	@Override
	public VoSingleReportResponse showHtmlSingleReport(VoSingleReportData singleReportDatas)
			throws JUnitHistoryException {

		return ShowHtmlManager.get().showHtmlSingleReport(singleReportDatas, this.getSession(), this.getHttpAddress());

	}

	private List<VoUser> buildListVoTesters(boolean withAdmin) throws JUnitHistoryException {

		List<VoUser> listVoUsers = null;
		List<DbTestUser> listUsers = DaoTestUser.get().listUsers(withAdmin);
		if (listUsers != null) {

			listVoUsers = new ArrayList<>(listUsers.size());
			for (DbTestUser user : listUsers) {
				listVoUsers.add(user.toVo());
			}
		} else {
			listVoUsers = new ArrayList<>(0);
		}

		return listVoUsers;

	}

	private void verifProfileAdminForPageAccess() throws JUnitHistoryException {
		if (!this.isSessionAtLeastAdmin()) {
			throw new JUnitHistoryException("User must be admin in order to access this page!");
		}

	}

	private void populateListGroupsFromBdd(List<VoIdName> listVoGroupNames) throws JUnitHistoryException {

		List<DbTestSuiteGroup> listGroups = DaoTestSuiteGroup.get().listGroups(true);
		if (listGroups != null) {

			for (DbTestSuiteGroup group : listGroups) {
				listVoGroupNames.add(group.toVo());
			}
			Collections.sort(listVoGroupNames);
		}

	}

	private void dumpRequest() {

		HttpServletRequest request = this.getThreadLocalRequest();
		log.info("getContextPath: " + request.getContextPath());
		log.info("getPathInfo: " + request.getPathInfo());
		log.info("getQueryString: " + request.getQueryString());
		log.info("getRequestURI: " + request.getRequestURI());

		log.info("getLocalAddr: " + request.getLocalAddr());
		log.info("getLocalPort: " + request.getLocalPort());
		log.info("getLocalName: " + request.getLocalName());

	}

}
