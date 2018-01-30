#!/bin/sh
echo "Shutdown..."

#JAR path
JAR_MAIN=app.jar

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
shutdown(){
    getPid
    if [ -n "$pid" ]; then
        echo "Find started service... $pid"
        echo "Kill $pid"
        kill -9 $pid
        echo "Service stopped..."
    else
        echo "Can not find started service..."
    fi
}
shutdown
