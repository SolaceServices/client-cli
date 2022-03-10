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

import com.solace.psg.sempv2.admin.model.ServiceDetails;
import com.solace.psg.sempv2.admin.model.ServiceManagementContext;
import com.solace.psg.sempv2.apiclient.ApiException;

import com.solace.psg.sempv2.ServiceManager;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;


/**
 * Command class to handle SEMP v1 curl tasks.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "scurl", description = "Returns an SEMP v1 curl connection string for a service.")
public class SolHammerScurlCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolHammerScurlCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@Option(names = {"-i", "-insecure"}, defaultValue = "false" ,description="Generates insecure connection parameters.")
	private boolean insecure;	
	
	@ArgGroup(exclusive = true, multiplicity = "0..1")
    ExcParam excl;

    static class ExcParam {
        @Option(names = {"-serviceName", "-sn"}, required = true) String serviceName;
        @Option(names = {"-serviceId", "-sid"}, required = true) String serviceId;
    }

	/**
	 * Initialises a new instance of the class.
	 */
	public SolHammerScurlCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol hammer scurl \n");

	    System.out.println(" Example command: sol hammer scurl <serviceId> | <serviceName>");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running scurl command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Generating SEMP v1 curl connection details...");	
			
			String token = ConfigurationManager.getInstance().getCloudAccountToken();
			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try login first.");	
				return;
			}
			
			ServiceManager sf = new ServiceManager(token);
			String ctxServiceId = ConfigurationManager.getInstance().getCurrentServiceId();
			String ctxServiceName = ConfigurationManager.getInstance().getCurrentServiceName();
						
			ServiceDetails sd = null;
			if (excl != null && excl.serviceId != null)
			{
				sd = sf.getServiceDetails(excl.serviceId);
			}
			else if (excl != null && excl.serviceName != null)
			{
				sd = sf.getServiceDetailsByName(excl.serviceName);
			}
			else if (ctxServiceId != null)
			{
				sd = sf.getServiceDetails(ctxServiceId);
			}
			else if (ctxServiceName != null)
			{
				sd = sf.getServiceDetailsByName(ctxServiceName);
			}
			else
			{
				System.out.println("Service ID or service name was not provided.");
				return;
			}
			
			if (sd != null) 
			{
				ServiceManagementContext ctx = new ServiceManagementContext(sd);
				
				StringBuffer sb = new StringBuffer();
				sb.append("curl -d @sampleRequestXmlFile.xml ");
				sb.append("-u");sb.append(" ");sb.append(ctx.getSempUsername());sb.append(":");sb.append(ctx.getSempPassword());sb.append(" ");
				sb.append(ctx.getSempV1Url());sb.append(" ");
				
				if (insecure)
					sb.append("-k -insecure");
				System.out.println(sb.toString());
			}
			else
			{
				System.out.println("No service found for the provided details.");
			}
		}
		catch (ApiException e)
		{
			System.out.println("Error occured while running command: " + e.getResponseBody());
			logger.error("Error occured while running command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occured while running  command: " + e.getMessage());
			logger.error("Error occured while running  command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
