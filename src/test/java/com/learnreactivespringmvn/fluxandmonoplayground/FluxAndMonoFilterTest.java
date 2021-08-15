package com.learnreactivespringmvn.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.stream.Stream;

public class FluxAndMonoFilterTest {

    @Test
    public void filterTestStartsWith(){
        Flux<String> stringFlux = Flux.fromStream(Stream.of("adam","anna","jenny","jack")).filter(s -> s.startsWith("a")).log();
        //StepVerifier is the subscriber here
        StepVerifier.create(stringFlux).expectNext("adam","anna").verifyComplete();
    }

    @Test
    public void filterTestLength(){
        Flux<String> stringFlux = Flux.fromStream(Stream.of("adam","anna","jenny","jack")).filter(s -> s.length()>4).log();
        //StepVerifier is the subscriber here
        StepVerifier.create(stringFlux).expectNext("jenny").verifyComplete();
    }
}
