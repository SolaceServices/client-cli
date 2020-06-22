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

import com.solace.psg.clientcli.sempv1.SolServicePurgeCommand;
import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.interfaces.ServiceFacade;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle service operations.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "service", description = "Handles service operations.", subcommands = {
		SolServiceDeleteCommand.class,
		SolServiceDetailsCommand.class,
	    SolServiceListCommand.class,
	    SolServicePurgeCommand.class,
	    SolServiceSetCommand.class	      
})
public class SolServiceCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service: ");
	    System.out.println(" list - lists all services for a Solace Cloud Console Account");
	    System.out.println(" details - lists all service details for a service");
	    System.out.println(" purge - Purges a queue");
	    System.out.println(" set - sets a service as the default service context by service name or service ID");

	    System.out.println(" Example command: sol service list");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running service command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
				
			System.out.println("Missing parameters for command. Try invokig command with -h for list of parameters.");
			
		}
		/*catch (ApiException e)
		{
			System.out.println("Error occurred while running login command: " + e.getResponseBody());
			logger.error("Error occurred while running login command: {}", e.getResponseBody());
		}*/
		catch (Exception e)
		{
			System.out.println("Error occurred while running login command: " + e.getMessage());
			logger.error("Error occurred while running login command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
