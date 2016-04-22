package com.perk.perksdk.adblocker;

public class NotEnoughSpaceException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8078863661638980424L;

	public NotEnoughSpaceException() {
	}

	public NotEnoughSpaceException(String msg) {
		super(msg);
	}
}