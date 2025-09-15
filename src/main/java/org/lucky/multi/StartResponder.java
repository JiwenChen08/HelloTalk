package org.lucky.multi;

import org.lucky.multi.impl.Message;
import org.lucky.utils.MessageUtils;
import org.lucky.multi.impl.Player;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class StartResponder {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(8);


    public static void main(String[] args) {

        Player responder = new Player("responder");
        responder.connect("localhost", 8888);

        executorService.submit(responder::listen);


        Future<?> read = executorService.submit(() -> {

            AtomicInteger receiveCountInteger = new AtomicInteger(0);
            AtomicInteger sendCountInteger = new AtomicInteger(0);

            responder.readingMessage((message, player) -> {

                receiveCountInteger.incrementAndGet();

                int i = sendCountInteger.incrementAndGet();

                String text = message.getText();
                String receiverId = message.getReceiverId();
                String senderId = message.getSenderId();
                Message res = MessageUtils.normalMsg(text + "_" + i, receiverId, senderId);

                player.sendMessage(res);

            });

        });


        System.out.println("Responder started.");


        try {
            read.get();
        } catch (InterruptedException | ExecutionException e) {
            System.out.println(e.getMessage());
        }

        executorService.shutdown();

        System.out.println("Responder stopped.");
    }

}
