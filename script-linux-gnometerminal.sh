#!/bin/bash
javac Server.java
javac Client.java
PORT=10008
gnome-terminal -x ./loadServer.sh $PORT
sleep 1
for i in `seq 9900 9905`;
do
  gnome-terminal -x ./loadClient.sh $i;
done
