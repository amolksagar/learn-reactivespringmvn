package com.learnreactivespringmvn.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoWithTimeTest {

    @Test
    public void infiniteSequence() throws InterruptedException {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200)).log(); // starts from 0 --> ...N
        //the test case is running in the main thread but the actual emission of elements is
        //happening in the parallel thread
        infiniteFlux.subscribe((element) -> System.out.println("Value is : "+element));
        Thread.sleep(3000);
    }

    @Test
    public void infiniteSequenceTest(){
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .take(3)
                .log();
        //StepVerifier is the subscriber here
        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L,1L,2L)
                .verifyComplete();
    }

    @Test
    public void infiniteSequenceMap_withDelay(){
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .delayElements(Duration.ofSeconds(1))
                .map(l-> l.intValue())
                .take(3)
                .log();
        //StepVerifier is the subscriber here
        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0,1,2)
                .verifyComplete();
    }
}
