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

import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.config.model.MsgVpnClientUsername;
import com.solace.psg.sempv2.config.model.MsgVpnQueue;
import com.solace.psg.sempv2.config.model.MsgVpnQueue.AccessTypeEnum;
import com.solace.psg.sempv2.config.model.MsgVpnQueue.PermissionEnum;
import com.solace.psg.sempv2.ServiceManager;
import com.solace.psg.sempv2.VpnManager;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command class to handle user name create.
 * 
 * 
 *
 */
@Command(name = "create", description = "Creates username.")
public class SolServiceUsernameCreateCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceUsernameCreateCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@ArgGroup(exclusive = true, multiplicity = "0..1")
    ExcParam excl;

    static class ExcParam {
        @Option(names = {"-serviceName", "-sn"}, required = true) String serviceName;
        @Option(names = {"-serviceId", "-sid"}, required = true) String serviceId;
    }

	@Parameters(index = "0", arity = "1", description="the username")
	private String username;

	@Option(names = {"-sm", "-subscriptionmanager"} , defaultValue = "false",  description="Indicates the username is subscription manager. Default is false.")
	private boolean subman;	

	@Option(names = {"-gp", "-guaranteedpermission"} , defaultValue = "false",  description="Sets Guaranteed Endpoint Permission Override. Default is false.")
	private boolean gp;	

	@Option(names = {"-p", "-password"} , defaultValue = "",  description="Sets the password. Default is blank.")
	private String password;	

	@Option(names = {"-cp", "-clientprofile"} , defaultValue = "default",  description="Sets the client profile name. Default is 'default'.")
	private String clientprofile;	

	@Option(names = {"-acl", "-aclprofile"} , defaultValue = "default",  description="Sets the acl name. Default is 'default'.")
	private String acl;	

	@Option(names = {"-e", "-enabled"} , defaultValue = "true",  description="Indicates whether the username is enabled. Default is true.")
	private boolean enabled;	

	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceUsernameCreateCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service username create \n");
	    System.out.println(" create - Creates a username for a service.");

	    System.out.println(" Example command: sol service username create <username> -p=<password>");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running queue create command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Creating username...");	
			
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
			if (excl != null && excl.serviceId != null)
			{
				sd = sm.getServiceDetails(excl.serviceId);
			}
			else if (excl != null && excl.serviceName != null)
			{
				sd = sm.getServiceDetailsByName(excl.serviceName);
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
				VpnManager vf = new VpnManager(sd);
				MsgVpnClientUsername request = new MsgVpnClientUsername();
				request.setClientUsername(username);
				request.setAclProfileName(acl);
				request.setClientProfileName(clientprofile);				
				request.setPassword(password);
				request.setEnabled(enabled);
				request.setSubscriptionManagerEnabled(subman);
				request.setGuaranteedEndpointPermissionOverrideEnabled(gp);
								
				boolean result = vf.addClientUsername(request);

				if (result)
					System.out.println("Username created successfully.");
				else
					System.out.println("Error creating the username.  Check logs for more details.");	
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
