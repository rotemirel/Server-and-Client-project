package bgu.spl.net.messages;


import bgu.spl.net.Objects.Database;
import bgu.spl.net.Objects.User;
import bgu.spl.net.api.bidi.ConnectionsImp;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.ArrayList;
import java.util.List;

public class LoggedInStatus implements ClientToServerMsg{

    private short Opcode ;

    public LoggedInStatus(){

        this.Opcode = 7;
    }

    @Override
    public Message process(int connId) {

        Database database = Database.getInstance();
        ConnectionsImp connectionsImp = ConnectionsImp.getInstance();
        ConnectionHandler handler = (ConnectionHandler) ConnectionsImp.getInstance().getIdToHandler().get(connId);
        User user = (User) ConnectionsImp.getInstance().getHandlerToUser().get(handler);

        if (user == null || !user.isConnect() ){
            return new Error(Opcode);
        }
        else {
            return new Ack(Opcode, database.getConnected(), user);
        }
    }
    @Override
    public short getOpcode() {
        return Opcode;
    }
}
