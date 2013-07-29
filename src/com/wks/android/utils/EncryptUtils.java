package com.wks.android.utils;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

public class EncryptUtils
{

	/**Encrypts given text with encryptionKey. Key must be 128-bits (16 characters long).
	 * 
	 * @param text text to be encrypted
	 * @param encryptionKey 128-bit key
	 * @return encrypted data
	 * @throws CryptoException 
	 */
	public static byte[] AES ( String text, String encryptionKey ) throws CryptoException 
	{
		byte[] encrypted = null;
		try
		{
			Cipher cipher = Cipher.getInstance("AES");
			SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes ("UTF-8"), "AES");
			cipher.init(Cipher.ENCRYPT_MODE, key);
			encrypted = cipher.doFinal(text.getBytes("UTF-8"));
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
		return encrypted;
	}
}
