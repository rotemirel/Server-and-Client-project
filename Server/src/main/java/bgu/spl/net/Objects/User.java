package bgu.spl.net.Objects;

import bgu.spl.net.messages.Message;
import bgu.spl.net.messages.Notification;


import java.time.LocalDate;
import java.time.Period;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;

public class User {

    private String userName;
    private String password;
    private String Birthday;
    private short age;
    private int connId;

    private ConcurrentHashMap<String,User> followList;
    private ConcurrentHashMap<String,User> followers;
    private ConcurrentHashMap<String,User> blockedUsers;
    private ConcurrentLinkedQueue<Notification> unreadNotifications ;
    private short numOfPosts;
    private short numOfPM;
    private boolean connect; // check if needed

    public User(String userName, String password, String Birthday, int connId){

        this.userName = userName;
        this.password = password;
        this.Birthday = Birthday;
        this.age = calculateAge();
        this.connId = connId;
        this.followList = new ConcurrentHashMap<>();
        this.followers = new ConcurrentHashMap<>();
        this.blockedUsers =new ConcurrentHashMap<>();
        this.numOfPosts = 0;
        this.numOfPM = 0;
        this.connect = false;
        unreadNotifications = new ConcurrentLinkedQueue<>(); // ThreadSafe
    }

    public ConcurrentHashMap<String, User> getBlockedUsers() {
        return blockedUsers;
    }

    public ConcurrentHashMap<String, User> getFollowList() {
        return followList;
    }

    public ConcurrentHashMap<String, User> getFollowers() {
        return followers;
    }

    public short getAge() {
        return age;
    }

    public String getBirthday() {
        return Birthday;
    }

    public String getPassword() {
        return password;
    }

    public String getUserName() {
        return userName;
    }

    public short getNumOfPM() {
        return numOfPM;
    }

    public ConcurrentLinkedQueue<Notification> getUnreadNotifications() {
        return unreadNotifications;
    }

    public boolean isConnect() {
        return connect;
    }

    public short getNumOfPosts() {
        return numOfPosts;
    }

    public void follow(User user){
        followList.put(user.getUserName(),user);
        user.addFollower(this);
    }
    public void unfollow(User user){
        followList.remove(user.userName);
        user.removeFollower(this);
    }
    private void removeFollower(User user){
        followers.remove(user.getUserName());
    }

    private void addFollower(User user){
        followers.put(user.getUserName(),user);
    }

    public void block(User user){
       blockedUsers.put(user.getUserName(),user);
       unfollow(user);
       user.unfollow(this);
    }

    public void addNotification(Notification n){
        unreadNotifications.add(n);
    }

    public void increaseNumOfPosts(){
        numOfPosts++;
    }

    public void increaseNumOfPM(){
        numOfPM++;
    }

    public void setConnect(boolean connectStatus){
        connect = connectStatus;
    }

    public int getConnId() {
        return connId;
    }

    public void setConnId(int connId) {
        this.connId = connId;
    }

    private short calculateAge(){

        String[] parts = Birthday.split("-");
        int day = Integer.parseInt(parts[0]);
        int month = Integer.parseInt(parts[1]);
        int year = Integer.parseInt(parts[2]);
        LocalDate birthdayLocalDate = LocalDate.of(year,month,day);
        Period period = Period.between(birthdayLocalDate,LocalDate.now());
        return (short)period.getYears();

    }
}
