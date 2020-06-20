/**
 * Copyright 2016-2020 Solace Corporation. All rights reserved.
 *
 * http://www.solace.com
 *
 * This source is distributed under the terms and conditions of any contract or
 * contracts between Solace Corporation ("Solace") and you or your company. If
 * there are no contracts in place use of this source is not authorized. No
 * support is provided and no distribution, sharing with others or re-use of 
 * this source is authorized unless specifically stated in the contracts 
 * referred to above.
 *
 * This software is custom built to specifications provided by you, and is 
 * provided under a paid service engagement or statement of work signed between
 * you and Solace. This product is provided as is and is not supported by 
 * Solace unless such support is provided for under an agreement signed between
 * you and Solace.
 */
package com.solace.psg.util;

import javax.crypto.spec.SecretKeySpec;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.crypto.spec.IvParameterSpec;
import javax.crypto.Cipher;

/**
 * Class to encapsulate AES encryption.
 *
 */
public class AES
{
	private static final Logger logger = LogManager.getLogger(AES.class);

	static String IV = "AAAAAAAAAAAAAAAA";
	static String plaintext = "test text 123\0\0\0"; /* Note null padding */
	static String encryptionKey = "s1o6l7p5s3g2k3e0";

	/**
	 * Encrypts a string.
	 * @param strIn String in
	 * @return Encrypted string
	 * @throws EncryptionException
	 */
	public static String encrypt(String strIn) throws EncryptionException
	{
		String hex = null;
		byte[] cipher = encrypt(strIn, encryptionKey);
		hex = convertToHex(cipher);
		return hex;
	}

	/**
	 * Decrypts a string.
	 * @param strIn encrypted string
	 * @return decrypted string
	 * @throws EncryptionException
	 */
	public static String decrypt(String strIn) throws EncryptionException
	{
		String plain = null;
		byte[] cipher = fromHex(strIn);
		plain = decrypt(cipher, encryptionKey);
		plain = plain.trim();
		return plain;
	}

	/**
	 * Decodes from Hex.
	 * @param buf hex buffer
	 * @return decoded bytes.
	 * @throws EncryptionException
	 */
	private static byte[] fromHex(String buf) throws EncryptionException
	{
		logger.debug("Converting from Hex ... ");

		byte[] bytes = new byte[buf.length() / 2];
		try
		{
			for (int i = 0; i < buf.length(); i += 2)
			{
				bytes[i / 2] = Integer.decode("0x" + buf.substring(i, i + 2)).byteValue();
			}
		}
		catch (Exception e)
		{
			throw new EncryptionException(buf + " is likely not an encrypted value.");
		}
		return bytes;
	}

	/**
	 * Converts a byte array to hex.
	 * @param data byte array
	 * @return converted data
	 */
	private static String convertToHex(byte[] data)
	{
		logger.debug("Converting to Hex ... ");

		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < data.length; i++)
		{
			int halfbyte = (data[i] >>> 4) & 0x0F;
			int two_halfs = 0;
			do
			{
				if ((0 <= halfbyte) && (halfbyte <= 9))
					buf.append((char) ('0' + halfbyte));
				else
					buf.append((char) ('a' + (halfbyte - 10)));
				halfbyte = data[i] & 0x0F;
			}
			while (two_halfs++ < 1);
		}
		return buf.toString();
	}

	/**
	 * Encrypts a String.
	 * @param plainText the string
	 * @param encryptionKey the key
	 * @return encrypted sting
	 * @throws EncryptionException
	 */
	public static byte[] encrypt(String plainText, String encryptionKey) throws EncryptionException
	{
		logger.debug("Encrypting string ... ");
		try
		{
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE"); // "AES/CBC/NoPadding"
			SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
			cipher.init(Cipher.ENCRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
			return cipher.doFinal(plainText.getBytes("UTF-8"));
		}
		catch (Exception e)
		{
			logger.error("Encrypting String failed with exception: {}, {}", e.getMessage(), e.getCause());

			throw new EncryptionException(e);
		}
	}

	/**
	 * Decrypts a String.
	 * @param cipherText encrypted string
	 * @param encryptionKey the key
	 * @return decrypted string
	 * @throws EncryptionException
	 */
	public static String decrypt(byte[] cipherText, String encryptionKey) throws EncryptionException
	{
		try
		{
			Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding", "SunJCE");
			SecretKeySpec key = new SecretKeySpec(encryptionKey.getBytes("UTF-8"), "AES");
			cipher.init(Cipher.DECRYPT_MODE, key, new IvParameterSpec(IV.getBytes("UTF-8")));
			return new String(cipher.doFinal(cipherText), "UTF-8");
		}
		catch (Exception e)
		{
			logger.error("Decrypting String failed with exception: {}, {}", e.getMessage(), e.getCause());

			throw new EncryptionException(e);
		}
	}
}
