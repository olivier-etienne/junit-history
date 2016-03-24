package com.francetelecom.orangetv.junithistory.client.service;

import java.util.List;

import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
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
import com.francetelecom.orangetv.junithistory.shared.vo.VoUserForEdit;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUserForGrid;
import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;

/**
 * The client-side stub for the RPC service.
 */
@RemoteServiceRelativePath("report")
public interface IGwtJUnitHistoryService extends RemoteService {

	/**
	 * Suppression d'une tclass category
	 * 
	 * @param categoryId
	 * @return
	 * @throws JUnitHistoryException
	 */
	public boolean deleteCategory(int categoryId) throws JUnitHistoryException;

	/**
	 * Suppression d'un group
	 * 
	 * @param groupId
	 * @return
	 * @throws JUnitHistoryException
	 */
	public boolean deleteTestGroup(int groupId) throws JUnitHistoryException;

	/**
	 * Suppression d'un utilisateur
	 * 
	 * @param userId
	 * @return
	 * @throws JUnitHistoryException
	 */
	public boolean deleteTestUser(int userId) throws JUnitHistoryException;

	/**
	 * Get Group info for edition
	 * 
	 * @param groupId
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoGroupForEdit getGroupForEdit(int groupId) throws JUnitHistoryException;

	/**
	 * Validation de la modification d'un group de stb
	 * 
	 * @param groupToUpdate
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoDatasValidation validTestGroup(VoGroupForEdit groupToUpdate) throws JUnitHistoryException;

	/**
	 * Miser à jour des information d'un group de stb
	 * 
	 * @param groupToUpdate
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoDatasValidation createOrUpdateTestGroup(VoGroupForEdit groupToUpdate) throws JUnitHistoryException;

	/**
	 * Get Category info for edition
	 * 
	 * @param categoryId
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoCategoryForEdit getCategoryForEdit(int categoryId) throws JUnitHistoryException;

	/**
	 * Validation de la modification d'une category
	 * 
	 * @param categoryToUpdate
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoDatasValidation validTestCategory(VoCategoryForEdit categoryToUpdate) throws JUnitHistoryException;

	/**
	 * Miser à jour des information d'une tclass category
	 * 
	 * @param categoryToUpdate
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoDatasValidation createOrUpdateTestCategory(VoCategoryForEdit categoryToUpdate)
			throws JUnitHistoryException;

	/**
	 * Get user info for edition
	 * 
	 * @param userId
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoUserForEdit getUserForEdit(int userId) throws JUnitHistoryException;

	/**
	 * Validation de la modification d'un TestUser
	 * 
	 * @param userToUpdate
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoDatasValidation validTestUser(VoUserForEdit userToUpdate) throws JUnitHistoryException;

	/**
	 * Miser à jour des information d'un TestUser
	 * 
	 * @param userToUpdate
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoDatasValidation createOrUpdateTestUser(VoUserForEdit userToUpdate) throws JUnitHistoryException;

	/**
	 * Validation des données de l'utilisateur avant l'ajout dans l'historique
	 * 
	 * @param dtoDatas
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoDatasValidation validSingleReport(VoSingleReportData dtoDatas) throws JUnitHistoryException;

	/**
	 * Authentification de l'utilisateur dans un des trois profil (admin,
	 * qualif, anonymus)
	 * 
	 * @param login
	 * @param pwd
	 * @return
	 */
	public String authenticateUserProfile(String login, String pwd);

	/**
	 * Retourne le profil de l'utilisateur (cookies de session)
	 * 
	 * @return
	 */
	public String getCurrentUserProfile();

	/**
	 * Après upload d'un fichier Gwt JUnit report xml, construit la page html du
	 * rapport et retourne l'url
	 * 
	 * @param singleReport
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoSingleReportResponse showHtmlSingleReport(VoSingleReportData singleReport) throws JUnitHistoryException;

	/**
	 * Depuis l'historique construit les pages html du group
	 * 
	 * @param groupId
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoListReportResponse showHtmlListReportForGroup(int groupId) throws JUnitHistoryException;

	/**
	 * Depuis la page historique, demander la construction du rapport individuel
	 * Html à partir d'une suite existante en base
	 * 
	 * @param suiteId
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoSingleReportResponse showHtmlSingleReport(int suiteId) throws JUnitHistoryException;

	/**
	 * Ajout d'un rapport xml (1 ou plusieurs fichiers) à l'historique
	 * 
	 * <ul>
	 * <li>copy et renommage des fichiers
	 * <li>sauvegarde en base de donnée de la suite
	 * </ul>
	 * 
	 * @param singleReportDatas
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoSingleReportResponse addSingleReportToHistory(VoSingleReportData singleReportDatas)
			throws JUnitHistoryException;

	/**
	 * Supprime une test suite (single report) de l'historique
	 * 
	 * @param suiteId
	 * @return
	 * @throws JUnitHistoryException
	 */
	public boolean deleteTestSuiteFromHistory(int suiteId) throws JUnitHistoryException;

	/**
	 * Signal envoye avant un nouvel upload dans la session
	 * 
	 * @throws JUnitHistoryException
	 */
	public void beforeStartNewUpload() throws JUnitHistoryException;

	/**
	 * Signal envoye après la fin d'un upload dans la session
	 * 
	 * @throws JUnitHistoryException
	 */

	public void afterUploadEnd() throws JUnitHistoryException;

	/**
	 * Retourne la listes nécessaires à l'initialisation de la vue
	 * DefectView
	 * 
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoInitDefectDatas getVoInitDefectDatas() throws JUnitHistoryException;

	/**
	 * Retourne la listes nécessaires à l'initialisation de la vue
	 * SingleReportView
	 * 
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoInitSingleReportDatas getVoInitSingleReportDatas() throws JUnitHistoryException;

	/**
	 * Protection sur la vue SingleReportView en fonction du profile utilsateru
	 * 
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoSingleReportProtection getSingleReportProtection() throws JUnitHistoryException;

	/**
	 * Retourne la listes nécessaires à l'initialisation de la vue
	 * HistoricReportView
	 * 
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoInitHistoricReportDatas getVoInitHistoricReportDatas() throws JUnitHistoryException;

	/**
	 * Retourne toutes les tests suites d'un group
	 * 
	 * @param groupId
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoListSuiteForGrid getListTestSuiteByGroup(int groupId) throws JUnitHistoryException;

	/**
	 * Retourne la liste des utilisateurs
	 * 
	 * @return
	 * @throws JUnitHistoryException
	 */
	public List<VoUserForGrid> getListUsers() throws JUnitHistoryException;

	/**
	 * Retourne la liste des TestSuiteGroups
	 * 
	 * @return
	 * @throws JUnitHistoryException
	 */
	public List<VoGroupForGrid> getListGroups() throws JUnitHistoryException;

	/**
	 * Retourne la liste des TClassCategories
	 * 
	 * @return
	 * @throws JUnitHistoryException
	 */
	public List<VoCategoryForGrid> getListTestClassCategories() throws JUnitHistoryException;

	/**
	 * Retourne les infos necessaires a l'initiation de la vue EditReportView
	 * 
	 * @param suiteId
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoEditReportDatas getEditTestSuiteDatas(int suiteId) throws JUnitHistoryException;

	/**
	 * Mise à jour de certaines informations de la suite
	 * 
	 * @param suiteToUpdate
	 * @throws JUnitHistoryException
	 */
	public VoDatasValidation updateTestSuiteInfo(VoTestSuiteForEdit suiteToUpdate) throws JUnitHistoryException;

	/**
	 * Validation avant la mise à jour de certaines informations de la suite
	 * 
	 * @param suiteToUpdate
	 * @throws JUnitHistoryException
	 */
	public VoDatasValidation validTestSuiteInfo(VoTestSuiteForEdit suiteToUpdate) throws JUnitHistoryException;

	/**
	 * Get the list of distinct testname for a group and a search input
	 * 
	 * @param vo
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoResultSearchTestDatas searchDefectTestList(VoSearchDefectDatas vo) throws JUnitHistoryException;

	/**
	 * Retourne la liste des tests de meme nom pour un groupId donne
	 * 
	 * @param vo
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoListTestsSameNameDatas getListTestsForGroupIdTClassIdAndTestName(VoSearchDefectDatas vo)
			throws JUnitHistoryException;

	/**
	 * Retourne la liste des Tclass (id & name) pour un test de nom donne et un
	 * groupId
	 * 
	 * @param vo
	 * @return
	 * @throws JUnitHistoryException
	 */
	public List<VoIdName> listTClassesForGroupIdAndTestName(VoSearchDefectDatas vo) throws JUnitHistoryException;

	/**
	 * Retourne le commentaire de test a editer avec infos complementaire
	 * 
	 * @param testId
	 * @param tcommentId
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoEditTCommentDatas getTCommentDatas(int testId, int tcommentId) throws JUnitHistoryException;

	/**
	 * Validation avant enregistrement d'un TComment de test
	 * 
	 * @param voTComment
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoDatasValidation validTComment(VoTestCommentForEdit voTComment) throws JUnitHistoryException;

	/**
	 * Creation or update d'un TComment de test
	 * 
	 * @param voTComment
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoDatasValidation createOrUpdateTComment(VoTestCommentForEdit voTComment) throws JUnitHistoryException;

	/**
	 * Delete comment from test
	 * 
	 * @param tcommentId
	 * @return
	 * @throws JUnitHistoryException
	 */
	public boolean deleteTComment(int tcommentId) throws JUnitHistoryException;
}
