package bgu.spl.net.messages;

import bgu.spl.net.Objects.Database;
import bgu.spl.net.Objects.User;
import bgu.spl.net.api.bidi.ConnectionsImp;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.concurrent.ConcurrentHashMap;

public class Register implements ClientToServerMsg{
    private short Opcode;
    private String userName;
    private String password;
    private String Birthday;

    public Register(String msg){

        String[] parts = msg.split("\0");
        this.userName = parts[0];
        this.password = parts[1];
        this.Birthday = parts[2];
        this.Opcode = 1;
    }
    @Override
    public Message process(int connId) {
        User user = new User(userName, password, Birthday, connId);
        Database database = Database.getInstance();
        if (database.getRegisters().get(userName) == null){

            database.register(user);

            return new Ack(Opcode);

        }
        else { // if user already registered
            return new Error(Opcode);
        }

    }
    @Override
    public short getOpcode() {
        return Opcode;
    }
}
