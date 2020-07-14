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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.solace.psg.clientcli.config.ConfigurationManager;

import com.solace.psg.sempv2.admin.model.ServiceDetails;

import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.config.model.CertAuthority;
import com.solace.psg.sempv2.ServiceManager;
import com.solace.psg.sempv2.VpnManager;
import com.solace.psg.tablereporter.Block;
import com.solace.psg.tablereporter.Board;
import com.solace.psg.tablereporter.Table;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle certificate authority lists.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "list", description = "Lists service certificate authority details.")
public class SolServiceCaListCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceCaListCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@ArgGroup(exclusive = true, multiplicity = "0..1")
    Exclusive exclusive;

    static class Exclusive {
        @Option(names = "-serviceName", required = true) String serviceName;
        @Option(names = "-serviceId", required = true) String serviceId;
    }
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceCaListCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service ca list \n");
	    System.out.println(" list - lists all certificate authorities.");

	    System.out.println(" Example command: sol service ca list");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running certificate authority list command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Listing certificate authoritiess:");	
			
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
				List<String> cas = sd.getCertificateAuthorities();

				printResults(cas, "");		
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
	
	private void printResults(List<String> cas, String message) throws IOException
	{
		System.out.println(message);
		logger.debug("Printing certificate authority list.");
		for (String ca : cas)
		{
			if (ca != null)
				System.out.println(ca);
			else
				System.out.println("<internal unnamed>");
		}		
	}

}
