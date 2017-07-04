package com.Router;

public class RouterParamException extends RouterException{
	private static final long serialVersionUID = -6208150007091787761L;

	public RouterParamException() {
		super();
	}

	public RouterParamException(String message, Throwable cause, boolean arg2, boolean arg3) {
		super(message, cause, arg2, arg3);
	}

	public RouterParamException(String message, Throwable cause) {
		super(message, cause);
	}

	public RouterParamException(String message) {
		super(message);
	}

	public RouterParamException(Throwable cause) {
		super(cause);
	}
}
