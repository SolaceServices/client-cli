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

/**
 * Command class to handle organisation account lists.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "list",description = "Lists all organization accounts for the current user.")
public class SolAccountListCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolAccountListCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolAccountListCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol account list \n");
	    System.out.println(" list - lists all organization accounts for the current username");

	    System.out.println(" Example command: sol account list");
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
			System.out.println("Listing organization accounts:");	
			
			String token = ConfigurationManager.getInstance().getCloudAccountToken();
			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try login first.");	
				return;
			}
			
			ServiceManager sm = new ServiceManager(token);	
			List<Organization> accs = sm.getAllOrgAccounts();
			
			printResults(accs, "");		
		}
		catch (ApiException e)
		{
			System.out.println("Error occurred while running list command: " + e.getResponseBody());
			logger.error("Error occurred while running list command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occurred while running list command: " + e.getMessage());
			logger.error("Error occurred while running list command: {}, {}", e.getMessage(), e.getCause());
		}
	}
	
	private void printResults(List<Organization> orgs, String message) throws IOException
	{
		System.out.println(message);
		logger.debug("Printing accounts list");
		
		List<String> headersList = Arrays.asList("Organization ID", "Name", "Organization Type", "Account Type", "Created on", "Deleted on");

		List<List<String>> rowsList = new ArrayList<List<String>>(orgs.size());
		
		DateFormat df = new SimpleDateFormat("dd/MM/yyyy");

		for (Organization org : orgs)
		{
			String cds = "----";
			String dds = "----";
			if (org.getCreatedTimestamp() != null)
			{
				Date cd = new Date(org.getCreatedTimestamp());
				cds = df.format(cd);
			}
			if (org.getDeletedTimestamp() != null)
			{
				Date dd = new Date(org.getDeletedTimestamp());
				dds = df.format(dd);
			}
			
			rowsList.add(Arrays.asList(StringUtils.abbreviate(org.getOrganizationId(), 28), StringUtils.abbreviate(org.getName(), 28), StringUtils.abbreviate(org.getOrganizationType(), 15), StringUtils.abbreviate(org.getType(), 15), cds, dds ));
		}
		
		List<Integer> colAlignList = Arrays.asList(Block.DATA_MIDDLE_LEFT,Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_CENTER, Block.DATA_CENTER);
		List<Integer> colWidthsListEdited = Arrays.asList(29, 29, 16, 16, 12, 12);
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
