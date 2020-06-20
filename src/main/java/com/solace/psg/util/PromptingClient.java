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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * This utility class has some convenience methods for prompting an operator on
 * std in via out
 */
public class PromptingClient
{
	private static final Logger logger = LogManager.getLogger(PromptingClient.class);

	BufferedReader m_thisBuffReader = null;

	public PromptingClient()
	{
		m_thisBuffReader = new BufferedReader(new InputStreamReader(System.in));
	}

	/**
	 * As the user if they are sure and get the answer as a boolean
	 */
	public boolean areYouSure() throws IOException
	{
		logger.debug("Calling are you sure ...");
		String strLine = "";
		String strPrompt = "\nAre you absolutely sure you want to do this? (type 'yes' or 'no')";
		while (!strLine.equalsIgnoreCase("YES") && !strLine.equalsIgnoreCase("NO"))
		{
			strLine = prompt(strPrompt);
		}
		return strLine.equalsIgnoreCase("YES");
	}

	/*
	 * internal state check
	 */
	private void validateState() throws AssertionError
	{
		if (m_thisBuffReader == null)
			throw new AssertionError("Internal error: the input reader is null");
	}

	/**
	 * Make a prompt where only one answer is acceptable (like a captcha)
	 */
	public void promptForFixedAnswer(String strPrompt, String strAnswer) throws IOException
	{
		logger.debug("Prompting for fixed answer ...");
		validateState();
		String strAcceptableAnswer = strAnswer.trim().toLowerCase();
		boolean bMatch = false;
		while (!bMatch)
		{
			String strRc = prompt(strPrompt);
			strRc = strRc.trim();
			if (strRc.equalsIgnoreCase(strAcceptableAnswer))
				bMatch = true;
		}
	}

	/**
	 * Prompt a string and return a string
	 */
	public String prompt(String strPrompt) throws IOException
	{
		logger.debug("Prompting ...");
		validateState();
		String strLine = "";
		while (strLine.equals(""))
		{
			System.out.println(strPrompt);
			strLine = m_thisBuffReader.readLine();
		}
		return strLine;
	}

	/**
	 * close any open readers
	 */
	public void close() throws IOException
	{
		logger.debug("Closing readers...");

		if (m_thisBuffReader != null)
		{
			m_thisBuffReader.close();
			m_thisBuffReader = null;
		}
	}
}