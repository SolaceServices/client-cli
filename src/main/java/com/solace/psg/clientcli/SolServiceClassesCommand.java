/**
 * Copyright 2022 Solace Systems, Inc. All rights reserved.
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

import com.solace.psg.sempv2.admin.model.ServiceClass;

import picocli.CommandLine.Command;


/**
 * Command class to handle service lists.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "classes",description = "Displays all service classes available.")
public class SolServiceClassesCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceClassesCommand.class);
		
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceClassesCommand()
	{
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Display service classes command.");
				
		try
		{
			System.out.println("Services classes available:");
			System.out.println(ServiceClass.DEVELOPER);
			System.out.println(ServiceClass.ENTERPRISE_KILO);
			System.out.println(ServiceClass.ENTERPRISE_MEGA);
			System.out.println(ServiceClass.ENTERPRISE_GIGA);
			System.out.println(ServiceClass.ENTERPRISE_TERA_50K);
			System.out.println(ServiceClass.ENTERPRISE_TERA_100K);			
		}
		catch (Exception e)
		{
			System.out.println("Error occurred while running service classes command: " + e.getMessage());
			logger.error("Error occurred while running service classes command: {}, {}", e.getMessage(), e.getCause());
		}
	}	
}
