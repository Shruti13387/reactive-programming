package com.learnreactive.springreactive.controller.v1;

import com.learnreactive.springreactive.constants.ItemConstants;
import com.learnreactive.springreactive.document.ItemCapped;
import com.learnreactive.springreactive.repository.ItemReactiveCappedRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
public class ItemStreamController {

    @Autowired
    ItemReactiveCappedRepository cappedRepository;

    @GetMapping(value = ItemConstants.ITEM_STREAM_END_POINT_V1, produces = MediaType.APPLICATION_STREAM_JSON_VALUE)
    public Flux<ItemCapped> getItemsStream(){
        return cappedRepository.findItemBy();
    }
}
