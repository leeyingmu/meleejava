package com.melee.meleejava.web.exceptions;

public class ServerError extends MeleeHTTPException {

	private static final long serialVersionUID = 1L;

	public ServerError(String description) {
		super(500, description);
	}

}
