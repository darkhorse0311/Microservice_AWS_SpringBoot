package br.com.lucio.service01;

import io.micrometer.core.instrument.util.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

@RestController
@RequestMapping("/person")
public class PersonController {

    private static final Logger LOG = LoggerFactory.getLogger(PersonController.class);

    private static Map<UUID, String> names;

    public PersonController() {
        names = new HashMap<>();
        names.put(UUID.fromString("83fe6746-9470-40e6-936f-8f4f32d85ae6"), "Rodrigo Lúcio");
        names.put(UUID.fromString("3f620bae-8b36-4448-ab9b-ff0fbb181802"), "Danielly Mattiollo");
        names.put(UUID.fromString("add8ed7d-5397-43a7-aef3-fa319f0cce4a"), "Ana Luiza Siqueira Lúcio");
        names.put(UUID.fromString("93840088-2ae2-47c3-bcea-1213c23ea780"), "Eloi Lúcio");
        names.put(UUID.fromString("46b67677-faed-4742-94f0-eb9ecbb21c3c"), "Nina Lúcio");
        names.put(UUID.fromString("0d98f6a1-0693-46ef-b50e-e3e082773459"), "Olívia Mattiollo Lúcio");
    }

    @GetMapping("/{id}")
    public ResponseEntity<Person> get(@PathVariable UUID id) {
        if(!names.containsKey(id)) {
            LOG.error("Pessoa não encontrada para o id {}", id);
            return ResponseEntity.notFound().build();
        }

        String name = names.get(id);
        return ResponseEntity.ok(new Person(id, name));
    }

    @PostMapping
    public ResponseEntity<Person> post(@RequestBody Person person) {
        UUID id = UUID.randomUUID();

        if(StringUtils.isBlank(person.getName())) {
            return ResponseEntity.badRequest().build();
        }

        names.putIfAbsent(id, person.getName());

        Person personSaved = new Person(id, person.getName().trim());
        LOG.info("Pessoa cadastrada com sucesso: {}", personSaved);
        return ResponseEntity.ok(personSaved);
    }

}
