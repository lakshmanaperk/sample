package com.perk.perksdk.adblocker;

import java.io.IOException;

public class BrokenBusyboxException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 3550235733557917361L;

	public BrokenBusyboxException() {
		super();
	}

	public BrokenBusyboxException(String detailMessage) {
		super(detailMessage);
	}

}
