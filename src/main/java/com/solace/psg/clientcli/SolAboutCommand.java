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

import picocli.CommandLine.Command;

/**
 * Command class to display about.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "about",description = "About this library.")
public class SolAboutCommand implements Runnable 
{

	/**
	 * Initialises a new instance of the class.
	 */
	public SolAboutCommand()
	{
	}

	public void run()
	{
		System.out.println("   _____       __                   _________            __     ________    ____");
		System.out.println("  / ___/____  / /___ _________     / ____/ (_)__  ____  / /_   / ____/ /   /  _/");
		System.out.println("  \\__ \\/ __ \\/ / __ `/ ___/ _ \\   / /   / / / _ \\/ __ \\/ __/  / /   / /    / /  ");
		System.out.println(" ___/ / /_/ / / /_/ / /__/  __/  / /___/ / /  __/ / / / /_   / /___/ /____/ /   ");
		System.out.println("/____/\\____/_/\\__,_/\\___/\\___/   \\____/_/_/\\___/_/ /_/\\__/   \\____/_____/___/   ");
		System.out.println("                                                                                ");
		System.out.println("		                  /                 ");
		System.out.println("		                 (((                ");
		System.out.println("		        ((((      (    (  (((       ");
		System.out.println("		             (((((((((((            ");
		System.out.println("		        (  (((((((((((((((          ");
		System.out.println("		          (((((((((((((((((         ");
		System.out.println("		    O-((  (((((((((((((((((  ((-(   ");
		System.out.println("		        ( (((((((((((((((((         ");
		System.out.println("		            (((((((((((((           ");
		System.out.println("		        (((    (((((((   .((        ");
		System.out.println("		         (  ` . ( (  (   ((         ");
		System.out.println("		                 (((                ");
		System.out.println("		                  /                 ");
	}
}
