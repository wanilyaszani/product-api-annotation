package com.wirebraincoffee.productapiannotation;

import com.wirebraincoffee.productapiannotation.model.Product;
import com.wirebraincoffee.productapiannotation.repository.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class ProductApiAnnotationApplication {

	public static void main(String[] args) {
		SpringApplication.run(ProductApiAnnotationApplication.class, args);
	}

	@Bean
	CommandLineRunner init(ProductRepository repository){
		return args -> {

			Flux<Product> productFlux = Flux.just(
					new Product(null,"Big Latte", 2.99),
					new Product(null,"Big Decaf", 2.49),
					new Product(null,"Green Tea", 1.99))
					.flatMap(p -> repository.save(p));
				// to simplify you can also use this .flatMap(repository::save);

			// in order to make sure, the following function can
			// only be run after everything save, below functions added

			productFlux
					.thenMany(repository.findAll())
					.subscribe(System.out::println);
		};
	}
}
