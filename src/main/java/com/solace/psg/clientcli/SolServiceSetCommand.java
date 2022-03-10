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

import com.solace.psg.clientcli.config.ConfigurationManager;
import com.solace.psg.sempv2.admin.model.Service;
import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.ServiceManager;

import picocli.CommandLine.ArgGroup;
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

	@ArgGroup(exclusive = true, multiplicity = "0..1")
    Exclusive exclusive;

    static class Exclusive {
        @Option(names = {"-serviceName", "-sn"}, required = false, description="the service name") String serviceName;
        @Option(names = {"-serviceId", "-sid"}, required = false, description="the service Id") String serviceId;
    }	

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
			ServiceManager sm = new ServiceManager(token);
			if (exclusive != null && exclusive.serviceId != null)
			{
				service = sm.getServiceById(exclusive.serviceId);
			}
			else if (exclusive != null && exclusive.serviceName != null)
			{
				service = sm.getServiceByName(exclusive.serviceName);
			}
			else
			{
				System.out.println("No service name or serviceId provided. Currently set serviceId: " + ConfigurationManager.getInstance().getCurrentServiceId() + " and serviceName: "+ ConfigurationManager.getInstance().getCurrentServiceName());	
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
