package org.lucky.single;

import org.lucky.multi.impl.Message;
import org.lucky.single.impl.LocalServer;
import org.lucky.single.impl.Player;
import org.lucky.utils.MessageUtils;
import org.lucky.utils.SleepUtils;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class StartSingle {

    private final static ExecutorService executorService = Executors.newFixedThreadPool(8);

    public static void main(String[] args) throws ExecutionException, InterruptedException {

        System.out.println("Talks start");

        LocalServer server = new LocalServer();


        Player initiator = new Player("initiator");
        server.register(initiator);


        Player responder = new Player("responder");
        server.register(responder);


        AtomicInteger round = new AtomicInteger(0);
        Future<?> future1 = executorService.submit(() -> {

            initiator.readMessage((message, player) -> {
                int i = round.incrementAndGet();

                if (i <= 10) {

                    Message res = MessageUtils.normalMsg(message.getText() + "_" + i,
                            message.getReceiverId(),
                            message.getSenderId());

                    player.sendMessage(res);
                } else {

                    Message bye = MessageUtils.ctrlMsg("bye",
                            message.getReceiverId(),
                            message.getSenderId());
                    player.sendMessage(bye);
                    player.stop();
                }

                SleepUtils.sleep(1000);
            });
        });


        AtomicInteger resRound = new AtomicInteger(0);
        Future<?> future2 = executorService.submit(() -> {

            responder.readMessage((message, player) -> {
                int i = resRound.incrementAndGet();
                Message res = MessageUtils.normalMsg(message.getText() + "_" + i,
                        message.getReceiverId(),
                        message.getSenderId());

                player.sendMessage(res);

            });

        });

        int i = round.incrementAndGet();
        Message message = MessageUtils.normalMsg("hello_" + i, "initiator", "responder");
        initiator.sendMessage(message);


        future1.get();
        future2.get();

        server.stop();
        executorService.shutdown();

        System.out.println("Talks stopped");

    }
}
