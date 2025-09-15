package org.lucky.multi;

import org.lucky.multi.impl.Message;
import org.lucky.utils.MessageUtils;
import org.lucky.multi.impl.Player;
import org.lucky.utils.SleepUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class StartInitiator {

    private static final ExecutorService executorService = Executors.newFixedThreadPool(4);


    public static void main(String[] args) {


        Player initiator = new Player("initiator");

        initiator.connect("localhost", 8888);

        executorService.submit(initiator::listen);



        AtomicInteger receiveCountInteger = new AtomicInteger(0);
        AtomicInteger sendCountInteger = new AtomicInteger(0);

        Future<?> read = executorService.submit(() -> {

            initiator.readingMessage((message, player) -> {


                int receiveCount = receiveCountInteger.incrementAndGet();
                if (receiveCount >= 10) {
                    player.sendMessage(MessageUtils.ctrlShutdown(player.getId()));
                    player.disConnect();
                    return;
                }

                String text = message.getText();

                int sendCount = sendCountInteger.incrementAndGet();
                Message res = MessageUtils.normalMsg(text + "_" + sendCount,
                        message.getReceiverId(),
                        message.getSenderId());

                player.sendMessage(res);
                SleepUtils.sleep(1000);
            });

        });

        System.out.println("Initiator started.");

        int i = sendCountInteger.incrementAndGet();
        Message res = MessageUtils.normalMsg("hello_" + i, "initiator", "responder");
        initiator.sendMessage(res);

        try {
            read.get();
        } catch (InterruptedException | ExecutionException ignored) {
        }
        executorService.shutdown();

        System.out.println("Initiator stopped.");
    }
}
