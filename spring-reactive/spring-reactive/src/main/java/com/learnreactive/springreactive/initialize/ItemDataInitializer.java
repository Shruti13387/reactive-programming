package com.learnreactive.springreactive.initialize;

import com.learnreactive.springreactive.document.Item;
import com.learnreactive.springreactive.document.ItemCapped;
import com.learnreactive.springreactive.repository.ItemReactiveCappedRepository;
import com.learnreactive.springreactive.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.data.mongodb.core.CollectionOptions;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
@Slf4j
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepository reactiveRepository;

    @Autowired
    ItemReactiveCappedRepository cappedRepository;

    @Autowired
    MongoOperations mongoOperations;

    private void createCappedCollection() {
        mongoOperations.dropCollection(ItemCapped.class);
        mongoOperations.createCollection(ItemCapped.class, CollectionOptions.empty().maxDocuments(20).size(50000).capped());
    }

    @Override
    public void run(String... args) throws Exception {
        initialDataSetUp();
        createCappedCollection();
        dataSetUpForCappedCollection();
    }

    public List<Item> data() {
        return Arrays.asList(new Item(null, "Samsung TV", 400.0),
                new Item(null, "LG TV", 440.0),
                new Item(null, "Apple Watch", 265.22),
                new Item(null, "Beats Headphones", 195.3),
                new Item("ABS", "Bose Headphones", 195.3));
    }

    private void initialDataSetUp() {
        reactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(reactiveRepository::save)
                .thenMany(reactiveRepository.findAll())
                .subscribe((item -> {
                    System.out.println("Item inserted from CommandLine runner: " + item);
                }));
    }

    private void dataSetUpForCappedCollection() {
        Flux<ItemCapped> itemCappedFlux = Flux.interval(Duration.ofSeconds(1))
                .map(i -> new ItemCapped(null, "Randon Item " + i, (100.00 + i)));
        cappedRepository.insert(itemCappedFlux)
                .subscribe((itemCapped -> {
                    log.info("Inserted Item is " + itemCapped);
                }));
    }
}
