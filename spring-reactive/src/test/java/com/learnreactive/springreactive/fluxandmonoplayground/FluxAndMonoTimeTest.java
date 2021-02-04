package com.learnreactive.springreactive.fluxandmonoplayground;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.Duration;

public class FluxAndMonoTimeTest {

    @Test
    public void infiniteSequence() throws InterruptedException {
        Flux<Long> infiniteFlux = Flux.interval(Duration.ofMillis(200)).log();

        infiniteFlux
                .subscribe((element) -> System.out.println("Value is :"+ element));


    }

    @Test
    public void infiniteSequenceTest() throws InterruptedException {
        Flux<Long> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0L,1L,2L)
                .verifyComplete();

    }

    @Test
    public void infiniteSequenceMapTest() throws InterruptedException {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .map(l -> new Integer(l.intValue()))
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0,1,2)
                .verifyComplete();

    }

    @Test
    public void infiniteSequenceMapTest_withDelay() throws InterruptedException {
        Flux<Integer> finiteFlux = Flux.interval(Duration.ofMillis(200))
                .delayElements(Duration.ofSeconds(1))
                .map(l -> new Integer(l.intValue()))
                .take(3)
                .log();

        StepVerifier.create(finiteFlux)
                .expectSubscription()
                .expectNext(0,1,2)
                .verifyComplete();

    }
}
