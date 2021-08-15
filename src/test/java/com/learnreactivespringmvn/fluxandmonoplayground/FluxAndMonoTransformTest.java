package com.learnreactivespringmvn.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("jack","jenny","anna","adam");
    @Test
    public void transformToStringUsingMap(){
        Flux<String> stringFlux = Flux.fromIterable(names).map(s -> s.toUpperCase()).log();
        //StepVerifier is the subscriber here
        StepVerifier.create(stringFlux).expectNext("JACK","JENNY","ANNA","ADAM").verifyComplete();
    }

    @Test
    public void transformToIntegerUsingMap(){
        Flux<Integer> stringFlux = Flux.fromIterable(names).map(String::length).repeat(1).log();
        //StepVerifier is the subscriber here
        StepVerifier.create(stringFlux).expectNext(4,5,4,4,4,5,4,4).verifyComplete();
    }

    @Test
    public void transformToStringUsingFilterAndMap(){
        Flux<String> stringFlux = Flux.fromIterable(names).filter(s -> s.length() > 4).map(s -> s.toUpperCase()).log();
        //StepVerifier is the subscriber here
        StepVerifier.create(stringFlux).expectNext("JENNY").verifyComplete();
    }

    @Test
    public void transformUsingFlatMap(){
        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                .flatMap(s -> {
                    return Flux.fromIterable(convertStringToList(s));//convertStringToList will return [A,"newValue"] for A & so on
                })//db or external service call that returns a flux
                .log();
        //StepVerifier is the subscriber here
        StepVerifier.create(stringFlux).expectNextCount(12).verifyComplete();
    }

    @Test
    public void transformUsingFlatMapWithSeperateMapperDeclaration(){
        //mapper is a Function of input String and output is Publisher
        Function<String,Publisher<String>> mapper = s -> Flux.fromIterable(convertStringToList(s));

        Flux<String> stringFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                //we use flatMap when we have a db or external service call "that returns a flux"
                //or when we have a Flux of Flux OR Flux of Collection Eg: Flux of List or Flux of Array
                .flatMap(mapper)
                .log();
        //StepVerifier is the subscriber here
        StepVerifier.create(stringFlux).expectNextCount(12).verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_usingparallel(){
        Function<String,List<String>> simpleMapMapper = s -> convertStringToList(s);
        //subscribeOn switches one thread to another thread
        //parallel() operation makes sure each and every operation is handled parallely
        Function<Flux<String>,Flux<List<String>>> flatMapper1 = (s) -> s.map(simpleMapMapper).subscribeOn(parallel());
        Function<List<String>,Flux<String>> flatMapper2 = s -> Flux.fromIterable(s);

        Flux<String> nestedFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                // window returns Flux<Flux<String>> -> (A,B), (C,D), (E,F)
                .window(2)
                //the below flatMap operates on Flux<String> elements in the above Flux<Flux<String>>
                // & returns Flux<List<String>>
                .flatMap(flatMapper1)
                //the below flatMap operates on List<String> and returns Flux<String>
                .flatMap(flatMapper2)
                .log();
        //StepVerifier is the subscriber here
        StepVerifier.create(nestedFlux).expectNextCount(12).verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_usingparallel_maintainorder(){
        Flux<String> nestedFlux = Flux.fromIterable(Arrays.asList("A","B","C","D","E","F"))
                .window(2)
                // concatMap works but takes more time even with parallel and hence we use flatMapSequential
                //.concatMap((s) -> s.map(this::convertStringToList).subscribeOn(parallel()))
                .flatMapSequential((s) -> s.map(this::convertStringToList).subscribeOn(parallel()))
                .flatMap(s -> Flux.fromIterable(s))
                .log();
        //StepVerifier is the subscriber here
        StepVerifier.create(nestedFlux).expectNextCount(12).verifyComplete();
        System.out.println("Subscription trigerred !!! "+Thread.currentThread().getName());
    }

    /*
    this method represents the DB action
     */
    private List<String> convertStringToList(String s) {
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return Arrays.asList(s,"newValue");
    }
}
