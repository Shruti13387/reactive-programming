package com.learnreactivespring.controller;

import com.learnreactivespring.domain.Item;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

@RestController
public class ItemClientController {

    WebClient webClient = WebClient.create("http://localhost:8080");

    @GetMapping("/client/retrieve")
    public Flux<Item> getAllItemsUsingRetrieve(){
        return webClient.get().uri("/c1/items")
                .retrieve()
                .bodyToFlux(Item.class)
                .log("Items in Client Project");
    }
}
