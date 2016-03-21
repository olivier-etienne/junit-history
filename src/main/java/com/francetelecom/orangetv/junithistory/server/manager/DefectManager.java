package com.francetelecom.orangetv.junithistory.server.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.server.dao.DaoTestInstance;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestMessage;
import com.francetelecom.orangetv.junithistory.server.model.DbTestInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestMessage;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.shared.TestStatusEnum;
import com.francetelecom.orangetv.junithistory.shared.TestSubStatusEnum;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoIdName;
import com.francetelecom.orangetv.junithistory.shared.vo.VoListTestsSameNameDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoResultSearchTestDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSearchDefectDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestDistinctName;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestInstanceForEdit;

/**
 * Manager pour la gestion des defects
 * 
 * @author NDMZ2720
 *
 */
public class DefectManager implements IManager {

	private static final Logger log = Logger.getLogger(DefectManager.class.getName());

	// ---------------------------------- Singleton
	private static DefectManager instance;

	public static final DefectManager get() {
		if (instance == null) {
			instance = new DefectManager();
		}
		return instance;
	}

	private DefectManager() {
	}

	// ------------------------------------- public methods
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
		// TODO recuperer tous les messages en une seule requete!!
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
			listTests.addTestInstance(voTest);
		}

		return listTests;

	}
}
