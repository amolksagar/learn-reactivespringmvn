package com.learnreactivespringmvn.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;
import reactor.util.retry.Retry;

import java.time.Duration;

public class FluxAndMonoErrorTest {

    @Test
    public void fluxErrorHandling(){
        Flux<String> stringFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorResume((e) -> {
                   System.out.println("Exception is "+e);
                   return Flux.just("default","default1");
                }).log();
        //StepVerifier is the subscriber here
        StepVerifier.create(stringFlux)
                .expectSubscription()
                //Here we dont have "D"
                .expectNext("A","B","C")
                //below code will be executed when you DONT have the onErrorResume code above
//                .expectError(RuntimeException.class)
//                .verify()
                .expectNext("default","default1")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandling_onErrorReturn(){
        Flux<String> stringFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorReturn("default").log();
        //StepVerifier is the subscriber here
        StepVerifier.create(stringFlux)
                .expectSubscription()
                //Here we dont have "D"
                .expectNext("A","B","C")
                //below code will be executed when you DONT have the onErrorResume code above
//               .expectError(RuntimeException.class)
//               .verify()
                .expectNext("default")
                .verifyComplete();
    }

    @Test
    public void fluxErrorHandling_onErrorMap(){
        Flux<String> stringFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap((e) -> new CustomException(e)).log();
        //StepVerifier is the subscriber here
        StepVerifier.create(stringFlux)
                .expectSubscription()
                //Here we dont have "D"
                .expectNext("A","B","C")
               .expectError(CustomException.class)
               .verify();
    }

    @Test
    public void fluxErrorHandling_onErrorMap_withRetry(){
        Flux<String> stringFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap((e) -> new CustomException(e)).retry(2).log();
        //StepVerifier is the subscriber here
        StepVerifier.create(stringFlux)
                .expectSubscription()
                //Here we dont have "D".This is the normal execution flow
                .expectNext("A","B","C")
                //this is retry # 1
                .expectNext("A","B","C")
                //this is retry # 2
                .expectNext("A","B","C")
                .expectError(CustomException.class)
                .verify();
    }

    @Test
    public void fluxErrorHandling_onErrorMap_withRetryBackOff(){
        Flux<String> stringFlux = Flux.just("A","B","C")
                .concatWith(Flux.error(new RuntimeException("Exception Occurred")))
                .concatWith(Flux.just("D"))
                .onErrorMap((e) -> new CustomException(e))
                //this duration that you have provided is not going to be exact
                //it will be somewhat more than you provide
                .retryWhen(Retry.backoff(2,Duration.ofSeconds(10))).log();
        //StepVerifier is the subscriber here
        StepVerifier.create(stringFlux)
                .expectSubscription()
                //Here we dont have "D".This is the normal execution flow
                .expectNext("A","B","C")
                //this is retry # 1
                .expectNext("A","B","C")
                //this is retry # 2
                .expectNext("A","B","C")
                .expectError(IllegalStateException.class)
                .verify();
    }
}
