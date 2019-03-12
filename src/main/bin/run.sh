#!/bin/bash

# 根目录
PACKAGE_HOME=$(cd "$(dirname "$0")";cd ..;pwd)

# Java程序主体所在的目录，即classes的上一级目录
MAIN_CLASS=cn.teleinfo.captcha.upms.PlatformUpmsApplication

# 日志的默认输出目录
LOG_DIR=$PACKAGE_HOME/logs
LOG_BASE_NAME=debug.log

# JVM启动参数
# -server：一定要作为第一个参数，多个CPU时性能佳
# -Xloggc：记录GC日志，建议写成绝对路径，如此便可在任意目录下执行该shell脚本
JAVA_OPTS=${JVM_OPTS:-"-server -Xms1024m -Xmx2048m -Xloggc:$LOG_DIR/gc.log"}

# classpath
CLASSPATH="${PACKAGE_HOME}/classes:${PACKAGE_HOME}/lib/*"

# 初始化全局变量，用于标识交易前置系统的PID（0表示未启动）
tradePortalPID=0

# 获取Java应用的PID
# ------------------------------------------------------------------------------------------------------
# 说明：通过JDK自带的jps命令，联合Linux中的grep命令，可以准确查找到Java应用的PID
#       [jps -l]表示显示Java主程序的完整包路径
#       awk命令可以分割出PID（$1部分）及Java主程序名称（$2部分）
# 例子：[$JAVA_HOME/bin/jps -l | grep $MAIN_CLASS]命令执行，会看到[5775 com.cucpay.tradeportal.MainApp]
# 另外：这个命令也可以取到程序的PID-->[ps aux|grep java|grep $MAIN_CLASS|grep -v grep|awk '{print $2}']
# ------------------------------------------------------------------------------------------------------
function getTradeProtalPID(){
    javaps=`jps -l | grep $MAIN_CLASS`
    if [ -n "$javaps" ]; then
        tradePortalPID=`echo $javaps | awk '{print $1}'`
    else
        tradePortalPID=0
    fi
}

# 启动Java应用程序
# ------------------------------------------------------------------------------------------------------
# 1、调用getTradeProtalPID()函数，刷新$tradePortalPID全局变量
# 2、若程序已经启动（$tradePortalPID不等于0），则提示程序已启动
# 3、若程序未被启动，则执行启动命令
# 4、启动命令执行后，再次调用getTradeProtalPID()函数
# 5、若步骤4执行后，程序的PID不等于0，则打印Success，反之打印Failed
# 注意：[echo -n]表示打印字符后不换行
# 注意：[nohup command > /path/nohup.log &]是将作业输出到nohup.log，否则它会输出到该脚本目录下的nohup.out中
# ------------------------------------------------------------------------------------------------------
function startup(){
    getTradeProtalPID
    echo "==============================================================================================="
    if [ $tradePortalPID -ne 0 ]; then
        echo "$MAIN_CLASS already started(PID=$tradePortalPID)"
        echo "==============================================================================================="
    else
	    if [ ! -d $LOG_DIR ]; then
	        mkdir $LOG_DIR
	    fi
        echo -n "Starting $MAIN_CLASS"
	    nohup java $JAVA_OPTS -classpath $CLASSPATH $MAIN_CLASS >/dev/null 2>&1 &
	    sleep 1
        getTradeProtalPID
        if [ $tradePortalPID -ne 0 ]; then
            echo "(PID=$tradePortalPID)...[Success]"
            echo "==============================================================================================="
            tail -f $LOG_DIR/$LOG_BASE_NAME
        else
            echo "[Failed]"
            echo "==============================================================================================="
        fi
    fi
}


function shutdown(){
    getTradeProtalPID
    echo "==============================================================================================="
    if [ $tradePortalPID -ne 0 ]; then
        echo -n "Stopping $MAIN_CLASS(PID=$tradePortalPID) waiting 5s ..."
        kill $tradePortalPID
        sleep 5
        if [ $? -eq 0 ]; then
            echo "[Success]"
            echo "==============================================================================================="
        else
            echo "[Failed]"
            echo "==============================================================================================="
        fi
        getTradeProtalPID
        if [ $tradePortalPID -ne 0 ]; then
            shutdown
        fi
    else
        echo "$MAIN_CLASS is not running"
        echo "==============================================================================================="
    fi
}

function docker(){
    if [ ! -d $LOG_DIR ]; then
	    mkdir $LOG_DIR
	fi
   java $JAVA_OPTS -classpath $CLASSPATH $MAIN_CLASS
}

function status(){
    getTradeProtalPID
    echo "==============================================================================================="
    if [ $tradePortalPID -ne 0 ]; then
        echo "$MAIN_CLASS already started(PID=$tradePortalPID)"
        echo "==============================================================================================="
        tail -f $LOG_DIR/$LOG_BASE_NAME
    else
        echo "$MAIN_CLASS is not running"
        echo "==============================================================================================="
    fi
}

case "$1" in
  start)
    startup
  ;;

  stop)
	shutdown
  ;;

  status)
  	status
    ;;

  docker)
      docker
   ;;

  restart)
    shutdown
    startup
  ;;

  *)
    echo "please use : ./run.sh {start|stop|restart|status}"
  ;;
esac
exit 0
