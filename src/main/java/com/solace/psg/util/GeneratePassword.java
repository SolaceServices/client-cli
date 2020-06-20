/**
 * Copyright 2016-2020 Solace Corporation. All rights reserved.
 *
 * http://www.solace.com
 *
 * This source is distributed under the terms and conditions of any contract or
 * contracts between Solace Corporation ("Solace") and you or your company. If
 * there are no contracts in place use of this source is not authorized. No
 * support is provided and no distribution, sharing with others or re-use of 
 * this source is authorized unless specifically stated in the contracts 
 * referred to above.
 *
 * This software is custom built to specifications provided by you, and is 
 * provided under a paid service engagement or statement of work signed between
 * you and Solace. This product is provided as is and is not supported by 
 * Solace unless such support is provided for under an agreement signed between
 * you and Solace.
 */
package com.solace.psg.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Password generation utility to run in command line.
 *
 */
public class GeneratePassword
{
	private static final String appName = System.getProperty("com.solace.psg.util.pwd-utility.appname",
			"solace-pwd-utility");
	
	private static final Logger logger = LogManager.getLogger(GeneratePassword.class);

	/**
	 * Main entry point.
	 * @param args arguments
	 * @throws Exception exception.
	 */
	public static void main(String[] args) throws Exception
	{
		System.out.println("Solace PSG Password Generation Utility v" + PwdUtilVersion.current().getVersion());
		System.out.println("-------------------------------------------------------------------");
		System.out.println("This program encrypts passwords for storage in application property files.");
		System.out.println("");

		String textToEncrypt = null;

		if (args.length > 1)
		{
			System.out.printf("Usage: %s [<password to encrypt>]%n", appName);
			System.exit(0);
		}
		else if (args.length == 1)
		{
			textToEncrypt = args[0];
		}
		else
		{
			PromptingClient client = new PromptingClient();
			textToEncrypt = client.prompt("Enter the password you would like to encrypt: ");
			System.out.println("");
		}

		// Encrypt password
		logger.info("Calling encrypt method to encrypt text.");

		String encryptedPw = AES.encrypt(textToEncrypt);

		System.out.println("The encrypted password is: '" + encryptedPw + "' (without the quotes).");
		System.out.println("You may use this value in the password field in configuration property files.");
	}
}
