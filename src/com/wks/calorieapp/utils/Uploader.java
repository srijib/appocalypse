package com.wks.calorieapp.utils;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

//http://www.coderzheaven.com/2012/03/29/uploading-audio-video-or-image-files-from-android-to-server/
public class Uploader
{
	public static String uploadFile ( File file, String _url ) throws IOException
	{
		String httpMethod = "POST";
		String boundary = "*****";
		String twoHyphens = "--";
		String carriageReturn = "\r\n";

		HttpURLConnection connection = null;
		DataOutputStream requestStream = null;
		DataInputStream responseStream = null;

		FileInputStream fileIn = null;

		fileIn = new FileInputStream ( file );
		URL url = new URL ( _url );
		connection = ( HttpURLConnection ) url.openConnection ();

		// set connection parameters

		// request will be sent (input)
		// response expected (output)

		connection.setDoInput ( true );
		connection.setDoOutput ( true );

		connection.setRequestMethod ( httpMethod );
		connection.setRequestProperty ( "Connection", "Keep-Alive" );
		connection.setRequestProperty ( "Content-Type", "multipart/form-data" );
		connection.setRequestProperty ( "Content-Type",
				"multipart/form-data;boundary=" + boundary );

		// establish connection
		requestStream = new DataOutputStream ( connection.getOutputStream () );
		requestStream.writeBytes ( twoHyphens + boundary + carriageReturn );
		requestStream
				.writeBytes ( "Content-Disposition: form-data; name='uploadedfile';filename="
						+ file.getName () + carriageReturn );
		requestStream.writeBytes ( "\r\n" );

		// begin writing image
		int bytesAvailable = fileIn.available ();
		int maxBufferSize = 1 * 1024 * 1024; // !1MB
		int bufferSize = Math.min ( bytesAvailable, maxBufferSize );
		byte [] buffer = new byte [bufferSize];

		int bytesRead = fileIn.read ( buffer, 0, bufferSize );
		while ( bytesRead > 0 )
		{
			requestStream.write ( buffer );
			bytesAvailable = fileIn.available ();
			bufferSize = Math.min ( bytesAvailable, maxBufferSize );
			bytesRead = fileIn.read ( buffer, 0, bufferSize );
		}
		requestStream.writeBytes ( carriageReturn );
		requestStream.writeBytes ( twoHyphens + boundary + twoHyphens
				+ carriageReturn );
		// close streams

		fileIn.close ();
		requestStream.flush ();
		requestStream.close ();
		
		responseStream = new DataInputStream(connection.getInputStream ());
		String response = "";
		String line = "";
		
		while( (line = responseStream.readLine ()) != null)
		{
			response += line;
		}
		responseStream.close ();
		
		return response;

	}
}
