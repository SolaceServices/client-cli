package com.solace.psg.clientcli;


import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.solace.psg.sempv2.interfaces.*;


/**
 * ClientCli main app. 
 *
 */
public class ClientCliApp 
{
	private static final Logger logger = LogManager.getLogger(ClientCliApp.class);

	public static void main( String[] args )
    {
		if (args.length < 2)
		{
			logger.error("Arguments must contain username and password passed: app.jar <uysername> <password>");
			System.exit(-1);
		}

        try
		{
			ServiceFacade sf = new ServiceFacade(args[0], args[1]);
		}
		catch (Exception e)
		{
			logger.error("ClietApp exception occured: {}", e.getMessage()); 
		}
    }
}
