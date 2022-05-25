package bgu.spl.net.messages;

import bgu.spl.net.Objects.Database;
import bgu.spl.net.Objects.User;
import bgu.spl.net.api.bidi.ConnectionsImp;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import static bgu.spl.net.messages.FollowUnfollow.type.follow;

public class FollowUnfollow implements ClientToServerMsg{

    enum type {follow , unfollow};

    private short Opcode;
    private String userName;
    private type followUnfollow;

    public FollowUnfollow(String msg){

        this.userName = msg.substring(1,msg.length()-1);
        this.Opcode = 4;

        String str = msg.substring(0,1);
        int numType = Integer.parseInt(str);
        if (numType == 0)
             this.followUnfollow = follow;
        else
            this.followUnfollow = type.unfollow;
    }

    @Override
    public Message process(int connId) {

        ConnectionHandler handler = (ConnectionHandler) ConnectionsImp.getInstance().getIdToHandler().get(connId);
        User user = (User) ConnectionsImp.getInstance().getHandlerToUser().get(handler);

        Database database = Database.getInstance();
        User followedUser = database.getRegisters().get(userName);

        if(user == null || followedUser == null || !user.isConnect()){
           return new Error(Opcode);
        }

        else {

            if (followUnfollow == follow){
                if(user.getFollowList().containsKey(followedUser.getUserName()) ||
                    followedUser.getBlockedUsers().containsKey(user.getUserName()) ||
                        user.getBlockedUsers().containsKey(followedUser.getUserName())){
                    return new Error(Opcode);
                }
                else {
                    user.follow(followedUser);
                    return new Ack(Opcode, userName, 0);
                }

            }
            else {
                if(!user.getFollowList().containsKey(followedUser.getUserName())){
                    return new Error(Opcode);
                }
                else {
                    user.unfollow(followedUser);
                    return new Ack(Opcode, userName, 1);
                }
            }
        }
    }
    @Override
    public short getOpcode() {
        return Opcode;
    }
}
