package org.lucky.multi.impl;

import org.lucky.utils.JsonUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Logger;

public class RemoteServer {

    private final ExecutorService executorService = Executors.newFixedThreadPool(10);

    private final Integer port;

    private ServerSocket serverSocket;

    private final Map<String, PlayerPoxy> playerMap = new ConcurrentHashMap<>();


    public RemoteServer(Integer port) {
        this.port = port;
    }

    public PlayerPoxy getPlayer(String id) {
        return playerMap.get(id);
    }

    public void removePlayer(String id) {
        playerMap.remove(id);
    }

    public void start() {

        try {
            serverSocket = new ServerSocket(port);

            while (!serverSocket.isClosed()) {

                Socket socket = serverSocket.accept();

                InputStream inputStream = socket.getInputStream();
                BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
                String msg = reader.readLine();
                Message message = JsonUtils.parse(msg, Message.class);
                String id = message.getSenderId();

                PlayerPoxy playerPoxy = new PlayerPoxy(id, socket, this);
                playerMap.put(id, playerPoxy);

                executorService.submit(playerPoxy::listen);

                System.out.format("Player %s connected.\n", message.getSenderId());
            }

        } catch (IOException ignored) {

        }
    }


    public void stop() {
        try {
            for (PlayerPoxy playerPoxy : playerMap.values()) {
                playerPoxy.disConnect();
            }

            serverSocket.close();
            executorService.shutdown();
        } catch (IOException ignored) {

        }
    }
}
