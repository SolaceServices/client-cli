# Solace Client CLI 

## Description
Solace Client CLI (code name: Project Corona) is an application for handling Solace Cloud operations via the command line.
The Client CLI can handle multiple cloud accounts by creating different profiles for each account and can also handle multiple organisations under a specified account. 

## Internal libraries part of the application release
The application is using libraries (sempv1-interface and sempv2-interface) for executing SEMP V1 and SEMP v2 commands from the client's command line. 

## Windows installation:
1. Copy the executable sol.exe and the 'lib' subfolder in a folder of your choice. 
2. Add to the System Path variable the path to the sol.exe file. 
3. Test the command by opening a command prompt (cmd.exe) and running "sol -v" or "sol version". For more help on commands type "sol -h".

## Linux installation. 
1. Copy the executable ClientCli-x.x.x.jar and the 'lib' directory somewhere. 
2. Run /sol.sh to create an alias or add the alias command to the user profile script.
3. Test the command by typing "sol -v" or "sol version". For more help on commands type "sol -h". 

## Integration with Cli-To-Semp tool
The application can be ingerated with Cli-To-Semp tool to generate and execute custom SEMP v1 commands against Solace Cloud instances. 

## Integration with SDKPerf tool
The application can be ingerated with SDKPerf by generating connection parameters for it to use for Solace Cloud instances.

## Initialization 
The application registers its executable as "sol" console command and provides an easy console usage by typing commands like "sol `commands`". For more information type `sol help` or `sol -h`.

## Some example commands and common tasks using the Client CLI tool.

### Login to Solace Cloud 
`sol login -u=<username> -p=<password>`
`sol login -u=<username> -p=<password> -o=<organisation ID>`

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
- bridge  - Handles service bridges.
- ca      - Handles service certificate authorities.
- classes - Displays available service classes.
- cp      - Handles service client profiles.
- create  - Creates a service.
- delete  - Deteles a service.
- details - Lists all service details for a service.
- list    - Lists all services for a Solace Cloud Console Account.
- queue   - Handles service queues.
- set     - Sets a service as the default service context by service name or service ID.
- types   - Displays available service types.

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

#### List services
To list services and apply a search filter:
`sol service list -fn=<part name>`

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

A sample command to create a bridge to a remote service:
`sol service bridge create -rn=testService2 -s="t/v1/1 IN D" -s="t/v1/2 OUT G"`

#### CAs

#### Client Profiles

#### Queues
The following operations on queues are currently available: 
- create  - Creates a queue.
- delete  - Deteles a queue.
- details - Details of a queue.
- list    - Lists all queues.
- purge   - Purges messages from a queue.
 
To create a queue: 
`sol service queue create <queueName> -exclusive`

To purge messages:
`sol service queue purge <queueName>` 

### Roles
`sol roles options`

### Users
`sol user options`

### Integration
`sol hammer callCli  <cli command> [-serviceId=id | -serviceName=name]`

`sol hammer cliToSemp <cli command>`

`sol hammer scurl [-serviceId=id | -serviceName=name] [-i]`

`sol hammer sperf [-serviceId=id | -serviceName=name] [-s] [-ss]`

### Help
`sol help`

### Version
`sol version` | `sol -v`