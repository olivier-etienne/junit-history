package com.francetelecom.orangetv.junithistory.server.manager;

import static org.junit.Assert.assertNotNull;

import java.util.logging.Logger;

import org.junit.Test;

import com.francetelecom.orangetv.junithistory.shared.vo.VoListTestsSameNameDatas;
import com.francetelecom.orangetv.junithistory.shared.vo.VoSearchDefectDatas;

public class AnalysisManagerTest {

	private static final Logger log = Logger.getLogger(AnalysisManagerTest.class.getName());
	private static final AnalysisManager manager = AnalysisManager.get();

	@Test
	public void testGetListTestsForGroupSameName() throws Exception {

		VoSearchDefectDatas selectDatas = new VoSearchDefectDatas(11, "testModifySchedule");
		VoListTestsSameNameDatas vo = manager.getListTestsForGroupIdTClassIdAndTestName(selectDatas, true);

		assertNotNull("VoListTestsSameNameDatas cannot be null!!", vo);
	}

}
