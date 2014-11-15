javac -encoding UTF-8 Server.java
javac -encoding UTF-8 Client.java
set PORT=10008
START cmd /k "java Server %PORT%"
ping -n 2 127.0.0.1 > nul
START cmd /k "java Client 127.0.0.1:%PORT% 9900"
START cmd /k "java Client 127.0.0.1:%PORT% 9901"
START cmd /k "java Client 127.0.0.1:%PORT% 9902"
START cmd /k "java Client 127.0.0.1:%PORT% 9903"
START cmd /k "java Client 127.0.0.1:%PORT% 9904"
START cmd /k "java Client 127.0.0.1:%PORT% 9905"
pause
