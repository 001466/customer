#!/bin/sh
echo "执行停止脚本..."

#JAR路径
JAR_MAIN=busineService.jar

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
shutdown(){
    getPid
    if [ -n "$pid" ]; then
        echo "检查到服务已启动... $pid"
        echo "Kill $pid"
        kill -9 $pid
        echo "服务已停止..."
    else
        echo "未检测到服务..."
    fi
}
shutdown
