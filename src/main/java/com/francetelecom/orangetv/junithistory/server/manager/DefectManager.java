package com.francetelecom.orangetv.junithistory.server.manager;

import java.util.List;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.server.dao.DaoTestInstance;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestMessage;
import com.francetelecom.orangetv.junithistory.server.model.DbTestInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestMessage;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.TestStatusEnum;
import com.francetelecom.orangetv.junithistory.server.model.TestSubStatusEnum;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
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
	public VoResultSearchTestDatas searchDefectTestList(VoSearchDefectDatas vo) throws JUnitHistoryException {

		VoResultSearchTestDatas listTestNames = new VoResultSearchTestDatas();
		if (vo == null || vo.getGroupId() == IVo.ID_UNDEFINED || vo.getSearch() == null) {
			return listTestNames;
		}
		final List<String> listDBDistinctTestNames = DaoTestInstance.get().searchDistinctNamesForGroupAndContainsName(
				vo.getGroupId(), vo.getSearch());

		if (listDBDistinctTestNames == null) {
			return listTestNames;
		}

		// for each test name
		for (String testDistinctName : listDBDistinctTestNames) {
			listTestNames.addTest(new VoTestDistinctName(testDistinctName));
		}

		return listTestNames;
	}

	public VoListTestsSameNameDatas getListTestsForGroupSameName(VoSearchDefectDatas vo) throws JUnitHistoryException {

		final VoListTestsSameNameDatas listTests = new VoListTestsSameNameDatas();
		if (vo == null || vo.getGroupId() == IVo.ID_UNDEFINED || vo.getSearch() == null) {
			return listTests;
		}

		final List<DbTestInstance> listDbTestInstances = DaoTestInstance.get().listTestsForGroupAndTestName(
				vo.getGroupId(), vo.getSearch());
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

			DbTestMessage message = test.getMessage();
			if (message != null && message.isLazy()) {
				message = DaoTestMessage.get().getById(message.getId());

				voTest.setType(message.getType());
				voTest.setMessage(message.getMessage());
				voTest.setOutputLog(message.getOutputLog());
				voTest.setStackTrace(message.getStackTrace());
			}
		}

		return listTests;

	}
}
