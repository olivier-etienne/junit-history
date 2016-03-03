package com.francetelecom.orangetv.junithistory.shared.vo;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VoIdUtils {

	/*
	 * Transforms a list of IVoId in a map[id, item]
	 */
	public static <T extends IVoId> Map<Integer, T> getMapId2Item(List<T> listItems) {

		Map<Integer, T> map = new HashMap<>(listItems == null ? 0 : listItems.size());

		if (listItems != null) {
			for (T item : listItems) {
				map.put(item.getId(), item);
			}
		}

		return map;
	}

}
