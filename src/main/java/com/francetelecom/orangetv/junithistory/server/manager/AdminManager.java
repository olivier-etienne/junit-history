package com.francetelecom.orangetv.junithistory.server.manager;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

import com.francetelecom.orangetv.junithistory.server.dao.DaoTestClass;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestClassCategory;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestSuiteGroup;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.dao.DaoTestUser;
import com.francetelecom.orangetv.junithistory.server.dao.IDaoTestClassCategory;
import com.francetelecom.orangetv.junithistory.server.dto.DtoTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestClass;
import com.francetelecom.orangetv.junithistory.server.model.DbTestClassCategory;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteGroup;
import com.francetelecom.orangetv.junithistory.server.model.DbTestSuiteInstance;
import com.francetelecom.orangetv.junithistory.server.model.DbTestUser;
import com.francetelecom.orangetv.junithistory.server.tools.junit.JUnitStatistics;
import com.francetelecom.orangetv.junithistory.server.util.AbstractValidator;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.francetelecom.orangetv.junithistory.shared.util.ObjectUtils;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;
import com.francetelecom.orangetv.junithistory.shared.vo.VoCategoryForEdit;
import com.francetelecom.orangetv.junithistory.shared.vo.VoCategoryForGrid;
import com.francetelecom.orangetv.junithistory.shared.vo.VoDatasValidation;
import com.francetelecom.orangetv.junithistory.shared.vo.VoGroupForEdit;
import com.francetelecom.orangetv.junithistory.shared.vo.VoGroupForGrid;
import com.francetelecom.orangetv.junithistory.shared.vo.VoGroupProtection;
import com.francetelecom.orangetv.junithistory.shared.vo.VoItemProtection;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUserForEdit;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUserForGrid;
import com.francetelecom.orangetv.junithistory.shared.vo.VoUserProtection;

/**
 * Manager pour des tâches évoluees d'administration
 * 
 * @author ndmz2720
 * 
 */
public class AdminManager implements IManager {

	private static final Logger log = Logger.getLogger(AdminManager.class.getName());

	private UserValidator userValidator;
	private CategoryValidator categoryValidator;
	private GroupValidator groupValidator;

	// ---------------------------------- Singleton
	private static AdminManager instance;

	public static final AdminManager get() {
		if (instance == null) {
			instance = new AdminManager();
		}
		return instance;
	}

	private AdminManager() {
	}

	// -------------------------------------
	// ================================================
	// ADMIN TCLASS CATEGORIES
	// ================================================
	public boolean deleteCategory(int categoryId) throws JUnitHistoryException {
		final DbTestClassCategory dbCategory = DaoTestClassCategory.get().getById(categoryId, false);
		if (dbCategory != null) {

			int count = DaoTestClass.get().countByCategory(dbCategory.getId());
			VoItemProtection voItemProtection = this.buildProtectionForCategory(dbCategory.isDefaultValue(), count);
			if (!voItemProtection.canDelete()) {
				throw new JUnitHistoryException("this category cannot be deleted!");
			}
		}
		boolean result = DaoTestClassCategory.get().deleteCategory(categoryId);
		return result;
	}

	public VoCategoryForEdit getCategoryForEdit(int categoryId) throws JUnitHistoryException {

		DbTestClassCategory dbCategory = DaoTestClassCategory.get().getById(categoryId, false);
		if (dbCategory == null) {
			throw new JUnitHistoryException("Category " + categoryId + " doesn't exist!");
		}

		VoCategoryForEdit voCategory = new VoCategoryForEdit(dbCategory.getId(), dbCategory.getName());

		final String[] dbTabClassnames = dbCategory.getSuiteNames();
		String tclassnames = dbTabClassnames == null ? null : ObjectUtils.tabToString(dbTabClassnames,
				IDaoTestClassCategory.CLASSNAME_SEPARATOR);

		voCategory.setListClassNames(tclassnames);

		return voCategory;

	}

	public VoDatasValidation validTestCategory(VoCategoryForEdit categoryToUpdate) throws JUnitHistoryException {

		boolean createCategory = categoryToUpdate != null && categoryToUpdate.getId() == IVo.ID_UNDEFINED;
		final DbTestClassCategory dbCategory = !createCategory && categoryToUpdate != null ? DaoTestClassCategory.get()
				.getById(categoryToUpdate.getId(), false) : null;

		if (dbCategory != null) {
			int count = DaoTestClass.get().countByCategory(dbCategory.getId());
			VoItemProtection voItemProtection = this.buildProtectionForCategory(dbCategory.isDefaultValue(), count);
			if (!voItemProtection.canEdit()) {
				throw new JUnitHistoryException("this category cannot be updated!");
			}

		}
		return this.getCategoryValidator().validCategory(dbCategory, categoryToUpdate, createCategory);
	}

	/**
	 * Mise à jour de la category.
	 * Si la liste des tclass names a ete modifie alors on met a jour la table
	 * tclass en fonction des nouvelles regles
	 * 
	 * @param categoryToUpdate
	 * @param sessionId
	 * @return
	 * @throws JUnitHistoryException
	 */
	public VoDatasValidation createOrUpdateTestCategory(VoCategoryForEdit categoryToUpdate, String sessionId)
			throws JUnitHistoryException {

		boolean createCategory = categoryToUpdate != null && categoryToUpdate.getId() == IVo.ID_UNDEFINED;
		DbTestClassCategory dbCategory = !createCategory && categoryToUpdate != null ? DaoTestClassCategory.get()
				.getById(categoryToUpdate.getId(), false) : null;
		VoDatasValidation voValidation = this.getCategoryValidator().validCategory(dbCategory, categoryToUpdate,
				createCategory);
		if (voValidation.isValid()) {

			final String[] dbTabClassnames = dbCategory == null ? null : dbCategory.getSuiteNames();
			String dbClassnames = dbTabClassnames == null ? null : ObjectUtils.tabToString(dbTabClassnames,
					IDaoTestClassCategory.CLASSNAME_SEPARATOR);

			final String tabClassnames = categoryToUpdate.getListClassNames();
			boolean suitenamesModified = !createCategory && !dbClassnames.equals(tabClassnames);

			try {
				DatabaseManager.get().beginTransaction(sessionId);

				boolean result;
				if (createCategory) {
					dbCategory = new DbTestClassCategory(categoryToUpdate.getName());
				}

				dbCategory.update(categoryToUpdate);

				result = createCategory ? DaoTestClassCategory.getWithTransaction(sessionId).createCategory(dbCategory)
						: DaoTestClassCategory.getWithTransaction(sessionId).updateCategory(dbCategory);

				if (!result) {
					throw new JUnitHistoryException("Failure in " + (createCategory ? "creating" : "updating")
							+ " category " + dbCategory.getId() + "!");
				}

				// on recalcule les categories
				// de toutes les tclass
				if (suitenamesModified || createCategory) {
					this.reassignCategoryForListTClass(sessionId);
				}

				DatabaseManager.get().closeAndCommitTransaction(sessionId);
				DaoTestClassCategory.get().reinitAllDaoCaches();

			} catch (Exception ex) {
				DatabaseManager.get().rollbackTransaction(sessionId);
				throw new JUnitHistoryException(ex);
			}
		}

		return voValidation;
	}

	public List<VoCategoryForGrid> getListTestClassCategories() throws JUnitHistoryException {

		final List<DbTestClassCategory> listCategories = DaoTestClassCategory.get().listCategories(false);

		final List<VoCategoryForGrid> listVos = new ArrayList<>(listCategories == null ? 0 : listCategories.size());
		if (listCategories == null) {
			return listVos;
		}
		// for each category
		for (DbTestClassCategory category : listCategories) {

			final VoCategoryForGrid vo = this.buildVoCategoryForGrid(category);
			listVos.add(vo);

		}

		// protection
		Map<Integer, Integer> mapIdCategory2Count = DaoTestClass.get().countCategories();
		this.defineProtectionToCategories(listVos, mapIdCategory2Count);
		return listVos;
	}

	// ================================================
	// ADMIN USER
	// ================================================
	public boolean deleteTestUser(int userId) throws JUnitHistoryException {

		final DbTestUser dbuser = DaoTestUser.get().getById(userId);

		if (dbuser != null) {
			int countInTestSuite = DaoTestSuiteInstance.get().countByUser(dbuser.getId());
			final VoItemProtection protection = this.buildProtectionForUser(dbuser.isAdmin(), countInTestSuite);
			if (!protection.canDelete()) {
				throw new JUnitHistoryException("This user cannot be deleted!");
			}
		}

		return DaoTestUser.get().deleteUser(userId);
	}

	public VoUserForEdit getUserForEdit(int userId) throws JUnitHistoryException {

		DbTestUser dbUser = DaoTestUser.get().getById(userId);
		if (dbUser == null) {
			throw new JUnitHistoryException("User " + userId + " doesn't exist!");
		}

		VoUserForEdit voUser = new VoUserForEdit(dbUser.getId(), dbUser.getName());
		voUser.setDescription(dbUser.getDescription());

		int count = DaoTestSuiteInstance.get().countByUser(userId);
		VoUserProtection protection = this.buildProtectionForUser(dbUser.isAdmin(), count);
		voUser.setProtection(protection);

		return voUser;
	}

	public VoDatasValidation validTestUser(VoUserForEdit userToUpdate) throws JUnitHistoryException {

		boolean createUser = userToUpdate != null && userToUpdate.isIdUndefined();
		final DbTestUser dbuser = !createUser && userToUpdate != null ? DaoTestUser.get().getById(userToUpdate.getId())
				: null;

		if (dbuser != null) {
			int countInTestSuite = DaoTestSuiteInstance.get().countByUser(dbuser.getId());
			final VoUserProtection protection = this.buildProtectionForUser(dbuser.isAdmin(), countInTestSuite);
			if (!protection.canEdit()) {
				throw new JUnitHistoryException("This user cannot be updated!");
			}
			if (!protection.canUpdateName()) {
				if (!dbuser.getName().equals(userToUpdate.getName())) {
					throw new JUnitHistoryException("This user name cannot be updated!");
				}
			}
		}

		return this.getUserValidator().validUser(dbuser, userToUpdate, createUser);
	}

	public VoDatasValidation createOrUpdateTestUser(VoUserForEdit userToUpdate) throws JUnitHistoryException {

		boolean createUser = userToUpdate != null && userToUpdate.isIdUndefined();
		DbTestUser dbuser = !createUser && userToUpdate != null ? DaoTestUser.get().getById(userToUpdate.getId())
				: null;
		VoDatasValidation validation = this.getUserValidator().validUser(dbuser, userToUpdate, createUser);
		if (validation.isValid()) {

			if (createUser) {
				dbuser = new DbTestUser(userToUpdate.getName());
				dbuser.setDescription(userToUpdate.getDescription());
			} else {
				dbuser.update(userToUpdate);

			}

			boolean result = createUser ? DaoTestUser.get().createUser(dbuser) : DaoTestUser.get().updateUser(dbuser);
			if (!result) {
				validation.getErrorMessages().add(
						"Failure in " + (createUser ? "creating" : "updating") + " user " + dbuser.getId() + "!");
			}
		}
		return validation;
	}

	public List<VoUserForGrid> getListUsers() throws JUnitHistoryException {

		final List<DbTestUser> listUsers = DaoTestUser.get().listUsers(true);

		final List<VoUserForGrid> listVos = new ArrayList<>(listUsers == null ? 0 : listUsers.size());

		if (listUsers == null) {
			return listVos;
		}

		// for each users
		for (DbTestUser user : listUsers) {
			final VoUserForGrid vo = this.buildVoUserForGrid(user);
			listVos.add(vo);
		}

		// Protection
		Map<Integer, Integer> countUsersInSuite = DaoTestSuiteInstance.get().countUsers();
		this.defineProtectionToUsers(listVos, countUsersInSuite);

		return listVos;
	}

	// ================================================
	// ADMIN GROUP
	// ================================================
	public boolean deleteTestGroup(int groupId) throws JUnitHistoryException {
		DbTestSuiteGroup dbGroup = DaoTestSuiteGroup.get().getById(groupId, false);
		if (dbGroup != null) {
			int countUsed = DaoTestSuiteInstance.get().countByGroup(groupId);
			final VoGroupProtection protection = this.buildProtectionForGroup(countUsed);
			if (!protection.canDelete()) {
				throw new JUnitHistoryException("This group cannot be deleted!");
			}
		}
		return DaoTestSuiteGroup.get().deleteGroup(groupId);
	}

	public VoGroupForEdit getGroupForEdit(int groupId) throws JUnitHistoryException {
		DbTestSuiteGroup dbGroup = DaoTestSuiteGroup.get().getById(groupId, false);
		if (dbGroup == null) {
			throw new JUnitHistoryException("Group " + groupId + " doesn't exist!");
		}

		VoGroupForEdit voGroup = new VoGroupForEdit(dbGroup.getId(), dbGroup.getName());
		voGroup.setStb(dbGroup.getStb());
		voGroup.setPrefix(dbGroup.getPrefix());

		// protection
		int countUsed = DaoTestSuiteInstance.get().countByGroup(groupId);
		voGroup.setProtection(this.buildProtectionForGroup(countUsed));

		return voGroup;

	}

	public VoDatasValidation validTestGroup(VoGroupForEdit groupToUpdate) throws JUnitHistoryException {

		final boolean createGroup = groupToUpdate != null && groupToUpdate.getId() == IVo.ID_UNDEFINED;
		final DbTestSuiteGroup dbgroup = !createGroup && groupToUpdate != null ? DaoTestSuiteGroup.get().getById(
				groupToUpdate.getId(), false) : null;

		if (dbgroup != null) {
			int countInTestSuite = DaoTestSuiteInstance.get().countByGroup(dbgroup.getId());
			VoGroupProtection protection = this.buildProtectionForGroup(countInTestSuite);
			if (!protection.canEdit()) {
				throw new JUnitHistoryException("This group cannot be updated!");
			}
			if (!protection.canUpdatePrefix()) {
				if (!dbgroup.getPrefix().equals(groupToUpdate.getPrefix())) {
					throw new JUnitHistoryException("This group prefix cannot be updated!");
				}
			}
		}
		return this.getGroupValidator().validGroup(dbgroup, groupToUpdate, createGroup);
	}

	public VoDatasValidation createOrUpdateTestGroup(VoGroupForEdit groupToUpdate) throws JUnitHistoryException {

		final boolean createGroup = groupToUpdate != null && groupToUpdate.getId() == IVo.ID_UNDEFINED;
		DbTestSuiteGroup dbgroup = !createGroup && groupToUpdate != null ? DaoTestSuiteGroup.get().getById(
				groupToUpdate.getId(), false) : null;
		VoDatasValidation voValidation = this.getGroupValidator().validGroup(dbgroup, groupToUpdate, createGroup);
		if (voValidation.isValid()) {

			if (createGroup) {
				dbgroup = new DbTestSuiteGroup(groupToUpdate.getStb(), groupToUpdate.getName(),
						groupToUpdate.getPrefix());
			} else {
				dbgroup.update(groupToUpdate);
			}
			boolean result = createGroup ? DaoTestSuiteGroup.get().createGroup(dbgroup) : DaoTestSuiteGroup.get()
					.updateGroup(dbgroup);
			if (!result) {
				voValidation.getErrorMessages().add(
						"Error in " + (createGroup ? "creating" : "updating") + dbgroup.getId() + "!");
			}
		}
		return voValidation;
	}

	public List<VoGroupForGrid> getListGroups() throws JUnitHistoryException {

		final List<DbTestSuiteGroup> listGroups = DaoTestSuiteGroup.get().listGroups(false);

		final List<VoGroupForGrid> listVos = new ArrayList<>(listGroups == null ? 0 : listGroups.size());
		if (listGroups == null) {
			return listVos;
		}

		// for each group
		for (DbTestSuiteGroup group : listGroups) {

			final VoGroupForGrid vo = this.buildVoGroupForList(group);
			listVos.add(vo);
		}

		// protection
		Map<Integer, Integer> mapIdGroup2Count = DaoTestSuiteInstance.get().countGroups();
		this.defineProtectionToGroups(listVos, mapIdGroup2Count);
		return listVos;
	}

	public boolean cleanAllSuiteInBdd() throws JUnitHistoryException {

		List<DbTestSuiteInstance> allSuites = DaoTestSuiteInstance.get().listSuites();
		if (allSuites != null && !allSuites.isEmpty()) {
			for (DbTestSuiteInstance dbTestSuiteInstance : allSuites) {
				DaoManager.get().deleteTestSuite(dbTestSuiteInstance.getId(), "token");
			}
			return true;
		}
		return false;
	}

	/**
	 * Initialisation de la base de donnees avec des rapports xml, xml.part, log
	 * et txt existants
	 * 
	 * @throws JUnitHistoryException
	 */
	public void initializeBddWithArchives(String archivePathname, boolean cleanBdd) throws JUnitHistoryException {

		if (cleanBdd) {
			// si cleanBdd on supprime les suites existantes
			this.cleanAllSuiteInBdd();
		}

		// on construit la liste des DtoTestSuiteInstance et les fichiers
		// associés
		Map<DtoTestSuiteInstance, List<File>> map = ReportManager.get().buildListDtoSuiteInstanceFromArchive(
				archivePathname);

		if (map == null || map.isEmpty()) {
			return;
		}

		// for each suite >> addToHistory
		for (DtoTestSuiteInstance dtoSuite : map.keySet()) {

			// determiner la version firmware
			String firmware = ReportManager.get().getVersionFromDtoTestSuiteInstance(dtoSuite);
			dtoSuite.getTestSuiteInstance().setReadonly(true);

			DaoManager.get().saveTestSuite(dtoSuite, "tititoken");
		}

		// et pour finir on copie les fichier dans /historic/xml
		// TODO
	}

	// -------------------------------------------- private methods

	/*
	 * Recalcul des categories pour toutes les tclass
	 * Intégrée dans une transaction (token) exterieure
	 */
	private void reassignCategoryForListTClass(String token) throws JUnitHistoryException {

		log.warning("reassignCategoryForListTClass()...");
		DaoTestClass daoTClass = DaoTestClass.getWithTransaction(token);

		// on recupère toutes les tclass en base
		List<DbTestClass> listTClasses = DaoTestClass.get().listTClasses();
		if (listTClasses != null) {

			DbTestClassCategory defaultCategory = DaoTestClassCategory.get().getDefaultCategory(true);
			List<DbTestClassCategory> listCategories = DaoTestClassCategory.getWithTransaction(token).listCategories(
					false);

			// for each tclass
			JUnitStatistics.resetMap();
			for (DbTestClass dbTClass : listTClasses) {

				log.fine("tclass: " + dbTClass.getName() + "- categoryId: " + dbTClass.getCategory().getId());

				// on cherche la category associee
				DbTestClassCategory category = JUnitStatistics.getStatCategory(dbTClass.getName(), listCategories);
				log.fine("associated category: "
						+ (category == null ? "NULL" : category.getName() + " - " + category.getId()));
				if (category == null) {
					category = defaultCategory;
				}

				// si differente alors on met a jour la BDD
				if (category.getId() != dbTClass.getCategory().getId()) {
					log.fine("update tclass with new category: " + category.getId());
					daoTClass.updateCategoryId(dbTClass.getId(), category.getId());
				}
			}
			JUnitStatistics.resetMap();
		}
	}

	private VoCategoryForGrid buildVoCategoryForGrid(DbTestClassCategory category) {

		final VoCategoryForGrid vo = new VoCategoryForGrid(category.getId(), category.getName());
		vo.setDefaultValue(category.isDefaultValue());
		vo.setSuiteNames(category.getSuiteNames());

		return vo;
	}

	private VoUserForGrid buildVoUserForGrid(DbTestUser user) {

		final VoUserForGrid vo = new VoUserForGrid(user.getId(), user.getName());
		vo.setDescription(user.getDescription());
		vo.setAdmin(user.isAdmin());

		return vo;
	}

	private VoGroupForGrid buildVoGroupForList(DbTestSuiteGroup group) {

		final VoGroupForGrid vo = new VoGroupForGrid(group.getId(), group.getName());
		vo.setPrefix(group.getPrefix());
		vo.setStb(group.getStb());

		return vo;
	}

	private void defineProtectionToGroups(List<VoGroupForGrid> listGroups, Map<Integer, Integer> mapGroupId2Count) {

		// for each group
		for (VoGroupForGrid voGroupForGrid : listGroups) {
			Integer count = mapGroupId2Count.get(voGroupForGrid.getId());
			voGroupForGrid.setProtection(this.buildProtectionForGroup(count));
		}

	}

	private VoGroupProtection buildProtectionForGroup(Integer count) {
		VoGroupProtection protection = new VoGroupProtection();
		// on ne peut supprimer un group que si il n'est pas
		// utilisé
		// dans la table suite
		// par contre on peut l'editer
		boolean groupInUse = count != null && count > 0;

		boolean canDelete = !groupInUse;
		protection.setCanDelete(canDelete);
		protection.setCanEdit(true);

		// si group est utilisé alors on ne peut pas modifier le prefix
		// (sinon il faudrait renommer tous les fichiers!)
		protection.setCanUpdatePrefix(!groupInUse);

		return protection;
	}

	private void defineProtectionToCategories(List<VoCategoryForGrid> listCategories,
			Map<Integer, Integer> mapIdCategory2Count) {

		// for each category
		for (VoCategoryForGrid voCategoryForGrid : listCategories) {
			Integer count = mapIdCategory2Count.get(voCategoryForGrid.getId());

			VoItemProtection protection = this.buildProtectionForCategory(voCategoryForGrid.isDefaultValue(), count);
			voCategoryForGrid.setProtection(protection);
		}

	}

	/*
	 * @param defaultValue: category.defaultValue
	 * @param countInClass: nbr de tclass pointant vers cette category
	 */
	private VoItemProtection buildProtectionForCategory(boolean defaultValue, Integer countInTclass) {
		VoItemProtection protection = new VoItemProtection();

		boolean categoryUsed = (countInTclass != null && countInTclass > 0);
		// on ne peut supprimer une category que si il n'est pas
		// utilisé
		// dans la table tclass
		// par contre on peut l'editer
		boolean canDelete = !defaultValue && !categoryUsed;
		protection.setCanDelete(canDelete);

		// on ne peut pas editer la category 'other'
		protection.setCanEdit(!defaultValue);

		return protection;

	}

	private void defineProtectionToUsers(List<VoUserForGrid> listUsers, Map<Integer, Integer> mapIdUser2Count) {

		// for each user
		for (VoUserForGrid voUserForGrid : listUsers) {
			Integer count = mapIdUser2Count.get(voUserForGrid.getId());
			VoItemProtection protection = this.buildProtectionForUser(voUserForGrid.isAdmin(), count);
			voUserForGrid.setProtection(protection);
		}
	}

	private VoUserProtection buildProtectionForUser(boolean isadmin, Integer countInTestSuite) {

		VoUserProtection protection = new VoUserProtection();
		// si user admin on ne peut ni l'editer ni le supprimer
		if (isadmin) {
			protection.setCanDelete(false);
			protection.setCanEdit(false);
		} else {

			boolean userUsed = countInTestSuite != null && countInTestSuite > 0;
			// on ne peut supprimer un utilisateur que si il n'est pas
			// utilisé
			// dans la table suite
			// par contre on peut l'editer
			boolean canDelete = !userUsed;
			protection.setCanDelete(canDelete);
			protection.setCanEdit(true);

			// ne pas pouvoir modifier le nom d'un utilisateur used
			protection.setCanUpdateName(!userUsed);

		}
		return protection;

	}

	private UserValidator getUserValidator() {
		if (this.userValidator == null) {
			this.userValidator = new UserValidator();
		}
		return this.userValidator;
	}

	private CategoryValidator getCategoryValidator() {
		if (this.categoryValidator == null) {
			this.categoryValidator = new CategoryValidator();
		}
		return this.categoryValidator;
	}

	private GroupValidator getGroupValidator() {
		if (this.groupValidator == null) {
			this.groupValidator = new GroupValidator();
		}
		return this.groupValidator;
	}

	// ====================================== INNER CLASS
	private static class UserValidator extends AbstractValidator {

		private synchronized VoDatasValidation validUser(DbTestUser dbuser, VoUserForEdit userToUpdate,
				boolean createUser) throws JUnitHistoryException {

			VoDatasValidation voValidation = new VoDatasValidation();

			if (createUser) {
				super.validateNull(dbuser, "user", voValidation);
			} else {
				super.validateNotNull(dbuser, "user", voValidation);
			}

			// common
			super.validateString(userToUpdate.getName(), 3, "user", voValidation);
			if (userToUpdate.getDescription() != null) {
				super.validateString(userToUpdate.getDescription(), 3, "description", voValidation);
			}

			// le nom doit rester unique
			if (createUser || !dbuser.getName().equals(userToUpdate.getName())) {
				// cas creation ou changement de nom.on verifie unicité

				if (DaoTestUser.get().countByName(userToUpdate.getName()) > 0) {
					voValidation.getErrorMessages().add("The name " + userToUpdate.getName() + " still exists!");
				}
			}

			return voValidation;
		}

	}

	private static class CategoryValidator extends AbstractValidator {

		private synchronized VoDatasValidation validCategory(DbTestClassCategory dbCategory,
				VoCategoryForEdit categoryToUpdate, boolean createCategory) throws JUnitHistoryException {

			VoDatasValidation voValidation = new VoDatasValidation();

			if (createCategory) {
				super.validateNull(dbCategory, "category", voValidation);
			} else {
				super.validateNotNull(dbCategory, "category", voValidation);
			}
			// common
			super.validateString(categoryToUpdate.getName(), 3, "name", voValidation);
			super.validateString(categoryToUpdate.getListClassNames(), 6, "list class names", voValidation);

			// le nom doit rester unique
			if (createCategory || !dbCategory.getName().equals(categoryToUpdate.getName())) {
				// cas creation ou changement de nom.on verifie unicité

				if (DaoTestClassCategory.get().countByName(categoryToUpdate.getName()) > 0) {
					voValidation.getErrorMessages().add("The name " + categoryToUpdate.getName() + " still exists!");
				}
			}

			return voValidation;
		}
	}

	private static class GroupValidator extends AbstractValidator {

		private synchronized VoDatasValidation validGroup(DbTestSuiteGroup dbGroup, VoGroupForEdit groupToUpdate,
				boolean createGroup) throws JUnitHistoryException {

			VoDatasValidation voValidation = new VoDatasValidation();

			if (createGroup) {
				super.validateNull(dbGroup, "group", voValidation);
			} else {
				super.validateNotNull(dbGroup, "group", voValidation);
			}

			super.validateString(groupToUpdate.getName(), 4, "name", voValidation);
			super.validateString(groupToUpdate.getStb(), 4, "STB", voValidation);
			super.validateString(groupToUpdate.getPrefix(), 5, "Prefix", voValidation);

			// verifier que prefix se termine par '-'
			if (voValidation.isValid()) {
				if (!groupToUpdate.getPrefix().endsWith("-")) {
					voValidation.getErrorMessages().add("Prefix must end with '-'!");
				}
			}

			// le nom doit rester unique
			if (createGroup || !dbGroup.getName().equals(groupToUpdate.getName())) {
				// cas creation ou changement de nom.on verifie unicité

				if (DaoTestSuiteGroup.get().countByName(groupToUpdate.getName()) > 0) {
					voValidation.getErrorMessages().add("The name " + groupToUpdate.getName() + " still exists!");
				}
			}

			return voValidation;

		}
	}
}
