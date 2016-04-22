package com.perk.perksdk.adblocker;

import java.io.IOException;

public class RootAccessDeniedException extends IOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7734221590320826966L;

	public RootAccessDeniedException() {
		super();
	}

	public RootAccessDeniedException(String detailMessage) {
		super(detailMessage);
	}

}
