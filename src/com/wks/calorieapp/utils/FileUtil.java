package com.wks.calorieapp.utils;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.Context;

public class FileUtil
{
	public static void writeToFile ( Context context, String filename, String text, int mode ) throws IOException
	{
		FileOutputStream fos = null;
		try
		{
			fos = context.openFileOutput ( filename, mode );
			fos.write ( text.getBytes () );
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

		StringBuilder content = new StringBuilder ( "" );
		try
		{
			byte [] buffer = new byte [1024];
			fis = context.openFileInput ( fileName );

			while ( fis.read ( buffer ) != -1 )
			{
				content.append ( new String ( buffer ) );
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
					fis.close ();
				}
				catch ( IOException e )
				{
					// TODO Auto-generated catch block
					e.printStackTrace ();
				}
			}
		}

		return content.toString ();
	}
}
