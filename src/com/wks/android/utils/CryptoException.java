package com.wks.android.utils;

public class CryptoException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = -7716384891744694519L;

	public CryptoException (String message)
	{
		super(message);
	}
	
	public CryptoException (Throwable t)
	{
		super(t);
	}
}
