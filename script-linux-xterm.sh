#!/bin/bash
javac Server.java
javac Client.java
PORT=10008
xterm -e ./loadServer.sh $PORT;
sleep 1
for i in `seq 9900 9905`;
do
  xterm -e ./loadClient.sh $i;
done
