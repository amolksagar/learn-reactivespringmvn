package com.learnreactivespringmvn.controller;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

@RunWith(SpringRunner.class)
//got introduced in spring 5.
//It Will scan for all classes annotated with @RestController and @Controller
//It will not scan classes annotated with @Component,@Service,@Repository etc
@SpringBootTest
@AutoConfigureWebTestClient
@DirtiesContext
@ActiveProfiles("test")
public class FluxAndMonoControllerTest {

    //this is comparable to TestRestTemplate in SpringMVC world
    @Autowired
    WebTestClient webTestClient;

    @Test
    public void flux_approach1(){
        //webTestClient is the subscriber
        Flux<Integer> integerFlux = webTestClient.get().uri("/flux")
                .accept(MediaType.APPLICATION_JSON)
                //this will actually make a call to the endpoint
                .exchange()
                .expectStatus().isOk()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(integerFlux)
                .expectSubscription()
                .expectNext(1,2,3,4)
                .verifyComplete();
    }

    @Test
    public void flux_approach2(){
        webTestClient.get().uri("/fluxstream")
                .accept(MediaType.valueOf(MediaType.APPLICATION_NDJSON_VALUE))
                //this will actually make a call to the endpoint
                .exchange()
                .expectStatus().isOk()
                .expectHeader().contentType(MediaType.APPLICATION_NDJSON_VALUE)
                .expectBodyList(Integer.class)
                .hasSize(4);
    }

    @Test
    public void flux_approach3() {
        List<Integer> listOfExpectedIntegers = Arrays.asList(1,2,3,4);
        EntityExchangeResult<List<Integer>> entityExchangeResult = webTestClient.get().uri("/fluxstream")
                .accept(MediaType.valueOf(MediaType.APPLICATION_NDJSON_VALUE))
                //this will actually make a call to the endpoint
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .returnResult();
        assertEquals(listOfExpectedIntegers,entityExchangeResult.getResponseBody());
    }

    @Test
    public void flux_approach4() {
        List<Integer> listOfExpectedIntegers = Arrays.asList(1,2,3,4);
        webTestClient
                .get().uri("/fluxstream")
                .accept(MediaType.valueOf(MediaType.APPLICATION_NDJSON_VALUE))
                //this will actually make a call to the endpoint
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(Integer.class)
                .consumeWith((response) -> {
                    assertEquals(listOfExpectedIntegers,response.getResponseBody());
                });
    }

    @Test
    public void fluxStream(){
        Flux<Long> longStreamFlux = webTestClient.get().uri("/fluxstreaminfinite")
                .accept(MediaType.valueOf(MediaType.APPLICATION_NDJSON_VALUE))
                //this will actually make a call to the endpoint
                .exchange()
                .expectStatus().isOk()
                .returnResult(Long.class)
                .getResponseBody();
        StepVerifier.create(longStreamFlux)
                .expectNext(0l)
                .expectNext(1l)
                .expectNext(2l)
                .thenCancel()
                .verify();
    }

    @Test
    public void mono(){
        Integer expectedValue = 1;
        //webTestClient is the subscriber
        webTestClient.get().uri("/mono")
                .accept(MediaType.valueOf(MediaType.APPLICATION_NDJSON_VALUE))
                //this will actually make a call to the endpoint
                .exchange()
                .expectStatus().isOk()
                .expectBody(Integer.class)
                .consumeWith(response -> {
                    assertEquals(expectedValue,response.getResponseBody());
                });
    }
}
