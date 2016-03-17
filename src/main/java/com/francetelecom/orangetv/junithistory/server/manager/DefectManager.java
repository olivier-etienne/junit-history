package com.francetelecom.orangetv.junithistory.server.manager;

import java.util.List;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.server.dao.DaoTestInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestInstance;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoResultDefectTestDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSearchDefectDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestInstanceForList;

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
	public VoResultDefectTestDatas searchDefectTestList(VoSearchDefectDatas vo) throws JUnitHistoryException {

		VoResultDefectTestDatas result = new VoResultDefectTestDatas();
		if (vo.getGroupId() == IVo.ID_UNDEFINED) {
			return result;
		}
		final List<DbTestInstance> listTests = DaoTestInstance.get().listTestsForGroupAndTestName(vo.getGroupId(),
				vo.getSearch());

		if (listTests == null) {
			return result;
		}

		// for each test
		for (DbTestInstance test : listTests) {
			result.addTest(new VoTestInstanceForList(test.getId(), test.getName()));
		}

		return result;
	}
}
