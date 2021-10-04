package com.wirebraincoffee.productapiannotation.controller;

import com.wirebraincoffee.productapiannotation.model.Product;
import com.wirebraincoffee.productapiannotation.repository.ProductRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/products")
public class ProductController {

    private ProductRepository repository;

    public ProductController(ProductRepository repository){
        this.repository = repository;
    }

    @GetMapping
    public Flux<Product> getAllProducts(){
        return repository.findAll();
    }

    @GetMapping("{id}")
    public Mono<ResponseEntity<Product>> getProduct(@PathVariable String id){
        return repository.findById(id)
                .map(product -> ResponseEntity.ok(product)) // convert mono of type product to mono of type response entity
                .defaultIfEmpty(ResponseEntity.notFound().build()); // if no product found, return default response
        // if theres no return, it's more appropriate to return http??
        // so instead of return mono of product, return mono of response entity of type product

    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<Product> saveProduct(@RequestBody Product product){
        return repository.save(product);
    }

    @PutMapping
    public Mono<ResponseEntity<Product>> updateProduct(@PathVariable(value="id") String id, @RequestBody Product product){
        return repository.findById(id)
                .flatMap(existingProduct ->{
                    existingProduct.setName(product.getName());
                    existingProduct.setPrice(product.getPrice());
                    return repository.save(existingProduct);
                })
                .map(updateProduct -> ResponseEntity.ok(updateProduct))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public Mono<ResponseEntity<Void>> deleteProduct(@PathVariable(value="id") String id){
        return repository.findById(id)
                .flatMap(existingProduct ->
                        repository.delete(existingProduct)
                                .then(Mono.just(ResponseEntity.ok().<Void>build()))
                )
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping
    public Mono<Void> deleteAllProducts(){
        return repository.deleteAll();
    }
}
