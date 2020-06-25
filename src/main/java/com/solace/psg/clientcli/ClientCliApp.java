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


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.solace.psg.clientcli.utils.SubscriptionConverter;
import com.solace.psg.sempv2.admin.model.Subscription;

import picocli.CommandLine;


/**
 * ClientCli main app. 
 *
 */
public class ClientCliApp 
{
	private static final Logger logger = LogManager.getLogger(ClientCliApp.class);

	public static void main( String[] args )
    {
        try
		{
        	CommandLine cmd = new CommandLine(new SolCommand());//.setColorScheme(myColorScheme()); 
        	cmd.registerConverter(Subscription.class, new SubscriptionConverter());
        	cmd.execute(args);
		}
		catch (Exception e)
		{
			logger.error("Client CLI App exception occured: {}, {}", e.getMessage(), e.getCause()); 
		}
    }
}
