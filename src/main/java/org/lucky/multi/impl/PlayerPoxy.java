package org.lucky.multi.impl;

import org.lucky.utils.JsonUtils;
import org.lucky.utils.MessageUtils;

import java.io.*;
import java.net.Socket;
import java.util.Objects;

public class PlayerPoxy {

    private final String id;
    private final RemoteServer remoteServer;
    private final Socket socket;
    private final BufferedReader in;
    private final PrintWriter out;


    public PlayerPoxy(String id, Socket socket, RemoteServer remoteServer) {
        this.id = id;
        this.socket = socket;
        this.remoteServer = remoteServer;

        try {
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(Message message) {
        String receiverId = message.getReceiverId();
        PlayerPoxy player = remoteServer.getPlayer(receiverId);
        player.receiveMessage(message);
    }

    public void receiveMessage(Message message) {
        String msg = JsonUtils.toJson(message);
        out.println(msg);
        out.flush();
    }


    public void listen() {
        while (!socket.isClosed()) {

            try {
                String msg = in.readLine();

                Message message = JsonUtils.parse(msg, Message.class);

                if (Objects.equals(message.getType(), Message.CL) &&
                        Objects.equals(message.getText(), "shutdown")) {
                    remoteServer.stop();
                    break;
                }

                System.out.format("%s -> %s: %s\n", message.getSenderId(), message.getReceiverId(), message.getText());
                sendMessage(message);

            } catch (IOException ignored) {

            }
        }
    }


    public void disConnect() {
        try {
            if (!socket.isClosed()) {
                out.println(MessageUtils.ctrlMsgStr("bye", "server", id));
                out.flush();
                socket.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
