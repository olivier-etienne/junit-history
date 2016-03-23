package com.francetelecom.orangetv.junithistory.server.manager;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.server.dao.DaoTestComment;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestInstance;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestMessage;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestUser;
import com.francetelecom.orangetv.junithistory.server.model.DbTestClass;
import com.francetelecom.orangetv.junithistory.server.model.DbTestComment;
import com.francetelecom.orangetv.junithistory.server.model.DbTestInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestMessage;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestUser;
import com.francetelecom.orangetv.junithistory.server.model.LazyTestUser;
import com.francetelecom.orangetv.junithistory.server.util.AbstractValidator;
import com.francetelecom.orangetv.junithistory.shared.TestStatusEnum;
import com.francetelecom.orangetv.junithistory.shared.TestSubStatusEnum;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoDatasValidation;
import com.francetelecom.orangetv.junithistory.shared.vo.VoEditTCommentDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdName;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdUtils;
import com.francetelecom.orangetv.junithistory.shared.vo.VoListTestsSameNameDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoResultSearchTestDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSearchDefectDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestCommentForEdit;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestDistinctName;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestInstanceForEdit;

/**
 * Manager pour la gestion des defects
 * 
 * @author NDMZ2720
 *
 */
public class AnalysisManager implements IManager {

	private static final Logger log = Logger.getLogger(AnalysisManager.class.getName());

	// ---------------------------------- Singleton
	private static AnalysisManager instance;

	public static final AnalysisManager get() {
		if (instance == null) {
			instance = new AnalysisManager();
		}
		return instance;
	}

	private AnalysisManager() {
	}

	// ----------------------------------------
	private TCommentValidator tcommentValidator;

	// ------------------------------------- public methods

	// ==========================================
	// TEST COMMENT
	// ==========================================

	public boolean deleteTComment(int tcommentId) throws JUnitHistoryException {

		return DaoTestComment.get().deleteTComment(tcommentId);
	}

	public VoDatasValidation validTComment(VoTestCommentForEdit tcommentToUpdate) throws JUnitHistoryException {

		boolean createTComment = tcommentToUpdate != null && tcommentToUpdate.isIdUndefined();
		final DbTestComment dbTComment = !createTComment && tcommentToUpdate != null ? DaoTestComment.get().getById(
				tcommentToUpdate.getId()) : null;

		return this.getTCommentValidator().validTComment(dbTComment, tcommentToUpdate, createTComment);
	}

	public VoDatasValidation createOrUpdateTComment(VoTestCommentForEdit tcommentToUpdate) throws JUnitHistoryException {

		boolean createTComment = tcommentToUpdate != null && tcommentToUpdate.isIdUndefined();
		DbTestComment dbTComment = !createTComment && tcommentToUpdate != null ? DaoTestComment.get().getById(
				tcommentToUpdate.getId()) : null;
		VoDatasValidation validation = this.getTCommentValidator().validTComment(dbTComment, tcommentToUpdate,
				createTComment);
		if (validation.isValid()) {

			if (createTComment) {
				dbTComment = new DbTestComment(new Date(), new LazyTestUser(tcommentToUpdate.getTesterId()));
				dbTComment.setTitle(tcommentToUpdate.getTitle());
				dbTComment.setDescription(tcommentToUpdate.getDescription());
			}

			boolean result = createTComment ? DaoTestComment.get().createTComment(dbTComment,
					tcommentToUpdate.getTestId()) : DaoTestComment.get().updateTComment(tcommentToUpdate);
			if (!result) {
				validation.getErrorMessages().add(
						"Failure in " + (createTComment ? "creating" : "updating") + " user " + dbTComment.getId()
								+ "!");
			}
		}
		return validation;

	}

	public VoEditTCommentDatas getTCommentDatas(int testId, int tcommentId) throws JUnitHistoryException {

		VoEditTCommentDatas voDatas = new VoEditTCommentDatas();
		VoTestCommentForEdit voTComment = new VoTestCommentForEdit(tcommentId);
		voDatas.setTCommentForEdit(voTComment);

		// dans tous les cas info test et suite
		voTComment.setTestId(testId);

		DbTestInstance dbTest = DaoTestInstance.get().getById(testId);
		if (dbTest == null) {
			throw new JUnitHistoryException("Test  " + testId + " doen't exists!");
		}

		DbTestClass dbTclass = dbTest.getTClass();
		if (dbTclass == null || dbTclass.isLazy()) {
			throw new JUnitHistoryException("Test tclass  for test " + testId + " doen't exists!");
		}
		dbTclass.setShortName(dbTclass.getName().substring(PACKAGE_TO_CUT_LENGTH));
		voTComment.setTestTitle(dbTest.getTClass().getShortName() + " " + dbTest.getName());

		DbTestSuiteInstance dbSuite = dbTest.getTestSuiteInstance();
		if (dbSuite != null && dbSuite.isLazy()) {
			dbSuite = DaoTestSuiteInstance.get().getById(dbSuite.getId());
		}
		if (dbSuite == null) {
			throw new JUnitHistoryException("Test suite  for test " + testId + " doen't exists!");
		}
		voTComment.setSuiteTitle(dbSuite.getName() + " " + DATE_FORMAT.format(dbSuite.getDate()));

		// comment existant
		if (tcommentId != IVo.ID_UNDEFINED) {

			DbTestComment dbComment = DaoTestComment.get().getById(tcommentId);
			if (dbComment == null) {
				throw new JUnitHistoryException("Test Comment " + tcommentId + " doen't exists!");
			}

			voTComment.setTesterId(dbComment.getTester().getId());
			voTComment.setTitle(dbComment.getTitle());
			voTComment.setDescription(dbComment.getDescription());
		}

		return voDatas;
	}

	// ==========================================
	// TEST CLASSES
	// ==========================================

	public List<VoIdName> listTClassesForGroupIdAndTestName(VoSearchDefectDatas vo) throws JUnitHistoryException {

		// testClass.getName().substring(PACKAGE_TO_CUT_LENGTH)
		List<VoIdName> listTClassShortNames = new ArrayList<>();
		if (vo == null || vo.getGroupId() == IVo.ID_UNDEFINED || vo.getSearch() == null) {
			return listTClassShortNames;
		}

		List<VoIdName> listTClassFullNames = DaoTestInstance.get().listTClassForGroupAndName(vo.getGroupId(),
				vo.getSearch());
		if (listTClassFullNames == null) {
			return listTClassShortNames;
		}

		// for each TClass
		for (VoIdName tclassFullName : listTClassFullNames) {
			listTClassShortNames.add(new VoIdName(tclassFullName.getId(), tclassFullName.getName().substring(
					PACKAGE_TO_CUT_LENGTH)));
		}

		return listTClassShortNames;
	}

	// ==========================================
	// TESTS
	// ==========================================

	public VoResultSearchTestDatas searchDefectTestList(VoSearchDefectDatas vo) throws JUnitHistoryException {

		VoResultSearchTestDatas listTestNames = new VoResultSearchTestDatas();
		if (vo == null || vo.getGroupId() == IVo.ID_UNDEFINED || vo.getSearch() == null) {
			return listTestNames;
		}
		final List<VoIdName> listDBDistinctTestNames = DaoTestInstance.get()
				.searchDistinctNamesForGroupAndContainsName(vo.getGroupId(), vo.getSearch());

		if (listDBDistinctTestNames == null) {
			return listTestNames;
		}

		// for each test name full name >> short name
		for (VoIdName testDistinctName : listDBDistinctTestNames) {
			listTestNames.addTest(new VoTestDistinctName(testDistinctName.getName()));
		}

		return listTestNames;
	}

	public VoListTestsSameNameDatas getListTestsForGroupIdTClassIdAndTestName(VoSearchDefectDatas vo)
			throws JUnitHistoryException {

		final VoListTestsSameNameDatas listTests = new VoListTestsSameNameDatas();
		if (vo == null || vo.getGroupId() == IVo.ID_UNDEFINED || vo.getSearch() == null) {
			return listTests;
		}

		final List<DbTestInstance> listDbTestInstances = DaoTestInstance.get().listTestsForGroupIdTClassIdAndTestName(
				vo.getGroupId(), vo.getTClassId(), vo.getSearch());
		if (listDbTestInstances == null) {
			return listTests;
		}

		// for each test

		// recuperer tous les users
		final List<DbTestUser> listTesters = DaoTestUser.get().listUsers(true);
		final Map<Integer, DbTestUser> mapId2Tester = VoIdUtils.getMapId2Item(listTesters);
		for (DbTestInstance test : listDbTestInstances) {

			VoTestInstanceForEdit voTest = new VoTestInstanceForEdit(test.getId(), test.getName());
			TestSubStatusEnum status = test.getStatus();
			boolean success = status.getStatus() == TestStatusEnum.Success;
			boolean skipped = status.getStatus() == TestStatusEnum.Skipped;

			voTest.setStatus(status.name());
			voTest.setSuccess(success);
			voTest.setSkipped(skipped);

			// suite
			DbTestSuiteInstance testSuite = test.getTestSuiteInstance();
			voTest.setSuiteName(testSuite.getName());
			voTest.setSuiteDate(DATE_FORMAT.format(testSuite.getDate()));

			// message
			DbTestMessage message = (skipped) ? null : test.getMessage();
			if (message != null && message.isLazy()) {
				message = DaoTestMessage.get().getById(message.getId());
			}

			if (message != null) {

				voTest.setType(message.getType());
				voTest.setMessage(message.getMessage());
				voTest.setOutputLog(message.getOutputLog());
				voTest.setStackTrace(message.getStackTrace());
			}

			// comment
			DbTestComment tcomment = test.getComment();
			if (tcomment != null && tcomment.isLazy()) {
				tcomment = DaoTestComment.get().getById(tcomment.getId());

				if (tcomment.getTester() != null && tcomment.getTester().isLazy()) {
					tcomment.setTester(mapId2Tester.get(tcomment.getTester().getId()));
				}
			}
			if (tcomment != null) {

				voTest.setTcommentId(tcomment.getId());

				StringBuffer sb = new StringBuffer();

				// 1ere ligne
				sb.append("Tester: " + tcomment.getTester().getName());
				sb.append(" - date: " + DATE_FORMAT.format(tcomment.getDateModification()));
				sb.append(" (creation: " + DATE_FORMAT.format(tcomment.getDateCreation() + ") \n\n"));

				// titre
				sb.append("Title: " + tcomment.getTitle() + "\n\n");

				// description
				sb.append(tcomment.getDescription());

				voTest.setTcomment(sb.toString());

			}

			listTests.addTestInstance(voTest);
		}

		return listTests;

	}

	private TCommentValidator getTCommentValidator() {

		if (this.tcommentValidator == null) {
			this.tcommentValidator = new TCommentValidator();
		}
		return this.tcommentValidator;
	}

	// ====================================== INNER CLASS
	private static class TCommentValidator extends AbstractValidator {

		private synchronized VoDatasValidation validTComment(DbTestComment dbTComment,
				VoTestCommentForEdit tcommentToUpdate, boolean createComment) throws JUnitHistoryException {

			VoDatasValidation voValidation = new VoDatasValidation();

			if (createComment) {
				super.validateNull(dbTComment, "comment", voValidation);
			} else {
				super.validateNotNull(dbTComment, "comment", voValidation);
			}

			// common
			super.validateIdDefined(tcommentToUpdate.getTesterId(), "tester", voValidation);
			super.validateString(tcommentToUpdate.getTitle(), 10, "comment", voValidation);
			super.validateString(tcommentToUpdate.getDescription(), 10, "description", voValidation);

			return voValidation;
		}

	}

}
