package bgu.spl.net.messages;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Notification implements ServerToClientMsg{

    private String content;
    private short opcode;
    private Short messageOpcode;
    private List<Short> data;


    public Notification(String messageType,String postingUser,String content){

        this.opcode = 9;
        this.messageOpcode = null ;
        this.content = messageType + postingUser + "\0" + content + "\0"+';';
        data = null;

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
        return null;
    }
}
