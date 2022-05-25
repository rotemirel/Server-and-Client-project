//
// Created by spl211 on 02/01/2022.
//
#include <ClientConnectionHandler.h>
#include "ReadFromKeyboardTask.h"
#include "ReadFromSocketTask.h"
#include <vector>
#include <ctime>

using boost::asio::ip::tcp;
using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;
using namespace std;

ReadFromKeyboardTask::ReadFromKeyboardTask(ClientConnectionHandler* _connectionHandler)
:connectionHandler(_connectionHandler){} // check!

void ReadFromKeyboardTask:: run() {

    while (*shouldTerminate == 0) {
        const short bufsize = 1024;
        char buf[bufsize];

        while (*tryLogout == 1){
            if (*shouldTerminate == 1)
                break;
        }
        if(*shouldTerminate == 1){
            break;
        }
        std::cin.getline(buf, bufsize);
        std::string line(buf);
        int index = line.find_first_of(" ");
        std::string strOpcode = line.substr(0,index);
        std::string content = line.substr(index+1);


        short opcode = findOpcode(strOpcode) ;
        std::string ans;
        size_t ansLen = makeMessage(opcode, content, ans);


        char shortBytes[2];
        connectionHandler->shortToBytes(opcode, shortBytes);

        char* ansBytes = const_cast<char *>(ans.c_str());

        char* bytes = new char[ ansLen + 3] ;

        bytes[0] = shortBytes[0];
        bytes[1] = shortBytes[1];

        for (size_t i = 2 ; i< ansLen+2 ; i++){
            bytes[i] = ansBytes[i-2] ;
        }

        if(opcode == 3){
            *tryLogout = 1;
        }

        if(!connectionHandler->sendBytes(bytes,ansLen+2 )){
            std::cout << "Disconnected. Exiting...\n" << std::endl;
            break;
        }


    }
}
short ReadFromKeyboardTask::findOpcode(std::string strOpcode){

    if(strOpcode == "REGISTER")
        return 1;
    else if(strOpcode == "LOGIN")
        return 2;
    else if(strOpcode == "LOGOUT")
        return 3;
    else if(strOpcode == "FOLLOW")
        return 4;
    else if(strOpcode == "POST")
        return 5;
    else if(strOpcode == "PM")
        return 6;
    else if(strOpcode == "LOGSTAT")
        return 7;
    else if(strOpcode == "STAT")
        return 8;
    else if(strOpcode == "BLOCK")
        return 12;

    return 0;
}
size_t ReadFromKeyboardTask::makeMessage(short opcode , std::string& content, std::string& ans){

    std::vector<std::string> parts;
    size_t size = 0 ;
    size_t index1;
    short op = opcode;

    if(op == 1){
        parts = splitStr(content);

        ans += parts[0];
        ans += '\0' ;
        ans += parts[1];
        ans += '\0' ;
        ans += parts[2];
        ans += '\0' ;
        size = parts[0].length() + parts[1].length() + parts[2].length() + 3 ;
    }
    else if(op == 2){
        parts = splitStr(content);

        ans += parts[0];
        ans += '\0' ;
        ans += parts[1];
        ans += '\0' ;
        ans += parts[2];

        size = parts[0].length() + parts[1].length() + parts[2].length() + 2 ;
    }
    else if(op == 4){
        parts = splitStr(content);

        ans += parts[0];
        ans += parts[1];
        ans += '\0' ;
        size = parts[0].length() + parts[1].length() + 1 ;
    }

    else if(op == 5 || op == 8 || op == 12){
        ans = content;
        ans += '\0';
        size = content.length() + 1 ;
    }
    else if(op == 6){
        index1 = content.find_first_of(' ');

        time_t now = time(0);
        tm* ltm = localtime(&now);
        int day = ltm->tm_mday;
        string strDay;
        if(day < 10)
            strDay = '0'+to_string(day);
        else
            strDay = to_string(day);

        int month = 1+ltm->tm_mon;
        string strMon;
        if(month < 10)
            strMon = '0'+to_string(month);
        else
            strMon = to_string(month);

        ans += content.substr(0,index1); //name
        ans += '\0';
        ans += content.substr(index1+1); // content without date
        ans += '\0';
        ans +=  strDay+ "-" + strMon + "-" + to_string(1900+ltm->tm_year); // date
        ans += '\0';

        size = content.length() +12;
    }

    ans += ';';
    size++;
    return size;
}
std::vector<std::string> ReadFromKeyboardTask::splitStr(std::string content){
    std::vector<std::string> parts;
    size_t prev =0 ,pos = 0;
    do{
        pos = content.find(' ',prev);
        if ( pos == string::npos)
            pos = content.length();
        string part = content.substr(prev , pos-prev);
        parts.push_back(part);
        prev = pos + 1;

    }
    while(pos < content.length() && prev < content.length());
    return parts;
}

