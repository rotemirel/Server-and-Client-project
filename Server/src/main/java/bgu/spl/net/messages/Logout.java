package bgu.spl.net.messages;

import bgu.spl.net.Objects.Database;
import bgu.spl.net.Objects.User;
import bgu.spl.net.api.bidi.ConnectionsImp;
import bgu.spl.net.srv.bidi.ConnectionHandler;

public class Logout implements ClientToServerMsg{

    private short Opcode;

    public Logout(){
        Opcode = 3;
    }

    @Override
    public Message process(int connId) {

        ConnectionsImp<Message> connectionsImp = ConnectionsImp.getInstance();
        Database database = Database.getInstance();
        ConnectionHandler handler = (ConnectionHandler) connectionsImp.getIdToHandler().get(connId);
        User user = (User) connectionsImp.getHandlerToUser().get(handler);

        if(user==null || !user.isConnect()){
            return new Error(Opcode);
        }
        else {
            database.logout(user);
            return new Ack(Opcode);
        }
    }
    @Override
    public short getOpcode() {
        return Opcode;
    }

}
