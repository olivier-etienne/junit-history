package com.francetelecom.orangetv.junithistory.shared.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ObjectUtils {

	public static Map<String, Object> buildMapWithOneItem(String key, Object value) {

		final Map<String, Object> map = new HashMap<>(1);
		map.put(key, value);
		return map;
	}

	public static <T> Map<Integer, T> buildMapIdWithOneItem(Integer key, T value) {

		final Map<Integer, T> map = new HashMap<>(1);
		map.put(key, value);
		return map;
	}

	public static <T extends Object> List<T> createList(T... items) {

		List<T> list = null;
		if (items != null) {
			list = new ArrayList<>(items.length);
			for (T item : items) {
				list.add(item);
			}
		} else {
			list = new ArrayList<>(0);
		}

		return list;
	}

	public static String tabToString(String[] items, char separator) {
		StringBuilder sb = new StringBuilder();
		if (items != null) {

			for (int i = 0; i < items.length; i++) {
				String item = items[i];

				sb.append(item);
				if (i < items.length - 1) {
					sb.append(separator);
					sb.append(" ");
				}
			}
		}

		return sb.toString();
	}

	public static String buildStringFromList(List<String> list, char separator) {

		if (list == null) {
			return null;
		}
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < list.size(); i++) {
			sb.append(list.get(i));
			if (i < list.size() - 1) {
				sb.append(separator);
			}

		}

		return sb.toString();
	}

	public static List<String> buildListItems(String items, String separator) {
		if (items == null) {
			return new ArrayList<>(0);
		}
		String[] tabItems = items.split(separator);

		List<String> listItems = new ArrayList<>(tabItems.length);
		for (String item : tabItems) {
			if (item != null) {
				listItems.add(item.trim());
			}
		}
		return listItems;
	}

	/**
	 * Creation d'une liste de lignes en s'appuyant sur les saut de lignes \n
	 * 
	 * @param text
	 * @return
	 */

	public static List<String> createListLines(String text) {

		final List<String> listLines = new ArrayList<>();
		if (text == null) {
			return listLines;
		}

		StringBuilder line = new StringBuilder();
		for (int i = 0; i < text.length(); i++) {

			char c = text.charAt(i);
			if (c == '\n') {
				listLines.add(line.toString());
				line = new StringBuilder();
			} else {
				line.append(c);
			}

		}

		return listLines;

	}

	public static String[] listToTab(List<String> list) {

		if (list == null) {
			return null;
		}
		String[] tab = new String[list.size()];
		for (int i = 0; i < list.size(); i++) {
			tab[i] = list.get(i);
		}
		return tab;
	}

}
