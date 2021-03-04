# Solace Client CLI 

## Description
Solace Client CLI (codename: Project Corona) is an application for handling Solace Cloud operations via the command line.
The Client CLI can handle multiple cloud accounts by creating different profiles for each account and can also handle multiple organisations under a specified account. 

## Internal libraries
The application is using libraries (sempv1-interface and sempv2-interface) for executing SEMP V1 and SEMP v2 commands from the client's command line. 

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
sol service `options`

#### Bridges

#### CAs

#### Client Profiles

#### Queues

### Roles
sol roles `options`

### Users
sol user `options`

### Integration
sol hammer callCli `options`
sol hammer cliToSemp `options`
sol hammer scurl `options`
sol hammer sperf `options`

### Help
sol help

### Version
sol version