package br.com.lucio.service01;

import java.util.UUID;

public class Test {

    private UUID id;
    private String name;
    public Test(UUID uuid, String name) {
        this.id = uuid;
        this.name = name;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


}
