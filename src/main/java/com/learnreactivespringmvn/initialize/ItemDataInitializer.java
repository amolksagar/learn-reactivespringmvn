package com.learnreactivespringmvn.initialize;

import com.learnreactivespringmvn.document.Item;
import com.learnreactivespringmvn.repository.ItemReactiveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;

@Component
@Profile("!test")
public class ItemDataInitializer implements CommandLineRunner {

    @Autowired
    ItemReactiveRepository itemReactiveRepository;
    @Override
    public void run(String... args) throws Exception {
        initialDataSetup();
    }

    public List<Item> data(){
        return Arrays.asList(
            Item.builder().description("Samsung TV").price(399.99).build(),
            Item.builder().description("LG TV").price(329.99).build(),
            Item.builder().description("Apple Watch").price(349.99).build(),
            Item.builder().id("ABC").description("Beats HeadPhones").price(19.99).build()
        );
    }

    private void initialDataSetup() {
        itemReactiveRepository.deleteAll()
                .thenMany(Flux.fromIterable(data()))
                .flatMap(itemReactiveRepository::save)
                .thenMany(itemReactiveRepository.findAll())
                .subscribe(item -> {
                    System.out.println("Item inserted from CommandLineRunner : " + item);
                });
    }
}
