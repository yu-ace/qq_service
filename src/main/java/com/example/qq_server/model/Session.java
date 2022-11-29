package com.example.qq_server.model;

import com.alibaba.fastjson2.JSON;
import com.example.qq_server.QqServerApplication;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.List;

public class Session implements Runnable , Closeable {
    String nickName;
    Socket socket;
    Thread thread;
    boolean isRunning = true;
    public String getNickName() {
        return nickName;
    }
    public void setNickName(String nickName) {
        this.nickName = nickName;
    }
    public Socket getSocket() {
        return socket;
    }
    public void setSocket(Socket socket) {
        this.socket = socket;
    }
    public Thread getThread() {
        return thread;
    }
    public void setThread(Thread thread) {
        this.thread = thread;
    }
    public Session(Socket socket) throws IOException {
        this.socket = socket;
        thread = new Thread(this);
        thread.start();
        Message message = new Message("SYSTEM","xxx","欢迎使用QQ");
        socket.getOutputStream().write(JSON.toJSONString(message).getBytes());
    }

    @Override
    public void close() throws IOException {
        System.out.println("用户退出，关闭线程，释放资源");
        QqServerApplication.sessionManage.removeSession(this);
        thread.interrupt();
        socket.close();
    }

    @Override
    public void run() {
        while(isRunning){
            try {
                byte[] buffer = new byte[1024];
                InputStream in = socket.getInputStream();
                in.read(buffer);
                String message = new String(buffer);
                Message message1 = JSON.parseObject(message.trim(), Message.class);
                if("SYSTEM".equals(message1.getReceiverName())){
                    if("LIST".equals(message1.message)){
                        List<String> onlineList = QqServerApplication.sessionManage.getOnlineList();
                        String join = String.join("\n",onlineList);
                        Message message2 = new Message("SYSTEM",message1.getNickName(),join);
                        socket.getOutputStream().write(JSON.toJSONString(message2).getBytes());
                    }else if("LOGIN".equals(message1.getMessage())){
                        this.setNickName(message1.getNickName());
                    }else if("LOGOUT".equals(message1.getMessage())){
                        isRunning = false;
                        System.out.printf("用户%s退出\n",message1.getNickName());
                        Message message2 = new Message("SYSTEM",message1.getNickName(),"BYE");
                        socket.getOutputStream().write(JSON.toJSONString(message2).getBytes());
                        this.close();
                    }
                }else{
                    String receiverName = message1.getReceiverName();
                    Session sessionByNickName = QqServerApplication.sessionManage.getSessionByNickName(receiverName);
                    if(sessionByNickName == null){
                        System.out.printf("来自%s发送给%s的消息：%s，%s",message1.getNickName(),
                                receiverName,message1.getMessage(),"单用户不在线");
                        Message message2 = new Message("SYSTEM",
                                message1.getNickName(),"用户不在线，发送失败");
                        socket.getOutputStream().write(JSON.toJSONString(message2).getBytes());
                    }else{
                        Socket receiverSocket = sessionByNickName.getSocket();
                        receiverSocket.getOutputStream().write(JSON.toJSONString(message1).getBytes());
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
