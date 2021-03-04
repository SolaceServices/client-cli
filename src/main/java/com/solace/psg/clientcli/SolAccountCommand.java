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
package com.solace.psg.clientcli;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle Organisation accounts operations.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "account", aliases = "a", description = "Handles account operations.", subcommands = {
	      SolAccountListCommand.class,
	      SolAccountSwitchCommand.class
})
public class SolAccountCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolAccountCommand.class);
	
	@Option(names = {"h", "help"})
	private boolean help;
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolAccountCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol account: \n");
	    System.out.println(" list - lists all organization accounts for username");
	    System.out.println(" switch - switches to a different organization");
	    System.out.println(" Example command: sol account list");
	    System.out.println(" Example command: sol account switch myorg-dev");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running list command.");
		
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
			System.out.println("Error occurred while running list command: " + e.getMessage());
			logger.error("Error occurred while running list command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
