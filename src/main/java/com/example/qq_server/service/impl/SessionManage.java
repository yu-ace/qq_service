package com.example.qq_server.service.impl;

import com.example.qq_server.model.Session;
import com.example.qq_server.service.ISessionManage;

import java.util.ArrayList;
import java.util.List;

public class SessionManage implements ISessionManage {

    private List<Session> sessionList = new ArrayList<>();

    public static SessionManage sessionManage = new SessionManage();
    public SessionManage(){
    }
    public static SessionManage getInstance(){
        return sessionManage;
    }

    @Override
    public List<String> getOnlineList() {
        List<String> online = new ArrayList<>();
        for(Session session:sessionList){
            online.add(session.getNickName());
        }
        return online;
    }

    @Override
    public void removeSession(Session session) {
        sessionList.remove(session);
    }

    @Override
    public void addSession(Session session) {
        sessionList.add(session);
    }

    @Override
    public Session getSessionByNickName(String nickName) {
        if(nickName == null && nickName.length() == 0){
            return null;
        }
        for(Session session:sessionList){
            if(session.getNickName().equals(nickName)){
                return session;
            }
        }
        return null;
    }
}
