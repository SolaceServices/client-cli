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

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;



import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.maven.shared.utils.StringUtils;

import com.solace.psg.clientcli.config.ConfigurationManager;
import com.solace.psg.sempv2.admin.model.DataCenter;
import com.solace.psg.sempv2.admin.model.Organization;
import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.ServiceManager;
import com.solace.psg.tablereporter.Block;
import com.solace.psg.tablereporter.Board;
import com.solace.psg.tablereporter.Table;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command class to handle organisation account lists.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "switch", aliases = "s", description = "Changes and organization account by provided organization ID.")
public class SolAccountSwitchCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolAccountSwitchCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@Parameters(index = "0", arity = "1", description="the organization ID")
	private String orgId;
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolAccountSwitchCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol account switch <orgId> \n");
	    System.out.println(" Example command: sol account switch myorg-dev");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running account list command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Switching organization account...");	
			
			ConfigurationManager config = ConfigurationManager.getInstance();

			
			String token = config.getCloudAccountToken();
			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try login first.");	
				return;
			}
			
			ServiceManager sm = new ServiceManager(token);	
			String newOrgToken = sm.getOrgToken(orgId);
			
			config.setCloudAccountToken(newOrgToken);
			config.setCloudAccountOrgId(orgId);
			
			// store the input data into the configuration file.
			config.store();
			
			System.out.println("Switching organization account successful.");
			
		}
		catch (ApiException e)
		{
			System.out.println("Error occurred while running command: " + e.getResponseBody());
			System.out.println("Please check if organization ID is valid by typing sol account list organization ID has different authentication mechanism.");
			logger.error("Error occurred while running command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occurred while running command: " + e.getMessage());
			logger.error("Error occurred while running command: {}, {}", e.getMessage(), e.getCause());
		}
	}	
}
