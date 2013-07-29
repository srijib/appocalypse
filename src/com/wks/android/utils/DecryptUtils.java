package com.wks.android.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class DecryptUtils
{
	public static String AES ( byte [] cipherText, String encryptionKey ) throws CryptoException 
	{
		String decrypted = null;
		
		try
		{
			Cipher cipher = Cipher.getInstance ( "AES");
			SecretKeySpec key = new SecretKeySpec ( encryptionKey.getBytes ( "UTF-8" ), "AES" );
			cipher.init ( Cipher.DECRYPT_MODE, key);
			decrypted = new String ( cipher.doFinal ( cipherText ), "UTF-8" );
		}
		catch ( InvalidKeyException e )
		{
			throw new CryptoException(e);
		}
		catch ( NoSuchAlgorithmException e )
		{
			throw new CryptoException(e);
		}
		catch ( NoSuchPaddingException e )
		{
			throw new CryptoException(e);
		}
		catch ( UnsupportedEncodingException e )
		{
			throw new CryptoException(e);
		}
		catch ( IllegalBlockSizeException e )
		{
			throw new CryptoException(e);
		}
		catch ( BadPaddingException e )
		{
			throw new CryptoException(e);
		}
		
		return decrypted;
	}
}
