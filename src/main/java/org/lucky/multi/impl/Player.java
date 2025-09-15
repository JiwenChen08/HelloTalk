package org.lucky.multi.impl;

import lombok.Getter;
import lombok.Setter;
import org.lucky.utils.JsonUtils;
import org.lucky.utils.MessageUtils;

import java.io.*;
import java.net.Socket;
import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;

public class Player {

    @Getter
    @Setter
    private String id;

    public Socket socket;
    private BufferedReader in;
    private PrintWriter out;

    private final LinkedBlockingQueue<Message> inbox = new LinkedBlockingQueue<>();


    public Player(String id) {
        this.id = id;


    }


    public void connect(String address, int port) {
        try {
            socket = new Socket(address, port);
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream()));

            out.println(MessageUtils.ctrlMsgStr("", id, "server"));
            out.flush();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }

    public void listen() {
        try {
            while (!socket.isClosed()) {

                String msg = in.readLine();
                inbox.put(JsonUtils.parse(msg, Message.class));

            }
        } catch (IOException | InterruptedException ignored) {

        }
    }


    public void disConnect() {
        try {
            if (!socket.isClosed()) {
                socket.close();
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void sendMessage(Message message) {
        if (socket.isClosed()) {
            return;
        }

        if ((Objects.equals(message.getType(), Message.MS))) {
            System.out.format("send    ->: %s\n", message.getText());
        }

        String msg = JsonUtils.toJson(message);
        out.println(msg);
        out.flush();
    }


    public void readingMessage(BiConsumer<Message, Player> consumer) {
        while (!socket.isClosed()) {
            try {
                Message message = inbox.take();

                if (Objects.equals(message.getType(), Message.CL)) {
                    if (Objects.equals(message.getText(), "bye")) {
                        disConnect();
                    }
                } else if (Objects.equals(message.getType(), Message.MS)) {

                    System.out.format("receive <-: %s\n", message.getText());
                    consumer.accept(message, this);
                }

            } catch (InterruptedException ignored) {

            }
        }
    }

}
