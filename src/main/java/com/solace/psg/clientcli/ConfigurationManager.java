/**
 * 
 */
package com.solace.psg.clientcli;

/**
 * Copyright 2020 Solace Systems, Inc. All rights reserved.
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.solace.psg.util.AES;

/**
 * Class to handle application's configuration.
 * @author VictorTsonkov
 *
 */
public class ConfigurationManager
{
	private static final Logger logger = LogManager.getLogger(ConfigurationManager.class);

	public static final String DEFAULT_NAME = "profile.conf"; 
	
	private String filename;
	
	private Properties props = new Properties();
	
	private boolean encryptDetails = true;

	private static ConfigurationManager instance = new ConfigurationManager();
	
	/**
	 * Encryption character indicating a string value is in encrypted format.
	 */
	private static final String ENC = "@"; 
	
	/**
	 * Returns singleton configuration.
	 * @return
	 */
	public static ConfigurationManager getInstance()
	{
		return instance;
	}
	
	/**
	 * Initialises a new instance of the class.
	 */
	private ConfigurationManager() 
	{
		this(DEFAULT_NAME);
	}

	/**
	 * Initialises a new instance of the class.
	 */
	private ConfigurationManager(String filename) 
	{
		this.filename = filename;
		
		load();
	}
	
	/**
	 * Gets the filename;
	 * @return the filename.
	 */
	public String getFilename()
	{
		return filename;
	}
	
	/**
	 * Loads the config file properties.
	 */
	private void load() 
	{
		InputStream input = null;
		try
		{
			File propsFile = new File(filename);
			
			if (propsFile.exists())
			{
				input = new FileInputStream(propsFile);
				
				props.load(input);		
				
				encryptDetails = getEncryptDetails();
			}
		}
		catch(Exception ex)
		{
			logger.error("Error while trying to parse config file: {} ", ex.getMessage());
		}
		finally
		{
			if (input != null)
				try
				{
					input.close();
				}
				catch (IOException e)
				{
					logger.error("Error while trying to close config file: {} ", e.getMessage());
				}
		}
	}
	
	/**
	 * Stored all the config values.
	 * @throws Exception
	 */
	public void store()
	{
		OutputStream output = null;
		try 
		{
			output = new FileOutputStream(filename);
			props.store(output, null);
		}
		catch (IOException ex)
		{
			logger.error("Error while trying to parse config file: {} ", ex.getMessage());
		}
		finally
		{
			if (output != null)
				try
				{
					output.close();
				}
				catch (IOException e)
				{
					logger.error("Error while trying to close config file: {} ", e.getMessage());
				}
		}
    }			
	
	/**
	 * Decrypts a password.
	 * @param password
	 * @return decrypted password
	 * @throws Exception
	 */
	private String decryptPassword(String password) throws Exception
	{
		logger.debug("Decrypting password...");
		String result = null;
		
        try 
        {
            result = AES.decrypt(password);
        } 
        catch (Exception e) 
        {
        	logger.error("Exception occured: {}, {}", e.getMessage(), e.getCause());
            throw new Exception("The password from the configuration properties does not appear to be encrypted or in the correct format.");
        }
		
		return result;
	}

	/**
	 * Encrypts a password.
	 * @param password
	 * @return encrypted password
	 * @throws Exception
	 */
	private String encryptPassword(String password) throws Exception
	{
		logger.debug("Encrypting password...");
		String result = null;
		
        try 
        {
            result = AES.encrypt(password);
        } 
        catch (Exception e) 
        {
        	logger.error("Exception occured: {}, {}", e.getMessage(), e.getCause());
            throw new Exception("Error while encrypting password", e);
        }
		
		return result;
	}

	/**
	 * Gets the cloud account password.
	 * @return password
	 * @throws Exception
	 */
	public String getCloudAccountPassword() throws Exception
	{
		String password = props.getProperty("cloudPassword");
		
		if (password != null && password.startsWith(ENC))
			password = decryptPassword(password.substring(ENC.length()-1));
		
		return password;
	}

	/**
	 * Gets the cloud account password.
	 * @throws Exception
	 */
	public void setCloudAccountPassword(String password) throws Exception
	{
		String encPass = null;
		if (encryptDetails)
			encPass = ENC + encryptPassword(password);
		else
			encPass = password;
				
		props.setProperty("cloudPassword", encPass);
	}

	/**
	 * Gets the cloud account token.
	 * @return token
	 * @throws Exception
	 */
	public String getCloudAccountToken() throws Exception
	{
		String token = props.getProperty("cloudToken");
		
		if (token != null && token.startsWith(ENC))
			token = decryptPassword(token.substring(ENC.length()-1));
		
		return token;
	}

	/**
	 * Gets the cloud account token.
	 * @throws Exception
	 */
	public void setCloudAccountToken(String token) throws Exception
	{
		String encPass = null;
		if (encryptDetails)
			encPass = ENC + encryptPassword(token);
		else
			encPass = token;
				
		props.setProperty("cloudToken", encPass);
	}

	/**
	 * Gets cloud account user name.
	 * @return user name
	 */
	public String getCloudAccountUsername()
	{
		return props.getProperty("cloudUsername");
	}

	/**
	 * Sets cloud account user name.
	 * @param username user name 
	 */
	public void setCloudAccountUsername(String username)
	{
		props.setProperty("cloudUsername", username);
	}
	
	/**
	 * Sets current VPN/service name.
	 * @param name name
	 */
	public void setCurrentServiceName(String name)
	{
		props.setProperty("currentServiceName", name);
	}

	/**
	 * Gets current VPN/service name.
	 * @return name
	 */
	public String getCurrentServiceName()
	{
		return props.getProperty("currentServiceName");
	}

	/**
	 * Sets current VPN name.
	 * @param id id
	 */
	public void setCurrentServiceId(String id)
	{
		props.setProperty("currentServiceId", id);
	}

	/**
	 * Gets current service id.
	 * @return id
	 */
	public String getCurrentServiceId()
	{
		return props.getProperty("currentServiceId");
	}	
	
	/**
	 * Get encrypt details.
	 * @return
	 */
	public Boolean getEncryptDetails()
	{
		return Boolean.valueOf(props.getProperty("encryptDetails"));
	}

	/**
	 * Mock Stream dataFusion service.
	 * @return
	 */
	public void setEncryptDetails(boolean value)
	{
		props.setProperty("encryptDetails", Boolean.toString(value));
	}
}
