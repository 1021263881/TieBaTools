package com.fapple.Tools;

public class mException extends Exception
{
	private String more = "";
	public mException()
	{
		super();
	}
	public mException(String message, Throwable cause)
	{
		super(message, cause);
	}
	public mException(String message)
	{
		super(message);
	}
	public mException(Throwable cause)
	{
		super(cause);
	}
	public mException(String message, String more)
	{
		super(message);
		this.more = more;
	}
	public String getMore()
	{
		return more;
	}
}
