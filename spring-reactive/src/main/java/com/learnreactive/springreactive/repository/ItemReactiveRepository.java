package com.learnreactive.springreactive.repository;

import com.learnreactive.springreactive.document.Item;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface ItemReactiveRepository extends ReactiveMongoRepository<Item,String> {

    Mono<Item> findByDescription(String description);
}
