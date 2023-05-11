package br.com.lucio.service02.controller;

import br.com.lucio.service02.model.ProductEventDTO;
import br.com.lucio.service02.repository.ProductEventRepository;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductEventController {

    private final ProductEventRepository productEventRepository;

    public ProductEventController(ProductEventRepository productEventRepository) {
        this.productEventRepository = productEventRepository;
    }

    @GetMapping("/events/{code}")
    private List<ProductEventDTO> getByCode(@PathVariable String code) {
        return productEventRepository.findAllByPk(code)
                .stream()
                .map(ProductEventDTO::new)
                .toList();
    }

    @GetMapping("/events/{code}/{eventType}")
    private List<ProductEventDTO> getByCodeAndEventType(@PathVariable String code, @PathVariable String eventType) {
        return productEventRepository.findAllByPkAndSkStartsWith(code, eventType)
                .stream()
                .map(ProductEventDTO::new)
                .toList();
    }

}
