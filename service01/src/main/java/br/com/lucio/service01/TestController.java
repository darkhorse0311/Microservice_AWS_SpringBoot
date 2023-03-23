package br.com.lucio.service01;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api")
public class TestController {


    @GetMapping("/{name}")
    public ResponseEntity<Test> test(@PathVariable String name) {
        return ResponseEntity.ok(new Test(UUID.randomUUID(), name));
    }

}
