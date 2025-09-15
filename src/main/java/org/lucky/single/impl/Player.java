package org.lucky.single.impl;

import lombok.Getter;
import org.lucky.multi.impl.Message;

import java.util.Objects;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.BiConsumer;

public class Player {

    @Getter
    private boolean started;

    @Getter
    private final String id;

    private LocalServer localServer;

    private final LinkedBlockingQueue<Message> inbox = new LinkedBlockingQueue<>();

    public Player(String id) {
        this.id = id;
    }

    public void connect(LocalServer localServer) {
        this.localServer = localServer;
    }

    public void sendMessage(Message message) {
        localServer.sendMessage(message);
    }

    public void receiveMessage(Message message) {
        try {
            inbox.put(message);
        } catch (InterruptedException ignored) {
        }
    }

    public void readMessage(BiConsumer<Message, Player> consumer) {
        started = true;
        while (started) {
            try {
                Message m = inbox.take();

                if (Objects.equals(m.getType(), Message.CL)) {
                    if (m.getText().equals("bye")) {
                        stop();
                        break;
                    }
                } else {
                    consumer.accept(m, Player.this);
                }
            } catch (InterruptedException ignore) {

            }
        }
    }

    public void stop() {
        started = false;
    }

}
