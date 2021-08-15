package com.learnreactivespringmvn.controller.v1;

import com.learnreactivespringmvn.document.Item;
import com.learnreactivespringmvn.repository.ItemReactiveRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static com.learnreactivespringmvn.constants.ItemConstants.ITEM_END_POINT_V1;

@RestController
@Slf4j
public class ItemController {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @GetMapping(ITEM_END_POINT_V1)
    public Flux<Item> getAllItems(){
       return itemReactiveRepository.findAll();
    }

    @GetMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<ResponseEntity<Item>> getOneItem(@PathVariable String id){
        return itemReactiveRepository.findById(id).map(item -> {
            return new ResponseEntity<>(item, HttpStatus.OK);
        }).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping(ITEM_END_POINT_V1)
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Item> createItem(@RequestBody Item item){
        return itemReactiveRepository.save(item);
    }

    @DeleteMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<Void> deleteItem(@PathVariable String id){
        //even if its not going to return anything we have to keep the return type as Mono<Void>
        //since its asynchronous and non blocking
        return itemReactiveRepository.deleteById(id);
    }

    //id and item to be updated in the req = path variable and request body - completed
    //using the id get the item from the database
    //update the item retrieved with the value from the request body
    //save the item
    //return the saved item
    @PutMapping(ITEM_END_POINT_V1+"/{id}")
    public Mono<ResponseEntity<Item>> updateItem(@PathVariable String id,@RequestBody Item item){
        return itemReactiveRepository.findById(id).flatMap(currentItem -> {
        currentItem.setPrice(item.getPrice());
        currentItem.setDescription(item.getDescription());
        return itemReactiveRepository.save(currentItem);
        }).map(updatedItem ->
            new ResponseEntity<>(updatedItem,HttpStatus.OK)
        ).defaultIfEmpty(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

}
