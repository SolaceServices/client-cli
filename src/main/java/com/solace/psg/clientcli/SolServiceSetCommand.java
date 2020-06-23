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
import com.solace.psg.sempv2.admin.model.Service;
import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.interfaces.ServiceFacade;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle service set.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "set",description = "Sets a service as the default context.")
public class SolServiceSetCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceSetCommand.class);

	@Option(names = {"-serviceName"}, description = "the service name.")
	private String serviceName;	

	@Option(names = {"-serviceId"}, description = "the service ID.")
	private String serviceId;	

	@Option(names = {"-none"}, fallbackValue = "true", description = "removes the set values")
	private Boolean none;	
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceSetCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service set [-serviceName=<name>] [-serviceId=<id>] \n");
	    System.out.println(" -serviceName - the name of the service.");
	    System.out.println(" -serviceId - the id of the service.");
	    System.out.println(" -none - removes the set values.");
	    System.out.println(" Example command: sol service set -serviceName=testService");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running service set command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		if (none != null)
		{
			ConfigurationManager.getInstance().removeCurrentServiceId();
			ConfigurationManager.getInstance().removeCurrentServiceName();
			
			ConfigurationManager.getInstance().store();
			System.out.println("Default service unset");
			
			return;
		}
		
		try
		{
			String token = ConfigurationManager.getInstance().getCloudAccountToken();
			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try to login first.");	
				return;
			}
						
			Service service = null;
			ServiceFacade sf = new ServiceFacade(token);
			if (serviceId != null)
			{
				service = sf.getServiceById(serviceId);
			}
			else if (serviceName != null)
			{
				service = sf.getServiceByName(serviceName);
			}
			else
			{
				System.out.println("Provide a serviceName or serviceId.");	
				return;
			}
			
			if (service != null)
			{
				ConfigurationManager.getInstance().setCurrentServiceId(service.getServiceId());
				ConfigurationManager.getInstance().setCurrentServiceName(service.getName());
				
				ConfigurationManager.getInstance().store();
				System.out.println("Successfully set as default service: " + service.getName());
			}
			else
			{
				System.out.println("No service found. Provide a valid serviceName or serviceId.");	
			}
		}
		catch (ApiException e)
		{
			System.out.println("Error occurred while running login command: " + e.getResponseBody());
			logger.error("Error occurred while running login command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occurred while running login command: " + e.getMessage());
			logger.error("Error occurred while running login command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
