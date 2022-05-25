package bgu.spl.net.api.bidi;
import bgu.spl.net.messages.Message;
import bgu.spl.net.messages.*;
import bgu.spl.net.srv.bidi.ConnectionHandler;

public class BidiMessagingProtocolImp<Message> implements BidiMessagingProtocol<Message>{

    private boolean shouldTerminate = false;
    private int connectionId;
    private Connections<Message> connections;

    @Override
    public void start(int connectionId, Connections<Message> connections) {

        this.connectionId =connectionId;
        this.connections = connections;

    }

    @Override
    public void process(Message message) {

        ClientToServerMsg clientToServerMsg = (ClientToServerMsg) message;

        bgu.spl.net.messages.Message response =  clientToServerMsg.process(connectionId);
        ServerToClientMsg res = (ServerToClientMsg)response;
        connections.send(connectionId, (Message) response);
        if(res.getOpcode() == 10 && res.getMessageOpcode() == 3)
            setShouldTerminate(true);
    }

    @Override
    public boolean shouldTerminate() {
        return shouldTerminate;
    }

    public void setShouldTerminate(boolean shouldTerminate) {
        this.shouldTerminate = shouldTerminate;
    }
}
