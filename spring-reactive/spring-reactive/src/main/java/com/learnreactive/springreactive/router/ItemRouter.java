package com.learnreactive.springreactive.router;

import com.learnreactive.springreactive.handler.ItemHandler;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static com.learnreactive.springreactive.constants.ItemConstants.ITEM_FUNCTIONAL_END_POINT_V1;
import static com.learnreactive.springreactive.constants.ItemConstants.ITEM_STREAM_FUNCTIONAL_END_POINT_V1;
import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class ItemRouter {

    @Bean
    public RouterFunction<ServerResponse> itemResponse(ItemHandler itemHandler) {

        return RouterFunctions
                .route(GET(ITEM_FUNCTIONAL_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON))
                        , itemHandler::getAllItems)
                .andRoute(GET(ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}").and(accept(MediaType.APPLICATION_JSON))
                        , itemHandler::getItemByID)
                .andRoute(POST(ITEM_FUNCTIONAL_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON))
                        , itemHandler::createItem)
                .andRoute(DELETE(ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}").and(accept(MediaType.APPLICATION_JSON))
                        , itemHandler::deleteItem)
                .andRoute(PUT(ITEM_FUNCTIONAL_END_POINT_V1 + "/{id}").and(accept(MediaType.APPLICATION_JSON))
                        , itemHandler::updateItem);
    }

    @Bean
    public RouterFunction<ServerResponse> itemStreamResponse(ItemHandler itemHandler){
        return RouterFunctions
                .route(GET(ITEM_STREAM_FUNCTIONAL_END_POINT_V1).and(accept(MediaType.APPLICATION_JSON))
                        ,itemHandler::getStreamItems);
    }
}