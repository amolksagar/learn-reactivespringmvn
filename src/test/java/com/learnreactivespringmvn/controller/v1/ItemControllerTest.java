package com.learnreactivespringmvn.controller.v1;

import com.learnreactivespringmvn.constants.ItemConstants;
import com.learnreactivespringmvn.document.Item;
import com.learnreactivespringmvn.repository.ItemReactiveRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertTrue;

//this is going to load the whole context
@SpringBootTest
@RunWith(SpringRunner.class)
//because we are going to do CRUD operations on the mongo
@DirtiesContext
//this will autoconfigure the WebTestClient
@AutoConfigureWebTestClient
@ActiveProfiles("test")
class ItemControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Autowired
    ItemReactiveRepository itemReactiveRepository;

    @BeforeEach
    public void setUp(){
        itemReactiveRepository.deleteAll().thenMany(Flux.fromIterable(data())).flatMap(item -> {
            return itemReactiveRepository.save(item);
        })
        .doOnNext(item -> {
            System.out.println("Inserted Item is : "+ item);
        })
        //this makes sure that this block is executed and then the test cases get executed
        .blockLast();
    }

    public List<Item> data(){
        return Arrays.asList(
                Item.builder().description("Samsung TV").price(399.99).build(),
                Item.builder().description("LG TV").price(329.99).build(),
                Item.builder().description("Apple Watch").price(349.99).build(),
                Item.builder().id("ABC").description("Beats HeadPhones").price(19.99).build()
        );
    }

    @Test
    public void getAllItems(){
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1).exchange().expectStatus().isOk().expectHeader()
                .contentType(MediaType.APPLICATION_JSON).expectBodyList(Item.class).hasSize(4);
    }

    @Test
    public void getAllItems_approach2(){
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1).exchange().expectStatus().isOk().expectHeader()
                .contentType(MediaType.APPLICATION_JSON).expectBodyList(Item.class).hasSize(4)
        .consumeWith(response -> {
            List<Item> items =  response.getResponseBody();
            items.forEach(item -> {
                assertTrue(item.getId()!=null);
            });
        });
    }

    @Test
    public void getAllItems_approach3(){
        Flux<Item> itemFlux = webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1).exchange().expectStatus().isOk().expectHeader()
                .contentType(MediaType.APPLICATION_JSON).returnResult(Item.class).getResponseBody();
        StepVerifier.create(itemFlux.log("value from network : ")).expectSubscription().expectNextCount(4).verifyComplete();
    }

    @Test
    public void getOneItem(){
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"),"ABC")
                .exchange().expectStatus().isOk().expectBody().jsonPath("$.price",19.99);
    }

    @Test
    public void getOneItem_notFound(){
        webTestClient.get().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"),"DEF")
                .exchange().expectStatus().isNotFound();
    }

    @Test
    public void createItem(){
        Item item = new Item().builder().description("Iphone X").price(999.99).build();
        webTestClient.post().uri(ItemConstants.ITEM_END_POINT_V1).contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(item),Item.class)
                .exchange().expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.id").isNotEmpty()
                .jsonPath("$.description").isEqualTo("Iphone X")
                .jsonPath("$.price").isEqualTo(999.99);
    }

    @Test
    public void deleteItem(){
        webTestClient.delete().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"),"ABC")
                .accept(MediaType.APPLICATION_JSON)
                .exchange()
                .expectStatus().isOk()
                .expectBody(Void.class);
    }

    @Test
    public void updateItem(){
        double newPrice = 129.99;
        Item item = new Item().builder().description("Beats Headphones").price(newPrice).build();
        webTestClient.put().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"),"ABC")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item),Item.class)
                .exchange().expectStatus().isOk()
                .expectBody()
                .jsonPath("$.price",newPrice);
    }

    @Test
    public void updateItem_notFound(){
        double newPrice = 129.99;
        Item item = new Item().builder().description("Beats Headphones").price(newPrice).build();
        webTestClient.put().uri(ItemConstants.ITEM_END_POINT_V1.concat("/{id}"),"DEF")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .body(Mono.just(item),Item.class)
                .exchange().expectStatus().isNotFound();
    }
}