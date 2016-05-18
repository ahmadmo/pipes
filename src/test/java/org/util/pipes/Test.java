package org.util.pipes;

import org.util.concurrent.pipes.Channel;
import org.util.concurrent.pipes.Pipeline;
import org.util.concurrent.pipes.PipelineFuture;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author ahmad
 */
public class Test {

    public static void main(String[] args) {
        Pipeline pipeline = Pipeline.builder().nextAsync(context -> {
            while (!context.dataBus().contains("terminate")) {
                context.writeToChannel(1, new Date());
                sleep(1, TimeUnit.SECONDS);
            }
        }).next(context -> {
            Channel channel = context.pipe().getChannel();
            for (int i = 0; i < 5; i++) {
                System.out.println(channel.readBlocking());
            }
            context.dataBus().set("terminate", true);
        }).build();

        PipelineFuture pipelineFuture = pipeline.start();

        pipelineFuture.pipeAt(0).whenComplete((v, t) -> System.out.println("Process 0 completed."));
        pipelineFuture.pipeAt(1).whenComplete((v, t) -> System.out.println("Process 1 completed."));

        pipelineFuture.whenComplete((v, t) -> System.out.println("Done.")).join();
    }

    private static void sleep(long timeout, TimeUnit unit) {
        try {
            unit.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
