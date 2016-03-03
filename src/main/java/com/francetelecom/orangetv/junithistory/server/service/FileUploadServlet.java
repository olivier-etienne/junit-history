package com.francetelecom.orangetv.junithistory.server.service;

import gwtupload.server.exceptions.UploadActionException;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;

import com.francetelecom.orangetv.junithistory.server.manager.PathManager;
import com.francetelecom.orangetv.junithistory.server.util.FileUtils;
import com.francetelecom.orangetv.junithistory.server.util.SessionHelper;

public class FileUploadServlet extends AbstractServlet {
	private static final long serialVersionUID = 1L;
	private long FILE_SIZE_LIMIT = 200 * 1024 * 1024; // 200 MiB
	private static final Logger log = Logger.getLogger(FileUploadServlet.class.getName());

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.info("doGet()");

		resp.setHeader("Access-Control-Allow-Origin", "*");

		PrintWriter out = resp.getWriter();
		out.println("<html>");
		out.println("<body>");
		out.println("<h1>FileUploadServlet HTTP GET</h1>");
		out.println("</body>");
		out.println("</html>");

	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		log.info("doPost()");
		try {
			DiskFileItemFactory fileItemFactory = new DiskFileItemFactory();
			ServletFileUpload fileUpload = new ServletFileUpload(fileItemFactory);
			fileUpload.setSizeMax(FILE_SIZE_LIMIT);

			List<FileItem> items = fileUpload.parseRequest(req);
			String response = this.executeAction(req, resp, items);

			PrintWriter out = resp.getWriter();
			out.println("<html>");
			out.println("<body>");
			out.println("<h1>FileUploadServlet HTTP POST</h1>");
			out.println("<h2>" + response + "</h2>");
			out.println("</body>");
			out.println("</html>");

		} catch (Exception e) {
			log.severe("Throwing servlet exception for unhandled exception" + e.getMessage());
			throw new ServletException(e);
		}
	}

	// ------------------------------------------------- private methods
	private String executeAction(HttpServletRequest request, HttpServletResponse resp, List<FileItem> sessionFiles)
			throws UploadActionException {
		log.config("executeAction()");

		String response = "";
		if (sessionFiles == null || sessionFiles.size() > 1) {
			response = "wrong session files...";
		} else {

			final HttpSession session = request.getSession(true);
			// sauvegarde du fichier sur le server
			String path = PathManager.get().getUploadXmlPathnameForSession(session.getId());
			if (path == null) {
				throw new UploadActionException("cannot find path to upload!!");
			}

			// on efface tous les fichiers du repertoire si on débute un nouvel
			// upload
			if (session.isNew()
					|| SessionHelper.getBooleanAttribute(session, KEY_SESSION_EVENT_START_NEW_UPLOAD, false)) {
				FileUtils.deleteAllFiles(path);
				session.setAttribute(KEY_SESSION_EVENT_START_NEW_UPLOAD, false);
				session.setAttribute(KEY_SESSION_EVENT_UPLOAD_ENDED, false);
				session.setAttribute(KEY_SESSION_GWTUPLOADED_DIR, path);

			}

			// for each fileItem
			for (FileItem fileItem : sessionFiles) {

				if (false == fileItem.isFormField()) {
					try {

						if (fileItem.getSize() > FILE_SIZE_LIMIT) {
							resp.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, "File size exceeds limit");
						}
						// / Create a new file based on the remote file name in
						// the
						// client
						String saveName = fileItem.getName().replaceAll("[\\\\/><\\|\\s\"'{}()\\[\\]]+", "_");

						log.info("path: " + path);
						File savedFile = new File(path, saveName);
						fileItem.write(savedFile);

						// Sauvegarde du path dans la session
						String uploadedPath = savedFile.getAbsolutePath();

						// / Send a customized message to the client.
						response += "File saved as " + uploadedPath;

						if (!fileItem.isInMemory()) {
							fileItem.delete();
						}

					} catch (Exception e) {
						throw new UploadActionException(e);
					}
				}
			}

		}

		// / Send your customized message to the client.
		return response;

	}

}
