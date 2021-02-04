package com.learnreactive.springreactive.controller;

//import org.junit.Assert;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    public void flux_approach1(){

//        VirtualTimeScheduler.getOrSet();

        Flux<Integer> integerFlux = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier./*withVirtualTime(() -> integerFlux)*/create(integerFlux)
                .expectSubscription()
              //  .thenAwait(Duration.ofSeconds(6))
                //.expectNextCount(6)
                .expectNext(1)
                .expectNext(2)
                .expectNext(3)
                .expectNext(4)
                .verifyComplete();
    }

    @Test
    public void flux_approach2(){

        webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Integer.class)
                .hasSize(4);

    }

    @Test
    public void flux_approach3(){

        List<Integer> expectedList = Arrays.asList(1,2,3,4);
        EntityExchangeResult<List<Integer>> exchangeResult = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();

        assertEquals(expectedList, exchangeResult.getResponseBody());
    }

    @Test
    public void flux_approach4(){
        List<Integer> expectedList = Arrays.asList(1,2,3,4);
        webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith((response) -> {
                    assertEquals(expectedList, response.getResponseBody());
                });
    }

    @Test
    public void fluxStream(){

//        VirtualTimeScheduler.getOrSet();

        Flux<Long> longFlux = webTestClient.get().uri("/fluxstream")
                .accept(MediaType.APPLICATION_STREAM_JSON)
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier./*withVirtualTime(() -> integerFlux)*/create(longFlux)
                .expectSubscription()
                //  .thenAwait(Duration.ofSeconds(6))
                //.expectNextCount(6)
                .expectNext(0l)
                .expectNext(1l)
                .expectNext(2l)
                .thenCancel()
                .verify();
    }

    @Test
    public void mono() {
        Integer expectedValue = new Integer(1);
        webTestClient.get().uri("/mono")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith((response) -> {
                    assertEquals(expectedValue, response.getResponseBody());
                });
    }
}