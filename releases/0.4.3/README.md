# Solace Client CLI 

![Logo](CoronaCLI.png)

## Description
Solace Client CLI is an application for handling Solace Cloud operations via the command line.
The Client CLI can handle multiple cloud accounts by creating different profiles for each account and can also handle multiple organisations under a specified account. 

## Requirements
The application requires Java JRE 11+ since version 0.4.0. Prior version is compatible with Java 1.8+

## Releases
The folder /releases/<version> contains a ready compiled release for everyone who doesn't want to build the tool but just to use it directly. It contains the executable file and the required libraries.
The files required for Windows are the sol.exe and the /libs folder.
The files required for Linux are the ClientCli-<version>, solinstall.sh (used to create alias for the sol command) and the /libs folder.

## Internal libraries part of the application release
The application is using libraries (sempv1-interface and sempv2-interface) for executing SEMP V1 and SEMP v2 commands from the client's command line. 

## Compiling the code
The source is depending on maven repos for *semp1-interface*, *sempv2-interface* and *solconfig*. You can import these repos and install them into the local maven repository in order to build the code. The compiled jars can be imported with the following command:
`mvn install:install-file -Dfile="<path-to-local-code>\client-cli\lib\solconfig.jar" -DgroupId=com.solace.tools -DartifactId=solconfig -Dversion=<version> -Dpackaging=jar`
`mvn install:install-file -Dfile="<path-to-local-code>\client-cli\lib\sempv1-interface.jar" -DgroupId=com.solace.psg -DartifactId=sempv1-interface -Dversion=<version> -Dpackaging=jar`
`mvn install:install-file -Dfile="<path-to-local-code>\client-cli\lib\sempv2-interface.jar" -DgroupId=com.solace.psg -DartifactId=sempv2-interface -Dversion=<version> -Dpackaging=jar`

Use the following command to compile (with *-DskipTests* as optional parameter):
mvn clean compile package

## Generating command autocpletion script
Picocli library supports autocmpletion (refer to https://picocli.info/autocomplete.html). To create a script run:
`java -cp "lib/picocli-<version>.jar;target/ClientCli-<version>.jar" picocli.AutoComplete -n sol com.solace.psg.clientcli.SolCommand`
The above command will generate a command script called *sol_completion*.
For Linux autocompletion run:
`source sol_completion`

## Building sol executable
Once the code is compiled in a jar file, there is a tool, which can be used to generate a sol.exe - Launch4j (http://launch4j.sourceforge.net/). **Launch4j** requires the executable jar and main class as parameters, as well as icon and output path tto generate an axecutable - *sol.exe*. 
The project icon and configuration is located in the launch4j project folder.
To be able to run the sol.exe you need to also place on its path the *lib* folder with all additional jars from the target build folder.  

## Windows installation:
1. Copy the executable sol.exe and the 'lib' subfolder in a folder of your choice. 
2. Add to the sol.exe *Path* to Environment variables. 
3. Test the command by opening a command prompt (cmd.exe) and running "sol -v" or "sol version". For more help on commands type "sol -h".

## Linux installation. 
1. Copy the executable ClientCli-x.x.x.jar and the 'lib' directory somewhere. 
2. Run `source solinstall.sh` to create an alias. Alternatively, the alias can be created directly via the follwing command:
`alias sol="java -jar <path-to-jar>/ClientCli-<version>.jar"`
4. solinstall.sh and sol_completion scripts can be installed on shell startup as well by adding to .bashrc the lines:
```
source <path to script>/solinstall.sh
source <path to script>/sol_completion
```
3. Test the command by typing "sol -v" or "sol version". For more help on commands type "sol -h". 

## Integration with Cli-To-Semp tool
The application can be ingerated with Cli-To-Semp tool to generate and execute custom SEMP v1 commands against Solace Cloud instances. 

## Integration with SDKPerf tool
The application can be ingerated with SDKPerf by generating connection parameters for it to use for Solace Cloud instances.

## Integration with SolConfig tool
Since version 0.4.0, the Client CLI uses a slightly modified SolClient library, which is used for configuration backups and restores. Current implementation is 1.1.5. The repo can be found here:
https://github.com/flyisland/solconfig   

## Initialization 
The application registers its executable as "sol" console command and provides an easy console usage by typing commands like "sol `commands`". For more information type `sol help` or `sol -h`.

## Some example commands and common tasks using the Client CLI tool.

### Login to Solace Cloud 
Login to Solace Cloud is done via the same credentials a user has for the Solace Cloud GUI. Sample login commands are listed here:

`sol login -u=<username> -p=<password>`
`sol login -u=<username> -p=<password> -o=<organisation ID>`

Once the user is logged in, a temporary token is issued and stored in the config file to be reused for other commands.

For **Solace Cloud SSO** login or for **permanent** token, the user should login to Solace Cloud via the GUI and manually issue a token from the Token page. 
`sol login -u=<username> -t=<token>`

### Logout from Solace Cloud 
`sol logout`

Log out and clean currently set context in the configuration such as context serviceId:

`sol logout -c`  

### Accounts
To list available organizations for an account: 
`sol account list`
To switch an account:
`sol account switch <organisation ID>`

### Configuration
`sol config <options>`

### Data centers
`sol dc list`

### Intermission
`sol jolt`

### Services
The following operations on services are currently available:
- bridge   - Handles service bridges.
- ca       - Handles service certificate authorities.
- classes  - Displays available service classes.
- config   - Creates config backups and restore services. 
- cp       - Handles service client profiles.
- create   - Creates a service.
- delete   - Deteles a service.
- details  - Lists all service details for a service.
- list     - Lists all services for a Solace Cloud Console Account.
- log      - Shows logs like Event, Command, Debug, System and Rest. 
- queue    - Handles service queues.
- set      - Sets a service as the default service context by service name or service ID.
- types    - Displays available service types.
- username - Manages usernames.

#### Create a service
To create a service get available service types (currently 'developer' and 'enterprise' types are available):
`sol service types`

then find available dc:
`sol dc list`

and list of service classes:
`sol service classes`

After having the necessairy values you can create a service with the following command:
`sol service create -class=<serviceClassId> -dc=<datacenterId> -serviceName=<serviceName> -type=<serviceTypeId>`

for example: 
`sol service create -class=developer -dc=aws-eu-west-2a -serviceName=my-new-test-service -type=developer`

#### Delete a service
To delete a service:
`sol service delete -serviceName=<serviceName> | -serviceId=<serviceId>`

#### Retrieve service logs
To view a service log:
`sol service log log_type [lineCount] -serviceName=<serviceName> | -serviceId=<serviceId>`

#### List services
To list services and apply a search filter:
`sol service list -fn=<part name>`

To list only services created by current user:
`sol service list -mine`

#### Service backups
To backup a service the following command can be used to create an exported configuration file:
`sol service config backup `

To restore a service the following command can be used to import configuration from a file:
`sol service config restore <filepath>`

#### Setting default context service
To set a default context service which will be picked by default use:
`sol service set -serviceName=<service name> | -serviceId=<service ID>`

To remove the default service:
`sol service set -none`

#### Bridges
The following operations on bridges are currently available:  
- create - Creates a bridge.
- delete - Deteles a bridge.
- list   - Lists all bridges.

Client CLI creates secured bridges as standard, with a name generated in the following format: *<Local_VPN_name>_<Remote_VPN_name>*. A sample command to create a bridge to a remote service is shown below. The command also adds 2 subscriptions - *Ingoing Direct* subscription **t/v1/1** and *Outgoing Guaranteed* **t/v1/2**:
`sol service bridge create -rn=<serviceName> -s="t/v1/1 IN D" -s="t/v1/2 OUT G"`

The following command creates a bridge with custom bridge users (local username needs to be configured on the local service and remote username - on the remote service): 
`service bridge create -rn=<serviceName> -lu=<local username> -lp=<local password> -ru=<remote username> -rp=<remote password> -s="t/v1/1 IN D" -s="t/v1/2 OUT G" `

To create a TLS bridge with certificates, there are several prerequisites (refer to https://docs.solace.com/Solace-Cloud/ght_client_certs.htm):
1. Certificate authentication on both services need to be enabled
2. Both services need to have CA of the issuing authority (RootCaCert), as well as the client certificates and chain if any (ClientCert).
3. Both services should have configured users to match the CN name of the certificates (*<local TCN>* and *<remote TCN>* if different CN names)
Parameters *localUsername* and *remoteUsername* should point to filepaths of files containing the client private key and certificate appended together as:
`-----BEGIN RSA PRIVATE KEY-----`
`...`
`-----END RSA PRIVATE KEY-----`
`-----BEGIN CERTIFICATE-----`
`...`
`-----END CERTIFICATE-----`
  
The CN name of the certificate content has to be added as well as *-cert=true* parameter to the command:
`service bridge create -rn=<serviceName> -cert=true -lu=<local user key path> -lp=<local password> -ltcn=<local TCN> -ru=<remote user key path> -rp=<remote password> -rtcn=<remote TCN> -s="t/v1/1 IN D" -s="t/v1/2 OUT G" `

To delete a bridge, the name or the id of the remote service need to be passed to the context of a local service:
`service bridge delete -rn=<serviceName>`

#### CAs
The following operations on CAs are currently available:  
- add    - Adds a certificate authority.
- delete - Deteles a certificate authority.
- list   - Lists all certificate authorities.

To add a CA, you can create a file with the CA content (if a chain of authority is required, create separate file and CA for each chained authority), and then add the CA with a given name and file path:
`service ca add <CA Name> <Path to CA file>`

#### Client Profiles
The following operations on Client Profiles are currently available:  
- create  - Creates a client profile.
- delete  - Deteles a client profile.
- details - Details for a client profile.
- list    - Lists all client profiles for a Solace Cloud Console Account.
A sample command is:
`sol service cp create <profileName>`

#### Queues
The following operations on queues are currently available: 
- copy    - Copies or moves messages from one service queue to another service queue 
- create  - Creates a queue.
- delete  - Deteles a queue.
- details - Details of a queue.
- stats   - Displays queue statistics.
- list    - Lists all queues.
- purge   - Purges messages from a queue.
 
To create a queue: 
`sol service queue create <queueName> -exclusive`

To copy messages from one queue to another on the same service:
`sol service queue copy [-ln=<localServiceName>] -lq=<sourceQueueName> -rq=<targetQueueName> -mn=<message number to copy> `

To copy queue messages to a different service:
`sol service queue copy [-ln=<localServiceName>] -lq=<localQueueName> -rn=<remoteServiceName> -rq=<remoteQueueName> -mn=<message number to copy> `

To copy queue messages with specific usernames:
`sol service queue copy [-ln=<localServiceName>] -lq=<localQueueName> -lu-<local username> -lp=<local password> -rn=<remoteServiceName> -rq=<remoteQueueName> -ru-<remote username> -rp=<remote password> -mn=<message number to copy> `

To move queue messages:
`sol service queue copy -r [-ln=<localServiceName>] -lq=<localQueueName> -rn=<remoteServiceName> -rq=<remoteQueueName> -mn=<message number to move> `

To copy/move queue messages with DMQ and optional TTL:
`sol service queue copy -r -dmq [-ttl=<millis>] [-ln=<localServiceName>] -lq=<localQueueName> -rn=<remoteServiceName> -rq=<remoteQueueName> -mn=<message number to move> `

To purge messages:
`sol service queue purge <queueName>` 

### Roles
`sol user roles`

### Users
sol user create <firstname> <lastname> <email> roles 
`sol user create <firstname> <lastname> <email> messaging-service-editor`


### Integration
Integration with Solace CLI provides a way to execute a command against the service CLI:
`sol hammer callCli  <cli command> [-serviceId=id | -serviceName=name]`

Integration with CLI to SEMP tool provides a way to generate CLI commands using the tool (the path to the tool needs to be configiures using *sol config* command):
`sol hammer cliToSemp <cli command>`

Integration with cURL  provides a way to excute command with cURL:
`sol hammer scurl [-serviceId=id | -serviceName=name] [-i]`

Integration with SDKPerf provides a command to generate a connection string for SDKPerf (the path to SDKPerf needs to be configiures using *sol config* command):
`sol hammer sperf [-serviceId=id | -serviceName=name] [-s] [-ss]`

Integration with SempConfig tool by Island Chen (https://github.com/flyisland/sempcfg) provides a way to export VPN configuration and import it into another service:

### Help
`sol help`

### Version
`sol version` | `sol -v`

## Release history
0.4.0 Support for configuration backup and restores.
0.4.1 Support for message copy DMQ eligible flag and TTL for cases when a message is copied from a DMQ back to a normal queue.
0.4.2 Support for viewing logs.
0.4.3 Support for backup custom filenames.