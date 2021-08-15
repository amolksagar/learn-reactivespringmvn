package com.learnreactivespringmvn.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

@RestController
public class FluxAndMonoController {

    //the browser is like a subscriber requesting data from this API
    @GetMapping("/flux")
    public Flux<Integer> returnFlux(){
        return Flux.just(1,2,3,4).delayElements(Duration.ofSeconds(1)).log();
    }

    //if you give the produces property as deprecated APPLICATION_STREAM_JSON_VALUE then the browser will showcase the stream like behaviour
    @GetMapping(value = "/fluxstream",produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Integer> returnFluxStream(){
        return Flux.just(1,2,3,4).delayElements(Duration.ofSeconds(1)).log();
    }

    //if you give the produces property as deprecated APPLICATION_STREAM_JSON_VALUE then the browser will showcase the stream like behaviour
    @GetMapping(value = "/fluxstreaminfinite",produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Flux<Long> returnFluxStreamInfinite(){
        return Flux.interval(Duration.ofSeconds(1)).log();
    }

    @GetMapping(value = "/mono",produces = MediaType.APPLICATION_NDJSON_VALUE)
    public Mono<Integer> returnMono(){
        return Mono.just(1).log();
    }

}
