package com.melee.meleejava.web.exceptions;

public class SignatureError extends MeleeHTTPException {

	private static final long serialVersionUID = 1L;

	public SignatureError(String description) {
		super(400, description);
	}

}
