package com.francetelecom.orangetv.junithistory.server.service;

import com.francetelecom.orangetv.junithistory.server.manager.IManager;

public interface IMyServices extends IManager {

	public static final String KEY_SESSION_PARAM = "getItem";
	public static final String KEY_SESSION_ITEM_ID = "itemId";
	public static final String KEY_SESSION_URL = "url";

	public static final String KEY_HISTORY_BACKUP_PATH = "historyBackupPath";

	public static final String KEY_CONTEXT_TEST_MODE = "testMode";
}
