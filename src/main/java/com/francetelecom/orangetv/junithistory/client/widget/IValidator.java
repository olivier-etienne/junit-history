package com.francetelecom.orangetv.junithistory.client.widget;

import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;

public interface IValidator<T> {

	public void validate(T value) throws JUnitHistoryException;

	public String getComment();

	// TODO systeme de validation non satisfaisant. A revoir
	public T getCorrectedValue(T value);
}
