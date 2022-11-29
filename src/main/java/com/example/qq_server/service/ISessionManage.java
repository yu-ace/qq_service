package com.example.qq_server.service;

import com.example.qq_server.model.Session;

import java.util.List;

public interface ISessionManage {
    List<String> getOnlineList();
    void removeSession(Session session);
    void addSession(Session session);
    Session getSessionByNickName(String nickName);
}
