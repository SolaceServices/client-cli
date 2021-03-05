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

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.solace.psg.clientcli.config.ConfigurationManager;
import com.solace.psg.sempv2.admin.model.ServiceDetails;
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
@Command(name = "details",description = "Lists service details.")
public class SolServiceDetailsCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceDetailsCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@ArgGroup(exclusive = true, multiplicity = "0..1")
    Exclusive exclusive;

    static class Exclusive {
        @Option(names = {"-serviceName", "-sn"}, required = false, description="the service name") String serviceName;
        @Option(names = {"-serviceId", "-sid"}, required = false) String serviceId;
    }
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceDetailsCommand()
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
		logger.debug("Running service details command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Listing service details:");
			String token = ConfigurationManager.getInstance().getCloudAccountToken();
			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try to login first.");	
				return;
			}
			
			ServiceManager sm = new ServiceManager(token);
			ServiceDetails sd = null;
			String ctxServiceId = ConfigurationManager.getInstance().getCurrentServiceId();
			String ctxServiceName = ConfigurationManager.getInstance().getCurrentServiceName();
			
			if (exclusive != null && exclusive.serviceId != null)
			{
				sd = sm.getServiceDetails(exclusive.serviceId);			
			}
			else if (exclusive != null && exclusive.serviceName != null)
			{
				sd = sm.getServiceDetailsByName(exclusive.serviceName);
			}
			else if (ctxServiceId != null && !ctxServiceId.isEmpty())
			{
				sd = sm.getServiceDetails(ctxServiceId);
			}
			else if (ctxServiceName != null && !ctxServiceName.isEmpty())
			{
				sd = sm.getServiceDetailsByName(ctxServiceName);
			}
			else 
			{
				System.out.println("No service ID or Name provided.");
				return;
			}

			if (sd != null)
			{
				printServiceDetails(sd);
			}
			else 
			{
				System.out.println("No service details available for the provided service ID or Name.");
			}
		}
		catch (ApiException e)
		{
			System.out.println("Error occurred while running service details command: " + e.getResponseBody());
			logger.error("Error occurred while running service details command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occurred while running service details command: " + e.getMessage());
			logger.error("Error occurred while running service details command: {}, {}", e.getMessage(), e.getCause());
		}
	}
	
	private void printServiceDetails(ServiceDetails sd) throws JsonProcessingException
	{
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		String yaml = mapper.writeValueAsString(sd);
		System.out.println(yaml);
	}
}
