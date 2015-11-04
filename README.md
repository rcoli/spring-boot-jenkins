# spring-boot-jenkins
Example of the deployment of a Spring Boot App with Jenkins in a Unix System



Here is the script example.

```bash

#!/bin/bash

# COMMAND LINE VARIABLES
#enviroment FIRST ARGUMENT 
# Ex: dev | sit | uat
env=$1
# deploy port SECOND ARGUMENT
# Ex: 8090 | 8091 | 8092 
serverPort=$2
# THIRD ARGUMENT project name, deploy folder name and jar name
projectName=$3 #spring-boot
# FOURTH ARGUMENT external config file name
# Ex: application-localhost.yml
configFile=$4


#### CONFIGURABLE VARIABLES ######
#destination absolute path. It must be pre created or you can
# improve this script to create if not exists
destAbsPath=/home/rcoli/Desktop/$projectName/$env
configFolder=resources
##############################################################

#####
##### DONT CHANGE HERE ##############
#jar file
# $WORKSPACE is a jenkins var
sourFile=$WORKSPACE/api/build/libs/$projectName*.jar
destFile=$destAbsPath/$projectName.jar

#config files folder
sourConfigFolder=$WORKSPACE/$configFolder*
destConfigFolder=$destAbsPath/$configFolder

properties=--spring.config.location=$destAbsPath/$configFolder/$configFile

#CONSTANTS
logFile=initServer.log
dstLogFile=$destAbsPath/$logFile
#whatToFind="Started Application in"
whatToFind="Started "
msgLogFileCreated="$logFile created"
msgBuffer="Buffering: "
msgAppStarted="Application Started... exiting buffer!"

### FUNCTIONS
##############
function stopServer(){
    echo " "
    echo "Stoping process on port: $serverPort"
    fuser -n tcp -k $serverPort > redirection &
    echo " "
}

function deleteFiles(){
    echo "Deleting $destFile"
    rm -rf $destFile

    echo "Deleting $destConfigFolder"
    rm -rf $destConfigFolder

    echo "Deleting $dstLogFile"
    rm -rf $dstLogFile
    
    echo " "
}

function copyFiles(){
    echo "Copying files from $sourFile"
    cp $sourFile $destFile

    echo "Copying files from $sourConfigFolder"
    cp -r $sourConfigFolder $destConfigFolder

    echo " "
}

function run(){

   #echo "java -jar $destFile --server.port=$serverPort $properties" | at now + 1 minutes

   nohup nice java -jar $destFile --server.port=$serverPort $properties $> $dstLogFile 2>&1 &

   echo "COMMAND: nohup nice java -jar $destFile --server.port=$serverPort $properties $> $dstLogFile 2>&1 &"

    echo " "
}
function changeFilePermission(){

    echo "Changing File Permission: chmod 777 $destFile"

    chmod 777 $destFile

    echo " "
}   

function watch(){
 
    tail -f $dstLogFile |

        while IFS= read line
            do
                echo "$msgBuffer" "$line"

                if [[ "$line" == *"$whatToFind"* ]]; then
                    echo $msgAppStarted
                    pkill  tail
                fi
        done
}

### FUNCTIONS CALLS
#####################
# Use Example of this file. Args: enviroment | port | project name | external resourcce
# BUILD_ID=dontKillMe /path/to/this/file/api-deploy.sh dev 8082 spring-boot application-localhost.yml

# 1 - stop server on port ...
stopServer

# 2 - delete destinations folder content
deleteFiles

# 3 - copy files to deploy dir
copyFiles

changeFilePermission
# 4 - start server
run

# 5 - watch loading messages until  ($whatToFind) message is found
watch
```

--- Project structure folder
![alt tag](https://cloud.githubusercontent.com/assets/1146514/10939537/708ed014-82e9-11e5-85e1-c53ac1d219eb.png)

--- External Resources Folder
![alt tag](https://cloud.githubusercontent.com/assets/1146514/10939548/7e91ed90-82e9-11e5-8a61-31e6f6f9c42a.png)

--- Deploy Structure Folder
![alt tag](https://cloud.githubusercontent.com/assets/1146514/10939616/0aefad86-82ea-11e5-8d6b-40ca67df04f2.png)

--- Jenkins Summary Beginning
![alt tag](https://cloud.githubusercontent.com/assets/1146514/10939540/74ed1058-82e9-11e5-9ca8-fcdfa9138647.png)

--- Jenkins Summary Finnished (Job Complete)
![alt tag](https://cloud.githubusercontent.com/assets/1146514/10939547/7a37dc6e-82e9-11e5-9b1e-bda47592ed6d.png)



