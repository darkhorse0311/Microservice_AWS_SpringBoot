package br.com.lucio.service01.controller;

import br.com.lucio.service01.enums.EventType;
import br.com.lucio.service01.model.Product;
import br.com.lucio.service01.repository.ProductRepository;
import br.com.lucio.service01.service.ProductCrudEventPublisher;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("api/products")
public class ProductController {

    private ProductRepository productRepository;
    private ProductCrudEventPublisher productCrudEventPublisher;

    public ProductController(ProductRepository productRepository, ProductCrudEventPublisher productCrudEventPublisher) {
        this.productRepository = productRepository;
        this.productCrudEventPublisher = productCrudEventPublisher;
    }

    @GetMapping
    public List<Product> findAll() {
        return productRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> findById(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            return ResponseEntity.ok(product.get());
        }
        return ResponseEntity.notFound().build();
    }

    @PostMapping
    public ResponseEntity<Product> create(@RequestBody Product product, UriComponentsBuilder uriBuilder) {
        Product productCreated = productRepository.save(product);
        URI uri = uriBuilder.path("/api/products/{id}").buildAndExpand(productCreated.getId()).toUri();
        productCrudEventPublisher.publishEvent(productCreated, EventType.CREATED);
        return ResponseEntity.created(uri).body(productCreated);
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<Product> update(@RequestBody Product product, @PathVariable Long id) {
        if (productRepository.existsById(id)) {
            product.setId(id);
            Product productUpdated = productRepository.save(product);
            productCrudEventPublisher.publishEvent(productUpdated, EventType.UPDATED);
            return ResponseEntity.ok(productUpdated);
        }

        return ResponseEntity.notFound().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Product> delete(@PathVariable Long id) {
        Optional<Product> product = productRepository.findById(id);
        if(product.isPresent()) {
            productRepository.deleteById(id);
            productCrudEventPublisher.publishEvent(product.get(), EventType.UPDATED);
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.notFound().build();

    }

}
