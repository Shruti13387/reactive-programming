package com.learnreactive.springreactive.controller.v1;

import com.learnreactive.springreactive.constants.ItemConstants;
import com.learnreactive.springreactive.document.Item;
import com.learnreactive.springreactive.repository.ItemReactiveRepository;
import org.junit.Assert;
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
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@SpringBootTest
@RunWith(SpringRunner.class)
@DirtiesContext
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ItemControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository reactiveRepository;

    public List<Item> data(){
        return Arrays.asList(new Item(null, "Samsung TV", 400.0),
                new Item(null, "LG TV", 440.0),
                new Item(null, "Apple Watch", 265.22),
                new Item("AS", "Beats Headphones", 195.3),
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
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5);
    }

    @Test
    public void getAllItems_approach2(){
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_JSON)
                .expectBodyList(Item.class)
                .hasSize(5)
                .consumeWith((response) -> {
                    List<Item> items = response.getResponseBody();
                    items.forEach((item -> {
                        Assert.assertTrue(item.getId()!=null);
                    }));
                });

    }

    @Test
    public void getAllItems_approach3(){
        Flux<Item> itemFlux = webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1)
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

    @Test
    public void getOneItems() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABS")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 195.3);
    }

    @Test
    public void getOneItems_NotFound() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "asa")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void createItem() {
        Item item = new Item(null, "Iphone X", 999.99);
        webTestClient.post().uri(ItemConstants.ITEM_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Iphone X")
                .jsonPath("$.price").isEqualTo(999.99);
    }

    @Test
    public void deleteItem() {
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABS")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateItem() {
        Item item = new Item(null, "Beats Headphones", 999.99);
        webTestClient.put().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "ABS")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Beats Headphones")
                .jsonPath("$.price").isEqualTo(999.99);
    }

    @Test
    public void updateItem_InvalidId() {
        Item item = new Item(null, "Beats Headphones", 999.99);
        webTestClient.put().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"), "DEF")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void getOneItems_Functional() {
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABS")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price", 195.3);
    }

    @Test
    public void getOneItems_NotFound_Functional() {
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "asa")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    public void createItem_Functional() {
        Item item = new Item(null, "Iphone X", 999.99);
        webTestClient.post().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Iphone X")
                .jsonPath("$.price").isEqualTo(999.99);
    }

    @Test
    public void deleteItem_Functional() {
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABS")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void deleteItem_NotFound_Functional() {
        webTestClient.get().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "AB1")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(Void.class);
    }

    @Test
    public void updateItem_Functional() {
        Item item = new Item(null, "Beats Headphones", 999.99);
        webTestClient.put().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "ABS")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Beats Headphones")
                .jsonPath("$.price").isEqualTo(999.99);
    }

    @Test
    public void updateItem_InvalidId_Functional() {
        Item item = new Item(null, "Beats Headphones", 999.99);
        webTestClient.put().uri(ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1.concat("/{id}"), "DEF")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item), Item.class)
                .exchange()
                .expectStatus().isNotFound();
    }
}