package bgu.spl.net.messages;

import bgu.spl.net.Objects.Database;
import bgu.spl.net.Objects.User;
import bgu.spl.net.api.bidi.ConnectionsImp;
import bgu.spl.net.srv.bidi.ConnectionHandler;

public class Block implements ClientToServerMsg{

    private short Opcode;
    private String userName;

    public Block(String msg){

        this.Opcode = 12;
        String[] parts = msg.split("\0");
        this.userName = parts[0];

    }
    @Override
    public Message process(int connId) {

        Database database = Database.getInstance();
        ConnectionHandler handler = (ConnectionHandler) ConnectionsImp.getInstance().getIdToHandler().get(connId);
        User user = (User) ConnectionsImp.getInstance().getHandlerToUser().get(handler);
        User blockedUser = database.getRegisters().get(userName);

        if(user == null || !user.isConnect() || blockedUser == null){
            return new Error(Opcode);
        }
        else {
            user.block(blockedUser);
            return new Ack(Opcode);
        }
    }

    @Override
    public short getOpcode() {
        return Opcode;
    }
}
