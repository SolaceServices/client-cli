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

import com.solace.psg.sempv2.admin.model.Service;
import com.solace.psg.sempv2.apiclient.ApiException;
import com.solace.psg.sempv2.interfaces.ServiceFacade;
import com.solace.psg.tablereporter.Block;
import com.solace.psg.tablereporter.Board;
import com.solace.psg.tablereporter.Table;

import picocli.CommandLine.Command;
import picocli.CommandLine.Option;

/**
 * Command class to handle service lists.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "list",description = "Lists service details.")
public class SolServiceListCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceListCommand.class);
	
	@Option(names = {"-h", "-help"})
	private boolean help;
	
	
	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceListCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service list \n");
	    System.out.println(" list - lists all services for Solace Cloud Console Account");

	    System.out.println(" Example command: sol service list");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running service list command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Listing services:");	
			
			String token = ConfigurationManager.getInstance().getCloudAccountToken();
			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try to login first.");	
				return;
			}
			
			ServiceFacade sf = new ServiceFacade(token);	
			List<Service> services = sf.getAllServices();
			
			printResults(services, "");		
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
	
	private void printResults(List<Service> services, String message) throws IOException
	{
		System.out.println(message);
		logger.debug("Printing service list");
		
		List<String> headersList = Arrays.asList("Service Name", "Service ID", "Data Center", "Service class", "User ID", "Admin status", "Admin state");

		List<List<String>> rowsList = new ArrayList<List<String>>(services.size());

		for (Service service : services)
		{
			rowsList.add(Arrays.asList(service.getName(), service.getServiceId(), service.getDatacenterId(), service.getServiceClassId(), service.getUserId(), service.getAdminProgress(), service.getAdminState()));
		}
		
		List<Integer> colAlignList = Arrays.asList(Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_MIDDLE_LEFT, Block.DATA_CENTER, Block.DATA_CENTER);
		List<Integer> colWidthsListEdited = Arrays.asList(40, 14, 20, 16, 14, 12, 11);
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
