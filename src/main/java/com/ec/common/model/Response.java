package com.ec.common.model;

public class Response<T> extends BaseEntity{

	private static final long serialVersionUID = 1L;

	public static enum Code {
		SUCCESS(0), FAIL(1),PER_IP_LIMIT(101),NOT_LOGIN(102),NOT_BROWSER(103) ;
		private int value;

		Code(int value) {
			this.value = value;
		}

		public int getValue() {
			return value;
		}
	}

	protected Integer errorCode;
	protected String message;
	protected T data;

	public Response(Integer errorCode) {
		super();
		this.errorCode = errorCode;
	}

	public Response(Integer errorCode, String message) {
		super();
		this.errorCode = errorCode;
		this.message = message;
	}

	public Response(Integer errorCode, String message, T data) {
		super();
		this.errorCode = errorCode;
		this.message = message;
		this.data = data;
	}

	public Integer getErrorCode() {
		return errorCode;
	}

	public void setErrorCode(Integer errorCode) {
		this.errorCode = errorCode;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public T getData() {
		return data;
	}

	public void setData(T data) {
		this.data = data;
	}

}
