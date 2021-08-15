package com.learnreactivespringmvn.repository;

import com.learnreactivespringmvn.document.Item;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import java.util.Arrays;
import java.util.List;

@DataMongoTest//advantage of this annotation is that it wont bring up the whole
// application context but only the mongodb related classes
@RunWith(SpringRunner.class)
//this is needed since these test cases change the DB
@DirtiesContext
@ActiveProfiles("integration-test")
public class ItemReactiveRepositoryTest {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    List<Item> itemList = Arrays.asList(Item.builder().description("Samsung TV").price(400.0).build()
                                        ,Item.builder().description("LG TV").price(420.0).build()
                                        ,Item.builder().description("Apple Watch").price(299.99).build()
                                        ,Item.builder().description("Beats Headphones").price(149.99).build()
                                        ,Item.builder().id("ABC").description("Bose Headphones").price(149.99).build());
    @BeforeEach
    public void setUp(){
        itemReactiveRepository.deleteAll().thenMany(Flux.fromIterable(itemList)).flatMap(itemReactiveRepository::save)
                .doOnNext(item -> {
                    System.out.println("Inserted Item is :" + item);
                })
                //the below blockLast call makes sure that all the data is inserted before the test case is executed
                //never use this in actual code.Only use in test cases
                .blockLast();
    }

    @Test
    public void getAllItems(){
        StepVerifier.create(itemReactiveRepository.findAll()).expectSubscription().expectNextCount(5).verifyComplete();
    }

    @Test
    public void getItemById(){
        StepVerifier.create(itemReactiveRepository.findById("ABC")).expectSubscription().expectNextMatches(item ->
            item.getDescription().equals("Bose Headphones")
        ).verifyComplete();
    }

    @Test
    public void findItemByDescription(){
        StepVerifier.create(itemReactiveRepository.findByDescription("Bose Headphones").log("findItemByDescription :")).expectSubscription().expectNextCount(1).verifyComplete();
    }

    @Test
    public void saveItem(){
        Item item = new Item(null,"Google Home Mini",30.00);
        Mono<Item> savedItem =  itemReactiveRepository.save(item);
        StepVerifier.create(savedItem.log("SavedItem : ")).expectSubscription().expectNextMatches(item1 -> item1.getId()!=null && item1.getDescription().equals("Google Home Mini")).verifyComplete();
    }

    @Test
    public void updateItem(){
        double newPrice = 520.00;
        Mono<Item> updatedItem = itemReactiveRepository.findByDescription("LG TV")
                .map(item -> {
                            item.setPrice(newPrice);
                            return item;
                        }
                )
                .flatMap(item -> {
                    return itemReactiveRepository.save(item);
                });
        StepVerifier.create(updatedItem).expectSubscription().expectNextMatches(item -> {
            return item.getPrice()==520.00;
        }).verifyComplete();
    }

    @Test
    public void deleteItemById(){
        Mono<Void> deletedItem = itemReactiveRepository.findById("ABC")//Mono<Item>
                .map(Item::getId)//transform the Item object to Id
                .flatMap(id -> {
                    return itemReactiveRepository.deleteById(id);
                });
        StepVerifier.create(deletedItem.log()).expectSubscription().verifyComplete();
        StepVerifier.create(itemReactiveRepository.findAll().log()).expectSubscription().expectNextCount(4).verifyComplete();
    }

    @Test
    public void deleteItem(){
        Mono<Void> deletedItem = itemReactiveRepository.findByDescription("LG TV")//Mono<Item>
                .flatMap(item -> {
                    return itemReactiveRepository.delete(item);
                });
        StepVerifier.create(deletedItem.log()).expectSubscription().verifyComplete();
        StepVerifier.create(itemReactiveRepository.findAll().log()).expectSubscription().expectNextCount(4).verifyComplete();
    }
}
