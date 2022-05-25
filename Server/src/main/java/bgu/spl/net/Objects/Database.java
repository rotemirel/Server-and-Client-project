package bgu.spl.net.Objects;

import bgu.spl.net.api.bidi.ConnectionsImp;

import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Database {

    ConcurrentHashMap<String,User> registers;
    ConcurrentHashMap<String,User> connected;
    final String[] filteredWords = {"kill", "abuse", "bomb"};

    private static class SingletonHolder{
        private static Database instance = new Database();
    }

    private Database(){
        registers = new ConcurrentHashMap<>();
        connected = new ConcurrentHashMap<>();
    }

    public static Database getInstance(){
        return SingletonHolder.instance;
    }

    public ConcurrentHashMap<String, User> getConnected() {
        return connected;
    }

    public ConcurrentHashMap<String, User> getRegisters() {
        return registers;
    }

    public void register(User newRegistered){
        registers.put(newRegistered.getUserName(),newRegistered);
    }

    public void unregister(User unregistered){
        registers.remove(unregistered.getUserName());
    }

    public void login(User newConnected){
        connected.put(newConnected.getUserName(),newConnected);
        newConnected.setConnect(true);
    }

    public void logout(User disconnect){

        connected.remove(disconnect.getUserName());
        disconnect.setConnect(false);


    }

    public String[] getFilteredWords() {
        return filteredWords;
    }
}
