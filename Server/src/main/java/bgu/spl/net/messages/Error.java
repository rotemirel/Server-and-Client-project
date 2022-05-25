package bgu.spl.net.messages;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class Error implements ServerToClientMsg{

    private short opcode;
    private Short messageOpcode;
    private String content;
    private List<Short> data;

    public Error(Short messageOpcode){

        data = new LinkedList<>();
        this.opcode = 11;
        this.messageOpcode = messageOpcode;
        short i = ';' ;
        content = null;

        data.add(opcode);
        data.add(messageOpcode);
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
    public List<Short> getData(){
        return data;
    }
}
