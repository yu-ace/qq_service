package com.example.qq_server;

import com.example.qq_server.model.Session;
import com.example.qq_server.service.ISessionManage;
import com.example.qq_server.service.impl.SessionManage;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

@SpringBootApplication
public class QqServerApplication implements CommandLineRunner {

    public static void main(String[] args) {
        SpringApplication.run(QqServerApplication.class, args);
    }

    public static ISessionManage sessionManage = SessionManage.getInstance();

    @Override
    public void run(String... args) throws Exception {
        Scanner scanner = new Scanner(System.in);
        sessionManage = new SessionManage();
        System.out.println("请输入监听的端口号：");
        int port = scanner.nextInt();

        ServerSocket serverSocket = new ServerSocket(port);
        while(true){
            Socket connect = serverSocket.accept();
            System.out.println("用户连接");
            Session session = new Session(connect);
            sessionManage.addSession(session);
        }
    }
}
