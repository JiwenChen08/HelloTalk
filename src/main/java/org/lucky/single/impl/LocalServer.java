package org.lucky.single.impl;

import org.lucky.multi.impl.Message;

import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class LocalServer {

    public final Map<String, Player> playerMap = new ConcurrentHashMap<>();

    public ExecutorService executorService = Executors.newFixedThreadPool(8);

    public void sendMessage(Message message) {


        executorService.submit(() -> {
            String receiverId = message.getReceiverId();
            Player player = playerMap.get(receiverId);

            if (Objects.equals(message.getType(), Message.MS)){
                System.out.format("%s -> %s: %s\n", message.getSenderId(), message.getReceiverId(), message.getText());
            }
            player.receiveMessage(message);

        });

    }

    public void register(Player player) {
        String id = player.getId();
        playerMap.put(id, player);
        player.connect(this);
    }

    public void stop() {
        executorService.shutdown();
    }

}
