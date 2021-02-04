package com.learnreactive.springreactive.handler;

import com.learnreactive.springreactive.constants.ItemConstants;
import com.learnreactive.springreactive.document.Item;
import com.learnreactive.springreactive.repository.ItemReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ItemHandlerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository reactiveRepository;

    public List<Item> data(){
        return Arrays.asList(new Item(null, "Samsung TV", 400.0),
                new Item(null, "LG TV", 440.0),
                new Item(null, "Apple Watch", 265.22),
                new Item(null, "Beats Headphones", 195.3),
                new Item("ABS", "Bose Headphones", 195.3));
    }

    @BeforeEach
    public void setUp(){

        reactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(reactiveRepository::save)
                .doOnNext((item -> {
                    System.out.println("Inserted item is: "+item);
                }))
                .blockLast();
    }

    @Test
    public void getAllItems(){
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5);
    }

    @Test
    public void getAllItems_approach2(){
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .consumeWith( responseBody -> {
                    List<Item> items = responseBody.getResponseBody();
                    items.forEach( item -> assertTrue(item.getId()!=null));
                });
    }

    @Test
    public void getAllItems_approach3(){
        Flux<Item> itemFlux = webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .returnResult(Item.class)
                .getResponseBody();

        StepVerifier.create(itemFlux)
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

}