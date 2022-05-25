//
// Created by spl211 on 02/01/2022.
//

#ifndef BOOST_ECHO_CLIENT_READFROMSOCKETTASK_H
#define BOOST_ECHO_CLIENT_READFROMSOCKETTASK_H


#include "ClientConnectionHandler.h"
extern int* shouldTerminate;
extern int* tryLogout;

class ReadFromSocketTask {
private:
    ClientConnectionHandler* connectionHandler;

public:
    ReadFromSocketTask(ClientConnectionHandler* _connectionHandler);
    void run();
};




#endif //BOOST_ECHO_CLIENT_READFROMSOCKETTASK_H
