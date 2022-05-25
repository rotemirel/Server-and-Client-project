# Server-and-Client-project

mvn clean
mvn compile

command line for reactor:
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.ReactorMain" -Dexec.args="7777 5"

command line for TPC:
mvn exec:java -Dexec.mainClass="bgu.spl.net.impl.BGSServer.TPCMain" -Dexec.args="7777"

commands for Client:
make
./bin/BGSclient 127.0.0.1 7777

examples for each message:

REGISTER dan 1234 12-01-1999
LOGIN dan 1234 1
LOGOUT
FOLLOW 0 dan
FOLLOW 1 dan
POST hi!
PM dan hi!
STAT dan|shay|may
LOGSTAT
BLOCK dan

filter words: field named :filteredWords at
Server/src/main/java/bgu/spl/net/Objects/Database

