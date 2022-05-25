//
// Created by spl211 on 03/01/2022.
//
#include <ClientConnectionHandler.h>
#include <thread>
#include "ReadFromKeyboardTask.h"
#include "ReadFromSocketTask.h"


__attribute__((unused)) int* shouldTerminate = new int(0);
__attribute__((unused)) int* tryLogout = new int(0);

int main(int argc, char** argv){
    if (argc < 3) {
        std::cerr << "Usage: " << argv[0] << " host port" << std::endl << std::endl;
        return -1;
    }
    std::string host = argv[1];
    short port = atoi(argv[2]);

    ClientConnectionHandler* connectionHandler = new ClientConnectionHandler(host, port);
    if (!connectionHandler->connect()) {
        std::cerr << "Cannot connect to " << host << ":" << port << std::endl;
        return 1;
    }

    ReadFromKeyboardTask task1(connectionHandler);
    ReadFromSocketTask task2(connectionHandler);


    std::thread th1(&ReadFromKeyboardTask::run, &task1);
    std::thread th2(&ReadFromSocketTask::run, &task2);

    th1.join();
    th2.join();


    delete shouldTerminate;
    delete tryLogout;
    delete connectionHandler;
}