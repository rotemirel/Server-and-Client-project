package bgu.spl.net.messages;

public interface ClientToServerMsg extends Message{

    public Message process(int connId);
}
