/**
 * Copyright 2021 Solace Systems, Inc. All rights reserved.
 *
 * http://www.solace.com
 *
 * This source is distributed under the terms and conditions
 * of any contract or contracts between Solace Systems, Inc.
 * ("Solace") and you or your company.
 * If there are no contracts in place use of this source
 * is not authorized.
 * No support is provided and no distribution, sharing with
 * others or re-use of this source is authorized unless
 * specifically stated in the contracts referred to above.
 *
 * This product is provided as is and is not supported
 * by Solace unless such support is provided for under 
 * an agreement signed between you and Solace.
 * 
 */
package com.solace.psg.util;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.solace.psg.clientcli.ClientCliApp;



/**
 * Simple file utilities class.
 * 
 * @author VictorTsonkov
 *
 */
public class FileUtils
{
	private static final Logger logger = LogManager.getLogger(FileUtils.class);
	
	private FileUtils() {}
	
	/**
	 * Writes a byte response to a file.
	 * @param message
	 * @throws IOException
	 */
	public static void writeReponseToFile(String filename, byte[] message) throws IOException
	{
		FileOutputStream stream = null;
		logger.debug("Writing response from file: {}", filename);

	    try
		{
	    	stream = new FileOutputStream(filename);
			stream.write(message);
		} catch (IOException e)
		{
			logger.error("Unable to write into a file: ", e );
		}
	    finally
	    {
	    	if (stream != null)
	    		stream.close();
	    }
	}

	/**
	 * Reads a response from a file name.
	 * @return
	 * @throws IOException
	 */
	public static byte[] readReponseFromFile(String filename) throws IOException
	{
		logger.debug("Reading response from file: {}", filename);
		byte[] result = null; 
		FileInputStream stream = null;
	    try
		{
	    	stream = new FileInputStream(filename);
	    	result = new byte[stream.available()];
			stream.read(result);
		} catch (IOException e)
		{
			logger.error("Unable to read from a file: ", e );
		}
	    finally
	    {
	    	if (stream != null)
	    		stream.close();
	    }
	    
	    return result;
	}
}
