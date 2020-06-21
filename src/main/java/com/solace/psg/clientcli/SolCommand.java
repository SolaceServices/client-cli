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

import picocli.CommandLine.Command;

/**
 * Sol Command base class.
 * @author VictorTsonkov
 *
 */
@Command(subcommands = {
			      SolLoginCommand.class,
			      SolServiceCommand.class,
			      SolUserCommand.class,
			      SolRolesCommand.class,
			      SolVersionCommand.class
})
public class SolCommand implements Runnable 
{

	/**
	 * Initialises a new instance of the class.
	 */
	public SolCommand()
	{
	}
	
	@Command(name = "help")
	public void helpCommand() {
	    System.out.println("Usage sol: sol command [parameters] \n");  
	    System.out.println("To login to Solace Cloud Console Account:");
	    System.out.println(" sol login [-u, -username=<username>] [-p, -password=<password>] [-n] \n");
	    System.out.println("To display the current Client CLI version:");
	    System.out.println(" sol version \n");
	    System.out.println(" Type sol <command> -h or -help for detailed help on the command usage. \n");
	}

	public void run()
	{
		System.out.println("For detailed usage run with 'help' parameter.");
	}

}
