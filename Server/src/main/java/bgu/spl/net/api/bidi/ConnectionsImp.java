package bgu.spl.net.api.bidi;
import bgu.spl.net.Objects.User;
import bgu.spl.net.messages.ClientToServerMsg;
import bgu.spl.net.messages.Message;
import bgu.spl.net.messages.ServerToClientMsg;
import bgu.spl.net.srv.bidi.ConnectionHandler;
import java.util.concurrent.ConcurrentHashMap;


public class ConnectionsImp<Message> implements Connections<Message>{

    ConcurrentHashMap<Integer, ConnectionHandler<Message>> idToHandler;
    ConcurrentHashMap<ConnectionHandler<Message>, User> handlerToUser;


    public ConnectionsImp(){
        idToHandler = new ConcurrentHashMap<>();
        handlerToUser = new ConcurrentHashMap<>();
    }

    @Override
    public boolean send(int connectionId, Message msg) {

        ConnectionHandler<Message> connectionHandler = idToHandler.get(connectionId);


        if (connectionHandler != null){
            connectionHandler.send(msg);

            ServerToClientMsg m = (ServerToClientMsg)msg;
            if(m.getOpcode()==10 && m.getMessageOpcode() == 3){
                User user = handlerToUser.get(connectionHandler);
                int id = user.getConnId();
                removeConnId(id);
                removeHandler(connectionHandler);
            }
            return true;
        }
        else{
            return false;
        }

    }

    @Override
    public void broadcast(Message msg) {
        idToHandler.forEach((k,v)-> v.send(msg));
    }

    @Override
    public void disconnect(int connectionId) {
        idToHandler.remove(connectionId);
    }

    private static class SingletonHolder{
        private static ConnectionsImp instance = new ConnectionsImp();
    }

    public static ConnectionsImp getInstance(){
        return ConnectionsImp.SingletonHolder.instance;
    }

    public ConcurrentHashMap<Integer, ConnectionHandler<Message>> getIdToHandler() {
        return idToHandler;
    }

    public ConcurrentHashMap<ConnectionHandler<Message>, User> getHandlerToUser() {
        return handlerToUser;
    }

    public void addUser(User user, int connId){

        ConnectionHandler<Message> connectionHandler = idToHandler.get(connId);
        handlerToUser.put(connectionHandler, user);

    }

    public void addHandler(ConnectionHandler<Message> handler, int connId){
        idToHandler.put(connId, handler);
    }

    public void removeConnId(int connId){
        idToHandler.remove(connId);
    }
    public void removeHandler(ConnectionHandler<Message> handler){
        handlerToUser.remove(handler);
    }
}
