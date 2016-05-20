package org.util.pipes;

import org.util.concurrent.pipes.CompletablePipe;
import org.util.concurrent.pipes.CompletablePipeline;
import org.util.concurrent.pipes.Pipeline;
import org.util.concurrent.pipes.PipelineEngine;

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
                        sleep(1, TimeUnit.SECONDS);
                        c.writeToChannel("consumer", new Date());
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

        PipelineEngine pipelineEngine = new PipelineEngine(pipeline);

        CompletablePipeline completablePipeline = pipelineEngine.start("Test Context");

        for (final CompletablePipe completablePipe : completablePipeline.completablePipes()) {
            completablePipe.whenComplete((v, t) -> System.out.println(completablePipe.pipe().name() + " finished its process."));
        }

        completablePipeline.whenComplete((v, t) -> System.out.println("Done.")).join();
    }

    private static void sleep(long timeout, TimeUnit unit) {
        try {
            unit.sleep(timeout);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

}
