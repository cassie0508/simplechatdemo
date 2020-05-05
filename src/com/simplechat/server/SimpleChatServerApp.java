package com.simplechat.server;

public class SimpleChatServerApp {
    public static void main(String[] args) {
        ClientManager clientManager = new ClientManager();
        SimpleChatService service = new SimpleChatService(clientManager);
        SimpleChatServerView view = new SimpleChatServerView(service);
    }
}
