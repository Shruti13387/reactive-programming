package com.learnreactive.springreactive.repository;

import com.learnreactive.springreactive.document.Item;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

@DataMongoTest
@RunWith(SpringJUnit4ClassRunner.class)
@DirtiesContext
class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository reactiveRepository;

    List<Item> itemList = Arrays.asList(new Item(null, "Samsung TV", 400.0),
            new Item(null, "LG TV", 440.0),
            new Item(null, "Apple Watch", 265.22),
            new Item(null, "Beats Headphones", 195.3),
            new Item("ABS", "Bose Headphones", 195.3));

    @BeforeEach
    public void onSetUp() {
        reactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(itemList))
                .flatMap(reactiveRepository::save)
                .doOnNext((item -> {
                    System.out.println("Inserted Item is :" + item);
                }))
                .blockLast();
    }

    @Test
    public void getAllItems() {
        StepVerifier.create(reactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(5)
                .verifyComplete();
    }

    @Test
    public void getItemById() {
        StepVerifier.create(reactiveRepository.findById("ABS"))
                .expectSubscription()
                .expectNextMatches((item -> item.getDescription().equals("Bose Headphones")))
                .verifyComplete();
    }

    @Test
    public void findByDescription(){

        StepVerifier.create(reactiveRepository.findByDescription("Bose Headphones").log())
                .expectSubscription()
                .expectNextCount(1)
                .verifyComplete();
    }

    @Test
    public void saveItem(){

        StepVerifier.create(reactiveRepository.save(new Item(null, "Google Home Mini", 30.00)).log("saveitem: "))
                .expectSubscription()
                .expectNextMatches(item1 -> item1.getId()!=null && item1.getDescription().equals("Google Home Mini"))
                .verifyComplete();
    }

    @Test
    public void updateItem(){

        Mono<Item> updatedItem = reactiveRepository.findByDescription("LG TV")
                .map(item -> {
                    item.setPrice(540.0);
                    return item;
                })
                .flatMap(item ->{
                    return reactiveRepository.save(item);
                });

        StepVerifier.create(updatedItem.log("Update item: "))
                .expectSubscription()
                .expectNextMatches(item1 -> item1.getPrice()==540)
                .verifyComplete();
    }

    @Test
    public void deleteItemById(){

        Mono<Void> updatedItem = reactiveRepository.findById("ABS")
                .map(Item::getId)
                .flatMap(id ->{
                    return reactiveRepository.deleteById(id);
                });

        StepVerifier.create(updatedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(reactiveRepository.findAll())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }

    @Test
    public void deleteItemByDescription(){

        Mono<Void> updatedItem = reactiveRepository.findByDescription("Apple Watch")
                .flatMap(item ->{
                    return reactiveRepository.delete(item);
                });

        StepVerifier.create(updatedItem.log())
                .expectSubscription()
                .verifyComplete();

        StepVerifier.create(reactiveRepository.findAll().log())
                .expectSubscription()
                .expectNextCount(4)
                .verifyComplete();
    }
}