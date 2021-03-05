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

import com.solace.psg.clientcli.config.ConfigurationManager;
import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.ServiceManager;
import com.solace.psg.sempv2.admin.model.User;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle login.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "login",description = "Login to a solace cloud account.")
public class SolLoginCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolLoginCommand.class);

	@Option(names = {"-u", "-username"}, required = true)
	private String username;
	
	@Option(names = {"-p", "-password"})	
	private String password;

	@Option(names = {"-o", "-org"})	
	private String org;

	@Option(names = {"-t", "-token"})	
	private String token;
	
	@Option(names = {"-n"})
	private boolean show;
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolLoginCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol login [-u, -username=<username>] [-p, -password=<password>] [-o, -org=<organizationID>] [-n] \n");
	    System.out.println(" -username - the email or username to login to Solace Cloud Console Account");
	    System.out.println(" -password - the password to login to Solace Cloud Console Account");
	    System.out.println(" -org - the organization ID. By default, login uses the first if more than one in the user account");
	    System.out.println(" -token    - set already obtained token for the Solace Cloud Console Account");
	    System.out.println(" -n        - does not print in command line the token generated for the Solace Cloud Console Account");
	    System.out.println(" Example command: sol login -u=John.Smith@example.com -p=hotshot -o=myorg-dev -n");
	    System.out.println(" If the account is obtained via organization SSO a token needs to be obtained via SSO first. -n");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running login command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			ConfigurationManager config = ConfigurationManager.getInstance();
			ServiceManager sm = null;
			if (username != null && password !=null && !username.isEmpty() && !password.isEmpty())
			{
				// obtain default token if org not set, otherwise get token of specified org ID.
				sm = new ServiceManager(username, password, org);
				
				token = sm.getCurrentAccessToken();
							
				config.setCloudAccountUsername(username);
				config.setCloudAccountPassword(password);

				if (!show)
					System.out.println("The following login token was generated: \n " + token);
			}
			
			if (token != null && !token.isEmpty())
			{
				config.setCloudAccountToken(token);
				config.setCloudAccountOrgId(org);
				
				// Get current user ID. 
				if (sm == null)
					sm = new ServiceManager(token);
				
				User currentUser = sm.getUserByEmail(username);
				config.setCloudAccountUserId(currentUser.getUserId());
				
				// store the input data into the configuration file.
				config.store();
				
				System.out.println("Login to Solace Cloud Console successful.");
			}
		}
		catch (ApiException e)
		{
			System.out.println("Error occurred while running login command: " + e.getResponseBody());
			System.out.println("Please check if the credentials are correct, the organization is valid or if your organization is using SSO.");
			logger.error("Error occurred while running login command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occurred while running login command: " + e.getMessage());
			logger.error("Error occurred while running login command: {}, {}", e.getMessage(), e.getCause());
		}
	}
}
