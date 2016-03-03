package com.francetelecom.orangetv.junithistory.server.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.francetelecom.orangetv.junithistory.server.manager.IManager;
import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;

public class FileUtils implements IManager {

	private static final Logger log = Logger.getLogger(FileUtils.class.getName());

	public static final FileFilter DIR_FILTER = new FileFilter() {

		@Override
		public boolean accept(File file) {
			return file.exists() && file.isDirectory();
		}
	};

	public static final FileFilter FILE_FILTER = new FileFilter() {

		@Override
		public boolean accept(File file) {
			return file.exists() && file.isFile();
		}
	};

	public static final Comparator<File> FILENAME_COMPARATOR = new Comparator<File>() {

		@Override
		public int compare(File file1, File file2) {

			if (file1 == null && file2 == null) {
				return 0;
			}
			if (file1 == null) {
				return -1;
			}
			if (file2 == null) {
				return 1;
			}

			return file1.getName().compareTo(file2.getName());
		}
	};

	/**
	 * Regex to use withe getListFile(File, String)
	 * pour filtrer tous les fichier <rootname>.xml d'un repertoire
	 * 
	 * @param rootName
	 * @return
	 */
	public static String createRegexXml(String rootName) {

		return "(" + rootName + ")" + REG_XML + REG_END;
	}

	/**
	 * Regex to use withe getListFile(File, String)
	 * pour filtrer tous les fichier <rootname>.xml.partx d'un repertoire
	 * 
	 * @param rootName
	 * @return
	 */
	public static String createRegexXmlPart(String rootName) {

		return "(" + rootName + ")" + REG_XML_PART + REG_END;
	}

	/**
	 * Regex to use withe getListFile(File, String)
	 * pour filtrer tous les fichier <rootname>.(xml|log|xml.partx|txt) d'un
	 * repertoire
	 * 
	 * @param rootName
	 * @return
	 */
	public static String createRegexXmlXmlPartLogTxt(String rootName) {

		return "(" + rootName + ")" + REG_XML_XMLPART_LOG_TXT + REG_END;
	}

	/**
	 * Suppression de tous les fichiers d'un répertoire
	 * 
	 * @param dirPathname
	 */
	public static boolean deleteAllFiles(String dirPathname) {
		return deleteAllFiles(new File(dirPathname));
	}

	public static boolean deleteAllFiles(File dir) {

		if (verifyDirectory(dir, false)) {

			File[] files = dir.listFiles(FILE_FILTER);

			return deleteListFiles(Arrays.asList(files));
		}
		return false;
	}

	public static boolean deleteDirectory(File dir) {

		if (verifyDirectory(dir, false)) {
			return dir.delete();
		}
		return false;
	}

	/**
	 * Supprime recursivement un repertoire et tout son contenu
	 * 
	 * @param dir
	 * @return
	 */
	public static boolean deleteDirectoryAndAllContent(File dir) {

		if (verifyDirectory(dir, false)) {

			// on supprime d'abord les fichiers
			deleteAllFiles(dir);

			// on récupère les sous repertoires
			File[] dirs = dir.listFiles(DIR_FILTER);
			if (dirs != null) {

				// for each sub directory
				for (File subDir : dirs) {
					deleteDirectoryAndAllContent(subDir);
				}
			}

			// on supprime le repertoire lui-meme
			return dir.delete();
		}
		return false;
	}

	/**
	 * supprime tous les fichiers de la liste
	 * (ne supprime pas les repertoires)
	 * 
	 * @param listFile
	 * @return
	 */
	public static boolean deleteListFiles(List<File> listFile) {

		if (listFile != null && !listFile.isEmpty()) {
			for (File file : listFile) {
				if (file.isFile()) {
					file.delete();
				}
			}
			return true;
		}

		return false;
	}

	/*
	 * Récupère la racine du nom du fichier sans l'extension
	 * pour les fichiers correspondant au pattern
	 */
	public static String getFileNameNoExt(File file, Pattern pattern) {
		String name = file.getName();

		Matcher m = pattern.matcher(name);
		if (m.find()) {
			return m.group(1);
		} else {
			return name;
		}

	}

	/*
	 * Récupère l'extension du  fichier 
	 * pour les fichiers correspondant au pattern
	 */
	public static String getExtFromFile(File file, Pattern pattern) {
		String name = file.getName();

		Matcher m = pattern.matcher(name);
		if (m.find()) {
			return m.group(2);
		} else {
			return name;
		}

	}

	/*
	 * Recupere la version d'un nom de fichier de type 
	 *  <groupPrefix><version>
	 */
	public static String getVersionFromRootName(String groupPrefix, String rootname) {

		Pattern pattern = Pattern.compile("(" + groupPrefix + ")" + REG_VERSION_FROM_ROOTNAME);

		Matcher m = pattern.matcher(rootname);
		if (m.find()) {
			return m.group(2);
		} else {
			return null;
		}
	}

	public static List<File> getListFileWithFilter(File directory, FileFilter filter) {

		if (filter == null) {
			return getListAllFiles(directory);
		}
		if (!verifyDirectory(directory, false)) {
			return null;
		}
		File[] files = directory.listFiles(filter);

		return Arrays.asList(files);
	}

	public static List<File> getListAllFiles(File directory) {

		if (!verifyDirectory(directory, false)) {
			return null;
		}
		File[] files = directory.listFiles();
		return Arrays.asList(files);

	}

	public static List<File> getListFile(File directory, final String pattern) {

		if (pattern == null) {
			return getListAllFiles(directory);
		}
		if (!verifyDirectory(directory, false)) {
			return null;
		}
		File[] files = directory.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f) {
				String name = f.getName();
				return f.isFile() && name.matches(pattern);
			}
		});

		return Arrays.asList(files);

	}

	/**
	 * Verify que la list des fichier donnés ont le meme rootname
	 * 
	 * @param listFiles
	 *            (xml, txt, log, xml.partx)
	 * @param pattern
	 * @return
	 */
	public static boolean verifySameRootnames(List<File> listFiles) {

		if (listFiles == null || listFiles.isEmpty()) {
			return false;
		}

		String firstRootName = null;
		for (File file : listFiles) {
			String root = getFileNameNoExt(file, PATTERN_ROOT_XML_XMLPART_LOG_TXT);
			if (firstRootName == null) {
				firstRootName = root;
			} else {
				if (!firstRootName.equals(root)) {
					return false;
				}
			}
		}
		return true;
	}

	public static boolean copyFilesToTargetDir(List<File> filesToCopy, File targetDir, boolean forceCreate)
			throws JUnitHistoryException {

		if (filesToCopy == null || filesToCopy.isEmpty() || targetDir == null) {
			return false;
		}
		if (verifyDirectory(targetDir, forceCreate)) {

			boolean result = true;
			// for each file
			for (File file : filesToCopy) {

				if (!copyFile(file, new File(targetDir, file.getName()))) {
					result = false;
				}
			}
			return result;
		}

		return false;
	}

	public static boolean copyFile(File srcFile, File targetFile) throws JUnitHistoryException {

		if (!verifyFile(srcFile, false)) {
			return false;
		}
		InputStream in;
		try {
			in = new FileInputStream(srcFile);
			return copyInputStream(in, targetFile);
		} catch (FileNotFoundException e) {
			throw new JUnitHistoryException("FileNotFoundException: " + e.getMessage());
		}

	}

	public static boolean copyInputStream(InputStream in, File targetFile) throws JUnitHistoryException {

		if (in == null) {
			log.severe("inputstream null!");
			return false;
		}
		if (targetFile == null) {
			log.severe("targetfile null!");
			try {
				in.close();
			} catch (IOException ignored) {
			}
			return false;
		}

		log.config("copyInputStream to " + targetFile.getAbsolutePath());
		if (verifyDirectory(targetFile.getParentFile(), true)) {
			BufferedInputStream bis = null;
			BufferedOutputStream bos = null;

			try {

				bos = new BufferedOutputStream(new FileOutputStream(targetFile));
				bis = new BufferedInputStream(in);

				byte[] buf = new byte[1024];
				int bytesRead = 0;

				while ((bytesRead = bis.read(buf)) != -1) {
					bos.write(buf, 0, bytesRead);
				}

				return true;
			} catch (Exception e) {
				log.severe(e.toString());
			} finally {

				try {
					if (bis != null) {
						bis.close();
					}
					if (bos != null) {
						bos.flush();
						bos.close();
					}
				} catch (IOException ignored) {
				}
			}

		}
		return false;
	}

	public static boolean verifyDirectory(File dir, boolean forceCreate) {

		String path = dir.getAbsolutePath();
		log.config("verifyDirectory(): " + path);
		boolean dirExists = dir.exists() && dir.isDirectory() && dir.canRead();
		if (!dirExists && forceCreate) {
			log.info("create dir: " + path);
			dirExists = dir.mkdirs();
		}

		return dirExists;

	}

	public static boolean verifyFile(File file, boolean canWrite) {

		return file.exists() && file.isFile() && file.canRead() && (canWrite ? file.canWrite() : true);
	}

	/*
	 * Par defaut on cree un nouveau fichier
	 */
	public static boolean writeFile(File file, ListLines lines) throws JUnitHistoryException {
		return writeFile(file, lines, false);
	}

	public static boolean writeFile(File file, ListLines lines, boolean append) throws JUnitHistoryException {

		if (file == null || lines == null) {
			return false;
		}
		boolean success = true;
		verifyDirectory(file.getParentFile(), true);
		BufferedWriter writer = null;

		try {

			writer = new BufferedWriter(new FileWriter(file, append));

			for (String line : lines.getLines()) {
				writer.append(line);
				writer.newLine();
			}

		} catch (Exception e) {
			throw new JUnitHistoryException(e);
		} finally {
			if (writer != null) {
				try {
					writer.flush();
					writer.close();
				} catch (IOException ignored) {
				}
			}
		}
		return success;
	}

}
