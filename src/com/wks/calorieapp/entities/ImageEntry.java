package com.wks.calorieapp.entities;

public class ImageEntry
{
	private long id;
	private String fileName;
	
	public ImageEntry()
	{
		this(0,"null");
	}
	
	public ImageEntry(long id, String fileName)
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
	
	@Override
	public String toString ()
	{
		return String.format ( "[id: %d,fileName: %s]", this.id,this.fileName );
	}
}
