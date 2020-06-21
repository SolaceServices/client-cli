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

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.solace.psg.sempv2.admin.model.ServiceDetails;
import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.interfaces.ServiceFacade;

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
	
	@Option(names = {"-serviceName"})
	private String serviceName;	

	@Option(names = {"-serviceId"})
	private String serviceId;	
	
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
			
			ServiceFacade sf = new ServiceFacade(token);
			ServiceDetails sd = null;
			
			if (serviceId == null)
			{
				serviceId = ConfigurationManager.getInstance().getCurrentServiceId();;				
			}
			if (serviceId == null)
			{
				if (serviceName == null)
				{
					serviceName = ConfigurationManager.getInstance().getCurrentServiceName();
				}
				
				if (serviceName == null || serviceName.isEmpty())
				{
					System.out.println("No service provided for details. Provide serviceId or serviceName parameter or set default service context.");
					return;
				}
				else
				{
					sd = sf.getServiceDetailsByName(serviceName);
				}
			}
			else
			{
				sd = sf.getServiceDetails(serviceId);
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
