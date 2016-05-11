package org.util.pipes;

import org.util.concurrent.pipes.Channel;
import org.util.concurrent.pipes.Pipeline;

import java.util.Date;

/**
 * @author ahmad
 */
public class Test {

    public static void main(String[] args) {
        Pipeline.builder().nextAsync(context -> {
            while (!context.dataBus().contains("terminate")) {
                context.writeToChannel(1, new Date());
                sleep(1000);
            }
        }).next(context -> {
            Channel channel = context.pipe().getChannel();
            for (int i = 0; i < 5; i++) {
                System.out.println(channel.readBlocking());
            }
            context.dataBus().set("terminate", true);
        }).build().start().whenComplete((v, t) -> System.out.println("Done.")).join();
    }

    private static void sleep(long timeout) {
        try {
            Thread.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
