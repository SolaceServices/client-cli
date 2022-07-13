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

import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;


import com.solace.psg.clientcli.config.ConfigurationManager;

import com.solace.psg.sempv2.admin.model.ServiceDetails;

import com.solace.psg.sempv2.admin.model.Subscription;
import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.util.FileUtils;
import com.solace.psg.sempv2.ServiceManager;
import com.solace.psg.sempv2.VpnManager;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle bridge create.
 * 
 * 
 *
 */
@Command(name = "create", description = "Creates bridge.")
public class SolServiceBridgeCreateCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceBridgeCreateCommand.class);
	
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
    
    @Option(names = "-s", description = "Adds a subscription in format {name(mandatory) direction(opt.) type(opt.) default is SMF} -  <topicName> <IN>|<OUT> <D>|<G>|<DA> <SMF>|<MQTT>", arity = "0..*") 
    List<Subscription> subscriptions;
 
	@Option(names = {"-r", "-rollback"}, defaultValue = "true")
	private boolean rollback;

	@Option(names = {"-c", "-cert"}, defaultValue = "false", description = "Indicates if using certificate for authentication")
	private Boolean cert;	

	@Option(names = {"-lu", "-localUsername"}, description = "local username / certificate key file")
	private String localUsername;	

	@Option(names = {"-lp", "-localPassword"}, description = "local password")
	private String localPassword;	

	@Option(names = {"-ltcn", "-localTcn"}, description = "local Trusted Common Name")
	private String localTcn;	

	@Option(names = {"-ru", "-remoteUsername"}, description = "remote username / certificate key file")
	private String remoteUsername;	

	@Option(names = {"-rp", "-remotePassword"}, description = "remote password")
	private String remotePassword;	

	@Option(names = {"-rtcn", "-remoteTcn"}, description = "remote Trusted Common Name")
	private String remoteTcn;	

	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceBridgeCreateCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service bridge create \n");
	    System.out.println(" create - Creates a bridge for a service.");
	    System.out.println(" create - Creates a bridge for a service.");
	    
	    System.out.println(" Example command with default user: sol service bridge create -s=\"t/v1/1 IN D\" -s=\"t/v1/2 OUT G\"");
	    System.out.println(" Example command with provided user: sol service bridge create -lu=user -lp=pass -ru=user -rp=pass -s=\"t/v1/1 IN D\" -s=\"t/v1/2 OUT G\"");
	    System.out.println(" Example command with certificate: service bridge create -rn=<serviceName> -cert=true -lu=<local user key path> -lp=<local password> -ltcn=<local TCN> -ru=<remote user key path> -rp=<remote password> -rtcn=<remote TCN> -s=\"t/v1/1 IN D\" -s=\"t/v1/2 OUT G\"");
	}
	
	private boolean checkParams()
	{
		boolean result = false;
		
		if (cert)
		{
			if (localUsername == null || localUsername.isEmpty())
				System.out.println("Parameter localUsername is required.");				
			else if (remoteUsername == null || remoteUsername.isEmpty())
				System.out.println("Parameter remoteUsername is required.");
			else if (localTcn == null || localTcn.isEmpty())
				System.out.println("Parameter localTcn is required.");
			else if (remoteTcn == null || remoteTcn.isEmpty())
				System.out.println("Parameter remoteTcn is required.");
			else
				result = true;
		}
		else 
		{
			/*if (localUsername == null || localUsername.isEmpty())
				System.out.println("Parameter localUsername is required.");				
			else if (remoteUsername == null || remoteUsername.isEmpty())
				System.out.println("Parameter remoteUsername is required.");
			else*/
				result = true;
		}
		
		return result;
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
			if (!checkParams())
				return;
			
			System.out.println("Creating bridge...");	
			
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
			if (local != null && local.localServiceId != null)
			{
				sd = sm.getServiceDetails(local.localServiceId);
			}
			else if (local != null && local.localServiceName != null)
			{
				sd = sm.getServiceDetailsByName(local.localServiceName);
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
					
			ServiceDetails rsd = null;
			if (remote.remoteServiceId != null)
			{
				rsd = sm.getServiceDetails(remote.remoteServiceId);
			}
			else if (remote.remoteServiceName != null)
			{
				rsd = sm.getServiceDetailsByName(remote.remoteServiceName);
			}	
			
			if (sd != null && rsd != null)
			{
				VpnManager vf = new VpnManager(sd);

				boolean result = false;
				
				if (cert)
				{
					String localCertContent  = new String (FileUtils.readReponseFromFile(localUsername));
					String remoteCertContent  = new String (FileUtils.readReponseFromFile(remoteUsername));
					result = vf.createBridge(rsd, subscriptions, rollback, true, localCertContent, localPassword, remoteCertContent, remotePassword, localTcn, remoteTcn);
				}
				else
					result = vf.createBridge(rsd, subscriptions, rollback, false, localUsername, localPassword, remoteUsername, remotePassword, null, null);

				if (result)
					System.out.println("Bridge created successfully.");
				else
					System.out.println("Error creating the bridge.  Check logs for more details.");	
			}
			else
			{
				System.out.println("No service found for the provided details.");
			}
		}
		catch (ApiException e)
		{
			System.out.println("Error occurred while running command: " + e.getResponseBody());
			logger.error("Error occurred while running command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occrured while running  command: " + e.getMessage());
			logger.error("Error occurred while running  command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
