package com.francetelecom.orangetv.junithistory.server.util;

import java.util.ArrayList;
import java.util.List;

/**
 * Commentaire sur plusieurs lignes
 * 
 * @author ndmz2720
 *
 */
public class ListLines {

	private List<String> lines;

	public List<String> getLines() {
		return this.lines;
	}

	public void addLines(ListLines listLines) {

		if (listLines != null) {
			if (this.lines == null) {
				this.lines = new ArrayList<>();
			}
			this.lines.addAll(listLines.getLines());
		}
	}

	public boolean replaceFirst(String regex, String replacement) {

		if (this.lines == null) {
			return false;
		}

		boolean success = false;
		List<String> newLines = new ArrayList<>(this.lines.size());
		for (int i = 0; i < this.lines.size(); i++) {

			String line = this.lines.get(i);
			if (line.contains(regex)) {
				line = line.replaceFirst(regex, replacement);
				success = true;
			}
			newLines.add(line);
		}
		this.lines = newLines;
		return success;
	}

	public void replaceAll(String regex, String replacement) {

		if (this.lines == null) {
			return;
		}
		List<String> newLines = new ArrayList<>(this.lines.size());
		for (int i = 0; i < this.lines.size(); i++) {

			String line = this.lines.get(i);
			if (line.contains(regex)) {
				line = line.replaceAll(regex, replacement);
			}
			newLines.add(line);
		}

		this.lines = newLines;

	}

	public void addLine(int item) {
		this._addLine(item + "");
	}

	public void addLine(String... items) {

		String line = null;

		if (items != null) {

			if (items.length == 1) {
				line = items[0];
			} else {

				StringBuilder sb = new StringBuilder();
				for (String item : items) {
					sb.append(item);
				}
				line = sb.toString();
			}
		}

		this._addLine(line);
	}

	private void _addLine(String line) {
		if (line == null) {
			return;
		}

		if (lines == null) {
			this.lines = new ArrayList<>();
		}

		this.lines.add(line);
	}

	public boolean removeLine(String line) {
		if (this.lines == null) {
			return false;
		}
		return this.lines.remove(line);
	}

	public boolean isEmpty() {

		return (this.lines == null) ? true : this.lines.isEmpty();
	}

	public void newLine() {
		this._addLine("");
	}
}
