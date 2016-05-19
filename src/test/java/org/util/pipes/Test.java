package org.util.pipes;

import org.util.concurrent.pipes.PipeFuture;
import org.util.concurrent.pipes.Pipeline;
import org.util.concurrent.pipes.PipelineFuture;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author ahmad
 */
public class Test {

    public static void main(String[] args) {
        Pipeline pipeline = Pipeline.builder()
                .nextAsync("producer", c -> {
                    while (!c.dataBus().contains("terminate")) {
                        c.writeToChannel("consumer", new Date());
                        sleep(1, TimeUnit.SECONDS);
                    }
                    c.eventBus().publish("termination", new Date());
                })
                .next(c -> c.eventBus().register("termination", event -> System.out.println("Termination Date : " + event)))
                .next("consumer", c -> {
                    for (int i = 0; i < 5; i++) {
                        System.out.println(c.channel().readBlocking());
                    }
                    c.dataBus().set("terminate", true);
                })
                .build();

        PipelineFuture pipelineFuture = pipeline.start("Test Context");

        for (final PipeFuture pipeFuture : pipelineFuture.pipeFutures()) {
            pipeFuture.whenComplete((v, t) -> System.err.println(pipeFuture.pipe().name() + " finished its process."));
        }

        pipelineFuture.whenComplete((v, t) -> System.err.println("Done.")).join();
    }

    private static void sleep(long timeout, TimeUnit unit) {
        try {
            unit.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
