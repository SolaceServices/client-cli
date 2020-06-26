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

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle various common tasks.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "hammer", description = "Handles various common tasks.", subcommands = {
		SolHammerCallCliCommand.class,
		SolHammerCliToSempCommand.class,
		SolHammerSperfCommand.class,
		SolHammerScurlCommand.class
})
public class SolHammerCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolHammerCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolHammerCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol hammer: ");
	    System.out.println(" callCli   - Executes a cli command on a service via Semp v1.");
	    System.out.println(" cliToSemp - Generates a cliToSemp command. The cli-to-semp tool path needs to be set in config.");
	    System.out.println(" scurl     - Creates service connection string for Curl.");
	    System.out.println(" sperf     - Creates a service connection string for SDKPerf.");

	    System.out.println(" Example command: sol hammer sperf");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running hammer command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{		
			System.out.println("Missing parameters for command. Try invokig command with -h for list of parameters.");
			
		}
		catch (Exception e)
		{
			System.out.println("Error occurred while nailing with the hammer: " + e.getMessage());
			logger.error("Error occurred while nailing with the hammer: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
