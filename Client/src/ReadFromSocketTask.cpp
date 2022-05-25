//
// Created by spl211 on 02/01/2022.
//
#include <boost/asio/ip/tcp.hpp>
#include "ReadFromSocketTask.h"
#include "ClientConnectionHandler.h"
#include <thread>

using boost::asio::ip::tcp;
using std::cin;
using std::cout;
using std::cerr;
using std::endl;
using std::string;


ReadFromSocketTask::ReadFromSocketTask(ClientConnectionHandler* _connectionHandler):connectionHandler(_connectionHandler){}

void ReadFromSocketTask:: run(){

    while(*shouldTerminate==0){
        //std::cout << "im in readFromSocket" <<std::endl;
        // We can use one of three options to read data from the server:
        // 1. Read a fixed number of characters
        // 2. Read a line (up to the newline character using the getline() buffered reader
        // 3. Read up to the null character

        //std::string answer; - delete

        // Get back an answer: by using the expected number of bytes (len bytes + newline delimiter)
        // We could also use: connectionHandler.getline(answer) and then get the answer without the newline char at the end
        short opcode = connectionHandler->getShort();
    //    std::cerr << "opcode is :" << opcode << " im in readFromSocket" << std::endl;
        if(opcode == 9){

            char type;
            connectionHandler->getBytes(&type, 1);

            std::string postingUser;
            connectionHandler->getFrameAscii(postingUser, '\0');

            std::string content;
            connectionHandler->getFrameAscii(content, '\0');

            if (type == '0'){
                std::string date;
                connectionHandler->getFrameAscii(date, '\0');
                std::cerr << "NOTIFICATION " << type << " " << postingUser
                          << " " << content  <<date <<std::endl;
            }
            else{
                std::cerr << "NOTIFICATION " << type << " " << postingUser
                          << " " << content <<std::endl;
            }


            connectionHandler->getBytes(&type, 1);
            //change delimiter
        }
        else if(opcode == 10){

            short msgOpcode = connectionHandler->getShort();

            if(msgOpcode == 4){
               // std::cerr << "im in private case : ack of follow/unfollow" << std::endl;
                std::string content;
                connectionHandler->getFrameAscii(content, ';');
                int len = content.length() ;
                content.resize(len - 1);
                std::cerr << "ACK " << msgOpcode << " " << content << std::endl ;

            }
            else if(msgOpcode == 7 || msgOpcode == 8){ //logstat,stat
                std::string output = "" ;
                bool flag = true;
                short age = connectionHandler->getShort();
                if( age == 0){
                    flag = false;
                    connectionHandler->getShort();
                    std::cerr << "ACK " << msgOpcode <<std::endl;
                }

                else{

                    short numPosts = connectionHandler->getShort();
                    short numFollowers = connectionHandler->getShort();
                    short numFollowing = connectionHandler->getShort();
                    output = "ACK " + std::to_string(msgOpcode) + " " + std::to_string(age) + " " +
                             std::to_string(numPosts) + " " + std::to_string(numFollowers) + " " + std::to_string(numFollowing)+"\n";
                }

                while(flag){
                 //   std::cout << "im in read from socket , case-logstat,stat inside the loop"<<std::endl;
                    opcode = connectionHandler->getShort();
                    if(opcode < 13){
                        msgOpcode = connectionHandler->getShort();
                        short age = connectionHandler->getShort();
                        short numPosts = connectionHandler->getShort();
                        short numFollowers = connectionHandler->getShort();
                        short numFollowing = connectionHandler->getShort();

                        output += "ACK " + std::to_string(msgOpcode) + " " + std::to_string(age) + " " +
                                std::to_string(numPosts) + " " + std::to_string(numFollowers) + " " + std::to_string(numFollowing)+"\n";
                    }
                    else
                        flag = false;

                }
                std::cerr << output ;
            }

            else{
                connectionHandler->getShort(); // for clear ;
                //std::cerr << "before printing ,im in readFromSocket"<<std::endl;
                std::cerr << "ACK " << msgOpcode <<std::endl;

                /*do {
                    *x = 0 ;
                    std::this_thread::yield();
                } while (msgOpcode == 3); */

                if(msgOpcode == 3){
                    std::cerr << "Exiting...\n" << std::endl;
                    *shouldTerminate = 1;
                    break;
                }
            }
        }
        //error
        else{
            short msgOpcode = connectionHandler->getShort();
            connectionHandler->getShort(); // for clear ;
            std::cerr << "ERROR " << msgOpcode <<std::endl ;
            if(msgOpcode == 3){
                *tryLogout = 0;
            }
        }


        // A C string must end with a 0 char delimiter.  When we filled the answer buffer from the socket
        // we filled up to the \n char - we must make sure now that a 0 char is also present. So we truncate last character.

    }

}
