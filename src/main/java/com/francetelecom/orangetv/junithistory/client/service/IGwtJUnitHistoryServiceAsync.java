package com.francetelecom.orangetv.junithistory.client.service;

import java.util.List;

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
import com.google.gwt.user.client.rpc.AsyncCallback;

public interface IGwtJUnitHistoryServiceAsync {

	void showHtmlSingleReport(VoSingleReportData singleReport, AsyncCallback<VoSingleReportResponse> callback);

	void beforeStartNewUpload(AsyncCallback<Void> callback);

	void afterUploadEnd(AsyncCallback<Void> callback);

	void getVoInitSingleReportDatas(AsyncCallback<VoInitSingleReportDatas> callback);

	void addSingleReportToHistory(VoSingleReportData singleReportDatas, AsyncCallback<VoSingleReportResponse> callback);

	void getVoInitHistoricReportDatas(AsyncCallback<VoInitHistoricReportDatas> callback);

	void getListTestSuiteByGroup(int groupId, AsyncCallback<VoListSuiteForGrid> callback);

	void showHtmlSingleReport(int suiteId, AsyncCallback<VoSingleReportResponse> callback);

	void deleteTestSuiteFromHistory(int suiteId, AsyncCallback<Boolean> callback);

	void showHtmlListReportForGroup(int groupId, AsyncCallback<VoListReportResponse> callback);

	void authenticateUserProfile(String login, String pwd, AsyncCallback<String> callback);

	void getCurrentUserProfile(AsyncCallback<String> callback);

	void getSingleReportProtection(AsyncCallback<VoSingleReportProtection> callback);

	void validSingleReport(VoSingleReportData dtoDatas, AsyncCallback<VoDatasValidation> callback);

	void getEditTestSuiteDatas(int suiteId, AsyncCallback<VoEditReportDatas> callback);

	void updateTestSuiteInfo(VoTestSuiteForEdit suiteToUpdate, AsyncCallback<VoDatasValidation> callback);

	void getListTestClassCategories(AsyncCallback<List<VoCategoryForGrid>> callback);

	void getListUsers(AsyncCallback<List<VoUserForGrid>> callback);

	void getListGroups(AsyncCallback<List<VoGroupForGrid>> callback);

	void validTestUser(VoUserForEdit userToUpdate, AsyncCallback<VoDatasValidation> callback);

	void createOrUpdateTestUser(VoUserForEdit userToUpdate, AsyncCallback<VoDatasValidation> callback);

	void getUserForEdit(int userId, AsyncCallback<VoUserForEdit> callback);

	void getCategoryForEdit(int categoryId, AsyncCallback<VoCategoryForEdit> callback);

	void validTestCategory(VoCategoryForEdit categoryToUpdate, AsyncCallback<VoDatasValidation> callback);

	void createOrUpdateTestCategory(VoCategoryForEdit categoryToUpdate, AsyncCallback<VoDatasValidation> callback);

	void getGroupForEdit(int groupId, AsyncCallback<VoGroupForEdit> callback);

	void validTestGroup(VoGroupForEdit groupToUpdate, AsyncCallback<VoDatasValidation> callback);

	void createOrUpdateTestGroup(VoGroupForEdit groupToUpdate, AsyncCallback<VoDatasValidation> callback);

	void deleteTestUser(int userId, AsyncCallback<Boolean> callback);

	void deleteTestGroup(int groupId, AsyncCallback<Boolean> callback);

	void deleteCategory(int categoryId, AsyncCallback<Boolean> callback);

	void getVoInitDefectDatas(AsyncCallback<VoInitDefectDatas> callback);

	void searchDefectTestList(VoSearchDefectDatas vo, AsyncCallback<VoResultSearchTestDatas> callback);

	void getListTestsForGroupIdTClassIdAndTestName(VoSearchDefectDatas vo,
			AsyncCallback<VoListTestsSameNameDatas> callback);

	void listTClassesForGroupIdAndTestName(VoSearchDefectDatas vo, AsyncCallback<List<VoIdName>> callback);

	void getTCommentDatas(int testId, int tcommentId, AsyncCallback<VoEditTCommentDatas> callback);

	void validTComment(VoTestCommentForEdit voTComment, AsyncCallback<VoDatasValidation> callback);

	void createOrUpdateTComment(VoTestCommentForEdit voTComment, AsyncCallback<VoDatasValidation> callback);

	void deleteTComment(int tcommentId, AsyncCallback<Boolean> callback);

	void validTestSuiteInfo(VoTestSuiteForEdit suiteToUpdate, AsyncCallback<VoDatasValidation> callback);

}
