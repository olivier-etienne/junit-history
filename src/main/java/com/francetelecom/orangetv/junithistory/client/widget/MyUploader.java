package com.francetelecom.orangetv.junithistory.client.widget;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.moxieapps.gwt.uploader.client.Uploader;
import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteEvent;
import org.moxieapps.gwt.uploader.client.events.FileDialogCompleteHandler;
import org.moxieapps.gwt.uploader.client.events.FileDialogStartEvent;
import org.moxieapps.gwt.uploader.client.events.FileDialogStartHandler;
import org.moxieapps.gwt.uploader.client.events.FileQueueErrorEvent;
import org.moxieapps.gwt.uploader.client.events.FileQueueErrorHandler;
import org.moxieapps.gwt.uploader.client.events.FileQueuedEvent;
import org.moxieapps.gwt.uploader.client.events.FileQueuedHandler;
import org.moxieapps.gwt.uploader.client.events.UploadCompleteEvent;
import org.moxieapps.gwt.uploader.client.events.UploadCompleteHandler;
import org.moxieapps.gwt.uploader.client.events.UploadErrorEvent;
import org.moxieapps.gwt.uploader.client.events.UploadErrorHandler;
import org.moxieapps.gwt.uploader.client.events.UploadProgressEvent;
import org.moxieapps.gwt.uploader.client.events.UploadProgressHandler;

import com.francetelecom.orangetv.junithistory.client.presenter.SingleReportPresenter.UploadResult;
import com.francetelecom.orangetv.junithistory.client.presenter.SingleReportPresenter.UploadState;
import com.francetelecom.orangetv.junithistory.client.util.CssConstants;
import com.francetelecom.orangetv.junithistory.client.util.StatusUtils;
import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Image;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class MyUploader extends Uploader implements CssConstants {

	private final static Logger log = Logger.getLogger("MyUploader");

	private final int maxSizeMo;

	private HorizontalPanel uploadPanel = null;
	private final VerticalPanel multiProgressBarPanel = new VerticalPanel();

	private final Map<String, Label> mapFileId2LabelState = new LinkedHashMap<String, Label>();
	private final Map<String, Label> mapFileId2LabelFilename = new LinkedHashMap<String, Label>();

	private final Map<String, ProgressBar> mapFileId2ProgressBars = new LinkedHashMap<String, ProgressBar>();
	private final Map<String, Image> mapFileId2CancelButtons = new LinkedHashMap<String, Image>();

	private final Map<String, UploadState> mapFileId2UploadState = new LinkedHashMap<>();

	private boolean onloadDone = false;

	// ------------------------------- overriding Uploader

	@Override
	protected void onLoad() {

		log.config("onLoad()");

		if (!onloadDone) {
			if (isAjaxUploadWithProgressEventsSupported()) {

				super.onLoad();
				this.onloadDone = true;
			} else {
				log.severe("onload error.");
			}
		}
	}

	// --------------------------------- constructor
	public MyUploader(int maxSizeMo) {
		this.maxSizeMo = maxSizeMo;
	}

	// ------------------------------- public methods
	public Panel getUploadPanel() {

		return this.buildUploadAndProgressBar();
	}

	public void setUploadHandler(UploadHandler uploadHandler) {
		this._setUploadHandler(uploadHandler);
	}

	// ------------------------------------------- private methods

	private void clearMaps() {
		mapFileId2ProgressBars.clear();
		mapFileId2CancelButtons.clear();
		mapFileId2LabelState.clear();
		mapFileId2LabelFilename.clear();
		mapFileId2UploadState.clear();
	}

	/*
	onFileDialogStartEvent()
	onFileQueued(): testSfau2.ts - id: Uploader_1
	onFileDialogComplete(): 1(total in queue!)
	in progress: 0
	in progress: 98304
	in progress: 3964928
	in progress: 4232271
	in progress: 4232068
	onUploadComplete(): Uploader_1
	 */
	private Panel buildUploadAndProgressBar() {

		if (this.uploadPanel != null) {
			return this.uploadPanel;
		}
		this.uploadPanel = new HorizontalPanel();

		log.config("buildUploadAndProgressBar()... setButton...");
		// uploader à tester
		this.setUploadURL("fileupload")
		// uploader.setUploadURL("/JUnitHistory/fileupload")
				.setButtonText("Choose file...").setFileSizeLimit(maxSizeMo + " MB") // ...
				.setButtonCursor(Uploader.Cursor.HAND) // ...
				.setButtonAction(Uploader.ButtonAction.SELECT_FILES); // ...

		this.addStyleName(BUTTON_VIEW_UPLOAD);
		this.uploadPanel.add(this);
		this.uploadPanel.add(multiProgressBarPanel);
		this.uploadPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		this.uploadPanel.setCellHorizontalAlignment(this, HorizontalPanel.ALIGN_RIGHT);

		return this.uploadPanel;

	}

	private boolean isUploadCanceldOrInError(String id) {
		UploadState uploadState = mapFileId2UploadState.get(id);
		return uploadState == UploadState.error || uploadState == UploadState.canceled;

	}

	private void _setUploadHandler(final UploadHandler uploadHandler) {

		this.multiProgressBarPanel.clear();
		this.clearMaps();

		// uploader à tester
		this

		// ouverture de la boite de dialog de choix des fichiers
		.setFileDialogStartHandler(new FileDialogStartHandler() { // ...
					public boolean onFileDialogStartEvent(FileDialogStartEvent fileDialogStartEvent) {
						log.config("onFileDialogStartEvent()");

						if (MyUploader.this.getStats().getUploadsInProgress() <= 0) {
							// Clear the uploads that have completed, if
							// none are in process
							multiProgressBarPanel.clear();
							clearMaps();
							uploadHandler.onOpenDialog();

						}
						return true;
					}
				}) // ...

				// un fichier se prépare a etre uploade
				// >> build progressbar
				.setFileQueuedHandler(new FileQueuedHandler() { // ...
							public boolean onFileQueued(final FileQueuedEvent fileQueuedEvent) {

								final String filename = fileQueuedEvent.getFile().getName();
								final String id = fileQueuedEvent.getFile().getId();
								log.config("onFileQueued(): " + filename + " - id: " + id);
								uploadHandler.onQueue(filename);
								// Create a Progress Bar for this file
								// Create a Progress Bar for this file
								final ProgressBar progressBar = new ProgressBar(0.0, 1.0, 0.0,
										new CancelProgressBarTextFormatter());

								progressBar.setTitle(fileQueuedEvent.getFile().getName());
								progressBar.setHeight("18px");
								progressBar.setWidth("200px");
								mapFileId2ProgressBars.put(id, progressBar);

								// Add Cancel Button Image
								final Image cancelButton = new Image(GWT.getModuleBaseURL() + "images/cancel.png");
								cancelButton.setStyleName("cancelButton");
								cancelButton.addClickHandler(new ClickHandler() {
									public void onClick(ClickEvent event) {
										mapFileId2UploadState.put(id, UploadState.canceled);
										MyUploader.this.cancelUpload(id, false);
										mapFileId2ProgressBars.get(id).setProgress(-1.0d);
										cancelButton.removeFromParent();
										uploadHandler.onCancel(filename);
										setUploadResult(new UploadResult(id, UploadState.canceled));

									}
								});
								mapFileId2CancelButtons.put(id, cancelButton);

								// add label name and state
								final Label labelState = new Label();
								mapFileId2LabelState.put(id, labelState);
								final Label labelFilename = new Label();
								labelFilename.setText(filename);
								mapFileId2LabelFilename.put(id, labelFilename);

								multiProgressBarPanel.add(buildProgressBarPanel(progressBar, cancelButton, labelState,
										labelFilename));

								uploadHandler.onQueue(filename);
								mapFileId2UploadState.put(id, UploadState.started);
								setUploadResult(new UploadResult(id, UploadState.started));

								return true;
							}
						}) // ...

				// fermeture de la boite de dialog
				// lancement du premier upload
				.setFileDialogCompleteHandler(new FileDialogCompleteHandler() { // ...
							public boolean onFileDialogComplete(FileDialogCompleteEvent fileDialogCompleteEvent) {

								final int tot = fileDialogCompleteEvent.getTotalFilesInQueue();
								log.config("onFileDialogComplete(): " + tot + "(total in queue!)");
								if (tot > 0 && MyUploader.this.getStats().getUploadsInProgress() <= 0) {
									uploadHandler.beforeStarting(tot);
									MyUploader.this.startUpload();

								}
								return true;
							}
						}) // ...

				// progressBar affiche la progression de chaque upload
				.setUploadProgressHandler(new UploadProgressHandler() { // ...
							public boolean onUploadProgress(UploadProgressEvent uploadProgressEvent) {

								String id = uploadProgressEvent.getFile().getId();

								if (!isUploadCanceldOrInError(id)) {
									log.config("in progress: " + uploadProgressEvent.getBytesComplete());
									ProgressBar progressBar = mapFileId2ProgressBars.get(uploadProgressEvent.getFile()
											.getId());
									progressBar.setProgress((double) uploadProgressEvent.getBytesComplete()
											/ uploadProgressEvent.getBytesTotal());
								}
								return true;
							}
						}) // ...

				// fin du upload de chaque fichier
				// lancement du suivant
				// toujours appellé meme après un cancel ou une erreur
				.setUploadCompleteHandler(new UploadCompleteHandler() {
					public boolean onUploadComplete(UploadCompleteEvent uploadCompleteEvent) {

						final String id = uploadCompleteEvent.getFile().getId();

						if (!isUploadCanceldOrInError(id)) {
							log.config("onUploadComplete(): " + id);
							mapFileId2CancelButtons.get(id).removeFromParent();
							mapFileId2UploadState.put(id, UploadState.done);
							setUploadResult(new UploadResult(id, UploadState.done));
						}

						// Call upload to see if any additional files are queued
						MyUploader.this.startUpload();

						if (!isUploadCanceldOrInError(id)) {
							final String filename = uploadCompleteEvent.getFile().getName();
							final long size = uploadCompleteEvent.getFile().getSize();
							uploadHandler.onFinish(new UploadInfo(id, filename, size));
						}
						return true;
					}
				}) // ...

				// en cas d'erreur
				.setFileQueueErrorHandler(new FileQueueErrorHandler() { // ...
							public boolean onFileQueueError(FileQueueErrorEvent fileQueueErrorEvent) {

								final String filename = fileQueueErrorEvent.getFile().getName();
								final String id = fileQueueErrorEvent.getFile().getId();

								log.severe("onFileQueueError(): " + filename);
								final String errorMessage = "failed due to ["
										+ fileQueueErrorEvent.getErrorCode().toString() + "]: "
										+ fileQueueErrorEvent.getMessage();

								mapFileId2UploadState.put(id, UploadState.error);
								uploadHandler.onError(filename, errorMessage);
								return true;
							}
						}) // ..

				// en cas d'erreur
				.setUploadErrorHandler(new UploadErrorHandler() {
					public boolean onUploadError(UploadErrorEvent uploadErrorEvent) {

						final String filename = uploadErrorEvent.getFile().getName();
						final String id = uploadErrorEvent.getFile().getId();
						log.severe("onUploadError(): " + filename);
						mapFileId2CancelButtons.get(id).removeFromParent();
						final String errorMessage = "failed due to [" + uploadErrorEvent.getErrorCode().toString()
								+ "]: " + uploadErrorEvent.getMessage();

						mapFileId2UploadState.put(id, UploadState.error);
						setUploadResult(new UploadResult(id, UploadState.error));

						uploadHandler.onError(filename, errorMessage);
						return true;
					}
				});

	}

	private Panel buildProgressBarPanel(ProgressBar progressBar, Image cancelButton, Label labelState,
			Label labelFilename) {

		HorizontalPanel progressBarAndButtonPanel = new HorizontalPanel();
		progressBarAndButtonPanel.setSpacing(PANEL_SPACING);
		progressBarAndButtonPanel.setVerticalAlignment(HasVerticalAlignment.ALIGN_TOP);

		progressBarAndButtonPanel.add(progressBar);
		progressBarAndButtonPanel.add(cancelButton);
		labelState.setVisible(false);
		progressBarAndButtonPanel.add(labelState);
		labelFilename.setVisible(false);
		progressBarAndButtonPanel.add(labelFilename);

		return progressBarAndButtonPanel;

	}

	private void setUploadResult(UploadResult uploadResult) {

		String id = uploadResult.getId();
		if (id != null) {

			Label labelFilename = this.mapFileId2LabelFilename.get(id);
			if (labelFilename != null) {
				StatusUtils.buildLogLabel(labelFilename, uploadResult.getUploadState().getLogStatus());
				labelFilename.setVisible(true);

				Label labelState = this.mapFileId2LabelState.get(id);
				if (labelState != null) {
					StatusUtils.buildLabelStatus(labelState, uploadResult.getUploadState());
					labelState.setVisible(true);
				}
			}

		}
	}

	// ===================================== INNER CLASS
	protected class CancelProgressBarTextFormatter extends ProgressBar.TextFormatter {
		@Override
		protected String getText(ProgressBar bar, double curProgress) {
			if (curProgress < 0) {
				return "Cancelled";
			}
			return ((int) (100 * bar.getPercent())) + "%";
		}
	}

	public static interface UploadHandler {

		public void onOpenDialog();

		public void beforeStarting(int countOfFile);

		public void onQueue(String filename);

		public void onCancel(String filename);

		public void onError(String filename, String error);

		public void onFinish(UploadInfo uploadInof);

	}

	public static class UploadInfo {

		private final String id;
		private final String filename;
		private final long size;

		private UploadInfo(String id, String filename, long size) {
			this.id = id;
			this.filename = filename;
			this.size = size;
		}

		public String getId() {
			return id;
		}

		public String getFilename() {
			return filename;
		}

		public long getSize() {
			return size;
		}

	}

}
