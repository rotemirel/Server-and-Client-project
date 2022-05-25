package bgu.spl.net.messages;

import bgu.spl.net.Objects.Database;
import bgu.spl.net.Objects.User;
import bgu.spl.net.api.bidi.ConnectionsImp;
import bgu.spl.net.srv.bidi.ConnectionHandler;


public class Login implements ClientToServerMsg{

    private short Opcode;
    private String userName;
    private String password;
    private int captcha;


    public Login(String msg){
        String[] parts = msg.split("\0");
        this.userName = parts[0];
        this.password = parts[1];
        this.captcha = Integer.parseInt(parts[2]);
        this.Opcode = 2;
    }

    @Override
    public Message process(int connId) {

        Database database = Database.getInstance();
        User currUser = database.getRegisters().get(userName);
        ConnectionsImp<Message> connectionsImp = ConnectionsImp.getInstance();
        ConnectionHandler handler = (ConnectionHandler) connectionsImp.getIdToHandler().get(connId);
        User user = null;
        if(handler != null)
             user = connectionsImp.getHandlerToUser().get(handler);

        if (captcha == 0 || currUser == null || !currUser.getPassword().equals(password) || currUser.isConnect() ||
                (handler!=null && user!=null && !user.getUserName().equals(userName)) ){
            return new Error(Opcode);
        }
        else {
            currUser.setConnId(connId);
            database.login(currUser);
            ConnectionsImp.getInstance().addUser(currUser,connId);

            Notification n;

            while (!currUser.getUnreadNotifications().isEmpty()){
                n = currUser.getUnreadNotifications().poll();
                ConnectionsImp.getInstance().send(connId, n);
            }
            return new Ack(Opcode);
        }

    }
    @Override
    public short getOpcode() {
        return Opcode;
    }
}
