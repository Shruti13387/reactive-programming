package com.learnreactive.springreactive.repository;

import com.learnreactive.springreactive.document.ItemCapped;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.mongodb.repository.Tailable;
import reactor.core.publisher.Flux;

public interface ItemReactiveCappedRepository extends ReactiveMongoRepository<ItemCapped,String> {

    @Tailable
    Flux<ItemCapped> findItemBy();

}
