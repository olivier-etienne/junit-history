package com.francetelecom.orangetv.junithistory.client.widget;

import java.util.Date;

import com.francetelecom.orangetv.junithistory.client.util.CssConstants;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.i18n.client.DateTimeFormat;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HasVerticalAlignment;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.datepicker.client.DateBox;

public class LabelAndDateWidget extends Composite implements CssConstants {

	private final Label label;;
	private final DateBox dateBox = new DateBox();

	// -------------------------------------- accessors
	public Label getLabel() {
		return label;
	}

	public DateBox getDateBox() {
		return dateBox;
	}

	// ------------------------------------------ public methods
	public Date getUserInput() {

		return this.dateBox.getValue();
	}

	/**
	 * Set the code/value of a list item
	 * 
	 * @param value
	 */
	public void setValue(Date date) {
		this.dateBox.setValue(date);
	}

	public void setEnabled(boolean enabled) {
		if (this.dateBox != null) {
			this.dateBox.setEnabled(enabled);
		}
	}

	// --------------------------------------------- constructor

	public LabelAndDateWidget(final String labelText, final int labelWidth, final int boxWidth,
			DateTimeFormat dateTimeFormat, Date defaultDate) {

		this.label = new Label(labelText);
		this.label.setWidth(labelWidth + "px");
		this.dateBox.setWidth(boxWidth + "px");

		if (dateTimeFormat != null) {
			this.dateBox.setFormat(new DateBox.DefaultFormat(dateTimeFormat));
		}

		final HorizontalPanel panel = new HorizontalPanel();
		panel.setSpacing(PANEL_SPACING);
		panel.setVerticalAlignment(HasVerticalAlignment.ALIGN_MIDDLE);
		panel.setStyleName(CssConstants.PANEL_INPUT);
		panel.add(label);
		panel.add(this.dateBox);

		if (defaultDate != null) {
			this.setValue(defaultDate);
		}

		this.initWidget(panel);
	}

	public void addValueChangeHandler(ValueChangeHandler<Date> handler) {
		this.dateBox.addValueChangeHandler(handler);
	}

}
