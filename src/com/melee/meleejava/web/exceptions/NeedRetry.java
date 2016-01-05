package com.melee.meleejava.web.exceptions;

public class NeedRetry extends MeleeHTTPException {

	private static final long serialVersionUID = 1L;

	public NeedRetry(String description) {
		super(200, description);
	}

}
