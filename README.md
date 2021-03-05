# Solace Client CLI 

## Description
Solace Client CLI (codename: Project Corona) is an application for handling Solace Cloud operations via the command line.
The Client CLI can handle multiple cloud accounts by creating different profiles for each account and can also handle multiple organisations under a specified account. 

## Internal libraries part of the application release
The application is using libraries (sempv1-interface and sempv2-interface) for executing SEMP V1 and SEMP v2 commands from the client's command line. 

## Windows installation:
1. Copy the executable sol.exe and the 'lib' subfolder in a folder of your choice. 
2. Add to the System Path variable the path to the sol.exe file. 
3. Test the command by opening a command prompt (cmd.exe) and running "sol -v" or "sol version". For more help on commands type "sol -h".

## Linux installation. 
1. Copy the executable ClientCli-0.0.1-SNAPSHOT.jar and the 'lib' directory somewhere. 
2. Run /sol.sh to create an alias or add the alias command to the user profile script.
3. Test the command by typing "sol -v" or "sol version". For more help on commands type "sol -h". 

## Integration with Cli-To-Semp tool
The application can be ingerated with Cli-To-Semp tool to generate and execute custom SEMP v1 commands against Solace Cloud instances. 

## Integration with SDKPerf tool
The application can be ingerated with SDKPerf by generating connection parameters for it to use for Solace Cloud instances.

## Initialization 
The application registers its executable as "sol" console command and provides an easy console usage by typing commands like "sol `commands`". For more information type `sol help` or `sol -h`.

## Common commands description. 

### Login to Solace Cloud 
sol login -u=`username` -p=`password`
sol login -u=`username` -p=`password` -o=`organisation ID`

### Logout from Solace Cloud 
sol logout
sol logout -c // Logs out and cleans currently set context in the configuration such as context serviceId

### Accounts
sol account `options`

### Configuration
sol config `options`

### Data centers
sol dc `options`

### Intermission
sol jolt

### Services
sol service `options`:
	bridge  - Handles service bridges.
	ca      - Handles service certificate authorities.
	classes - Displays available service classes.
	cp      - Handles service client profiles.
	create  - Creates a service.
	delete  - Deteles a service.
	details - Lists all service details for a service.
	list    - Lists all services for a Solace Cloud Console Account.
	queue   - Handles service queues.
	set     - Sets a service as the default service context by service name or service ID.
	types   - Displays available service types.

#### Bridges

#### CAs

#### Client Profiles

#### Queues

### Roles
sol roles `options`

### Users
sol user `options`

### Integration
sol hammer callCli  `cli command` [`-serviceId=id` | `-serviceName=name`]

sol hammer cliToSemp `cli command`

sol hammer scurl [`-serviceId=id` | `-serviceName=name`] [-i]

sol hammer sperf [`-serviceId=id` | `-serviceName=name`] [-s] [-ss]

### Help
sol help

### Version
sol version | sol -v