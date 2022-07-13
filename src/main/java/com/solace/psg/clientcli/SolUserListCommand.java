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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.solace.psg.clientcli.config.ConfigurationManager;
import com.solace.psg.sempv2.admin.model.User;
import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.ServiceManager;
import com.solace.psg.tablereporter.Block;
import com.solace.psg.tablereporter.Board;
import com.solace.psg.tablereporter.Table;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle user lists.
 * 
 * 
 *
 */
@Command(name = "list",description = "Lists user details.")
public class SolUserListCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolUserListCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolUserListCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol user list \n");
	    System.out.println(" list - lists all services for Solace Cloud Console Account");

	    System.out.println(" Example command: sol service list");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running user list command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Listing users:");	
			
			String token = ConfigurationManager.getInstance().getCloudAccountToken();
			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try login first.");	
				return;
			}
			
			ServiceManager sm = new ServiceManager(token);	
			List<User> users = sm.getAllUsers();
			
			printResults(users, "");		
		}
		catch (ApiException e)
		{
			System.out.println("Error occured while running list command: " + e.getResponseBody());
			logger.error("Error occured while running list command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occured while running list command: " + e.getMessage());
			logger.error("Error occured while running list command: {}, {}", e.getMessage(), e.getCause());
		}
	}
	
	private void printResults(List<User> users, String message) throws IOException
	{
		System.out.println(message);
		logger.debug("Printing user list");
		
		List<String> headersList = Arrays.asList("User ID", "First Name", "Last Name", "Email", "Company", "Organization ID", "User state");

		List<List<String>> rowsList = new ArrayList<List<String>>(users.size());

		for (User user : users)
		{
			rowsList.add(Arrays.asList(user.getUserId(), StringUtils.abbreviate(user.getFirstName(), 14), StringUtils.abbreviate(user.getLastName(), 14), StringUtils.abbreviate(user.getEmail(), 38), StringUtils.abbreviate(Objects.toString(user.getCompany(), ""),  23), Objects.toString(user.getOrganizationId(), ""), user.getState()));
		}
		
		List<Integer> colAlignList = Arrays.asList(Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_CENTER, Block.DATA_CENTER);
		List<Integer> colWidthsListEdited = Arrays.asList(14, 15, 15, 40, 25, 25, 12);
		int width = Board.getRecommendedWidth(colWidthsListEdited, true);
				
		Board board = new Board(width);	
		Table table = new Table(board, width, headersList, rowsList);		
		table.setColWidthsList(colWidthsListEdited);
		table.setColAlignsList(colAlignList);
		
		Block block = table.tableToBlocks();
		//block.setBelowBlock(summaryBlock);
		board.setInitialBlock(block);
		
		String tableString = board.build().getPreview();
		System.out.println(tableString);
	}

}
