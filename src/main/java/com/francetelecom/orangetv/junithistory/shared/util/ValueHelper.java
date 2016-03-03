package com.francetelecom.orangetv.junithistory.shared.util;


public class ValueHelper {

	public static int[] reverseArray(int[] items) {

		if (items == null) {
			return null;
		}

		int length = items.length;
		int[] reversedItems = new int[length];
		for (int i = 0; i < length; i++) {
			reversedItems[length - (i + 1)] = items[i];
		}
		return reversedItems;
	}

	public static String getStringValue(String value, String defaultValue) {
		return isStringEmptyOrNull(value) ? defaultValue : value;
	}

	public static int getIntValue(String value, int defaultValue) {

		int intValue = defaultValue;
		try {
			intValue = Integer.parseInt(value);
		} catch (NumberFormatException ignored) {
			// do nothing
		}

		return intValue;
	}

	public static boolean isStringEmptyOrNull(String value) {
		return value == null || value.trim().length() == 0;
	}

}
