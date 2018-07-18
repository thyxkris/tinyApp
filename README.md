# tinyApp
a tiny app to help demo in aws network setup, showing ip/hostname/cpu/memory /router info etc

```bash

#!/bin/bash

# upgrade machine
sudo yum update -y

# install java 8 jdk
sudo yum install -y java-1.8.0-openjdk-devel

# set java jdk 8 as default
sudo /usr/sbin/alternatives --config java
sudo /usr/sbin/alternatives --config javac

# verify java 8 is the default
java -version

# Download app
cd /home/ec2-user
wget https://github.com/thyxkris/tinyApp/raw/master/tinyApp-1.1-jar-with-dependencies.jar

# Test the app
java -Xmx700m -jar tinyApp-1.1-jar-with-dependencies.jar

# long running configuration (persist after reboot)
# write the tinyApp file
sudo bash -c 'cat << \EOF > /etc/init.d/tinyApp
#
# tinyApp    Init script for tinyApp
#
# chkconfig: 345 99 76
# processname: tinyApp
#
APP_EXEC="/usr/bin/java -Xmx700m -jar /home/ec2-user/tinyApp-1.1-jar-with-dependencies.jar"
NAME=tinyApp
PIDFILE=/var/run/$NAME.pid
LOG_FILE="/home/ec2-user/$NAME.log"
SCRIPTNAME=/etc/init.d/$NAME
RETVAL=0

start() {
    echo "Starting $NAME..."
    $APP_EXEC 1>"$LOG_FILE" 2>&1 &
    echo $! > "$PIDFILE"
    echo "$NAME started with pid $!"
}

status() {
    printf "%-50s" "Checking $NAME..."
    if [ -f $PIDFILE ]; then
        PID=$(cat $PIDFILE)
        if [ -z "$(ps axf | grep ${PID} | grep -v grep)" ]; then
            printf "Process dead but pidfile exists"
        else
            echo "Running"
        fi
    else
        printf "Service not running"
    fi
}

stop() {
    printf "%-50s" "Stopping $NAME"
        PID=`cat $PIDFILE`
    if [ -f $PIDFILE ]; then
        kill -HUP $PID
        printf "%s\n" "Ok"
        rm -f $PIDFILE
    else
        printf "%s\n" "pidfile not found"
    fi
}

case "$1" in
    start)
    start
    ;;
    status)
    status
    ;;
    stop)
    stop
    ;;
    restart)
        $0 stop
        $0 start
    ;;
    *)
        echo "Usage: $0 {status|start|stop|restart}"
        exit 1
    ;;
esac
EOF'

# permissions to start the file
sudo chmod +x /etc/init.d/tinyApp

# apply across reboots
sudo chkconfig --add tinyApp
sudo chkconfig tinyApp on

```
