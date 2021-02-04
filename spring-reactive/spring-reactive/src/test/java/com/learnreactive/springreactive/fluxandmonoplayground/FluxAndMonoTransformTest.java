package com.learnreactive.springreactive.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static reactor.core.scheduler.Schedulers.parallel;

public class FluxAndMonoTransformTest {

    List<String> names = Arrays.asList("adam","anna","jack","jenny");

    @Test
    public void transformUsingMap(){
        Flux<String> nameFlux = Flux.fromIterable(names)
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(nameFlux)
                .expectNext("ADAM","ANNA","JACK", "JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Length(){
        Flux<Integer> nameFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .log();

        StepVerifier.create(nameFlux)
                .expectNext(4,4,4,5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Length_Repeat(){
        Flux<Integer> nameFlux = Flux.fromIterable(names)
                .map(s -> s.length())
                .repeat(1)
                .log();

        StepVerifier.create(nameFlux)
                .expectNext(4,4,4,5,4,4,4,5)
                .verifyComplete();
    }

    @Test
    public void transformUsingMap_Filter(){
        Flux<String> nameFlux = Flux.fromIterable(names)
                .filter(s -> s.length() > 4)
                .map(s -> s.toUpperCase())
                .log();

        StepVerifier.create(nameFlux)
                .expectNext("JENNY")
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap(){
        Flux<String> nameFlux = Flux.fromIterable(Arrays.asList("A", "B","C","D","E","F"))
                .flatMap(s -> {
                    return Flux.fromIterable(convertToList(s));
                }) // db or external service call that returns a flux -> s -> Flux<String>
                .log();

        StepVerifier.create(nameFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_usingParallel(){
        Flux<String> nameFlux = Flux.fromIterable(Arrays.asList("A", "B","C","D","E","F"))
                .window(2) //Flux<Flux<String>> -> (A,B), (C,D), (E,F)
                .flatMap(s ->
                    s.map(this::convertToList).subscribeOn(parallel()))
//                ) // db or external service call that returns a flux -> s -> Flux<String>
                .flatMap(s -> Flux.fromIterable(s))
                .log();

        StepVerifier.create(nameFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    @Test
    public void transformUsingFlatMap_usingParallel_maintain_order(){
        Flux<String> nameFlux = Flux.fromIterable(Arrays.asList("A", "B","C","D","E","F"))
                .window(2) //Flux<Flux<String>> -> (A,B), (C,D), (E,F)
//                .concatMap(s ->
//                        s.map(this::convertToList).subscribeOn(parallel()))
                .flatMapSequential(s ->
                        s.map(this::convertToList).subscribeOn(parallel()))
                .flatMap(s -> Flux.fromIterable(s))
                .log();

        StepVerifier.create(nameFlux)
                .expectNextCount(12)
                .verifyComplete();
    }

    private List<String> convertToList(String s) {
        try {
            Thread.sleep(1000);
        }catch(InterruptedException ex){
            ex.printStackTrace();
        }
        return Arrays.asList(s, "newValue");
    }
}