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

import picocli.CommandLine.Command;

/**
 * Command class to display version.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "version", aliases = "-v", description = "Displays the library version.")
public class SolVersionCommand implements Runnable 
{

	/**
	 * Initialises a new instance of the class.
	 */
	public SolVersionCommand()
	{
	}

	public void run()
	{
		System.out.println("Solace Client CLI version: " + ClientCliGlobals.VERSION);
	}
}
