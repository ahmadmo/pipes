package org.util.pipes;

import org.util.concurrent.pipes.Pipeline;
import org.util.concurrent.pipes.PipelineContext;
import org.util.concurrent.pipes.PipelineFuture;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @author ahmad
 */
public class Test {

    public static void main(String[] args) {
        Pipeline pipeline = Pipeline.builder()
                .nextAsync(c -> {
                    while (!c.dataBus().contains("terminate")) {
                        c.writeToChannel(1, new Date());
                        sleep(1, TimeUnit.SECONDS);
                    }
                    c.eventBus().publish("termination", new Date());
                })
                .next(c -> {
                    for (int i = 0; i < 5; i++) {
                        System.out.println(c.channel().readBlocking());
                    }
                    c.dataBus().set("terminate", true);
                })
                .nextAsync(c -> c.eventBus().register("termination", event -> System.out.println("Termination Date : " + event)))
                .build();

        PipelineFuture pipelineFuture = pipeline.start(PipelineContext.named("Test Context"));

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
