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

import com.solace.psg.clientcli.config.ConfigurationManager;
import com.solace.psg.sempv2.apiclient.ApiException;


import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle logout.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "logout", description = "Log out from a solace cloud account.")
public class SolLogoutCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolLogoutCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@Option(names = {"-c", "-clean"})
	private boolean clean;

	/**
	 * Initialises a new instance of the class.
	 */
	public SolLogoutCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" -c , -clean removes also currecnly set service Id and other shortcut account options. ");
		System.out.println(" sol logout [-c]");
	    System.out.println(" Example command: sol logout \n");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running logout command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			ConfigurationManager config = ConfigurationManager.getInstance();
			
			config.setCloudAccountUsername("");
			config.setCloudAccountPassword("");
			config.setCloudAccountToken("");
			config.setCloudAccountOrgId("");
			config.setCloudAccountUserId("");
			
			if (clean)
			{
				config.setCurrentServiceId("");
				config.setCurrentServiceName("");
			}
			
			// store the input data into the configuration file.
			config.store();
				
			System.out.println("Successfully logged out.");
		}
		catch (ApiException e)
		{
			System.out.println("Error occurred while running logout command: " + e.getResponseBody());
			logger.error("Error occurred while running logout command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occurred while running logout command: " + e.getMessage());
			logger.error("Error occurred while running logout command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
