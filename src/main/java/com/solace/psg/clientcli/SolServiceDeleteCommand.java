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

import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.solace.psg.clientcli.config.ConfigurationManager;
import com.solace.psg.sempv2.admin.model.Service;
import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.ServiceManager;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle service lists.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "delete",description = "Deletes a service.")
public class SolServiceDeleteCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceDeleteCommand.class);
	
	@ArgGroup(exclusive = true, multiplicity = "1")
    Exclusive exclusive;

    static class Exclusive {
        @Option(names = {"-serviceName", "-sn"}, required = true) String serviceName;
        @Option(names = {"-serviceId", "-sid"}, required = true) String serviceId;
    }
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceDeleteCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service details [-serviceName=<name>] [-serviceId=<id>] \n");
	    System.out.println(" -serviceName - the name of the service.");
	    System.out.println(" -serviceId - the id of the service.");
	    System.out.println(" Example command: sol service details -serviceName=testService");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running service delete command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Deleting service ...");
			String token = ConfigurationManager.getInstance().getCloudAccountToken();
			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try to login first.");	
				return;
			}
			
			ServiceManager sm = new ServiceManager(token);
			//Service service = null;
			boolean result = false;
			
			if (exclusive.serviceId != null && !exclusive.serviceId.isEmpty()) // try to delete by provided ID
			{
				if (promptForConfirm())
					result = sm.deleteService(exclusive.serviceId);		
				else
				{
					System.out.println("Service command aborted.");
					return;
				}
			}
			else if (exclusive.serviceName != null && !exclusive.serviceName.isEmpty()) // try to delete by provided name
			{				
				Service service = sm.getServiceByName(exclusive.serviceName);
				if (service != null)
					if (promptForConfirm())
						result = sm.deleteService(service.getServiceId());	
					else
					{
						System.out.println("Service command aborted.");
						return;
					}
				else
				{
					System.out.println("Service failed to delete. Check if provided name is correct.");
					return;
				}
			}
			
			if (result)
				System.out.println("Service successfully deleted.");
			else 
				System.out.println("Service failed to delete. Switch to debug and check logs for more info.");
		}
		catch (ApiException e)
		{
			System.out.println("Error occurred while running service delete command: " + e.getResponseBody());
			logger.error("Error occurred while running service delete command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occurred while running service delete command: " + e.getMessage());
			logger.error("Error occurred while running service delete command: {}, {}", e.getMessage(), e.getCause());
		}
	}
	
	private boolean promptForConfirm()
	{
		boolean result = false;
		
		if (ConfigurationManager.getInstance().getPromptToConfirm())
		{
			System.out.println("Do you want to proceed? (y/yes to confirm):");
			@SuppressWarnings("resource")
			Scanner scanner = new Scanner(System.in);

			String val = scanner.next();
			if(val.equalsIgnoreCase("y")||val.equalsIgnoreCase("yes")) 
			    result = true;
		}
		else // no need to confirm
			result = true; 
			
		return result;
	}
}
