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
package com.solace.psg.clientcli;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.solace.psg.clientcli.config.ConfigurationManager;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle login.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "config", description = "Config settings.")
public class SolConfigCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolConfigCommand.class);

	@Option(names = {"-d", "-decrypted"} )
	private Boolean decrypt;
	
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolConfigCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol config [-d, -decrypted] \n");

	    System.out.println(" Example command: sol config -d");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running config command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			ConfigurationManager config = ConfigurationManager.getInstance();
			
			config.setEncryptDetails(decrypt);

			// store the input data into the configuration file.
			config.store();
			
			System.out.println("Config values saved.");
		}
		catch (Exception e)
		{
			System.out.println("Error occurred while running config command: " + e.getMessage());
			logger.error("Error occurred while running config command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
