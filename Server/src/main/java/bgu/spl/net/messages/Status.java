package bgu.spl.net.messages;

import bgu.spl.net.Objects.Database;
import bgu.spl.net.Objects.User;
import bgu.spl.net.api.bidi.ConnectionsImp;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.List;
import java.util.Vector;
import java.util.regex.Pattern;

public class Status implements ClientToServerMsg{

    private short Opcode;
    private List<User> users;
    private String[] usersNames;
    private boolean legalUsersList;

    public Status(String msg) {
        legalUsersList = true;
        users = new Vector<>(); // thread safe
        Opcode = 8;

        String[] parts = msg.split("\0");
        String part = parts[0];
        usersNames = part.split(Pattern.quote("|"));

        Database database = Database.getInstance();
    }
    @Override
    public Message process(int connId) {

        Database database = Database.getInstance();
        ConnectionsImp connectionsImp = ConnectionsImp.getInstance();
        ConnectionHandler handler = (ConnectionHandler) connectionsImp.getIdToHandler().get(connId);
        User sender = (User) connectionsImp.getHandlerToUser().get(handler);

        if (sender == null || !sender.isConnect() ){
            return new Error(Opcode);
        }

        for (int i = 0; i < usersNames.length && legalUsersList; i++) {

            User user = database.getRegisters().get(usersNames[i]);
            if (user == null || user.getBlockedUsers().containsKey(sender.getUserName()) ||
                    sender.getBlockedUsers().containsKey(user.getUserName()))
                legalUsersList = false;
            else
                 users.add(user);

        }

        if(legalUsersList)
            return new Ack(Opcode, users, sender);
        else
            return new Error(Opcode);

    }
    @Override
    public short getOpcode() {
        return Opcode;
    }
}
