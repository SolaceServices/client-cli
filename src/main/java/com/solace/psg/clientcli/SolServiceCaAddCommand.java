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
import com.solace.psg.sempv2.admin.model.ServiceDetails;

import com.solace.psg.sempv2.apiclient.ApiException;


import com.solace.psg.sempv2.ServiceManager;
import com.solace.psg.util.FileUtils;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command class to handle certificate authority add.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "add", description = "Adds certificate authority.")
public class SolServiceCaAddCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceCaAddCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@ArgGroup(exclusive = true, multiplicity = "0..1")
    Exclusive exclusive;

    static class Exclusive {
        @Option(names = "-serviceName", required = true) String serviceName;
        @Option(names = "-serviceId", required = true) String serviceId;
    }

	@Parameters(index = "0", arity = "1", description="the certificate authority name")
	private String caName;

	@Parameters(index = "1", arity = "1", description="the certificate authority file path")
	private String filePath;
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceCaAddCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service ca add \n");
	    System.out.println(" add - Adds a certificate authority for a service.");

	    System.out.println(" Example command: sol service ca add <caName> <caFilePath");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running certificate authority create command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Creating certificate authority...");	
			
			String caContent = new String (FileUtils.readReponseFromFile(filePath));
			
			String token = ConfigurationManager.getInstance().getCloudAccountToken();
			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try login first.");	
				return;
			}
			
			ServiceManager sm = new ServiceManager(token);
			String ctxServiceId = ConfigurationManager.getInstance().getCurrentServiceId();
			String ctxServiceName = ConfigurationManager.getInstance().getCurrentServiceName();
			
			ServiceDetails sd = null;
			if (exclusive != null && exclusive.serviceId != null)
			{
				sd = sm.getServiceDetails(exclusive.serviceId);
			}
			else if (exclusive != null && exclusive.serviceName != null)
			{
				sd = sm.getServiceDetailsByName(exclusive.serviceName);
			}
			else if (ctxServiceId != null)
			{
				sd = sm.getServiceDetails(ctxServiceId);
			}
			else if (ctxServiceName != null)
			{
				sd = sm.getServiceDetailsByName(ctxServiceName);
			}
			else
			{
				System.out.println("Service ID or service name was not provided.");
				return;
			}
			
			if (sd != null)
			{
				boolean result = sm.addClientCertificateAuthority(sd.getServiceId(),caName, caContent);

				if (result)
					System.out.println("Certificate authority added successfully.");
				else
					System.out.println("Error creating the certificate authority.  Check logs for more details.");	
			}
			else
			{
				System.out.println("No service found for the provided details.");
			}
		}
		catch (ApiException e)
		{
			System.out.println("Error occured while running certificate authority command: " + e.getResponseBody());
			logger.error("Error occured while running certificate authority command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occured while running certificate authority command: " + e.getMessage());
			logger.error("Error occured while running certificate authority command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
