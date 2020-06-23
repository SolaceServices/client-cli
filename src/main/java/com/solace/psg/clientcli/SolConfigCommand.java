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

	@Option(names = {"-e", "-encrypted"}, fallbackValue = "true", description = "Sets whether the credentials should be encrypted." )
	private Boolean encrypt;
	
	@Option(names = {"-p", "-prompt"}, fallbackValue = "true", description = "Sets Prompt to confirm flag to true or false."  )
	private Boolean prompt;
	
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
	    System.out.println(" sol config [-e, -encrypted=true|false] \n");
	    System.out.println(" sol config [-p, -prompt=true|false] \n");

	    System.out.println(" Example command: sol config -e -p");
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
			
			if (encrypt != null)
				config.setEncryptDetails(encrypt);

			if (prompt != null)
				config.setPromptToConfirm(prompt);

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
