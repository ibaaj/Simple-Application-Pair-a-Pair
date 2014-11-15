#!/bin/bash
javac -encoding UTF-8 Server.java
javac -encoding UTF-8 Client.java
PORT=10008
osascript <<END
tell application "Terminal"
  do script "cd \"`pwd`\";./loadServer.sh $PORT;exit"
end tell
END
sleep 1
for i in `seq 9900 9905`;
do
osascript <<END
tell application "Terminal"
  do script "cd \"`pwd`\";./loadClient.sh $i;exit"
end tell
END
done
