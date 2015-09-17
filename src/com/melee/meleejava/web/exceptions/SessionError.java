package com.melee.meleejava.web.exceptions;

public class SessionError extends MeleeHTTPException {
	
	private static final long serialVersionUID = 1L;

	public SessionError(String description) {
		super(400, description);
	}
}
