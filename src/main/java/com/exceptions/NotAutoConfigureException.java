package com.exceptions;
/**
 * 
 * @date 2019年1月30日 上午9:50:57
 * @author chenxianjun
 * @since version
 */
public class NotAutoConfigureException extends RuntimeException{
	private static final long serialVersionUID = 1L;
	public NotAutoConfigureException(String msg) {
		super(msg);
	}
}
