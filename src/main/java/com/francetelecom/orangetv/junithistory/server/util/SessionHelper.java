package com.francetelecom.orangetv.junithistory.server.util;

import javax.servlet.http.HttpSession;

import com.francetelecom.orangetv.junithistory.shared.util.ValueHelper;
import com.francetelecom.orangetv.junithistory.shared.vo.IVo;

public class SessionHelper {

	public static String getStringAttribute(HttpSession session, String key, String defaultValue) {

		Object obj = session.getAttribute(key);
		return obj == null ? defaultValue : obj.toString();

	}

	public static int getIntAttribute(HttpSession session, String key, int defaultValue) {

		String value = getStringAttribute(session, key, null);
		return ValueHelper.getIntValue(value, IVo.ID_UNDEFINED);

	}

	public static boolean getBooleanAttribute(HttpSession session, String key, boolean defaultValue) {

		Object obj = session.getAttribute(key);
		return obj == null ? defaultValue : Boolean.parseBoolean(obj.toString());

	}

}
