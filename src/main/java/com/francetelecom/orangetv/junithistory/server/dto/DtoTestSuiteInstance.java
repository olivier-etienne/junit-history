package com.francetelecom.orangetv.junithistory.server.dto;

import java.util.List;
import java.util.Map;

import com.francetelecom.orangetv.junithistory.server.model.DbStatsCategoryInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;

/**
 * Container: testSuite, liste des tests, statistiques par categorie
 * 
 * @author ndmz2720
 *
 */
public class DtoTestSuiteInstance implements IDto {

	private final DbTestSuiteInstance testSuiteInstance;
	private final List<DbTestInstance> listDbTestInstances;
	private final List<DbStatsCategoryInstance> listDbStatsCategoryInstances;

	// facultatif: associer les tests et leur nom
	private Map<String, DbTestInstance> mapTestname2DbTestInstance;

	public DtoTestSuiteInstance(DbTestSuiteInstance testSuiteInstance, List<DbTestInstance> listDbTestInstances,
			List<DbStatsCategoryInstance> listDbStatsCategoryInstances) {
		this.testSuiteInstance = testSuiteInstance;
		this.listDbTestInstances = listDbTestInstances;
		this.listDbStatsCategoryInstances = listDbStatsCategoryInstances;
	}

	// ------------------------------------------ accessors

	public DbTestSuiteInstance getTestSuiteInstance() {
		return testSuiteInstance;
	}

	public Map<String, DbTestInstance> getMapTClassAndTestname2DbTestInstance() {
		return mapTestname2DbTestInstance;
	}

	public void setMapTClassAndTestname2DbTestInstance(Map<String, DbTestInstance> mapTestname2DbTestInstance) {
		this.mapTestname2DbTestInstance = mapTestname2DbTestInstance;
	}

	public List<DbTestInstance> getListDbTestInstances() {
		return listDbTestInstances;
	}

	public List<DbStatsCategoryInstance> getListDbStatsCategoryInstances() {
		return listDbStatsCategoryInstances;
	}
}
