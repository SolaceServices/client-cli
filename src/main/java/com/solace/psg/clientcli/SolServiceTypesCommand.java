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

import com.solace.psg.sempv2.admin.model.ServiceType;

import picocli.CommandLine.Command;


/**
 * Command class to handle service lists.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "types",description = "Displays all service types available.")
public class SolServiceTypesCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceTypesCommand.class);
		
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceTypesCommand()
	{
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Display service types command.");
				
		try
		{
			System.out.println("Services types available:");
			System.out.println(ServiceType.DEVELOPER);
			System.out.println(ServiceType.ENTERPRISE);		
		}
		catch (Exception e)
		{
			System.out.println("Error occurred while running service types command: " + e.getMessage());
			logger.error("Error occurred while running service types command: {}, {}", e.getMessage(), e.getCause());
		}
	}	
}
