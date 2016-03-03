package com.francetelecom.orangetv.junithistory.server.util;

import com.francetelecom.orangetv.junithistory.shared.util.JUnitHistoryException;
import com.francetelecom.orangetv.junithistory.shared.vo.VoDatasValidation;

public abstract class AbstractValidator {

	protected long itemsToLongValue(int[] items, int[] digits) {

		int length = items.length;
		long value = 0;

		byte exposant = 0;
		for (int i = 0; i < length; i++) {
			int digit = digits[i];

			value += items[length - (i + 1)] * ((long) Math.pow(10, exposant));
			exposant += digit;
		}

		return value;
	}

	protected String getRange(Number valueInf, Number valueSup) {
		return "[" + valueInf + ", " + valueSup + "]";
	}

	protected void validateInOutRange(boolean inRange, int value, int valueInf, int valueSup, String comment,
			String requiredRange) throws JUnitHistoryException {

		String rangeMessage = this.getInOutRangeMessage(inRange, valueInf, valueSup, comment, requiredRange);

		// value in [valueInf, valueSup]
		if (inRange) {
			if (!(value >= valueInf && value <= valueSup)) {
				throw new JUnitHistoryException(rangeMessage);
			}
		}
		// value out of [valueInf, valueSup]
		else {
			if ((value >= valueInf && value <= valueSup)) {
				throw new JUnitHistoryException(rangeMessage);
			}
		}
	}

	protected void validateInOutRange(boolean inRange, long value, long valueInf, long valueSup, String comment,
			String requiredRange) throws JUnitHistoryException {

		if (valueSup < 0 || valueInf < 0) {
			return;
		}

		requiredRange = (requiredRange == null) ? "[" + valueInf + ", " + valueSup + "]" : requiredRange;
		// value in [valueInf, valueSup]
		if (inRange) {
			if (!(value >= valueInf && value <= valueSup)) {
				throw new JUnitHistoryException(comment + " must be in " + requiredRange + " range!");
			}
		}
		// value out of [valueInf, valueSup]
		else {
			if ((value >= valueInf && value <= valueSup)) {
				throw new JUnitHistoryException(comment + " must be out of " + requiredRange + " range!");
			}
		}
	}

	/**
	 * 
	 * @param toValidate
	 * @param digits
	 *            : count max of digit
	 * @param comment
	 * @return
	 * @throws JUnitHistoryException
	 */
	protected int validateNumber(String toValidate, int digits, String comment) throws JUnitHistoryException {

		int value = this.validateNumber(toValidate, comment);
		if (digits <= 0) {
			return value;
		}

		int maxValue = (int) Math.pow(10, digits);
		if (!(value < (maxValue))) {
			throw new JUnitHistoryException("The " + comment + " must be < " + maxValue + "!");
		}
		return value;
	}

	protected int validateNumber(String toValidate, String comment) throws JUnitHistoryException {

		int value = Integer.MAX_VALUE;
		try {
			value = Integer.parseInt(toValidate);
		} catch (NumberFormatException e) {
			throw new JUnitHistoryException("The " + comment + " is not a number!");
		}
		return value;
	}

	protected void validateRequired(String toValidate, String comment, VoDatasValidation voValidation) {
		if (toValidate == null || toValidate.length() == 0) {
			voValidation.getErrorMessages().add("The " + comment + " is required!");
		}
	}

	protected void validateNotNull(Object toValidate, String comment, VoDatasValidation voValidation) {
		if (toValidate == null) {
			voValidation.getErrorMessages().add("The " + comment + " cannot be null!");
		}
	}

	protected void validateNull(Object toValidate, String comment, VoDatasValidation voValidation) {
		if (toValidate != null) {
			voValidation.getErrorMessages().add("The " + comment + " must be null!");
		}
	}

	protected void validateIntValue(int toValidate, int minValue, String comment) throws JUnitHistoryException {

		if (toValidate <= minValue) {
			String message = (minValue == 0) ? " cannot be null or negative!" : " must been > " + minValue;
			throw new JUnitHistoryException(comment + message);
		}
	}

	protected void validateString(String toValidate, int minLength, String comment, VoDatasValidation voValidation) {
		if (toValidate == null || toValidate.length() < minLength) {
			voValidation.getErrorMessages()
					.add("The " + comment + " must have more than " + minLength + " characters!");
		}
	}

	private String getInOutRangeMessage(boolean inRange, Number valueInf, Number valueSup, String comment,
			String requiredRange) {

		if (valueSup == null || valueInf == null) {
			return null;
		}

		requiredRange = (requiredRange == null) ? this.getRange(valueInf, valueSup) : requiredRange;
		String rangeMessage = (inRange) ? comment + " must be in " + requiredRange + " range!" : comment
				+ " must be out of " + requiredRange + " range!";

		return rangeMessage;
	}

}
