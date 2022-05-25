package bgu.spl.net.messages;

import bgu.spl.net.Objects.Database;
import bgu.spl.net.Objects.User;
import bgu.spl.net.api.bidi.ConnectionsImp;
import bgu.spl.net.srv.bidi.ConnectionHandler;

import java.util.regex.Pattern;

public class PM implements ClientToServerMsg{

    private short Opcode;
    private String userName;
    private String content;
    private String sendingDateAndTime;

    public PM(String msg){

        String[] parts = msg.split(Pattern.quote("\0"));

        this.userName = parts[0];
        this.sendingDateAndTime = parts[2];
        this.Opcode = 6;

        String content = parts[1];
        filter(content);
        this.content += '\0' + sendingDateAndTime;
    }

    @Override
    public Message process(int connId) {
        ConnectionsImp connectionsImp = ConnectionsImp.getInstance();
        Database database = Database.getInstance();
        ConnectionHandler handler = (ConnectionHandler) connectionsImp.getIdToHandler().get(connId);

        User user = (User) ConnectionsImp.getInstance().getHandlerToUser().get(handler);
        User destUser = database.getRegisters().get(userName);

        if (user == null || destUser == null || !user.isConnect() || !user.getFollowList().containsKey(destUser.getUserName())
                || destUser.getBlockedUsers().containsKey(user.getUserName()) ||  user.getBlockedUsers().containsKey(destUser.getUserName())){

            return new Error(Opcode);
        }
        else {
            Notification notification = new Notification("0",user.getUserName(),content);
            if(destUser.isConnect())
                connectionsImp.send(destUser.getConnId(),notification);
            else
                destUser.addNotification(notification);
            user.increaseNumOfPM();
            return new Ack(Opcode);
        }
    }

    private void filter(String content){

        Database database = Database.getInstance();
        String[] filteredWords = database.getFilteredWords();
        String[] parts = content.split(Pattern.quote(" "));
        boolean flag;
        this.content = "";
        for(String part: parts){
             flag = false;
            for(int i = 0 ; i<filteredWords.length && !flag ; i++){

                if(part.equals(filteredWords[i]) || part.equals(filteredWords[i]+".") ||
                        part.equals(filteredWords[i]+"?") || part.equals(filteredWords[i]+"!") || part.equals(filteredWords[i]+",")){

                    flag = true;
                    this.content += "<filtered> " ;
                }
            }
            if (!flag)
                this.content += part+" ";

        }
    }
    @Override
    public short getOpcode() {
        return Opcode;
    }
}
