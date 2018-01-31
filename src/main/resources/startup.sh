#!/bin/sh
echo "Startup..."

#JAR path
JAR_MAIN=ordersvr.jar

#Get PID
pid=0

getPid(){
    javaps='jps -l | grep $JAR_MAIN'
    if [ -n "$javaps" ]; then
        pid=$(jps -l | grep $JAR_MAIN | awk '{print $1}')
    else
        pid=0
    fi
}

#Check to see if there is a started service
startup(){
    getPid
    if [ -n "$pid" ]; then
        echo "Find started service... $pid"
    else    
        exec java -server -jar  $JAR_MAIN &
        getPid
        echo "Service started ... $pid"
    fi
}
startup
















