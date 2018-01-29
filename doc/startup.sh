#!/bin/sh
echo "执行启动脚本..."

#JAR路径
JAR_MAIN=service.jar

#获取PID
pid=0

getPid(){
    javaps='jps -l | grep $JAR_MAIN'
    if [ -n "$javaps" ]; then
        pid=$(jps -l | grep $JAR_MAIN | awk '{print $1}')
    else
        pid=0
    fi
}

#检查是否有已启动的服务
startup(){
    getPid
    if [ -n "$pid" ]; then
        echo "检查到服务已启动... $pid"
    else    
        exec java -server -jar  $JAR_MAIN &
    fi
}
startup
















