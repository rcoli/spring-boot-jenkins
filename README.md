# spring-boot-jenkins
Example of the deployment of a Spring Boot App with Jenkins in a Unix System

One thing that I found very hard to do was to integrate a spring boot project into a CI environment using jenkins. As a default behavior, the Jenkins process tree killer always kills the process started by a job which stops the execution of a Spring Boot App after the jenkins job finishes. In addition of that, I wanted to see the server log on the jenkyns windows until it finishes loading. This article will try to help us solving this problems.

But first I would like to discuss what I consider a good practice to a Spring Boot App CI environment. I find very useful to first copy the artifacts to a specified area on the server to keep track of the artifacts deployed and deploy the artifact from that location. Also, I create a server log  file there and start to listening on the jenkins window until the server started.

So the script below does that. With some minor improvements self explained on the comments, but in summary it does this:

- stop whatever process running on the deployed port. 
- delete the files of the previous deploy 
- copy the files to deploy location 
- start application with nohup command, java - jar
- start listens to the server log until it reaches an specific instruction.


Finally you have to do some adjustments to your job on Jenkins to avoid the default tree killing process. Just add this instruction before calling the sh: BUILD_ID=dontKillMe /path/to/my/script.sh (FIGURE 3) 

You can see the jenkins job configuration window on FIGURES 1, 2, and 3 and the log result window on FIGURES 4 and 5.

Go to my github repo to check the project, but it recommend to extract the shell script to another repo to keep it lifecycle independent of your app.

https://github.com/rcoli/spring-boot-jenkins
  

This is my deploy folder structure (FIGURE 6):
```
-- spring-boot

---- dev
------ resources
-------- application.yml
------ initServer.log
------ my-app-jar

---- sit
------ resources
-------- application.yml
------ initServer.log
------ my-app-jar

---- uat
------ resources
-------- application.yml
------ initServer.log
------ my-app-jar
```
* dev - develop, sit - system integration testing, uat - user acceptance testing, application.yml - external app configuration file


This my project folder structure (FIGURE 7 and 8):
```
-- my-project
---- resources
------ application.yml
---- api
------ src
------ (other project file)
------ build.gradle
```

The script example.

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

--- Jenkins Job Configuration (Git) FIGURE 1
![alt tag](https://cloud.githubusercontent.com/assets/1146514/10940518/ed6d4062-82ed-11e5-88e8-6529970d2831.png)

--- Jenkins Job Configuration (Gradle) FIGURE 2
![alt tag](https://cloud.githubusercontent.com/assets/1146514/10940527/fc1a0078-82ed-11e5-9dd7-aa75924b1d3f.png)

--- Jenkins Job Configuration (Deploy) FIGURE 3
![alt tag](https://cloud.githubusercontent.com/assets/1146514/10940534/0678e232-82ee-11e5-84dd-6ca751e66903.png)



--- Jenkins Summary Beginning FIGURE 4
![alt tag](https://cloud.githubusercontent.com/assets/1146514/10939540/74ed1058-82e9-11e5-9ca8-fcdfa9138647.png)

--- Jenkins Summary Finnished (Job Complete) FIGURE 5
![alt tag](https://cloud.githubusercontent.com/assets/1146514/10939547/7a37dc6e-82e9-11e5-9b1e-bda47592ed6d.png)


--- Deploy Structure Folder FIGURE 6
![alt tag](https://cloud.githubusercontent.com/assets/1146514/10939616/0aefad86-82ea-11e5-8d6b-40ca67df04f2.png)


--- Project structure folder FIGURE 7
![alt tag](https://cloud.githubusercontent.com/assets/1146514/10939537/708ed014-82e9-11e5-85e1-c53ac1d219eb.png)

--- External Resources Folder FIGURE 8
![alt tag](https://cloud.githubusercontent.com/assets/1146514/10939548/7e91ed90-82e9-11e5-8a61-31e6f6f9c42a.png)
