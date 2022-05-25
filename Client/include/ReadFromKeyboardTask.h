//
// Created by spl211 on 02/01/2022.
//

#ifndef BOOST_ECHO_CLIENT_READFROMKEYBOARDTASK_H
#define BOOST_ECHO_CLIENT_READFROMKEYBOARDTASK_H
#include <connectionHandler.h>
extern int* shouldTerminate;
extern int* tryLogout;
class ReadFromKeyboardTask {

private:
   ClientConnectionHandler* connectionHandler;

public:
    ReadFromKeyboardTask(ClientConnectionHandler* _connectionHandler);
    void run();
    short findOpcode(std::string strOpcode);
    std::vector<std::string> splitStr(std::string content);
    size_t makeMessage(short opcode, std::string& content, std::string& ans);
};


#endif //BOOST_ECHO_CLIENT_READFROMKEYBOARDTASK_H
