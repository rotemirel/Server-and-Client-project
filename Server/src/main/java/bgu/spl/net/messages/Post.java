package bgu.spl.net.messages;

import bgu.spl.net.Objects.User;
import  bgu.spl.net.Objects.Database;
import bgu.spl.net.api.bidi.ConnectionsImp;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;

public class Post implements ClientToServerMsg{

    private short Opcode;
    private String content;
    private List<User> taggedUsers;

    public Post(String msg){

        this.Opcode = 5;
        this.taggedUsers = new Vector<>(); //Thread safe

        String[] parts = msg.split("\0");
        String content = parts[0];

        String[] parts2 = content.split(" ");
        Database database = Database.getInstance();
        String[] filteredWords = database.getFilteredWords();

        for (String str : parts2){
            if(str.charAt(0) == '@'){
                str = str.substring(1);
                User user = database.getRegisters().get(str);
                if(user != null)// which users , one user can't tag ?
                    taggedUsers.add(user);
            }
        }

        this.content = content;

    }
    @Override
    public Message process(int connId) {

        ConnectionsImp connectionsImp = ConnectionsImp.getInstance();
        ConnectionHandler handler = (ConnectionHandler) ConnectionsImp.getInstance().getIdToHandler().get(connId);
        User user = (User) ConnectionsImp.getInstance().getHandlerToUser().get(handler);

        if (user == null || !user.isConnect() || content == null){
            return new Error(Opcode);
        }
        else {
            Notification notification = new Notification("1", user.getUserName(), content);
            ConcurrentHashMap<String,User> myFollowers = user.getFollowers();

            myFollowers.forEach((k,v)-> {
                if (v.isConnect())
                    connectionsImp.send(v.getConnId(),notification);
                else
                    v.addNotification(notification);
            } );

            for (User destUser : taggedUsers){
                if(!myFollowers.containsKey(destUser.getUserName()) &&
                    !destUser.getBlockedUsers().containsKey(user.getUserName())  &&
                    !user.getBlockedUsers().containsKey(destUser.getUserName())){

                    if (destUser.isConnect())
                        connectionsImp.send(destUser.getConnId(),notification);
                    else
                        destUser.addNotification(notification);
                }
            }
            user.increaseNumOfPosts();
            return new Ack(Opcode);
        }
    }
    @Override
    public short getOpcode() {
        return Opcode;
    }
}
