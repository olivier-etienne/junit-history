package com.francetelecom.orangetv.junithistory.client.panel.analysis;

import com.francetelecom.orangetv.junithistory.client.util.CssConstants;
import com.francetelecom.orangetv.junithistory.client.util.StatusUtils;
import com.francetelecom.orangetv.junithistory.client.view.AnalysisView;
import com.francetelecom.orangetv.junithistory.client.view.AnalysisView.CreateCommentButton;
import com.francetelecom.orangetv.junithistory.client.view.AnalysisView.DeleteCommentButton;
import com.francetelecom.orangetv.junithistory.client.view.AnalysisView.EditCommentButton;
import com.francetelecom.orangetv.junithistory.client.widget.LabelAndBoxWidget;
import com.francetelecom.orangetv.junithistory.shared.TestSubStatusEnum;
import com.francetelecom.orangetv.junithistory.shared.vo.VoItemProtection;
import com.francetelecom.orangetv.junithistory.shared.vo.VoTestInstanceForEdit;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.DisclosurePanel;
import com.google.gwt.user.client.ui.HasHorizontalAlignment;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.SimplePanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;

/**
 * Panel pour les informations d'un TestInstance
 * 
 * <ul>
 * <li>message type
 * <li>message output log & stacktrace
 * <li>comment (title, user, date) & description
 * 
 * </ul>
 * 
 * @author NDMZ2720
 *
 */
public final class TestInfoPanel extends SimplePanel implements CssConstants {

	private VerticalPanel vpBody = new VerticalPanel();

	private CreateCommentButton btCreateComment;
	private DeleteCommentButton btDeleteComment;
	private EditCommentButton btEditComment;

	private Panel suiteNamePanel = new SimplePanel();
	private HorizontalPanel panelButton = new HorizontalPanel();
	private Label labelSuiteDate = new Label();
	private Label labelTestStatus = new Label();

	private LabelAndBoxWidget wTypeBox = new LabelAndBoxWidget("Type", 50, 500);
	private LabelAndBoxWidget wMessageBox = new LabelAndBoxWidget("Message", 50, 500);

	private DisclosurePanel disPanComment = null;
	private DisclosurePanel disPanOutput = null;
	private DisclosurePanel disPanStack = null;

	private TextArea taComment = new TextArea();
	private TextArea taOutputLogs = new TextArea();
	private TextArea taStackTrace = new TextArea();

	private ClickHandler actionClickHandler;

	// ------------------------------ constructor
	public TestInfoPanel() {
		this.initComposants();
		this.add(this.buildBodyPanel());
	}

	// ---------------------------------------- public methods
	public void setTestActionClickHandler(ClickHandler actionClickHandler) {
		this.actionClickHandler = actionClickHandler;
	}

	public void setDatas(VoTestInstanceForEdit voTest) {

		this.suiteNamePanel.add(new Label(voTest.getSuiteName()));
		this.labelSuiteDate.setText(voTest.getSuiteDate());
		StatusUtils.buildTestStatus(this.labelTestStatus, TestSubStatusEnum.valueOf(voTest.getStatus()));

		boolean messageVisible = voTest.getType() != null;
		boolean commentVisible = voTest.hasComment();
		if (!messageVisible && !commentVisible) {
			this.vpBody.setVisible(false);
		}

		else {

			if (voTest.getType() != null) {
				this.wTypeBox.setValue(voTest.getType());
				this.wMessageBox.setValue(voTest.getMessage());
				this.taOutputLogs.setValue(voTest.getOutputLog());
				this.taStackTrace.setValue(voTest.getStackTrace());
			}
			this.setMessageVisible(messageVisible);

			// comment
			if (voTest.hasComment()) {
				this.taComment.setValue(voTest.getTcomment());
			}
			this.setCommentVisible(commentVisible);

		}

		// buttons
		VoItemProtection protection = voTest.getProtection();
		int testId = voTest.getId();
		int tcommentId = voTest.getTcommentId();
		// create button
		this.manageCreateCommentButton(!voTest.hasComment(), true, protection, testId);
		// edit button
		this.manageEditCommentButton(voTest.hasComment(), true, protection, testId, tcommentId);
		// delete button
		this.manageDeleteCommentButton(voTest.hasComment(), true, protection, testId, tcommentId);
	}

	// ---------------------------- private methods
	private void setCommentVisible(boolean visible) {
		this.disPanComment.setVisible(visible);
	}

	private void setMessageVisible(boolean visible) {
		this.wTypeBox.setVisible(visible);
		this.wMessageBox.setVisible(visible);
		this.disPanOutput.setVisible(visible);
		this.disPanStack.setVisible(visible);
	}

	private void manageCreateCommentButton(boolean visible, boolean forceCreate, VoItemProtection protection, int testId) {

		if (protection == null || protection.canEdit()) {
			if (this.btCreateComment == null && forceCreate) {
				this.btCreateComment = new CreateCommentButton(testId);
				if (this.actionClickHandler != null) {
					this.btCreateComment.addClickHandler(actionClickHandler);
				}

				this.panelButton.add(this.btCreateComment);
			}
		}
		if (this.btCreateComment != null) {
			this.btCreateComment.setVisible(visible);
		}
	}

	private void manageEditCommentButton(boolean visible, boolean forceCreate, VoItemProtection protection, int testId,
			int commentId) {
		if (protection == null || protection.canEdit()) {
			if (this.btEditComment == null) {
				this.btEditComment = new EditCommentButton(testId, commentId);
				if (this.actionClickHandler != null) {
					this.btEditComment.addClickHandler(actionClickHandler);
				}
				this.panelButton.add(this.btEditComment);
			}
		}
		if (this.btEditComment != null) {
			this.btEditComment.setVisible(visible);
		}

	}

	private void manageDeleteCommentButton(boolean visible, boolean forceCreate, VoItemProtection protection,
			int testId, int commentId) {
		if (protection == null || protection.canDelete()) {
			if (this.btDeleteComment == null) {
				this.btDeleteComment = new DeleteCommentButton(testId, commentId);
				if (this.actionClickHandler != null) {
					this.btDeleteComment.addClickHandler(actionClickHandler);
				}
				this.panelButton.add(this.btDeleteComment);
			}
		}
		if (this.btDeleteComment != null) {
			this.btDeleteComment.setVisible(visible);
		}

	}

	private void initComposants() {

		this.setStyleName(AnalysisView.PANEL_TEST_INFO);

		this.taComment.setStyleName(TEXT_AREA_TEST_COMMENT);
		this.taComment.setEnabled(false);

		this.taOutputLogs.setStyleName(TEXT_AREA_TEST_INFO);
		this.taOutputLogs.setEnabled(false);

		this.taStackTrace.setStyleName(TEXT_AREA_TEST_INFO);
		this.taStackTrace.setEnabled(false);

		this.wTypeBox.setEnabled(false);
		this.wMessageBox.setEnabled(false);

		this.suiteNamePanel.addStyleName(STYLE_SUITE_NAME);
		this.labelSuiteDate.addStyleName(STYLE_SUITE_DATE);

		this.panelButton.setWidth("50px");

	}

	private Panel buildBodyPanel() {

		this.vpBody.setWidth(AnalysisView.MAX_WIDTH);

		final VerticalPanel vpPanel = new VerticalPanel();
		vpPanel.setSpacing(AnalysisView.PANEL_SPACING);
		vpPanel.setWidth(AnalysisView.MAX_WIDTH);
		vpPanel.setBorderWidth(1);

		vpPanel.add(this.buildTitlePanel());

		// body (peut etre masqué)
		this.vpBody.add(this.wTypeBox);
		this.vpBody.add(this.wMessageBox);

		this.disPanComment = this.buildDisclosurePanel("comments:", this.taComment, true);
		this.vpBody.add(this.disPanComment);

		this.disPanOutput = this.buildDisclosurePanel("output logs:", this.taOutputLogs, false);
		this.vpBody.add(this.disPanOutput);

		this.disPanStack = this.buildDisclosurePanel("stack trace:", this.taStackTrace, false);
		this.vpBody.add(this.disPanStack);

		vpPanel.add(this.vpBody);
		return vpPanel;
	}

	/*
	 * Test suite name / buttons / test suite date / test status
	 */
	private Panel buildTitlePanel() {

		final HorizontalPanel hpTitle = new HorizontalPanel();
		hpTitle.setSpacing(PANEL_SPACING);
		hpTitle.setWidth(MAX_WIDTH);
		hpTitle.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);

		hpTitle.add(this.suiteNamePanel);
		hpTitle.add(this.labelSuiteDate);
		hpTitle.setCellHorizontalAlignment(this.labelSuiteDate, HasHorizontalAlignment.ALIGN_CENTER);

		this.panelButton.setSpacing(PANEL_SPACING);
		hpTitle.add(this.panelButton);
		hpTitle.setCellHorizontalAlignment(this.panelButton, HasHorizontalAlignment.ALIGN_RIGHT);

		hpTitle.add(this.labelTestStatus);
		hpTitle.setCellHorizontalAlignment(this.labelTestStatus, HasHorizontalAlignment.ALIGN_RIGHT);

		return hpTitle;
	}

	private DisclosurePanel buildDisclosurePanel(String title, Widget body, boolean open) {
		DisclosurePanel disPan = new DisclosurePanel(title);
		disPan.add(body);
		disPan.setOpen(open);
		disPan.setWidth(MAX_WIDTH);
		return disPan;
	}

}