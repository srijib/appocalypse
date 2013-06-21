package com.wks.calorieapp.daos;

public class ImageDataTransferObject
{
	private long id;
	private String fileName;
	
	public ImageDataTransferObject()
	{
		this(0,"null");
	}
	
	public ImageDataTransferObject(long id, String fileName)
	{
		setId(id);
		setFileName(fileName);
	}
	
	public long getId ()
	{
		return id;
	}
	
	public void setId ( long id )
	{
		if(id < 0) throw new IllegalStateException("id must be a positive integer.");
		this.id = id;
	}
	
	public String getFileName ()
	{
		return fileName;
	}
	
	public void setFileName ( String fileName )
	{
		if( fileName == null || fileName.isEmpty ()) throw new IllegalStateException("file name must not be empty");
		this.fileName = fileName;
	}
}
