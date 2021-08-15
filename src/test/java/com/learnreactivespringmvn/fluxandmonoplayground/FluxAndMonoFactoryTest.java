package com.learnreactivespringmvn.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

public class FluxAndMonoFactoryTest {

    List<String> names = Arrays.asList("adam","anna","jack","jenny");
    @Test
    public void fluxUsingIterable(){
        Flux<String> namesFlux = Flux.fromIterable(names).log();
        //StepVerifier is the subscriber here
        StepVerifier.create(namesFlux).expectNext("adam","anna","jack","jenny").verifyComplete();
    }

    @Test
    public void fluxUsingArrays(){
        String[] names = new String[]{"adam","anna","jack","jenny"};
        Flux<String> namesFlux = Flux.fromArray(names);
        //StepVerifier is the subscriber here
        StepVerifier.create(namesFlux).expectNext("adam","anna","jack","jenny").verifyComplete();
    }

    @Test
    public void fluxUsingStream(){
        Flux<String> namesFlux = Flux.fromStream(names.stream());
        //StepVerifier is the subscriber here
        StepVerifier.create(namesFlux).expectNext("adam","anna","jack","jenny").verifyComplete();
    }

    @Test
    public void monoUsingJustOrEmpty(){
        Mono<String> mono = Mono.justOrEmpty(null);
        //StepVerifier is the subscriber here
        StepVerifier.create(mono.log()).verifyComplete();
    }

    @Test
    public void monoUsingSupplier(){
        Supplier<String> stringSupplier = () -> "adam";
        Mono<String> mono = Mono.fromSupplier(stringSupplier);
        //StepVerifier is the subscriber here
        StepVerifier.create(mono.log()).expectNext("adam").verifyComplete();
    }

    @Test
    public void fluxUsingRange(){
        Flux<Integer> integerFlux = Flux.range(1,5).log();
        //StepVerifier is the subscriber here
        StepVerifier.create(integerFlux).expectNext(1,2,3,4,5).verifyComplete();
    }
}
