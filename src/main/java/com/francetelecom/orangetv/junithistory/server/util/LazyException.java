package com.francetelecom.orangetv.junithistory.server.util;

/**
 * Exception lancée quand on essai d'acceder à un attribut d'un objet lazy
 * 
 * @author ndmz2720
 *
 */
public class LazyException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public LazyException(String message) {
		super(message);
	}

}
