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
import java.nio.file.Path;
import java.text.SimpleDateFormat;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.solace.psg.clientcli.config.ConfigurationManager;

import com.solace.psg.sempv2.admin.model.ServiceDetails;
import com.solace.psg.sempv2.apiclient.ApiException;

import com.solace.psg.util.FileUtils;
import com.solace.tools.solconfig.Commander;
import com.solace.tools.solconfig.SempClient;
import com.solace.tools.solconfig.model.AttributeType;
import com.solace.tools.solconfig.model.ConfigBroker;
import com.solace.tools.solconfig.model.ConfigObject;
import com.solace.tools.solconfig.model.SempSpec;
import com.solace.psg.sempv2.ServiceManager;
import com.solace.psg.sempv2.VpnManager;

import picocli.CommandLine.ArgGroup;
import picocli.CommandLine.Command;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

/**
 * Command class to handle configuration backup.
 * 
 * @author VictorTsonkov
 *
 */
@Command(name = "backup", description = "Creates a service backup config.")
public class SolServiceConfigBackupCommand implements Runnable 
{
	private static final Logger logger = LogManager.getLogger(SolServiceConfigBackupCommand.class);

	@Parameters(index = "0", arity = "0..1", defaultValue=Option.NULL_VALUE, description="the file name")
	private String filename;

	@Option(names = {"-h", "-help"})
	private boolean help;
	
	@ArgGroup(exclusive = true, multiplicity = "0..1")
    ExcParam excl;

    static class ExcParam {
        @Option(names = {"-serviceName", "-sn"}, required = true) String serviceName;
        @Option(names = {"-serviceId", "-sid"}, required = true) String serviceId;
    }

	@Option(names = {"-d", "-default"} , defaultValue = "false",  description="Indicates whether to Keep attributes with a default value. Default is false")
	private boolean keepDefault;	

	@Option(names = {"-o", "-opaquePassword"} , defaultValue = Option.NULL_VALUE,  description="Sets a custom opaque password for the password encryption. Length should be at least 8 characters.")
	private String opaquePassword;	

    @Option(names = {"-k", "--insecure"}, description = "Allow insecure server connections when using SSL")
    private boolean insecure = false;

    @Option(names = "--cacert", description = "CA certificate file to verify peer when using SSL")
    private Path cacert;

	/**
	 * Initialises a new instance of the class.
	 */
	public SolServiceConfigBackupCommand()
	{
	}

	/**
	 * Shows help menu.
	 */
	private void showHelp()
	{
	    System.out.println(" sol service config backup <params> \n");
	    System.out.println(" backup - Creates a service backup config file <service_name.json>");

	    System.out.println(" Example command: sol service config backup");
	    System.out.println(" Example command: sol service config backup <filename>");
	}
	
	/**
	 * Runs the command.
	 */
	public void run()
	{
		logger.debug("Running config backup command.");
		
		if (help)
		{
			showHelp();
			return;
		}
		
		try
		{
			System.out.println("Creating backup config ...");	
			
			String token = ConfigurationManager.getInstance().getCloudAccountToken();
			if (token == null || token.isEmpty() )
			{
				System.out.println("Token is not set. Try login first.");	
				return;
			}
			
			ServiceManager sm = new ServiceManager(token);
			String ctxServiceId = ConfigurationManager.getInstance().getCurrentServiceId();
			String ctxServiceName = ConfigurationManager.getInstance().getCurrentServiceName();
			
			ServiceDetails sd = null;
			if (excl != null && excl.serviceId != null)
			{
				sd = sm.getServiceDetails(excl.serviceId);
			}
			else if (excl != null && excl.serviceName != null)
			{
				sd = sm.getServiceDetailsByName(excl.serviceName);
			}
			else if (ctxServiceId != null)
			{
				sd = sm.getServiceDetails(ctxServiceId);
			}
			else if (ctxServiceName != null)
			{
				sd = sm.getServiceDetailsByName(ctxServiceName);
			}
			else
			{
				System.out.println("Service ID or service name was not provided.");
				return;
			}
			
			if (sd != null)
			{
				VpnManager vf = new VpnManager(sd);
				if (opaquePassword == null)
					opaquePassword = vf.getOpaquePassword();

				String host = vf.getDefaultVpnContext().getSempV1Url();
				host = host.substring(0, host.indexOf("/SEMP"));
				
				String username = vf.getDefaultVpnContext().getSempUsername();
				String password = vf.getDefaultVpnContext().getSempPassword();
				
				SempClient sempClient = new SempClient(host, username, password, insecure, cacert);								
				Commander commander = Commander.ofSempClient(sempClient);
		        commander.setCurlOnly(false);
		        // This needs to be set after commander is initialised. 
				sempClient.setOpaquePassword(opaquePassword);
    	        
		        //commander.backup(ctxServiceName, null, exclusive);
		        // It can pass only one VPN name, as cloud service has only one VPN.
		        String[] vpns = new String[1];
		        vpns[0] = vf.getDefaultVpnContext().getVpnName();
		        
		        String resourceType = SempSpec.RES_ABBR.vpn.getFullName();
		        
		        ConfigBroker configBroker = commander.generateConfigFromBroker(resourceType, vpns);

		        configBroker.removeChildrenObjects(ConfigObject::isReservedObject, ConfigObject::isDeprecatedObject);
		        configBroker.removeAttributes(AttributeType.PARENT_IDENTIFIERS, AttributeType.DEPRECATED);
		        if (!keepDefault) 
		        {
		            configBroker.removeAttributesWithDefaultValue();
		            configBroker.getChildren();;
		            //configBroker.removeAttributes(null)
		        }
		        configBroker.checkAttributeCombinations(); // keep requires attribute for backup

		        if (filename == null)
		        {
		        	String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(System.currentTimeMillis());
		        	filename = vpns[0] + "_" + timeStamp+ ".json";
		        }
		        
		        printFile(configBroker, filename);
		        
		        System.out.println("Config backup created successfully.");
			}
			else
			{
				System.out.println("No service found for the provided details.");
			}
		}
		catch (ApiException e)
		{
			System.out.println("Error occured while running command: " + e.getResponseBody());
			logger.error("Error occured while running command: {}", e.getResponseBody());
		}
		catch (Exception e)
		{
			System.out.println("Error occured while running  command: " + e.getMessage());
			logger.error("Error occured while running  command: {}, {}", e.getMessage(), e.getCause());
		}
	}
	
	/**
	 * Print content.
	 * @param configBroker
	 * @param filename
	 * @throws IOException
	 */
	private void printFile(ConfigBroker configBroker, String filename) throws IOException
	{
		FileUtils.writeReponseToFile(filename, configBroker.toString().getBytes());
	}
	
	
}
