package com.github.putpixel.arvato.model;

import java.io.Serializable;
import java.util.UUID;

public abstract class ModelWithIdentifier implements Serializable {

    private static final long serialVersionUID = -2456217662168352738L;

    private String identifier;

    public ModelWithIdentifier() {
        this.identifier = UUID.randomUUID().toString();
    }

    public String getIdentifier() {
        return identifier;
    }

    public void setIdentifier(String identifier) {
        this.identifier = identifier;
    }

}
