package com.learnreactive.springreactive.controller.v1;

import com.learnreactive.springreactive.document.Item;
import com.learnreactive.springreactive.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.learnreactive.springreactive.constants.ItemConstants.*;

@RestController
@Slf4j
public class ItemController {

    @Autowired
    ItemReactiveRepository reactiveRepository;

    @GetMapping(ITEM_END_POINT_V1)
    public Flux<Item> getAllItems(){
        return reactiveRepository.findAll();
    }

    @GetMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<ResponseEntity<Item>> getOneItems(@PathVariable String id){
        return reactiveRepository.findById(id)
                .map((item) -> new ResponseEntity<>(item, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(ITEM_END_POINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItems(@RequestBody Item item){
        return reactiveRepository.save(item);
    }

    @DeleteMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<Void> deleteItems(@PathVariable String id){
        return reactiveRepository.deleteById(id);
    }

    @PutMapping(ITEM_END_POINT_V1+"/{id}")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<ResponseEntity<Item>> updateItems(@PathVariable String id,
                                                  @RequestBody Item item){
        return reactiveRepository.findById(id)
                .flatMap(currentItem -> {
                    currentItem.setPrice(item.getPrice());
                    currentItem.setDescription(item.getDescription());
                    return reactiveRepository.save(currentItem);
                })
                .map(updateItems -> new ResponseEntity<>(updateItems, HttpStatus.OK))
                .defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }
}
