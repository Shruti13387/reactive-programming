package com.learnreactive.springreactive.handler;

import com.learnreactive.springreactive.document.Item;
import com.learnreactive.springreactive.document.ItemCapped;
import com.learnreactive.springreactive.repository.ItemReactiveCappedRepository;
import com.learnreactive.springreactive.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static org.springframework.web.reactive.function.BodyInserters.fromValue;

@Component
public class ItemHandler {

    @Autowired
    ItemReactiveRepository reactiveRepository;

    @Autowired
    ItemReactiveCappedRepository cappedRepository;

    static Mono<ServerResponse> NOT_FOUND = ServerResponse.notFound().build();

    public Mono<ServerResponse> getAllItems(ServerRequest serverRequest){
        return ServerResponse.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(reactiveRepository.findAll(), Item.class);
    }

    public Mono<ServerResponse> getItemByID(ServerRequest serverRequest){
        String id = serverRequest.pathVariable("id");
        Mono<Item> itemMono = reactiveRepository.findById(id);
        return itemMono.flatMap(item ->
                ServerResponse.ok()
        .contentType(MediaType.APPLICATION_JSON)
        .body(fromValue(item)))
                .switchIfEmpty(NOT_FOUND);
    }

    public Mono<ServerResponse> createItem(ServerRequest serverRequest){
        Mono<Item> itemMono = serverRequest.bodyToMono(Item.class);
        return itemMono.flatMap(item ->
                ServerResponse.created(serverRequest.uri())
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(reactiveRepository.save(item),Item.class));
    }

    public Mono<ServerResponse> deleteItem(ServerRequest serverRequest){
        String id = serverRequest.pathVariable("id");
        Mono<Void> deleteItem = reactiveRepository.deleteById(id);
        return deleteItem.flatMap(item ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(deleteItem,Void.class)
                        .switchIfEmpty(NOT_FOUND));
    }

    public Mono<ServerResponse> updateItem(ServerRequest serverRequest){
        String id = serverRequest.pathVariable("id");
        Mono<Item> updatedItem = serverRequest.bodyToMono(Item.class)
                .flatMap((item) -> {
                    Mono<Item> itemMono = reactiveRepository.findById(id)
                            .flatMap(currentItem -> {
                                currentItem.setDescription(item.getDescription());
                                currentItem.setPrice(item.getPrice());
                                return reactiveRepository.save(currentItem);
                            });
                    return itemMono;
                });

        return updatedItem.flatMap(item ->
                ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(fromValue(item))
                        .switchIfEmpty(NOT_FOUND));
    }

    public Mono<ServerResponse> getStreamItems(ServerRequest serverRequest){
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_STREAM_JSON)
                .body(cappedRepository.findItemBy(), ItemCapped.class);
    }
}
