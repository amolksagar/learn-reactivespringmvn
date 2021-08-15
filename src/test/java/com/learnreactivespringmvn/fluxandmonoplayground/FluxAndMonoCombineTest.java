package com.learnreactivespringmvn.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoCombineTest {

    @Test
    public void combineUsingMerge(){
        Flux<String> flux1 = Flux.just("A","B","C");
        Flux<String> flux2 = Flux.just("D","E","F");
        Flux<String> fluxMerged = Flux.merge(flux1,flux2).log();

        //StepVerifier is the subscriber here
        StepVerifier.create(fluxMerged)
                .expectSubscription()
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
        System.out.println("Subscription trigerred !!! "+Thread.currentThread().getName());
    }

    @Test
    public void combineUsingMerge_withDelay(){
        Flux<String> flux1 = Flux.just("A","B","C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D","E","F").delayElements(Duration.ofSeconds(1));
        Flux<String> fluxMerged = Flux.merge(flux1,flux2).log();
        //StepVerifier is the subscriber here
        StepVerifier.create(fluxMerged)
                .expectSubscription()
                //output wont be in expected order
                //.expectNext("A","B","C","D","E","F")
                .expectNextCount(6)
                .verifyComplete();
    }

    @Test
    public void combineUsingConcat(){
        Flux<String> flux1 = Flux.just("A","B","C");
        Flux<String> flux2 = Flux.just("D","E","F");
        //bcoz of the concat method used below, flux2 does not emit value to fluxMerged until flux1 is completed
        Flux<String> fluxMerged = Flux.concat(flux1,flux2).log();
        //StepVerifier is the subscriber here
        StepVerifier.create(fluxMerged)
                .expectSubscription()
                //output WILL BE in expected order
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    public void combineUsingConcat_WithDelay(){
        Flux<String> flux1 = Flux.just("A","B","C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D","E","F").delayElements(Duration.ofSeconds(1));
        Flux<String> fluxMerged = Flux.concat(flux1,flux2).log();
        //StepVerifier is the subscriber here
        StepVerifier.create(fluxMerged)
                .expectSubscription()
                //output WILL BE in expected order
                .expectNext("A","B","C","D","E","F")
                .verifyComplete();
    }

    @Test
    public void combineUsingZip(){
        Flux<String> flux1 = Flux.just("A","B","C").delayElements(Duration.ofSeconds(1));
        Flux<String> flux2 = Flux.just("D","E","F").delayElements(Duration.ofSeconds(1));
        //use this approach of t1 and t2 if we want to merge the elements in a particular order
        Flux<String> fluxMerged = Flux.zip(flux1,flux2,(t1,t2) -> {
            return t1.concat(t2);
        }).log();
        //StepVerifier is the subscriber here
        StepVerifier.create(fluxMerged)
                .expectSubscription()
                //output WILL BE in expected order
                .expectNext("AD","BE","CF")
                .verifyComplete();
    }
}
