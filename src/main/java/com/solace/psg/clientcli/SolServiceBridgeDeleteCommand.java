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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.solace.psg.clientcli.config.ConfigurationManager;

import com.solace.psg.sempv2.admin.model.ServiceDetails;

import com.solace.psg.sempv2.admin.model.Subscription;
import com.solace.psg.sempv2.apiclient.ApiException;

import com.solace.psg.sempv2.interfaces.ServiceFacade;
import com.solace.psg.sempv2.interfaces.VpnFacade;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle bridge delete.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "delete", description = "Deletes bridge.")
public class SolServiceBridgeDeleteCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceBridgeDeleteCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@ArgGroup(exclusive = true, multiplicity = "0..1")
    ExcParamLocal local;

	@ArgGroup(exclusive = true, multiplicity = "1")
    ExcParamRemote remote;

    static class ExcParamLocal {
        @Option(names = {"-localServiceName", "-ln"}, required = true) String localServiceName;
        @Option(names = {"-localServiceId", "-lid"}, required = true) String localServiceId;
    }

    static class ExcParamRemote {
        @Option(names = {"-remoteServiceName", "-rn"}, required = true) String remoteServiceName;
        @Option(names = {"-remoteServiceId", "-rid"}, required = true) String remoteServiceId;
    }

    static class ParamSubscription {
        @Option(names = {"-remoteServiceName", "-rn"}, required = true) String remoteServiceName;
        @Option(names = {"-remoteServiceId", "-rid"}, required = true) String remoteServiceId;
    }
    
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceBridgeDeleteCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service bridge delete \n");
	    System.out.println(" create - Deletes a bridge for a service.");
	    System.out.println(" create - Deletes a bridge for a service.");
	    
	    System.out.println(" Example command: sol service bridge delete -rn=testService2");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running bridge create command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Deleting bridge...");	
			
			String token = ConfigurationManager.getInstance().getCloudAccountToken();
			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try login first.");	
				return;
			}
			
			ServiceFacade sf = new ServiceFacade(token);
			String ctxServiceId = ConfigurationManager.getInstance().getCurrentServiceId();
			String ctxServiceName = ConfigurationManager.getInstance().getCurrentServiceName();
			
			ServiceDetails sd = null;
			if (local != null && local.localServiceId != null)
			{
				sd = sf.getServiceDetails(local.localServiceId);
			}
			else if (local != null && local.localServiceName != null)
			{
				sd = sf.getServiceDetailsByName(local.localServiceName);
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
					
			ServiceDetails rsd = null;
			if (remote.remoteServiceId != null)
			{
				rsd = sf.getServiceDetails(remote.remoteServiceId);
			}
			else if (remote.remoteServiceName != null)
			{
				rsd = sf.getServiceDetailsByName(remote.remoteServiceName);
			}	
			
			if (sd != null && rsd != null)
			{
				VpnFacade vf = new VpnFacade(sd);

				boolean result = vf.deleteBridge(rsd);

				if (result)
					System.out.println("Bridge deleted successfully.");
				else
					System.out.println("Error deleting the bridge.  Check logs for more details.");	
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
