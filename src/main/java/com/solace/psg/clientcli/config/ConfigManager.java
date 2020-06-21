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
package com.solace.psg.clientcli.config;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * Class to handle configuration of the CLI. 
 * @author VictorTsonkov
 *
 */
public class ConfigManager
{

	private String configPath;
	
	private Properties props;
	
	/**
	 * Initialises a new instanc of the class.
	 * @throws IOException 
	 */
	public ConfigManager(String configPath) throws IOException
	{
		this.configPath = configPath;
		props = new Properties();
		FileInputStream in = new FileInputStream(configPath);
		props.load(in);
		in.close();
	}
	
	/**
	 * Saves properties.
	 * @throws IOException 
	 */
	public void saveProperties(String comment) throws IOException
	{
		FileOutputStream out = new FileOutputStream(configPath);
		props.store(out, comment);
		out.close();	
	}
	
	/**
	 * Gets the whole collection of properties.
	 * @return
	 */
	public Properties getProperties()
	{
		return props;
	}
}
