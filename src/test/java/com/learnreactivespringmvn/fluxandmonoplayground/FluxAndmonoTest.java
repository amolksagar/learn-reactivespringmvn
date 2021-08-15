package com.learnreactivespringmvn.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

public class FluxAndmonoTest {

    @Test
    public void fluxTest(){
        Flux<String> stringFlux = Flux.just("Spring","Spring Boot","Reactive Spring")
                //this will trigger the error consumer in the subscribe call below
//                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("After Error"))
                .log();
        stringFlux.subscribe(
                //this block gets executed for each element in the Flux
                e->{
                    System.out.println("All Success "+e);
                },
                //this block gets executed whenever there is an Error event from Flux
                e ->{
                    System.err.println("Exception is "+e);
                },
                //this block gets executed whenever there is a complete event from Flux
                () -> {
                    System.out.println("Completed");
                }
            );
    }

    @Test
    public void fluxTestElements_WithoutError(){
        Flux<String> stringFlux = Flux.just("Spring","Spring Boot","Reactive Spring")
                .log();
        //the verifyComplete is the call which will get the values to be emitted from the flux
        StepVerifier.create(stringFlux).expectSubscription().expectNext("Spring").expectNext("Spring Boot").expectNext("Reactive Spring").verifyComplete();
    }

    @Test
    public void fluxTestElements_WithError(){
        Flux<String> stringFlux = Flux.just("Spring","Spring Boot","Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("Spring")
                .expectNext("Spring Boot")
                .expectNext("Reactive Spring")
                //you cannot have the expectError and expectErrorMessage used together
//              .expectError(RuntimeException.class)
                .expectErrorMessage("Exception Occurred")
                //the verify is the call which will get the values to be emitted from the flux
                .verify();
    }

    @Test
    public void fluxTestElements_WithError1(){
        Flux<String> stringFlux = Flux.just("Spring","Spring Boot","Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNext("Spring","Spring Boot","Reactive Spring")
                //you cannot have the expectError and expectErrorMessage used together
//              .expectError(RuntimeException.class)
                .expectErrorMessage("Exception Occurred")
                //the verify is the call which will get the values to be emitted from the flux
                .verify();
    }

    @Test
    public void fluxTestElementsCount_WithError(){
        Flux<String> stringFlux = Flux.just("Spring","Spring Boot","Reactive Spring")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .log();
        StepVerifier.create(stringFlux)
                .expectNextCount(3)
                .expectErrorMessage("Exception Occurred")
                //the verify is the call which will get the values to be emitted from the flux
                .verify();
    }

    @Test
    public void monoTest(){
        Mono<String> mono = Mono.just("Spring");
        StepVerifier.create(mono.log()).expectNext("Spring").verifyComplete();
    }

    @Test
    public void monoTest_Error(){
        StepVerifier.create(Mono.error(new RuntimeException("Exception Occurred")).log())
                .expectError(RuntimeException.class)
//                .expectErrorMessage("Exception Occurred")
                //the verify is the call which will get the values to be emitted from the mono
                .verify();
    }

}
