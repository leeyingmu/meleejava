package com.melee.meleejava.web.exceptions;

public class BadRequest extends MeleeHTTPException {
	
	private static final long serialVersionUID = 1L;

	public BadRequest(String description) {
		super(400, description);
	}
	
}
