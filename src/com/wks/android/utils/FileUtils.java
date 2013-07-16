package com.wks.android.utils;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.util.Log;

public class FileUtils
{
	public static void writeToFile ( Context context, String filename, String text, int mode ) throws IOException
	{
		FileOutputStream fos = null;
		try
		{
			fos = context.openFileOutput ( filename, mode );
			fos.write ( text.getBytes ( "UTF-8" ) );

			Log.e ( "WRITTEN", text );
		}
		catch ( FileNotFoundException e )
		{
			throw e;
		}
		catch ( IOException e )
		{
			throw e;
		}
		finally
		{
			if ( fos != null )
			{
				try
				{
					fos.close ();
				}
				catch ( IOException e )
				{

				}
			}
		}
	}

	public static String readFromFile ( Context context, String fileName ) throws IOException
	{
		FileInputStream fis = null;
		InputStreamReader isr = null;
		BufferedReader reader = null;
		StringBuilder content = new StringBuilder ( "" );
		try
		{
			fis = context.openFileInput ( fileName );
			isr = new InputStreamReader ( fis );
			reader = new BufferedReader ( isr );
			String line;
			while ( ( line =reader.readLine () ) != null )
			{
				content.append ( line );
			}
		}
		catch ( FileNotFoundException e )
		{
			throw e;
		}
		catch ( IOException e )
		{
			throw e;
		}
		finally
		{
			if ( fis != null )
			{
				try
				{
					reader.close ();
					isr.close ();
					fis.close ();
				}
				catch ( IOException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace ();
				}
			}
		}
		Log.e ( "READ", content.toString () );
		return content.toString ();
	}
}
