package br.com.lucio.service01;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class TestController {

    private static final Logger LOG = LoggerFactory.getLogger(TestController.class);

    @GetMapping("/{name}")
    public ResponseEntity<Test> test(@PathVariable String name) {
        LOG.info("Teste passando em /api com o nome {}", name);
        return ResponseEntity.ok(new Test(UUID.randomUUID(), name));
    }

}
