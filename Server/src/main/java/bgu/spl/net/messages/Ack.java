package bgu.spl.net.messages;

import bgu.spl.net.Objects.User;
import sun.rmi.runtime.Log;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.concurrent.ConcurrentHashMap;

public class Ack implements ServerToClientMsg{

    private String content;
    private short opcode;
    private short messageOpcode;
    private List<Short> data;

    public Ack(short messageOpcode){

        opcode = 10;
        this.messageOpcode = messageOpcode;
        content = null;
        short i = ';' ;

        data = new LinkedList<>();
        data.add(opcode);
        data.add(messageOpcode);
        data.add(i);
    }
    public Ack(short messageOpcode , String userName , int type){ // follow unfollow

        opcode = 10;
        this.messageOpcode = messageOpcode;
        content = type+" "+userName+"\0"+';';

        data = new LinkedList<>();
        data.add(opcode);
        data.add(messageOpcode);
    }

    public Ack(short messageOpcode, List<User> users , User sender){ //Stat

        this.opcode = 10;
        this.messageOpcode = messageOpcode;
        content = null;
        data = new LinkedList<>();

        for (User user : users){

            data.add(opcode);
            data.add(messageOpcode);
            data.add(user.getAge());
            data.add(user.getNumOfPosts());
            data.add((short)user.getFollowers().size());
            data.add((short)user.getFollowList().size());
        }

        if (data.isEmpty()){
            data.add(opcode);
            data.add(messageOpcode);
            short j = 0 ;
            data.add(j);
        }
        short i = ';' ;
        data.add(i);
    }

    public Ack(short messageOpcode, ConcurrentHashMap<String,User> users , User sender) { // logstat

        this.opcode = 10;
        this.messageOpcode = messageOpcode;
        content = null;
        data = new LinkedList<>();


        users.forEach((k, v)->{
            if(!v.getBlockedUsers().containsKey(sender.getUserName()) &&
                    !sender.getBlockedUsers().containsKey(v.getUserName())){

                data.add(opcode);
                data.add(messageOpcode);
                data.add(v.getAge());
                data.add(v.getNumOfPosts());
                data.add((short)v.getFollowers().size());
                data.add((short)v.getFollowList().size());
            }
        });

        if (data.isEmpty()){
            data.add(opcode);
            data.add(messageOpcode);
            short j = 0 ;
            data.add(j);
        }
        short i = ';' ;
        data.add(i);

    }
    @Override
    public String getContent() {
        return content;
    }
    @Override
    public short getOpcode() {
        return opcode;
    }
    @Override
    public Short getMessageOpcode() {
        return messageOpcode;
    }

    @Override
    public List<Short> getData() {
        return data;
    }
}
