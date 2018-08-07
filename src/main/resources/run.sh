#!/bin/sh
case "$1" in 
  start)
    java -Xmx128m -Xms128m -XX:+UseParallelGC -XX:+AggressiveOpts -XX:+UseFastAccessorMethods -jar captcha.jar & 
    echo $! > server.pid
    ;;
 
  stop)
    kill `cat server.pid`
    rm -rf server.pid
    ;;
 
  restart)
    $0 stop
    sleep 1
    $0 start
    ;;
 
  *)
    echo "Usage: run.sh {start|stop|restart}"
    ;;
 
esac
 
exit 0
